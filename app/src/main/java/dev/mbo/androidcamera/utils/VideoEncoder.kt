package dev.mbo.androidcamera.utils

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import java.io.IOException

@Suppress("MemberVisibilityCanBePrivate")
class VideoEncoder(
    val width: Int = DEFAULT_WIDTH,
    val height: Int = DEFAULT_HEIGHT,
    val bitRate: Int = DEFAULT_BITRATE,
    val frameRate: Int = DEFAULT_FRAME_RATE,
    val iFrameInterval: Int = DEFAULT_I_FRAME_INTERVAL,
    val codecName: String = chooseHardwareCodecName()
) : AutoCloseable {

    private var mediaCodec: MediaCodec? = null
    private var inputSurface: Surface? = null
    private var started = false

    @Synchronized
    fun prepareEncoder() {
        if (!started) {
            Log.i(TAG, "using codec: $codecName")
            val format =
                MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
            format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)
            format.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )

            try {
                mediaCodec = MediaCodec.createByCodecName(codecName)
                mediaCodec!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                inputSurface = mediaCodec!!.createInputSurface()
                mediaCodec!!.start()
                started = true
                Log.d(TAG, "started encoder: width=$width, height=$height")
            } catch (exc: IOException) {
                Log.e(TAG, "creating encoder failed", exc)
            }
        }
    }

    fun getSurface(): Surface {
        assertStarted()
        return inputSurface!!
    }

    override fun close() {
        mediaCodec?.stop()
        mediaCodec?.release()
    }

    fun getMediaCodec(): MediaCodec {
        assertStarted()
        return mediaCodec!!
    }

    private fun assertStarted() {
        if (!started) throw IllegalStateException("not started - use prepareEncoder method")
    }

    companion object {
        private const val TAG = "VideoEncoder"
        const val DEFAULT_WIDTH = 1920
        const val DEFAULT_HEIGHT = 1080
        const val DEFAULT_BITRATE = 50_000_000
        const val DEFAULT_FRAME_RATE = 30
        const val DEFAULT_I_FRAME_INTERVAL = 1
        const val TIMEOUT_US = 10000L

        fun chooseHardwareCodecName(codecName: String = "avc"): String {
            val encoderInfoList = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos
                .filter { it.isEncoder && it.isHardwareAccelerated }
            encoderInfoList.forEach { Log.d(TAG, "supported codec: ${it.name}") }
            val name = codecName.lowercase()
            return encoderInfoList.firstOrNull { it.name.endsWith(name) }?.name
                ?: throw IllegalStateException("$name not supported")
        }
    }

}