package common;

import java.util.Locale;
import java.util.Random;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.app.color_alarm.R;

public class Question extends FragmentActivity {

	public enum QuestionColor {
		RED, GREEN, BLUE;
	}

	QuestionColor CURRENT_QUESTION_COLOR = QuestionColor.RED;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Locale.setDefault(Locale.ENGLISH);

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
				String source = paramThread != null ? paramThread.getName() : "";
				String stack = AppModel.getStackTrace(paramThrowable);
				AppModel.WriteLog(AppModel.LOG_FILE_NAME, source, stack);
			}
		});

		setContentView(R.layout.questions);

		TextView tv_Question = (TextView) findViewById(R.id.tv_Question);
		int randomColorNum = randInt(1, 3);
		if (randomColorNum == 1)
			CURRENT_QUESTION_COLOR = QuestionColor.RED;
		else if (randomColorNum == 2)
			CURRENT_QUESTION_COLOR = QuestionColor.GREEN;
		else
			CURRENT_QUESTION_COLOR = QuestionColor.BLUE;
		tv_Question.setText("Please Tap " + CURRENT_QUESTION_COLOR + " Color to stop alarm");

		findViewById(R.id.btnRed).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CURRENT_QUESTION_COLOR == QuestionColor.RED) {
					AppModel.SaveSettingVariable_Bool(getApplicationContext(), AppModel.APP_STOP_MUSIC, true);
					finish();
				}
			}
		});
		findViewById(R.id.btnGreen).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (CURRENT_QUESTION_COLOR == QuestionColor.GREEN) {
					AppModel.SaveSettingVariable_Bool(getApplicationContext(), AppModel.APP_STOP_MUSIC, true);
					finish();
				}
			}
		});
		findViewById(R.id.btnBlue).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (CURRENT_QUESTION_COLOR == QuestionColor.BLUE) {
					AppModel.SaveSettingVariable_Bool(getApplicationContext(), AppModel.APP_STOP_MUSIC, true);
					finish();
				}
			}
		});
	}

	public static int randInt(int min, int max) {

		// NOTE: This will (intentionally) not run as written so that folks
		// copy-pasting have to think about how to initialize their
		// Random instance. Initialization of the Random instance is outside
		// the main scope of the question, but some decent options are to have
		// a field that is initialized once and then re-used as needed or to
		// use ThreadLocalRandom (if using at least Java 1.7).

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = (new Random()).nextInt((max - min) + 1) + min;

		return randomNum;
	}

	@Override
	public void onBackPressed() {

	}
}
