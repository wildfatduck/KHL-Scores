package ru.javverwocky.khl;

import java.util.Date;

import ru.javverwocky.khl.data.Game;
import android.app.Application;

public class KHLApplication extends Application {
	public static Game CURRENT_GAME;
    public static String DATE_FROM_KHL;
	public static Date currentDate = new Date();
}
