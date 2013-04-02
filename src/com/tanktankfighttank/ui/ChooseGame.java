package com.tanktankfighttank.ui;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.tanktankfighttank.R;

public class ChooseGame extends ListActivity{
	private ChooseGameAdapter adapter;
	
	/**
	 * onCreate Override for the game selection menu.
	 *
	 * @param savedInstanceState Last state of the game.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_game);
		
		adapter = new ChooseGameAdapter(this);
		for(int i = 0; i < 10; i++){
			adapter.add("Game " + i);
		}
		
		TextView header = (TextView) ((LayoutInflater) getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.choose_game_header,
						getListView(), false);
		
		getListView().addHeaderView(header);
		getListView().setDividerHeight(1);
		getListView().setAdapter(adapter);
	}
}
