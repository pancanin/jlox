package tool;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GenerateVisitorAstCode {
    private static final int WRONG_INVOKE_STATUS_CODE = 64;
    private static final int IO_ERR_STATUS_CODE = 5;
    private static final String lineSeparator = System.lineSeparator();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(WRONG_INVOKE_STATUS_CODE);
        }
        final String outputDir = args[0];

        try {
            final long start = System.nanoTime();
            writeVisitorAstFile(outputDir, "Expr", new HashMap<String, Collection<String>>() {{
                put("Binary", Arrays.asList("Expr left", "Token operator", "Expr right"));
                put("Grouping", Collections.singletonList("Expr expression"));
                put("Literal", Collections.singletonList("Object value"));
                put("Unary", Arrays.asList("Token operator", "Expr right"));
            }});
            final long end = System.nanoTime();
            
            System.out.println(String.format("Generating classes took %.2f milliseconds.", (end - start) / 1000000.0));
        } catch (Exception e) {
            System.err.println("Failed to generate ast code: " + e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(IO_ERR_STATUS_CODE);
        }
    }

    private static void writeVisitorAstFile(String outputDir, String baseClassName, Map<String, Collection<String>> subclasses) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        final String path = outputDir + '/' + baseClassName + ".java";
        
        try (FileWriter fw = new FileWriter(path)) {
            try (BufferedWriter writer = new BufferedWriter(fw)) {
                writer.write("/* Do not manually change this file as it is generated by a script. */");
                writer.write(lineSeparator);
                writer.write("package jlox.parser;");
                writer.write(lineSeparator);
                writer.write(lineSeparator);
                writer.write("import jlox.scanner.Token;");
                writer.write(lineSeparator);
                writer.write(lineSeparator);
                writer.write("public abstract class ");
                writer.write(baseClassName);
                writer.write(" {");
                writer.write(lineSeparator);
                writer.write("  public abstract <R> R accept(Visitor<R> visitor);");
                writer.write(lineSeparator);

                writeVisitor(writer, baseClassName, subclasses.keySet());

                for (Map.Entry<String, Collection<String>> subclass : subclasses.entrySet()) {
                    generateClass(writer, subclass.getKey(), baseClassName, subclass.getValue());
                    writer.write(lineSeparator);
                }
                writer.write('}');
                writer.write(lineSeparator);
            }
        }
    }

    private static void writeVisitor(Writer writer, String baseName, Collection<String> types) throws IOException {
        writer.write("public interface Visitor<R> {");
        writer.write(lineSeparator);

        for (String type : types) {
            writer.write("\tR visit");
            writer.write(type);
            writer.write(baseName);
            writer.write('(');
            writer.write(type);
            writer.write(' ');
            writer.write(baseName.toLowerCase());
            writer.write(");");
            writer.write(lineSeparator);
        }

        writer.write('}');
        writer.write(lineSeparator);
    }

    private static void generateClass(Writer writer, String className, String baseClassName, Collection<String> fields) throws IOException {
        writer.append("public static class ");
        writer.append(className);
        if (!baseClassName.isEmpty()) {
            writer.append(" extends ");
            writer.append(baseClassName);
        }
        writer.append(" {")
        .append(System.lineSeparator())
        .append('\t')
        .append(className)
        .append('(')
        .append(String.join(", ", fields))
        .append(") {")
        .append(System.lineSeparator());

        for (final String field : fields) {
            String[] typeAndName = field.split(" ");
            writer.append('\t')
            .append('\t')
            .append("this.")
            .append(typeAndName[1])
            .append(" = ")
            .append(typeAndName[1])
            .append(';')
            .append(System.lineSeparator());
        }

        writer.append('\t')
        .append('}')
        .append(System.lineSeparator())
        .append(System.lineSeparator())
        .append("\t@Override")
        .append("\tpublic <R> R accept(Visitor<R> visitor) {")
        .append("\treturn visitor.visit").append(className).append(baseClassName).append("(this);")
        .append("\t}");

        writer.append(System.lineSeparator());

        for (final String field : fields) {
            writer.append('\t')
            .append("public final ")
            .append(field)
            .append(';')
            .append(System.lineSeparator());
        }
        writer.append('}')
        .append(System.lineSeparator());
    }
}
