package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

class Main {
    public static void main(String[] args) {
        String inputFile = "src/main/java/org/example/input.txt";
        String outputFile = "src/main/java/org/example/output.txt";

        try {
            Map<String, Map<String, Integer>> graph = new HashMap<>();

            // 读取文本文件并统计相邻单词出现次数
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String prevWord = null;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[^a-zA-Z]+"); // 使用正则表达式分割单词
                for (String word : words) {
                    if (prevWord != null && !prevWord.isEmpty() && !word.isEmpty()) {
                        // 更新图中的边和权重
                        graph.computeIfAbsent(prevWord, k -> new HashMap<>())
                                .put(word, graph.getOrDefault(prevWord, new HashMap<>()).getOrDefault(word, 0) + 1);
                    }
                    prevWord = word;
                }
            }
            reader.close();

            // 生成有向图并存储到目标文件
            FileWriter writer = new FileWriter(outputFile);
            for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
                String prev = entry.getKey();
                for (Map.Entry<String, Integer> innerEntry : entry.getValue().entrySet()) {
                    String next = innerEntry.getKey();
                    int weight = innerEntry.getValue();
                    writer.write(prev + " " + next + " " + weight + "\n");
                }
            }
            writer.close();

            System.out.println("Graph generated and saved to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 查找桥接词的方法
    private static List<String> findBridgeWords(String word1, String word2) {
        // 从文件中读取有向图的信息并构建图
        Map<String, Map<String, Integer>> graph = readGraphFromFile("main/src/main/java/org/example/output.txt");

        Map<String, Integer> neighbors1 = graph.get(word1);
        Map<String, Integer> neighbors2 = graph.get(word2);
        List<String> bridgeWords = new ArrayList<>();

        if (neighbors1 != null && neighbors2 != null) {
            for (String neighbor : neighbors1.keySet()) {
                if (neighbors2.containsKey(neighbor)) {
                    bridgeWords.add(neighbor);
                }
            }
        }

        return bridgeWords;
    }

    // 计算桥接词并插入新文本的方法
    private static String generateNewText( String inputText) {
        StringBuilder result = new StringBuilder();
        String[] words = inputText.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            result.append(word1).append(" ");
            List<String> bridges = findBridgeWords(word1, word2);
            if (!bridges.isEmpty()) {
                result.append(bridges.get(0)).append(" ");
            }
        }
        result.append(words[words.length - 1]);
        return result.toString();
    }

    // 查找最短路径的方法（Dijkstra算法）
    private static String calcShortestPath( String word1, String word2) {
        // 从文件中读取有向图的信息并构建图
        Map<String, Map<String, Integer>> graph = readGraphFromFile("main/src/main/java/org/example/output.txt");
        // 初始化距离和前驱节点
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();
        for (String word : graph.keySet()) {
            distance.put(word, Integer.MAX_VALUE);
            predecessor.put(word, null);
        }
        distance.put(word1, 0);

        // Dijkstra算法
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get));
        queue.offer(word1);
        while (!queue.isEmpty()) {
            String currentWord = queue.poll();
            if (currentWord.equals(word2)) {
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
        String currentWord = word2;
        while (currentWord != null) {
            shortestPath.add(0, currentWord);
            currentWord = predecessor.get(currentWord);
        }

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
        return "没什么可返回的";
    }
//随机游走
    private static String randomWalk() {
        String inputFile = "main/src/main/java/org/example/output.txt";
        String outputFile = "main/src/main/java/org/example/traversal_output.txt";
        // 读取有向图
        Map<String, Map<String, Integer>> graph = readGraphFromFile(inputFile);

        // 随机选择起始节点进行遍历
        RandomTraversal randomTraversal = new RandomTraversal(graph);
        List<String> nodes = new ArrayList<>(graph.keySet());
        Random random = new Random();
        String startNode = nodes.get(random.nextInt(nodes.size()));
        randomTraversal.traverse(startNode);

        // 获取遍历的节点路径
        List<String> traversalPath = randomTraversal.getTraversalPath();

        // 将遍历的节点路径转换成字符串
        StringBuilder sb = new StringBuilder();
        for (String node : traversalPath) {
            sb.append(node).append("  ->\n");
        }

        // 将遍历的节点写入文件
        try {
            FileWriter writer = new FileWriter(outputFile);
            for (String node : traversalPath) {
                writer.write(node + "  ->\n");
            }
            writer.close();
            System.out.println("Traversal path saved to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
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
