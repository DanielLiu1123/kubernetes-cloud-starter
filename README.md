# kubernetes-cloud-starter

[![Build](https://img.shields.io/github/workflow/status/DanielLiu1123/kubernetes-cloud-starter/Build/main)](https://github.com/DanielLiu1123/kubernetes-cloud-starter/actions)
[![Maven Central](https://img.shields.io/maven-central/v/com.freemanan/kubernetes-config-cloud-starter?versionPrefix=3.0)](https://search.maven.org/artifact/com.freemanan/kubernetes-config-cloud-starter)
[![Maven Central](https://img.shields.io/maven-central/v/com.freemanan/kubernetes-config-cloud-starter?versionPrefix=2.6)](https://search.maven.org/artifact/com.freemanan/kubernetes-config-cloud-starter)
[![Maven Central](https://img.shields.io/maven-central/v/com.freemanan/kubernetes-config-cloud-starter?versionPrefix=2.4)](https://search.maven.org/artifact/com.freemanan/kubernetes-config-cloud-starter)
[![License](https://img.shields.io/github/license/DanielLiu1123/kubernetes-cloud-starter)](./LICENSE)

[English](./README.md) | [中文](./README-zh-CN.md)

## Modules

- [kubernetes-config-cloud-starter](#kubernetes-config-cloud-starter)

  The main purpose of this module is to use Kubernetes ConfigMap/Secret as a distributed configuration center to achieve
  dynamic configuration updates without restarting the application.

### kubernetes-config-cloud-starter

#### Usage

Maven users:

```xml

<dependency>
    <groupId>com.freemanan</groupId>
    <artifactId>kubernetes-config-cloud-starter</artifactId>
    <version>2.6.2</version>
</dependency>
```

Gradle users:

```groovy
implementation 'com.freemanan:kubernetes-config-cloud-starter:2.6.2'
```

#### Quick Start

First you need a Kubernetes cluster, you can use [docker-desktop](https://www.docker.com/products/docker-desktop/)
or [minikube](https://minikube.sigs.k8s.io/docs/) to create a cluster.

1. Clone the project

    ```bash
    git clone --depth=1 https://github.com/DanielLiu1123/kubernetes-cloud-starter.git
    cd kubernetes-cloud-starter
    ```

2. Create Role and RoleBinding for ServiceAccount
    ```bash
    # For the example, we created a ClusterRole, but in fact, you can control resources more finely, only need the get,list,watch permissions of ConfigMap/Secret
    kubectl create clusterrole config-cluster-reader --verb=get,list,watch --resource=configmaps,secrets
    # Bind ClusterRole to ServiceAccount (namespace: default, name: default)
    kubectl create clusterrolebinding config-cluster-reader-default-default --clusterrole config-cluster-reader --serviceaccount default:default
    ```

3. Build and Start
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

4. Add a ConfigMap
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

5. Delete Resources
    ```shell
    # Delete all resources created by the above operations
    kubectl delete -f examples/kubernetes-config/deployment.yaml
    kubectl delete -f examples/kubernetes-config/configmap-example-01.yaml
    kubectl delete clusterrole config-cluster-reader
    kubectl delete clusterrolebinding config-cluster-reader-default-default
    ```

#### Main Features

- Dynamic update configuration（ConfigMap/Secret）

  You can manually configure whether to monitor configuration file changes.

- Configuration priority

  Through configuration, choose to use local configuration or remote configuration first.

- Supports multiple configuration file formats

  Supports configuration files in `yaml`, `properties`, `json` and key-value pair.

#### Best Practices

Spring Cloud provides the capability of dynamically refreshing the Environment at runtime, which mainly dynamically
updates the properties of two types of beans:

- Beans annotated with @ConfigurationProperties
- Beans annotated with @RefreshScope

A good practice is to use `@ConfigurationProperties` to organize your configurations.

In general, the configuration of an application falls into two categories:

- Basic configuration

    - public

      It can be managed through the configuration center or the jar package. Generally, there is no need for
      dynamic updates, such as Tomcat connection pool parameters, database connection pool parameters, etc.

    - private

      It can be placed in a local configuration file, such as database connection information. This type of
      configuration is generally sensitive and can be managed through Kubernetes Secret.

- Business configuration

  This type of configuration should be strongly related to the business logic, but users need to judge whether they need
  to be
  placed in the configuration center and whether there is a need for dynamic updates.

## Versions

Mainly maintains versions: `3.0.x`, `2.6.x`, `2.4.x`

| Branch | Support Spring Boot Version | Latest Version  |
|:------:|:---------------------------:|:---------------:|
|  main  |            3.0.x            | not release yet |
| 2.6.x  |       [2.6.0, 3.0.0)        |      2.6.2      |
| 2.4.x  |       [2.4.0, 2.6.0)        |      2.4.2      |

Choose the corresponding version according to the version of Spring Boot you are using. For example, if you are using
Spring Boot 2.4.x, then you can use any version of the 2.4.x branch, but please try to use the latest version.
