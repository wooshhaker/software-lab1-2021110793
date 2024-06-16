package org.example;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class writeTest {
    // 假设readfromFile方法已经实现并正确读取文件

    @Test
    void Word1NotInGraph() {
        String result = allinone.queryBridgeWords("x", "b");
        assertEquals("无桥接词", result);
    }

    @Test
    void Word2NotInGraph() {
        String result = allinone.queryBridgeWords("a", "x");
        assertEquals("无桥接词", result);
    }
    @Test
    void NoAfterNeibor() {
        String result = allinone.queryBridgeWords("g", "g");
        assertEquals("无桥接词", result);
    }
    @Test
    void NoBeforeNeibor() {
        String result = allinone.queryBridgeWords("g", "a");
        assertEquals("无桥接词", result);
    }
    @Test
    void NoBridge() {
        String result = allinone.queryBridgeWords("a", "g");
        assertEquals("无桥接词", result);
    }
    @Test
    void NormalPathWithBridge() {
        String result = allinone.queryBridgeWords("a", "c");
        assertEquals("桥接词：b", result);
    }
}