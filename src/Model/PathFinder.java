package Model;


import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;

import java.util.Arrays;
import java.util.List;

public class PathFinder {
    private SproutModel model;
    private boolean[][] grid = new boolean[10][10];

    public PathFinder(SproutModel model) {
        this.model = model;
    }

    public void initGrid() {
        List<Shape> edges = model.getEdges();
        List<Node> nodes = model.getNodes();

        for (Shape shape : edges) {
            for (PathElement pe : ((Path) shape).getElements()) {
                String pathElemString = pe.toString();



                double x = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("x")+2,
                        pathElemString.indexOf(",")));
                double y = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("y")+2,
                        pathElemString.indexOf("]")));

                int nX = (int) (x/(model.getWidth()/grid.length));
                int nY = (int) (y/(model.getHeight()/grid.length));

//                System.out.println("x: " + nX + ", y: " + nY);

                grid[nY][nX] = true;
            }
        }

        for (Node node : nodes) {
            int x = (int) (node.getX()/(model.getWidth()/grid.length));
            int y = (int) (node.getY()/(model.getHeight()/grid.length));
            grid[y][x] = true;
        }
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < grid.length; i++) {
            res.append(Arrays.toString(grid[i])).append("\n");
        }
        return res.toString();
    }
}
