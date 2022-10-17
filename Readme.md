# Lab1文档

钟尹骏 19302010025（50%）

李欣泽 19302010033（50%）

[TOC]

## 使用说明

客户端只有连接位于同一局域网内的服务端才能连接成功

多图并排排列时，从左到右由依次命名为图1、图2···

### 通用

在应用主界面点击右上角...图标显示菜单，点击view files查看文件，点击about查看应用信息

#### 查看文件

显示当前应用已存储文件（图1）

点击文件夹展示该文件夹中文件，非根目录下顶部有一个../文件夹，点击该文件夹返回上一级目录

点击文件展示文件信息（大小和md5）（图2）



<center class="half">
  <img src="view files.png" alt="view files" style="zoom:25%;" />
  <img src="file information.png" alt="view files" style="zoom:25%;" />
</center>

#### 关于

显示本机IP、监听端口以及其它信息

<img src="about.png" alt="view files" style="zoom:25%;" />

### 服务端

#### 主界面

显示当前监听状态，启动应用时默认为关闭状态，分为关闭状态（图1）和打开状态（图2）

点击按钮设置监听/停止监听连接请求

<center class="half">
	<img src="serverOff.png" alt="view files" style="zoom:25%;" />
	<img src="serverOn.png" alt="view files" style="zoom:25%;" />
</center>



### 客户端

#### 主界面

进入应用界面如下，点击connect跳转至连接页面

<img src="main.png" alt="view files" style="zoom:25%;" />

#### 连接页面

连接服务器，需要手动设置服务器IP和端口，通过下方按钮设置非匿名登陆（图1）或匿名登陆（图2）

若连接失败，显示错误信息，错误分为无法连接（图3）和用户错误（图4）

连接成功后跳转至下载页面

从此页面返回主界面或再次登录时会断开之前与服务器的连接

<center class="half">
	<img src="connect.png" alt="view files" style="zoom:25%;" />
	<img src="connectAnonymous.png" alt="view files" style="zoom:25%;" />
  <img src="connectFail.png" alt="view files" style="zoom:25%;" />
  <img src="connectFalse.png" alt="view files" style="zoom:25%;" />
</center>

#### 下载页面

显示服务器应用根目录下文件（图1）

点击文件夹展示该文件夹中文件，非根目录下顶部有一个../文件夹，点击该文件夹返回上一级目录

长按文件夹或文件提示是否下载（图2），点击yes确认下载，点击提示框外部分取消下载

下载成功后，页面下方弹出消息提示下载信息（图3）

点击右上角...图标显示菜单，点击upload跳转至上传页面，点击terminal跳转至命令行页面，点击settings跳转至设置页面，点击refresh刷新内容，显示更新后根目录下文件

<center class="half">
	<img src="download.png" alt="view files" style="zoom:25%;" />
  <img src="downloadRequest.png" alt="view files" style="zoom:25%;" />
  <img src="downloadReply.png" alt="view files" style="zoom:25%;" />
</center>

#### 上传页面

显示客户端应用根目录下文件（图1）

点击文件夹展示该文件夹中文件，非根目录下顶部有一个../文件夹，点击该文件夹返回上一级目录

点击文件展示文件信息（大小和md5）（图2）

长按文件夹或文件提示是否上传（图3），点击yes确认上传，点击提示框外部分取消上传

上传成功后，页面下方弹出消息提示上传信息（图4）

点击右上角...图标显示菜单，点击upload跳转至下载页面，点击terminal跳转至命令行页面，点击settings跳转至设置页面，点击refresh刷新内容，显示更新后根目录下文件

<center class="half">
	<img src="upload.png" alt="view files" style="zoom:25%;" />
	<img src="uploadInformation.png" alt="view files" style="zoom:25%;" />
  <img src="uploadRequest.png" alt="view files" style="zoom:25%;" />
  <img src="uploadReply.png" alt="view files" style="zoom:25%;" />
</center>

#### 命令行页面

显示日志以及命令输入框，上侧为日志，下侧为输入框（图1），日志效果（图2），输入时效果（图3）

<center class="half">
	<img src="terminal.png" alt="view files" style="zoom:25%;" />
  <img src="terminalLog.png" alt="view files" style="zoom:25%;" />
  <img src="terminalInput.png" alt="view files" style="zoom:25%;" />
</center>

#### 设置页面

显示当前文件传输模式设置

<center class="half">
	<img src="setting.png" alt="view files" style="zoom:25%;" />
