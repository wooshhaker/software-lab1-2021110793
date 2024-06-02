package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BridgeWordInserter {
    public static void main(String[] args) {
        // 从 output.txt 文件中读取有向图的信息并构建图
        Map<String, Map<String, Integer>> graph = readGraphFromFile("src/main/java/org/example/output.txt");

        // 用户输入一行新文本
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入一行新文本：");
        String newText = scanner.nextLine().toLowerCase();

        // 计算桥接词并插入新文本
        String newTextWithBridges = insertBridgeWords(graph, newText);

        // 输出结果
        System.out.println("新文本（含桥接词）：");
        System.out.println(newTextWithBridges);
    }

    // 从文件中读取有向图的信息并构建图
    private static Map<String, Map<String, Integer>> readGraphFromFile(String filename) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String sourceNode = parts[0];
                String targetNode = parts[1];
                int weight = Integer.parseInt(parts[2]);
                graph.computeIfAbsent(sourceNode, k -> new HashMap<>()).put(targetNode, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    // 计算桥接词并插入新文本的方法
    private static String insertBridgeWords(Map<String, Map<String, Integer>> graph, String newText) {
        StringBuilder result = new StringBuilder();
        String[] words = newText.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            result.append(word1).append(" ");
            List<String> bridges = findBridgeWords(graph, word1, word2);
            if (!bridges.isEmpty()) {
                result.append(bridges.get(0)).append(" ");
            }
        }
        result.append(words[words.length - 1]);
        return result.toString();
    }

    // 查找桥接词的方法
    private static List<String> findBridgeWords(Map<String, Map<String, Integer>> graph, String word1, String word2) {
        List<String> bridges = new ArrayList<>();
        Map<String, Integer> neighbors1 = graph.get(word1);
        Map<String, Integer> neighbors2 = graph.get(word2);
        if (neighbors1 != null && neighbors2 != null) {
            for (String bridge : neighbors1.keySet()) {
                if (neighbors2.containsKey(bridge)) {
                    bridges.add(bridge);
                }
            }
        }
        return bridges;
    }
}

