package com.serialport.demo

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object SerialPortExecutors {
    // 密集型IO  disk network
    private val io: ExecutorService =Executors.newFixedThreadPool(2)

    fun execIO(r:Runnable){
         io.execute(r)
    }

    fun shutDown() {
        if(!io.isShutdown) io.shutdownNow()
    }
}