</center>

## 文件结构

客户端基本结构如左图所示，服务端基本结构如右图所示，两者结构基本一致，主要分为3个部分，即Activity、Service和Logic

<center class="half">
	<img src="clientStructure.png" alt="view files" style="zoom:25%;" />
  <img src="serverStructure.png" alt="view files" style="zoom:29%;" />
</center>

```java
Activity										//构成安卓应用前端
Service											//构成安卓应用后端
Logic												//实现FTP的所有逻辑，可以作为独立的java应用来使用
	Client/Server								//存储连接状态
		Status											//包含type、mode、structure设置、连接采用主动/被动模式和是否采用压缩策略传输
		IOManager										//存储连接所用的命令连接和数据连接，封装了写命令、读响应、关闭连接等方法
	Command											//命令相关
  	CommandDealer								//接口，提供统一的deal方法
  	Command											//实现FTP命令，根据命令类型执行对应命令
  Transmitter									//封装传输操作
  	Transmitter									//抽象类，提供统一的retrieve和store方法
  	AsciiTransmitter						//以Ascii模式传输
  	ImageTransmitter						//以Image模式传输（Binary）
  Util												//存储常量和静态方法
  	Constants										//存储常量，主要包括端口范围、连接超时、各种传输模式对应序号和page模式规范
  	ReturnCode									//用HashMap记录返回码对应的信息
  	Tools												//存储静态方法，主要包括获取端口、解析地址和端口、获取文件信息、压缩和解压
FileAdapter/LogAdapter			//附属于Activity，用于创建RecyclerView
MyApplication								//在启动时调用，仅用于获取静态context来获取包的文件根路径
```

## 具体实现

### FTP部分

#### 状态存储

因为连接状态中内容较多，拆分为Status和IOManager分别存储socket无关/相关的内容

```java
public class Status {
    private int type = 0;                       //0 = Ascii, 1 = Image
    private int mode = 0;                       //0 = Stream, 1 = Block, 2 = Compressed
    private int structure = 0;                  //0 = File, 1 = Record, 2 = Page
    private boolean isPassive = true;
    private boolean zipped = false;
    
    ... //getter and setter
}
```

```java
public class IOManager {
    private BufferedReader reader = null;		//读取命令连接内容
    private PrintWriter writer = null;			//写入命令连接

    private String response = "";						//记录上次命令执行的响应（仅客户端）

    private Socket commandSocket;
    private Socket dataSocket;
    private ServerSocket serverSocket;      //如果以accept方式创建datasocket，需要同时关闭，否则端口会被一直占用
  
		public void write(String content) {
        writer.println(content);
        writer.flush();
    }

    public String read() {
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
  
    ... //getter and setter
}
```

#### 执行命令

使用策略模式实现命令，通过命令类型（如USER、RETR等）从Hashmap中获取对应的CommandDealer实现对象，向这个对象的deal方法传入参数执行命令

```java
public interface CommandDealer {
    String deal(String commandLine, Status status, IOManager ioManager);
}

private static final HashMap<String, CommandDealer> commands = new HashMap<>();

public static String doCommand(String commandLine, Status status, IOManager ioManager) {
    String[] parameters = commandLine.split(" ");
    CommandDealer commandDealer = commands.getOrDefault(parameters[0].toUpperCase(), defaultDealer);
    return commandDealer.deal(commandLine, status, ioManager);
}
```

#### 主动模式

主动模式下，由客户端开启Serversocket来accept来自服务端的datasocket请求。如果该Serversocket关闭会导致datasocket同样关闭，但不关闭会一直占用端口，因此需要在关闭datasocket的时候一起关闭。被动模式下的服务端同理

#### 上传/下载

按照RFC文档，因为简化了mode部分，所以最后具体传输方式取决于type+structure，type分为Ascii（字符流）和Image（字节流），structure分为File（逐字节/字符传输）、Record（按行传输）和Page（按页传输），可行的四种组合为Ascii+File、Ascii+Record、Image+File和Image+Page，前三种通过关闭datasocket来表示传输完成，最后一种可以根据头部判断传输完成

Page模式下，需要一个头部来表示头部长度、页号、页中数据长度和当前页类型（起始、中间传输、结束），生成头部方法如下

