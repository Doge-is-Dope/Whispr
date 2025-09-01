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
  Transcribes user speech into text using a self-hosted Whisper implementation. Supports both Chinese and English.

- [ ] **AI Dialogue Engine**  
  Uses OpenAI API to handle context-aware conversations (greetings, Q&A, small talk).

- [ ] **Text-to-Speech (TTS)**  
  Converts AI-generated text responses into natural-sounding audio using the system TTS engine.

- [x] **Free Mic Mode**  
  Enables a manual toggle for continuous listening. VAD stays active to detect utterances. Supports interrupting TTS playback.

- [ ] **Interrupt Mechanism**  
  Allows users to interrupt AI speech mid-sentence. TTS stops immediately and switches back to listening mode while preserving context.
