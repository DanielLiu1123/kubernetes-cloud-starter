package com.freemanan.kubernetes.grey.common;

import java.util.List;

/**
 * @author Freeman
 */
public class Grey {
    private Destination master;
    private List<Destination> features;

    public Destination getMaster() {
        return master;
    }

    public void setMaster(Destination master) {
        this.master = master;
    }

    public List<Destination> getFeatures() {
        return features;
    }

    public void setFeatures(List<Destination> features) {
        this.features = features;
    }

    public void validate() {
        if (master == null) {
            throw new IllegalArgumentException("master must be set !");
        }
        master.validate();

        if (features != null && !features.isEmpty()) {
            double featuresWeight = 0;
            for (Destination feature : features) {
                feature.validate();
                if (features.size() > 1 && feature.getWeight() == null) {
                    throw new IllegalArgumentException("weight must be set because multiple features found !");
                }

                if (feature.getWeight() != null) {
                    featuresWeight += feature.getWeight();
                }
            }

            if (featuresWeight > 100 && master.getWeight() == null) {
                throw new IllegalArgumentException("sum of features weight must be less than 100 !");
            }
        }
    }
}
