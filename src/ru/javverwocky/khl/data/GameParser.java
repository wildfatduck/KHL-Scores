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

		return games;
	}

	public static List<TimelineItem> parseTimeline(String timeline) {
		//timeline = "<SCRIPT>var gkA=30;\nvar gkB=26;\nvar teamNames = {'A':'Торпедо','B':'Атлант'};\nvar teamPlayers = {'A':{'t_1':['Брюклер Бернд','в'],'t_2':['Лобанов Евгений','в'],'t_5':['Новопашин Виталий','з'],'t_6':['Жуков Валерий','з'],'t_7':['Трощинский Алексей','з'],'t_8':['Заболотнев Андрей','з'],'t_9':['Космачев Дмитрий','з'],'t_10':['Крстев Ангел','з'],'t_17':['Клопов Дмитрий','н'],'t_18':['Галузин Владимир','н'],'t_19':['Линдстрем Юаким','н'],'t_20':['Шахрайчук Вадим','н'],'t_21':['Никитенко Андрей','н'],'t_22':['Крикунов Илья','н'],'t_23':['Варнаков Михаил','н'],'t_24':['Беднарж Ярослав','н'],'t_25':['Александров Виктор','н'],'t_26':['Брендл Павел','н'],'t_27':['Шастин Егор','н'],'t_28':['Косоуров Алексей','н']},'B':{'t_3':['Гербер Мартин','в'],'t_4':['Егоров Алексей','в'],'t_11':['Канарейкин Леонид','з'],'t_12':['Зубарев Андрей','з'],'t_13':['Луома Микко','з'],'t_14':['Хомицкий Вадим','з'],'t_15':['Баландин Михаил','з'],'t_16':['Быков Дмитрий','з'],'t_29':['Ландри Эрик','н'],'t_30':['Мозякин Сергей','н'],'t_31':['Новотны Иржи','н'],'t_32':['Жердев Николай','н'],'t_33':['Петров Олег','н'],'t_34':['Нестеров Александр','н'],'t_35':['Мирнов Игорь','н'],'t_36':['Булис Ян','н'],'t_37':['Семин Дмитрий','н'],'t_38':['Чернов Павел','н'],'t_39':['Глухов Алексей','н'],'t_40':['Кваша Олег','н'],'t_41':['Поснов Андрей','н'],'t_42':['Лазарев Антон','н']}};\nvar gamePlayers = {'A':{'30':['t_1','в','1',''],'41':['t_2','в','0',''],'4':['t_5','з','1',''],'5':['t_6','з','1',''],'8':['t_7','з','1',''],'16':['t_8','з','1',''],'36':['t_9','з','1',''],'73':['t_10','з','1',''],'9':['t_17','н','1',''],'10':['t_18','н','1',''],'11':['t_19','н','1',''],'12':['t_20','н','1',''],'15':['t_21','н','1',''],'17':['t_22','н','1',''],'18':['t_23','н','1',''],'21':['t_24','н','1',''],'23':['t_25','н','1',''],'28':['t_26','н','1',''],'33':['t_27','н','1',''],'34':['t_28','н','1','']},'B':{'26':['t_3','в','1',''],'35':['t_4','в','0',''],'2':['t_11','з','1',''],'3':['t_12','з','1',''],'5':['t_13','з','1',''],'11':['t_14','з','1',''],'39':['t_15','з','1',''],'55':['t_16','з','1',''],'9':['t_29','н','1',''],'10':['t_30','н','1',''],'12':['t_31','н','1',''],'13':['t_32','н','1',''],'14':['t_33','н','1',''],'16':['t_34','н','1',''],'37':['t_35','н','1',''],'38':['t_36','н','1',''],'42':['t_37','н','1',''],'53':['t_38','н','0',''],'57':['t_39','н','1',''],'78':['t_40','н','1',''],'81':['t_41','н','1',''],'90':['t_42','н','1','']}};\nvar newPlayers = {A:{},B:{}};\nvar olEvents = ['ga|19:15|1|0','ga|19:15|1|3','co||Голыброски: Торпедо : 2/31 , Атлант: 8/21;','to|59:53|B','pn|59:53|A|5|2|2||60:00','co|57|Обменялись опасными моментами команды.','go|55:08|A|28|||0|0||||||||||||','co|55|Брендл с кистей в девятку ворот Егорова! ','gc|54:30|30|35','co|52|Атлант активнее стал играть. Заперли торпедовцев в зоне.','go|50:38|A|18|16||0|0||||||||||||','co|51|Смогли торпедовцы распечатать ворота Гербера!','co|48|Атлант играет в полном составе!','co|47|Шахрайчук упускает возможность размочить ворота Гербера!','pn|45:45|B|38|2|5||','co|42|Торпедо играет в полном составе. Атлант не смог создать угрозу у ворот Брюклера за эти две минуты.','pn|40:47|A|73|2|3||','co|41|Крстев потерял клюшку и схватил руками нападающего гостей. ','ga|18:48|0|3','co||Торпедо: 14/31;','co||Атлант: 17/31;','co||Выигрыши/вбрасывания:','co||Атлант: 4/7;','co||Торпедо: 0/12;','co||Голы/броски:','co||Статистика по итогам второго периода:','ga|18:33|1|2','co|36|В полном составе Атлант.','co|35|Минуту в большинстве сыграли торпедовцы. Пока без бросков по воротам.','pn|34:16|B|81|2|8||','go|33:46|B|13|78||0|0||||||||||||','co|33|Красивейшая комбинация гостей и 0-8.','co|31|Атака Торпедо! Линдстрем бросал! Мимо ворот!','to|29:42|A','go|29:42|B|57|||1|0||||||||||||','co|29|Сегодня в ударе Атлант! Гости заставляют забивать даже игроков Торпедо в собственные ворота!','pn|28:56|A|73|2|2||','gc|28:40|30|26','go|27:02|B|42|14|81|0|0||||||||||||','co|27|Одна команда сегодня на площадке.','co|24|Шахрайчук мог с пятака зымыкать прострел! Не повезло Вадиму.','co|24|Редкая атака Торпедо. Варнаков бросал с острого угла. Прямо в Гербера.','go|21:21|B|38|55||0|0||||||||||||','co|22|Одного на штанге Булиса оставили! 0-5!','ga|17:55|0|2','co||Атлант: 13/25;','co||Торпедо: 12/25;','co||Выигрыши/вбрасывания:','co||Атлант: 4/7;','co||Торпедо: 0/8;','co||Голы/броски: ','co||Статистика после первого периода:','ga|17:40|1|1','pn|19:17|A|8|2|2||','co|19|В контратаку убежали гости. Пришлось фолить Трощинскому.','pn|17:47|B|38|2|3||','go|16:32|B|14|81||0|0||||||||||||','co|17|Петрова одного на пятаке оставили! ','co|17|15 секунд Лобанов был сухим.','co|17|Грубейшая ошибка в обороне и 0-3.','gc|16:17|41|26','go|16:17|B|78|57||0|0||||||||||||','co|14|Атлант играет в полном составе.','co|14|Галузин с кистей бросал! Рядом со штангой шайба пролетела!','pn|12:21|B|55|2|2||','co|12|Кваша мог третью забивать. Брюклер выручил.','go|10:14|B|39|10||1|0||||||||||||','co|10|Баландин бросил от синей. 0-2.','pn|10:05|A|5|2|2||','go|08:18|B|12|10||0|0||||||||||||','co|8|Мозякин бросил с кистей от синей. Новотны подставил клюшку.','co|4|Очень активно играют хозяева! ','co|3|Беднарж  бросал! Мимо!','co|2|А вот и ответ гостей. Глухов подставлял клюшку на пятаке!','co|1|На первой минуте Шастин упускает возможность распечатать ворота Гербера! Варнаков отдавал отличный пас на дальнюю штангу!','ga|17:00|0|1','ga|17:00|0|0','co||4 звено: Клопов - Галузин - Александров;','co||3 звено: Космачев - Заболотнев, Крикунов - Беднарж - Косоуров;','co||2 звено: Жуков - Новопашин, Линдстрем - Шахрайчук - Брендл;','co||1 звено: Трощинский - Крстев, Шастин - Никитенко - Варнаков;','co||В воротах Бернд Брюклер;','co||Торпедо (Нижний Новгород):','co||4 звено: Лазарев, Нестеров - Ландри - Мирнов;','co||3 звено: Канарейкин - Зубарев, Поснов - Семин - Петров;','co||2 звено: Хомицкий - Луома, Кваша - Глухов - Жердев;','co||1 звено: Баландин - Быков, Мозякин - Новотны - Булис;','co||В воротах Мартин Гербер;','co||Атлант (Московская область):','co||Составы команд по пятеркам:'];\nvar eventIndex = 0;var scA=0;var scB=0;</SCRIPT><TABLE><TBODY><TR><TD colspan=\"2\">		<DIV style=\"float:left; font-weight:bold;\">			Чемпионат КХЛ Игра №249. Торпедо - Атлант [01-11-2009]		</DIV>		<DIV style=\"float:right; font-size:120%; font-weight:bold\"><A href=\"http://online.khl.ru/online/2009-11-01.html\">К списку трансляций</A></DIV>			</TD></TR><TR style=\"font-size:90%; font-weight:bold; color:gray;\">	<TD>		Место проведения: Дворец спорта Профсоюзов<BR>		Текущий счет матча <SPAN style=\"color:black\">2:8</SPAN>	</TD>	<TD>					Главные судьи: Гусев С. (Серов), <BR>					Линейные судьи: Михаевич С. (С-Петербург), Нестеров А. (С-Петербург)	</TD>";

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

					items.add(new TimelineItem(TimelineItem.TYPE_COMMENT, "-", "В воротах " + goalieA + "." + goalieAName
							+ " (" + teamNames.getString("A") + ") - " + goalieB + "." + goalieBName + " ("
							+ teamNames.getString("B") + ")"));

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
		return items;
	}

	private static String reverseTeam(String team) {
		if (team.equals("A"))
			return "B";
		else
			return "A";
	}
}
