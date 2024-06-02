package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class RandomTraversal {
    private Map<String, Map<String, Integer>> graph;
    private Set<String> visitedNodes;
    private List<String> traversalPath;

    public RandomTraversal(Map<String, Map<String, Integer>> graph) {
        this.graph = graph;
        visitedNodes = new HashSet<>();
        traversalPath = new ArrayList<>();
    }

    public void traverse(String startNode) {
        Random random = new Random();
        String currentNode = startNode;
        traversalPath.add(currentNode);

        while (graph.containsKey(currentNode)) {
            Map<String, Integer> neighbors = graph.get(currentNode);
            List<String> unvisitedNeighbors = new ArrayList<>();

            // 将未访问过的邻居节点添加到列表中
            for (String neighbor : neighbors.keySet()) {
                if (!visitedNodes.contains(neighbor)) {
                    unvisitedNeighbors.add(neighbor);
                }
            }

            if (unvisitedNeighbors.isEmpty()) {
                // 如果当前节点的所有邻居节点都已经访问过，则结束遍历
                break;
            }

            // 从未访问过的邻居节点中随机选择一个作为下一个节点
            String nextNode = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
            traversalPath.add(nextNode);
            visitedNodes.add(nextNode);
            currentNode = nextNode;
        }
    }

    public List<String> getTraversalPath() {
        return traversalPath;
    }

    public static void main(String[] args) {
        String inputFile = "src/main/java/org/example/output.txt";
        String outputFile = "src/main/java/org/example/traversal_output.txt";

        // 读取有向图
        Map<String, Map<String, Integer>> graph = readGraphFromFile("src/main/java/org/example/output.txt");

        // 随机选择起始节点进行遍历
        RandomTraversal randomTraversal = new RandomTraversal(graph);
        List<String> nodes = new ArrayList<>(graph.keySet());
        Random random = new Random();
        String startNode = nodes.get(random.nextInt(nodes.size()));
        randomTraversal.traverse(startNode);

        // 将遍历的节点写入文件
        List<String> traversalPath = randomTraversal.getTraversalPath();
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
    }

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


}

