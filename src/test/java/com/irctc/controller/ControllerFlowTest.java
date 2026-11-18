package com.irctc.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ControllerFlowTest {

    @Test
    @DisplayName("MainController should cleanly process exit command (6) and terminate loop")
    void testMainControllerExit() {
        Scanner simulatedInput = new Scanner("6\n");
        MainController controller = new MainController();
        assertDoesNotThrow(() -> controller.start(simulatedInput));
    }

    @Test
    @DisplayName("MainController should handle invalid input and then exit cleanly")
    void testMainControllerInvalidThenExit() {
        Scanner simulatedInput = new Scanner("99\n6\n");
        MainController controller = new MainController();
        assertDoesNotThrow(() -> controller.start(simulatedInput));
    }
}
