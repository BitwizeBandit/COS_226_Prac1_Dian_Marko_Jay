JAVAC = javac
JAVA = java
SRC = locks.java
MAIN = locks
BUILD_DIR = out

.PHONY: all run clean

all: $(BUILD_DIR)/$(MAIN).class

$(BUILD_DIR)/$(MAIN).class: $(SRC)
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) $(SRC)

run: all
	$(JAVA) -cp $(BUILD_DIR) $(MAIN)

clean:
	rm -rf $(BUILD_DIR)