package com.fapple.tbtools;
import android.util.*;
import com.fapple.tools.*;
import java.util.*;
import org.json.*;
import android.support.annotation.*;

public abstract class TBScan
{
	private TBAPI api = null;

	private String fid ="";
	private String tiebaName = "";

	//所有规则
	private JSONArray ruleList = null;
	private int ruleListLen = -1;

	//单条规则
	private JSONObject ruleObj = null;
	private boolean using = false;//是否启用

	//规则里的条件
	private JSONArray rule = null;//条件的集合
	private int ruleLen = -1;
	private int ruleMatchTimes = -1;//条件匹配成功的次数

	//单条条件
	private JSONObject cond = null;
	private String condType = "";
	private String condContent = "";
	private int condMatchTimes = 1;//单条条件匹配成功的次数

	//用于判断是否已满足条件该被删除
	private boolean del = false;

	private JSONObject scanRule = null;
	private boolean scanThread = true;//判断是否扫描帖子和楼中楼
	private boolean scanLzl = true;

	private static final String emptyStr = "";

	public static final String ruleTitle = "1";
	public static final String ruleUid = "20";
	public static final String ruleUserName = "21";
	public static final String ruleUserNickName = "22";
	public static final String ruleUserLevelL = "23+";//用户等级≥条件等级
	public static final String ruleUserLevelS = "23-";//用户等级≤条件等级
	public static final String ruleContent = "3";
	public static final String ruleFloorL = "4+";//用户等级≥条件等级
	public static final String ruleFloorS = "4-";//用户等级≥条件等级
	public static final String ruleTimeL = "5+";//用户等级≥条件等级
	public static final String ruleTimeS = "5-";//用户等级≥条件等级
	public static final String ruleMatchTimesL = "6+";//用户等级≥条件等级
	public static final String ruleMatchTimesS = "6-";//用户等级≥条件等级
	public static final String ruleRunTimeL = "7+";//用户等级≥条件等级
	public static final String ruleRunTimeS = "7-";//用户等级≥条件等级

	private static final String type_content = "0";
	private static final String type_url = "1";
	private static final String type_biaoqing = "2";
	private static final String type_at = "4";

	private JSONObject zhutie = null;
	private JSONObject floor = null;
	private JSONObject lzl = null;
	private String tid = "";
	private String pid = "";
	private String spid = "";
	private String title = "";
	private String content = "";
	private String floornum = "";
	private String lzlnum = "";

	private JSONObject author = null;
	private String auid = "";
	private String aname = "";
	private String anickname = "";
	private String ahead = "";
	private String alevel = "";

	private String ctime = "";

	private JSONObject lastReplayer = null;
	private String luid = "";
	private String lname = "";
	private String lnickname = "";

	private String ltime = "";

	public abstract void scan();
	
	public abstract void finish();
	
	public abstract void deleteThread(int ruleIndex, String ruleName, boolean needCheck, String fid, String tiebaName, String tid);

	public abstract void deleteFloor(int ruleIndex, String ruleName, boolean needCheck);

	public abstract void deleteLzl(int ruleIndex, String ruleName, boolean needCheck);

	public abstract void addLog(int type, String content);

	private void addLog(String content)
	{
		addLog(-1, content);
	}

	public TBScan(TBAPI api, @NonNull JSONArray ruleList)
	{
		this.api = api;
		this.ruleList = ruleList;
		ruleListLen = ruleList.length();
		scanRule = ruleList.optJSONObject(0);
		scanThread = scanRule.optBoolean("scanThread", scanThread);
		scanLzl = scanRule.optBoolean("scanLzl", scanLzl);
	}

	public void setRule(@NonNull JSONArray ruleList)
	{
		if (ruleList != null && ruleList.length() > 0) {
			this.ruleList = ruleList;
			ruleListLen = ruleList.length();
		}
	}

	public boolean setScanTieba(String tiebaName) throws mException
	{
		fid = api.getFid(tiebaName);
		if (fid == null || fid == "") {
			fid = "";
			return false;
		}
		this.tiebaName = tiebaName;
		return true;
	}
	public void scan(String Frs) throws mException
	{
		anaFrs(fid, tiebaName, api.getFrs("", tiebaName, 1, 0));
	}

