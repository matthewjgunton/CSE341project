all: dev

dev: *.java
	javac *.java
	jar cfmv mjg422.jar Manifest.txt project.class Database.class Customer.class Transaction.class Location.class Debt.class
	mv mjg422.jar ..
