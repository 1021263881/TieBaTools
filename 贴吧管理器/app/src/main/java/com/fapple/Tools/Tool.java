package com.fapple.Tools;

import android.text.*;
import android.webkit.*;
import java.io.*;
import java.security.*;
import java.text.*;
import java.util.*;

public class Tool
{
	public static void loadHtmlInWebview(WebView webview, String Html)
	{
		String thing = "<html><header>" + getCSS() + "</header><body>" + Html + "</body></html>";
		webview.loadData(thing, "text/html; charset=UTF-8", null);
	}

	//自适应CSS
	private static String getCSS()
	{
		return "<style type=\"text/css\"> img {" +
			"width:100%;" +//限定图片宽度填充屏幕
			"height:auto;" +//限定图片高度自动
			"}" +
			"body {" +
			"margin-right:15px;" +//限定网页中的文字右边距为15px(可根据实际需要进行行管屏幕适配操作)
			"margin-left:15px;" +//限定网页中的文字左边距为15px(可根据实际需要进行行管屏幕适配操作)
			"margin-top:15px;" +//限定网页中的文字上边距为15px(可根据实际需要进行行管屏幕适配操作)
			"font-size:42px;" +//限定网页中文字的大小为40px,请务必根据各种屏幕分辨率进行适配更改
			"word-wrap:break-word;" +//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
			"}" +
			"</style>";
	}

	public static void copyToClipBoard(ClipboardManager cm, String text)
	{
		// 从API11开始android推荐使用android.content.ClipboardManager
		// 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
		//ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		// 将文本内容放到系统剪贴板里。
		cm.setText(text);

		/*A. 创建普通字符型ClipData：
		 ClipData mClipData =ClipData.newPlainText("Label", "Content");         //‘Label’这是任意文字标签
		 B. 创建URL型ClipData：
		 //ClipData.newRawUri("Label",Uri.parse("http://www.baidu.com"));
		 C. 创建Intent型ClipData：

		 ClipData.newIntent("Label", intent);
		 注意：上面三种方法只在ClipData对象中创建了一个ClipData.Item对象，如果想向ClipData对象中添加多个Item应该通过ClipData对象的addItem()方法添加。

		 （3）将ClipData数据复制到剪贴板：
		 ClipboardManager.setPrimaryClip(ClipData对象);
		 （4）从剪贴板中获取ClipData数据：
		 ClipboardManager.getPrimaryClip();
		 */
	}

	//产生一个新的随机数，传参随意位数
	public static String newNumber(int num)
	{
		num --;
		Random rnd = new Random();
		String re = "";
		int r = 9 * (int)Math.pow(10, num) - 1;
		r = rnd.nextInt(r) + (int)Math.pow(10, num);
		re += r;
		return re;
	}

	//获取md5
	public static String getMD5(String content) throws mException
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new mException("取MD5出错了喵~", "取MD5错误，编码前文本:“" + content + "”\n错误信息:" + e.toString() + "\n" + e.getMessage());
		}
		digest.update(content.getBytes());

		StringBuilder builder = new StringBuilder();
		for (byte b : digest.digest()) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString().toLowerCase();
	}

	//字符串转时间戳
	/*public static String strTimeToUnix(String time)
	 {
	 String timeStamp = null;
	 //日期格式，yyyy-MM-dd HH:mm:ss
	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 Date d;
	 try {
	 d = sdf.parse(time);
	 long l = d.getTime();
	 timeStamp = String.valueOf(l);
	 } catch (ParseException e) {
	 e.printStackTrace();
	 }
	 return timeStamp;
	 }*/

	// 将时间戳转为字符串
	public static String unixToStrTime(String cc_time)
	{
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	public static String streamToString(InputStream is)throws IOException
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

