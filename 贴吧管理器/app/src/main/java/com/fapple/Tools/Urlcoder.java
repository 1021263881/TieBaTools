package com.fapple.Tools;

import java.io.*;
import java.net.*;

public class Urlcoder
{
    //Url编码
	public static String UrlEncodeUtf_8(String str) throws mException
	{
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new mException("Url编码出错了喵~", "UrlEncode出错，编码前文本:“" + str + "”错误信息:" + e.toString());
		}
	}

	//Url解码
	public static String UrlDecodeUtf_8(String str)throws mException
	{
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new mException("Url解码出错了喵~", "UrlDecode出错，编码前文本:“" + str + "”错误信息:" + e.toString());
		}
	}

}
