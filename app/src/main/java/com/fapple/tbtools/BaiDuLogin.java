package com.fapple.tbtools;
import android.app.*;
import android.content.*;
import android.os.*;
import android.webkit.*;
import android.widget.*;
import com.fapple.tools.*;

public class BaiDuLogin extends Activity
{
	private WebView web;
	private final static String BaiDuLoginUrl = "https://wappass.baidu.com/passport/login?u=//tieba.baidu.com";
	private final static String duibi = "u=//tieba.baidu.com";
	private String cookie = "";
	private Intent result = new Intent();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		web = (WebView) findViewById(R.id.loginWeb);

		WebSettings webset =  web.getSettings();

		webset.setUseWideViewPort(true);//设置此属性，可任意比例缩放
		webset.setLoadWithOverviewMode(true);

		webset.setJavaScriptEnabled(true);
        webset.setBuiltInZoomControls(true);
        webset.setSupportZoom(true);

		web.loadUrl(BaiDuLoginUrl);

		web.setWebViewClient(new tbweb());
	}

	@Override
	protected void onPause()
	{
		setResult(0);
		super.onPause();
	}

	public class tbweb extends WebViewClient
	{

		@Override
		public void onPageFinished(WebView view, String url)
		{
			//Toast.makeText(BaiDuLogin.this, url, 0).show();
			if (!Zhengze.ZZ(url, duibi, false, 0).equals(duibi)) {
				CookieManager cookieManager = CookieManager.getInstance();
				String CookieStr = cookieManager.getCookie("http://tieba.baidu.com");
				if (CookieStr != null) {
					cookie = CookieStr;
					result.putExtra("cookie", CookieStr);
					setResult(1, result);
					finish();
				} else {
					Toast.makeText(BaiDuLogin.this, "cookie获取失败", 0).show();
					setResult(0);
					finish();
				}
			}
			super.onPageFinished(view, url);
		}

	}
}
