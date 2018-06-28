package com.fakie.graph.mapping;

import com.fakie.graph.model.Graph;
import com.fakie.graph.model.Vertex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Neo4jMappingTest {
    private static final Label appLabel = Label.label("App");
    private GraphDatabaseService graphDatabaseService;
    private File dir;

    @Before
    public void setUp() throws URISyntaxException {
        URL db = getClass().getClassLoader().getResource("db");
        assert db != null : "Could not locate the db directory";
        Path neo4j = new File(db.toURI()).toPath().resolve("neo4j");
        dir = neo4j.toFile();
        assert dir.mkdirs() : "Could not create neo4j resource folder";
        graphDatabaseService = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(dir)
                .newGraphDatabase();
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            Node node = graphDatabaseService.createNode(appLabel);
            node.setProperty("name", "wikipedia");
            transaction.success();
        }
        graphDatabaseService.shutdown();
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deletePathRecursively(dir.toPath());
    }

    @Test
    public void name() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("name", "wikipedia");
        try (Neo4jMapping neo4jMapping = new Neo4jMapping(dir.toPath())) {
            Graph graph = neo4jMapping.convertToGraph();
            List<Vertex> vertices = graph.getVertices();
            assertEquals(1, vertices.size());
            assertEquals(expected, vertices.get(0).getProperties());
            assertTrue(graph.getEdges().isEmpty());
        }
    }
}