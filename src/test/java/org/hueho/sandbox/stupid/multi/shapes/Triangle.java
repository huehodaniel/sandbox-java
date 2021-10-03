package org.hueho.sandbox.stupid.multi.shapes;

public class Triangle extends Shape {
    private final double sideA, sideB, sideC;

    public Triangle(double sideA, double sideB, double sideC) {
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    public double getSideA() {
        return sideA;
    }

    public double getSideB() {
        return sideB;
    }

    public double getSideC() {
        return sideC;
    }

    @Override
    public double area() {
        var p = sideA + sideB +sideC;
        var s = p / 2;

        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }
}
