package com.serialport.demo

import java.io.PrintWriter
import java.io.StringWriter

fun Any.getStackInfo(e:Throwable):String{
    val sw = StringWriter()
    e.printStackTrace(PrintWriter(sw))
    return sw.toString()
}