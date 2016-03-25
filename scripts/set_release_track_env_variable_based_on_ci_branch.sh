#!/usr/bin/env bash
##############################################################################
##
##  Custom script executed by Travis-CI before deploying an apk.
##
##  Sets ANDROID_APK_RELEASE_TRACK based on the current Travis-CI branch
##  master = production
##  beta = beta
##
##############################################################################


if [ "$TRAVIS_BRANCH" == "master" ]; then
    export ANDROID_APK_RELEASE_TRACK=prod
elif [ "$TRAVIS_BRANCH" == "beta" ]; then
    export ANDROID_APK_RELEASE_TRACK=beta
else
    echo "TRAVIS_BRANCH env variable not set"
    exit 1
fi

echo "If build succeeds, apk will be pushed to ${ANDROID_APK_RELEASE_TRACK} track"
