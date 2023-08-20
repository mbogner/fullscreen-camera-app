package dev.mbo.androidcamera.ui.viewmodels

import android.content.Context
import android.media.MediaCodec
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mbo.androidcamera.utils.CameraSizeUtil
import dev.mbo.androidcamera.utils.IpHelper
import dev.mbo.androidcamera.utils.ToastHelper
import dev.mbo.androidcamera.utils.VideoEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.Executors

class HomeViewModel : ViewModel() {
    private var encoder: VideoEncoder? = null
    private var cameraSize: Size? = null
    private var serverSocket: ServerSocket? = null

    fun initializeCamera(
        streaming: Boolean,
        previewView: PreviewView,
        context: Context
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraSize = CameraSizeUtil.getMaxSize(context)!!

                val previews = mutableListOf<Preview>()

                previews.add(Preview.Builder()
                    .setTargetResolution(cameraSize!!)
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    })


                if (streaming) {
                    previews.add(Preview.Builder()
                        .setTargetResolution(
                            Size(
                                VideoEncoder.DEFAULT_WIDTH,
                                VideoEncoder.DEFAULT_HEIGHT
                            )
                        )
                        .build().also {
                            encoder = VideoEncoder()
                            encoder!!.prepareEncoder()
                            it.setSurfaceProvider { request ->
                                request.provideSurface(
                                    encoder!!.getSurface(),
                                    ContextCompat.getMainExecutor(context)
                                ) {}
                            }
                        }
                    )
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector,
                        *previews.toTypedArray()
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun toggleStream(scope: CoroutineScope, context: Context, streaming: MutableState<Boolean>) {
        val isStreaming = streaming.value
        streaming.value = !streaming.value
        if (isStreaming) {
            stopStreaming(context)
        } else {
            startStreaming(scope, context, streaming)
        }
    }

    private fun startStreaming(
        scope: CoroutineScope,
        context: Context,
        streaming: MutableState<Boolean>
    ) {
        viewModelScope.launch {
            scope.launch(Dispatchers.IO) {
                serverSocket = ServerSocket(9000, 0, IpHelper.getWifiIpAddress(context))
                Log.d(TAG, "socket opened on port ${serverSocket!!.localPort}")
                while (streaming.value) {
                    try {
                        serverSocket?.accept().use { clientSocket ->
                            if (clientSocket != null) {
                                Log.d(TAG, "client connected")
                                ToastHelper.showToast(context, "client connected")
                                handleClient(clientSocket, streaming)
                                clientSocket.close()
                            }
                        }
                    } catch (exc: SocketException) {
                        Log.d(TAG, "socket exception", exc)
                    }
                }
            }
        }
    }

    private fun handleClient(clientSocket: Socket, streaming: MutableState<Boolean>) {
        val mediaCodec = encoder!!.getMediaCodec()
        val bufferInfo = MediaCodec.BufferInfo()
        while (streaming.value) {
            try {
                val outputBufferIndex =
                    mediaCodec.dequeueOutputBuffer(
                        bufferInfo,
                        VideoEncoder.TIMEOUT_US
                    ) // -1 for none

                if (outputBufferIndex > -1) {
                    val encodedData = mediaCodec.getOutputBuffer(outputBufferIndex)
                    val length = bufferInfo.size

                    // Send the encoded data over the socket
                    if (length > 0) {
                        val dataToSend = ByteArray(length)
                        encodedData!!.get(dataToSend)
                        clientSocket.getOutputStream().write(dataToSend)
                    }

                    // Release output buffer
                    mediaCodec.releaseOutputBuffer(outputBufferIndex, false)

                }
            } catch (thr: Throwable) {
                // ignore
            }
        }
    }

    private fun stopStreaming(context: Context) {
        try {
            encoder?.close()
            serverSocket?.close()
            unbindSurfaces(context)
        } catch (exc: Exception) {
            Log.e(TAG, "close failed", exc)
        }
    }

    private fun unbindSurfaces(context: Context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }

}