package com.tanktankfighttank.game_engine;

import android.graphics.Bitmap;

import com.google.android.maps.GeoPoint;

public class Tank extends Unit {
	
	private boolean canFire = true;
	
	public Tank(Bitmap unitBitmap, int hit, int align){
		super(unitBitmap, hit, align, true);
	}
	
//	public void act() {
//		selected = true;
//	}
	
	public int attack() {
		hitPoints--;
		return hitPoints;
	}
	
	public boolean canFire() {
		return canFire;
	}
	
	public void fire() {
		canFire = false;
	}
	
	@Override
	public void endTurn(){
		canFire = true;
		super.endTurn();
	}

}