	//扫描forum时判断
	public void checkThread(String fid, String tiebaName, String tid, String title, String content, String auid, String aname, String anickname, String ahead, String ctime, String luid, String lname, String lnickname, String ltime) throws mException
	{
		del = false;
		for (int a = 0; a < ruleListLen && del == false; a ++) {
			ruleObj = ruleList.optJSONObject(a);
			if (ruleObj == null) {
				continue;
			}
			rule = null;
			ruleLen = -1;
			using = false;
			ruleMatchTimes = 0;

			using = ruleObj.optBoolean("using", using);
			rule = ruleObj.optJSONArray("rule");
			ruleLen = rule.length();
			if (ruleLen < 1 || using == false) {
				continue;
			}
			addLog("扫描Thread,规则名:" + ruleObj.optString("name"));
			for (int b = 0;b < ruleLen;b ++) {
				cond = null;
				condType = "";
				condContent = "";

				cond = rule.optJSONObject(b);
				condType = cond.optString("type", condType);
				condContent = cond.optString("content", condContent);

				if (condType == "" || condContent == "") {
					continue;
				}

				if (condType.equals(ruleTitle)) {
					if (Zhengze.ZZ(title, condContent, false, 0).length() > 0) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUid)) {
					if (auid.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserName)) {
					if (aname.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserNickName)) {
					if (aname.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserLevelL)) {
					break;
				} else if (condType.equals(ruleUserLevelS)) {
					break;
				} else if (condType.equals(ruleContent)) {
					if (Zhengze.ZZ(content, condContent, false, 0).length() > 0) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleFloorL)) {
					break;
				} else if (condType.equals(ruleFloorS)) {
					break;
				} else if (condType.equals(ruleTimeL)) {
					if (Integer.valueOf(ctime) >= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleTimeS)) {
					if (Integer.valueOf(ctime) <= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				}
			}
			if (ruleMatchTimes == ruleLen) {
				del = true;
				deleteThread(a, ruleObj.optString("name"), ruleObj.optBoolean("needCheck"), fid, tiebaName, tid);
			}
		}
		if (scanThread) {
			try {
				anaPage(tid, new JSONArray(api.getPage("", tid, 1).get(4)));
			} catch (JSONException e) {

			}
		}
	}

	//扫描每一层
	public void checkFloor(String tid, String pid, String auid, String aname, String anickname, String ahead, String alevel, String content, String floor, String time, String lzlnum) throws mException
	{
		del = false;
		for (int a = 0; a < ruleListLen && del == false; a ++) {
			ruleObj = ruleList.optJSONObject(a);
			if (ruleObj == null) {
				continue;
			}
			rule = null;
			ruleLen = -1;
			using = false;
			ruleMatchTimes = 0;

			using = ruleObj.optBoolean("using", using);
			rule = ruleObj.optJSONArray("rule");
			ruleLen = rule.length();
			if (ruleLen < 1 || using == false) {
				continue;
			}
			addLog("扫描Floor,规则名:" + ruleObj.optString("name"));
			for (int b = 0;b < ruleLen;b ++) {
				cond = null;
				condType = "";
				condContent = "";

				cond = rule.optJSONObject(b);
				condType = cond.optString("type", condType);
				condContent = cond.optString("content", condContent);

				if (condType == "" || condContent == "") {
					continue;
				}

				if (condType.equals(ruleTitle)) {
					continue;
				} else if (condType.equals(ruleUid)) {
					if (auid.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserName)) {
					if (aname.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserNickName)) {
					if (aname.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserLevelL)) {
					if (Integer.valueOf(alevel) >= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserLevelS)) {
					if (Integer.valueOf(alevel) <= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleContent)) {
					if (Zhengze.ZZ(content, condContent, false, 0).length() > 0) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleFloorL)) {
					if (Integer.valueOf(floor) >= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleFloorS)) {
					if (Integer.valueOf(floor) <= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleTimeL)) {
					if (Integer.valueOf(time) >= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleTimeS)) {
					if (Integer.valueOf(time) <= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				}
			}
			if (ruleMatchTimes == ruleLen) {
				del = true;
				deleteFloor(a, ruleObj.optString("name"), ruleObj.optBoolean("needCheck"));
			}
		}

		if (scanLzl && Integer.valueOf(lzlnum) > 0) {
			analzl(tid, pid, new JSONArray(api.getlzl("", tid, pid, -1)));
		}
	}

	//扫描楼中楼
	public void checklzl(String tid, String pid, String spid, String auid, String aname, String anickname, String content, String time)
	{
		del = false;
		for (int a = 0; a < ruleListLen && del == false; a ++) {
			ruleObj = ruleList.optJSONObject(a);
			if (ruleObj == null) {
				continue;
			}
			rule = null;
			ruleLen = -1;
			using = false;
			ruleMatchTimes = 0;

			using = ruleObj.optBoolean("using", using);
			rule = ruleObj.optJSONArray("rule");
			ruleLen = rule.length();
			if (ruleLen < 1 || using == false) {
				continue;
			}
			addLog("扫描Lzl,规则名:" + ruleObj.optString("name"));
			for (int b = 0;b < ruleLen;b ++) {
				cond = null;
				condType = "";
				condContent = "";

				cond = rule.optJSONObject(b);
				condType = cond.optString("type", condType);
				condContent = cond.optString("content", condContent);

				if (condType == "" || condContent == "") {
					continue;
				}

				if (condType.equals(ruleTitle)) {
					break;
				} else if (condType.equals(ruleUid)) {
					if (auid.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserName)) {
					if (aname.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserNickName)) {
					if (aname.equals(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserLevelL)) {
					if (Integer.valueOf(alevel) >= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleUserLevelS)) {
					if (Integer.valueOf(alevel) <= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleContent)) {
					if (Zhengze.ZZ(content, condContent, false, 0).length() > 0) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleFloorL)) {
					break;
				} else if (condType.equals(ruleFloorS)) {
					break;
				} else if (condType.equals(ruleTimeL)) {
					if (Integer.valueOf(time) >= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				} else if (condType.equals(ruleTimeS)) {
					if (Integer.valueOf(time) <= Integer.valueOf(condContent)) {
						ruleMatchTimes ++;
					}
				}
			}
			if (ruleMatchTimes == ruleLen) {
				del = true;
				deleteLzl(a, ruleObj.optString("name"), ruleObj.optBoolean("needCheck"));
			}
		}
	}

	public void anaFrs(String fid, String tiebaName, JSONArray list) throws mException
	{
		if (list == null || list.length() < 1) {
			return ;
		}
		clearFrsVar();//清空变量
		int len = list.length();
		for (int i = 0; i < len; i++) {
			try {
				zhutie = list.getJSONObject(i);
			} catch (JSONException e) {
				zhutie = null;
				throw new mException("主题帖解析失败", "");
			}
			if (zhutie == null) {
				throw new mException("主题帖解析失败", "");
			}
			tid = zhutie.optString("tid", "");
			title = zhutie.optString("title", "");
			content = anaContent(zhutie.optJSONArray("abstract"));

			author = zhutie.optJSONObject("author");
			auid = author.optString("id");
			aname = author.optString("name");
			anickname = author.optString("name_show");
			ahead = author.optString("portrait");

			ctime = zhutie.optString("create_time");

			lastReplayer = zhutie.optJSONObject("last_replyer");
			if (lastReplayer != null) {
				luid = lastReplayer.optString("id");
				lname = lastReplayer.optString("name");
				lnickname = lastReplayer.optString("name_show");
			} else {
				luid = "";
				lname = "";
				lnickname = "";
			}

			ltime = zhutie.optString("last_time_int", "");

			checkThread(fid, tiebaName, tid, title, content, auid, aname, anickname, ahead, ctime, luid, lname, lnickname, ltime);

			clearFrsVar();
		}
	}

	public void anaPage(String tid, JSONArray list) throws mException
	{
		if (list == null || list.length() < 1) {
			return ;
		}
		clearPageVar();
		int len = list.length();
		for (int i = 0; i < len; i++) {
			try {
				floor = list.getJSONObject(i);
			} catch (JSONException e) {
				throw new mException("楼层解析失败", "");
			}
			pid = floor.optString("id");
			content = anaContent(floor.optJSONArray("content"));
			floornum = floor.optString("floor");
			ctime = floor.optString("time");
			lzlnum = floor.optString("sub_post_number", "0");

			author = floor.optJSONObject("author");
			auid = author.optString("id");
			aname = author.optString("name");
			anickname = author.optString("name_show");
			ahead = author.optString("portrait");
			alevel = author.optString("level_id");

			checkFloor(tid, pid, auid, aname, anickname, ahead, alevel, content, floornum, ctime, lzlnum);

			clearPageVar();
		}
	}

	public void analzl(String tid, String pid, JSONArray list) throws mException
	{
		if (list == null || list.length() < 1) {
			return ;
		}
		clearlzlVar();
		int len = list.length();
		for (int i = 0; i < len; i++) {
			try {
				lzl = list.getJSONObject(i);
			} catch (JSONException e) {
				throw new mException("楼中楼解析失败", "");
			}
			spid = lzl.optString("id");
			content = anaContent(lzl.optJSONArray("content"));
			ctime = lzl.optString("time");

			author = lzl.optJSONObject("author");
			auid = author.optString("id");
			aname = author.optString("name");
			anickname = author.optString("name_show");
			ahead = author.optString("portrait");
			alevel = author.optString("level_id");

			checklzl(tid, pid, spid, auid, aname, anickname, content, ctime);

			clearlzlVar();
		}
	}

	private String anaContent(JSONArray list)
	{
		String content = "";
		if (list == null || list.length() < 1) {
			return content;
		}
		int len = list.length();
		for (int i = 0; i < len; i++) {
			content += anaContent(list.optJSONObject(i));
		}
		return content;
	}

	private String anaContent(JSONObject content)
	{
		String type = content.optString("type", "0");
		String thing = "";
		if (type.equals(type_content)) {
			//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
			thing = content.optString("text", "[文本获取失败]");
		} else if (type.equals(type_url)) {
			//{"type":1,"link":"网址","text":"网址"}
			//thing += "#链接=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"] + "#";
			thing = "[链接:" + content.optString("text", "") + "]";
			//控制台_输出(0, "#链接# " + (String)内容源码["post_list"][内容计数]["content"][文本计数]["link"]);
		} else if (type.equals(type_biaoqing)) {
			//{"type": "2","text": "image_emoticon33","c": "喷"}
			//thing += "#表情=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["c"] + "#";
			thing = "[表情:" + content.optString("c") + "]";
		} else if (type.equals("3")) {
			//{"type":3,"src":"链接","bsize":"189,199","size":"49634"}
			//Console.WriteLine((String)内容源码["post_list"][内容计数]["content"].ToString());
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=MD5=" + tlib.取网络资源MD5((String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"]) + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			//thing += "#图片=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["origin_src"] + "=size=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["bsize"] + "#";
			thing = "<img src=\"" + content.optString("origin_src") + "\">";
		} else if (type.equals(type_at)) {
			//thing += "#艾特=" + 内容源码["post_list"][内容计数]["content"][文本计数]["text"].ToString().Replace("@", "") + "#";
			thing += "[@" + content.optString("text", "") + "]";
		} else if (type.equals("5")) {
			//{"type":5,"e_type":15,"width":"480","height":"480","bsize":"480,480","during_time":"2","origin_size":"168046","text":"http:\/\/tieba.baidu.com\/mo\/q\/movideo\/page?thumbnail=d109b3de9c82d158d3fcee1d880a19d8bc3e421b&video=10363_ed294eae88371575b3dbcf9f1990f68d","link":"http:\/\/tb-video.bdstatic.com\/tieba-smallvideo\/10363_ed294eae88371575b3dbcf9f1990f68d.mp4","src":"http:\/\/imgsrc.baidu.com\/forum\/pic\/item\/d109b3de9c82d158d3fcee1d880a19d8bc3e421b.jpg","is_native_app":0,"native_app":[]}
			//thing += "#视频#";
		} else if (type.equals("7")) {
			//{"type":"7","text":"\n"}
			thing = "\n";
		} else if (type.equals("9")) {
			//{"type":"9","text":"6666666","phonetype":"2"}
			//thing += (String)内容源码["post_list"][内容计数]["content"][文本计数]["text"];
			thing = content.optString("text", "");
		} else if (type.equals("10")) {
			//{"type":"10","during_time":"15000","voice_md5":"e25ef2db5076f825e229c6cdb1613f38_1064475243"}
			//thing += "#语音=" + (String)内容源码["post_list"][内容计数]["content"][文本计数]["voice_md5"] + "," + (String)内容源码["post_list"][内容计数]["content"][文本计数]["during_time"] + "#";
			thing = "[语音]";
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
			thing = "<img src=\"" + content.optString("src") + "\">";
		}
		return thing;
	}

	private void clearFrsVar()
	{
		tid = emptyStr;
		title = emptyStr;
		content = emptyStr;

		author = null;
		auid = emptyStr;
		aname = emptyStr;
		anickname = emptyStr;
		ahead = emptyStr;

		ctime = emptyStr;

		lastReplayer = null;
		luid = emptyStr;
		lname = emptyStr;
		lnickname = emptyStr;

		ltime = emptyStr;
	}
	private void clearPageVar()
	{
		pid = emptyStr;
		content = emptyStr;
		floornum = emptyStr;
		lzlnum = emptyStr;

		author = null;
		auid = emptyStr;
		aname = emptyStr;
		anickname = emptyStr;
		ahead = emptyStr;
		alevel = emptyStr;

		ctime = emptyStr;
	}
	private void clearlzlVar()
	{
		spid = emptyStr;
		content = emptyStr;
		ctime = emptyStr;

		author = null;
		auid = emptyStr;
		aname = emptyStr;
		anickname = emptyStr;
		ahead = emptyStr;
		alevel = emptyStr;
	}
}
