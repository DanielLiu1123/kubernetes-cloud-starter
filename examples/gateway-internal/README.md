## Overview

This example demonstrates how to use `Spring Cloud Gateway` as an api gateway and
integrate `kubernetes-grey-cloud-starter` to achieve grayscale release.

### Start

port mapping:

    gateway: 10000
    web: 80
    api-gateway: 9999
    grey-gateway: 8080
    user: 8081
    pet: 8082/9082

1. Start all services

    - gateway
    - api-gateway
    - user
    - pet(2 instances)
    - grey-gateway

2. Access

   ```bash
   curl http://localhost:10000/api/v1/user/users/1/dogs -H gv:test
   ```
   or
   ```bash
   groovy run.groovy
   ```
   

