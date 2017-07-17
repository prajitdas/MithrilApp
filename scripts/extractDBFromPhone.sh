#!/bin/bash
adb shell "su -c 'cp /data/data/edu.umbc.ebiquity.mithril/databases/mithril.db /sdcard/mithril.db'"
adb pull /sdcard/mithril.db $HOME/code/android/projects/MithrilApp/