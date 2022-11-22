### Steps to run the example

First of all, you need a Kubernetes cluster. You can
use [docker-desktop](https://www.docker.com/products/docker-desktop/)
or [minikube](https://kubernetes.io/zh-cn/docs/tutorials/hello-minikube/).

1. Create role and role binding for the service account
    ```bash
    # we need get,list,watch permissions of configmaps
    kubectl create clusterrole configmap-cluster-reader --verb=get,list,watch --resource=configmaps
    # bind the ClusterRole to the service account (namespace: default, name: default)
    kubectl create clusterrolebinding configmap-cluster-reader-default-default --clusterrole configmap-cluster-reader --serviceaccount default:default
    ```

2. Start
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

3. Add a configmap
    ```shell
    kubectl apply -f examples/kubernetes-config/configmap-example-01.yaml
   
    curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
   
    # you should see the response is `200`
    ```

4. Delete the resources
    ```shell
    kubectl delete -f examples/kubernetes-config/deployment.yaml
    kubectl delete -f examples/kubernetes-config/configmap-example-01.yaml
    kubectl delete clusterrole configmap-cluster-reader
    kubectl delete clusterrolebinding configmap-cluster-reader-default-default
    ```
