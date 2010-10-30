package ru.javverwocky.khl;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ListView;
import android.widget.TextView;

public class KHLScores extends ListActivity {
	private static final int MSG_PROGRESS_START = 0;
	private static final int MSG_UPDATE = 1;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private TextView empty;
	private GameAdapter gameAdapter;
	private final List<Object> games = new ArrayList<Object>();
	private Timer timer;
	private final Runnable loadThread = new Runnable() {
		@Override
		public void run() {
			loadGames();
		}
	};

	private GestureDetector gestureDetector;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);

		gestureDetector = new GestureDetector(new KHLGestureDetector());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

		empty = (TextView) findViewById(android.R.id.empty);
		empty.setOnTouchListener(gestureListener);

		ListView gamesList = getListView();
		gamesList.setItemsCanFocus(true);
		gamesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		gamesList.setOnTouchListener(gestureListener);

		gameAdapter = new GameAdapter(KHLScores.this, games);
		setListAdapter(gameAdapter);

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
				}
				
				setTitle("KHLScores [" + (games.get(0) != null ? games.get(0) : "") + "]");
				games.remove(0);

				gameAdapter.notifyDataSetChanged();

				setProgressBarIndeterminateVisibility(false);
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		double period = Prefs.getInterval(this);
		if (period > 0) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					loadGames();
				}
			}, 0, (long) (period * 60000));
		} else {
			new Thread(loadThread).start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

	private void loadGames() {
		loadHandler.sendEmptyMessage(MSG_PROGRESS_START);
		List<Object> newResults = GameParser.parseGameResults(URLDownloader.loadResults());
		games.clear();
		games.addAll(newResults);
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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Object item = games.get(position);
		if (item instanceof Game && ((Game) item).getDetailsLink() != null) {
			KHLApplication.CURRENT_GAME = (Game) item;
			startActivity(new Intent(this, GameTimeline.class));
		}
	}

	class KHLGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;

				Calendar c = Calendar.getInstance();
				c.setTime(KHLApplication.currentDate);
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					c.add(Calendar.DATE, 1);
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					c.add(Calendar.DATE, -1);
				}
				KHLApplication.currentDate = c.getTime();
				loadGames();
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
	}
}