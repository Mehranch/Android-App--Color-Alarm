package common;

import java.io.File;
import java.util.Calendar;

import services.AlarmReceiver;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.app.color_alarm.R;

public class HomeCtrl extends LinearLayout {

	TimePicker tp;
	Button btnSave;
	RadioButton rb_Def, rb_Custom;
	EditText et_Path;

	public static int PICK_CODE = 11;

	public HomeCtrl(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.ctrl_home, this);

		if (!this.isInEditMode()) {
			tp = (TimePicker) findViewById(R.id.tp);
			btnSave = (Button) findViewById(R.id.btnSave);

			rb_Custom = (RadioButton) findViewById(R.id.rb_Custom);
			rb_Def = (RadioButton) findViewById(R.id.rb_Def);

			et_Path = (EditText) findViewById(R.id.et_Path);

			btnSave.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int hour = tp.getCurrentHour();
						int min = tp.getCurrentMinute();

						AppModel.SaveSettingVariables(App.Object, AppModel.APP_MARKING_TIME_KEY, hour + "," + min);

						SetAlarm(App.Object);

						MessageCtrl.Toast("Settings Saved");

					} catch (Exception ex) {
						AppModel.ApplicationError(ex, "HomeCtrl::btnSave");
					}
				}
			});
		}
	}

	public static void SetAlarm(Context context) {
		try {
			int ALARM_ID = 100;

			AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			Intent iCancel = new Intent(context, AlarmReceiver.class);
			PendingIntent piCancel = PendingIntent.getBroadcast(context, ALARM_ID, iCancel, PendingIntent.FLAG_UPDATE_CURRENT);
			piCancel.cancel();
			alarmMgr.cancel(piCancel);

			int[] hourMin = GetHourMinuteOfMarking(context);
			if (hourMin.length > 0) {
				Calendar alarmTime = AppModel.GetCalendar(hourMin[0], hourMin[1], 0, 0);
				Calendar currentTime = AppModel.GetCalendar(false);
				currentTime.set(Calendar.SECOND, 0);
				currentTime.set(Calendar.MILLISECOND, 0);

				if (alarmTime.before(currentTime))
					alarmTime.add(Calendar.DAY_OF_YEAR, 1);

				Intent intent = new Intent(context, AlarmReceiver.class);
				PendingIntent operation = PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_ONE_SHOT);
				alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), operation);
			}
		} catch (Exception e) {
			AppModel.ApplicationError(e, "App::SetAlarm");
		}
	}

	public void Load() {
		String path = AppModel.GetSettingVariable(App.Object, AppModel.APP_MUSIC_PATH);

		if (!AppModel.IsNullOrEmpty(path) && new File(path).exists()) {
			et_Path.setText(path);
			rb_Custom.setChecked(true);
			rb_Def.setChecked(false);
		} else {
			et_Path.setText("");
			AppModel.SaveSettingVariables(App.Object, AppModel.APP_MUSIC_PATH, "");
			rb_Custom.setChecked(false);
			rb_Def.setChecked(true);
		}

		rb_Custom.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					Intent pick = new Intent();
					pick.setType("audio/mpeg");
					pick.setAction(Intent.ACTION_GET_CONTENT);
					App.Object.startActivityForResult(pick, PICK_CODE);
				} else {
					AppModel.SaveSettingVariables(App.Object, AppModel.APP_MUSIC_PATH, "");
					et_Path.setText("");
				}
			}
		});

		int[] hourMin = GetHourMinuteOfMarking(App.Object);
		if (hourMin.length > 0) {
			tp.setCurrentHour(hourMin[0]);
			tp.setCurrentMinute(hourMin[1]);
		}
	}

	public void OnFileSelected(Uri uri) {
		String path = uri.getPath();
		if (!(new File(path).exists()))
			path = getRealPathFromURI(uri);
		if (AppModel.IsNullOrEmpty(path))
			path = getPath(App.Object, uri);

		if (!AppModel.IsNullOrEmpty(path)) {
			AppModel.SaveSettingVariables(App.Object, AppModel.APP_MUSIC_PATH, path);
			et_Path.setText(path);
		} else {
			MessageCtrl.Show("Invalid file");
			et_Path.setText("");
			AppModel.SaveSettingVariables(App.Object, AppModel.APP_MUSIC_PATH, "");
			rb_Custom.setChecked(false);
			rb_Def.setChecked(true);
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = App.Object.getContentResolver().query(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static int[] GetHourMinuteOfMarking(Context context) {
		String hourMin = AppModel.GetSettingVariable(context, AppModel.APP_MARKING_TIME_KEY);
		if (!AppModel.IsNullOrEmpty(hourMin)) {
			String[] hm = AppModel.Split(hourMin, ",");
			int hour = Integer.parseInt(hm[0]);
			int min = Integer.parseInt(hm[1]);

			return new int[] { hour, min };
		}
		return new int[0];
	}
}
