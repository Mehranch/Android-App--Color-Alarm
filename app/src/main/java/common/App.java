package common;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.color_alarm.R;

public class App extends FragmentActivity {

	public static final boolean IsTesting = false;

	AppModel Model;
	public static App Object;

	ImageView iv_Weather, iv_Home;
	WebView webView;

	// Controls
	public HomeCtrl homeCtrl = null;
	public RelativeLayout webViewCtrl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Object = this;
		Model = new AppModel(getApplicationContext());

		Locale.setDefault(Locale.ENGLISH);

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
				String source = paramThread != null ? paramThread.getName() : "";
				String stack = AppModel.getStackTrace(paramThrowable);
				AppModel.WriteLog(AppModel.LOG_FILE_NAME, source, stack);
			}
		});

		setContentView(R.layout.main);

		final RelativeLayout splashCtrl = (RelativeLayout) findViewById(R.id.splashCtrl);

		homeCtrl = (HomeCtrl) findViewById(R.id.homeCtrl);
		webViewCtrl = (RelativeLayout) findViewById(R.id.webViewCtrl);

		iv_Weather = (ImageView) findViewById(R.id.iv_Weather);
		iv_Weather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				webViewCtrl.setVisibility(View.VISIBLE);
			}
		});

		iv_Home = (ImageView) findViewById(R.id.iv_Home);
		iv_Home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				webViewCtrl.setVisibility(View.GONE);
			}
		});

		webView = (WebView) findViewById(R.id.webView);
		InitializeWebView();
		webView.loadUrl("https://weather.com");

		LoadControls();
		Model.Start();

		(new Handler()).postDelayed(new Runnable() {
			@Override
			public void run() {
				splashCtrl.setVisibility(View.GONE);
			}
		}, 3000);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void InitializeWebView() {
		try {
			webView.getSettings().setLoadsImagesAutomatically(true);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setScrollBarStyle(0);
			webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView wv, String url) {
					wv.loadUrl(url);
					return true;
				}

				public void onPageFinished(WebView view, String url) {
				}

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
				}
			});
			webView.setWebChromeClient(new WebChromeClient());
		} catch (Exception ex) {
			AppModel.ApplicationError(ex, "App::InitializeWebView");
		}
	}

	public void LoadControls() {
		homeCtrl.Load();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Model.Stop();
	}

	@Override
	public void onBackPressed() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == HomeCtrl.PICK_CODE) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				homeCtrl.OnFileSelected(uri);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
