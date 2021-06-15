package graph;

public class GraphTest {

    public static void main(String[] args) {

        testGraph();

        testDFS();

    }

    private static void testDFS() {

        Graph graph = new SimpleGraph(8);

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addVertex("G");
        graph.addVertex("H");

        graph.addEdge("A", "B", "C", "D");
        graph.addEdge("B", "E");
        graph.addEdge("E", "H");
        graph.addEdge("C", "F");
        graph.addEdge("D", "G");

        System.out.println("DFS test: ");
        graph.dfs("A");

        System.out.println("BFS test: ");
        graph.bfs("A");

    }

    private static void testGraph() {

        Graph graph = new SimpleGraph(4);

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B", "C");
        graph.addEdge("B", "A", "C", "D");
        graph.addEdge("C", "A", "B", "D");
        graph.addEdge("D", "B", "C");

        System.out.println("Graph size: " + graph.getSize());
        graph.display();

    }

}
