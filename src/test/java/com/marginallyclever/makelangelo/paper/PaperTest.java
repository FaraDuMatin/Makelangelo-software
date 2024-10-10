package com.marginallyclever.makelangelo.paper;

import com.marginallyclever.util.PreferencesHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PaperTest {
	@BeforeEach
	public void beforeEach() {
		PreferencesHelper.start();
	}

	@Test
	public void testPaperSettingChanges() {
		Paper a = new Paper();
		a.loadConfig();
		double w = a.getPaperWidth();
		double h = a.getPaperHeight();
		a.setPaperSize(w/2,h/2,0,0);
		a.saveConfig();
		Paper b = new Paper();
		b.loadConfig();
		Assertions.assertEquals(w/2, b.getPaperWidth());
		Assertions.assertEquals(h/2, b.getPaperHeight());
		a.setPaperSize(w,h,0,0);
		a.saveConfig();
		// TODO: this is a potentially destructive change if the test fails.
	}

	//@Test(expected = [Erreur.Exception]

	@Test
	public void testPaperLocation() {
		Paper a = new Paper();
		a.setPaperSize(200,100,0,0);
		a.setPaperMargin(0.9);

		Rectangle2D.Double rect = a.getMarginRectangle();
		Assertions.assertEquals(180,rect.getWidth());
		Assertions.assertEquals(90,rect.getHeight());
		Assertions.assertEquals(-90,rect.getMinX());
		Assertions.assertEquals(-45,rect.getMinY());
		Assertions.assertEquals(90,rect.getMaxX());
		Assertions.assertEquals(45,rect.getMaxY());
		Assertions.assertEquals(0,a.getCenterX());
		Assertions.assertEquals(0,a.getCenterY());

		a.setPaperSize(200,100,50,100);
		rect = a.getMarginRectangle();
		Assertions.assertEquals(180,rect.getWidth());
		Assertions.assertEquals(90,rect.getHeight());
		Assertions.assertEquals(-90,rect.getMinX());
		Assertions.assertEquals(-45,rect.getMinY());
		Assertions.assertEquals(90,rect.getMaxX());
		Assertions.assertEquals(45,rect.getMaxY());
		Assertions.assertEquals(50,a.getCenterX());
		Assertions.assertEquals(100,a.getCenterY());
	}

	@Test
	public void testGetPaperWidth() {
		Paper paper = new Paper(); // Create a new Paper object

		// Default width should be 420 mm (as defined by DEFAULT_WIDTH)
		double expectedWidth = 420;
		Assertions.assertEquals(expectedWidth, paper.getPaperWidth(), 0.001, "The paper width should be 420mm.");

		// Test after setting a new paper size
		paper.setPaperSize(500, 700, 0, 0);
		expectedWidth = 500;
		Assertions.assertEquals(expectedWidth, paper.getPaperWidth(), 0.001, "The paper width should be 500mm.");
	}

	@Test
	public void testGetPaperHeight() {
		Paper paper = new Paper();
		// Default height
		double expectedHeight = Paper.DEFAULT_HEIGHT;
		Assertions.assertEquals(expectedHeight, paper.getPaperHeight(), 0.001, "Default paper height should be " + expectedHeight + "mm.");

		// After setting a new size
		paper.setPaperSize(500, 700, 0, 0);
		Assertions.assertEquals(700, paper.getPaperHeight(), 0.001, "Paper height should be 700mm.");
	}



}
