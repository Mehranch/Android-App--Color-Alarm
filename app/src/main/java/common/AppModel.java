package common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.app.color_alarm.R;

public class AppModel {

	public static final boolean IsBeta = false;

	public static final String APP_FOLDER_NAME = "Color Alarm";

	public static final String APP_SHARED_PREFERENCE_NAME = "AppSharedPref";
	public static final String LOG_FILE_NAME = "ExceptionsLog";
	public static final String LOG_FOLDER_NAME = "Logs";

	public static final String APP_STOP_MUSIC = "StopMusic";
	public static final String APP_MUSIC_PATH = "MusicPath";
	public static final String APP_MARKING_TIME_KEY = "AppMarkingTime";

	public Context context;
	public SharedPreferences sharedPreferences;
	public static AppModel Object;
	ConnectivityManager connectivityManager;

	boolean cancel = false;

	public AppModel(Context _context) {
		context = _context;
		sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Object = this;
	}

	public void Start() {
		try {

		} catch (Exception ex) {
			ApplicationError(ex, "AppModel::Start");
		}
	}

	public void Stop() {
		// Shut the application background worker
		cancel = true;
	}

	public static void SaveSettingVariables(Context context, String key, String value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(APP_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();
		} catch (Exception e) {
			ApplicationError(e, "AppModel::SaveSettings");
		}
	}

	public static String GetSettingVariable(Context context, String key) {
		try {
			SharedPreferences sp = context.getSharedPreferences(APP_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
			return sp.getString(key, null);
		} catch (Exception e) {
			ApplicationError(e, "AppModel::GetSettingVariable");
		}
		return null;
	}

	public static void SaveSettingVariable_Bool(Context context, String key, boolean value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(APP_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(key, value);
			editor.commit();
		} catch (Exception e) {
			ApplicationError(e, "AppModel::SaveSettingVariable_Bool");
		}
	}

	public static boolean GetSettingVariable_Bool(Context context, String key) {
		try {
			SharedPreferences sp = context.getSharedPreferences(APP_SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
			return sp.getBoolean(key, false);
		} catch (Exception e) {
			ApplicationError(e, "AppModel::GetSettingVariable_Bool");
		}
		return false;
	}

	public static MediaPlayer PlaySound(Context context, int resourceId) {
		Uri url = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
		MediaPlayer p = MediaPlayer.create(context, url);
		p.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		p.start();
		return p;
	}

	public static MediaPlayer PlaySound(Context context, String path) {
		Uri url = Uri.parse(path);
		MediaPlayer p = MediaPlayer.create(context, url);
		p.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		p.start();
		return p;
	}

	public String GetStringResource(int id) {
		return context.getString(id);
	}

	public Drawable GetDrawableResource(int id) {
		return context.getResources().getDrawable(id);
	}

	public Bitmap GetBitmapResource(int id) {
		return BitmapFactory.decodeResource(context.getResources(), id);
	}

	public int GetColorResource(int id) {
		return context.getResources().getColor(id);
	}

	public static Bitmap GetResizedBitmapFromPath(String path, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Calendar GetCalendar(boolean onlyDate) {
		Calendar cal = Calendar.getInstance();
		if (onlyDate) {
			cal.set(Calendar.HOUR_OF_DAY, 0); // HOUR -> Doesn't effect AM/PM
												// While HOUR_OF_DAY effects..
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal;
	}

	public static Calendar GetCalendar(int DAY_OF_YEAR, int MONTH, int YEAR, int HOUR_OF_DAY, int MINUTE, int SECOND, int MILLISECOND) {
		Calendar cal = GetCalendar(false);
		cal.set(Calendar.DAY_OF_YEAR, DAY_OF_YEAR);
		cal.set(Calendar.MONTH, MONTH);
		cal.set(Calendar.YEAR, YEAR);
		cal.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY); // HOUR -> Doesn't effect AM/PM
		// While HOUR_OF_DAY effects..
		cal.set(Calendar.MINUTE, MINUTE);
		cal.set(Calendar.SECOND, SECOND);
		cal.set(Calendar.MILLISECOND, MILLISECOND);
		return cal;
	}

	public static Calendar GetCalendar(int HOUR_OF_DAY, int MINUTE, int SECOND, int MILLISECOND) {
		Calendar cal = GetCalendar(false);
		return GetCalendar(cal.get(Calendar.DAY_OF_YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND);
	}

	public static Calendar GetCalendar(int DAY_OF_YEAR, int MONTH, int YEAR) {
		Calendar cal = GetCalendar(false);
		return GetCalendar(DAY_OF_YEAR, MONTH, YEAR, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
	}

	public static Calendar GetCalendar(Date time, boolean setTodayDate) {
		if (setTodayDate)
			return GetCalendar(GetCalendar(false), time);
		else {
			Calendar cal = GetCalendar(false);
			cal.setTime(time);
			return cal;
		}
	}

	public static Calendar GetCalendar(Calendar date, Date time) {
		Calendar cal = GetCalendar(false);
		cal.setTime(time);
		return GetCalendar(date.get(Calendar.DAY_OF_YEAR), date.get(Calendar.MONTH), date.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
	}

	public void SetEnability_EditText(final EditText et, final boolean doEnable) {
		App.Object.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				et.setFocusable(doEnable);
				et.setFocusableInTouchMode(doEnable);
				et.setClickable(doEnable);

				AppModel.Object.SetBackground(et, doEnable ? R.drawable.edittext_whitebg_primaryborder_notop_rounded : R.drawable.edittext_whitebg_primaryborder_notop_rounded_disabled);
			}
		});
	}

	public void SetEnability_Spinner(final Spinner spin, final boolean doEnable) {
		App.Object.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				spin.setEnabled(doEnable);

				AppModel.Object.SetBackground(spin, doEnable ? R.drawable.edittext_whitebg_rounded : R.drawable.edittext_whitebg_rounded_disable);
			}
		});
	}

