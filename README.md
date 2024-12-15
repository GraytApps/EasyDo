<img src="https://github.com/user-attachments/assets/3e7675e6-3e56-4cc7-a457-03e3c99ac7c3" height="650" />

Sample To Do List Android App
=============================

This is a sample Android App that allows users to create a todo list of items.

## Building the Sample App

First, clone the repo:

`git clone https://github.com/GraytApps/EasyDo.git`

### Demo
Find a demo of the app [here](https://drive.google.com/file/d/12FRRwHOvwYajil_gxMVnIRgxZX60e7gD/view?usp=share_link)

### Android Studio

(These instructions were tested with Android Studio Ladybug | 2024.2.1 Patch 3)

* Open Android Studio and select `File->Open...` or from the Android Launcher select `Import project` and navigate to the root directory of your project.
* Select the directory of the cloned repo.
* Click 'OK' to open the the project in Android Studio.
* A Gradle sync should start, but you can force a sync and build the 'app' module as needed.

## Running the Sample App

Connect an Android device to your development machine, or use an Android emulator.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'

## Using the Sample App

* Create new to-do items by clicking on the FAB in the bottom right corner of the home screen.
* Edit an existing to-do item by simply tapping on it in the home screen.
* Delete a to-do item by long clicking on it, and then clicking on the 'Delete' option in the popup menu.

## Development decisions

* The `SafeArgs` plugin was used to ensure that `NewTodoFragment` would always be passed a `todoId` when editing a `Todo` item.
* `Hilt` was used to allow the `TodoRepository` to be easily accessible to all `ViewModels` that wish to manipulate the `Todo` items.
* A single activity approach was incorporated into the code architecture, so that each screen can act as a `Fragment`. This will allow for easier and more scalable future iterations.
