# kubernetes-cloud-starter

[中文文档](./README-zh.md)

## kubernetes-config-cloud-starter

The main purpose of this module is to use Kubernetes ConfigMap as a distributed configuration center to achieve dynamic
configuration updates without restarting the application.

### Quick Start

First you need a Kubernetes cluster, you can use [docker-desktop](https://www.docker.com/products/docker-desktop/)
or [minikube](https://minikube.sigs.k8s.io/docs/) to create a cluster.

1. Create Role and RoleBinding for ServiceAccount
    ```bash
    # For example, we created a ClusterRole, but in fact, you can control resources more finely, only need the get,list,watch permissions of ConfigMap
    kubectl create clusterrole configmap-cluster-reader --verb=get,list,watch --resource=configmaps
    # Bind ClusterRole to ServiceAccount (namespace: default, name: default)
    kubectl create clusterrolebinding configmap-cluster-reader-default-default --clusterrole configmap-cluster-reader --serviceaccount default:default
    ```

2. Build and Start
    ```shell
    ./gradlew clean bootJar
    
    docker build -f examples/kubernetes-config/Dockerfile -t kubernetes-config:1.0.0 .
    
    kubectl apply -f examples/kubernetes-config/deployment.yaml
    ```
    ```shell
    # Execute the following command after the application startup, the startup process should be very fast (less than 3s)
    curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
    
    # You should see a response of `100`
    ```

3. Add a ConfigMap
    ```shell
    # This ConfigMap is being monitored by the current application, so when this ConfigMap is added, the application will automatically update the configuration
    kubectl apply -f examples/kubernetes-config/configmap-example-01.yaml
   
    # Visit again
    curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
   
    # You should see a response of `200`
    ```
   You can modify the configuration in `configmap-example-01.yaml`, and then re-apply the file to observe the change of
   the interface result.

   Through the above operations, you can see that the application can dynamically update the configuration without
   restarting.

4. Delete Resources
    ```shell
    # Delete all resources created by the above operations
    kubectl delete -f examples/kubernetes-config/deployment.yaml
    kubectl delete -f examples/kubernetes-config/configmap-example-01.yaml
    kubectl delete clusterrole configmap-cluster-reader
    kubectl delete clusterrolebinding configmap-cluster-reader-default-default
    ```