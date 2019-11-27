# UpgradeJavaChallenge

###Setup 

```shell script
docker run -p 3307:3306 --name mysql-db -e MYSQL_ROOT_PASSWORD=root -d mysql:5.6
```

###To run the application 
```shell script
mvn spring-boot:run
``` 

