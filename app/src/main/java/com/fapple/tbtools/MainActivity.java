package com.fapple.tbtools;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.fapple.tools.*;
import org.json.*;

import android.content.ClipboardManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import java.util.*;
import android.widget.GridLayout.*;

public class MainActivity extends AppCompatActivity 
{
	private int width;//屏幕宽(像素)
	private int height;//屏幕高(像素)
	private float dpp = 1;//屏幕密度（0.75 / 1.0 / 1.5）
	private int dpi;//屏幕密度DPI（120 / 160 / 240）

	private final static String DATABASENAME = "tbdatabase";
	private final static int LOGINREQUEST = 111;

	private TBAPI api = new TBAPI();
	private TBScan tbScan = null;

	private int networkType = -1;
	private boolean isLogined = false;
	private int version = 0;//客户端版本号
	private boolean isFront = false;//是否显示在前台
	private String cookie = "";
	private String BDUSS = "";
	private String uid = "";
	private String lastTime = "";//上一次打开APP的时间

	private JSONArray rule = null;
	private JSONObject scanRule = null;
	private boolean scanThread = true;
	private boolean scanLzl = true;
	public static int scanPage = 1;
	private boolean needCheck = false;

	//推送通知
	NotificationCompat.Builder builder = null;
	Notification notification = null;
	NotificationManager notificationManager = null;

	//获取剪贴板
	private ClipboardManager cm = null;

	private Toolbar toolbar;
	private DrawerLayout draw;
	private ActionBarDrawerToggle drawlay;

	//标题栏上色
	private SpannableString titleSp = new SpannableString("");
	private SpannableString subtitleSp = new SpannableString("");
	private ForegroundColorSpan titleColorSpan = null;

	//侧边栏
	private NavigationView navigation = null;

	//底板
	private FrameLayout mainContentLayout = null;
	private FloatingActionButton fab = null;
	private int currentMenuItemID = -1;

	//首页界面contentLayout
	private View homeView = null;
	private CardView switchCard = null;

	//规则界面
	private View ruleView = null;
	private LinearLayout ruleContentView = null;
	private OnClickListener ruleClickListen = null;

	//LogView
	private View logView = null;
	private TextView log = null;
	private OnClickListener logClickListen = null;

	private DataBase db = new DataBase(this, DATABASENAME);

	BroadcastReceiver networkChangeReceiver = new NetworkChangeReceiver();

