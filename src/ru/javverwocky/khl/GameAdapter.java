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
	private List<Object> games;

	public GameAdapter(Context context, List<Object> games) {
		super();
		this.context = context;
		this.games = games;
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
		View view;
		if (games.get(position) instanceof String) {
			view = LayoutInflater.from(context).inflate(R.layout.header, parent, false);
			view.setClickable(false);
			TextView text = (TextView)view.findViewById(R.id.header);
			text.setText((String)games.get(position));
			text.setClickable(false);
		} else {
			Game game = (Game) games.get(position);
			view = LayoutInflater.from(context).inflate(R.layout.game, parent, false);

			TextView text = (TextView) view.findViewById(R.id.homeTeam);
			text.setText(game.getHomeTeam());

			text = (TextView) view.findViewById(R.id.awayTeam);
			text.setText(game.getAwayTeam());

			text = (TextView) view.findViewById(R.id.homeTeamScore);
			text.setText(game.getHomeTeamScore());

			text = (TextView) view.findViewById(R.id.awayTeamScore);
			text.setText(game.getAwayTeamScore());

			text = (TextView) view.findViewById(R.id.time);
			text.setText(game.getTime());

			text = (TextView) view.findViewById(R.id.extraTime);
			if (game.getExtra().length() == 0 && game.getDetailsLink() != null) {
				text.setText(">");
			} else {
				text.setText(game.getExtra());
			}
		}

		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		return games.get(position) instanceof Game;
	}
}
