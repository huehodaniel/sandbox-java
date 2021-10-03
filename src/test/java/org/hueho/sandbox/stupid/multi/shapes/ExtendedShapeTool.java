package org.hueho.sandbox.stupid.multi.shapes;

import org.hueho.sandbox.stupid.multi.annotations.MultimethodFor;

public abstract class ExtendedShapeTool extends ShapeTool {
    @MultimethodFor("compare")
    private String compareRectangle(Rectangle a, Rectangle b) {
        if(a.isSquare() == b.isSquare()) {
            if(a.isSquare()) return "both rectangles are squares";
            else return "both rectangles aren't squares";
        }

        return "the area difference is " + Math.abs(a.area() - b.area());
    }

    @MultimethodFor(value = "compare", deriveMirror = true)
    private String compareCircleAndRectangle(Circle a, Rectangle b) {
        var diameter = a.getRadius() * 2;
        if (b.isSquare() && diameter <= Math.sqrt(b.area())) {
            return "the circle fits the rectangle";
        } else if (diameter <= b.getHeight() && diameter <= b.getWidth()){
            return "the circle fits the rectangle";
        } else {
            return "the circle does not fit the rectangle";
        }
    }
}
