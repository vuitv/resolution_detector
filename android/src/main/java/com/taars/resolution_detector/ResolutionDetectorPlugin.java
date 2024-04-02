package com.taars.resolution_detector;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** ResolutionDetectorPlugin */
public class ResolutionDetectorPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "resolution_detector");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getMaxResolution")) {
      MediaCodecInfo codecInfo = findVideoCodecInfo();

      String type = getMineType(codecInfo);
      if (type != null){
        MediaCodecInfo.CodecCapabilities codecCapabilities = codecInfo.getCapabilitiesForType(type);
        MediaCodecInfo.VideoCapabilities videoCapabilities = codecCapabilities.getVideoCapabilities();

        int maxWidth = videoCapabilities.getSupportedWidths().getUpper();
        int maxHeight = videoCapabilities.getSupportedHeights().getUpper();
        result.success(maxWidth + "x" + maxHeight);
      } else {
        result.success("0x0");
      }
    } else {
      result.notImplemented();
    }
  }

    private MediaCodecInfo findVideoCodecInfo() {
      int numCodecs = MediaCodecList.getCodecCount();
      for (int i = 0; i < numCodecs; i++) {
        MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
        if (!codecInfo.isEncoder()) {
          continue;
        }
        String[] types = codecInfo.getSupportedTypes();
        for (String type : types) {
          if (type.startsWith("video/")) {
            return codecInfo;
          }
        }
      }
      return null;
    }

  private String getMineType(MediaCodecInfo codecInfo) {
    String[] types = codecInfo.getSupportedTypes();
    for (String type : types) {
      if (type.startsWith("video/")) {
        return type;
      }
    }
    return null;
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
