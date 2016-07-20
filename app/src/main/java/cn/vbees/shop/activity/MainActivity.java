package cn.vbees.shop.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.vbees.shop.R;
import cn.vbees.shop.client.MyWebChromeClient;
import cn.vbees.shop.client.MyWebViewClient;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.web)
    WebView web;

    private long exitTime = 0L;

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
        if(android.os.Build.VERSION.SDK_INT > 11){
            settings.setDisplayZoomControls(false);
        }
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()){
            web.goBack();
        }else {
            if (System.currentTimeMillis() - this.exitTime > 2000L) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                this.exitTime = System.currentTimeMillis();
            }else {
                super.onBackPressed();
            }
        }
    }
}
