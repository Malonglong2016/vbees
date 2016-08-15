package com.yanjushop.m.wxapi;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yanjushop.m.entry.Constants;
import com.yanjushop.m.entry.Events;
import com.yanjushop.m.utils.L;

import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
		L.d("onPayFinish, 进入了这个 = ");
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		L.d("onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int code = resp.errCode;
			String msg = "支付失败";
			switch (code){
				case BaseResp.ErrCode.ERR_OK:
					msg = "支付成功";
					EventBus.getDefault().post(Events.PAY_OK);
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					msg = "取消支付";
					break;
				case BaseResp.ErrCode.ERR_SENT_FAILED:
					msg = "请求支付失败";
					break;
			}
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle("支付结果")
					.setMessage(msg)
					.setPositiveButton("确定", null)
					.show();
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
				}
			});
		}
	}
}