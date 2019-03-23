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
      initOkSerialPort()
  }

    private fun initOkSerialPort(){
        OkSerialPort.instance.init(MySerialHelper())
    }

}
