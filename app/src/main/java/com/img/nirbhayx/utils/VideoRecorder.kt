package com.img.nirbhayx.utils

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class VideoRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var outputFile: String? = null
    private var isRecording = false
    private var recordingStartedContinuation: Continuation<String?>? = null

    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    suspend fun startRecording(): String? = suspendCancellableCoroutine { continuation ->
        if (isRecording) {
            Log.w("VideoRecorder", "Recording already in progress")
            continuation.resume(outputFile)
            return@suspendCancellableCoroutine
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("VideoRecorder", "Camera permission not granted")
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "SOS_VIDEO_$timestamp.mp4"
        val appDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "NirbhayX")
        if (!appDir.exists()) appDir.mkdirs()
        val file = File(appDir, fileName)
        outputFile = file.absolutePath
        Log.d("VideoRecorder", "Video file path: $outputFile")

        try {
            startBackgroundThread()
            setupMediaRecorder()
            recordingStartedContinuation = continuation

            continuation.invokeOnCancellation {
                Log.w("VideoRecorder", "Coroutine cancelled before camera setup")
                cleanupResources()
            }

            openCamera()
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Video recording setup failed: ${e.message}", e)
            cleanupResources()
            continuation.resume(null)
        }
    }

    private fun createCaptureSession() {
        try {
            val surface = mediaRecorder?.surface ?: return
            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        startRecordingCapture(surface)
                        try {
                            recordingStartedContinuation?.resume(outputFile)
                        } catch (e: IllegalStateException) {
                            Log.w("VideoRecorder", "Continuation already completed or cancelled", e)
                        }

                        recordingStartedContinuation = null
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("VideoRecorder", "Capture session configuration failed")
                        recordingStartedContinuation?.resume(null)
                        recordingStartedContinuation = null
                    }
                },
                backgroundHandler
            )
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Failed to create capture session", e)
            recordingStartedContinuation?.resume(null)
            recordingStartedContinuation = null
        }
    }

    private fun setupMediaRecorder() {
        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(outputFile)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoSize(640, 480)
                setVideoFrameRate(15)
                setVideoEncodingBitRate(1000000)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(64000)
                prepare()
            }
        } catch (e: Exception) {
            Log.e("VideoRecorder", "MediaRecorder setup failed", e)
            throw e
        }
    }

    private fun openCamera() {
        try {
            val cameraId = getBackCameraId()
            if (cameraId == null) {
                Log.e("VideoRecorder", "No back camera found")
                recordingStartedContinuation?.resume(null)
                return
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    createCaptureSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.w("VideoRecorder", "Camera disconnected")
                    camera.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e("VideoRecorder", "Camera error: $error")
                    camera.close()
                    cameraDevice = null
                    recordingStartedContinuation?.resume(null)
                }
            }, backgroundHandler)

        } catch (e: SecurityException) {
            Log.e("VideoRecorder", "Camera permission denied", e)
            throw e
        }
    }

    private fun getBackCameraId(): String? {
        return try {
            cameraManager.cameraIdList.find { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Error finding back camera", e)
            null
        }
    }

    private fun startRecordingCapture(surface: Surface) {
        try {
            val requestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            requestBuilder?.addTarget(surface)
            requestBuilder?.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
            val request = requestBuilder?.build() ?: return
            captureSession?.setRepeatingRequest(request, null, backgroundHandler)
            mediaRecorder?.start()
            isRecording = true
            Log.d("VideoRecorder", "Recording started")
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Failed to start recording", e)
        }
    }

    fun stopRecording() {
        if (!isRecording) {
            Log.w("VideoRecorder", "stopRecording called but recording wasn't active.")
            return
        }

        isRecording = false

        try {
            mediaRecorder?.apply {
                try {
                    stop()
                    Log.d("VideoRecorder", "Recording stopped")
                } catch (e: RuntimeException) {
                    Log.e("VideoRecorder", "Recording failed. Deleting file.", e)
                    outputFile?.let { File(it).delete() }
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.e("VideoRecorder", "stopRecording error", e)
        } finally {
            mediaRecorder = null
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            stopBackgroundThread()
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("VideoRecorderThread").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e("VideoRecorder", "stopBackgroundThread error", e)
        }
    }

    private fun cleanupResources() {
        isRecording = false
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Cleanup error (MediaRecorder)", e)
        }

        mediaRecorder = null

        try {
            captureSession?.close()
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Cleanup error (CaptureSession)", e)
        }

        captureSession = null

        try {
            cameraDevice?.close()
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Cleanup error (CameraDevice)", e)
        }

        cameraDevice = null
        stopBackgroundThread()
    }

    fun isCurrentlyRecording(): Boolean = isRecording
}
