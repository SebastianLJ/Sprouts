package Exceptions;

import javafx.scene.shape.Path;

public class CollisionException extends Exception {
    Path path;

    public CollisionException(String errorMessage) {
        super(errorMessage);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}