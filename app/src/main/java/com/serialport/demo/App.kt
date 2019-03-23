package com.serialport.demo

import android.app.Application


class App : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: App
    }

  override fun onCreate(){
      super.onCreate()
  }

}
