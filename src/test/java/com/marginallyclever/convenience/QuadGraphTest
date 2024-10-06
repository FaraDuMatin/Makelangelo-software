package com.marginallyclever.convenience;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class QuadGraphTest {

    @Test
    public void testConstructorInitialization() {
        QuadGraph quadGraph = new QuadGraph(0.0, 0.0, 10.0, 10.0);

        assertEquals(0.0, quadGraph.bounds.getMinX());
        assertEquals(0.0, quadGraph.bounds.getMinY());
        assertEquals(10.0, quadGraph.bounds.getMaxX());
        assertEquals(10.0, quadGraph.bounds.getMaxY());
        assertTrue(quadGraph.sites.isEmpty(), "Sites list should be empty upon initialization");
        assertNull(quadGraph.children, "Children should be null upon initialization");
    }
}
