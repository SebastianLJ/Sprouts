package Exceptions;

import javafx.scene.shape.Path;

public class InvalidPath extends Exception {
    Path path;

    public InvalidPath(Path path) {this.path = path;}

    public Path getPath() {
        return path;
    }
}
