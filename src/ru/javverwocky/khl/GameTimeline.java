package ru.javverwocky.khl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.javverwocky.khl.data.GameParser;
import ru.javverwocky.khl.data.TimelineItem;
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

public class GameTimeline extends ListActivity {
	private static final int MSG_PROGRESS_START = 0;
	private static final int MSG_UPDATE = 1;

	private TextView empty;
	private TimelineAdapter timelineAdapter;
	private List<TimelineItem> timeline = new ArrayList<TimelineItem>();
	private Timer timer;
	private Runnable loadThread = new Runnable() {
		@Override
		public void run() {
			loadTimeline();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);

		empty = (TextView) findViewById(android.R.id.empty);

		ListView timelineList = getListView();
		timelineList.setItemsCanFocus(true);
		timelineList.setChoiceMode(ListView.CHOICE_MODE_NONE);

		timelineAdapter = new TimelineAdapter(this, timeline);
		setListAdapter(timelineAdapter);
	}

	private final Handler loadHandler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case GameTimeline.MSG_PROGRESS_START:
				setProgressBarIndeterminateVisibility(true);
				break;
			case GameTimeline.MSG_UPDATE:
				if (timeline == null || timeline.size() == 0) {
					empty.setText(getResources().getText(R.string.empty));
				}

				timelineAdapter.notifyDataSetChanged();

				setProgressBarIndeterminateVisibility(false);
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		double period = Prefs.getIntervalForGame(this);
		if (period > 0) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					loadTimeline();
				}
			}, 0, (long) (period * 60000));
		} else {
			new Thread(loadThread).start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		timer.purge();
	}

	protected void loadTimeline() {
		loadHandler.sendEmptyMessage(MSG_PROGRESS_START);
		List<TimelineItem> newResults = GameParser.parseTimeline(URLDownloader
				.loadGameDetails(KHLApplication.CURRENT_GAME.getDetailsLink()));
		timeline.clear();
		timeline.addAll(newResults);
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
			new Thread(loadThread).start();
			return true;
		case R.id.prefs_mnu:
			startActivity(new Intent(this, Prefs.class));
			return true;
		case R.id.standings_mnu:
			startActivity(new Intent(this, Standings.class));
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
