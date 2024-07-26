#!/bin/bash
# Go to the root directory of the project
cd ..
javac ./src/main/java/tool/*.java -d target/classes
java -cp target/classes tool.GenerateVisitorAstCode ./src/main/java/interpreter/parser
# This script needs a lot of changes when the class name of the generator changes.
# Or any of the paths change.
