4Year Distributed Systems  
Asynchronous RMI Dictionary Service
----------------------------------------------------------------------------------------

GitHub
------
https://github.com/alexpt2000gmit/4Year_DistributedSystems_AsynchronousRMIDictionaryService


Application deployed on Amazon
------------------------------
http://54.210.4.37:8080/job-server/


Overview
--------
A JSP page should provide users with the ability to specify a string which will be checked 
against the dictionary. The HTML form information should be dispatched to a servlet that adds 
the client request to an in-queue and then returns a job ID to the web client. The web client 
should poll the web server periodically (every 10 seconds) and query if the request has been 
processed. Client requests in the inQueue should be periodically removed and processed 
(every 10 seconds).




INSTRUCTIONS FOR USE 
--------------------
1 - DOWNLOAD THE FOLLOWING FILES:
    - dictionary-service.jar
    - job-server.war
    - WebstersUnabridgedDictionary.txt

2 - STARTING THE SERVER: 
    $ java -cp dictionary-service.jar ie.gmit.sw.ServiceSetup

3 - STARTING THE TOMCAT 

    After install the Tomcat 8.5, copy the file "job-server.war" into the folder 
    "C:\Program Files\Apache Software Foundation\Tomcat 8.5\webapps" , and start the Tomcat.

4 - OPEN THE BROWSER

    http://localhost:8080/job-server/




HOW TO CREATE AN EXECUTABLE "JAR" AND "WAR" FILES 
-------------------------------------------------

CEATING "JAR" FILE:
    Inside "SRC folder" - you can create the "Class" file using the following command from :

    $ javac -cp servlet-api.jar ie/gmit/sw/*.java
    $ jar -cf dictionary-service.jar ie/gmit/sw/*.class


CEATING "WAR" FILE:
Into de folder "WebContent":
    $ jar –cf job-server.war *



Author
--------------------------------------------------
    Alexander Souza
    
        - G00317835@gmit.ie
        - alexpt2000@gmail.com
        - https://github.com/alexpt2000gmit
        - https://github.com/alexpt2000
        - www.linkedin.com/in/alexander-souza-3a841539/


