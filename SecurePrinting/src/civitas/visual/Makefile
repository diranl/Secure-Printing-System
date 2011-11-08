#
# A simple makefile for compiling three java classes
#

# define a makefile variable for the java compiler
#
JCC = javac

# define a makefile variable for compilation flags
# the -g flag compiles with debugging information
#
JFLAGS = -g

# typing 'make' will invoke the first target entry in the makefile 
# (the default one in this case)
#
default: Bitmap.class BasisMatrix.class Matrix.class SecurePrinting.class 

# this target entry builds the Average class
# the Average.class file is dependent on the Average.java file
# and the rule associated with this entry gives the command to create it
#
Bitmap.class: Bitmap.java
	$(JCC) $(JFLAGS) Bitmap.java

BasisMatrix.class: BasisMatrix.java
	$(JCC) $(JFLAGS) BasisMatrix.java

Matrix.class: Matrix.java
	$(JCC) $(JFLAGS) Matrix.java

SecurePrinting.class: SecurePrinting.java
	$(JCC) $(JFLAGS) SecurePrinting.java

# To start over from scratch, type 'make clean'.  
# Removes all .class files, so that the next make rebuilds them
#
clean: 
	$(RM) *.class *.bmp
