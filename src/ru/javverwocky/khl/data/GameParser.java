package ru.javverwocky.khl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameParser {
	private static final Pattern SCORES_PATTERN = Pattern
			.compile(">([А-Яа-я\\s\\.]+)<ul>|<li>\\d+\\.\\s*<b>([А-Яа-я\\d\\s\\-\\w]+)</b>(\\s*<b>(\\d+):(\\d+)(<sup>([А-Яа-я\\w]+)</sup>)*</b>)*,([А-Яа-я\\d\\s\\-:\\w]+)(,\\s\\[<a href='(\\d+)\\.(\\w+)'>)*");
	private static final Pattern GKA_PATTERN = Pattern.compile("var gkA\\s*=\\s*(\\d*);");
	private static final Pattern GKB_PATTERN = Pattern.compile("var gkB\\s*=\\s*(\\d*);");
	private static final Pattern TEAMNAMES_PATTERN = Pattern.compile("var teamNames\\s*=\\s*(\\{.*\\});");
	private static final Pattern TEAMPLAYERS_PATTERN = Pattern.compile("var teamPlayers\\s*=\\s*(\\{.*\\});");
	private static final Pattern GAMEPLAYERS_PATTERN = Pattern.compile("var gamePlayers\\s*=\\s*(\\{.*\\});");
	private static final Pattern EVENTS_PATTERN = Pattern.compile("var olEvents\\s*=\\s*(\\[.*\\]);");

	public static List<Object> parseGameResults(String results) {
		List<Object> games = new ArrayList<Object>();

		if (results.contains("нет трансляций")) {
			games.add("нет трансляций");
		} else {

			Matcher matcher = SCORES_PATTERN.matcher(results);

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
					game.setHomeTeamScore(matcher.group(4) != null ? matcher.group(4).trim() : "");
					game.setAwayTeamScore(matcher.group(5) != null ? matcher.group(5).trim() : "");
					game.setExtra(matcher.group(7) != null ? matcher.group(7).trim() : "");
					game.setTime(matcher.group(8).trim());
					game.setDetailsLink(matcher.group(10) != null ? matcher.group(10) + "." + matcher.group(11) : null);
					games.add(game);
				}
			}
		}

		return games;
	}

	public static List<TimelineItem> parseTimeline(String timeline) {
		int goalieA = -1;
		int goalieB = -1;

		Matcher matcher = GKA_PATTERN.matcher(timeline);
		if (matcher.find()) {
			goalieA = Integer.valueOf(matcher.group(1));
		}

		matcher = GKB_PATTERN.matcher(timeline);
		if (matcher.find()) {
			goalieB = Integer.valueOf(matcher.group(1));
		}

		String teamNames = "";
		matcher = TEAMNAMES_PATTERN.matcher(timeline);
		if (matcher.find()) {
			teamNames = matcher.group(1);
		}

		String teamPlayers = "";
		matcher = TEAMPLAYERS_PATTERN.matcher(timeline);
		if (matcher.find()) {
			teamPlayers = matcher.group(1);
		}

		String gamePlayers = "";
		matcher = GAMEPLAYERS_PATTERN.matcher(timeline);
		if (matcher.find()) {
			gamePlayers = matcher.group(1);
		}

		String events = "";
		matcher = EVENTS_PATTERN.matcher(timeline);
		if (matcher.find()) {
			events = matcher.group(1);
		}

		List<TimelineItem> res = null;

		try {
			res = eventsToList(goalieA, goalieB, teamNames, teamPlayers, gamePlayers, events);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	// TODO: getTeam, getPlayer
	private static List<TimelineItem> eventsToList(int goalieA, int goalieB, String _teamNames, String _teamPlayers,
			String _gamePlayers, String _events) throws JSONException {
		List<TimelineItem> items = new ArrayList<TimelineItem>();

		JSONObject teamNames = new JSONObject(_teamNames);
		JSONObject teamPlayers = new JSONObject(_teamPlayers);
		JSONObject gamePlayers = new JSONObject(_gamePlayers);
		JSONArray events = new JSONArray(_events);

		int goalA = 0;
		int goalB = 0;
		for (int i = 0; i < events.length(); i++) {
			if (events.getString(i).startsWith("go")) {
				String[] eventProps = events.getString(i).split("\\|");
				if (eventProps[2].equals("A"))
					goalA++;
				else
					goalB++;
			}
		}

		for (int i = 0; i < events.length(); i++) {
			String[] eventProps = events.getString(i).split("\\|");

			if (eventProps[0].equals("co")) {
				items.add(new TimelineItem(TimelineItem.TYPE_COMMENT,
						eventProps[1].length() == 0 ? "-" : eventProps[1], eventProps[2]));
			} else if (eventProps[0].equals("go")) {
				String gamePlayer;
				String goalPlayer = "";
				String assistPlayer = "";
				String assistPlayer2 = "";

				try {
					gamePlayer = gamePlayers.getJSONObject(eventProps[2]).getJSONArray(eventProps[3]).getString(0);
					goalPlayer = teamPlayers.getJSONObject(eventProps[2]).getJSONArray(gamePlayer).getString(0);
				} catch (JSONException e) {
					goalPlayer = "[нет в составе]";
				}

				if (eventProps[4].length() > 0) {
					try {
						gamePlayer = gamePlayers.getJSONObject(eventProps[2]).getJSONArray(eventProps[4]).getString(0);
						assistPlayer = teamPlayers.getJSONObject(eventProps[2]).getJSONArray(gamePlayer).getString(0);
					} catch (JSONException e) {
						assistPlayer = "[нет в составе]";
					}
				}

				if (eventProps[5].length() > 0) {
					try {
						gamePlayer = gamePlayers.getJSONObject(eventProps[2]).getJSONArray(eventProps[5]).getString(0);
						assistPlayer2 = teamPlayers.getJSONObject(eventProps[2]).getJSONArray(gamePlayer).getString(0);
					} catch (JSONException e) {
						assistPlayer2 = "[нет в составе]";
					}
				}

				items.add(new TimelineItem(TimelineItem.TYPE_GOAL, eventProps[1], "Гол: "
						+ goalA
						+ ":"
						+ goalB
						+ ". "
						+ eventProps[3]
						+ "."
						+ goalPlayer
						+ ". "
						+ (eventProps[4].length() == 0 ? ""
								: ("("
										+ eventProps[4]
										+ "."
										+ assistPlayer
										+ (eventProps[5].length() == 0 ? "" : ", " + eventProps[5] + "."
												+ assistPlayer2) + "). ")) + teamNames.getString(eventProps[2]) + ". "
						+ KHLData.rbm.get(Integer.valueOf(eventProps[6]))
						+ (eventProps[7].equals("1") ? ", в пустые ворота" : "")));

				if (eventProps[2].equals("A"))
					goalA--;
				else
					goalB--;
			} else if (eventProps[0].equals("pn")) {
				String gamePlayer;
				String penaltyPlayer = "";
				String shootPlayer = "";

				try {
					gamePlayer = gamePlayers.getJSONObject(eventProps[2]).getJSONArray(eventProps[3]).getString(0);
					penaltyPlayer = teamPlayers.getJSONObject(eventProps[2]).getJSONArray(gamePlayer).getString(0);
				} catch (JSONException e) {
					penaltyPlayer = "[нет в составе]";
				}

				boolean isPenalty = Integer.valueOf(eventProps[4]) > 0;
				if (!isPenalty) {
					try {
						gamePlayer = gamePlayers.getJSONObject(reverseTeam(eventProps[2])).getJSONArray(eventProps[6])
								.getString(0);
						shootPlayer = teamPlayers.getJSONObject(reverseTeam(eventProps[2])).getJSONArray(gamePlayer)
								.getString(0);
					} catch (JSONException e) {
						shootPlayer = "[нет в составе]";
					}
				}

				items.add(new TimelineItem(TimelineItem.TYPE_PENALTY, eventProps[1],
						(eventProps[4].equals("0") ? "Буллит. Наказан " : "Удаление. ")
								+ (eventProps[3].equals("0") ? "Командный штраф" : eventProps[3] + '.' + penaltyPlayer)
								+ " ("
								+ teamNames.getString(eventProps[2])
								+ "). "
								+ (isPenalty ? eventProps[4] + "мин. " : "")
								+ (KHLData.penalties.get(eventProps[5]) != null ? KHLData.penalties.get(eventProps[5])
										: "[???]")
								+ (isPenalty ? "" : "Штрафной бросок выполнил " + eventProps[6] + "." + shootPlayer
										+ " (" + teamNames.getString(reverseTeam(eventProps[2])))));
			} else if (eventProps[0].equals("ga")) {
				int periodNum = Integer.valueOf(eventProps[3]);

				if (eventProps[2].equals("0") && periodNum == 1) {
					String gamePlayer;
					String goalieAName = "";
					String goalieBName = "";

					try {
						gamePlayer = gamePlayers.getJSONObject("A").getJSONArray("" + goalieA).getString(0);
						goalieAName = teamPlayers.getJSONObject("A").getJSONArray(gamePlayer).getString(0);
					} catch (JSONException e) {
						goalieAName = "[нет в составе]";
					}

					try {
						gamePlayer = gamePlayers.getJSONObject("B").getJSONArray("" + goalieB).getString(0);
						goalieBName = teamPlayers.getJSONObject("B").getJSONArray(gamePlayer).getString(0);
					} catch (JSONException e) {
						goalieBName = "[нет в составе]";
					}

					items.add(new TimelineItem(TimelineItem.TYPE_COMMENT, "-", "В воротах " + goalieA + "."
							+ goalieAName + " (" + teamNames.getString("A") + ") - " + goalieB + "." + goalieBName
							+ " (" + teamNames.getString("B") + ")"));

				}

				items
						.add(new TimelineItem(TimelineItem.TYPE_TIMEEVENTS, eventProps[1],
								(eventProps[2].equals("0") ? "Начало " : "Окончание ")
										+ (periodNum == 0 ? "игры" : (periodNum < 4 ? periodNum + "-го периода"
												: "овертайма"))));
			} else if (eventProps[0].equals("gc")) {
				String gamePlayer;
				String goalieAName = "";
				String goalieBName = "";

				try {
					gamePlayer = gamePlayers.getJSONObject("A").getJSONArray("" + goalieA).getString(0);
					goalieAName = teamPlayers.getJSONObject("A").getJSONArray(gamePlayer).getString(0);
				} catch (JSONException e) {
					goalieAName = "[нет в составе]";
				}

				try {
					gamePlayer = gamePlayers.getJSONObject("B").getJSONArray("" + goalieB).getString(0);
					goalieBName = teamPlayers.getJSONObject("B").getJSONArray(gamePlayer).getString(0);
				} catch (JSONException e) {
					goalieBName = "[нет в составе]";
				}

				items.add(new TimelineItem(TimelineItem.TYPE_GOALIE, eventProps[1], "Замена вратаря. "
						+ teamNames.getString("A")
						+ ": "
						+ (eventProps[2].length() == 0 || eventProps[2].equals("0") ? "без вратаря" : "в воротах "
								+ goalieAName)
						+ " - "
						+ teamNames.getString("B")
						+ ": "
						+ (eventProps[3].length() == 0 || eventProps[3].equals("0") ? "без вратаря" : "в воротах "
								+ goalieBName)));
			} else if (eventProps[0].equals("to")) {
				items.add(new TimelineItem(TimelineItem.TYPE_TIMEOUT, eventProps[1], "Таймаут. "
						+ teamNames.getString(eventProps[2])));
			}

		}

		if (items.size() == 0)
			items.add(new TimelineItem(TimelineItem.TYPE_COMMENT, "-", "Текущий счет матча " + goalA + ":" + goalB));

		return items;
	}

	private static String reverseTeam(String team) {
		if (team.equals("A"))
			return "B";
		else
			return "A";
	}
}
