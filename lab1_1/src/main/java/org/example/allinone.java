package org.example;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.graphml.GraphMLExporter;

import java.io.*;
import java.util.*;

public class allinone {
    private static Graph<String, DefaultWeightedEdge> graph;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputFile = "src/main/java/org/example/output.txt";
        // 读取文本生成有向图
        graph = readGraphFromFile(inputFile);
        // 循环输出菜单
        while (true) {
            System.out.println("请选择功能：");
            System.out.println("1. 更新有向图      2. 展示有向图");
            System.out.println("3. 查询桥接词      4. 根据桥接词生成新文本");
            System.out.println("5. 计算最短路径    6. 随机游走");
            System.out.println("7. 退出");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 读取换行符

            switch (choice) {
                case 1:
                    try{
                    readAndWriteGraph();
                    }
                catch(IOException e){
                    e.printStackTrace();
                }
                    break;
                case 2:
                    showDirectedGraph();
                    break;
                case 3:
                    System.out.print("请输入第一个单词：");
                    String word1 = scanner.nextLine();
                    System.out.print("请输入第二个单词：");
                    String word2 = scanner.nextLine();
                    System.out.println(queryBridgeWords(word1, word2));
                    break;
                case 4:
                    System.out.print("请输入输入文本：");
                    String inputText = scanner.nextLine();
                    System.out.println(generateNewText(inputText));
                    break;
                case 5:
                    System.out.print("请输入第一个单词：");
                    word1 = scanner.nextLine();
                    System.out.print("请输入第二个单词：");
                    word2 = scanner.nextLine();
                    System.out.println(calcShortestPath(word1, word2));
                    break;
                case 6:
                    System.out.println(randomWalk());
                    break;
                case 7:
                    System.out.println("已退出。");
                    return;
                default:
                    System.out.println("无效操作，请重新输入。");
            }
        }
    }

    //更新有向图
    public static void readAndWriteGraph() throws IOException {
        //在更新有向图时作为参数
        String input = "src/main/java/org/example/input.txt";
        String output = "src/main/java/org/example/output.txt";
        try {
            Map<String, Map<String, Integer>> graph = new HashMap<>();
            // 读取文本文件并统计相邻单词出现次数
            BufferedReader reader = new BufferedReader(new FileReader(input));
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
            FileWriter writer = new FileWriter(output);
            for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
                String prev = entry.getKey();
                for (Map.Entry<String, Integer> innerEntry : entry.getValue().entrySet()) {
                    String next = innerEntry.getKey();
                    int weight = innerEntry.getValue();
                    writer.write(prev + " " + next + " " + weight + "\n");
                }
            }
            writer.close();
            System.out.println("有向图信息已保存至 " + output);
        } catch (FileNotFoundException e) {
            throw new IOException("输入文件未找到: " + input, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //展示有向图，保存在graphml文件中
    private static void showDirectedGraph() {
        GraphMLExporter<String, DefaultWeightedEdge> exporter = new GraphMLExporter<>();
        exporter.setExportEdgeWeights(true);

        // 设置顶点 ID 提供程序
        exporter.setVertexIdProvider(v -> v);

        // 设置顶点和边的标签提供程序
        exporter.setVertexAttributeProvider(v -> {
            Map<String, Attribute> map = new HashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });
        exporter.setEdgeAttributeProvider(e -> {
            Map<String, Attribute> map = new HashMap<>();
            map.put("weight", DefaultAttribute.createAttribute(graph.getEdgeWeight(e)));
            return map;
        });

        String filename = "src/main/java/org/example/graph.graphml";
        try (FileWriter writer = new FileWriter(filename)) {
            exporter.exportGraph(graph, writer);
            System.out.println("Graph exported to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //查询桥接词
    public static String queryBridgeWords(String word1, String word2) {
        Map<String, Map<String, Double>> graph = readfromFile("src/main/java/org/example/output.txt");
        // 检查单词是否在有向图中
        if (!graph.containsKey(word1)) {
            //System.err.println("错误：单词 " + word1 + " 不存在于有向图中。");
            return "无桥接词";
        }
        if (!graph.containsKey(word2)) {
            //System.err.println("错误：单词 " + word2 + " 不存在于有向图中。");
            return "无桥接词";
        }
        // 获取 word1 和 word2 的邻居节点
        List<String> neighbors1 = new ArrayList<>(graph.get(word1).keySet());
        List<String> neighbors2 = new ArrayList<>();
        for (String node : graph.keySet()) {
            if (graph.get(node).containsKey(word2)) {
                neighbors2.add(node);
            }
        }
        List<String> bridgeWords = new ArrayList<>();
        // 查找共同的邻居节点作为桥接词
        for (String neighbor : neighbors1) {
            if (neighbors2.contains(neighbor)) {
                bridgeWords.add(neighbor);
            }
        }
        return bridgeWords.isEmpty() ? "无桥接词" : "桥接词：" + String.join(", ", bridgeWords);
    }

    //生成新文本
    private static String generateNewText(String inputText) {
        String[] words = inputText.split("[^a-zA-Z]+");
        StringBuilder newText = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            // 检查当前单词和下一个单词是否存在于图中
            if (!graph.containsVertex(words[i])) {
                continue;
            }
            if (!graph.containsVertex(words[i + 1])) {
                words[i+1] = words[i];
                continue;
            }
            // 加入前一个单词，下一个单词在下次循环中加入
            newText.append(words[i]).append(" ");
            String bridgeWords = queryBridgeWords(words[i], words[i + 1]);
            if (!bridgeWords.equals("无桥接词")) {
                // 随机选择一个桥接词
                String[] bridges = bridgeWords.split("：")[1].split(", ");
                if (bridges.length > 0) {
                    String randomBridge = bridges[random.nextInt(bridges.length)];
                    newText.append(randomBridge).append(" ");
                }
            }
        }
        // 检查最后一个单词是否存在于图中，然后加入
        if (graph.containsVertex(words[words.length - 1])) {
            newText.append(words[words.length - 1]);
        }
        return newText.toString();
    }


    //计算最短路径
    private static String calcShortestPath(String word1, String word2) {
        // 从文件中读取有向图的信息，此处重新定义，未使用第三方库
        Map<String, Map<String, Double>> graph = readfromFile("src/main/java/org/example/output.txt");
        // 使用 Dijkstra 算法计算最短路径
        Map<String, Double> dist = new HashMap<>();     //最短距离
        Map<String, String> prev = new HashMap<>();     //前驱节点
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));  //优先队列
        // 初始化距离和前驱节点，其中距离初始值为无穷大，前驱节点初始值为 null。
        for (String node : graph.keySet()) {
            dist.put(node, Double.POSITIVE_INFINITY);
            prev.put(node, null);
        }
        //将起始节点的距离设为0，并加入优先队列
        dist.put(word1, 0.0);
        pq.add(word1);
        //优先队列中取出节点，更新相邻节点的距离和前驱节点，并将其加入优先队列，直到找到目标节点或者优先队列为空
        while (!pq.isEmpty()) {
            String u = pq.poll();               //弹出距离起始节点最近的节点
            if (u.equals(word2)) break;         //可能造成错误
            if (!graph.containsKey(u)) continue; // 跳过不存在的节点

            for (String v : graph.get(u).keySet()) {    //遍历所有邻居节点
                double weight = graph.get(u).get(v);
                if (dist.get(u) + weight < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, dist.get(u) + weight);
                    prev.put(v, u);                     //设置前驱节点
                    pq.add(v);                          //小于已知最短路径，添加到优先级队列
                }
            }
        }
        // 构建路径
        List<String> path = new ArrayList<>();
        String curr = word2;
        while (curr != null) {
            path.add(curr);
            curr = prev.get(curr);
        }
        Collections.reverse(path);

        // 输出结果
        if (path.size() == 1 || path.get(0).equals(word2)) {
            return "无法找到从 " + word1 + " 到 " + word2 + " 的路径";
        } else {
            double totalWeight = dist.get(word2);
            return "最短路径：" + String.join(" -> ", path) + "\n总权重：" + totalWeight;
        }
    }

    //随机游走
    private static String randomWalk() {
        Random random = new Random();
        List<String> nodes = new ArrayList<>(graph.vertexSet());

        String currentNode = nodes.get(random.nextInt(nodes.size()));   //随机选取起始节点
        StringBuilder walk = new StringBuilder(currentNode);
        Set<String> visitedNodes = new HashSet<>();                     //记录访问过的节点
        visitedNodes.add(currentNode);

        while (true) {
            Set<DefaultWeightedEdge> outgoingEdges = graph.outgoingEdgesOf(currentNode);
            if (outgoingEdges.isEmpty()) {
                break;  //无出边，停止
            }
            List<DefaultWeightedEdge> edgeList = new ArrayList<>(outgoingEdges);
            DefaultWeightedEdge edge = edgeList.get(random.nextInt(edgeList.size()));
            currentNode = graph.getEdgeTarget(edge);        //更新当前节点
            walk.append(" -> ").append(currentNode);        //在路径中加入当前节点

            // 如果已经访问过该节点，则停止游走
            if (visitedNodes.contains(currentNode)) {
                break;
            }
            visitedNodes.add(currentNode);
        }
        return walk.toString();
    }

    //从文本中读取有向图，Graph为外部库的结构，该方法已被替代
    private static Graph<String, DefaultWeightedEdge> readGraphFromFile(String filename) {
        Graph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String sourceNode = parts[0];
                String targetNode = parts[1];
                double weight = Double.parseDouble(parts[2]);

                graph.addVertex(sourceNode);
                graph.addVertex(targetNode);
                DefaultWeightedEdge edge = graph.addEdge(sourceNode, targetNode);
                graph.setEdgeWeight(edge, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }
    // 从文件中读取有向图的信息，返回的是Map
    private static Map<String, Map<String, Double>> readfromFile(String filename) {
        Map<String, Map<String, Double>> graph = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String sourceNode = parts[0];
                String targetNode = parts[1];
                double weight = Double.parseDouble(parts[2]);

                graph.computeIfAbsent(sourceNode, k -> new HashMap<>())
                        .put(targetNode, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }
}
