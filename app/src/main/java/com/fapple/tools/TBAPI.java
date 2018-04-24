package com.fapple.tools;

import android.content.*;
import android.provider.*;
import android.telephony.*;
import java.security.*;
import java.util.*;
import org.json.*;
import android.support.annotation.*;

public class TBAPI
{
	private static final String DEFAULE_IMEI = "000000000000000";

	private static final int DEFAULT_CUID_LENGTH = 50;

	private static final String BASE_URL = "http://c.tieba.baidu.com/";
	private static final String TBS_URL = "http://tieba.baidu.com/dc/common/tbs";
	private static final String FRS_URL = BASE_URL + "c/f/frs/page";
	private static final String PAGE_URL = BASE_URL + "c/f/pb/page";
	private static final String LZL_URL = BASE_URL + "c/f/pb/floor";

	private static final String DEL_THREAD_URL = BASE_URL + "/c/c/bawu/delthread";

	private static final String LOGIN_URL = BASE_URL + "c/s/login";
	private static final String TB_AUTO_SIGN = BASE_URL + "/c/c/forum/msign";

	private HttpService httpService = new HttpService();

	//产生一个新的随机数，传参随意位数
	public static String newNumber(int num)
	{
		Random rnd = new Random();
		String re = "";
		re += (rnd.nextInt(9) + 1);
		for (int i = 1; i < num; i ++) {
			re += String.valueOf(rnd.nextInt(10));
		}
		return re;
	}

	public static String getIMEI(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);  
		String imei = telephonyManager.getDeviceId(); 
		return imei;
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

	public static String newUUID()
	{
		return UUID.randomUUID().toString();
	}

	public static String newcuid(Context context) throws mException
	{
		//getApplicationContext().getContentResolver()
		String android_id = Settings.System.getString(context.getContentResolver(), "android_id");
		android_id = "com.baidu" + android_id;

		return getMD5(android_id).toUpperCase() + "|" + newNumber(15).toString();

		/*StringBuilder stringBuilder = new StringBuilder(DEFAULT_CUID_LENGTH);

		 stringBuilder.append("baidutiebaapp");
		 stringBuilder.append(newUUID());
		 if (stringBuilder.length() > DEFAULT_CUID_LENGTH) {
		 stringBuilder.setLength(DEFAULT_CUID_LENGTH);
		 }
		 char[] toCharArray = stringBuilder.toString().toCharArray();
		 StringBuilder stringBuilder2 = new StringBuilder(toCharArray.length);
		 for (int i = 0; i < toCharArray.length; i++) {
		 if (toCharArray[i] != '\u0000') {
		 stringBuilder2.append(toCharArray[i]);
		 }
		 }
		 return stringBuilder2.toString();*/
	}

	public static String getTimeStamp()
	{
		return Long.toString(System.currentTimeMillis());
	}

	public static String getStamp()
	{
		return "wappc_" + newNumber(5) + newNumber(4) + newNumber(4) + "_" + newNumber(3);
	}

	public static String getSign(String str)throws mException
	{
		return getMD5(Urlcoder.UrlDecodeUtf_8(str.replace("&", "")) + "tiebaclient!!!").toUpperCase();
	}

	public String getTbs(String BDUSS) throws mException
	{
		String html = httpService.get_cookie(TBS_URL, "BDUSS=" + BDUSS);
		String tbs = "";

		try {
			JSONObject ohtml = new JSONObject(html);
			tbs = ohtml.optString("tbs");
			if (tbs.length() != 26) {
				throw new mException("tbs获取失败", "tbs获取失败，tbs=" + tbs);
			}
			return tbs;
		} catch (JSONException e) {
			throw new mException("", e.toString());
		}
	}

