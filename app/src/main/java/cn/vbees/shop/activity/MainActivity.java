package cn.vbees.shop.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vbees.shop.R;
import cn.vbees.shop.client.MyWebChromeClient;
import cn.vbees.shop.utils.Log;
import cn.vbees.shop.zxing.activity.CaptureActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.web)
    WebView web;
    @BindView(R.id.scan)
    Button scan;

    private long exitTime = 0L;
    private String permission = Manifest.permission.CAMERA;
    private static final int permissionRequestCode = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.init("LOG");
        web.setWebChromeClient(new MyWebChromeClient());
        web.setWebViewClient(new MyWebViewClient());
        initWebView();
        web.loadUrl("http://shop.vbees.cn/mobile/");
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
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            if (System.currentTimeMillis() - this.exitTime > 2000L) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                this.exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void chenkPermission(){
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
            startScanCode();
        }else {
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
            }else{
                ActivityCompat.requestPermissions(this, new String[]{permission}, permissionRequestCode);
            }
        }
    }



    private void startScanCode() {
        Intent i = new Intent(this, CaptureActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            scan.setText(scanResult);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case permissionRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startScanCode();
                }else{
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("shouldOverrideUrlLoading:  " + url);
            if (Patterns.WEB_URL.matcher(url).matches()) {
                return super.shouldOverrideUrlLoading(view, url);
            } else {
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            Log.i("onPageStarted:  " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            Log.i("onPageFinished:  " + url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.i("onReceivedError:  ");
//            view.loadUrl("file:///android_asset/error/error.html");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            view.loadUrl("file:///android_asset/error/error.html");
        }
    }

}
