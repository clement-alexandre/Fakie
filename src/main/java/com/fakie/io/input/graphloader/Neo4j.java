package com.fakie.io.input.graphloader;

import com.fakie.model.graph.Edge;
import com.fakie.model.graph.Graph;
import com.fakie.model.graph.Vertex;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4j implements GraphLoader {
    private GraphDatabaseService database;
    private Graph graph;

    @Override
    public Graph load(Path path) {
        database = new GraphDatabaseFactory().newEmbeddedDatabase(path.toFile());
        graph = new Graph();
        try (Transaction transaction = database.beginTx()) {
            populateGraph();
        }
        database.shutdown();
        return graph;
    }

    private void populateGraph() {
        Map<Node, Vertex> mapping = addVertices();
        addEdges(mapping);
    }

    private Map<Node, Vertex> addVertices() {
        Map<Node, Vertex> mapping = new HashMap<>();
        for (Node node : database.getAllNodes()) {
            Vertex vertex = new Vertex(node.getId(), retrieveLabels(node), node.getAllProperties());
            mapping.put(node, vertex);
            graph.addVertex(vertex);
        }
        return mapping;
    }

    private List<String> retrieveLabels(Node node) {
        List<String> labels = new ArrayList<>();
        for (Label label : node.getLabels()) {
            labels.add(label.name());
        }
        return labels;
    }

    private void addEdges(Map<Node, Vertex> mapping) {
        for (Relationship rs : database.getAllRelationships()) {
            Vertex source = mapping.get(rs.getStartNode());
            Vertex destination = mapping.get(rs.getEndNode());
            graph.addEdge(new Edge(source, destination, rs.getType().name(), rs.getAllProperties()));
        }
    }
}
