package org.hueho.sandbox.stupid.multi.shapes;

import org.hueho.sandbox.stupid.multi.annotations.Multimethod;
import org.hueho.sandbox.stupid.multi.annotations.MultimethodFor;

public abstract class ShapeTool {
    @Multimethod
    public abstract String compare(Shape a, Shape b);

    @MultimethodFor("compare")
    private String compareCircles(Circle a, Circle b) {
        return "the radius difference is " + Math.abs(a.getRadius() - b.getRadius());
    }

    @MultimethodFor("compare")
    private String compareTriangles(Triangle a, Triangle b) {
        return String.format("the area difference is aproximately %.5g", Math.abs(a.area() - b.area()));
    }

    @MultimethodFor("compare")
    private String compareCircleAndTriangle(Circle a, Triangle b) {
        var p = b.getSideA() + b.getSideB() + b.getSideC();
        var s = p / 2;

        if ((a.area() / s) < a.getRadius()) {
            return "the circle fits the triangle";
        } else {
            return "the circle does not fit the triangle";
        }
    }

    @MultimethodFor("compare")
    private String compareTriangleAndCircle(Triangle a, Circle b) {
        return this.compareCircleAndTriangle(b, a);
    }
}
