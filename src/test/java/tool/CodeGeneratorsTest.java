package tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorsTest {
    @Test
    public void testGenerateClass() {
        final String nl = System.lineSeparator();
        String classSource = CodeGenerators.generateClass("Binary", "Expr", Arrays.asList("Expr left", "Token operator", "Expr right"));
        String expected = "class Binary extends Expr {" + nl +
                "\tBinary(Expr left, Token operator, Expr right) {" + nl +
                "\tthis.left = left;" + nl +
                "\tthis.operator = operator;" + nl +
                "\tthis.right = right;" + nl +
                "}" + nl +
                nl +
                "\tfinal Expr left;" + nl +
                "\tfinal Token operator;" + nl +
                "\tfinal Expr right;" + nl +
                "}";
        Assertions.assertEquals(expected, classSource);
    }
}