	public String send(String BDUSS) throws mException
	{
		String url = BASE_URL;
		String data = "";
		/*
		 url += "c/c/forum/like";
		 data += "BDUSS=" + BDUSS;
		 data += "&_client_type=2";
		 data += "&_client_version=6.6.2";
		 data += "&_client_id=" + getStamp();
		 data += "&kw=minecraftpe";
		 data += "&fid=4409677";
		 data += "&tbs=" + gettbs();
		 */

		//url += "c/u/user/profile";

		//url += "c/u/user/getuserinfo";
		url += "c/f/frs/getmypost";
		data += "BDUSS=" + BDUSS;
		data += "&_client_type=2";
		data += "&_client_version=6.6.6";
		data += "&_client_id=" + getStamp();
		data += "&q_type=1";
		//data += "&uid=580978555";
		//data += "&name=1021263881";
		data += "&from=baidu_appstore";
		data += "&_phone_imei=" + DEFAULE_IMEI;
		data += "&model=GT-I9100";
		data += "&timestamp=" + getTimeStamp();//1521548973986
		data += "&tbs=" + getTbs(BDUSS);
		data += "&sign=" + getSign(data);

		String thing = httpService.post(url, data);
		return thing;
	}

	public String sendLoginMess(String BDUSS) throws mException
	{
		String data = "";
		data += "BDUSS=" + BDUSS;
		data += "&_client_type=2";
		data += "&_client_version=6.6.2";
		//data += "&_phone_imei=000000000000000";
		data += "&bdusstoken=" + BDUSS + "|null";
		//data += "&channel_id=";
		//data += "&channel_uid=";
		//data += "&cuid=" + cuid;
		//data += "&from=tieba";
		//data += "&model=Redmi+Note+4X";
		//data += "&stErrorNums=0";
		data += "&timestamp=" + getTimeStamp();//1521548973986
		data += "&sign=" + getSign(data);

		String thing = httpService.post(LOGIN_URL, data);

		return thing;
	}

	public String autoSign(String BDUSS, String uid) throws mException
	{
		StringBuilder data = new StringBuilder()
			.append("BDUSS=").append(BDUSS)
			.append("&_client_id=").append(getStamp())
			.append("&_client_type=2")
			.append("&_client_version=5.3.1")
			//data += "&_phone_imei=042791438690445";
			//data += "&cuid=DCE2BCBBC5F4307F7457E2463A14F382%7C544096834197240";
			//data += "&forum_ids=1938496%2C1929829";
			//data += "&from=baidu_appstore";
			//data += "&model=GT-I9100";
			.append("&tbs=" + getTbs(BDUSS))
			.append("&timestamp=" + getTimeStamp())
			.append("&user_id=" + uid);
		data.append("&sign=" + getSign(data.toString()));
		String thing = httpService.post(TB_AUTO_SIGN, data.toString());
		thing = Tool.decodeUnicode(thing);
		//thing = Tool.decodeUnicode(Zhengze.ZZ(thing, "usermsg\":\".*?\"", false, 0));
		return thing;
	}

	public String getFid(String tiebaName) throws mException
	{
		StringBuilder data = new StringBuilder()
			.append("BDUSS=")
			.append("&_client_id=").append(getStamp())
			.append("&_client_type=2&_client_version=6.0.1")
			.append("&kw=").append(tiebaName)
			.append("&pn=1")
			.append("&rn=50")
			.append("&sort_type=0");//回复0发帖1关注2智障3
		data.append("&sign=" + getSign(data.toString()));

		try {
			return new JSONObject(httpService.post(FRS_URL, data.toString())).optJSONObject("forum").optString("id");
		} catch (JSONException e) {
			return "";
		}
	}
	//获取主贴列表
	public JSONArray getFrs(String BDUSS, String tiebaName, int pn, int sorttype) throws mException
	{
		tiebaName = Urlcoder.UrlEncodeUtf_8(tiebaName);

		StringBuilder data = new StringBuilder()
			.append("BDUSS=").append(BDUSS)
			.append("&_client_id=").append(getStamp())
			.append("&_client_type=2&_client_version=6.0.1")
			.append("&kw=").append(tiebaName)
			.append("&pn=").append(pn)
			.append("&rn=50")
			.append("&sort_type=").append(sorttype);//回复0发帖1关注2智障3
		data.append("&sign=" + getSign(data.toString()));

		String thing = httpService.post(FRS_URL, data.toString());
		JSONObject list;
		try {
			list = new JSONObject(thing);
		} catch (JSONException e) {
			throw new mException("JSON解析好像遇到问题了", "主题列表JSON解析错误, Str:“" + thing + "”, 错误信息:" + e.toString());
		}
		return list.optJSONArray("thread_list");
	}

