package com.marginallyclever.makelangelo.plotter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//import com.jogamp.opengl.GL2;
import com.marginallyclever.convenience.Point2D;
import com.marginallyclever.makelangelo.plotter.plottersettings.PlotterSettings;
//import com.marginallyclever.makelangelo.turtle.TurtleMove;
//import com.marginallyclever.makelangelo.turtle.MovementType;
import com.marginallyclever.util.PreferencesHelper; // Import PreferencesHelper

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

public class PlotterTest {

    private Plotter plotter;
    private PlotterEventListener listenerMock;

    @BeforeEach
    public void setUp() {
        PreferencesHelper.start(); // Initialize PreferencesHelper
        plotter = new Plotter();
        listenerMock = mock(PlotterEventListener.class);
        plotter.addPlotterEventListener(listenerMock);
    }


    @Test
    public void testRaisePen() {
        plotter.lowerPen(); // Ensure pen is down
        plotter.raisePen();
        assertTrue(plotter.getPenIsUp(), "Pen should be up after calling raisePen()");
    }






    @Test
    public void testLowerPen() {
        plotter.raisePen(); // Ensure pen is up
        plotter.lowerPen();
        assertFalse(plotter.getPenIsUp(), "Pen should be down after calling lowerPen()");
    }



    @Test
    public void testSetPos() {
        plotter.setPos(100.0, 200.0);
        Point2D pos = plotter.getPos();
        assertEquals(100.0, pos.x, 0.0001, "X position should be 100.0");
        assertEquals(200.0, pos.y, 0.0001, "Y position should be 200.0");
        verify(listenerMock).plotterEvent(any(PlotterEvent.class));
    }

    /**
     * Test setting motors engaged state.
     */
    @Test
    public void testSetMotorsEngaged() {
        plotter.setMotorsEngaged(true);
        assertTrue(plotter.getMotorsEngaged(), "Motors should be engaged");
        verify(listenerMock).plotterEvent(any(PlotterEvent.class));

        plotter.setMotorsEngaged(false);
        assertFalse(plotter.getMotorsEngaged(), "Motors should be disengaged");
        verify(listenerMock, times(2)).plotterEvent(any(PlotterEvent.class));
    }

    /**
     * Test re-initialization.
     */
    @Test
    public void testReInit() {
        plotter.findHome();
        plotter.setMotorsEngaged(true);
        plotter.reInit();
        assertFalse(plotter.getMotorsEngaged(), "Motors should be disengaged after reInit()");
        assertFalse(plotter.getDidFindHome(), "Plotter should not have found home after reInit()");
    }



}
