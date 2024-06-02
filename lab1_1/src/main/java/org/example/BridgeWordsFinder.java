package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BridgeWordsFinder {
    public static void main(String[] args) {
        // 从 output.txt 文件中读取有向图的信息并构建图
        Map<String, List<String>> graph = readGraphFromFile("src/main/java/org/example/output.txt");

        // 用户输入任意两个英文单词
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入第一个单词：");
        String word1 = scanner.nextLine().toLowerCase();
        System.out.print("请输入第二个单词：");
        String word2 = scanner.nextLine().toLowerCase();

        // 查找桥接词
        List<String> bridgeWords = findBridgeWords(graph, word1, word2);

        // 输出结果
        if (bridgeWords.isEmpty()) {
            System.out.println("无桥接词");
        } else {
            System.out.println("桥接词：");
            for (String bridgeWord : bridgeWords) {
                System.out.println(bridgeWord);
            }
        }
    }

    // 从文件中读取有向图的信息并构建图
    private static Map<String, List<String>> readGraphFromFile(String filename) {
        Map<String, List<String>> graph = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String sourceNode = parts[0];
                String targetNode = parts[1];
                graph.computeIfAbsent(sourceNode, k -> new ArrayList<>()).add(targetNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    // 查找桥接词的方法
    private static List<String> findBridgeWords(Map<String, List<String>> graph, String word1, String word2) {
        List<String> neighbors1 = graph.get(word1);
        List<String> neighbors2 = graph.get(word2);
        List<String> bridgeWords = new ArrayList<>();

        if (neighbors1 != null && neighbors2 != null) {
            for (String neighbor : neighbors1) {
                if (neighbors2.contains(neighbor)) {
                    bridgeWords.add(neighbor);
                }
            }
        }

        return bridgeWords;
    }
}

