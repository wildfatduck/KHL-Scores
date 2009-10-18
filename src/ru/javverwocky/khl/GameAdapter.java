package ru.javverwocky.khl;

import java.util.List;

import ru.javverwocky.khl.data.Game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GameAdapter extends BaseAdapter {
	private Context context;
	private List<Game> games;

	public GameAdapter(Context context, List<Game> matches) {
		super();
		this.context = context;
		this.games = matches;
	}

	@Override
	public int getCount() {
		return games.size();
	}

	@Override
	public Object getItem(int position) {
		return games.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View matchView = convertView;
		if (convertView == null) {
			matchView = LayoutInflater.from(context).inflate(R.layout.game, parent, false);
		}

		TextView text = (TextView) matchView.findViewById(R.id.homeTeam);
		text.setText(games.get(position).getHomeTeam());

		text = (TextView) matchView.findViewById(R.id.awayTeam);
		text.setText(games.get(position).getAwayTeam());

		text = (TextView) matchView.findViewById(R.id.homeTeamScore);
		text.setText(games.get(position).getHomeTeamScore());

		text = (TextView) matchView.findViewById(R.id.awayTeamScore);
		text.setText(games.get(position).getAwayTeamScore());

		text = (TextView) matchView.findViewById(R.id.time);
		text.setText(games.get(position).getTime());

		text = (TextView) matchView.findViewById(R.id.extraTime);
		if (games.get(position).getExtra().length() == 0 && games.get(position).getDetailsLink() != null) {
			text.setText(">");
		} else {
			text.setText(games.get(position).getExtra());
		}

		return matchView;
	}
}
