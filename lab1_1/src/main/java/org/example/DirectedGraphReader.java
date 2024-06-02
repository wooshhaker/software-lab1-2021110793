package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.graphml.GraphMLExporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DirectedGraphReader {

    public static void main(String[] args) {
        String inputFile = "src/main/java/org/example/output.txt";
        String outputFile = "src/main/java/org/example/graph.graphml";

        // 读取有向图
        Graph<String, DefaultWeightedEdge> graph = readGraphFromFile(inputFile);

        // 导出图形为 GraphML 格式
        exportGraphToGraphML(graph, outputFile);
    }

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

    private static void exportGraphToGraphML(Graph<String, DefaultWeightedEdge> graph, String filename) {
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

        try (FileWriter writer = new FileWriter(filename)) {
            exporter.exportGraph(graph, writer);
            System.out.println("Graph exported to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