```java
//头部长度默认为8，第0位表示头部长度，第1至4位表示页号，第5和6位表示数据长度，第7位表示页类型
private static byte[] getHeader(int pageNumber, int dataLength, int type) {
  byte[] head = new byte[Constants.HEAD_LENGTH];
  head[0] = Constants.HEAD_LENGTH;
  head[1] = (byte) ((pageNumber >> 24) & 0xff);
  head[2] = (byte) ((pageNumber >> 16) & 0xff);
  head[3] = (byte) ((pageNumber >> 8) & 0xff);
  head[4] = (byte) (pageNumber & 0xff);
  head[5] = (byte) ((dataLength >> 8) & 0xff);
  head[6] = (byte) (dataLength & 0xff);
  head[7] = (byte) (type);
  return head;
}
```

因为各页到达顺序可能错乱，需要根据页号写入文件对应位置，因此需要使用RandomAccessFile的seek()方法

接收方每次先读这样一个页头部，判断页类型，若为结束则停止接收，若为开始则继续读下一个页头部，否则为中间接收

中间接收情况下，根据头部获取数据长度和页号，读取相应长度的内容并写入到文件对应位置，该页处理结束

当前页处理结束后，从数据连接中再读取的8位刚好为下一个page的头部，循环执行直到遇到结束页

### 安卓部分

#### 权限申请

为获取本机IP和实现网络通信，需要申请ACCESS_NETWORK_STATE和INTERNET的授权

为使用前台Service，需要申请FOREGROUND_SERVICE授权

#### 导航设置

使用安卓原生menu用于导航，重写onCreateOptionsMenu动态添加栏目，重写onOptionsItemSelected动态设置响应事件

#### 后台响应

为使服务端在后台也能运行，调用startForeground方法将ListenService修改为前台Service

#### 文件/日志展示

查看、上传和下载文件，以及命令行日志都使用了RecyclerView组件来展示，创建一个ArrayList存储内容，通过对应的Adapter为每一个单独元素创建Holder并渲染在视图中，替代了原来ListView的实现方式。该组件优点在于响应更快，而且可以为每个元素自定义布局，例如额外创建图标用于区分文件/文件夹，而ListView中不能插入文本以外的内容

#### 逻辑处理

安卓应用禁止在主线程中进行网络通信，而创建的子线程中禁止修改view中元素，最后选择使用IntentService发送Broadcast并在Activity中注册BroadcastReceiver的方式，在后端处理逻辑，前端修改视图

## 优化策略

### 使用两个datasocket（未采用）

该优化策略仅能在传输文件夹或page模式传输文件时使用，适用范围较小而且仅能加速本地文件读写，对于网络传输优化意义不大

此外，使用多线程编程会导致内存占用增加，在模拟器上测试时频繁触发垃圾回收机制，反而减慢了传输速率，同时考虑到如果作为实际应用，应当尽可能减少对于用户设备的负担

综合以上两点考量，最后并没有采用该策略

### 使用zip传输（小文件）

#### 计算方式

在Image+Page下比较，开始计时——客户端发送传输命令——客户端传输文件——客户端收到服务端成功响应——停止计时

考虑到本地解压文件不影响传输时间和用户在应用中感受到的等待时间，服务端接收完立即发出成功响应，如果是zip模式下服务端发送响应后再本地解压文件，即实际zip模式传输流程如下：

开始计时——客户端压缩文件——客户端发送传输命令——客户端传输文件——客户端收到服务端成功响应——停止计时——服务端解压

#### 测试结果

#### 分析

对于小文件而言，耗时部分主要在于网络传输而非本地文件读写
多个小文件在网络IO时浪费大量时间，file模式下需要多次建立datasocket，page模式下虽然不关闭datasocket，但每一个page中也有大量载荷被浪费，而减小page size也会导致更多空间被page header占据

相比之下，在本地将多个小文件压缩成一个文件耗时较少，但能大幅减少网络传输时间。以视频为例，未开启zip时传输200个小文件需要14.6秒，而开启zip时仅需要2.7秒（这一测试建立在模拟器本地传输之上，真实情况下设之间互传网络传输占比更大，导致差异会更大）

但是这一策略不适用于大文件，因为本地文件读写时间占比大幅增加。压缩传输经历三个过程：本地压缩——网络传输——接收方解压，对于测试文件，尤其是给出的测试文件，因为随机生成所以无法有效压缩文件大小，基本上相当于本地多读写同一大文件全部内容两遍，远远超过网络传输节省的时间。在模拟器上测试时，未开启zip时传输big0000需要18.2秒，而开启zip时需要44.8秒



