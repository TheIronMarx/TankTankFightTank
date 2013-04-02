package com.tanktankfighttank.game_engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;
import com.tanktankfighttank.R;
import com.tanktankfighttank.ui.TankDialog;

public class GameManager {
	
	public static final int scale = 500;
	public static final int MAX_Y_GRID = 14;
	public static final int MAX_X_GRID = 12;
	public static final int MAX_BASE_HITPOINTS = 3;
	public static final int MAX_TANK_HITPOINTS = 3;
	public static final int MAX_MOVES = 8;
	public static final int MAX_TANK_MOVES = 4;
	public static final int MAX_FIRE_DISTANCE = 2;
	
//	private Context context;
	private Activity activity;
	private int activePlayer; //0 or 1, obviously, do not refactor into boolean
	private int[] activeUnit = new int[2]; //Coords of active unit if one exists, else (-1,-1)
	private Unit[] hostTankList;
	private Unit[] guestTankList;
	//private Base hostBase;
	//private Base guestBase;
	private FiniteGrid grid;
	//private int[][] moves;
	private Gson gson = new Gson();
	private int currentMove; //Current move number for a given turn
	
	public GameManager(Activity activity, Resources res, GeoPoint anchorGeoPoint){
		this.activity = activity;
		
		grid = new FiniteGrid(MAX_X_GRID, MAX_Y_GRID, anchorGeoPoint);
		activePlayer = 0;
		activeUnit[0] = -1;
		activeUnit[1] = -1;
		
		//moves=new int[MAX_MOVES][4];
		currentMove = 0;
		
		Bitmap hostBaseBitmap = BitmapFactory.decodeResource(res, R.drawable.base_host);
		Bitmap hostTankBitmap = BitmapFactory.decodeResource(res, R.drawable.tank_host);

		
		Bitmap guestTankBitmap = BitmapFactory.decodeResource(res, R.drawable.tank_guest);
		Bitmap guestBaseBitmap = BitmapFactory.decodeResource(res, R.drawable.base_guest);
		
		//Host units
		grid.put(MAX_X_GRID - 1, 0, new Base(hostBaseBitmap, MAX_BASE_HITPOINTS, 0));
		grid.put(MAX_X_GRID - 1, 1, new Tank(hostTankBitmap, MAX_TANK_HITPOINTS, 0));
		grid.put(MAX_X_GRID - 2, 1, new Tank(hostTankBitmap, MAX_TANK_HITPOINTS, 0));
		grid.put(MAX_X_GRID - 2, 0, new Tank(hostTankBitmap, MAX_TANK_HITPOINTS, 0));
				
		//Guest units
		grid.put(0, MAX_Y_GRID - 1, new Base(guestBaseBitmap, MAX_BASE_HITPOINTS, 1));
		grid.put(1, MAX_Y_GRID - 1, new Tank(guestTankBitmap, MAX_TANK_HITPOINTS, 1));
		grid.put(1, MAX_Y_GRID - 2, new Tank(guestTankBitmap, MAX_TANK_HITPOINTS, 1));
		grid.put(0, MAX_Y_GRID - 2, new Tank(guestTankBitmap, MAX_TANK_HITPOINTS, 1));


		hostTankList=new Unit[3];
		guestTankList=new Unit[3];
		
		hostTankList[0]=grid.get(MAX_X_GRID - 1, 1);
		hostTankList[1]=grid.get(MAX_X_GRID - 2, 1);
		hostTankList[2]=grid.get(MAX_X_GRID - 2, 0);
		
		guestTankList[0]=grid.get(1, MAX_Y_GRID - 1);
		guestTankList[1]=grid.get(1, MAX_Y_GRID - 2);
		guestTankList[2]=grid.get(0, MAX_Y_GRID - 2);
	}
	
