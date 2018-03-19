package com.fapple.tbtools;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class MainActivity extends Activity 
{
	private Button but;
	private EditText text;
	private final static int loginrequest = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		text = (EditText) findViewById(R.id.mainEditText1);
		but = (Button) findViewById(R.id.mainButton1);

		but.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					login();
				}
			});
    }

	private void login()
	{
		Intent login = new Intent(this, BaiDuLogin.class);
		startActivityForResult(login, loginrequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == loginrequest) {
			if (resultCode == 1) {
				//cookie获取成功
				String cookie = data.getStringExtra("cookie");
				Toast.makeText(MainActivity.this, "cookie=\n" + cookie, 0).show();
				text.setText(cookie);
			} else if (resultCode == 0) {
				//cookie获取失败
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
}
