package Model;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.List;

class EdgeTools {

    /**
     * @param circle1 : A circle shape
     * @param circle2 : A circle shape
     * @return distance between circle centers
     * @Thea Birk Berger
     */
    double distanceBetweenCircleCenter(Circle circle1, Circle circle2) {

        double x1 = circle1.getCenterX();
        double y1 = circle1.getCenterY();
        double x2 = circle2.getCenterX();
        double y2 = circle2.getCenterY();
        return getDistanceBetweenTwoPoints(x1,y1,x2,y2);
    }

    /**
     * Creates a straight line between the centers of two given nodes
     *
     * @param startNode : Start node
     * @param endNode : End node
     * @return a Line object
     * @author Thea Birk Berger
     */
    private Line createLineBetweenNodes(Node startNode, Node endNode) {

        Line newLine = new Line();
        newLine.setStartX(startNode.getX());
        newLine.setStartY(startNode.getY());
        newLine.setEndX(endNode.getX());
        newLine.setEndY(endNode.getY());

        return newLine;
    }

    /**
     * Creates a straight line between the contour of two given nodes
     *
     * @param startNode : The start node
     * @param endNode : The end node
     * @return a Line object
     * @author Thea Birk Berger
     */
    Line createLineBetweenNodeContours(Node startNode, Node endNode) {

        double x1 = startNode.getX();
        double x2 = endNode.getX();
        double y1 = startNode.getY();
        double y2 = endNode.getY();
        double r1 = startNode.getNodeRadius();
        double r2 = endNode.getNodeRadius();

        // Compute distance between node centers
        double length = getDistanceBetweenTwoPoints(x1,y1,x2,y2);

        // Set line end points as the node edge (cut off the radius)
        Line newLine = new Line();

        newLine.setStartX(x1 + (r1/length) * (x2-x1));
        newLine.setStartY(y1 + (r1/length) * (y2-y1));
        newLine.setEndX(x2 + (r2/length) * (x1-x2));
        newLine.setEndY(y2 + (r2/length) * (y1-y2));

        return newLine;
    }

