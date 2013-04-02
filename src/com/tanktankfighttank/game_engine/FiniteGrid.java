package com.tanktankfighttank.game_engine;
import com.google.android.maps.GeoPoint;

public class FiniteGrid implements Grid{
	
	private static Unit[][] occupants;
	
	/**
	 * Constructor for the FiniteGrid. At heart, it is simply a collected of units which is arrayed and
	 * displayed as the combat field.
	 * 
	 * @param cols The maximum number of columns
	 * @param rows The maximum number of rows
	 * @param anchorPoint The most recently accessed GPS location stored on the phone.
	 */
	public FiniteGrid(int cols, int rows, GeoPoint anchorPoint ) {
		if (rows <= 0)
            throw new IllegalArgumentException("rows <= 0");
        if (cols <= 0)
            throw new IllegalArgumentException("cols <= 0");
        occupants = new Unit[cols][rows];
	}
	
	/**
	 * Places a new Unit on the grid.
	 * 
	 * @param x The Cartesian x value of the grid to place the Unit on
	 * @param y The Cartesian y value of the grid to place the Unit on
	 * @param u The unit which will be placed on the location (x, y)
	 */
	public void put(int x, int y, Unit u) {
		if(isOccupied(x, y))
			throw new IllegalArgumentException("Location invalid!");
		else{
			occupants[x][y]=u;
			
		}
	}
	
	/**
	 * Removes a unit from the grid
	 * 
	 * @param x The Cartesian x value of the Unit to remove from the grid.
	 * @param y The Cartesian y value of the Unit to remove from the grid.
	 */
	public void remove(int x, int y) {
			occupants[x][y] = null;
	}
	
	/**
	 * Returns the Unit at a given location on the grid.
	 * 
	 * @param x The Cartesian x value of the Unit to retrieve.
	 * @param y The Cartesian y value of the Unit to retrieve.
	 * @return The Unit requested.
	 */
	public Unit get(int x, int y) {
		return occupants[x][y];
	}
	
	/**
	 * A boolean accessor determining if a location on the grid is occupied by a unit
	 * 
	 * @param x The Cartesian x value in question
	 * @param y The Cartesian y value in question
	 * @return True if the location on the Grid is occupied by Unit.
	 */
	public boolean isOccupied(int x, int y) {
		if(occupants[x][y]!=null){
			return true;
		} else {
			return false;
		}
	}
}
