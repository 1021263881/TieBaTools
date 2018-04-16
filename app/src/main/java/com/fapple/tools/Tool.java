package com.fapple.tools;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.webkit.*;
import com.fapple.tbtools.*;
import java.io.*;
import java.security.*;
import java.text.*;
import java.util.*;

import android.text.ClipboardManager;

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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		return sdf.format(new Date(lcc_time * 1000L));
	}
	// 将时间戳转为字符串
	public static String currentToStrTime(long cc_time)
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return dateformat.format(cc_time);
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

	public static Drawable zoomDrawable(Drawable drawable, int w, int h)
	{  
        int width = drawable.getIntrinsicWidth();  
        int height = drawable.getIntrinsicHeight();  
        Bitmap oldbmp = drawableToBitmap(drawable);  
        Matrix matrix = new Matrix();  
        float scaleWidth = ((float) w / width);  
        float scaleHeight = ((float) h / height);  
        matrix.postScale(scaleWidth, scaleHeight);  
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);  
        return new BitmapDrawable(null, newbmp);
    }  

    private static Bitmap drawableToBitmap(Drawable drawable)
	{  
        int width = drawable.getIntrinsicWidth();  
        int height = drawable.getIntrinsicHeight();  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;  
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, width, height);  
        drawable.draw(canvas);  
        return bitmap;  
    }

	public static float[] getdpi(MainActivity main)
	{
		DisplayMetrics metric = new DisplayMetrics();
        main.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
		float[] list = {width, height, density, densityDpi};
		return list;
	}

	public static float getMemory()
	{
		/*Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
		Debug.getMemoryInfo(memoryInfo);
        // dalvikPrivateClean + nativePrivateClean + otherPrivateClean;
		int totalPrivateClean = memoryInfo.getTotalPrivateClean();
		// dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty;
		int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
		// dalvikPss + nativePss + otherPss;
		int totalPss = memoryInfo.getTotalPss();
		// dalvikSharedClean + nativeSharedClean + otherSharedClean;
		int totalSharedClean = memoryInfo.getTotalSharedClean();
		// dalvikSharedDirty + nativeSharedDirty + otherSharedDirty;
		int totalSharedDirty = memoryInfo.getTotalSharedDirty();
		// dalvikSwappablePss + nativeSwappablePss + otherSwappablePss;
		int totalSwappablePss = memoryInfo.getTotalSwappablePss();

        int total = totalPrivateClean + totalPrivateDirty + totalPss + totalSharedClean + totalSharedDirty + totalSwappablePss;
        return total ;*/
		
		//ActivityManager activityManager = (ActivityManager) main.getSystemService(Context.ACTIVITY_SERVICE);
		//最大分配内存
		//int memory = activityManager.getMemoryClass();
		//System.out.println("memory: "+memory);
		//最大分配内存获取方法2
		//float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
		//当前分配的总内存
		//float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));
		return (float) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));
		//剩余内存
		//float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0/ (1024 * 1024));
		//System.out.println("maxMemory: "+maxMemory);
		//System.out.println("totalMemory: "+totalMemory);
		//System.out.println("freeMemory: "+freeMemory);
		
	}

	public static void save(Activity main, String path, String content) throws mException
	{
		try {
			FileOutputStream output = main.openFileOutput(path, Context.MODE_PRIVATE);
			/**
			 * Context.MODE_PRIVATE = 0
			 * 为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容。
			 * Context.MODE_APPEND = 32768
			 * 该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。　
			 * Context.MODE_WORLD_READABLE = 1
			 * 表示当前文件可以被其他应用读取。
			 * MODE_WORLD_WRITEABLE
			 * 表示当前文件可以被其他应用写入。
			 */

			output.write(content.getBytes());
			output.close();
		} catch (IOException e) {
			throw new mException("", e.toString());
		}
	}

	public static String load(Activity main, String path) throws mException
	{
		String content = "";
		FileInputStream input = null;
		try {
			input = main.openFileInput(path);
		} catch (FileNotFoundException e) {
			throw new mException("没找到该文件", e.toString());
		}
		try {
			content = Tool.streamToString(input);
			input.close();
		} catch (IOException e) {
			throw new mException("", e.toString());
		}
		return content;
	}

	//中文转Unicode  
	public static String encodeUnicode(final String gbString)
	{
		//gbString = "测试"
		char[] utfBytes = gbString.toCharArray();//utfBytes = [测, 试]
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);//转换为16进制整型字符串
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		return unicodeBytes;
	}
	//Unicode转中文
	public static String decodeUnicode(String unicodeStr)
	{
		if (unicodeStr == null) {
			return null;
		}
		StringBuffer retBuf = new StringBuffer();
		int maxLoop = unicodeStr.length();
		for (int i = 0; i < maxLoop; i++) {
			if (unicodeStr.charAt(i) == '\\') {
				if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
					try {
						retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException localNumberFormatException) {
						retBuf.append(unicodeStr.charAt(i));
					}
				else
					retBuf.append(unicodeStr.charAt(i));
			} else {
				retBuf.append(unicodeStr.charAt(i));
			}
		}
		return retBuf.toString();
	}

	/*============================进制转换===========================*/
	public static String BintoOct(String num)
	{
		//二转八
		return Integer.toOctalString(Integer.parseInt(num, 2));
	}
	public static String BintoDec(String num)
	{

		//二转十
		return Integer.valueOf(num, 2).toString();
	}
	public static String BintoHex(String num)
	{
		//二转十六
		return Integer.toHexString(Integer.parseInt(num, 2));
	}
	public static String OcttoBin(String num)
	{
		//八转二
		return Integer.toBinaryString(Integer.valueOf(num, 8));
	}
	public static String OcttoDec(String num)
	{
		//八转十
		return Integer.valueOf(num, 8).toString();
	}
	public static String OcttoHex(String num)
	{
		//八转十六
		return Integer.toHexString(Integer.valueOf(num, 8));
	}
	public static String DectoBin(int num)
	{
		//十转二
		return Integer.toBinaryString(num);
	}
	public static String DectoOct(int num)
	{
		//十转八
		return Integer.toOctalString(num);
	}
	public static String DectoHex(int num)
	{
		//十转十六
		return Integer.toHexString(num);
	}
	public static String HextoBin(String num)
	{
		//十六转二
		return Integer.toBinaryString(Integer.valueOf(num, 16));
	}
	public static String HextoOct(String num){
		//十六转八
		return Integer.toOctalString(Integer.valueOf(num, 16));
	}
	public static String HextoDec(String num)
	{
		//十六转十
		return Integer.valueOf(num, 16).toString();
	}
}

