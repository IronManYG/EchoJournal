# EchoJournal

<p align="center">
  A colorful audio journaling app designed to help users quickly log their thoughts and mood throughout the day with voice memos.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Min%20SDK-25-blue.svg" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target%20SDK-35-brightgreen.svg" alt="Target SDK">
  <img src="https://img.shields.io/badge/Kotlin-2.0.20-purple.svg" alt="Kotlin Version">
  <img src="https://img.shields.io/badge/Jetpack%20ComposeBom-2025.02.00-orange.svg" alt="Jetpack Compose Version">
</p>

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack & Libraries](#tech-stack--libraries)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Mockups & Design](#mockups--design)
- [Future Considerations](#future-considerations)
- [Acknowledgment](#acknowledgment)

## Overview

**EchoJournal** is an Android application that allows users to:

- Record quick voice memos  
- Tag them with moods and topics  
- Organize logs in a filterable journal  
- Optionally enable extended features like AI transcription  

This project is built primarily with **Kotlin**, **Jetpack** libraries, and **Material Design** to deliver a modern, intuitive user experience.

## Features

### Splash Screen
- Simple branding screen displayed on app launch.

### Journal History
- Displays a list of audio entries grouped by day (Today, Yesterday, or date headers).  
- Filter audio logs by mood and topic.  
- Supports inline audio playback with visual audio-level indicators.

### Quick Voice Recording
- Start recording via a floating action button.  
- Pause/resume functionality.  
- Cancel ongoing recordings.

### Create Record Screen
- Add or edit details like mood, topic(s), title, and optional description.  
- Save the new log entry to the journal.

### (Future) Settings Screen
- Set default mood or topics for new log entries.

### (Optional) AI Transcription
- Button to transcribe recordings and insert text into the description field.

### (Future) Home Screen Widget
- Quickly open the app to a new recording state from the user’s home screen.

## Screenshots

<table>
  <tr>
    <td align="center">
      <strong>1. Splash-screen</strong><br/>
      <img src="previews/1- Splash-screen.png" width="240" alt="Splash-screen" />
    </td>
    <td align="center">
      <strong>2. Entries - Empty</strong><br/>
      <img src="previews/2- Entries - Empty.png" width="240" alt="Entries - Empty" />
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>3. Entries</strong><br/>
      <img src="previews/3- Entries.png" width="240" alt="Entries" />
    </td>
    <td align="center">
      <strong>4. Entries - Mood Filter Active</strong><br/>
      <img src="previews/4- Entries - Mood Filter Active.png" width="240" alt="Entries - Mood Filter Active" />
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>5. Entries - Topic Filter Active</strong><br/>
      <img src="previews/5- Entries - Topic Filter Active.png" width="240" alt="Entries - Topic Filter Active" />
    </td>
    <td align="center">
      <strong>6. Entries - Record in Progress</strong><br/>
      <img src="previews/6- Entries - Record in Progress.png" width="240" alt="Entries - Record in Progress" />
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>7. Create Record – Journal Entry Additional Details (Empty fields)</strong><br/>
      <img src="previews/7- Create Record - Journal Entry Additional Details - Empty fields.png" width="240" alt="Create Record - Empty fields" />
    </td>
    <td align="center">
      <strong>8. Create Record – Journal Entry Additional Details</strong><br/>
      <img src="previews/8- Create Record - Journal Entry Additional Details.png" width="240" alt="Create Record - Additional Details" />
    </td>
  </tr>
</table>

## Tech Stack & Libraries

- Minimum SDK level 25
- Modern Development
  - [Kotlin](https://kotlinlang.org/) + [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
  - [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI development
  - [Koin](https://insert-koin.io/) for dependency injection
- Architecture Components
  - Room for local database
  - Lifecycle components
  - Navigation Compose
 - UI Components
  - [Material Design 3](https://m3.material.io/) components
- **Audio Recording & Playback**:  
  - Native [MediaRecorder](https://developer.android.com/reference/android/media/MediaRecorder) for capturing audio  
  - Native [MediaPlayer](https://developer.android.com/reference/android/media/MediaPlayer) for playback  
- Testing & Debugging
  - JUnit for unit testing
  - [Timber](https://github.com/JakeWharton/timber) for logging

## Architecture

For the **single-module MVP**, EchoJournal follows these general guidelines:

- **MVI (Model-View-Intent)** for a unidirectional data flow and clear separation of concerns.  
- **Repository Pattern** to manage data sources (local database or future remote services).  
- **Jetpack Components** (ViewModel, StateFlow, etc.) for state management and lifecycle awareness.

### Why Single-Module?
- Simpler Gradle configuration and fewer module boundaries.  
- Faster to set up for smaller teams or single developers.  
- Ideal for rapidly iterating on the MVP.

## Getting Started

1. Clone the repository
```bash
   git clone https://github.com/IronManYG/EchoJournal.git
```

2. Open with Android Studio
3. Sync project with Gradle files
4. Run the app

## Future Considerations
- **Multi-Module Refactor**: As EchoJournal expands (e.g., larger teams, more complex AI features), break down the single module into domain, data, and individual feature modules for better scalability.
- **Advanced AI Transcription**: Move AI logic into a dedicated module (e.g., feature_transcribe).
- **Dark Theme & Theming**: Introduce dynamic theming or light/dark mode toggles once the MVP is solidified.
- **Network Synchronization**: Add cloud sync or user authentication if needed for multi-device usage.

## Acknowledgment

This project was built as part of the **[Pl Mobile Dev Campus](https://pl-coding.com/campus/)** community challenge.

**Thank you for checking out EchoJournal!**

If you have any questions or suggestions, feel free to open an issue or reach out to the maintainer. Happy coding!


