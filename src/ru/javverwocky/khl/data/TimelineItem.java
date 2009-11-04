package ru.javverwocky.khl.data;

public class TimelineItem {
	public static final int TYPE_COMMENT = 0;
	public static final int TYPE_GOAL = 1;
	public static final int TYPE_PENALTY = 2;
	public static final int TYPE_TIMEEVENTS = 3;
	public static final int TYPE_GOALIE = 4;
	public static final int TYPE_TIMEOUT = 5;
	
	private String time;
	private String comment;
	private int type;

	public TimelineItem(int type, String time, String comment) {
		this.time = time;
		this.comment = comment;
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
