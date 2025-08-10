package com.img.nirbhayx.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var outputFile: String? = null
    private var isRecording = false

    fun startRecording(): String? {
        if (isRecording) {
            Log.w("AudioRecorder", "Recording already in progress")
            return outputFile
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SOS_AUDIO_$timestamp.mp4"

        val appDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "NirbhayX")

        if (!appDir.exists()) {
            val created = appDir.mkdirs()
            Log.d("AudioRecorder", "Directory creation result: $created, path: $appDir")
        }

        val file = File(appDir, fileName)
        outputFile = file.absolutePath
        Log.d("AudioRecorder", "Audio file path: $outputFile")

        try {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setAudioSamplingRate(8000)
                setAudioEncodingBitRate(12200)
                setOutputFile(outputFile)

                try {
                    prepare()
                    start()
                    isRecording = true

                    Log.d("AudioRecorder", "Recording started successfully at $outputFile")

                    Handler(Looper.getMainLooper()).post {
                        android.widget.Toast.makeText(
                            context,
                            "Audio recording started: $fileName",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Log.e("AudioRecorder", "Failed to start recording", e)
                    release()
                    recorder = null
                    isRecording = false

                    Handler(Looper.getMainLooper()).post {
                        android.widget.Toast.makeText(
                            context,
                            "Audio recording failed to start",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                    return null
                }
            }

        } catch (e: Exception) {
            Log.e("AudioRecorder", "❌ Recording setup failed: ${e.localizedMessage}", e)
            Handler(Looper.getMainLooper()).post {
                android.widget.Toast.makeText(
                    context,
                    "Audio recording setup failed",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            return null
        }

        return outputFile
    }

    fun stopRecording() {
        if (!isRecording || recorder == null) {
            Log.w("AudioRecorder", "No active recording to stop")
            return
        }

        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false

            Log.d("AudioRecorder", "✅ Recording stopped. File saved at: $outputFile")

            Handler(Looper.getMainLooper()).post {
                android.widget.Toast.makeText(
                    context,
                    "Audio recording saved",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Log.e("AudioRecorder", "❌ Stop recording failed: ${e.localizedMessage}", e)
            recorder?.release()
            recorder = null
            isRecording = false
        }
    }

    fun isCurrentlyRecording(): Boolean = isRecording

    fun getOutputFilePath(): String? = outputFile
}