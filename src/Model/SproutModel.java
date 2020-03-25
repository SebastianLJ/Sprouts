package Model;

import javafx.scene.shape.*;

import javafx.scene.input.MouseEvent;

import java.util.*;

public class SproutModel {
    private static final int DISTANCE_BETWEEN_POINTS = 20;
    private static final int DISTANCE_FROM_BORDER = 20;
    private ArrayList<Shape> lines = new ArrayList<>();
    private List<Shape> edges;
    private List<Node> nodes;

    private int height = 280;       // TODO: make user-settable
    private int width = 500;        // TODO: make user-settable
    private Path path;
    private final static double COLLISION_WIDTH = 1.5;
    private boolean isCollided;
    private Point point;


    private Path pathTmp;
    private Line pathTmp2;


    public SproutModel() {
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    public void addRandomNodes(int amount) {
        Random random = new Random();

        int x;
        int y;
        Circle circle = new Circle();
        circle.setRadius(5); // TODO make scalable

        for (int i = 0; i < amount; i++) {
            do {
                x = random.nextInt(width - 2*DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                y = random.nextInt(height - 2*DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                circle.setCenterX(x);
                circle.setCenterY(y);
            } while (invalidPointLocation(circle));
            nodes.add(new Node(x, y, 0));
        }
    }

    private boolean invalidPointLocation(Circle circle) {
        for (Node node : nodes) {
            Shape intersect = Shape.intersect(circle, node.getShape());
            if (intersect.getBoundsInLocal().getWidth() != -1) {
                return true;
            }
            if (DISTANCE_BETWEEN_POINTS > distanceBetweenCircleCenter(node.getShape(), circle)) {
/*
                System.out.println(distanceBetweenCircleCenter(node.getShape(), circle));
*/
                return true;
            }
        }

        return false;
    }

    private int distanceBetweenCircleCenter(Circle circle1, Circle circle2) {
        double dx = circle2.getCenterX()-circle1.getCenterX();
        double dy = circle2.getCenterY()-circle1.getCenterY();

        return (int) Math.ceil(Math.sqrt(dx*dx+dy*dy));
    }


    public void resetGame() {
        edges.clear();
        nodes.clear();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Shape> getEdges() {
        return edges;
    }

    public boolean hasNodeWithName(int name) {
        return name < nodes.size();
    }

    public int getNumberOfEdges(int name) {
        return nodes.get(name).getNumberOfConnectingEdges();
    }

    public void drawEdgeBetweenNodes(int startNode, int endNode) {

        if (startNode == endNode) {
            drawCircleFromNodeToItself(startNode);
        } else {
            drawLineBetweenNodes(startNode, endNode);
        }
        // TODO: check for collision
        // slet fra database in case (nodes list, edges list)
    }

    public void drawLineBetweenNodes(int startNodeName, int endNodeName) {

        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Line newLine = new Line();

        newLine.setStartX(startNode.getX());
        newLine.setStartY(startNode.getY());
        newLine.setEndX(endNode.getX());
        newLine.setEndY(endNode.getY());

        edges.add(newLine);
        addNodeOnLine(newLine);

        // Update number of connecting edges
        startNode.incNumberOfConnectingEdges(1);
        endNode.incNumberOfConnectingEdges(1);
        nodes.set(startNodeName, startNode);
        nodes.set(endNodeName, endNode);
    }

    public void drawCircleFromNodeToItself(int nodeName) {

        Node node = nodes.get(nodeName);
        Circle newCircle = new Circle();

        double radius = width / 100;                                  // TODO: adjust to various window sizes
        double nodeX = node.getX();
        double nodeY = node.getY();

        Double[] center = getCircleCenterCoordinates(nodeX, nodeY, radius);
        newCircle.setCenterX(center[0]);
        newCircle.setCenterY(center[1]);
        newCircle.setRadius(radius);
        newCircle.setStrokeWidth(1.0);

        edges.add(newCircle);
        addNodeOnCircle(newCircle, nodeX, nodeY);

        // Update number of connecting edges
        node.incNumberOfConnectingEdges(2);
        nodes.set(nodeName, node);
    }

    public void addNodeOnLine(Line edge) {

        double edgeIntervalX = Math.abs(edge.getEndX() - edge.getStartX());
        double edgeIntervalY = Math.abs(edge.getEndY() - edge.getStartY());
        double newNodeX = Math.min(edge.getStartX(), edge.getEndX()) + (edgeIntervalX / 2);
        double newNodeY = Math.min(edge.getStartY(), edge.getEndY()) + (edgeIntervalY / 2);

        Node newNode = new Node(newNodeX, newNodeY, 2);
        nodes.add(newNode);
    }

    public void addNodeOnCircle(Circle edge, double originNodeX, double originNodeY) {

        double newNodeX = originNodeX + (edge.getCenterX() - originNodeX) * 2;
        double newNodeY = originNodeY + (edge.getCenterY() - originNodeY) * 2;

        Node newNode = new Node(newNodeX, newNodeY, 2);
        nodes.add(newNode);
    }

    public Double[] getCircleCenterCoordinates(double originNodeX, double originNodeY, double radius) {

        Double[] center = {originNodeX, originNodeY};

        if (originNodeX - radius >= 0) {
            center[0] = originNodeX - radius;
        } else if (originNodeX + radius < width) {
            center[0] = originNodeX + radius;
        } else if (originNodeY - radius >= 0) {
            center[1] = originNodeY - radius;
        } else {
            center[1] = originNodeY + radius;
        }

        return center;
    }

    public void initializePath(MouseEvent event) {
        isCollided = false;
        point = new Point((int) event.getX(), (int) event.getY());
        path = new Path();
        pathTmp = new Path();
        path.getElements().add(new MoveTo(point.getX(), point.getY()));
        pathTmp2 = new Line();
        pathTmp2.setStartX(point.getX());
        pathTmp2.setStartY(point.getY());
    }


    public void drawPath(MouseEvent event) {
        if(isCollided){
/*
            System.out.println("you collided draw somewhere else");
*/
        }
        else {
            pathTmp.getElements().add(new MoveTo(point.getX(), point.getY()));
            pathTmp2.setStartX(point.getX());
            pathTmp2.setStartY(point.getY());
            point = new Point((int) event.getX(), (int) event.getY());
            pathTmp.getElements().add(new LineTo(point.getX(), point.getY()));
            pathTmp2.setEndX(point.getX());
            pathTmp2.setEndY(point.getY());
            if (doPathsCollide()){
                System.out.println("test");
                isCollided = true;
                path.getElements().clear();
                System.out.println("collision at " + point.getX() + ", " + point.getY());
            } else {
                path.getElements().add(new LineTo(point.getX(), point.getY()));
                pathTmp.getElements().clear();
            }
        }
    }

    public void finishPath() {
            lines.add(path);
        }

    public boolean doPathsCollide() {
/*
        System.out.println("Bredde: " + Shape.intersect(pathTmp, path).getBoundsInLocal().getWidth());
*/


        Shape test = Shape.intersect(pathTmp2, path);
        pathTmp.getElements().remove(pathTmp.getElements().size()-2);



        if ((path.intersects(pathTmp2.getBoundsInLocal()))) {
            return true;
        }
        for (Shape line : lines) {
            if (Shape.intersect(path, line).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        /*System.out.println("tmp local: \n " + pathTmp2.getBoundsInLocal());
        System.out.println("tmp parent: \n " + pathTmp2.getBoundsInParent());*/
        /*System.out.println("path: \n " + path);*/
        return false;
    }

    public Path getPath() {
        return path;
    }

    public Path getPathTmp() {
        return pathTmp;
    }

    public boolean getIsCollided() {
        return isCollided;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean hasNodeWithName(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return true;
            }
        }
        return false;
    }

    public int getNumberOfEdges(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return node.getNumberOfConnectingEdges();
            }
        }
        return -1; // Should never be reached
    }

    public void drawEdgeBetweenNodes(Circle startNode, Circle endNode) {
        int nameOfStartNode = 0, nameOfEndNode = 0;
        int i = 0;
        for (Node node : nodes) {
            if (node.getShape() == startNode) {
                nameOfStartNode = i;
            }
            if (node.getShape() == endNode) {
                nameOfEndNode = i;
            }
            i++;
        }
        drawEdgeBetweenNodes(nameOfStartNode, nameOfEndNode);
    }

    public Circle getNewestNode() {
        return nodes.get(nodes.size()-1).getShape();
    }

    public Shape getNewestEdge() {
        return edges.get(edges.size()-1);
    }
}

