package ru.javverwocky.khl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class URLDownloader {
	private static final String URL_RESULTS = "http://online.khl.ru/online/";
	
	public static String loadResults() {
		return urlToString(URL_RESULTS);
	}
	
	public static String loadGameDetails(String gameUrl) {
		return urlToString(URL_RESULTS + gameUrl);
	}
	
	private static String urlToString(String url) {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			URL urlToLoad = new URL(url);
			reader = new BufferedReader(new InputStreamReader(urlToLoad.openStream(), "windows-1251"));
			String ln;
			while ((ln = reader.readLine()) != null) {
				sb.append(ln);
			}
		} catch (MalformedURLException e) {
			Log.e("KHLScores", "Going to invalid url", e);
		} catch (IOException e) {
			Log.e("KHLScores", "Error while reading file from internet", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.e("KHLScores", "Error while closing stream", e);
				}
			}
		}
		return sb.toString();
	}
}
