package com.serialport.demo;

import android.util.Log;
import com.okserialport.ByteUtil;
import com.okserialport.SerialHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MySerialHelper extends SerialHelper {
    private String TAG="MySerialHelper";

    @Override
    public String getPort() {
        return "/dev/ttyS0";
    }

    @Override
    public int getBaudRate() {
        return 9600;
    }

    @Override
    public int getTimeout() {
        return 5*1000;
    }

    @Override
    protected byte[] readCommand(byte[] command) throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean loop = true;
        byte[] temp = new byte[1];

        while (loop&&!isTimeOut()){
            readWithNoBlock(temp);
            if (temp[0] == getHEADER()) {// 过滤脏指令
                readWithNoBlock(temp);
                if(temp[0]==command[0]){
                    baos.write(getHEADER());
                    baos.write(temp);
                    loop = false;
                }else if(temp[0]!=0){
                    Log.i(TAG,"【过滤脏指令】："+ ByteUtil.ByteArrToHex(temp));
                }
            }else if(temp[0]!=0){
                Log.i(TAG,"【过滤脏指令】："+ ByteUtil.ByteArrToHex(temp));
            }
            if(loop)Thread.sleep(getSleepTime());
        }
        readWithNoBlock(temp);
        int len=temp[0];// 获取数据的长度
        Log.i(TAG,"【响应数据长度】："+ len);
        baos.write(temp);
        byte [] data=new byte[len];
        readWithNoBlock(data);
        baos.write(data);
        return baos.toByteArray();
    }
}
