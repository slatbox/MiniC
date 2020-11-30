@echo off

set output_path=./classes
set full_files=./src/main/*.java ./src/inter/*.java ./src/lexer/*.java ./src/parser/*.java ./src/symbols/*.java 

javac  -encoding UTF-8 %full_files% -d %output_path%
pause

