package com.mdiluca.ptdma.Tools;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechInstance {
    private static TextToSpeech tts;

    public static TextToSpeech createInstance(Context context) {
        if(tts == null) {
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(Locale.US);

                        if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "This language is not supported");
                        }
                    } else {
                        Log.e("TTS", "Initialization Failed");
                    }
                }
            });
        }
        return tts;
    }

    public static void setOnUtteranceProgressListener(UtteranceProgressListener utteranceProgressListener) {
        if(tts != null)
            tts.setOnUtteranceProgressListener(utteranceProgressListener);
    }

    public static void stop() {
        tts.stop();
    }


    public static void speak(String s) {
        if(tts != null)
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, "text");
    }

    public static void shutdown() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
