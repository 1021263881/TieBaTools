package com.fapple.tbtools;
import android.app.*;
import android.content.*;
import android.os.*;
import com.fapple.tbtools.*;

public class LaunchActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);
		launchwait(0);
	}
	

	private void launch()
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				{
					//耗时任务，比如加载网络数据
					runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								//跳转至 MainActivity
								Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
								startActivity(intent);
								//结束当前的 Activity
								LaunchActivity.this.finish();
							}
						});
				}
			}).start();
	}

	private void launchwait(int time)
	{
		//Integer time = 2000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
				@Override
				public void run()
				{
					startActivity(new Intent(LaunchActivity.this, MainActivity.class));
					LaunchActivity.this.finish();
				}
			}, time);
    }
}