	public void act(int x, int y) {
		int movesMade; //The number of moves the current act takes
		if(activeUnit[0] != -1 && activeUnit[1] != -1) { //If there already is an active unit
			if(grid.isOccupied(x, y)) {
				if(grid.get(x, y).getAlignment() == activePlayer) {
					Log.d("Act","Active unit, occupied square");
					grid.get(activeUnit[0], activeUnit[1]).setSelected(false);
					
					activeUnit[0] = x;
					activeUnit[1] = y;
					if(grid.get(activeUnit[0], activeUnit[1]).getClass().getSimpleName().equals("Tank")) {
						TankDialog tankDialog = new TankDialog(activity, this, grid.get(activeUnit[0], activeUnit[1]));
						tankDialog.show();
					}
					grid.get(x, y).act();
				} else if (grid.get(activeUnit[0], activeUnit[1]).getClass().getSimpleName().equals("Tank") && ((Tank) grid.get(activeUnit[0], activeUnit[1])).canFire()) { //THIS CURRENTLY BREAKS NEUTRAL ITEMS -- BOTH ALIGNMENT -1 AND 0 WILL FALL INTO THIS BRANCH
					Log.d("Act", "Active unit, enemy square. Prepare for war!");
					int fireDistance = Math.abs(activeUnit[0]-x)+Math.abs(activeUnit[1]-y);
					if (fireDistance <= MAX_FIRE_DISTANCE) {
						grid.get(activeUnit[0], activeUnit[1]).move();
						((Tank) grid.get(activeUnit[0], activeUnit[1])).fire();
						if (grid.get(x, y).attack() <= 0) {
							if(grid.get(x, y).getClass().getSimpleName().equals("Base")) {
								AlertDialog.Builder builder = new AlertDialog.Builder(activity);
								builder.setMessage("Spoils to the victor!")
								.setTitle("Victory!")
								.setPositiveButton("Acknowledge victory", new OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										activity.finish();
									}
								})
								.setCancelable(false)
								.create().show();
							}
							grid.remove(x, y);
						}
					}				
				}
			} else { //Unoccupied grid pressed after active unit
				Log.d("Act","Active unit, unoccupied square");
				movesMade = Math.abs(activeUnit[0]-x)+Math.abs(activeUnit[1]-y);
				
				if(currentMove+movesMade <= MAX_MOVES && grid.get(activeUnit[0], activeUnit[1]).isValidMove() && movesMade <= MAX_TANK_MOVES) {
						grid.get(activeUnit[0], activeUnit[1]).move();
						grid.put(x, y, grid.get(activeUnit[0], activeUnit[1]));
						grid.get(activeUnit[0], activeUnit[1]).setSelected(false);
						grid.remove(activeUnit[0], activeUnit[1]);
						currentMove+=movesMade;
						activeUnit[0] = -1;
						activeUnit[1] = -1;
				}
				else{
					Log.d("Act","User attempted to perform more than the allowed amount of moves");
				}

			}
		} else { //Else there is no active unit
			if(grid.isOccupied(x, y)) {
				if(grid.get(x, y).getAlignment() == activePlayer) {
					Log.d("Act","No active unit, occupied square");
					activeUnit[0] = x;
					activeUnit[1] = y;
					if(grid.get(activeUnit[0], activeUnit[1]).getClass().getSimpleName().equals("Tank")){
						TankDialog tankDialog = new TankDialog(activity, this, grid.get(activeUnit[0], activeUnit[1]));
						tankDialog.show();
					}
					grid.get(x, y).act();
				} else {
					Log.d("Act","No active unit, illegal unit to make active Active player= "+activePlayer);
				}
			} else {
				Log.d("Act","No active unit, unoccupied square");
			}
		}
	}
	
	
	
	
	
	public void endTurn(){
		currentMove=0;
		if(activePlayer==0){
			for(int i=0;i<hostTankList.length;i++){
				hostTankList[i].endTurn();
			}
			activePlayer=1;
			Log.d("act","Active player should be 1");
		}else{
			for(int i=0;i<guestTankList.length;i++){
				guestTankList[i].endTurn();
			}
			activePlayer=0;
		}
		/*Object[] state = new Object[2];
		state[0] = grid;
		state[1] = activePlayer;
		gson.toJson(state);*/
	}
	
	
	
	public int[] getActiveUnit() {
		return activeUnit;
	}

	public Unit[] getHostTankList() {
		return hostTankList;
	}

	public Unit[] getGuestTankList() {
		return guestTankList;
	}
	
	public FiniteGrid getGrid() {
		return grid;
	}
	
	class CallDatabaseTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://samsserverspace.com/script.php");
			
			try{
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("name", "value"));
				// ... keep adding as many values as you want
				
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				@SuppressWarnings("unused")
				HttpResponse response = httpClient.execute(httpPost);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}
	}
}
