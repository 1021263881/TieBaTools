package com.fapple.Tools;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class HttpService
{
	public String get(String Url) throws mException 
	{
		//第一种方式
		ExecutorService executor = Executors.newCachedThreadPool();
		Task task = new Task("GET", Url);
		FutureTask<String> futureTask = new FutureTask<String>(task);
		executor.submit(futureTask);
		executor.shutdown();
		String re = "";
		try {
			re = futureTask.get();
		} catch (InterruptedException e) {
			throw new mException("网络好像出了点问题喵~", "Get出错，错误信息:\n" + e.toString());
		} catch (ExecutionException e) {
			throw new mException("网络好像出了点问题喵~", "Get出错，错误信息:\n" + e.toString());
		}
		if (re == "" || re == null) {
			throw new mException("网络好像出了点问题喵~", "Get无接收");
		}
		return re;
	}
	public String post(String Url, String data) throws mException
	{
		//第一种方式
		ExecutorService executor = Executors.newCachedThreadPool();
		Task task = new Task("POST", Url, data);
		FutureTask<String> futureTask = new FutureTask<String>(task);
		executor.submit(futureTask);
		executor.shutdown();
		String re = "";
		try {
			re = futureTask.get();
		} catch (InterruptedException e) {
			throw new mException("网络好像出了点问题喵~", "Post出错，错误信息:\n" + e.toString());
		} catch (ExecutionException e) {
			throw new mException("网络好像出了点问题喵~", "Post出错，错误信息:\n" + e.toString());
		}
		if (re == "" || re == null) {
			throw new mException("网络好像出了点问题喵~", "Post无接收");
		}
		return re;
	}
	class Task implements Callable<String>
	{
		private String method;
		private String Url;
		private String data;

		Task(String method, String Url)
		{
			this.method = method;
			this.Url = Url;
			this.data = "";
		}

		Task(String method, String Url, String data)
		{
			this.method = method;
			this.Url = Url;
			this.data = data;
		}

		@Override
		public String call() throws IOException
		{
			switch (method) {
				case "GET":
					return Get(Url);
				case "POST":
					return Post_(Url, data);
				default:
					return "Error Method!";
			}
		}
	}
	private String Get(String Url)throws  UnsupportedEncodingException, IOException
	{
		//string转URL(utf-8)编码
		//产生UnsupportedEncodingException
		Url = URLEncoder.encode(Url, "utf-8");

		// 新建一个URL对象
		URL url = new URL(Url);

		// 打开一个HttpURLConnection连接
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

		// 设置连接主机超时时间
		urlConn.setConnectTimeout(5 * 1000);

		//设置从主机读取数据超时
		urlConn.setReadTimeout(5 * 1000);

		// 设置是否使用缓存  默认是true
		urlConn.setUseCaches(true);

		// 设置为Get请求
		urlConn.setRequestMethod("GET");

		//urlConn设置请求头信息
		//设置请求中的媒体类型信息。
		//urlConn.setRequestProperty("Content-Type", "application/json");

		//设置客户端与服务连接类型
		urlConn.addRequestProperty("Connection", "Keep-Alive");

		// 开始连接
		urlConn.connect();

		// 判断请求是否成功
		String result;
		if (urlConn.getResponseCode() == 200) {
			// 获取返回的数据
			result = streamToString(urlConn.getInputStream());
			//Log.e(TAG, "Get方式请求成功，result--->" + result);
		} else {
			result = null;
			//Log.e(TAG, "Get方式请求失败");
		}

		// 关闭连接
		urlConn.disconnect();
		return result;
	}

	private String Post(String Url, String data)throws  UnsupportedEncodingException, IOException
	{
		//用POST发送键值对数据
		URL url = new URL("http://192.168.31.200:8080/HttpServer/MyServlet");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//通过setRequestMethod将conn设置成POST方法
		conn.setRequestMethod("POST");
		//调用conn.setDoOutput()方法以显式开启请求体
		conn.setDoOutput(true);
		//获取conn的输出流
		OutputStream os = conn.getOutputStream();

		//将请求体写入到conn的输出流中
		os.write(data.getBytes());
		//记得调用输出流的flush方法
		os.flush();
		//关闭输出流
		os.close();
		//当调用getInputStream方法时才真正将请求体数据上传至服务器
		InputStream is = conn.getInputStream();
		//获得响应体的字节数组
		return streamToString(is);
	}
	private String Post_(String path, String Info) throws IOException
	{ 

		//1, 得到URL对象 
		URL url = new URL(path); 

		//2, 打开连接 
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 

		//3, 设置提交类型 
		conn.setRequestMethod("POST"); 

		//4, 设置允许写出数据,默认是不允许 false 
		conn.setDoOutput(true); 
		conn.setDoInput(true);//当前的连接可以从服务器读取内容, 默认是true 

		//5, 获取向服务器写出数据的流 
		OutputStream os = conn.getOutputStream(); 
		//参数是键值队  , 不以"?"开始 
		os.write(Info.getBytes()); 
		//os.write("googleTokenKey=&username=admin&password=5df5c29ae86331e1b5b526ad90d767e4".getBytes()); 
		os.flush();
		//6, 获取响应的数据 
		//得到服务器写回的响应数据 
		BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
		String str = br.readLine();   
		//System.out.println("响应内容为:  " + str); 

		return  str;
	}

	/**
	 * 将输入流转换成字符串
	 *
	 * @param is 从网络获取的输入流
	 * @return
	 */
	private String streamToString(InputStream is)throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		baos.close();
		is.close();
		byte[] byteArray = baos.toByteArray();
		return new String(byteArray);
	}
}
