package ru.javverwocky.khl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.javverwocky.khl.data.GameParser;
import ru.javverwocky.khl.data.TimelineItem;
import ru.javverwocky.khl.util.URLDownloader;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
		new Thread(loadThread).start();
	}

	protected void loadTimeline() {
		loadHandler.sendEmptyMessage(MSG_PROGRESS_START);
		List<TimelineItem> newResults = GameParser.parseTimeline(URLDownloader
				.urlToString("http://online.khl.ru/online/" + KHLApplication.CURRENT_GAME.getDetailsLink()));
		timeline.clear();
		timeline.addAll(newResults);
		loadHandler.sendEmptyMessage(MSG_UPDATE);
	}
}