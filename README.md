# Sound nRF Toolbox
 
Sound nRF is an extended version of the nRF Toolbox which can be found at https://github.com/NordicSemiconductor/Android-nRF-Toolbox. This manual provides the code explanation how to add sound synchronization with cadence data to the nRF Toolbox app. It also shows how to enable/re-enable sound for 2-minutes when the presented cadence deviates by 15.

## Requirements.
- An Android IDE for Android operating system such as Android Studio (https://developer.android.com/studio)
- An Android Mobile Smartphone. Sound nRF Toolbox is currently compatible to android devices. For assurance, android 10 update is ideal. Android 4.3 or newer is required.
- A Sensor device with BLE supported that provides RSC (Running speed and cadence) data. e.g. Stryd

## Cloning the repository
With Android Studio, you don't need to use the terminal to contribute to an Android project on GitHub. It has native integration with git and GitHub to allow most actions via the Android Studio UI.

When you open Android Studio, it offers the option to open a project from version control. That's the option we'll use.

![step 1](Source_code/resources/8SEqyls.png)

After selecting that option, you can type the URL of the repository, press "Clone", and select a folder. After that, Android Studio will do all the work and open the project ready to go

<p float="left">
  <img src="Source_code/resources/2.png" width="450" />
  <img src="/Source_code/resources/bM92C6R.png" width= "450" /> 
</p>

## RSC path

To find the path to RSC cadence characteristics, go to <i>Users\username\...\Android-nRF-Toolbox-master\app\src\main\java\no\nordicsemi\android\nrftoolbox\rsc\ </i>
Find more information in the Manual.pdf
 


