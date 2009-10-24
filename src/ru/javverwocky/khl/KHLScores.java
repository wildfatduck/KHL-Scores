package ru.javverwocky.khl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.javverwocky.khl.data.Game;
import ru.javverwocky.khl.data.GameParser;
import ru.javverwocky.khl.util.URLDownloader;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class KHLScores extends ListActivity {
	private static final int MSG_PROGRESS_START = 0;
	private static final int MSG_UPDATE = 1;

	private TextView empty;
	private GameAdapter gameAdapter;
	private List<Game> games;
	private TimerTask updateTimerTask = new TimerTask() {
		@Override
		public void run() {
			loadGames();
		}
	};
	private Timer timer = new Timer();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);

		empty = (TextView) findViewById(R.id.empty);

		ListView gamesList = getListView();
		gamesList.setEmptyView(empty);
		gamesList.setItemsCanFocus(true);
		gamesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	private final Handler loadHandler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case KHLScores.MSG_PROGRESS_START:
				setProgressBarIndeterminateVisibility(true);
				break;
			case KHLScores.MSG_UPDATE:
				if (games == null || games.size() == 0) {
					empty.setText(getResources().getText(R.string.empty));
				} else {
					gameAdapter = new GameAdapter(KHLScores.this, games);
					setListAdapter(gameAdapter);
				}
				setProgressBarIndeterminateVisibility(false);
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		int period = Prefs.getInterval(this);
		if (period > 0) {
			timer.schedule(updateTimerTask, 0, period * 60000);
		} else {
			new Thread() {
				@Override
				public void run() {
					loadGames();
				}

			}.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
	}

	private void loadGames() {
		loadHandler.sendEmptyMessage(MSG_PROGRESS_START);
		games = GameParser.get().parseGameResults(URLDownloader.get().urlToString("http://online.khl.ru/online/"));
		loadHandler.sendEmptyMessage(MSG_UPDATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.prefsmenu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh_mnu:
			loadGames();
			return true;
		case R.id.prefs_mnu:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}