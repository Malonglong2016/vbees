package cn.vbees.shop.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.vbees.shop.R;
import cn.vbees.shop.utils.SystemBarTintManager;

/**
 * 欢迎界面
 * @ClassName WelcomeActivity
 * @date 2015-4-7
 */
public class WelcomeActivity extends BaseActivity {
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		setTranslucentStatus(true);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setNavigationBarTintEnabled(true);
		tintManager.setTintAlpha(0);
		handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, 4000);
	}

	protected void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	@Override
	public void onBackPressed() {

	}
}
