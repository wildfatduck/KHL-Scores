package ru.javverwocky.khl;

import java.util.List;

import ru.javverwocky.khl.data.TimelineItem;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimelineAdapter extends BaseAdapter {
	private final Context context;
	private final List<TimelineItem> timeline;

	public TimelineAdapter(Context context, List<TimelineItem> timeline) {
		super();
		this.context = context;
		this.timeline = timeline;
	}

	@Override
	public int getCount() {
		return timeline.size();
	}

	@Override
	public Object getItem(int position) {
		return timeline.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.timeline, parent, false);
		}

		TextView text = (TextView) view.findViewById(R.id.time);
		text.setText(timeline.get(position).getTime());
		switch (timeline.get(position).getType()) {
		case TimelineItem.TYPE_GOAL:
			text.setTextColor(Color.GREEN);
			break;
		case TimelineItem.TYPE_PENALTY:
			text.setTextColor(Color.RED);
			break;
		case TimelineItem.TYPE_TIMEEVENTS:
			text.setTextColor(Color.BLUE);
			break;
		case TimelineItem.TYPE_GOALIE:
			text.setTextColor(Color.MAGENTA);
			break;
		case TimelineItem.TYPE_TIMEOUT:
			text.setTextColor(Color.YELLOW);
			break;
		default:
			text.setTextColor(Color.LTGRAY);
		}

		text = (TextView) view.findViewById(R.id.event);
		text.setText(timeline.get(position).getComment());
		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}
