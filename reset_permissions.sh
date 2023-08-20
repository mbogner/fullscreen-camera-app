#!/usr/bin/env bash
adb -s "$(adb devices | grep emulator | grep device | awk '{print $1}')" shell pm reset-permissions