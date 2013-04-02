package com.tanktankfighttank.game_engine;

import static org.junit.Assert.*;

import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.maps.GeoPoint;
import com.tanktankfighttank.R;
import com.tanktankfighttank.ui.GameField;

public class FiniteGridTest {

	GameField field = new GameField();
	Bitmap bitmap = BitmapFactory.decodeResource(field.getResources(), R.drawable.tank_host, null);
	Unit u = new Unit(bitmap, 3, 0, true);
	FiniteGrid grid = new FiniteGrid(10, 10, new GeoPoint(100000,100000));
	
	@Test
	public void testPut() {
		grid.put(5, 5, u);
		assertTrue(grid.isOccupied(5, 5));
	}

	@Test
	public void testRemove() {
		grid.remove(5, 5);
		assertFalse(grid.isOccupied(5, 5));
	}

	@Test
	public void testGet() {
		assertNull(grid.get(4, 3));
		grid.put(4, 3, u);
		assertSame(grid.get(4, 3), u);
	}

	@Test
	public void testIsOccupied() {
		assertFalse(grid.isOccupied(5, 5));
		assertTrue(grid.isOccupied(4, 3));
	}

}