	public void SetEnability(final View btn, final boolean doEnable, final int enableDrawableId) {
		App.Object.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btn.setEnabled(doEnable);
				AppModel.Object.SetBackground(btn, doEnable ? enableDrawableId : R.drawable.button_rounded_disabled);
				// ANDROID_COMPATIBILITY
				Runnable sdkAction = new Runnable() {
					@SuppressLint("NewApi")
					@Override
					public void run() {
						btn.setAlpha(doEnable ? 1F : 0.5F);
					}
				};
				if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
					sdkAction.run();
				else
					btn.getBackground().setAlpha(doEnable ? 255 : 127);
			}
		});
	}

	public void SetEnability(final View btn, final boolean doEnable) {
		SetEnability(btn, doEnable, R.drawable.button_style);
	}

	public void SetBackground(View view, int resId) {
		view.setBackgroundResource(resId);
	}

	public static boolean IsNullOrEmpty(String string) {
		return string == null || string.equals("") || string.trim().equals("") || string.length() == 0 || string.trim().equalsIgnoreCase("null");
	}

	public enum AppFolderType {
		Logs, Images
	}

	public static File GetAppFolder(AppFolderType type) {
		File reqFile = null;

		File appDir = new File(Environment.getExternalStorageDirectory() + File.separator + APP_FOLDER_NAME);
		if (!appDir.exists())
			appDir.mkdirs();

		switch (type) {
		case Logs:
			reqFile = new File(appDir.getAbsolutePath() + File.separator + "Logs");
			break;
		case Images:
			reqFile = new File(appDir.getAbsolutePath() + File.separator + "Images");
			break;
		default:
			reqFile = appDir;
			break;
		}

		if (!reqFile.exists())
			reqFile.mkdirs();

		return reqFile;
	}

	public static String ConvertToCsv(Object[] items) {
		String csv = "";
		for (int indx = 0; indx < items.length; indx++) {
			csv += indx == 0 ? items[indx].toString() : ("," + items[indx].toString());
		}
		return csv;
	}

	public static String[] Split(String stringToSplit, String splitBy) {
		return IsNullOrEmpty(stringToSplit) ? new String[0] : stringToSplit.trim().split("[" + splitBy + "]");
	}

	public boolean IsNetworkAvailable(boolean showMsg) {

		try {
			if (connectivityManager == null)
				connectivityManager = (ConnectivityManager) App.Object.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			// boolean isWiFi = info.getType() == ConnectivityManager.TYPE_WIFI;
			boolean hasNetwork = info != null && (info.getState() == NetworkInfo.State.CONNECTED);

			if (!hasNetwork && showMsg) {
				MessageCtrl.Show("Internet connection is not available");
			}

			return hasNetwork;
		} catch (Exception ex) {
			ApplicationError(ex, "App::IsNetworkAvailable");
		}

		return false;
	}

	public static String GetExceptionMessage(Exception ex) {
		String msg = "";
		try {
			if (ex.getMessage() == null) {
				msg = ex.toString();
				msg = msg.contains(":") ? Split(msg, ":")[1].trim() : msg;
			} else
				msg = ex.getMessage();
		} catch (Exception exp) {
			ApplicationError(exp, "AppModel::GetExceptionMessage()");
		}
		return msg;
	}

	public static void ApplicationError(Exception exp, String source) {
		try {

			String message = getStackTrace(exp.fillInStackTrace());
			if (message == null)
				message = GetExceptionMessage(exp);

			AppModel.WriteLog(LOG_FILE_NAME, source, message);
		} catch (Exception ex) {

		}
	}

	public static void WriteLog(String logFileName, String source, String stack) {
		try {
			String logTxt = "\n  Source: " + (IsNullOrEmpty(source) ? "" : source) + "\n  Stack: " + (IsNullOrEmpty(stack) ? "" : stack);

			Calendar day = AppModel.GetCalendar(false);
			String month = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(day.getTime()); // (day.get(Calendar.MONTH) + 1)
			String date = day.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + day.get(Calendar.YEAR);
			String fileName = "/" + logFileName + "(" + date + ").txt";
			File log = new File(AppModel.GetAppFolder(AppFolderType.Logs) + "/" + fileName);
			OutputStream myOutput = new FileOutputStream(log, log.exists());
			logTxt = "Datetime: " + day.getTime().toString() + "\n" + "Exception:" + logTxt + "\n\n\n";
			myOutput.write(logTxt.getBytes());

			// Close the streams
			myOutput.flush();
			myOutput.close();

		} catch (IOException ex) {

		}
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
