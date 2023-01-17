# Spring Boot Server 

## Purpose

This is merely generated to be used for comparison against bare servlet for bonus points.

Changes to this generated project is kept at a minimum:

1. Changes to `SwipeApiController` implementing dummy validation and returning corresponding response codes.
2. Changes to `pom.xml` to adapt to my OpenJDK11 environment.
3. Changes to `Swagger2SpringBoot` because for some reason `SpringBootServletInitializer` wasn't extended and url mapping was failing on Tomcat9.
