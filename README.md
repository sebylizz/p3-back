# Leghetto webshop backend

## Running the application in live mode (Recommended)

Using the Maven wrapper included in the project, you can run application in live dev mode with:

```shell script
./mvnw quarkus:dev
```

The application will default to run on port 8080, which is also the expected port from the frontend app. The database is currently running locally on a Ubuntu server, which will be online until 31/1.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.
