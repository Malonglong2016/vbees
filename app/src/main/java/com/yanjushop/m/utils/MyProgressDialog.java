package com.yanjushop.m.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.yanjushop.m.views.ProgressWheel;

import cn.vbees.shop.R;

/**
 * 进度条
 * @ClassName MyProgressDialog
 * @date 2015-4-10
 */
public class MyProgressDialog extends Dialog {
	private Context context;
	private ProgressWheel progress;
	public MyProgressDialog(Context context) {
		super(context, R.style.MyDialogStyle);
		this.context=context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init(context);
	}
	public void init(Context context){
		View progressView = View.inflate(context, R.layout.my_dialog_view, null);
		progress = (ProgressWheel) progressView.findViewById(R.id.progress);
		setContentView(progressView);
	}
	public void dismissProgress(){
		if(this.isShowing()){
			this.dismiss();
			progress.stopSpinning();
		}
	}
	
	
}
