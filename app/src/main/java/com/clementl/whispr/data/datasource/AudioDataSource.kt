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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

interface AudioDataSource {
    fun startListening()
    fun stopListening()
    fun release()
    fun getRecordingStateFlow(): Flow<RecordingState>
}

@Singleton
class AudioRecorderDataSource @Inject constructor() : AudioDataSource {

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var recordingJob: Job? = null
    private var audioRecord: AudioRecord? = null
    private var vad: VadWebRTC? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun startListening() {
        if (recordingJob?.isActive == true) {
            Timber.tag(TAG).w("Recording is already in progress.")
            return
        }

        try {
            if (audioRecord == null) {
                val bufferSize =
                    AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
                )
            }

            if (vad == null) {
                vad = VadWebRTC(
                    sampleRate = SampleRate.SAMPLE_RATE_16K,
                    frameSize = FrameSize.FRAME_SIZE_320,
                    mode = Mode.VERY_AGGRESSIVE,
                    silenceDurationMs = SILENCE_DURATION_MS,
                    speechDurationMs = SPEECH_DURATION_MS,
                )
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to initialize AudioRecord or VAD")
            _recordingState.value = RecordingState.Error(e)
            return
        }

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Timber.tag(TAG).e("AudioRecord not initialized")
            _recordingState.value = RecordingState.Error(Exception("AudioRecord not initialized"))
            return
        }

        audioRecord?.startRecording()
        _recordingState.value = RecordingState.Recording

        recordingJob = scope.launch(Dispatchers.IO) {
            processAudioStream(this)
        }
    }

    private fun processAudioStream(scope: CoroutineScope) {
        val currentAudioRecord = audioRecord ?: return
        val currentVad = vad ?: return

        val frameBuffer = ShortArray(FrameSize.FRAME_SIZE_320.value)

        while (scope.isActive) {
            try {
                val readSize = currentAudioRecord.read(frameBuffer, 0, frameBuffer.size)

                if (readSize > 0) {
                    val isSpeech = currentVad.isSpeech(frameBuffer)
                    _recordingState.value =
                        if (isSpeech) RecordingState.Speech else RecordingState.Silence
                    // TODO: Process frameBuffer for transcription when in Speech
                } else {
                    Timber.tag(TAG).w("AudioRecord read failed: $readSize")
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error during audio processing")
                _recordingState.value = RecordingState.Error(e)
                break
            }
        }
    }


    override fun stopListening() {
        if (recordingJob?.isActive != true) {
            return
        }
        recordingJob?.cancel()
        recordingJob = null


        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
        } catch (e: IllegalStateException) {
            Timber.tag(TAG).e(e, "AudioRecord failed to stop")
        }
        _recordingState.value = RecordingState.Idle
        Timber.tag(TAG).d("Recording stopped.")
    }

    override fun release() {
        stopListening()
        audioRecord?.release()
        audioRecord = null
        vad?.close()
        vad = null
        scope.cancel()
    }

    override fun getRecordingStateFlow(): Flow<RecordingState> = _recordingState.asStateFlow()

    companion object {
        private const val TAG = "Test"
        private const val SAMPLE_RATE = 16_000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val SILENCE_DURATION_MS = 300
        private const val SPEECH_DURATION_MS = 50
    }
}
