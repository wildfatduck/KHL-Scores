package ru.javverwocky.khl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import android.util.Log;

public class URLDownloader {
	private static final String URL_RESULTS = "http://online.khl.ru/online/";

	public static final String URL_STANDINGS_CHAMP = "http://www.khl.ru/standings/";
	public static final String URL_STANDINGS_CONF = "http://www.khl.ru/standings/conference185/";
	public static final String URL_STANDINGS_DIVS = "http://www.khl.ru/standings/division185/";

	
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
			URLConnection conn = urlToLoad.openConnection();
			if ("gzip".equals(conn.getHeaderField("content-encoding"))) {
				reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream()), "windows-1251"));
			} else {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-1251"));
			}
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
