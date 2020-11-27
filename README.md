# MiniC 编译器说明
## 简介
MiniC是一个简易的编译器程序，能够实现C语言语法的一个子集。
## 语法说明：
$$
program \rightarrow block \\
block \rightarrow {decls \space stmts}\\
decls \rightarrow decls \space decl | \epsilon \\
decl \rightarrow type \space id;\\
type \rightarrow type[num] | basic \\
stmts	\rightarrow stmts \space stmt | \epsilon \\
$$



## 目标
1. 丰富语法：for,stmt,decl,hanshu
2. 记录行号
3. 删除评论