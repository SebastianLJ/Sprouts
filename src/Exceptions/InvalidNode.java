package Exceptions;

import Model.Node;

public class InvalidNode extends Exception {
    Node node;

    public InvalidNode(Node node) {this.node = node;}

    public InvalidNode(String message) {super(message);}

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
