package graph;

public interface Graph {

    void addVertex(String label);

    boolean addEdge(String startLabel, String secondLabel, String ... others);

    int getSize();

    void findWay(String startLabel, String endLabel);

    void display();

    int indexOf(String label);

    void dfs(String startLabel);

    void bfs(String startLabel);



}
