package com.yanjushop.m.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.orhanobut.logger.Logger;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yanjushop.m.entry.Constants;
import com.yanjushop.m.entry.Events;
import com.yanjushop.m.okhttp.RetrofitUtils;
import com.yanjushop.m.utils.ActivityManager;
import com.yanjushop.m.utils.DeviceInfo;
import com.yanjushop.m.utils.L;
import com.yanjushop.m.utils.MyProgressDialog;
import com.yanjushop.m.utils.ToastUtlis;
import com.yanjushop.m.views.ProgressWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.vbees.shop.R;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.web)
    ProgressWebView web;
    private IWXAPI api;

    private long exitTime = 0L;
    private String permission = Manifest.permission.CAMERA;
    private static final int permissionRequestCode = 77;

    private String failingUrl;
    private MyProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Logger.init("LOG");
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        web.setWebViewClient(new MyWebViewClient());
        initWebView();
        web.loadUrl("http://shop.vbees.cn/mobile/");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initWebView() {
        //请求焦点
        web.requestFocus();
        //获取缩放控制控件
        ZoomButtonsController zbc = new ZoomButtonsController(web);
        zbc.getZoomControls().setVisibility(View.GONE);
        WebSettings settings = web.getSettings();
        //支持javascript
        settings.setJavaScriptEnabled(true);
        //设置是否支持缩放
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        if (DeviceInfo.isNetworkConnected(this))
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        else
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //自适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//		//支持内容重新布局
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //文字编码
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDomStorageEnabled(true);
        //隐藏缩放控制条
        if (Build.VERSION.SDK_INT > 11) {
            settings.setDisplayZoomControls(false);
        }
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        web.addJavascriptInterface(this, "vbees");
        web.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (result == null)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE)
                    return false;
                if (type == WebView.HitTestResult.PHONE_TYPE) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + result.getExtra())));
                    } catch (Exception e) {

                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - this.exitTime > 1200L) {
            this.exitTime = System.currentTimeMillis();
            if (web.canGoBack()) {
                web.goBack();
            } else {
                showExitDialog();
            }
        } else {
            showExitDialog();
        }
    }

    public void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("退出提示")
                .setMessage("确定要退出程序吗")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityManager.getInstance().exitApp(MainActivity.this);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void chenkPermission() {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            startScanCode();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("权限说明")
                        .setMessage("请开启相机权限，以便您能正常使用扫码功能")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("开启权限", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, permissionRequestCode);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, permissionRequestCode);
            }
        }
    }


    @JavascriptInterface
    public void refersh() {
        if (!TextUtils.isEmpty(failingUrl))
            web.loadUrl(failingUrl);
        else if (web.canGoBack())
            web.goBack();
    }

    /**
     * 处理事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(Events event) {
        switch (event){
            case PAY_OK:
                web.loadUrl("http://shop.vbees.cn/mobile/");
                web.clearHistory();
                break;
        }
    }


    private void startScanCode() {
        Intent i = new Intent(this, CaptureActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final String resuelt = data.getStringExtra(CaptureActivity.RESULT_KEY);
            new AlertDialog.Builder(this)
                    .setTitle("扫描结果")
                    .setMessage(resuelt)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("打开连接", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Patterns.WEB_URL.matcher(resuelt).matches()) {
                                web.loadUrl(resuelt);
                            }
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case permissionRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanCode();
                } else {
                    chenkPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            L.i("shouldOverrideUrlLoading:  " + url);
            if (TextUtils.isEmpty(url))
                return true;
            if (Patterns.WEB_URL.matcher(url).matches()) {
                if (url.contains("wx_pay")) {
                    if (api.getWXAppSupportAPI() >= com.tencent.mm.sdk.constants.Build.PAY_SUPPORTED_SDK_INT)
                        wechatPay(url);
                    else
                        ToastUtlis.makeTextLong(MainActivity.this, "您安装的微信版本不支持支付功能，请升级微信版本");
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            } else {
                try {
                    if (url.contains("tel:")) {
                        final String telUrl = url;
                        String check = "[\\d\\-]{9,}";
                        Pattern p = Pattern.compile(check);
                        Matcher matcher = p.matcher(telUrl);
                        if (matcher.find()) {
                            String group = matcher.group();
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("温馨提示")
                                    .setMessage("您确定要拨打客服热线:" + group)
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(telUrl)));
                                        }
                                    })
                                    .show();
                        }
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                } catch (Exception e) {
                }
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            L.i("onPageStarted:  " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            L.i("onPageFinished:  " + url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            MainActivity.this.failingUrl = request.getUrl().toString();
            view.loadUrl("file:///android_asset/error/error.html");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            MainActivity.this.failingUrl = failingUrl;
            L.i("加载错误的网页------------------》" + failingUrl);
            view.loadUrl("file:///android_asset/error/error.html");
        }
    }

    private void wechatPay(String url) {
        RetrofitUtils.getStringRequet().wechatPay(url)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (progress == null)
                            progress = new MyProgressDialog(MainActivity.this);
                        progress.show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        progress.dismissProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismissProgress();
                        ToastUtlis.requestFailToast(MainActivity.this, "调起支付失败");
                    }

                    @Override
                    public void onNext(String s) {
                        try {
                            if (!TextUtils.isEmpty(s)) {
                                JSONObject data = new JSONObject(s);
                                if (data != null) {
                                    PayReq req = new PayReq();
                                    req.appId = data.getString("appid");
                                    req.partnerId = data.getString("partnerid");
                                    req.prepayId = data.getString("prepayid");
                                    req.nonceStr = data.getString("noncestr");
                                    req.timeStamp = data.getString("timestamp");
                                    req.packageValue = data.getString("package");
                                    req.sign = data.getString("sign");
                                    req.extData = "app data"; // optional
                                    api.sendReq(req);
                                }
                            }
                        } catch (Exception e) {
                            ToastUtlis.makeTextLong(MainActivity.this, "数据解析异常");
                        }
                    }
                });
    }

}