	//tid和pid获取帖子
	public ArrayList<String> getPage(String BDUSS, String tid, String pid) throws mException
	{
		return getPage(BDUSS, tid, pid, -1, false);
	}

	public ArrayList<String> getPage(String BDUSS, String tid, int pn) throws mException
	{
		return getPage(BDUSS, tid, "-1", pn, false);
	}

	public ArrayList<String> getPage(String BDUSS, String tid, String pid, int pn, boolean daoxu) throws mException
	{
		StringBuilder data = new StringBuilder()
			.append("BDUSS=").append(BDUSS)
			.append("&_client_id=").append(getStamp())
			.append("&_client_type=2&_client_version=6.0.1")//8883
			.append("&kz=").append(tid);
		if (!pid.equals("-1")) {
			data.append("&mark=1&pid=").append(pid);
		} else if (daoxu == true) {
			data.append("&last=1&r=1");
		} else {
			data.append("&pn=").append(pn);
		}
		data.append("&sign=" + getSign(data.toString()));

		String thing = httpService.post(PAGE_URL, data.toString());

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
		//list.add(page.optJSONArray("user_list").toString());

		return list;
	}

	//pn为-1获取全部
	public ArrayList<String> getlzl(String BDUSS, String tid, String pid, int pn) throws mException
	{
		String data = "";
		data += "BDUSS=" + BDUSS;
		data += "&_client_id=" + getStamp();
		data += "&_client_type=2&_client_version=8.7.8.6";
		data += "&kz=" + tid;
		data += "&pid=" + pid;
		//判断页码是否为-1，-1时获取所有，否则只获取该页
		data += "&pn=" + ((pn == -1) ?1: pn);
		data += "&sign=" + getSign(data);

		String thing = httpService.post(LZL_URL, data);

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

		//判断是否为遍历模式
		if (pn == -1 && pnow < pmax) {
			thing = thing.substring(1, thing.length() - 1);
			for (int i = pnow; i <= pmax; i++) {
				thing += getlzl(BDUSS, tid, pid, i).get(4).substring(1);
				thing = thing.substring(0, thing.length() - 1);
			}
			thing = "[" + thing + "]";
		}

		//添加所获取的内容
		list.add(thing);

		return list;
	}

	public ArrayList<Integer> getMaxPage(String tid) throws mException
	{
		String data = "";
		data += "&_client_id=" + getStamp();
		data += "&_client_type=2&_client_version=8.8.8.3";
		data += "&kz=" + tid;
		data += "&last=1&r=1";
		data += "&sign=" + getSign(data);

		String thing = httpService.post(PAGE_URL, data);
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
	public String delThread(@NonNull String BDUSS, int fid, String tiebaName, String tid) throws mException
	{
		String data = "";
		data += "BDUSS=" + BDUSS;
		data += "&_client_id=" + getStamp();
		data += "&_client_type=102";
		data += "&_client_version=6.6.6";
		//data += "&_phone_imei=695086200637216"
		data += "&appid=bazhu";
		//data += "&cuid=BBC2966C3091587DC54AA61D13BE4481%7C612736002680596";
		data += "&fid=" + fid;
		//data += "&from=1006294d";
		data += "&is_vipdel=0";
		//data += "&model=CHM-TL00H";
		data += "&obj_locate=2";
		//data += "&stoken=e617bfebf4de21af193668fc599c5e28d12f6abf45e665b5dac8f477f23fa776";
		//data += "&subapp_type=admin";
		data += "&tbs=" + getTbs(BDUSS);
		data += "&timestamp=" + getTimeStamp();
		data += "&word=" + Urlcoder.UrlEncodeUtf_8(tiebaName);
		data += "&z=" + tid;
		data += "&sign=" + getSign(data);

		return httpService.post(DEL_THREAD_URL, data);
	}
}
