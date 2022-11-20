### Steps to run the example

1. Start
    ```shell
    ./gradlew clean bootJar
    
    docker build -f examples/kubernetes-config/Dockerfile -t kubernetes-config:1.0.0 .
    
    kubectl apply -f examples/kubernetes-config/deployment.yaml
    ```
   ```shell
   # after application started up
   curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
   
   # you should see the response is `100`
   ```

2. Add a configmap
    ```shell
    kubectl apply -f examples/kubernetes-config/configmap-example-01.yaml
   
    curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
   
    # you should see the response is `200`
    ```

3. Delete the resources
    ```shell
    kubectl delete -f examples/kubernetes-config/deployment.yaml
    kubectl delete -f examples/kubernetes-config/configmap-example-01.yaml
    ```
