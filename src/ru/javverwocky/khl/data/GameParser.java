package ru.javverwocky.khl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameParser {
	private static final String SCORES_PATTERN = ">([А-Яа-я\\s\\.]+)<ul>|<li>\\d+\\.\\s*<b>([А-Яа-я\\d\\s\\-\\w]+)</b>(\\s*<b>(\\d+):(\\d+)(<sup>([А-Яа-я\\w]+)</sup>)*</b>)*,([А-Яа-я\\d\\s\\-:\\w]+)(,\\s\\[<a href='(\\d+)\\.(\\w+)'>)*";

	public static GameParser get() {
		return new GameParser();
	}

	public List<Object> parseGameResults(String results) {
		List<Object> games = new ArrayList<Object>();
		Pattern p = Pattern.compile(SCORES_PATTERN);
		Matcher matcher = p.matcher(results);

		while (matcher.find()) {
			String title = matcher.group(1);
			if (title != null) {
				games.add(title.trim());
			} else {
				Game game = new Game();

				String[] teams = matcher.group(2).split(" - ", 3);
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
				game.setHomeTeamScore(matcher.group(4) != null ? matcher.group(3).trim() : "");
				game.setAwayTeamScore(matcher.group(5) != null ? matcher.group(4).trim() : "");
				game.setExtra(matcher.group(7) != null ? matcher.group(6).trim() : "");
				game.setTime(matcher.group(8).trim());
				game.setDetailsLink(matcher.group(10) != null ? matcher.group(10) + "." + matcher.group(11) : null);
				games.add(game);
			}
		}

		return games;
	}

}
