package com.irctc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    @DisplayName("Main entry point should execute cleanly without exceptions")
    void testMainExecution() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}
