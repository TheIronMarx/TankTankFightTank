package com.tanktankfighttank.ui;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.tanktankfighttank.R;
import com.tanktankfighttank.game_engine.FiniteGrid;
import com.tanktankfighttank.game_engine.GameManager;
import com.tanktankfighttank.game_engine.LocationGetter;
import com.tanktankfighttank.game_engine.Unit;

public class GameField extends MapActivity{
	
	private MapView mapView;
	private TextView textMovesLeft;
	private Button buttonEndTurn;
	
	private MapController mapController;
	private Projection projection;
	private LocationGetter locationGetter;
	private GameManager gameManager;
	private GeoPoint anchorGeoPoint;
	private FiniteGrid grid;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.game_field);
		
		mapView = (MapView) findViewById(R.id.mapGame);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		
		mapController = mapView.getController();
		mapController.setZoom(17);
		
		locationGetter = new LocationGetter(this, mapController);
		anchorGeoPoint = new GeoPoint(
				(int) (locationGetter.getCurrentBestLocation().getLatitude() * 1E6),
				(int) (locationGetter.getCurrentBestLocation().getLongitude() * 1E6));
		
		gameManager = new GameManager(this, getResources(), anchorGeoPoint);
		
		grid = gameManager.getGrid();
		
		mapView.getOverlays().add(new GridOverlay());
		mapView.getOverlays().add(new UnitOverlay());
		
		projection = mapView.getProjection();
		
		textMovesLeft = (TextView) findViewById(R.id.textMovesLeft);
		textMovesLeft.setText("Moves left:  " + GameManager.MAX_MOVES);
		
		buttonEndTurn = (Button) findViewById(R.id.buttonEndTurn);
		buttonEndTurn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gameManager.endTurn();
				Toast.makeText(GameField.this, "endTurn pressed", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	class UnitOverlay extends Overlay{
		
		private Paint mPaint;
		private Bitmap hostBaseBitmap, guestBaseBitmap, hostTankBitmap, guestTankBitmap;
		
		public UnitOverlay(){
			mPaint = new Paint();
			mPaint.setDither(true);
			
			hostBaseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.base_host, null);
			guestBaseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.base_guest, null);
			hostTankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_host, null);
			guestTankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_guest, null);
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			float meters = GameManager.scale / 10;
			double degrees = (double) (meters * 1E6 / 111325); // Convert the meters into degrees
			int dimension = (int) projection.metersToEquatorPixels(meters); // Dimension to scale the pictures down to
			
			if (dimension > 0) {
				Bitmap scaledHostBase = Bitmap.createScaledBitmap(hostBaseBitmap, dimension, dimension, true);
				Bitmap scaledGuestBase = Bitmap.createScaledBitmap(guestBaseBitmap, dimension, dimension, true);
				Bitmap scaledHostTank= Bitmap.createScaledBitmap(hostTankBitmap, dimension, dimension, true);
				Bitmap scaledGuestTank= Bitmap.createScaledBitmap(guestTankBitmap, dimension, dimension, true);
				
				if(!shadow){
					for(int y = 0; y < GameManager.MAX_Y_GRID; y++){
						for(int x = 0; x < GameManager.MAX_X_GRID; x++){
							if(grid.isOccupied(x, y)){
								int xCoord = x * GameManager.scale + anchorGeoPoint.getLongitudeE6();
								int yCoord = y * GameManager.scale + anchorGeoPoint.getLatitudeE6();
								
								int wCoord = (int) (xCoord + GameManager.scale / 2 - degrees / 2); //Corner x value of the bitmap to be drawn
								int zCoord = (int) (yCoord + GameManager.scale / 2 + degrees / 2); //Corner y value of the bitmap to be drawn
								
								Point point = new Point();
								projection.toPixels(new GeoPoint(zCoord, wCoord), point);
								
	//							Bitmap bitmap = Bitmap.createScaledBitmap(grid.get(x, y).getUnitBitmap(),
	//									width, height, true);
								
								Unit unit = grid.get(x, y);
								if(unit.getClass().getSimpleName().equals("Base")){
									if(unit.getAlignment() == 0){
										canvas.drawBitmap(scaledHostBase, point.x, point.y, mPaint);
									} else if(unit.getAlignment() == 1){
										canvas.drawBitmap(scaledGuestBase, point.x, point.y, mPaint);
									}
								} else if(unit.getClass().getSimpleName().equals("Tank")){
									if(unit.getAlignment() == 0){
										canvas.drawBitmap(scaledHostTank, point.x, point.y, mPaint);
									} else if(unit.getAlignment() == 1){
										canvas.drawBitmap(scaledGuestTank, point.x, point.y, mPaint);
									}
								}
							}
						}
					}
				}
			}
		}

		@Override
		public boolean onTap(GeoPoint geoPoint, MapView mapView) {			
			// Finds the difference between where the player clicked and
			// where the anchorGeoPoint is. If this is within the bounds
			// of the grid, then convert that to the grid position for
			// the FiniteGrid's array. If the click is outside of the
			// grid, indicate by setting x and y equal to -1.
			double xDif = geoPoint.getLongitudeE6() - anchorGeoPoint.getLongitudeE6();
			double yDif = geoPoint.getLatitudeE6() - anchorGeoPoint.getLatitudeE6();
			int x, y;
			
			if (xDif >= 0 && yDif >= 0 && xDif < GameManager.MAX_X_GRID * GameManager.scale && yDif < GameManager.MAX_Y_GRID * GameManager.scale)
			{
				x = (int) (xDif / GameManager.scale);
				y = (int) (yDif / GameManager.scale);
				gameManager.act(x,y);
			}
			else
			{
				x = -1;
				y = -1;
			}
			
			
			Log.d("tank", "Grid position corresponds to: x = " + x + " y = " + y);
			return true;
		}
	}
	
	/**GridOverlay is responsible for drawing the grid on the MapView, including
	 * the squares that are highlighted when you select a Unit
	 */
	class GridOverlay extends Overlay{
		
		private GeoPoint[][] gridPoints; // A 2D array storing the GeoPoints of the grid coordinates
		private Paint mPaint;
		
		public GridOverlay(){
			gridPoints = new GeoPoint[GameManager.MAX_Y_GRID + 1][GameManager.MAX_X_GRID + 1];
			
			// Populate the gridPoints array by adding the iterator times GameManager.scale
			for(int i = 0; i <= GameManager.MAX_Y_GRID; i++){
				for(int j = 0; j <= GameManager.MAX_X_GRID; j++){
					gridPoints[i][j] = new GeoPoint(
							anchorGeoPoint.getLatitudeE6() + (GameManager.scale * i),
							anchorGeoPoint.getLongitudeE6() + (GameManager.scale * j));
				}
			}
			
			mPaint = new Paint();
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			if(!shadow){
				// These Points are used to translate lat/long to pixels on the MapView
				Point point1 = new Point();
				Point point2 = new Point();
//				Point point3 = new Point();
				
				// Draw the horizontal lines
				for(int i = 0; i <= GameManager.MAX_Y_GRID; i++){
					projection.toPixels(gridPoints[i][0], point1);
					projection.toPixels(gridPoints[i][GameManager.MAX_X_GRID], point2);

					mPaint.setColor(Color.BLACK);
					mPaint.setStrokeWidth(3);
					
					// Draw the underlying black line
					canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
					
					mPaint.setColor(Color.WHITE);
					mPaint.setStrokeWidth(1);

					// Draw the overlying white line
					canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
				}
				
				// Draw the vertical lines
				for(int i = 0; i <= GameManager.MAX_X_GRID; i++){
					projection.toPixels(gridPoints[0][i], point1);
					projection.toPixels(gridPoints[GameManager.MAX_Y_GRID][i], point2);

					mPaint.setColor(Color.BLACK);
					mPaint.setStrokeWidth(3);
					
					canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
					
					mPaint.setColor(Color.WHITE);
					mPaint.setStrokeWidth(1);

					canvas.drawLine(point1.x, point1.y, point2.x, point2.y, mPaint);
				}

				// Work in progress...  Draw the highlight around a selected unit
				if(gameManager.getActiveUnit()[0] != -1 && gameManager.getActiveUnit()[1] != -1){
//					Path highlightPath = new Path();
//					int x = gameManager.getActiveUnit()[0];
//					int y = gameManager.getActiveUnit()[1];
////					Log.d("tank", "GridOverlay:  calculating highlight for selected unit at ("
////							+ x + ", " + y + ")");
//					
//					// Find the initial point from which to start
//					for(int q = -GameManager.MAX_TANK_MOVES; q < 1; q++){
//						if(x+q >= 0 && x+q < GameManager.MAX_X_GRID+1){
//							projection.toPixels(gridPoints[y][x+q], point1);
//							highlightPath.moveTo(point1.x, point1.y);
//							break;
//						}
//					}
//					
//					// First loop calculates half the highlightPath
//					int j = 1;
//					for(int i = -GameManager.MAX_TANK_MOVES; i <= GameManager.MAX_TANK_MOVES; i++){
//						if(i < 1){
//							j--;
//							if((x+i >= 0 && x+i < GameManager.MAX_X_GRID+1)
//									&& (y+j >= 0 && y+j < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+j][x+i], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							if((x+i+1 >= 0 && x+i+1 < GameManager.MAX_X_GRID+1)
//									&& (y+j >= 0 && y+j < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+j][x+i+1], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							
//							//Draw a straight line across the bottom if near the edge
//							if((x+i >= 0 && x+i < GameManager.MAX_X_GRID+1)
//									&& y+j < 0){
//								projection.toPixels(gridPoints[0][x+i], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							if((x+i+1 >= 0 && x+i+1 < GameManager.MAX_X_GRID+1)
//									&& y+j < 0){
//								projection.toPixels(gridPoints[0][x+i+1], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							//Draw a straight line across the top if near the edge
//							if((x+i >= 0 && x+i < GameManager.MAX_X_GRID+1)
//									&& y+j > GameManager.MAX_Y_GRID){
//								projection.toPixels(gridPoints[GameManager.MAX_Y_GRID][x+i], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							if((x+i+1 >= 0 && x+i+1 < GameManager.MAX_X_GRID+1)
//									&& y+j > GameManager.MAX_Y_GRID){
//								projection.toPixels(gridPoints[GameManager.MAX_Y_GRID][x+i+1], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//						}
//						else{
//							j++;
//							if((x+i >= 0 && x+i < GameManager.MAX_X_GRID+1)
//									&& (y+j >= 0 && y+j < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+j][x+i], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							if((x+i+1 >= 0 && x+i+1 < GameManager.MAX_X_GRID+1)
//									&& (y+j >= 0 && y+j < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+j][x+i+1], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							//Draw a straight line across the bottom if near the edge
//							if((x+i >= 0 && x+i < GameManager.MAX_X_GRID+1)
//									&& y+j < 0){
//								projection.toPixels(gridPoints[0][x+i], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//						}
//					}
//					
//					// Second loop calculates the other half of the highlightPath
//					int b = 0;
//					for(int a = GameManager.MAX_TANK_MOVES+1; a > -GameManager.MAX_TANK_MOVES; a--){
//						if(a > 0){
//							b++;
//							if((x+a >= 0 && x+a < GameManager.MAX_X_GRID+1)
//									&& (y+b >= 0 && y+b < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+b][x+a], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							if((x+a-1 >= 0 && x+a-1 < GameManager.MAX_X_GRID+1)
//									&& (y+b >= 0 && y+b < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+b][x+a-1], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//						}
//						else{
//							b--;
//							if((x+a >= 0 && x+a < GameManager.MAX_X_GRID+1)
//									&& (y+b >= 0 && y+b < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+b][x+a], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							if((x+a-1 >= 0 && x+a-1 < GameManager.MAX_X_GRID+1)
//									&& (y+b >= 0 && y+b < GameManager.MAX_Y_GRID+1)){
//								projection.toPixels(gridPoints[y+b][x+a-1], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//							//Draw a straight line across the top if near the edge
//							if((x+a >= 0 && x+a < GameManager.MAX_X_GRID+1)
//									&& y+b > GameManager.MAX_Y_GRID+1){
//								projection.toPixels(gridPoints[GameManager.MAX_Y_GRID][x+a], point1);
//								highlightPath.lineTo(point1.x, point1.y);
//							}
//						}
//					}
//					
//					mPaint.setColor(Color.parseColor("#AA0000FF"));
//					mPaint.setStrokeWidth(3);
//					mPaint.setStrokeJoin(Join.ROUND);
//					
//					canvas.drawPath(highlightPath, mPaint);
					
					//PUT IT HERE
					//Maximum number of possible positions to highlight = 2n^2 + 2n + 1
					//First value is y coordinate, second is x coordinate
					ArrayList<int[]> highlightCoords = new ArrayList<int[]>(0);
					int x = gameManager.getActiveUnit()[0];
					int y = gameManager.getActiveUnit()[1];
					int w = 0;
					//z = y coordinate, j = x coordinate
					for (int z = y + GameManager.MAX_TANK_MOVES; z >= y - GameManager.MAX_TANK_MOVES; z--){
						for (int j = x - w; j <= x + w; j++){
							if (j >= 0 && j < GameManager.MAX_X_GRID && z >= 0 && z < GameManager.MAX_Y_GRID){
								highlightCoords.add(new int[] { z, j });
							}
						}
						if(z > y){
							w++;
						} else {
							w--;
						}
					}
					
					mPaint.setColor(Color.parseColor("#AA0000FF"));
					for (int z = 0; z < highlightCoords.size(); z++){
						projection.toPixels(gridPoints[highlightCoords.get(z)[0]][highlightCoords.get(z)[1]], point1);
						projection.toPixels(gridPoints[highlightCoords.get(z)[0]+1][highlightCoords.get(z)[1]+1], point2);
						canvas.drawRect(point1.x, point1.y, point2.x, point2.y, mPaint);
					}
				}				
			}
		}
	}
}
