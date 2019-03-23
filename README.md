# OkSerialPort
 支持无阻塞，设置超时时间，可多个串口，一问一答同步响应
 
## 预览
![预览](/img/serialport.gif)

## 使用

### 创建自己的 SerialHelper，配置Port、BaudRate、Timeout及实现自己的读取协议

```
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
        ...
        
  ```
  
  ### 初始化OkSerialPort（App.kt）
  ```
     private fun initOkSerialPort(){
        OkSerialPort.instance.init(MySerialHelper())
    }
  ```
  
  ### 发送（MainActivity.kt）
  ```
   private fun send(ba: ByteArray){
        OkSerialPort.instance.start {
            Log.e(TAG,"【----------start------------】")
            setRecord(ba,true)
        }.send(ba).receive {
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
  ```
  
  ## 本地模拟串口收发
  
  ### 安装[虚拟串口软件](https://raw.githubusercontent.com/fengjunren/SerialPortDemo/master/%E7%9B%B8%E5%85%B3%E8%BD%AF%E4%BB%B6/vspd.exe)
  ![预览](/img/vsp.png)
  
   ### 安装[串口调试助手](https://raw.githubusercontent.com/fengjunren/SerialPortDemo/master/%E7%9B%B8%E5%85%B3%E8%BD%AF%E4%BB%B6/serial_port_utility_20160916.exe)
  ![预览](/img/assistant.png)
  
   ### VirtualBox串口配置
  ![预览](/img/vbox-serialport.png)
  
  打开genymotion,直接跑代码
