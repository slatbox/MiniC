@echo off
set workpath=C:\school_work\computer_learning\Java\MiniC\src 
set inter_path=%workpath%\inter 
set lexer_path=%workpath%\lexer 
set main_path=%workpath%\main 
set parser_path=%workpath%\parser 
set symbols_path=%workpath%\symbols

set output_path=C:\school_work\computer_learning\Java\MiniC\classes
set full_files=%main_path%\*.java %inter_path%\*.java %lexer_path%\*.java %parser_path%\*.java  %symbols_path%\*.java 

javac  -encoding UTF-8 ./src/main/*.java ./src/inter/*.java ./src/lexer/*.java ./src/parser/*.java ./src/symbols/*.java 

pause
