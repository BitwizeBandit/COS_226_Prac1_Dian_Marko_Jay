# COS 226 - Practical 3 (Test-and-Set Locks and Contention)
# Dian le Roux (25147065), Marko de Swardt (24658562), Jay Macaskill (25198387)

# Usage:
#   make          -> compiles all .java files
#   make run      -> compiles (if needed) and runs Main
#   make clean    -> removes all compiled .class files

JAVAC   = javac
JAVA    = java
SOURCES = $(wildcard *.java)
CLASSES = $(SOURCES:.java=.class)

.PHONY: all run clean

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $(SOURCES)

run: all
	$(JAVA) Main

clean:
	rm -f *.class