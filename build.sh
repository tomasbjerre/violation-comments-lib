#!/bin/bash
./gradlew --refresh-dependencies clean gitChangelog eclipse build install -i
