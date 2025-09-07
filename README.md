# Whispr

Whispr is an intelligent Android app that detects faces to automatically initiate voice interaction. It records speech, transcribes it via OpenAI’s Transcription API, and generates AI-powered responses. The app showcases how to combine CameraX, ML Kit, audio processing, and conversational AI in a modern Android architecture.

> ⚠️ This app is provided as a demo and is not intended for production use.

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Android Studio Iguana | 2023.2.1 or newer.
*   Android SDK 27 or newer.
*   An OpenAI API key.

### Installation

1.  Clone the repo
2.  Open the project in Android Studio.
3.  Create a `local.properties` file in the root of the project and add your OpenAI API key:

    ```
    OPENAI_API_KEY=sk-...
    ```
4.  Sync Gradle and download the required dependencies.
5.  Run the app on an emulator or a physical device.

## 📂 Project Structure

The project follows the standard Android app structure, with a few key directories:

```
app/src/main/java/com/clementl/whispr/
├── data
│   ├── datasource      # Local and remote data sources (Audio, Face Detection, API).
│   └── repository      # Implementation of the repositories.
├── di                  # Hilt dependency injection modules.
├── domain
│   ├── model           # Domain models.
│   ├── repository      # Repository interfaces.
│   └── usecase         # Use cases for different business logic.
├── ui
│   ├── screens         # Composable screens for different parts of the app.
│   └── components      # Reusable Jetpack Compose components.
└── utils               # Utility classes and extension functions.
```

## 🗺️ Project Roadmap

- [x] **Face Detection Trigger**  
  Automatically starts audio recording when a face is detected using ML Kit.

- [ ] **Voice Activity Detection (VAD)**  
  Detects the start and end of speech using a VAD engine to avoid silent segments.

- [ ] **Speech-to-Text (STT)**  
  Real-time streaming transcription of user speech with Whisper-Streaming or OpenAI Realtime API. Provides partial and final transcripts.

- [ ] **AI Dialogue Engine**  
  Uses OpenAI API to handle context-aware conversations.
  Includes memory layers (short-term summaries, retrieval, preference KV) for personalization and continuity.

- [ ] **Text-to-Speech (TTS)**  
  Streaming text-to-speech playback (Realtime) with immediate audio output. Falls back to system TTS when streaming is unavailable.

- [x] **Free Mic Mode**  
  Enables a manual toggle for continuous listening. VAD stays active to detect utterances. Supports interrupting TTS playback.

- [ ] **Interrupt Mechanism**  
  Supports barge-in: user speech interrupts AI playback instantly. Unfinished responses are canceled/truncated, context is preserved, and the app returns to listening mode.

## ✨ Interaction Flow

The primary states are:

1.  Idle: The initial state. The microphone is off.
2.  Recording: A brief, transitional state when the microphone is activated.
3.  Silence: The microphone is on and actively listening, but no speech is detected.
4.  Speech: The microphone is on, and the VAD has detected speech.
5.  Transcribing (Streaming): The microphone is on and partial transcripts are produced in real time, with final text committed before sending to AI.
6.  Thinking: The transcribed text is sent to the AI to generate a response.
7.  Responding: The AI's response is converted to audio and played back (TTS).

State Transition Diagram

```
Idle
 ↓
Recording
 ↓
Silence ←→ Speech
 ↓
Transcribing
 ↓
Thinking
 ↓
Responding
```
