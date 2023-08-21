# Fullscreen Camera

Very simple Android app that start with max resolution of the main camera in fullscreen mode.

This can then be used to stream with [srccpy](https://github.com/Genymobile/scrcpy).

See the [stream.sh](stream.sh) file for a sample to start srccpy. The cool thing is that you can not
just stream the image with very small delay, you can also remotely control your phone.

In my `stream.sh` file I use `-s <deviceId>` because I have multiple devices connected. You get the
id by running

```shell
adb devices
```

If you only have one device connected you can skip that `-s` parameter completely.

Make sure to have debugging enabled on your phone. For a tutorial how to do so, please lookup a
tutorial in the internet for your specific phone.

## Play Store

https://play.google.com/store/apps/details?id=dev.mbo.androidcamera