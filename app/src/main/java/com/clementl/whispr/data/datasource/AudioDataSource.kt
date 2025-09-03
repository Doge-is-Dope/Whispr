package com.clementl.whispr.data.datasource

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.clementl.whispr.domain.model.RecordingState
import com.konovalov.vad.webrtc.VadWebRTC
import com.konovalov.vad.webrtc.config.FrameSize
import com.konovalov.vad.webrtc.config.Mode
import com.konovalov.vad.webrtc.config.SampleRate
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

interface AudioDataSource {
    suspend fun startListening()
    suspend fun stopListening()
    fun release()
    fun getRecordingStateFlow(): Flow<RecordingState>
}

@Singleton
class AudioRecorderDataSource @Inject constructor() : AudioDataSource {

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var vad: VadWebRTC? = null

    // Common settings for VAD/STT
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun startListening() {
        if (recordingJob?.isActive == true) {
            Timber.tag("test").w("Recording is already in progress.")
            return
        }

        if (audioRecord == null) {
            // Create an AudioRecord instance
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
        }

        if (vad == null) {
            vad = VadWebRTC(
                sampleRate = SampleRate.SAMPLE_RATE_16K,
                frameSize = FrameSize.FRAME_SIZE_320,
                mode = Mode.VERY_AGGRESSIVE,
                silenceDurationMs = 300,
                speechDurationMs = 50
            )
        }

        audioRecord?.startRecording()
        _recordingState.value = RecordingState.Recording

        // Start a coroutine on the IO thread to continuously read audio
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val frameSize = 320 // 20ms at 16kHz, matches FRAME_SIZE_320
            val frameBuffer = ShortArray(frameSize)
            while (isActive) {
                var filled = 0
                while (isActive && filled < frameSize) {
                    val readNow = audioRecord?.read(frameBuffer, filled, frameSize - filled) ?: -1
                    if (readNow <= 0) continue
                    filled += readNow
                }
                if (!isActive) break

                val isSpeech = vad?.isSpeech(frameBuffer) ?: false
                val newState = if (isSpeech) RecordingState.Speech else RecordingState.Silence
                if (_recordingState.value != newState) {
                    _recordingState.value = newState
                    Timber.tag("VAD").d("state -> %s", newState)
                }
                // TODO: Process frameBuffer for transcription when in Speech
            }
        }
    }

    override suspend fun stopListening() {
        recordingJob?.cancel()
        recordingJob = null

        audioRecord?.stop()
        _recordingState.value = RecordingState.Idle
        Timber.tag("test").d("Recording stopped.")
    }

    override fun release() {
        audioRecord?.release()
        audioRecord = null
        vad?.close()
        vad = null
    }

    override fun getRecordingStateFlow(): Flow<RecordingState> = _recordingState.asStateFlow()
}