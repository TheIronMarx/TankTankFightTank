package com.tanktankfighttank.game_engine;

import static org.junit.Assert.*;

import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tanktankfighttank.R;
import com.tanktankfighttank.ui.GameField;

public class UnitTest {
	GameField field = new GameField();
	Bitmap bitmap = BitmapFactory.decodeResource(field.getResources(), R.drawable.tank_host, null);

	@Test
	public void testAct() {
		Unit u = new Unit(bitmap, 3, 0, true);
		u.act();
		assertTrue(u.isSelected());
	}

	@Test
	public void testGetUnitBitmap() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertSame(bitmap, u.getUnitBitmap());
	}

	@Test
	public void testGetHitPoints() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertEquals(3, u.getHitPoints());
	}

	@Test
	public void testGetAlignment() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertEquals(0, u.getAlignment());
	}

	@Test
	public void testIsValidMove() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertTrue(u.isValidMove());
		u.move();
		assertFalse(u.isValidMove());
	}

	@Test
	public void testMove() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertTrue(u.isValidMove());
		u.move();
		assertFalse(u.isValidMove());
	}

	@Test
	public void testEndTurn() {
		Unit u = new Unit(bitmap, 3, 0, true);
		u.move();
		assertFalse(u.isValidMove());
		u.endTurn();
		assertTrue(u.isValidMove());
	}

	@Test
	public void testSetSelected() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertFalse(u.isSelected());
		u.setSelected(true);
		assertTrue(u.isSelected());
		u.setSelected(false);
		assertFalse(u.isSelected());
	}

	@Test
	public void testIsSelected() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertFalse(u.isSelected());
		u.act();
		assertTrue(u.isSelected());
	}

	@Test
	public void testAttack() {
		Unit u = new Unit(bitmap, 3, 0, true);
		assertEquals(2, u.attack());
	}

}
