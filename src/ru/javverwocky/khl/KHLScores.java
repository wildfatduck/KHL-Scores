package ru.javverwocky.khl;

import java.util.List;

import ru.javverwocky.khl.data.Game;
import ru.javverwocky.khl.data.GameParser;
import ru.javverwocky.khl.util.URLDownloader;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class KHLScores extends ListActivity {
	private static final int MENU_REFRESH = Menu.FIRST;

	private TextView empty;
	private GameAdapter gameAdapter;
	private List<Game> games;
	private ProgressDialog progress;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		empty = (TextView) findViewById(R.id.empty);

		ListView matchList = getListView();
		matchList.setEmptyView(empty);
		matchList.setItemsCanFocus(true);
		matchList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	private final Handler loadHandler = new Handler() {
		public void handleMessage(final Message msg) {
			progress.dismiss();
			if (!(games == null || games.size() == 0)) {
				gameAdapter = new GameAdapter(KHLScores.this, games);
				setListAdapter(gameAdapter);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		loadGames();
	}

	private void loadGames() {
		progress = ProgressDialog.show(this, "Загрузка...", "Загрузка информации о матчах");

		new Thread() {
			@Override
			public void run() {
				games = GameParser.get().parseGameResults(
						URLDownloader.get().urlToString("http://online.khl.ru/online/"));
				loadHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.menu_refresh);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			loadGames();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}