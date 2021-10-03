package org.hueho.sandbox.stupid.multi.shapes;

public class Rectangle extends Shape{
    private final double height, width;

    public Rectangle(double height, double width) {
        this.height = height;
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public boolean isSquare() {
        return height == width;
    }

    @Override
    public double area() {
        return height * width;
    }
}
