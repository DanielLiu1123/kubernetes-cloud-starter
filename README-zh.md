# kubernetes-cloud-starter

## kubernetes-config-cloud-starter

这个模块的主要目的是使用 Kubernetes 的 ConfigMap 作为分布式配置中心，在不重启应用的情况下实现配置的动态更新。

### Quick Start

首先你需要一个 Kubernetes 集群，你可以使用 [docker-desktop](https://www.docker.com/products/docker-desktop/)
或者 [minikube](https://minikube.sigs.k8s.io/docs/) 来创建一个集群。

1. 为 ServiceAccount 创建 Role 和 RoleBinding
    ```bash
    # 为了示例我们创建了 ClusterRole，但其实你可以更加精细化地控制资源，只需要 ConfigMap/Secret 的 get,list,watch 权限
    kubectl create clusterrole config-cluster-reader --verb=get,list,watch --resource=configmaps,secrets
    # 绑定 ClusterRole 到 ServiceAccount (namespace: default, name: default)
    kubectl create clusterrolebinding config-cluster-reader-default-default --clusterrole config-cluster-reader --serviceaccount default:default
    ```

2. 构建并启动
    ```shell
    ./gradlew clean bootJar
    
    docker build -f examples/kubernetes-config/Dockerfile -t kubernetes-config:1.0.0 .
    
    kubectl apply -f examples/kubernetes-config/deployment.yaml
    ```
    ```shell
    # 在应用启动后执行下面的命令，启动过程应该会很快（小于3s）
    curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
    
    # 你应该看到响应结果是 `100`
    ```

3. 添加一个 ConfigMap
    ```shell
    # 这个 ConfigMap 正在由当前应用程序所监听，因此当这个 ConfigMap 被添加后，应用会自动更新配置
    kubectl apply -f examples/kubernetes-config/configmap-example-01.yaml
   
    # 再次访问
    curl http://localhost:`kubectl get svc kubernetes-config -o jsonpath='{..nodePort}'`/price
   
    # 你应该看到响应结果是 `200`
    ```
   你可以通过修改 `configmap-example-01.yaml` 中的配置，然后重新 apply 该文件来观察接口结果的变化。

   通过上述操作，你可以看到应用程序不需要重启就可以实现配置的动态更新。

4. 删除资源
    ```shell
    # 删除上述操作创建的所有资源
    kubectl delete -f examples/kubernetes-config/deployment.yaml
    kubectl delete -f examples/kubernetes-config/configmap-example-01.yaml
    kubectl delete clusterrole config-cluster-reader
    kubectl delete clusterrolebinding config-cluster-reader-default-default
    ```

### 主要特性

- 动态更新配置

  可以手动配置是否需要监听配置文件变化。

- 配置优先级

  通过配置选择优先使用本地配置还是远程配置。

### 最佳实践

一般一个应用程序的配置分为两类：

- 基础配置

    - 公有

      可以通过配置中心或者公共 jar 包来管理，一般不会有动态更新的需求，比如 Tomcat 连接池参数、数据库连接池参数等。

    - 私有

      可以放在本地配置文件中，比如数据库连接信息，这类配置一般比较敏感，可以通过 Kubernetes Secret 来管理。

- 业务配置

  这类配置应该与业务强相关，但是需要用户自行判断是否需要放入配置中心，是否有动态更新的需求。
