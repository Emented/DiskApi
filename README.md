# DiskApi

Second task for yandex backend development school

## Overview

REST API service for working with a file tree

## Technologies

* Developed with Spring Boot and PostgreSQL database
* Built using Maven
* Containerized using Docker

## Description

The service allows you to operate a database with `SystemItems`

### Available functions

* Create
* Update
* Delete
* Retrieve
* Get history
* Get statistic

## API Doc
The API documentation is available [here](https://github.com/Emented/DiskApi/blob/master/api-doc).

## Install and run

### Clone the repository:

```console
foo@bar:~$ git clone https://github.com/Emented/DiskApi
```

### Run API using Docker Compose:

Before launching the application for the first time,
you need to create `.env` file using the example below

```Properties
POSTGRES_PASSWORD=postgres
POSTGRES_USER=postgres
POSTGRES_DB=disk_api_db

SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/disk_api_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

#### Are you ready? Then let's go!

Building...

```console
foo@bar:~$ mvn clean install
```

Launching Docker Compose

```console
foo@bar:~$ docker-compose up --build
```

### Run API without Docker:

Before launching the application, you need to create a database and change the file [application.properties](https://github.com/Emented/DiskApi/blob/master/src/main/resources/application.properties):

* Put your username in the place of "USERNAME"
* Put your password in the place of "PASSWORD"
* Put your database name in the place of "DATABASE_NAME"

```Properties
spring.datasource.url=jdbc:postgresql://localhost:5432/DATABASE_NAME?useSSL=false&amp&serverTimezone=UTC
spring.datasource.username=USERNAME
spring.datasource.password=PASSWORD
```


#### Have you changed everything? Starting up!

```console
foo@bar:~$ mvn spring-boot:run
```

