
javac -cp servlet-api.jar ie/gmit/sw/*.java

jar -cf dictionary-service.jar ie/gmit/sw/*.class

cls 

java -cp dictionary-service.jar ie.gmit.sw.ServiceSetup

