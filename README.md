# Sound nRF Toolbox
 
Sound nRF is an extended version of the nRF Toolbox which can be found at https://github.com/NordicSemiconductor/Android-nRF-Toolbox. it connects to a BLE (Bluetooth low energy) device and provide audio feedback in sync with the provided cadence characteristic measurement. The sound feedback enables for 2-minutes. At the end of the 2-minutes, the present cadence serves as the predefined threshold. As long the present cadence stabilizes within 15 range, the sound gets disabled after the initial 2-minute. If it deviates from the present cadence by 15, the sound re-enables for another 2-minutes. With this system, runners do not receive constant feedback when their cadence stabilizes. The sound serves as an auditory alarm and a compass to help runners find their ideal cadence.

## Requirements.
- An Android IDE for Android operating system such as Android Studio (https://developer.android.com/studio)
- An Android Mobile Smartphone. Sound nRF Toolbox is currently compatible to android devices. For assurance, android 10 update is ideal. Android 4.3 or newer is required.
- A Sensor device with BLE supported that provides RSC (Running speed and cadence) data. e.g. Stryd

### Dependencies

nRF Toolbox depends on [Android BLE Library](https://github.com/NordicSemiconductor/Android-BLE-Library/) 
which has to be cloned into the same root folder as this app. If you prefer a different name, 
update the [*settings.gradle*](https://github.com/NordicSemiconductor/Android-BLE-Library/blob/master/settings.gradle) file.

In order to compile the project the **DFU Library is required**. This project may be found here: 
https://github.com/NordicSemiconductor/Android-DFU-Library.
Since version 1.16.1 it is imported automatically from *jcenter* repository and no special 
configuration is needed. If you want to make some modifications in the DFU Library, please clone 
the DFU Library to the same root as nRF Toolbox is cloned and name the library's folder **DFULibrary**. 
Add the dfu module in Project Structure and edit *app/build.gradle* file and *settings.gradle* 
files as describe in them.

The nRF Toolbox also uses the nRF Logger API library which may be found here: 
https://github.com/NordicSemiconductor/nRF-Logger-API. The library is included in dependencies 
in *build.gradle* file. This library allows the app to create log entries in the 
[nRF Logger](https://play.google.com/store/apps/details?id=no.nordicsemi.android.log) application. 
Please, read the library documentation on GitHub for more information about the usage and permissions.

## Cloning the repository from version control
With Android Studio, you don't need to use the terminal to contribute to an Android project on GitHub. It has native integration with git and GitHub to allow most actions via the Android Studio UI.

When you open Android Studio, it offers the option to open a project from version control. That's the option we'll use.

![step 1](Source_code/resources/8SEqyls.png)

After selecting that option, you can type the URL of the repository, press "Clone", and select a folder. After that, Android Studio will do all the work and open the project ready to go

<p float="left">
  <img src="Source_code/resources/2.png" width="400" />
  <img src="/Source_code/resources/bM92C6R.png" width= "450" /> 
</p>

## How Sound nRF Toolbox works 

### When sound start:
Sound will initiate for 2 minutes when cadence meets the following conditions:
1. When cadence increases from 10.
<b>Example </b>: It's the case when cadence starts from zero.
2. When the cadence gap deviates by 15.
<b>Example </b>: cadence is 150 when sound stops and then it will re-initiate the sound for 2 minutes when cadence increases to 166 or dropped to 134.

### When sound stop:
It will stop when cadence meets the following conditions:
1. When two minutes completed
2. When cadence suddenly dropped to 0 or less than 10 

## RSC path

To find the path to RSC cadence characteristics, go to <i>Users\username\...\Android-nRF-Toolbox-master\app\src\main\java\no\nordicsemi\android\nrftoolbox\rsc\ </i>

Find more information in the Manual.pdf


 