	Timer timer = new Timer();
	TimerTask task = new TimerTask(){
		@Override
		public void run()
		{
			addLog(1, "内存占用" + Tool.getMemory() + "MB");
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_cb);

		float[] dp = Tool.getdpi(this);
		if (dp.length == 4) {
			width = (int) dp[0];
			height = (int) dp[1];
			dpp = dp[2];
			dpi = (int) dp[3];
		} else {
			Toast.makeText(this, "屏幕尺寸获取失败", 0).show();
		}

		//获取剪贴板
		cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		draw = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawlay = new ActionBarDrawerToggle(this, draw, toolbar, R.string.open, R.string.close);
		drawlay.syncState();

		draw.setDrawerListener(drawlay);

		builder = new NotificationCompat.Builder(this);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		//主页内容的Layout
		mainContentLayout = (FrameLayout) findViewById(R.id.maincontentLayout);
		mainContentLayout.addView(getHomeView());

		currentMenuItemID = R.id.menu_home;

		fab = (FloatingActionButton)findViewById(R.id.fab);
		fab.hide();
		ruleClickListen = new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				Snackbar.make(mainContentLayout, "点击", 0).show();
			}
		};
		logClickListen = new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				db.clearLog();
				log.setText("");
			}
		};

		db.getWritableDatabase();

		initSetting();
		initToolBar();
		initNavigationView();
		initInternetLitsen();

		sendNotification(102, "APP已启动", "上次关闭时间:" + Tool.currentToStrTime(Long.valueOf(lastTime)));

		//tbScan = new TBScan(api, rule){};

		addLog(0, "APP启动");
		//db.clearLog();
		//BDUSS = db.getBDUSS("580978555");
		try {
			//api.sendLoginMess(BDUSS);
			//showAlertDialog(db.getLog());
			//showAlertDialog(db.getBDUSS());
			//showAlertDialog(api.deltz(BDUSS, 0, "我的世界", "5643840652"));
			//showAlertDialog("Res=" + api.send(BDUSS));
			if (false) {
				uid = Zhengze.ZZ(api.sendLoginMess(""), "id\":\"\\d*?\"", false, 0);
				uid = uid.substring(5, uid.length() - 1);
				//showAlertDialog(uid);
				//showAlertDialog(Tool.decodeUnicode("OJBK\u8f3c"));
				//showAlertDialog(api.tbautosign(BDUSS, uid));
			}
		} catch (mException e) {
			showAlertDialog(e.toString());
		}
		timer.schedule(task, 5000, 10000);
	}

	private void initInternetLitsen()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		filter.addAction("android.net.wifi.STATE_CHANGE");
		//filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkChangeReceiver, filter);
	}

	private void initSetting()
	{
		try {
			version = APKVersion.getVersionCode(this);
		} catch (mException e) {

		}
		String load = "";
		try {
			load = Tool.load(this, "setting.json");
		} catch (mException e) {
			showWelcomeMess();
			rule = new JSONArray();
			scanRule = new JSONObject();
			lastTime = "0";
			try {
				scanRule.put("scanThread", scanThread);
				scanRule.put("scanLzl", scanLzl);
				scanRule.put("scanPage", scanPage);
				scanRule.put("needCheck", needCheck);
				rule.put(scanRule);
			} catch (JSONException e2) {

			}
			return ;
		}
		try {
			JSONObject setting = new JSONObject(load);
			if (version != setting.optInt("version", version)) {
				showUpdateMess();
			}
			lastTime = setting.optString("lastTime");
			rule = setting.optJSONArray("rule");
			scanRule = rule.optJSONObject(0);
			scanThread = scanRule.optBoolean("scanThread", scanThread);
			scanLzl = scanRule.optBoolean("scanLzl", scanLzl);
			scanPage = scanRule.optInt("scanPage", scanPage);
			needCheck = scanRule.optBoolean("needCheck", needCheck);
		} catch (JSONException e) {
			showAlertDialog("获取设置时JSON解析失败，str=" + load + "\nMess=" + e.toString());
		}
	}
	private void saveSetting()
	{
		JSONObject setting = new JSONObject();
		try {
			lastTime = String.valueOf(System.currentTimeMillis());
			setting.put("version", version);
			setting.put("rule", rule);
			setting.put("lastTime", lastTime);
			Tool.save(this, "setting.json", setting.toString());
		} catch (JSONException e) {

		} catch (mException e) {

		}
	}

	private void initToolBar()
	{
		//获取toolbar
		toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

		setSupportActionBar(toolbar);
		titleColorSpan = new ForegroundColorSpan(getColor(R.color.mdwhite));
		setTitle("222");
		setSubtitle("33");

		//从Resources()获取menu图标，修改大小
		Drawable da = getResources().getDrawable(R.drawable.ic_menu);
		da = zoomDrawable(da, (int)(24 * dpp), (int)(24 * dpp));

		//设置导航按钮图片
		toolbar.setNavigationIcon(R.drawable.ic_menu);
		//abc_ic_menu_overflow_material三个点
		//abc_ic_menu_paste_mtrl_am_alpha粘贴

		//给左上角图标的左边加上一个返回的图标
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setNavigationOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Toast.makeText(MainActivity.this, "点击", 0).show();
					draw.openDrawer(Gravity.START);
				}
			});
	}

	public void setTitle(String title)
	{
		titleSp = titleSp.valueOf(title);
		titleSp.setSpan(titleColorSpan, 0, titleSp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(titleSp);
	}
	public void setSubtitle(String title)
	{
		subtitleSp = subtitleSp.valueOf(title);
		subtitleSp.setSpan(titleColorSpan, 0, subtitleSp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setSubtitle(subtitleSp);
	}
	private void initNavigationView()
	{
		navigation = (NavigationView) findViewById(R.id.navigationview);

		// 设置导航菜单宽度  
        //ViewGroup.LayoutParams params = navigation.getLayoutParams();  
        //params.width = width * 1 / 3;
        //navigation.setLayoutParams(params);  

		//头部绑定View
		navigation.inflateHeaderView(R.layout.navigation_head);

		//头部点击事件
		navigation.getHeaderView(0).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					login();
				}
			});

		//菜单绑定
		navigation.inflateMenu(R.menu.navigation_menu);

		//菜单点击事件
		navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

				@Override
				public boolean onNavigationItemSelected(MenuItem p1)
				{
					if (!p1.isChecked()) {
						navigation.getMenu().findItem(currentMenuItemID).setChecked(false);
						mainContentLayout.removeAllViews();
						fab.hide();
						currentMenuItemID = p1.getItemId();
						switch (currentMenuItemID) {
							case R.id.menu_home:
								fab.setOnClickListener(null);
								mainContentLayout.addView(getHomeView());
								break;
							case R.id.menu_rule:
								fab.setOnClickListener(ruleClickListen);
								fab.setImageResource(R.drawable.ic_add);
								mainContentLayout.addView(getRuleView());
								fab.show();
								break;
							case R.id.menu_log:
								fab.setOnClickListener(logClickListen);
								fab.setImageResource(R.drawable.ic_delete);
								mainContentLayout.addView(getLogView());
								fab.show();
								break;
							case R.id.menu_setting:
								break;
						}
						p1.setChecked(true);
					}
					draw.closeDrawers();
					return true;
				}
			});

		//将menu第一个Item设为选中
		navigation.getMenu().getItem(0).setChecked(true);
	}








	private void addNewRule(String name, boolean check, JSONArray rule)
	{
		try {
			rule.put((new JSONObject().put("name", name).put("check", check).put("rule" , rule).put("matchTimes", new Integer(0))));
		} catch (JSONException e) {
			showAlertDialog("add-Exception=" + e.toString());
		}
	}

	private View getHomeView()
	{
		if (homeView == null) {
			homeView = View.inflate(this, R.layout.homeview, null);
			switchCard = (CardView) homeView.findViewById(R.id.switchCard);
			switchCard.setRadius(8);//设置图片圆角的半径大小
			switchCard.setCardElevation(8);//设置阴影部分大小
			switchCard.setContentPadding(10, 10, 10, 10);//设置图片距离阴影大小
		}
		return homeView;
	}
	private View getRuleView()
	{
		if (ruleView == null) {
			ruleView = View.inflate(this, R.layout.ruleview, null);
			ruleContentView = (LinearLayout) ruleView.findViewById(R.id.ruleviewContent);
		}
		if (rule.length() - 1 != ruleContentView.getChildCount()) {
			ruleContentView.removeAllViews();
			
		}
		return ruleView;
	}
	private View getLogView()
	{
		if (logView == null) {
			logView = View.inflate(this, R.layout.logview, null);
			log = (TextView) logView.findViewById(R.id.logviewLog);
		}
		log.setText(db.getLog());
		return logView;
	}
	private void showWelcomeMess()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(getString(R.string.welcomemess));
		dialog.setCancelable(false);
		dialog.setNegativeButton("确认", null);
		/*dialog.setNeutralButton("退出", new DialogInterface.OnClickListener(){

		 @Override
		 public void onClick(DialogInterface p1, int p2)
		 {
		 finish();
		 }
		 });*/
		dialog.show();
	}
	public void sendNotification(int id, String title, String content)
	{
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(title);
		builder.setContentText(content);
		//.setWhen(System.currentTimeMillis());         //发送时间  
		//.setDefaults(Notification.DEFAULT_ALL);      //设置默认的提示音，振动方式，灯光  
		//.setAutoCancel(true);                         //打开程序后图标消失  
		//.setContentIntent(pendingIntent)              //设置点击响应  
		//显示所有内容  
		//.setStyle(new NotificationCompat.BigTextStyle().bigText("Hello world Hello world Hello world Hello world " +  
		//"Hello world Hello world Hello world "))  
		//显示图片
		//.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)))  

		notification = builder.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		notificationManager.notify(id, notification);
	}

	private void changeNetWork(int networkType)
	{
		if (this.networkType == networkType) {
			return ;
		}
		if (isFront) {
			if (this.networkType == -1) {
				if (networkType == 1) {
					Toast.makeText(this, "检测到WiFi环境", 0).show();
				} else if (networkType == 2) {
					Toast.makeText(this, "检测到流量环境", 0).show();
				} else if (networkType == 0) {
					Toast.makeText(this, "未检测到网络连接", 0).show();
				} 
			} else { 
				if (networkType == 1) {
					Snackbar.make(mainContentLayout, "已切换至WiFi环境", 0).show();
				} else if (networkType == 2) {
					Snackbar.make(mainContentLayout, "已切换至流量环境", 0).show();
				} else if (networkType == 0) {
					Snackbar.make(mainContentLayout, "无网络连接", 0).show();
				} else {
					Toast.makeText(this, "ERROR:未获取到网络环境状态,请检查权限设置", 0).show();
				}
			}
		}
		this.networkType = networkType;
	}

	private Drawable zoomDrawable(Drawable draw, int w, int h)
	{
		return Tool.zoomDrawable(draw, (int)(w * dpp), (int)(h * dpp));
	}

	//贴吧登录
	private void login()
	{
		Intent login = new Intent(this, BaiDuLogin.class);
		startActivityForResult(login, LOGINREQUEST);
	}

	private boolean checkBDUSS(String BDUSS, boolean isNewLogin)
	{
		showAlertDialog(BDUSS);
		try {
			String login = api.sendLoginMess(BDUSS);
			uid = Zhengze.ZZ(login, "id\":\"\\d*?\"", false, 0);
			String name = Zhengze.ZZ(login, "name\":\".*?\"", false, 0);
			if (uid.length() > 5) {
				uid = uid.substring(5, uid.length() - 1);
				name = name.substring(7, name.length() - 1);
				if (isNewLogin) {
					api.autoSign(BDUSS, uid);
					db.addBDUSS(uid, name, BDUSS, System.currentTimeMillis());
				}
			} else {
				return false;
			}
		} catch (mException e) {
			return false;
		}
		return false;
	}

	private void showUpdateMess()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(getString(R.string.updatemess));
		dialog.setCancelable(false);
		dialog.setNegativeButton("ojbk", null);
		dialog.show();
	}

	private void addLog(int type, String content)
	{
		if (db != null) {
			db.addLog(type, content);
		}
	}
	public void showAlertDialog(final String content)
	{
		//addLog(1, "AlertDialog:" + content);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(content);
		dialog.setCancelable(false);
		dialog.setNegativeButton("ojbk", null);
		dialog.setNeutralButton("复制", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Tool.copyToClipBoard(cm, content);
				}
			});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == LOGINREQUEST) {
			if (resultCode == 1) {
				//cookie获取成功
				cookie = data.getStringExtra("cookie");
				if (cookie.length() > 10) {
					BDUSS = Zhengze.ZZ(cookie, "BDUSS=.*?;", false, 0);
					BDUSS = BDUSS.substring(BDUSS.indexOf("=") + 1, BDUSS.length() - 1);
					checkBDUSS(BDUSS, true);
				} else {

				}
			} else if (resultCode == 0) {
				//cookie获取失败
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
    public void onBackPressed()
	{
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
		Toast.makeText(this, "OJBK", 1).show();
    }

	@Override
	protected void onResume()
	{
		addLog(0, "进入前台");
		isFront = true;
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		addLog(0, "进入后台");
		isFront = false;
		saveSetting();
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		addLog(0, "APP被关闭");
		db.setTransactionSuccessful();
		//db.closeDatabase();
		unregisterReceiver(networkChangeReceiver);
		saveSetting();
		super.onDestroy();
	}

	class NetworkChangeReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//Toast.makeText(context,"network changes",Toast.LENGTH_SHORT).show();
			ConnectivityManager connectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  //得到系统服务类
			NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
			NetworkInfo mobileInfo = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiinfo = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			//if (networkInfo != null && networkInfo.isAvailable()) {
			if (wifiinfo.isConnected()) {
				changeNetWork(1);
			} else if (mobileInfo.isConnected()) {
				changeNetWork(2);
			} else {
				changeNetWork(0);
			}
			//}
		}
	}
}
