package ru.javverwocky.khl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.javverwocky.khl.KHLApplication;

public class GameParser {
	private static final Pattern SCORES_PATTERN = Pattern
			.compile("<b>([А-Яа-я\\s\\.]+)[<>\\w\\s=\"/:.А-Яа-я\\[\\]]*<ul>|<li>\\d+\\.\\s*<b>([А-Яа-я\\d\\s\\-\\w]+)</b>(\\s*<b>(\\d+):(\\d+)(<sup>([А-Яа-я\\w]+)</sup>)*</b>)*,([А-Яа-я\\d\\s\\-:\\w]+)(,\\s\\[<a href='(\\d+)\\.(\\w+)'>)*|\\[([\\d\\-]+)\\]</h3>");

	private static final Pattern GKA_PATTERN = Pattern.compile("var gkA\\s*=\\s*(\\d*);");
	private static final Pattern GKB_PATTERN = Pattern.compile("var gkB\\s*=\\s*(\\d*);");
	private static final Pattern TEAMNAMES_PATTERN = Pattern.compile("var teamNames\\s*=\\s*(\\{.*\\});");
	private static final Pattern TEAMPLAYERS_PATTERN = Pattern.compile("var teamPlayers\\s*=\\s*(\\{.*\\});");
	private static final Pattern GAMEPLAYERS_PATTERN = Pattern.compile("var gamePlayers\\s*=\\s*(\\{.*\\});");
	private static final Pattern EVENTS_PATTERN = Pattern.compile("var olEvents\\s*=\\s*(\\[.*\\]);");

	/**
	 * Parse livescores html page
	 * 
	 * @param results
	 *            html page content
	 * @return list of rscores
	 */
	public static List<Object> parseGameResults(String results) {
		List<Object> games = new ArrayList<Object>();

		if (results.contains("нет трансляций")) {
			games.add("нет трансляций");
		} else {

			Matcher matcher = SCORES_PATTERN.matcher(results);

			while (matcher.find()) {
				String title = matcher.group(12) != null ? matcher.group(12) : matcher.group(1);
				if (matcher.group(12) != null) {
                    KHLApplication.DATE_FROM_KHL = matcher.group(12);
                } else if (matcher.group(1) != null) {
					games.add(matcher.group(1).trim());
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

	/**
	 * Parse details of game
	 * 
	 * @param timeline
	 *            html page content
	 * @return list of game events
	 */
	public static List<TimelineItem> parseTimeline(String timeline) {
		int goalieA;
		int goalieB;

		try {
			goalieA = Integer.valueOf(extractGroup(GKA_PATTERN, timeline));
		} catch (NumberFormatException nfe) {
			goalieA = -1;
		}

		try {
			goalieB = Integer.valueOf(extractGroup(GKB_PATTERN, timeline));
		} catch (NumberFormatException nfe) {
			goalieB = -1;
		}

		String teamNames = extractGroup(TEAMNAMES_PATTERN, timeline);
		String teamPlayers = extractGroup(TEAMPLAYERS_PATTERN, timeline);
		String gamePlayers = extractGroup(GAMEPLAYERS_PATTERN, timeline);
		String events = extractGroup(EVENTS_PATTERN, timeline);

		List<TimelineItem> res = null;

		try {
			res = eventsToList(goalieA, goalieB, teamNames, teamPlayers, gamePlayers, events);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Convert json data of game events to list of events
	 * 
	 * @param goalieA
	 *            id of goalkeeper of first team
	 * @param goalieB
	 *            id of goalkeeper of second team
	 * @param _teamNames
	 *            team names as json object
	 * @param _teamPlayers
	 *            team players data as json object
	 * @param _gamePlayers
	 *            players data for game as json object
	 * @param _events
	 *            game events as json array
	 * @return list of game events
	 * @throws JSONException error of JSON processing
	 */
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
			String[] eventProps = events.getString(i).split("\\|", -1);

			if (eventProps[0].equals("co")) {
				items.add(new TimelineItem(TimelineItem.TYPE_COMMENT,
						eventProps[1].length() == 0 ? "-" : eventProps[1], eventProps[2]));
			} else if (eventProps[0].equals("go")) {
				String goalPlayer = getPlayerName(gamePlayers, teamPlayers, eventProps[2], eventProps[3]);
				String assistPlayer = getPlayerName(gamePlayers, teamPlayers, eventProps[2], eventProps[4]);
				String assistPlayer2 = getPlayerName(gamePlayers, teamPlayers, eventProps[2], eventProps[5]);

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
				String penaltyPlayer = getPlayerName(gamePlayers, teamPlayers, eventProps[2], eventProps[3]);
				String shootPlayer = "";

				boolean isPenalty = Integer.valueOf(eventProps[4]) > 0;
				if (!isPenalty) {
					getPlayerName(gamePlayers, teamPlayers, reverseTeam(eventProps[2]), eventProps[6]);
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
					String goalieAName = getPlayerName(gamePlayers, teamPlayers, "A", String.valueOf(goalieA));
					String goalieBName = getPlayerName(gamePlayers, teamPlayers, "B", String.valueOf(goalieB));

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
				String goalieAName = getPlayerName(gamePlayers, teamPlayers, "A", String.valueOf(goalieA));
				String goalieBName = getPlayerName(gamePlayers, teamPlayers, "B", String.valueOf(goalieB));

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

	private static String extractGroup(Pattern pattern, String text) {
		Matcher matcher = pattern.matcher(text);
		String res = "";
		if (matcher.find()) {
			res = matcher.group(1);
		}

		return res;
	}

	private static String getPlayerName(JSONObject gamePlayers, JSONObject teamPlayers, String teamId, String playerId) {
		if(playerId.length() == 0) return "";

		String name;
		try {
			name = teamPlayers.getJSONObject(teamId).getJSONArray(
					gamePlayers.getJSONObject(teamId).getJSONArray(playerId).getString(0)).getString(0);
		} catch (JSONException e) {
			name = "[нет в составе]";
		}

		return name;
	}
}
