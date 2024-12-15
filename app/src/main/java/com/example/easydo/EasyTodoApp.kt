package com.example.easydo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// We create an empty custom Application because Hilt requires an Application class
// with the @HiltAndroidApp annotation.

@HiltAndroidApp
class EasyTodoApp : Application()