
# Test the Chisel design
run-test:
	sbt test

# Configure the Basys3 with open source tools

config:
	openocd -f 7series.txt
