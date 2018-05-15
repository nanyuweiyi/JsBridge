实现WebViewClient类
```
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (isHttpUrl(url)) {
            //加载url，显示网页
            return false;
        }
        //处理拦截事件
        return true;
    }
```