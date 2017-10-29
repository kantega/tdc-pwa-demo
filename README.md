This demo requires [Apache Maven 3.3](https://maven.apache.org/) or later and Java 8 to run.

To run the demo, you must first generate a pair of encryption keys. 
Run no.kantega.tdc2017.keygen.GenerateKeys to get the keys. See 
file src/main/resources/config.properties.example for further instructions.

After the encryption keys are in place, start the server with "mvn jetty:run".
The start page for the demo should be at [http://localhost:8080](http://localhost:8080).

Try stopping the server (use CTRL+C from console) after you've loaded the demo.
You should still get a response from all pages.

The code is implemented with a minimum of frameworks, so it uses only the Java Servlet API
server-side, and plain JavaScript and manual DOM manipulation client-side.
 