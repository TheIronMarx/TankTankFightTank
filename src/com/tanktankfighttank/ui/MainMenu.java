package com.tanktankfighttank.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tanktankfighttank.R;
import com.tanktankfighttank.game_engine.LocationGetter;

public class MainMenu extends Activity {

	private LocationGetter locationGetter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		Button playGame = (Button) findViewById(R.id.buttonPlayGame);
		playGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				locationGetter.stopListening();
				startActivity(new Intent("com.tanktankfighttank.ui.ChooseGame"));
			}
		});
		
		Button settings = (Button) findViewById(R.id.buttonSettings);
		settings.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MainMenu.this, "Settings pressed", Toast.LENGTH_SHORT).show();
				locationGetter.stopListening();
			}
		});
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		locationGetter = new LocationGetter(this);
		
		if(!locationGetter.isGPSEnabled()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false)
				.setMessage("GPS is not enabled.  GPS is required for " +
						"maximum tank fighting experience.")
				.setPositiveButton("Enable it", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MainMenu.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create().show();
		} else{
			locationGetter.startListening();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationGetter.stopListening();
	}
}
