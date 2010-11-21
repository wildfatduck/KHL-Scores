package ru.javverwocky.khl;

import java.util.Calendar;
import java.util.Date;

import ru.javverwocky.khl.data.Game;
import android.app.Application;

public class KHLApplication extends Application {
	public static Game CURRENT_GAME;
    public static String DATE_FROM_KHL;
	public static Calendar currentDate = Calendar.getInstance();
}
