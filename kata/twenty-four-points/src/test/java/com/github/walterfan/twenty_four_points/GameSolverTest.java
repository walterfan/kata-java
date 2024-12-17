package com.github.walterfan.twenty_four_points;


import org.junit.jupiter.api.Test;

import com.github.walterfan.twenty_four_points.GameSolver;

import static org.junit.jupiter.api.Assertions.*;

class GameSolverTest {
    @Test
    void testCanReach24() {
        GameSolver solver = new GameSolver();
        assertTrue(solver.canReach24(new int[]{8, 1, 6, 6})); // 可解
        assertFalse(solver.canReach24(new int[]{1, 1, 1, 1})); // 不可解
    }
}