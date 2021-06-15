package graph;

import java.util.Objects;

public class Vertex {

    private final  String label;
    private boolean visited;
    private Vertex previus;

    public Vertex getPrevius() {
        return previus;
    }

    public void setPrevius(Vertex previus) {
        this.previus = previus;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "laabel='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(label, vertex.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    public String getLabel() {
        return label;
    }

    public Vertex(String laabel) {
        this.label = laabel;
    }
}
