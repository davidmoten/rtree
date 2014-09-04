package com.github.davidmoten.rtree;

final class NodePosition<T> {

    private final Node<T> node;
    private final int position;

    NodePosition(Node<T> node, int position) {
        this.node = node;
        this.position = position;
    }

    Node<T> node() {
        return node;
    }

    int position() {
        return position;
    }

    NodePosition<T> nextPosition() {
        return new NodePosition<T>(node, position + 1);
    }

}
