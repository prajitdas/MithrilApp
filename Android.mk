
# Auto-generated module by script
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := \
   android-support-v7-appcompat \
   android-support-design \
   android-support-v4 \
   android-support-v7-recyclerview \
   android-support-v7-cardview \
   android-support-vector-drawable \
   android-support-constraint \
   android-support-v7-appcompat \
   android-support-v7-appcompat \

    compile 'com.android.support:appcompat-v7:25.0.1'
    compile 'com.android.support:design:25.0.1'
    compile 'com.android.support:support-v4:25.0.1'
    compile 'com.android.support:recyclerview-v7:25.0.1'
    compile 'com.android.support:cardview-v7:25.0.1'
    compile 'com.android.support:support-vector-drawable:25.0.1'
    compile 'com.android.support.constraint:constraint-layout:1.0.0-alpha9'
    compile 'com.google.android.gms:play-services-location:10.0.1'
    compile 'com.google.android.gms:play-services-maps:10.0.1'
    compile 'com.google.android.gms:play-services-places:10.0.1'
    compile 'com.android.volley:volley:1.0.0'

LOCAL_MODULE := MithrilApp
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_SRC_FILES := $(call all-subdir-java-files src)
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

include $(BUILD_PREBUILT)
