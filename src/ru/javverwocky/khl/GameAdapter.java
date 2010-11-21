package ru.javverwocky.khl;

import java.util.List;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import ru.javverwocky.khl.data.Game;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GameAdapter extends BaseAdapter {
	private final Context context;
	private final List<Object> games;

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
			if (convertView != null && !(convertView instanceof TableLayout)) {
                view = convertView;
            } else {
                view = View.inflate(context, R.layout.header, null);
            }
			view.setClickable(false);
			TextView text = (TextView) view.findViewById(R.id.header);
			text.setText((String) games.get(position));
			text.setClickable(false);
		} else {
			if (convertView != null && convertView instanceof TableLayout) {
                view = convertView;
            } else {
               view = View.inflate(context, R.layout.game, null);
            }

            Game game = (Game) games.get(position);
			boolean isGoing = game.getDetailsLink() != null && !game.getTime().equals("матч завершен");

			TextView text = (TextView) view.findViewById(R.id.homeTeam);
			text.setText(game.getHomeTeam());

			text = (TextView) view.findViewById(R.id.awayTeam);
			text.setText(game.getAwayTeam());

			text = (TextView) view.findViewById(R.id.homeTeamScore);
			text.setText(game.getHomeTeamScore());
			if (isGoing) text.setTextColor(Color.GREEN);

			text = (TextView) view.findViewById(R.id.awayTeamScore);
			text.setText(game.getAwayTeamScore());
			if (isGoing) text.setTextColor(Color.GREEN);

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
