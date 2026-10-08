# COS 226 - Assignment 2 (Concurrent Queues and Producer-Consumer Systems)
# Usage:
#   make -> compiles all .java files
#   make run -> runs Main with defaults (BOUNDED 4 4)
#   make experiment -> runs the full Task 3 comparison
#   make clean -> removes compiled .class files

JAVAC   = javac
JAVA    = java
SOURCES = $(wildcard *.java)

.PHONY: all run experiment clean

all:
	$(JAVAC) $(SOURCES)

run: all
	$(JAVA) Main

experiment: all
	$(JAVA) ExperimentRunner

clean:
	rm -f *.class