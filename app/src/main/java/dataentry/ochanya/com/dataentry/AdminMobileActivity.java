package dataentry.ochanya.com.dataentry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AdminMobileActivity extends AppCompatActivity {
    private WebView wv;
    private ProgressDialog pd;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mobile);
        context=this;
        pd=new ProgressDialog(context);
        wv=(WebView) findViewById(R.id.admin_wv);
        WebSettings ws=wv.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);

        wv.loadUrl("https://royalsophen.com/mobile_login.php");

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view, url, favicon);
                //pd=new ProgressDialog(context);
                pd.setMessage("Loading. Please wait...");
                pd.setIndeterminate(false);
                pd.setCancelable(true);
                pd.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(view, url);
                if(pd.isShowing()){
                    pd.dismiss();
                }
            }
        });
        wv.canGoBack();
        wv.canGoForward();

    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }

    }
}
