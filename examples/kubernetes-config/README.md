# 

```shell
./gradlew clean bootJar

docker build -t kubernetes-config:1.0.0 .

kubectl apply -f deployment.yaml
```