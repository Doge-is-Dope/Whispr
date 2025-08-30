package com.clementl.whispr.data.datasource

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.clementl.whispr.domain.model.RecordingState
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

        audioRecord?.startRecording()
        _recordingState.value = RecordingState.Recording

        // Start a coroutine on the IO thread to continuously read audio
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val audioBuffer = ShortArray(bufferSize / 2)
            while (isActive) {
                val readSize = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: -1
                if (readSize > 0) {
                    // [VAD integration point]
                    // TODO: Pass this audioBuffer raw audio data to the Silero VAD model
                    Timber.d("Audio chunk read, size: $readSize")
                }
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
    }

    override fun getRecordingStateFlow(): Flow<RecordingState> = _recordingState.asStateFlow()
}