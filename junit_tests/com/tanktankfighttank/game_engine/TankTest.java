package com.tanktankfighttank.game_engine;

import static org.junit.Assert.*;

import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tanktankfighttank.R;
import com.tanktankfighttank.ui.GameField;

public class TankTest {
	GameField field = new GameField();
	Bitmap bitmap = BitmapFactory.decodeResource(field.getResources(), R.drawable.tank_host, null);
	
	@Test
	public void testAct() {
		Tank t = new Tank(bitmap, 3, 0);
		assertFalse(t.isSelected());
		t.act();
		assertTrue(t.isSelected());
	}

	@Test
	public void testEndTurn() {
		Tank t = new Tank(bitmap, 3, 0);
		t.fire();
		assertFalse(t.canFire());
		t.endTurn();
		assertTrue(t.canFire());
	}

	@Test
	public void testAttack() {
		Tank t = new Tank(bitmap, 3, 0);
		assertEquals(2, t.attack());
	}

	@Test
	public void testCanFire() {
		Tank t = new Tank(bitmap, 3, 0);
		assertTrue(t.canFire());
		t.fire();
		assertFalse(t.canFire());
	}

	@Test
	public void testFire() {
		Tank t = new Tank(bitmap, 3, 0);
		assertTrue(t.canFire());
		t.fire();
		assertFalse(t.canFire());
	}

}
