const EXIT_CODE_ENV_VARIABLE_NAME = 'DEVICE_FARM_RECEIVE_SERVER_RESPONSE';
process.env[EXIT_CODE_ENV_VARIABLE_NAME] = '1'; // Set failure incase we quit early

const LISTEN_PORT = 9292;
const ARTIFACTS_SAVE_DIR = '../UoitDCLibraryBooking/UoitDCLibraryBooking/build';
const DEVICE_FARM_UPLOAD_APKS_FOR_TESTING_ENDPOINT = 
    'http://api.uoitdclibrarybooking.objectivetruth.ca/circleci_build_webhook/upload_to_devicefarm';
const NGROK_TUNNEL_URL_ENV_VARIABLE_NAME = 
    'NGROK_TUNNEL_URL';
const ANDROID_TEST_INSTRUMENTATION_APK_LOCATION = 
    '../UoitDCLibraryBooking/build/outputs/apk/UoitDCLibraryBooking-debug-androidTest-unaligned.apk';
const ANDROID_DEBUG_APK_LOCATION = 
    '../UoitDCLibraryBooking/build/outputs/apk/UoitDCLibraryBooking-debug-unaligned.apk';

var express = require('express');
var fs = require('fs');
var app = express();
var bodyParser = require('body-parser');
var multer = require('multer');
var FormData = require('form-data');
var storage = multer.diskStorage({
    destination: function (req, file, callback) {
        callback(null, ARTIFACTS_SAVE_DIR);
    },
    filename: function (req, file, cb) {
        cb(null, file.fieldname + '.zip');
    }
});
var upload = multer({storage: storage});




//==============MAIN=================

sendAPKsToDeviceFarmServer();


app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.post('/reply', upload.fields([
    { name: 'artifacts', maxCount: 1 }
]), function(req, res) {
        const DEVICE_FARM_RESULT_CODE_FIELDNAME = 'result_code';
        console.log(`Received response from device farm. Result Code: ${res.body[DEVICE_FARM_RESULT_CODE_FIELDNAME]}`);
        process.exit(res.body[DEVICE_FARM_RESULT_CODE_FIELDNAME]);
    });

if (isADeviceFarmServerAvailable(process.env[EXIT_CODE_ENV_VARIABLE_NAME])) {
    app.listen(LISTEN_PORT, function () {
        console.log(`Device Farm Receive Server listening on port ${LISTEN_PORT}!`);
    });
}else {
    process.exit(0);
}





//============Utility Functions================

function sendAPKsToDeviceFarmServer() {
    console.log(`Sending the debug and instrumentation apks to the device farm. ${process.env[NGROK_TUNNEL_URL_ENV_VARIABLE_NAME]}`);

    var requestForCircleCIServer = new FormData();
    requestForCircleCIServer.append('instrumentation', fs.createReadStream(ANDROID_TEST_INSTRUMENTATION_APK_LOCATION));
    requestForCircleCIServer.append('debug', fs.createReadStream(ANDROID_DEBUG_APK_LOCATION));
    //requestForCircleCIServer.append('callback', process.env[NGROK_TUNNEL_URL_ENV_VARIABLE_NAME]);
    requestForCircleCIServer.submit(DEVICE_FARM_UPLOAD_APKS_FOR_TESTING_ENDPOINT, function(error, response){
        if(error || (response.statusCode < 200 || response.statusCode > 299)) {
            console.log(`Error Code: ${response.statusCode} when sending results to Device Farm Server`);
            console.log(error || response.statusMessage);
            // 69 is Error Code: Service Unavailable
            process.env[EXIT_CODE_ENV_VARIABLE_NAME] = '69';
        }else {
            process.env[EXIT_CODE_ENV_VARIABLE_NAME] = '0';
            console.log(`Successfully transferred results to Device Farm Server, will wait for results...`);
        }
    });
    
}

function isADeviceFarmServerAvailable(code) {
    return code !== '69';
}
