const LISTEN_PORT = 9292;
const ARTIFACTS_SAVE_DIR = '../UoitDCLibraryBooking/UoitDCLibraryBooking/build';

var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var multer = require('multer');
var storage = multer.diskStorage({
    destination: function (req, file, callback) {
        callback(null, ARTIFACTS_SAVE_DIR);
    },
    filename: function (req, file, cb) {
        cb(null, file.fieldname + '.zip');
    }
});
var upload = multer({storage: storage});


app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.post('/reply', upload.fields([
    { name: 'artifacts', maxCount: 1 }
]), function(req, res) {
        const DEVICE_FARM_RESULT_CODE_FIELDNAME = 'result_code';
        console.log(`Received response from device farm. Result Code: ${res.body[DEVICE_FARM_RESULT_CODE_FIELDNAME]}`);
    process.exit(res.body[DEVICE_FARM_RESULT_CODE_FIELDNAME]);
    });

app.listen(LISTEN_PORT, function () {
    console.log('Device Farm Receive Server listening on port ' + LISTEN_PORT + '!');
});
