package ru.javverwocky.khl;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	private static final String OPT_INTERVAL = "refresh_inerval";
	private static final String OPT_INTERVAL_DEF = "1";
	private static final String OPT_INTERVAL_GAME = "refresh_game_inerval";
	private static final String OPT_INTERVAL_GAME_DEF = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	public static double getInterval(Context context) {
		return Double.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_INTERVAL, OPT_INTERVAL_DEF));
	}

	public static double getIntervalForGame(Context context) {
		return Double.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_INTERVAL_GAME, OPT_INTERVAL_GAME_DEF));
	}

}
