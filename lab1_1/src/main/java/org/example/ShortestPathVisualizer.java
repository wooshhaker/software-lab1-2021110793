package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ShortestPathVisualizer {
    public static void main(String[] args) {
        // 从 output.txt 文件中读取有向图的信息并构建图
        Map<String, Map<String, Integer>> graph = readGraphFromFile("src/main/java/org/example/output.txt");

        // 用户输入任意两个英文单词
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入起始单词：");
        String startWord = scanner.nextLine().toLowerCase();
        System.out.print("请输入目标单词：");
        String endWord = scanner.nextLine().toLowerCase();

        // 查找最短路径
        List<String> shortestPath = findShortestPath(graph, startWord, endWord);

        // 输出结果
        if (shortestPath.isEmpty()) {
            System.out.println("不存在路径");
        } else {
            System.out.println("最短路径：");
            for (String word : shortestPath) {
                System.out.print(word + " -> ");
            }
            System.out.println("\n路径长度：" + calculatePathLength(graph, shortestPath));
        }
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

    // 查找最短路径的方法（Dijkstra算法）
    private static List<String> findShortestPath(Map<String, Map<String, Integer>> graph, String startWord, String endWord) {
        // 初始化距离和前驱节点
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();
        for (String word : graph.keySet()) {
            distance.put(word, Integer.MAX_VALUE);
            predecessor.put(word, null);
        }
        distance.put(startWord, 0);

        // Dijkstra算法
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get));
        queue.offer(startWord);
        while (!queue.isEmpty()) {
            String currentWord = queue.poll();
            if (currentWord.equals(endWord)) {
                break;
            }
            if (!graph.containsKey(currentWord)) {
                continue;
            }
            for (Map.Entry<String, Integer> entry : graph.get(currentWord).entrySet()) {
                String neighbor = entry.getKey();
                int weight = entry.getValue();
                int newDistance = distance.get(currentWord) + weight;
                if (newDistance < distance.get(neighbor)) {
                    distance.put(neighbor, newDistance);
                    predecessor.put(neighbor, currentWord);
                    queue.offer(neighbor);
                }
            }
        }

        // 构建路径
        List<String> shortestPath = new ArrayList<>();
        String currentWord = endWord;
        while (currentWord != null) {
            shortestPath.add(0, currentWord);
            currentWord = predecessor.get(currentWord);
        }
        return shortestPath;
    }

    // 计算路径长度的方法
    private static int calculatePathLength(Map<String, Map<String, Integer>> graph, List<String> path) {
        int length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String sourceWord = path.get(i);
            String targetWord = path.get(i + 1);
            length += graph.get(sourceWord).get(targetWord);
        }
        return length;
    }
}

