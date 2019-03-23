package com.serialport.demo

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.okserialport.SerialHelper
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

class OKSerialPort private constructor() {
    lateinit var serialHelper: SerialHelper
    private  var mHandler: Handler= Handler(Looper.getMainLooper())

    private val TAG = "OKSerialPort"

    private object Holder {
        val INSTANCE = OKSerialPort()
        val INSTANCE2 = OKSerialPort() // 支持多个串口
    }

    companion object {
        val instance by lazy { Holder.INSTANCE }
        val instance2 by lazy { Holder.INSTANCE2 } // 支持多个串口
    }

    fun init(sh: SerialHelper) {
        serialHelper=sh
    }

    fun open(success:(()->Unit),fail:((Exception)->Unit)){
        if(serialHelper.isOpen)return
        SerialPortExecutors.execIO(Runnable {
            try {
                serialHelper?.open()
                success.invoke()
            }catch (e:Exception){
                fail.invoke(e)
            }
        })
    }

    fun close(success:(()->Unit),fail:((Exception)->Unit)){
        if(!serialHelper.isOpen)return
        SerialPortExecutors.execIO(Runnable {
            try {
                serialHelper?.close()
                success.invoke()
            }catch (e:Exception){
                fail.invoke(e)
            }
        })
    }

    fun ok(param: Param){
        var canExec=true
        if(!serialHelper.isOpen){
            Log.i(TAG, "【串口还没打开，请先打开】" )
            canExec=false
        }
        param.command?:let {
            Log.i(TAG, "【发送的指令不能为空】" )
            canExec=false
        }
        if(!canExec)return

        SerialPortExecutors.execIO(Runnable {
             try {
                param.startCallBack?.run {
                    mHandler.post{
                        this.invoke()
                    }
                }

                val result=serialHelper.senAndRev(param.command)
                param.recvCallBack?.run {
                    mHandler.post{
                        this.invoke(result)
                    }
                }
            }catch (e: IOException){
                param.errorCallBack?.run {
                    mHandler.post{
                        this.invoke(e)
                    }
                }
            }catch (e: ExecutionException){
                 param.errorCallBack?.run {
                     mHandler.post{
                         this.invoke(e)
                     }
                 }
             }catch (e: TimeoutException){
                param.errorCallBack?.run {
                    mHandler.post{
                        this.invoke(e)
                    }
                }
            }catch (e:InterruptedException){
                param.errorCallBack?.run {
                    mHandler.post{
                        this.invoke(e)
                    }
                }
            }finally {
                param.completeCallBack?.run {
                    mHandler.post{
                        this.invoke()
                    }
               }
            }
        })
    }


    fun start(callback:(()->Unit)):Param{
        val param=Param()
        param.startCallBack=callback
        param.okSerialPort=this
        return param
    }

    class Param{
          var recvCallBack: ((ByteArray) -> Unit)? =null
          var errorCallBack: ((Exception) -> Unit)? =null
          var startCallBack: (() -> Unit)? =null
          var completeCallBack: (() -> Unit)? =null
          var command: ByteArray? =null
          var okSerialPort: OKSerialPort? =null

        fun send(cmd: ByteArray):Param{
            command=cmd
            return this
        }

        fun receive(callback:((ByteArray)->Unit)):Param{
            recvCallBack=callback
            return this
        }

        fun onError(callback:((Exception)->Unit)):Param{
            errorCallBack=callback
            return this
        }


        fun onComplete(callback:(()->Unit)):Param{
            completeCallBack=callback
            return this
        }

        fun ok(){
            okSerialPort?.ok(this)
        }

    }

}