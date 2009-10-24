package ru.javverwocky.khl;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	private static String OPT_INTERVAL = "refresh_inerval";
	private static String OPT_INTERVAL_DEF = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	public static int getInterval(Context context) {
		return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_INTERVAL, OPT_INTERVAL_DEF));
	}
}
