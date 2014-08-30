package com.github.davidmoten.rtree;

public class NodePosition<T> {

    private final Node<T> node;
    private final int position;

    public NodePosition(Node<T> node, int position) {
        this.node = node;
        this.position = position;
    }

    public Node<T> node() {
        return node;
    }

    public int position() {
        return position;
    }

    public NodePosition<T> nextPosition() {
        return new NodePosition<T>(node, position + 1);
    }

}
