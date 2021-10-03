package org.hueho.sandbox.stupid.multi;

import org.hueho.sandbox.stupid.multi.shapes.Circle;
import org.hueho.sandbox.stupid.multi.shapes.ShapeTool;
import org.hueho.sandbox.stupid.multi.shapes.Triangle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShapeTest {
    @Test
    void testShapeTool() {
        var shapetool =  Multi.get(ShapeTool.class);
        Assertions.assertNotNull(shapetool);

        Assertions.assertEquals("the radius difference is 10.0",
                shapetool.compare(new Circle(10), new Circle(20)));
        Assertions.assertEquals("the area difference is aproximately 6.3224",
                shapetool.compare(new Triangle(10, 10, 10), new Triangle(8, 15, 10)));
        Assertions.assertEquals("the circle does not fit the triangle",
                shapetool.compare(new Circle(2), new Triangle(2, 3, 2)));
        Assertions.assertEquals("the circle fits the triangle",
                shapetool.compare(new Triangle(30, 30, 30), new Circle(2)));
    }
}
