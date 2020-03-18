package Exceptions;

public class InvalidFileSyntax extends Exception {
    public InvalidFileSyntax(int lineNumber) {
        super("Invalid syntax on line " + lineNumber);
    }
}

