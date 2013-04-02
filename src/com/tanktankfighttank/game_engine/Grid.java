package com.tanktankfighttank.game_engine;
import com.google.android.maps.GeoPoint;

//If you want to return sets of neighbors, whatever, use ArrayList, PLEASE
//import java.util.ArrayList;

//Don't forget that our grid is Cartesian and the bottom left grid is (0,0).
public interface Grid {

	boolean isOccupied(int x, int y);
	void put(int x, int y, Unit u);
	void remove(int x, int y);
	Unit get(int x, int y);
}
