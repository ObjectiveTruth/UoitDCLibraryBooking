#!/bin/bash
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
    echo "Device Farm already ran tests, skipping CircleCI container emulator testing";
    exit $DEVICE_FARM_EXIT_CODE
else
    echo "Device Farm couldn't be contacted. Running tests on CircleCI container using emulation";

    # Run unit tests first (results will be collected at the last step)
    ./gradlew test -PdisablePreDex

    # Create a mounted sd card to save android screenshots to (the custom CI emulator's sd card is read only)
    mksdcard -l e 512M mysdcard.img

    # Start the emulator
    emulator -avd circleci-android22 -no-audio -no-window -sdcard mysdcard.img &

    # Wait for it to have booted
    circle-android wait-for-boot

    # Wait a moment before Unlocking the emulator screen
    sleep 15
    adb shell input keyevent 82

    # Run all android tests using spoon against the emulator.
    ./gradlew spoon -PdisablePreDex:

fi
