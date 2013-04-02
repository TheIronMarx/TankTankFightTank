package com.tanktankfighttank.game_engine;
import android.graphics.Bitmap;

/**
 * 
 * Everything that exists on the grid, excluding empty grid spots extend the Unit class.
 *
 */
public class Unit {
	private boolean moveable;
	private Bitmap unitBitmap;
	protected int hitPoints;
	private int alignment = - 1; //Defaults to no one's control
	private boolean selected;
	private boolean canMove;
	

	/**
	 * Constructor for Unit class; in particular, the base
	 *
	 * @param unitBitMap The name of the .pngs of the unit being instantiated
	 * @param hit The starting hitpoints this unit will have
	 * @param align Which player this unit belongs to. 0 is host, 1 is guest, -1 is neither/both/neutral
	 */
	public Unit(Bitmap unitBitmap, int hit, int align){
		this.unitBitmap = unitBitmap;
		hitPoints = hit;
		selected = false;
		alignment = align;
		moveable=false;
	}
	
	/**
	 * Alt constructor for Unit class. 
	 *
	 * @param unitBitMap The name of the .pngs of the unit being instantiated
	 * @param hit The starting hitpoints this unit will have
	 * @param align Which player this unit belongs to. 0 is host, 1 is guest, -1 is neither/both/neutral
	 * @param move Determines the movability of the unit
	 */
	public Unit(Bitmap unitBitmap, int hit, int align, boolean move){
		this.unitBitmap = unitBitmap;
		hitPoints = hit;
		selected = false;
		alignment = align;
		moveable=move;
		canMove=true;
	}
	
	/** 
	 * Acting is what units do when they're selected, typically through Android's onTap() method
	 *
	 */
	public void act() {
		selected = true;
	}
	
	/**
	 * Accessor for the image displayed on the screen representing the Unit
	 *
	 * @return Bitmap of the Unit
	 */
	public Bitmap getUnitBitmap(){
		return unitBitmap;
	}
	
	/**
	 * Accessor for the current hitpoints of the Unit
	 *
	 * @return Integer value of the hitpoints
	 */
	public int getHitPoints(){
		return hitPoints;
	}
	
	/**
	 * Accessor for the player that controls the Unit
	 * 
	 * @ The value of the alignment for the player that owns the unit, 0 is host, 1 is guest, -1 is neutral/neither
	 */
	public int getAlignment() {
		return alignment;
	}
	
	/**
	 * Determines if the attempted move of the Unit is legal.
	 * 
	 * @return True if an Unit can move.
	 */
	public boolean isValidMove(){
		if(moveable){
			return canMove;
		}else
			return false;
	}
	/**
	 * Sets the canMove flag to false, therefore disallowing further moves by this tank
	*/
	public void move(){
		canMove=false;
	}
	/**
	 * Resets necessary data for the Unit to be prepared for the next turn.
	 * 
	 */
	public void endTurn(){
		canMove=true;
	}
	/**
	 * Mutator to activate this unit as the current active Unit.
	 * 
	 * @param selected It's pretty much only going to be true, anyway.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Accessor for obtaining weather or not the Unit is the active Unit.
	 * 
	 * @return The true or false value of whether the unit is currently selected and the active unit.
	 */
	public boolean isSelected(){
		return selected;
	}
	
	public int attack() {
		hitPoints--;
		return hitPoints;
	}
	
	
}
