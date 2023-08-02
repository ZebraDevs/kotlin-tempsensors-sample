# TempSensors Demo Application

Demo Application demonstrating the capabilities and the integration of the ZSFinder SDK for the communication with the our 2nd generation of Zebra's Electronic Temperature Sensors.

## Features

- Written in Kotlin
- MVVM & Coroutines have been used
- Full integration with the ZSFinder service (Binding and receiving events from the Service)
- Discovery process, connect to a discovered sensor
- Retrieve current temperature from a sensor
- Retrieve all the recorded temperatures by a sensor if being used for a Task
- Retrieve other useful info from the sensor such as:
  - ID
  - Battery percentage level
  - Alarm Status
  - MAC Address
  - Last Discovered date

## Blog Post

This demo is part of a blog post which has been released on the developer portal where I'm explaining step by step how to integrate the AIDL interfaces inside a project and how to communicate with the sensors.
If you're interested, feel free to head over [here](https://developer.zebra.com/blog/getting-started-zsfinder-sdk-android-project).

## Disclaimer

Please be aware that this application is distributed as is without any guarantee of additional support or updates.

# License

[MIT](LICENSE.txt)