# Pigeon

Pigeon is a Bluetooth-based Android chat application that enables seamless, real-time communication between nearby devices. Developed in Android Studio using Java, Pigeon provides an intuitive interface and straightforward Bluetooth connectivity, making chatting with friends or peers as simple as turning on your Bluetooth.

## Features

- **Bluetooth Connectivity:** Easily discover and connect with nearby Bluetooth-enabled devices.
- **Real-time Chat:** Send and receive messages instantly over a secure Bluetooth connection.
- **User-Friendly Interface:** A clean and minimalistic UI designed for efficient chatting.
- **Lightweight & Fast:** Optimized for performance with minimal resource consumption.

## Getting Started

These instructions will help you set up the project on your local machine for development and testing.

### Prerequisites

- **Android Studio:** Ensure you have Android Studio installed (recommended version 4.0 or above).
- **Android SDK:** The project supports devices running on Android API level 16 (Jelly Bean) or higher. (Check your project’s `build.gradle` for exact version requirements.)
- **Bluetooth-Capable Device/Emulator:** Either an Android device with Bluetooth capabilities or an emulator that supports Bluetooth testing.

### Installation

1. **Clone the Repository:**

   Open your terminal and run:
   ```bash
   git clone https://github.com/tomasproanop/pigeon.git
   ```

2. **Open the Project in Android Studio:**

   - Launch Android Studio.
   - Click on `File > Open` and navigate to the cloned repository folder.
   - Allow Android Studio to sync and build the project (Gradle sync may take a few minutes).

3. **Build and Run:**

   - Connect your Android device (with Bluetooth enabled) or start an emulator.
   - Click the **Run** button in Android Studio to install and launch the app.

## Usage

1. **Enable Bluetooth:**
   - Before launching the app, make sure Bluetooth is enabled on your device.

2. **Start the App:**
   - Open the Pigeon app. It will automatically search for nearby devices running Pigeon.

3. **Connect:**
   - Select a device from the list to establish a Bluetooth connection.
   - Once connected, you can start chatting.

4. **Chat:**
   - Use the chat interface to send and receive messages in real-time.
   - Enjoy seamless communication over Bluetooth.

## Project Structure: Main Classes

- **`MainActivity.java`:** The entry point of the application, handling the overall UI and navigation.
- **`BluetoothService.java`:** Manages Bluetooth functionalities such as scanning, pairing, connecting, and data transfer.
- **Other Components:** Additional classes and fragments handle the chat interface, message display, and user input.

## Contributing

Contributions are welcome! If you wish to improve Pigeon, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes.
4. Submit a pull request with a detailed description of your changes.

## Known Issues

- **Bluetooth Variability:** Some devices may experience connectivity issues due to variations in Bluetooth hardware and Android versions.
- **Compatibility Testing:** The app has been tested on a limited range of devices (Android 9 up to Android 13). Please report any issues on your device model.

## License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/tomasproanop/pigeon/blob/main/LICENSE) file for details.

## Contact

For questions, suggestions, or support, please open an issue in this repository or contact me at [your-tomasproanop at gmail.com.
