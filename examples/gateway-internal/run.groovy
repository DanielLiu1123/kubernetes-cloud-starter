#!/usr/bin/env groovy
import java.util.concurrent.CompletableFuture

def result = Collections.synchronizedMap(new TreeMap())

def queue = new ArrayList<CompletableFuture>()

(1..50).each {
    queue << CompletableFuture.runAsync({
        def resp = "curl http://localhost:10000/api/v1/user/users/1/dogs -H gv:test".execute().text
        result.merge(resp, 1, { a, b -> a + b })
    })
}

CompletableFuture.allOf(queue.toArray(CompletableFuture[]::new)).join()

result.forEach { k, v -> println "$k : $v" }
