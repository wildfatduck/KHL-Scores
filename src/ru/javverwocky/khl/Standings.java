package ru.javverwocky.khl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import ru.javverwocky.khl.data.StandingsParser;
import ru.javverwocky.khl.util.URLDownloader;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Standings extends Activity {
	protected static final int MSG_PROGRESS_START = 0;
	protected static final int MSG_UPDATE = 1;
	private String currentStandings = URLDownloader.URL_STANDINGS_CHAMP;

	StandingsParser parser = new StandingsParser();
	HashMap<String, LinkedList<LinkedList<String>>> data;
	private ProgressDialog progressDialog;
	private Runnable loadThread = new Runnable() {
		@Override
		public void run() {
			loadStandings(currentStandings);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.standings);
		showStandings(URLDownloader.URL_STANDINGS_CHAMP);
	}

	private final Handler loadHandler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS_START:
				break;
			case MSG_UPDATE:
				buildTables(data);
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void loadStandings(String standings) {
		data = parser.parse(currentStandings);
		loadHandler.sendEmptyMessage(MSG_UPDATE);
	}

	private void buildTables(HashMap<String, LinkedList<LinkedList<String>>> data) {
		TableLayout table = (TableLayout) findViewById(R.id.standings_table);
		table.removeAllViews();

		int rowNum;
		int cellNum;

		for (Entry<String, LinkedList<LinkedList<String>>> tbl : data.entrySet()) {
			TextView header = new TextView(this);
			header.setText(tbl.getKey());
			header.setTextColor(Color.WHITE);
			header.setTypeface(Typeface.DEFAULT_BOLD);
			table.addView(header);

			rowNum = 0;
			for (LinkedList<String> row : tbl.getValue()) {
				TableRow tableRow = new TableRow(this);
				cellNum = 0;
				for (String cellText : row) {
					if (cellNum > 0) {
						TextView cellView = new TextView(this);
						cellView.setPadding(3, 1, 3, 1);
						cellView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
						cellView.setText(cellText);
						cellView.setTextColor(Color.WHITE);
						
						if (rowNum == 0) {
							cellView.setGravity(Gravity.CENTER_HORIZONTAL);
							cellView.setBackgroundColor(Color.rgb(0, 0, 0x80));
							cellView.setTypeface(Typeface.DEFAULT_BOLD);
						} else {
							switch (cellNum) {
							case 1:
								break;
							case 9:
								cellView.setGravity(Gravity.CENTER_HORIZONTAL);
								break;
							default:
								cellView.setGravity(Gravity.RIGHT);
							}
							
							if (rowNum%2 == 0) cellView.setBackgroundColor(Color.DKGRAY);
						}
						
						tableRow.addView(cellView);
					}
					cellNum++;
				}
				table.addView(tableRow);
				rowNum++;
			}
		}
	}

	private void showStandings(String standingsType) {
		progressDialog = ProgressDialog.show(Standings.this, "Пожалуйста, ждите", "Загружаются турнирные таблицы");

		currentStandings = standingsType;
		new Thread(loadThread).start();
	}
}
