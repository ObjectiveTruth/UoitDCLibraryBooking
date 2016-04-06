#!/usr/bin/env bash
##############################################################################
##
##  Custom script executed by Travis-CI before deploying an apk.
##
##  Sets ANDROID_APK_RELEASE_TRACK based on the current Travis-CI branch
##  master = beta
##  prod = production
##
##############################################################################


if [ "$TRAVIS_BRANCH" == "master" ]; then
    export ANDROID_APK_RELEASE_TRACK=beta
elif [ "$TRAVIS_BRANCH" == "prod" ]; then
    export ANDROID_APK_RELEASE_TRACK=prod
else
    echo "TRAVIS_BRANCH env variable not set"
    exit 1
fi

echo "If build succeeds, apk will be pushed to ${ANDROID_APK_RELEASE_TRACK} track"
