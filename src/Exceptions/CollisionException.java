package Exceptions;

import javafx.scene.shape.Path;

public class CollisionException extends Exception {
    Path path;
    String message;

    public CollisionException(String errorMessage) {
        super(errorMessage);
    }

    public CollisionException(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}