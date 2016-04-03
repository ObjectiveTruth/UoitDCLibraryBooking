#!/bin/bash
set -o xtrace  # Debug mode
set -o errexit # Exit if any commands gives non-zero return code
set -o nounset # Exit if referencing any variable that's not been set
##############################################################################
##
##  Custom script executed by CircleCI to open emulator in container and run test
##
##  Meant to be run from circle.yml and from the CircleCI container
##
##############################################################################

# If the response from the previous step (which should be the device_farm_receive_server) isn't 69,
# then the code was either 1 or 0 which means the tests occured or something went terribly bad earlier in the commands
# In that case, we just return the code that was returned to us and CircleCI will interpret that as success or fail
# accordingly
DEVICE_FARM_EXIT_CODE=`cat device_farm_receive_server_exit_code.txt`

if [[ $DEVICE_FARM_EXIT_CODE -ne 69 ]] ; then
    echo "Device Farm already ran tests so, no need to do it again"
    echo "Unzipping device farm tests results";
    set +e
    cd UoitDCLibraryBooking/build
    tar -xvf artifacts.tgz
    set -e
    exit $DEVICE_FARM_EXIT_CODE
else
    echo "Device Farm couldn't be contacted. Running tests on CircleCI container using emulation";

    # Create a mounted sd card to save android screenshots to (the custom CI emulator's sd card is read only)
    mksdcard -l e 512M mysdcard.img

    # Start the emulator
    emulator -avd circleci-android22 -no-audio -no-window -sdcard mysdcard.img &

    # Wait for it to have booted
    circle-android wait-for-boot

    # Wait a moment before Unlocking the emulator screen
    sleep 15
    adb shell input keyevent 82

    # Download the spoon jar and run all androidTests
    curl -o spoon-runner-with-dependencies.jar -L \
        --remote-name "https://search.maven.org/remote_content?g=com.squareup.spoon&a=spoon-runner&v=1.3.2&c=jar-with-dependencies"
    java -jar spoon-runner-with-dependencies.jar \
        --apk UoitDCLibraryBooking/build/outputs/apk/UoitDCLibraryBooking-debug-unaligned.apk \
        --test-apk UoitDCLibraryBooking/build/outputs/apk/UoitDCLibraryBooking-debug-androidTest-unaligned.apk \
        --output UoitDCLibraryBooking/build/outputs/spoon/

fi
