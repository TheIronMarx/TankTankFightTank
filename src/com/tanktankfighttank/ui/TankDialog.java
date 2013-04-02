package com.tanktankfighttank.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tanktankfighttank.R;
import com.tanktankfighttank.game_engine.GameManager;
import com.tanktankfighttank.game_engine.Unit;

public class TankDialog extends Dialog implements OnClickListener{

	public TankDialog(Activity activity, GameManager gameManager, Unit unit) {
		super(activity);
		setContentView(R.layout.tank_popup);
		
		TextView textHitPoints = (TextView) findViewById(R.id.textHitPoints);
		textHitPoints.setText("HP: " + unit.getHitPoints());
		
		TextView textHasMoved = (TextView) findViewById(R.id.textHasMoved);
		if(unit.isValidMove()){
			textHasMoved.setText("Hasn't moved");
		} else{
			textHasMoved.setText("Has moved");
		}
		
		Button buttonMove = (Button) findViewById(R.id.buttonMove);
		buttonMove.setOnClickListener(this);
		
		Button buttonFire = (Button) findViewById(R.id.buttonFire);
		buttonFire.setOnClickListener(this);
		
		Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonMove:
			break;
		case R.id.buttonFire:
			break;
		case R.id.buttonCancel:
			this.dismiss();
			break;
		}
	}
}
