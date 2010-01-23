package ru.javverwocky.khl.data;

import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class StandingsParser extends DefaultHandler {
	private static final int IN_HEADER = 1;
	private static final int IN_TABLE = 2;
	private static final int IN_OTHER = 0;
	private static final int AFTER_HEADER = 3;
	private static final int IN_ROW = 4;
	private static final int IN_CELL = 5;
	private int currentState;
	private String currentHeader;
	private LinkedList<String> currentRow;
	private LinkedList<LinkedList<String>> currentTable;
	private HashMap<String, LinkedList<LinkedList<String>>> tables;

	public HashMap<String, LinkedList<LinkedList<String>>> parse(String url) {
		tables = new HashMap<String, LinkedList<LinkedList<String>>>();
		try {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse(url, this);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getMessage());
		}

		return tables;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		switch (currentState) {
		case IN_HEADER:
			currentHeader = String.valueOf(ch, start, length);
			break;
		case IN_CELL:
			if (currentTable.size() == 0 && currentRow.size() == 0) currentRow.add(" ");
			currentRow.add(String.valueOf(ch, start, length));
			break;
		default:
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (currentState == IN_HEADER && localName.length() == 2
				&& (localName.startsWith("h") || localName.startsWith("H"))) {
			currentState = AFTER_HEADER;
		} else if (currentState == IN_TABLE && localName.equalsIgnoreCase("table")) {
			tables.put(currentHeader, currentTable);
			currentState = IN_OTHER;
		} else if (currentState == IN_ROW && localName.equalsIgnoreCase("tr")) {
			currentTable.add(currentRow);
			currentState = IN_TABLE;
		} else if (currentState == IN_CELL && (localName.equalsIgnoreCase("td") || localName.equalsIgnoreCase("th"))) {
			currentState = IN_ROW;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.length() == 2 && (localName.startsWith("h") || localName.startsWith("H"))) {
			currentState = IN_HEADER;
		} else if (currentState == AFTER_HEADER && localName.equalsIgnoreCase("table")) {
			String tableClass = attributes.getValue("", "class");
			if (tableClass != null
					&& (tableClass.equalsIgnoreCase("championship_table") || tableClass
							.equalsIgnoreCase("division_table"))) {
				currentState = IN_TABLE;
				currentTable = new LinkedList<LinkedList<String>>();
			} else
				currentState = IN_OTHER;
		} else if (currentState == IN_TABLE && localName.equalsIgnoreCase("tr")) {
			currentRow = new LinkedList<String>();
			currentState = IN_ROW;
		} else if (currentState == IN_CELL
				|| (currentState == IN_ROW && (localName.equalsIgnoreCase("td") || localName.equalsIgnoreCase("th")))) {
			currentState = IN_CELL;
		} else {
			currentState = IN_OTHER;
		}
	}
}
