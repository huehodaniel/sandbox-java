package org.hueho.sandbox.stupid.multi.shapes;

import org.hueho.sandbox.stupid.multi.annotations.Multimethod;
import org.hueho.sandbox.stupid.multi.annotations.MultimethodFor;

public abstract class EvenMoreExtendedTool extends ExtendedShapeTool {
    @Multimethod
    public abstract Shape choose(Shape a, Shape b, Shape c);

    @MultimethodFor("compare")
    private String compareRectangleAndTriangle(Rectangle a, Triangle b) {
        return String.format("the area difference between this rectangle and this triangle is aproximately %.5g",
                Math.abs(a.area() - b.area()));
    }

    @MultimethodFor("choose")
    private Shape choose(Rectangle a, Rectangle b, Circle c) {
        return c;
    }

    @MultimethodFor("choose")
    private Shape choose(Rectangle a, Triangle b, Circle c) {
        return b;
    }
}
