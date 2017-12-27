# 4Year Advanced Object-Oriented Design Principles & Patterns
## A JEE Application for Measuring Document Similarity

### Overview
You are required to develop a Java web application that enables two or more text documents to
be compared for similarity.

## Application deployed on Amazon
![](https://a0.awsstatic.com/main/images/logos/aws_logo_179x109.gif)
### http://54.210.4.37:8080/jaccard/

![Screencast](Screencast/client.gif)

## INSTRUCTIONS FOR USE

### 1 - DOWNLOAD THE FOLLOWING FILES:
 - jaccard.war
 - SampleDocs.zip   (optional)

![Screencast](Screencast/files.png)

### 2 - STARTING THE TOMCAT 

After install the Tomcat, copy the file "jaccard.war" into the folder 
#### Windows
```
"C:\Program Files\Apache Software Foundation\Tomcat\webapps" 
```

#### Mac
```
/usr/local/User/tomcat/8.0.30/libexec/webapps
```

#### Linux
```
/var/lib/tomcat9/webapps/
```

Now start the Tomcat.

![Screencast](Screencast/tomcat.gif)


### 3 - OPEN THE BROWSER
```
http://localhost:8080/jaccard/
```

## HOW TO CREATE AN EXECUTABLE "WAR" FILE

### CEATING "WAR" FILE:

Into de folder "WebContent":
```
jar –cf jaccard.war *
```

# Author
### Alexander Souza
- G00317835@gmit.ie
- alexpt2000@gmail.com
- https://github.com/alexpt2000gmit
- https://github.com/alexpt2000
- www.linkedin.com/in/alexander-souza-3a841539/



