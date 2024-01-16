package tool;

import java.util.List;

public class CodeGenerators {
    public static String generateClass(String className, String baseClassName, List<String> fields) {
        StringBuilder classSource = new StringBuilder();
        classSource.append("static class ");
        classSource.append(className);
        if (!baseClassName.isEmpty()) {
            classSource.append(" extends ");
            classSource.append(baseClassName);
        }
        classSource.append(" {")
        .append(System.lineSeparator())
        .append('\t')
        .append(className)
        .append('(')
        .append(String.join(", ", fields))
        .append(") {")
        .append(System.lineSeparator());

        for (String field : fields) {
            String[] typeAndName = field.split(" ");
            classSource.append('\t')
            .append('\t')
            .append("this.")
            .append(typeAndName[1])
            .append(" = ")
            .append(typeAndName[1])
            .append(';')
            .append(System.lineSeparator());
        }

        classSource.append('\t')
        .append('}')
        .append(System.lineSeparator())

        .append(System.lineSeparator())
        .append("\t@Override")
        .append("\t<R> R accept(Visitor<R> visitor) {")
        .append("\treturn visitor.visit").append(className).append(baseClassName).append("(this);")
        .append("\t}");

        classSource.append(System.lineSeparator());

        for (String field : fields) {
            classSource.append('\t')
            .append("final ")
            .append(field)
            .append(';')
            .append(System.lineSeparator());
        }
        classSource.append('}')
        .append(System.lineSeparator());

        return classSource.toString();
    }
}
