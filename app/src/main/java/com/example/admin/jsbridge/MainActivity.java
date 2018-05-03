package com.example.admin.jsbridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    BridgeWebView webView;

    Button button;

    int RESULT_CODE = 0;

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        button = findViewById(R.id.button);

        button.setOnClickListener(this);

        webView.setDefaultHandler(new myHadlerCallBack());

        webView.loadUrl("file:///android_asset/demo.html");

        webView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                String str = "这是html返回给java的数据:" + data;
                Toast.makeText(MainActivity.this, "java原生方法被调用了" + str, Toast.LENGTH_LONG).show();
                //回调返回给Js
                function.onCallBack("web调用原生");//该参数是传递给html的
            }

        });

        User user = new User();
        Location location = new Location();
        location.address = "北京";
        user.location = location;
        user.name = "Android";

        //不需要回调
        webView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });

        //java执行完js方法需要回调值时使用
        webView.addJavascriptInterface(new JavaScriptInterface(), "huidiao");

    }

    //java执行完js方法无回传值
//    public void JsParse(String data){
//        webView.loadUrl("javascript:dealWithRequest('" + data + "')");
//    }

    //java执行完js方法有回传值
    public void JsParse(String data) {
        String url = "javascript:huidiao.startFunction(dealWithRequest('" + data + "'));";
        webView.loadUrl(url);
    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void startFunction(String result) {
            String aString = result;
            Toast.makeText(MainActivity.this, aString, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {

                    @Override
                    public void onCallBack(String data) {
                        Toast.makeText(MainActivity.this, "reponse data from js " + data, Toast.LENGTH_SHORT).show();
                    }

                });
                break;
            case R.id.button1:
                JsParse("传递给js的值");
                break;
        }

    }

    /**
     * 自定义回调
     */
    class myHadlerCallBack extends DefaultHandler {

        @Override
        public void handler(String data, CallBackFunction function) {
            if (function != null) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
