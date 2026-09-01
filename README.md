# VertexLink-Android
> **Warning**
> 
> This project is currently a **Work in Progress (WIP)**. Features, protocols, and APIs are subject to change.

VertexLink-Android is the Android client application for controlling a PC running [VertexLink-Desktop](https://github.com/arkteek/vertexlink-desktop). It allows users to control mouse movements, keyboard inputs, clipboard content, and system volume over secure encrypted network connections.

## Features

* **Touchpad & Mouse Control**: Multi-touch gestures, left/right click, and scrolling.
* **Remote Keyboard**: Send text, shortcuts, and key events to the desktop host.
* **Clipboard Sync**: Easily transfer copied text between mobile and host OS.
* **Volume Adjustments**: Control desktop master volume remotely.
* **Encrypted Communication**:
  * **TLS**: Secures TCP-based control commands and metadata.
  * **dTLS**: Encrypts low-latency UDP packet streams.
* **ACK Handshake**: Strict pairing process using ACK confirmation to validate authorization before opening network sockets.

## Prerequisites

* **Android Device**: Android 8.0 (API level 26) or higher.
* **VertexLink-Desktop**: The desktop host software running on the target PC. Ensure it is configured and active on the same local network.

## Getting Started

### Installation

1. Download the latest `.apk` from the [Releases](https://github.com/arkteek/vertexlink-android/releases) section.
2. Install the APK on your Android device (ensure installation from unknown sources is allowed if installing manually).

### Pairing & Usage

1. Start **VertexLink-Desktop** on your computer.
2. Launch **VertexLink-Android**.
3. Scan for active desktop hosts or enter the PC's IP address.
4. Execute the ACK pairing request. Once verified by the host, secure TLS (TCP) and dTLS (UDP) streams will initialize automatically.
