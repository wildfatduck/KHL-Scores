package ru.javverwocky.khl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameParser {
	private static final String SCORES_PATTERN = "<li>\\d+\\.\\s*<b>([А-Яа-я\\d\\s\\-\\w]+)</b>(\\s*<b>(\\d+):(\\d+)(<sup>([А-Яа-я\\w]+)</sup>)*</b>)*,([А-Яа-я\\d\\s\\-:\\w]+)(,\\s\\[<a href='(\\d+)\\.(\\w+)'>)*";

	public static GameParser get() {
		return new GameParser();
	}

	public List<Game> parseGameResults(String results) {
		List<Game> games = new ArrayList<Game>();
		Pattern p = Pattern.compile(SCORES_PATTERN);
		Matcher matcher = p.matcher(results);

		while (matcher.find()) {
			Game game = new Game();

			String[] teams = matcher.group(1).split(" - ", 3);
			if (teams.length == 3) {
				if (teams[0].startsWith("ЦСКА")) {
					game.setHomeTeam(teams[0].trim() + " - " + teams[1].trim());
					game.setAwayTeam(teams[2].trim());
				} else {
					game.setHomeTeam(teams[0].trim());
					game.setAwayTeam(teams[1].trim() + " - " + teams[2].trim());
				}
			} else {
				game.setHomeTeam(teams[0].trim());
				game.setAwayTeam(teams[1].trim());
			}
			game.setHomeTeamScore(matcher.group(3) != null ? matcher.group(3).trim() : "");
			game.setAwayTeamScore(matcher.group(4) != null ? matcher.group(4).trim() : "");
			game.setExtra(matcher.group(6) != null ? matcher.group(6).trim() : "");
			game.setTime(matcher.group(7).trim());
			game.setDetailsLink(matcher.group(9) != null ? matcher.group(9) + "." + matcher.group(10) : null);
			games.add(game);
		}

		return games;
	}

}
