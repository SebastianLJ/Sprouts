package Exceptions;

import javafx.scene.shape.Path;

public class CollisionException extends Exception {
    Path path;

    public CollisionException(String errorMessage) {
        super(errorMessage);
    }

    public CollisionException(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}