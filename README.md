# Alumni

This is my project of an web application that facilitates the communcation of parents, structured as a chat.

## Technologies - Languages+Frameworks ##
*Java 8 / Spring + Junit 5* - for this backend.  
*Typescript / Angular 9*  - frontend - the socketio compat. version is dependent on the framework version used).  
*SocketIO* - netty version -> 1.7.17
Check the compatible version on the frontend repo for JavaScript.
## Security
This is just a stub. A new project will be consistent with current solutions on market.
### TLS for HTTPS on localhost
```
keytool -genkeypair -alias alumni -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore alumni.p12 -validity 3650
```
And steps according to 
* [tutorial](https://www.baeldung.com/spring-boot-https-self-signed-certificate)

Properties for spring:
```server:
  ssl:
    key-store-type: PKCS12
    key-store: classpath:keystore/alumni.p12
    key-store-password: student
    key-alias: alumni
    enabled: true
```


Installation of mkcert:
``` choco install mkcert```

###Creating a new local CA  
The local CA is now installed in the system trust store!
The local CA is now installed in Java's trust store!  
```mkcert -install```  
Creating the PKCS12 certificate:
```
mkcert -cert-file cert_localhost.pem -key-file key_localhost.pem -p12-file localhost.p12 localhost
```
If you want to change file name, as for an ip, use
* [github-mkcert](https://github.com/FiloSottile/mkcert/pull/77)

The resulting file should be moved to the resources folder of the spring app.
Properties for spring:
```
server:

  ssl:
    key-store-type: PKCS12
    key-store: classpath:keystore/localhost.p12
    key-store-password: changeit
```
It worked on localhost!
The project works on docker with a different configuration.




