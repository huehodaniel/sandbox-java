package org.hueho.sandbox.stupid.multi.shapes;

public class Circle extends Shape {
    private final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double area() {
        return 2 * Math.PI * radius;
    }
}
