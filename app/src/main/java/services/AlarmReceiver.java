package services;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.app.color_alarm.R;
import common.AppModel;
import common.Question;

public class AlarmReceiver extends BroadcastReceiver {

	MediaPlayer mp = null;

	@Override
	public void onReceive(final Context context, Intent intent) {
		new AppModel(context);

		AppModel.SaveSettingVariable_Bool(context, AppModel.APP_STOP_MUSIC, false);

		String path = AppModel.GetSettingVariable(context, AppModel.APP_MUSIC_PATH);

		if (!AppModel.IsNullOrEmpty(path) && new File(path).exists())
			mp = AppModel.PlaySound(context, path);
		else
			mp = AppModel.PlaySound(context, R.raw.alarm);

		Intent i = new Intent(context, Question.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);

		(new Thread(new Runnable() {
			@Override
			public void run() {
				while (mp.isPlaying()) {
					try {
						boolean stop = AppModel.GetSettingVariable_Bool(context, AppModel.APP_STOP_MUSIC);
						if (stop) {
							if (mp != null)
								mp.stop();
							break;
						}
					} catch (Exception e) {
						AppModel.ApplicationError(e, "AlarmReceiver::Thread");
					}

					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						AppModel.ApplicationError(e, "AlarmReceiver::Thread-Sleep");
					}
				}
			}
		})).start();
	}
}