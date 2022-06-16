# Evidence Management Annotation App
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/hmcts/em-annotation-app.svg?branch=master)](https://travis-ci.org/hmcts/em-annotation-app)
[![codecov](https://codecov.io/gh/hmcts/em-annotation-app/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/em-annotation-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8a50dd2a7b9144029e8547bf019fe2c7)](https://www.codacy.com/app/HMCTS/em-annotation-app)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/8a50dd2a7b9144029e8547bf019fe2c7)](https://www.codacy.com/app/HMCTS/em-annotation-app)
[![Known Vulnerabilities](https://snyk.io/test/github/hmcts/em-annotation-app/badge.svg)](https://snyk.io/test/github/hmcts/em-annotation-app)

Annotation API is a backend service to store and retrieve annotations.

## Quickstart.
#### To clone repo and prepare to pull containers:
```bash
git clone https://github.com/hmcts/em-annotation-app.git
cd em-annotation-app/
brew install jq
az login
az acr login --name hmctspublic
```

There are two methods to start the containers and run the application. 

Method 1 is the recommended option for improved performance and debugging.
Method 2 is currently maintained for support.

#### To start the docker containers:

Method 1:
```
docker-compose -f docker-compose-dependencies-db.yml pull
docker-compose -f docker-compose-dependencies-db.yml up
```

Method 2:
```
./bin/start-local-environment.sh
```
#### Clean and build for both methods:
```
./gradlew clean
./gradlew build
```

#### Run below command to setup the db for both methods:
```
./gradlew migratePostgresDatabase
```

#### To start the application:

Method 1:
```
./gradlew bootWithCCD
```

Method 2:
```
./gradlew bootRun
```

#### To remove docker containers after stopping the run:
Method 1:
```
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -f status=exited -q)
```

Method 2:
```
docker-compose -f docker-compose-dependencies.yml down
```

### Swagger UI
To view our REST API go to http://{HOST}/swagger-ui/index.html
On local machine with server up and running, link to swagger is as below
> http://localhost:8080/swagger-ui/index.html
> if running on AAT, replace localhost with ingressHost data inside values.yaml class in the necessary component, making sure port number is also removed.

### API Endpoints
A list of our endpoints can be found here
> https://hmcts.github.io/reform-api-docs/specs/rpa-em-annotation-app.json

### Running contract or pact tests:

You can run contract or pact tests as follows:
```
./gradlew clean
```

```
./gradlew contract
```

You can then publish your pact tests locally by first running the pact docker-compose:

```
docker-compose -f docker-pactbroker-compose.yml up
```


and then using it to publish your tests:

```
./gradlew pactPublish
```

### Tech

It uses:

* Java11
* Spring boot
* Junit, Mockito and SpringBootTest and Powermockito
* Gradle
* [lombok project](https://projectlombok.org/) - Lombok project

### Plugins
* [lombok plugin](https://plugins.jetbrains.com/idea/plugin/6317-lombok-plugin) - Lombok IDEA plugin

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

