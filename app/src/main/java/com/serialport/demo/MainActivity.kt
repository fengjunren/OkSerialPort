package com.serialport.demo

import android.os.Bundle
import android.util.Log
import com.okserialport.ByteUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException


class MainActivity : BaseActivity() {
    private val TAG="MainActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initListener() {
        super.initListener()
        btnSend.setOnClickListener {
            val txtCmd=etCmd.text.trim()
            if(txtCmd.isEmpty()){
                toast("请输入要发送的指令")
            }else{
//                val ba=byteArrayOf(0x03, 0x04, 0x0C, 0x00, 0x01)
                val ba= ByteUtil.HexToByteArr(txtCmd.toString())
                send(ba)
            }
        }
        btnOpen.setOnClickListener {
             open()
        }
        btnClose.setOnClickListener {
            close()
        }
        btnClean.setOnClickListener {
           tvRecord.setText("")
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)
        open()
        val i=Integer.parseInt("A", 16)
        Log.e(TAG,""+i)
    }

    private fun send(ba: ByteArray){
        OkSerialPort.instance.start(ba){
            Log.e(TAG,"【----------start------------】")
            setRecord(ba,true)
        }.receive {
            res->
            setRecord(res,false)
        }.onError {
            e->
            run {
                when (e) {
                    is IOException -> toast("通信超时")
                    is ExecutionException -> toast("通信超时")
                    is TimeoutException -> toast("响应超时")
                    is InterruptedException -> toast("通信中断")
                }
            }
        }.onComplete {
            Log.e(TAG,"【----------onComplete------------】")
        }.ok()
    }


    private fun open(){
        OkSerialPort.instance.open({
            setStatus("已连接")
        },{e->
            Log.i(TAG,"串口启动失败..."+"\n"+getStackInfo(e))
            setStatus("启动失败")
        })
    }

    private fun close(){
        OkSerialPort.instance.close({
            setStatus("未连接")
        },{e->
            Log.i(TAG,"串口关闭异常..."+"\n"+getStackInfo(e))
            setStatus("关闭异常")
        })
    }



    private fun setStatus(txt:String){
        runOnUiThread {
            tvStatus.text = "状态: "+txt
        }
    }

    private fun setRecord(txt:ByteArray, isSend:Boolean){
         setRecord(ByteUtil.ByteArrToHex(txt),isSend)
    }

    private fun setRecord(txt:String, isSend:Boolean){
        runOnUiThread {
            var direction="接收："
            if(isSend){
                direction="发送："
            }
            val s=tvRecord.text.toString()+"\n"+direction+" "+txt+"            "+SimpleDateFormat("HH:mm:ss").format(Date())
            tvRecord.text=s
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        close()
    }
}
