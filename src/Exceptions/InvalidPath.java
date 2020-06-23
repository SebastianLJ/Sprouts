package Exceptions;

import javafx.scene.shape.Path;

public class InvalidPath extends Exception {
    Path path;

    public InvalidPath(String message) {super(message);}

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
