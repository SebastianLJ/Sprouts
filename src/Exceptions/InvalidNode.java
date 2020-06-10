package Exceptions;

import Model.Node;

public class InvalidNode extends Exception {
    Node node;

    public InvalidNode(Node node) {this.node = node;}

    public Node getNode() {
        return node;
    }
}
