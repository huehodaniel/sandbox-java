package org.hueho.sandbox.stupid.multi;

import org.hueho.sandbox.stupid.multi.exceptions.MultiException;
import org.hueho.sandbox.stupid.multi.shapes.*;
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

        Assertions.assertThrows(MultiException.class, () -> shapetool.compare(new Circle(10), null));
    }

    @Test
    void testExtendedShapeTool() {
        var shapetool = Multi.get(ExtendedShapeTool.class);

        Assertions.assertEquals("the area difference is aproximately 6.3224",
                shapetool.compare(new Triangle(10, 10, 10), new Triangle(8, 15, 10)));
        Assertions.assertEquals("both rectangles aren't squares",
                shapetool.compare(new Rectangle(7, 10), new Rectangle(8, 7)));
        Assertions.assertEquals("the circle does not fit the rectangle",
                shapetool.compare(new Circle(4), new Rectangle(8, 7)));
        Assertions.assertEquals("the circle fits the rectangle",
                shapetool.compare(new Rectangle(4, 4), new Circle(2)));

    }

    @Test
    void testEvenMoreExtendedShapeTool() {
        var shapetool = Multi.get(EvenMoreExtendedTool.class);

        Assertions.assertEquals("the area difference is aproximately 6.3224",
                shapetool.compare(new Triangle(10, 10, 10), new Triangle(8, 15, 10)));
        Assertions.assertEquals("both rectangles aren't squares",
                shapetool.compare(new Rectangle(7, 10), new Rectangle(8, 7)));
        Assertions.assertEquals("the area difference between this rectangle and this triangle is aproximately 12.699",
                shapetool.compare(new Rectangle(8, 7), new Triangle(10, 10, 10)));

        Triangle triangle = new Triangle(1, 1, 1);
        Rectangle rectangle = new Rectangle(1, 1);
        Circle circle = new Circle(1);

        Assertions.assertEquals(triangle, shapetool.choose(rectangle, triangle, circle));
        Assertions.assertEquals(circle, shapetool.choose(rectangle, rectangle, circle));
    }
}
