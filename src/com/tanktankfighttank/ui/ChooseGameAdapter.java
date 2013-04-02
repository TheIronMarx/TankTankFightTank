package com.tanktankfighttank.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tanktankfighttank.R;

public class ChooseGameAdapter extends ArrayAdapter<String>{
	
	private Context context;
	private LayoutInflater inflater;

	public ChooseGameAdapter(Context context) {
		super(context, R.layout.choose_game_item);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Creates a given view based on game chosen.
	 *
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View root = inflater.inflate(R.layout.choose_game_item, parent, false);
		
		TextView name = (TextView) root.findViewById(R.id.textGameName);
		name.setText(getItem(position));
		
		Button pickGame = (Button) root.findViewById(R.id.buttonPickGame);
		pickGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.tanktankfighttank.ui.GameField");
				intent.putExtra("game_name", getItem(position));
				context.startActivity(intent);
			}
		});
		
		return root;
	}
}
