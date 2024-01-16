package tool;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateAst {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        try {
            defineAst(outputDir, "Expr", new HashMap<String, List<String>>() {{
                put("Binary", Arrays.asList("Expr left", "Token operator", "Expr right"));
                put("Grouping", Collections.singletonList("Expr expression"));
                put("Literal", Collections.singletonList("Object value"));
                put("Unary", Arrays.asList("Token operator", "Expr right"));
            }});
        } catch (Exception e) {
            System.err.println("Failed to generate ast code: " + e.getMessage());
            System.exit(64);
        }
    }

    private static void defineAst(String outputDir, String baseClassName, Map<String, List<String>> subclasses) throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/" + baseClassName + ".java";
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("package main.parser;");
            writer.println("import main.scanner.Token;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println("abstract class " + baseClassName + " {");
            writer.println();
            writer.println("  abstract <R> R accept(Visitor<R> visitor);");

            defineVisitor(writer, baseClassName, new ArrayList<>(subclasses.keySet()));

            for (Map.Entry<String, List<String>> subclass : subclasses.entrySet()) {
                writer.println(CodeGenerators.generateClass(subclass.getKey(), baseClassName, subclass.getValue()));
            }
            writer.println("}");
        }
    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types) {
        writer.println("interface Visitor<R> {");

        for (String type : types) {
            writer.println("\tR visit" + type + baseName + "(" +
                    type + " " + baseName.toLowerCase() + ");");
        }

        writer.println("}");
    }
}
