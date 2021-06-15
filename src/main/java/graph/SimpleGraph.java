package graph;

import java.util.*;

public class SimpleGraph implements Graph{

    private final List<Vertex> vertexList;
    private final  boolean[][] adjMat;


    public SimpleGraph(int maxVertexCount) {
        this.vertexList = new ArrayList<>();
        this.adjMat = new boolean[maxVertexCount][maxVertexCount];
    }

    @Override
    public void addVertex(String label) {
        vertexList.add(new Vertex(label));
    }

    @Override
    public boolean addEdge(String startLabel, String secondLabel, String... others) {

        boolean res = addEdge(startLabel, secondLabel);

        for (String otherLabel: others) {
            res &= addEdge(startLabel, otherLabel);
        }

        return res;
    }

    public boolean addEdge(String startLabel, String endLabel) {

        int startIndex = indexOf(startLabel);
        int endIndex = indexOf(endLabel);

        if(startIndex == -1 || endIndex == -1){
            return false;
        }
        adjMat[startIndex][endIndex] = true;
        adjMat[endIndex][startIndex] = true;

        return true;
    }

    public int indexOf(String label){
        for (int i = 0; i < vertexList.size(); i++) {
            Vertex v = vertexList.get(i);
            if(v.getLabel().equals(label)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSize() {
        return vertexList.size();
    }

    @Override
    public void display() {
        for (int i = 0; i < getSize(); i++) {
            System.out.print(vertexList.get(i));
            for (int j = 0; j < getSize(); j++) {
                if(adjMat[i][j]){
                    System.out.print(" -> " + vertexList.get(j));
                }
            }
            System.out.println();
        }
    }

    @Override
    public void dfs(String startLabel) {

        int startIndex = indexOf(startLabel);
        if(startIndex==-1){
            throw new IllegalArgumentException("Invalid argument 'startLabel' " + startLabel);
        }

        Stack<Vertex> stack = new Stack<>();
        Vertex vertex = vertexList.get(startIndex);
        stack.push(vertex);
        visitVertex(vertex);

        while(!stack.isEmpty()){
            vertex = getNearestUnvisitVertex(stack.peek());
            if(vertex != null){
                stack.push(vertex);
                visitVertex(vertex);
            }else{
                stack.pop();
            }
        }

        resetVertex();

    }

    private void resetVertex() {
        for (Vertex v:vertexList) {
            v.setVisited(false);
            v.setPrevius(null);
        }
    }

    private Vertex getNearestUnvisitVertex(Vertex vertex) {
        int vertexInd = indexOf(vertex.getLabel());
        for (int i = 0; i < getSize(); i++) {
            if(adjMat[vertexInd][i]){
                if (!vertexList.get(i).isVisited()){
                    return vertexList.get(i);
                }
            }
        }
        return null;
    }

    private void visitVertex(Vertex vertex) {
        System.out.println(vertex);
        vertex.setVisited(true);
    }

    private void visitVertexQuietly(Vertex vertex, Vertex parent) {
        vertex.setVisited(true);
        vertex.setPrevius(parent);
    }

    @Override
    public void bfs(String startLabel) {

        int startIndex = indexOf(startLabel);
        if(startIndex==-1){
            throw new IllegalArgumentException("Invalid argument 'startLabel' " + startLabel);
        }

        Queue<Vertex> stack = new LinkedList<>();
        Vertex vertex = vertexList.get(startIndex);
        stack.add(vertex);
        visitVertex(vertex);

        while(!stack.isEmpty()){
            vertex = getNearestUnvisitVertex(stack.peek());
            if(vertex != null){
                stack.add(vertex);
                visitVertex(vertex);
            }else{
                stack.remove();
            }
        }

        resetVertex();

    }

    @Override
    public void findWay(String startLabel, String endLabel) {

        int startIndex = indexOf(startLabel);
        if(startIndex==-1){
            throw new IllegalArgumentException("Invalid argument 'startLabel' " + startLabel);
        }

        Vertex parent = null;

        Queue<Vertex> queue = new LinkedList<>();
        Vertex vertex = vertexList.get(startIndex);
        queue.add(vertex);
        visitVertexQuietly(vertex, parent);

        while(!queue.isEmpty()){
            parent = queue.peek();
            vertex = getNearestUnvisitVertex(queue.peek());

            if(vertex==null){
                queue.remove();
            }
            else if(vertex.getLabel().equals(endLabel)){
                visitVertexQuietly(vertex, parent);
                break;
            }
            else{
                queue.add(vertex);
                visitVertexQuietly(vertex, parent);
            }

        }

        System.out.println(showFindWay(vertexList.get(indexOf(endLabel))));

        resetVertex();

    }

    private String showFindWay(Vertex vertex) {
        if(vertex.getPrevius()==null){
            return vertex.getLabel();
        }
        return showFindWay(vertex.getPrevius()) + " -> " + vertex.getLabel();
    }

}
