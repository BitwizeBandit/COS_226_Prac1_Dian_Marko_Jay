JAVAC = javac
JAVA  = java
SRC   = $(wildcard *.java)

.PHONY: all compile run clean

all: run

compile:
	$(JAVAC) $(SRC)

run: compile
	$(JAVA) Main

clean:
	rm -f *.class