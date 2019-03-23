package com.okserialport;

import android.util.Log;
import android_serialport_api.SerialPort;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.concurrent.*;

public abstract class SerialHelper {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private DataInputStream mInputStream;
    private String sPort = "/dev/ttyS0";
    private int iBaudRate = 9600;
    private boolean _isOpen = false;
    private int timeout=2*1000;// write and read timeout
    private int sleepTime=10;// loop read inputstream availablelen
    private String Tag="SerialHelper";
    private Boolean isTimeOut=false;

    /**
     * 开始头标志位
     */
    private static final byte HEADER = 0x55;
    /**
     * 结束标志位
     */
    private static final byte END = (byte)0xAA;

    public SerialHelper(String sPort, int iBaudRate)
    {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    public SerialHelper() {
    }

    public SerialHelper(String sPort) {
        this.setPort(sPort);
    }

    public SerialHelper(String sPort, String sBaudRate) {
        this.setPort(sPort);
        this.setBaudRate(sBaudRate);
    }

    public void open() throws SecurityException, IOException, InvalidParameterException
    {
        Log.i(Tag,"【serial is opening .... 】  port:"+getPort()+",baudRate:"+getBaudRate());
        this.mSerialPort = new SerialPort(new File(this.getPort()), this.getBaudRate(), 0);
        this.mOutputStream = this.mSerialPort.getOutputStream();
        this.mInputStream = new DataInputStream(this.mSerialPort.getInputStream());
        this._isOpen = true;

    }

    public void close() throws IOException {
        if(mInputStream!=null){
            mInputStream=null;
        }
        if(mOutputStream!=null){
            mOutputStream.flush();
            mOutputStream.close();
        }

        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
        this._isOpen = false;
    }


    public synchronized byte[] senAndRev(final byte[] command)
            throws IOException, TimeoutException,InterruptedException,ExecutionException {
        Log.i(Tag,"senAndRev .........."+ Arrays.toString(command));

        try{
            Callable<byte[]> callable = new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    isTimeOut=false;
                    mOutputStream.write(command);
                    mOutputStream.flush();
                    return readCommand(command);
                }
            };

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<byte[]> future = executor.submit(callable);
            byte[] v = future.get(getTimeout(), TimeUnit.MILLISECONDS);
            executor.shutdownNow();
            return v;
        }catch (TimeoutException e){
            isTimeOut=true;
            throw e;
        }
    }

    protected abstract byte[] readCommand(byte[] command) throws IOException,InterruptedException;

    protected void readWithNoBlock(byte[] result) throws IOException,InterruptedException{
        if(result==null||result.length<=0)return;
        int len=result.length;
        int availLen=mInputStream.available();
        if(availLen>=len){
             mInputStream.read(result);
        }else {
            boolean loop = true;
            while (loop&&!isTimeOut()){
                availLen=mInputStream.available();
                if(availLen>=len){
                    mInputStream.read(result);
                    loop=false;
                }
                if(loop)Thread.sleep(getSleepTime());
            }
        }
    }


    public int getBaudRate()
    {
        return this.iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (this._isOpen) {
            return false;
        }
        this.iBaudRate = iBaud;
        return true;
    }

    public boolean setBaudRate(String sBaud)
    {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    public String getPort()
    {
        return this.sPort;
    }

    public boolean setPort(String sPort) {
        if (this._isOpen) {
            return false;
        }
        this.sPort = sPort;
        return true;
    }

    public boolean isOpen()
    {
        return this._isOpen;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    public Boolean isTimeOut() {
        return isTimeOut;
    }

    public static byte getHEADER() {
        return HEADER;
    }

    public static byte getEND() {
        return END;
    }

    public int getSleepTime() {
        return sleepTime;
    }
}
