#!/bin/bash
# Sample quick build for bullhead
device=`adb devices | awk '{if($2 == "device") print $1;}' | head -n 1 | tr -d '\n'`
model=`adb -s $device shell getprop ro.product.model | tr -d '\n'`
date=`date +%Y%m%d | tr -d '\n'`
build="bullhead"
expected="Nexus 5X"
filename="lineage-14.1-"$date"-UNOFFICIAL-"$build".zip"
if [[ $model == $expected ]]; then
        #Measure execution time
        start_time=`date +%s`
        make clobber
        source build/envsetup.sh
        breakfast $build
        cd ~/android/$build-lineage-14.1-experimental-prajit/device/lge/$build
        ./extract-files.sh
	cp "/home/prajit/android/proprietary_vendor_stuff/proprietary_vendor_lge/bullhead/proprietary/app/Tycho/Tycho.apk" "/home/prajit/android/bullhead-lineage-14.1-experimental-prajit/vendor/lge/bullhead/proprietary/app/Tycho/"
	cp "/home/prajit/android/proprietary_vendor_stuff/proprietary_vendor_lge/bullhead/proprietary/priv-app/GCS/GCS.apk" "/home/prajit/android/bullhead-lineage-14.1-experimental-prajit/vendor/lge/bullhead/proprietary/priv-app/GCS/"
        export USE_CCACHE=1
        croot
        prebuilts/misc/linux-x86/ccache/ccache -M 50G
        export ANDROID_JACK_VM_ARGS="-Dfile.encoding=UTF-8 -XX:+TieredCompilation -Xmx4G"
        brunch $build
        cd $OUT
	if [[ -f "$filename" ]]; then
		adb push $filename /sdcard/
		adb reboot recovery
	fi
        end_time=`date +%s`
        echo execution time was `expr $end_time - $start_time` s.
else
        echo "You have your $model device connected to the computer. This is a build process for the $expected. Please connect your $expected device to the computer and restart the build."
fi