    /**
     * Calculates euclidean distance between two points in 2D
     *
     * @param x1 : 1st point x-coordinate
     * @param y1 : 1st point y-coordinate
     * @param x2 : 2nd point x-coordinate
     * @param y2 : 2nd point y-coordinate
     * @return the distance between the two points
     * @author Thea Birk Berger
     */
    double getDistanceBetweenTwoPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(Math.abs(x1-x2),2) + Math.pow(y1-y2,2));
    }

    /**
     * Creates a circular edge from a node to itself
     *
     * @param node : The node for which we want self-connection
     * @return a Circle object
     * @author Thea Birk Berger
     */
    Circle createCircleToDraw(Node node) {

        Circle newCircle = new Circle();
        double nodeDiameter = Node.radius*2;

        // Set edge properties
        newCircle.setCenterX(node.getX());
        newCircle.setCenterY(node.getY() + nodeDiameter);
        newCircle.setRadius(nodeDiameter);
        newCircle.setFill(Color.TRANSPARENT);
        newCircle.setStroke(Color.BLACK);

        return newCircle;
    }

    /**
     * Finds the point on a line given a distance from the line's starting point in 1D
     *
     * @param startCoor : Start coordinate
     * @param endCoor : End coordinate
     * @param distance : Distance from start coordinate
     * @param lineLength : Length og line
     * @return : Coordinate on the line that is "distance" away from the line's starting point
     * @author Thea Birk Berger
     */
    double getPointOnLine(double startCoor, double endCoor, double distance, double lineLength) {
        if (distance >= lineLength) {
            return endCoor;
        }
        return startCoor + (distance/lineLength) * (endCoor-startCoor);
    }

    /**
     * Finds the contour point of a node given a center and a nearby path element
     *
     * @param c1 : Node center x
     * @param c2 : Node center y
     * @param peX : Path element coordinate x
     * @param peY : Path element coordinate y
     * @param radius : Node radius
     * @return Contour point {x,y}
     * @author Thea Birk Berger
     */
    double[] getContourPoint(double c1, double c2, double peX, double peY, double radius) {
        double gapDistance = getDistanceBetweenTwoPoints(c1,c2,peX,peY);
        double contourX = getPointOnLine(c1,peX,radius,gapDistance);
        double contourY = getPointOnLine(c2,peY,radius,gapDistance);
        return new double[] {contourX,contourY};
    }

    /**
     * Gets path element (x,y) coordinates
     *
     * @param pe : Path element of interest
     * @param numberOfNodes : Number of nodes on the gameboard
     * @return Node with center in (x,y)
     * @author Thea Birk Berger
     */
    Node getCoordinates(PathElement pe, int connectingEdges, int numberOfNodes) {

        double x = pe instanceof MoveTo ? ((MoveTo) pe).getX() : ((LineTo) pe).getX();
        double y = pe instanceof MoveTo ? ((MoveTo) pe).getY() : ((LineTo) pe).getY();

        return new Node(x,y,connectingEdges,numberOfNodes+1);
    }

    /**
     * Gets line between two path elements
     *
     * @param pathElements : A list of the two path elements
     * @param numberOfNodes : Number of nodes on the gameboard
     * @return a Line object
     * @author Thea Birk Berger
     */
    Line getLineBetweenPathElements(List<PathElement> pathElements, int numberOfNodes) {
        PathElement pe1 = pathElements.get(0);
        PathElement pe2 = pathElements.get(1);

        Node pathCoor1 = getCoordinates(pe1, 0, numberOfNodes);
        Node pathCoor2 = getCoordinates(pe2, 0, numberOfNodes);
        return createLineBetweenNodes(pathCoor1, pathCoor2);
    }

    /**
     * Gets line equation (y = ax + b) coefficients
     *
     * @param line
     * @return double[] = {a,b}
     * @author Thea Birk Berger
     */
    private double[] getLineCoefficients(Line line) {
        double x1 = line.getStartX();
        double x2 = line.getEndX();
        double y1 = line.getStartY();
        double y2 = line.getEndY();
        double a = (y2-y1)/(x2-x1);
        double b = y2 - a * x2;

        return new double[]{a,b};
    }

    /**
     * Determines if two Circle object collides (hence overlap)
     *
     * @param circle1 : A circle object
     * @param circle2 : A circle object
     * @return true if the circle objects collide, false otherwise
     * @author Thea Birk Berger
     */
    boolean twoCirclesCollide(Circle circle1, Circle circle2) {

        double x1 = circle1.getCenterX();
        double y1 = circle1.getCenterY();
        double x2 = circle2.getCenterX();
        double y2 = circle2.getCenterY();
        double centerDistance = getDistanceBetweenTwoPoints(x1,y1,x2,y2);

        return centerDistance <= circle1.getRadius() + circle2.getRadius();
    }

    /**
     * Determines if a Line and Circle object collides (hence overlap)
     *
     * @param line : A line object
     * @param circle : A circle object
     * @return true if the objects collide, false otherwise
     * @author Thea Birk Berger
     */
    boolean lineAndCircleCollide(Shape line, Shape circle) {

        double a = ((Circle) circle).getCenterX();
        double b = ((Circle) circle).getCenterY();
        double r = ((Circle) circle).getRadius();

        double m = getLineCoefficients((Line) line)[0];
        double d = getLineCoefficients((Line) line)[1];

        double discriminant = Math.pow(r,2) * (1 + Math.pow(m,2)) - Math.pow(b - m * a - d, 2);

        if (discriminant < 0) { return false; }

        // Determine intersection points (x1,y1) and (x2,y2)
        double x1 = (a + b * m - d * m + Math.sqrt(discriminant)) / (1 + Math.pow(m,2));
        double y1 = (d + a * m + b * Math.pow(m,2) + m * Math.sqrt(discriminant)) / (1 + Math.pow(m,2));
        double x2 = (a + b * m - d * m - Math.sqrt(discriminant)) / (1 + Math.pow(m,2));
        double y2 = (d + a * m + b * Math.pow(m,2) - m * Math.sqrt(discriminant)) / (1 + Math.pow(m,2));

        // Check if intersection points is on the line edge
        return line.contains(x1,y1) || line.contains(x2,y2);
    }


    /**
     * Determines whether an automatically drawn edge should be a line or a circle
     * and initializes the creation
     *
     * @param startNode : Start node
     * @param endNode : End node
     * @return the new edge as a Shape object
     * @author Thea Birk Berger
     */
    public Shape createEdgeBetweenNodes(Node startNode, Node endNode) {
        if (startNode.getId() == endNode.getId()) {
            return createCircleToDraw(startNode);
        } else {
            return createLineBetweenNodeContours(startNode, endNode);
        }
    }

}
