package com.fapple.Tools;

import org.json.*;
import java.util.*;

public class TBAPI
{
	private HttpService httpService = new HttpService();
	private static final String TBS_URL = "http://tieba.baidu.com/dc/common/tbs";
	private static final String PAGE_URL = "http://c.tieba.baidu.com/c/f/pb/page";

	public static String getStamp()
	{
		return "wappc_" + Tool.newNumber(5) + Tool.newNumber(4) + Tool.newNumber(4) + "_" + Tool.newNumber(3);
	}

	public static String sign(String str)throws mException
	{
		return Tool.getMD5(Urlcoder.UrlDecodeUtf_8(str.replace("&", "")) + "tiebaclient!!!").toUpperCase();
	}

	public String gettbs() throws mException
	{

		String html = httpService.get(TBS_URL);
		String tbs = "";

		try {
			JSONObject ohtml = new JSONObject(html);
			tbs = ohtml.optString("tbs");
			return tbs;
		} catch (JSONException e) {
			throw new mException("", e.toString());
		}
	}

	//等待维护
	private JSONArray gettz(String cookie, String tiebaname, int pn, int sorttype) throws mException
	{
		String 贴吧名_URL_UTF8 = Urlcoder.UrlEncodeUtf_8(tiebaname);

		String url = "http://c.tieba.baidu.com/c/f/frs/page";
		String data = cookie;
		data += "&_client_id=" + getStamp();
		data += "&_client_type=2&_client_version=7.8.1&kw=";
		data += 贴吧名_URL_UTF8;
		data += "&pn=" + pn;
		data += "&rn=50";
		data += "&sort_type=" + sorttype;//回复0发帖1关注2智障3
		data += "&sign=" + sign(data);

		String thing = httpService.post(url, data);
		JSONObject list;
		try {
			list = new JSONObject(thing);
		} catch (JSONException e) {
			throw new mException("JSON解析好像遇到问题了", "主题列表JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
		}
		//return list.optJSONArray("thread_list");
		return null;
	}

	//tid和pid获取帖子
	public ArrayList<String> getPage(String cookie, String tid, String pid) throws mException
	{
		return getPage("", tid, pid, -1, false);
	}

	public ArrayList<String> getPage(String cookie, String tid, int pn) throws mException
	{
		return getPage(cookie, tid, "-1", pn, false);
	}

	public ArrayList<String> getPage(String cookie, String tid, String pid, int pn, boolean daoxu) throws mException
	{
		String data = cookie;
		data += "&_client_id=" + getStamp();
		data += "&_client_type=2&_client_version=8.8.8.3";
		data += "&kz=" + tid;
		if (!pid.equals("-1")) {
			data += "&mark=1&pid=" + pid;
		} else if (daoxu == true) {
			data += "&last=1&r=1";
		} else {
			data += "&pn=" + pn;
		}
		data += "&sign=" + sign(data);

		String thing = httpService.post(PAGE_URL, data);

		JSONObject page;
		try {
			page = new JSONObject(thing);
		} catch (JSONException e) {
			throw new mException("JSON解析好像遇到问题了", "获取帖子JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
		}

		ArrayList<String> list = new ArrayList<String>();

		//获取帖子所在贴吧的ID
		String frs = page.optJSONObject("forum").optString("id", "");

		//获取该贴最大页码，获取失败返回-1
		int pagemax = Integer.valueOf(page.optJSONObject("page").optString("total_page", "-1"));

		int pagenow;

		if (daoxu == false) {
			pagenow = Integer.valueOf(page.optJSONObject("page").optString("current_page", "-1"));
		} else {
			pagenow = 1;
		}
		if (pagenow > pagemax) {
			pagenow = pagemax;
		}

		list.add(frs);
		list.add(tid);
		list.add(String.valueOf(pagenow));
		list.add(String.valueOf(pagemax));
		list.add(page.optJSONArray("post_list").toString());
		list.add(page.optJSONArray("user_list").toString());

		return list;
	}

	//pn为-1获取全部
	public ArrayList<String> getlzl(String cookie, String tid, String pid, int pn) throws mException
	{
		String url = "http://c.tieba.baidu.com/c/f/pb/floor";
		String data = cookie;
		data += "&_client_id=" + getStamp();
		data += "&_client_type=2&_client_version=8.7.8.6";
		data += "&kz=" + tid;
		data += "&pid=" + pid;
		//判断页码是否为-1，-1时获取所有，否则只获取该页
		data += "&pn=" + ((pn == -1) ?1: pn);
		data += "&sign=" + sign(data);

		String thing = httpService.post(url, data);

		JSONObject lzl;
		try {
			lzl = new JSONObject(thing);
		} catch (JSONException e) {
			throw new mException("JSON解析好像遇到问题了", "获取楼中楼JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
		}

		thing = lzl.optJSONArray("subpost_list").toString();
		if (thing.length() <= 1) {
			throw new mException("楼中楼获取失败", "tid=" + tid + "pid=" + pid + "pn=" + pn);
		}

		ArrayList<String> list = new ArrayList<String>();
		list.add(tid);
		list.add(pid);

		//获取当前页码
		int pnow = Integer.valueOf(lzl.optJSONObject("page").optString("current_page"));

		list.add(String.valueOf(pnow));

		//获取总页码
		int pmax = Integer.valueOf(lzl.optJSONObject("page").optString("total_page"));

		list.add(String.valueOf(pmax));

		//添加所获取的内容
		list.add(thing);

		//判断是否为遍历模式
		if (pn == -1 && pnow < pmax) {
			for (int i = pnow; i <= pmax; i++) {
				list.add(getlzl(cookie, tid, pid, i).get(4));
			}
		}
		return list;
	}

	public ArrayList<Integer> getMaxPage(String tid) throws mException
	{
		String url = "http://c.tieba.baidu.com/c/f/pb/page";
		String data = "";
		data += "&_client_id=" + getStamp();
		data += "&_client_type=2&_client_version=8.8.8.3";
		data += "&kz=" + tid;
		data += "&last=1&r=1";
		data += "&sign=" + sign(data);

		String thing = httpService.post(url, data);
		ArrayList<Integer> all = new ArrayList<Integer>();

		JSONObject page;
		try {
			page = new JSONObject(thing);
			String maxpage = page.optJSONObject("page").optString("total_page");
			String maxfloor = page.optJSONArray("post_list").optJSONObject(0).optString("floor");
			all.add(Integer.valueOf(maxpage));
			all.add(Integer.valueOf(maxfloor));
		} catch (JSONException e) {
			throw new mException("JSON解析好像遇到问题了", "更新最大页码JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
		}
		return all;
	}

	public static String anaContentToHtml(JSONObject content) throws mException
	{
		String type = content.optString("type", "0");
		String thing = "";
		if (type.equals("0")) {
			//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
			thing = content.optString("text", "[文本获取失败]");
		} else if (type.equals("1")) {
			//{"type":1,"link":"网址","text":"网址"}
			//thing += "#链接=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
			thing = "<a href=\"" + content.optString("link", "") + "\">" + content.optString("text", "") + "</a>";
			//控制台_输出(0, "#链接# " + (String)内容源码["post_list"][内容计数]["content"][文本计数]["link"]);
		} else if (type.equals("2")) {
			//{"type": "2","text": "image_emoticon33","c": "喷"}
			//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
			thing = "[贴吧表情:" + content.optString("c") + "]";
		} else if (type.equals("3")) {
			//{"type":3,"src":"链接","bsize":"189,199","size":"49634"}
			//Console.WriteLine((String)内容源码["post_list"][内容计数]["content"].ToString());
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			thing = "<img src=\"" + content.optString("origin_src") + "\" alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
		} else if (type.equals("4")) {
			//thing += "#艾特=" + 内容源码["post_list"][内容计数]["content"][文本计数]["text"].ToString().Replace("@", "") + "#";
			thing += "<a href=\"http://tieba.baidu.com/home/main/?un=" + Urlcoder.UrlEncodeUtf_8(content.optString("text", "").replace("@", "")) + "\">" + content.optString("text", "") + "</a>";
		} else if (type.equals("5")) {
			//{"type":5,"e_type":15,"width":"480","height":"480","bsize":"480,480","during_time":"2","origin_size":"168046","text":"http:\/\/tieba.baidu.com\/mo\/q\/movideo\/page?thumbnail=d109b3de9c82d158d3fcee1d880a19d8bc3e421b&video=10363_ed294eae88371575b3dbcf9f1990f68d","link":"http:\/\/tb-video.bdstatic.com\/tieba-smallvideo\/10363_ed294eae88371575b3dbcf9f1990f68d.mp4","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/d109b3de9c82d158d3fcee1d880a19d8bc3e421b.jpg","is_native_app":0,"native_app":[]}
			//thing += "#视频#";
		} else if (type.equals("7")) {
			//{"type":"7","text":"\n"}
			thing = "<br>";
		} else if (type.equals("9")) {
			//{"type":"9","text":"6666666","phonetype":"2"}
			//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
			thing = content.optString("text", "");
		} else if (type.equals("10")) {
			//{"type":"10","during_time":"15000","voice_md5":"e25ef2db5076f825e229c6cdb1613f38_1064475243"}
			//thing += "#语音=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["voice_md5"] + "," + (String)内容源码["post_list"][内容计数]["content"][文本计数]["during_time"] + "#";
		} else if (type.equals("11")) {
			//{"type":"11","c":"白发魔女传之明月天国_女屌丝","static":"png静态图链接","dynamic":"gif动态图链接","height":"160","width":"160","icon":"http://tb2.bdstatic.com/tb/editor/images/faceshop/1058_baifa/panel.png","packet_name":"白发魔女传之明月天国"}
			//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
		} else if (type.equals("16")) {
			//{"type":"16","bsize":"560,560","graffiti_info":{"url":"jpg网页端原图","gid":"123456"},"cdn_src":"客户端缩略图","big_cdn_src":"客户端大图"}
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["graffiti_info"]["url"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			thing = "<img src=\"" + content.optJSONObject("graffiti_info").optString("url") + "alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
		} else if (type.equals("17")) {
			//{"type":"17","high_together":{"album_id":"478448408116821906","album_name":"关于众筹西游记歌曲演唱会活动","start_time":"0","end_time":"0","location":"","num_join":"0","pic_urls":[]}}
			//thing += "#活动=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["album_name"] + "#";
		} else if (type.equals("18")) {
			//{"type":"18","text":"#白狐狸不改国庆礼包就滚出dnf#","link":"http://tieba.baidu.com/mo/q/hotMessage?topic_id=0&topic_name=白狐狸不改国庆礼包就滚出dnf"}
			//thing += "#热议=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
		} else if (type.equals("20")) {
			//{"type":"20","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","bsize":"375,348","meme_info":{"pck_id":"0","pic_id":"47098639564","width":"375","height":"348","pic_url":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","thumbnail":"http:\/\/imgsrc.baidu.com\/forum\/abpic\/item\/4c086e061d950a7bce3c370300d162d9f3d3c9e8.jpg","detail_link":"http:\/\/tieba.baidu.com\/n\/interact\/emoticon\/0\/47098639564?frompb=1"}}
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			thing = "<img src=\"" + content.optString("src") + "\" alt=\"图片加载失败，请反馈至1021263881@qq.com\" >";
		}
		return thing;
	}
}
