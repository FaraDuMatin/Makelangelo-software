package com.marginallyclever.convenience;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the QuadGraph class.
 */
public class QuadGraphTest {

    private QuadGraph quadGraph;

    @BeforeEach
    public void setUp() {
        // Initialize a QuadGraph with bounds from (0,0) to (10,10)
        quadGraph = new QuadGraph(0.0, 0.0, 10.0, 10.0);
    }

    /**
     * Test the constructor initializes the QuadGraph correctly.
     */
    @Test
    public void testConstructorInitialization() {
        assertEquals(0.0, quadGraph.bounds.getMinX());
        assertEquals(0.0, quadGraph.bounds.getMinY());
        assertEquals(10.0, quadGraph.bounds.getMaxX());
        assertEquals(10.0, quadGraph.bounds.getMaxY());
        assertTrue(quadGraph.sites.isEmpty(), "Sites list should be empty upon initialization");
        assertNull(quadGraph.children, "Children should be null upon initialization");
    }

    /**
     * Test inserting a point within bounds.
     */
    @Test
    public void testInsertPointWithinBounds() {
        Point2D point = new Point2D(5.0, 5.0);

        boolean result = quadGraph.insert(point);

        assertTrue(result, "Insertion should succeed for a point within bounds");
        assertEquals(1, quadGraph.countPoints(), "QuadGraph should contain one point");
        assertTrue(quadGraph.sites.contains(point), "Point should be in the sites list");
    }

    /**
     * Test inserting a point outside bounds.
     */
    @Test
    public void testInsertPointOutsideBounds() {
        Point2D point = new Point2D(15.0, 15.0);

        boolean result = quadGraph.insert(point);

        assertFalse(result, "Insertion should fail for a point outside bounds");
        assertEquals(0, quadGraph.countPoints(), "QuadGraph should contain zero points");
    }

    /**
     * Test inserting multiple points to trigger a split.
     */
    @Test
    public void testInsertPointsTriggerSplit() {
        List<Point2D> points = new ArrayList<>();

        // Insert MAX_POINTS to fill the node
        for (int i = 0; i < 5; i++) {
            Point2D point = new Point2D(i, i);
            quadGraph.insert(point);
            points.add(point);
        }

        // Insert one more point to trigger split
        Point2D extraPoint = new Point2D(2.5, 2.5);
        quadGraph.insert(extraPoint);
        points.add(extraPoint);

        assertNotNull(quadGraph.children, "QuadGraph should have split into children");
        assertEquals(6, quadGraph.countPoints(), "QuadGraph should contain six points");

        // Verify points are distributed in children
        int totalPointsInChildren = 0;
        for (QuadGraph child : quadGraph.children) {
            totalPointsInChildren += child.countPoints();
        }
        assertEquals(6, totalPointsInChildren, "All points should be in the children nodes");
    }

    /**
     * Test searching for an existing point.
     */
    @Test
    public void testSearchExistingPoint() {
        Point2D point = new Point2D(3.0, 3.0);
        quadGraph.insert(point);

        Point2D found = quadGraph.search(point);

        assertNotNull(found, "Search should return a point");
        assertEquals(point, found, "Search should return the exact point inserted");
    }

    /**
     * Test searching for the nearest neighbor when the exact point is not present.
     */
    @Test
    public void testSearchNearestNeighbor() {
        Point2D point1 = new Point2D(2.0, 2.0);
        Point2D point2 = new Point2D(8.0, 8.0);
        quadGraph.insert(point1);
        quadGraph.insert(point2);

        Point2D searchPoint = new Point2D(5.0, 5.0);
        Point2D found = quadGraph.search(searchPoint);

        assertNotNull(found, "Search should return the nearest point");
        assertTrue(found.equals(point1) || found.equals(point2), "Found point should be one of the inserted points");
    }

    /**
     * Test searching in an empty QuadGraph.
     */
    @Test
    public void testSearchInEmptyQuadGraph() {
        Point2D searchPoint = new Point2D(5.0, 5.0);

        Point2D found = quadGraph.search(searchPoint);

        assertNull(found, "Search should return null when QuadGraph is empty");
    }

    /**
     * Test the countPoints method.
     */
    @Test
    public void testCountPoints() {
        assertEquals(0, quadGraph.countPoints(), "Initial point count should be zero");

        quadGraph.insert(new Point2D(1.0, 1.0));
        assertEquals(1, quadGraph.countPoints(), "Point count should be one after one insertion");

        quadGraph.insert(new Point2D(2.0, 2.0));
        quadGraph.insert(new Point2D(3.0, 3.0));
        assertEquals(3, quadGraph.countPoints(), "Point count should be three after three insertions");
    }

    /**
     * Test inserting points on the boundary.
     */
    @Test
    public void testInsertPointOnBoundary() {
        Point2D point = new Point2D(10.0, 10.0);

        boolean result = quadGraph.insert(point);

        assertFalse(result, "Insertion should fail for a point on the boundary (exclusive)");
        assertEquals(0, quadGraph.countPoints(), "QuadGraph should contain zero points");
    }

    /**
     * Test inserting duplicate points.
     */
    @Test
    public void testInsertDuplicatePoints() {
        Point2D point = new Point2D(5.0, 5.0);
        quadGraph.insert(point);
        quadGraph.insert(point);

        assertEquals(2, quadGraph.countPoints(), "QuadGraph should contain two points (duplicates allowed)");
    }

    /**
     * Test searching for a point outside the bounds.
     */
    @Test
    public void testSearchPointOutsideBounds() {
        quadGraph.insert(new Point2D(5.0, 5.0));
        Point2D searchPoint = new Point2D(15.0, 15.0);

        Point2D found = quadGraph.search(searchPoint);

        assertNull(found, "Search should return null for a point outside bounds");
    }

    /**
     * Test the behavior when the QuadGraph has multiple levels of children.
     */
    @Test
    public void testMultipleSplits() {
        // Insert enough points to cause multiple splits
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                quadGraph.insert(new Point2D(x + 0.5, y + 0.5));
            }
        }

        assertNotNull(quadGraph.children, "QuadGraph should have children after multiple insertions");
        assertEquals(100, quadGraph.countPoints(), "QuadGraph should contain 100 points");
    }

    /**
     * Test the search method with multiple levels of children.
     */
    @Test
    public void testSearchWithMultipleLevels() {
        // Insert points to create multiple levels
        quadGraph.insert(new Point2D(1.0, 1.0));
        quadGraph.insert(new Point2D(9.0, 9.0));
        quadGraph.insert(new Point2D(5.0, 5.0));
        quadGraph.insert(new Point2D(2.0, 2.0));
        quadGraph.insert(new Point2D(8.0, 8.0));
        quadGraph.insert(new Point2D(3.0, 3.0));

        Point2D searchPoint = new Point2D(4.0, 4.0);
        Point2D found = quadGraph.search(searchPoint);

        assertNotNull(found, "Search should return the nearest point");
        assertEquals(3.0, found.x, 0.0001, "Nearest point's x-coordinate should be 3.0");
        assertEquals(3.0, found.y, 0.0001, "Nearest point's y-coordinate should be 3.0");
    }
}

