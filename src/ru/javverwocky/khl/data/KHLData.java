package ru.javverwocky.khl.data;

import java.util.ArrayList;
import java.util.HashMap;

public class KHLData {
	public static HashMap<String, String> penalties = new HashMap<String, String>();
	public static ArrayList<String> rbm = new ArrayList<String>();
	static {
		penalties.put("11", "Атака в область головы и шеи");
		penalties.put("1", "Атака игрока не владеющего шайбой");
		penalties.put("10", "Атака сзади");
		penalties.put("21", "Бросок клюшки");
		penalties.put("19", "Грубость");
		penalties.put("23", "Выброс шайбы");
		penalties.put("39", "Дисциплинарный штраф");
		penalties.put("40", "Дисциплинарный до конца игры");
		penalties.put("41", "Драка");
		penalties.put("26", "Задержка игры");
		penalties.put("4", "Задержка клюшки соперника");
		penalties.put("2", "Задержка клюшкой");
		penalties.put("3", "Задержка руками");
		penalties.put("20", "Исключительная грубость");
		penalties.put("27", "Игра со сломанной клюшкой");
		penalties.put("14", "Колющий удар");
		penalties.put("42", "Матч-штраф");
		penalties.put("32", "Малый скамеечный штраф");
		penalties.put("31", "Незаконное и опасное снаряжение");
		penalties.put("9", "Неправильная атака");
		penalties.put("30", "Нарушение численного состава");
		penalties.put("8", "Опасная игра высоко поднятой клюшкой");
		penalties.put("33", "Оскорбление судей и неспортивное поведение");
		penalties.put("36", "Отказ начать игру");
		penalties.put("12", "Отсечение");
		penalties.put("5", "Подножка");
		penalties.put("37", "Предупреждение инфекций");
		penalties.put("29", "Покидание скамейки");
		penalties.put("25", "Сдвиг ворот");
		penalties.put("43", "Симуляция");
		penalties.put("7", "Толчок на борт");
		penalties.put("6", "Толчок клюшкой");
		penalties.put("13", "Удар клюшкой");
		penalties.put("44", "Удар концом клюшки");
		penalties.put("16", "Удар коленом");
		penalties.put("15", "Удар локтем");
		penalties.put("17", "Удар ногой");
		penalties.put("35", "Физический контакт со зрителем");
		penalties.put("38", "Штрафы вратаря");
		
		rbm.add("В равенстве");
		rbm.add("В большинстве");
		rbm.add("В меньшинстве");
		rbm.add("Буллит");
	}
}
