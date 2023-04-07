package com.freemanan.kubernetes.grey.common.util;

import com.freemanan.kubernetes.commons.K8s;
import com.freemanan.kubernetes.grey.common.Destination;
import com.freemanan.kubernetes.grey.common.Grey;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import lombok.experimental.UtilityClass;

/**
 * @author Freeman
 */
@UtilityClass
public class GreyUtil {
    private static final Random random = new Random();

    /**
     * @param uri   URI
     * @param greys Grey list
     * @return matched grey, may be null
     */
    public static Grey getMathchedGrey(URI uri, List<Grey> greys) {
        if (greys == null || greys.isEmpty()) {
            return null;
        }
        Destination dest = new Destination();
        dest.setService(service(uri));
        dest.setNamespace(namespace(uri));
        dest.setPort(port(uri));
        for (Grey grey : greys) {
            if (Objects.equals(grey.getMaster(), dest)) {
                return grey;
            }
        }
        return null;
    }

    public static URI grey(URI uri, Grey grey) {
        if (grey == null || grey.getFeatures() == null || grey.getFeatures().isEmpty()) {
            return uri;
        }
        // one feature, no weight
        List<Destination> features = grey.getFeatures();
        if (features.size() == 1) {
            Destination d = features.get(0);
            if (d.getWeight() == null) {
                return greyUri(d, uri);
            }
        }
        return randomGreyUri(uri, grey);
    }

    public static URI grey(URI uri, List<Grey> greys) {
        return Optional.ofNullable(getMathchedGrey(uri, greys))
                .map(grey -> grey(uri, grey))
                .orElse(uri);
    }

    private static double getMasterWeight(Grey grey) {
        if (grey.getMaster().getWeight() != null) {
            return grey.getMaster().getWeight();
        }
        double featuresWeight = 0;
        for (Destination d : grey.getFeatures()) {
            if (d.getWeight() == null) {
                throw new IllegalArgumentException("weight must be set because multiple features found !");
            }
            featuresWeight += d.getWeight();
        }
        if (featuresWeight > 100) {
            throw new IllegalArgumentException("features weight must be less than 100 !");
        }
        return 100 - featuresWeight;
    }

    private static URI randomGreyUri(URI uri, Grey grey) {
        double masterWeight = getMasterWeight(grey);
        double totalWeight = getTotalWeight(grey);

        double r = random.nextDouble() * totalWeight;
        if ((r -= masterWeight) < 0) {
            return uri;
        }
        for (Destination feature : grey.getFeatures()) {
            if ((r -= feature.getWeight()) < 0) {
                return greyUri(feature, uri);
            }
        }
        throw new IllegalStateException("Should never happen !");
    }

    private static double getTotalWeight(Grey grey) {
        double totalWeight = getMasterWeight(grey);
        for (Destination d : grey.getFeatures()) {
            if (d.getWeight() == null) {
                throw new IllegalArgumentException("weight must be set because multiple features found !");
            }
            totalWeight += d.getWeight();
        }
        return totalWeight;
    }

    static URI greyUri(Destination destination, URI uri) {
        String uriStr = uri.toString();

        String svc = service(uri);
        String ns = namespace(uri);

        if (containsNamespace(uri)) {
            uriStr = uriStr.replace(svc + "." + ns, destination.getService() + "." + destination.getNamespace());
        } else {
            // if uri not contains namespace and namespace not changed, don't add namespace manually
            if (Objects.equals(ns, destination.getNamespace())) {
                uriStr = uriStr.replace(svc, destination.getService());
            } else {
                // namespace changed, must add namespace manually
                uriStr = uriStr.replace(svc, destination.getService() + "." + destination.getNamespace());
            }
        }

        if (containsPort(uri)) {
            Integer port = port(uri);
            uriStr = uriStr.replace(
                    ":" + port, ":" + Optional.ofNullable(destination.getPort()).orElse(port));
        }

        return URI.create(uriStr);
    }

    private static boolean containsPort(URI uri) {
        return uri.getPort() != -1;
    }

    private static boolean containsNamespace(URI uri) {
        return uri.getHost().contains(".");
    }

    static Integer port(URI uri) {
        return uri.getPort() != -1 ? uri.getPort() : null;
    }

    static String namespace(URI uri) {
        // http://svc.namespace:port/path
        String[] arr = host(uri).split("\\.");
        if (arr.length >= 2) {
            return arr[1];
        }
        return K8s.currentNamespace();
    }

    static String service(URI uri) {
        // http://svc.namespace:port/path
        String[] arr = host(uri).split("\\.");
        if (arr.length >= 1) {
            return arr[0];
        }
        throw new IllegalArgumentException("Invalid URI: " + uri);
    }

    static String host(URI uri) {
        return uri.getHost();
    }
}
