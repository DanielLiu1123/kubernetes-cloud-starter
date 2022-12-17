package com.freemanan.kubernetes.grey.common;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class GreyTest {

    @Test
    void testValidate() {
        Grey grey = new Grey();
        Destination destination = new Destination();
        destination.setService("user");
        destination.setNamespace("default");
        destination.setPort(null);
        destination.setWeight(20.0);
        grey.setMaster(destination);

        Destination d1 = new Destination();
        d1.setService("user");
        d1.setNamespace("default");
        d1.setPort(null);
        d1.setWeight(20.0);
        Destination d2 = new Destination();
        d2.setService("user");
        d2.setNamespace("default");
        d2.setPort(null);
        d2.setWeight(null);
        grey.setFeatures(Arrays.asList(d1, d2));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(grey::validate)
                .withMessageContaining("weight must be set because multiple features found !");

        grey = new Grey();
        destination = new Destination();
        destination.setService("user");
        destination.setNamespace("default");
        destination.setPort(null);
        destination.setWeight(20.0);
        grey.setMaster(destination);

        d1 = new Destination();
        d1.setService("user");
        d1.setNamespace("default");
        d1.setPort(null);
        d1.setWeight(100.1);
        grey.setFeatures(Arrays.asList(d1));

        assertThatCode(grey::validate).doesNotThrowAnyException();

        grey = new Grey();
        destination = new Destination();
        destination.setService("user");
        destination.setNamespace("default");
        destination.setPort(null);
        destination.setWeight(null);
        grey.setMaster(destination);

        d1 = new Destination();
        d1.setService("user");
        d1.setNamespace("default");
        d1.setPort(null);
        d1.setWeight(100.1);
        grey.setFeatures(Arrays.asList(d1));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(grey::validate)
                .withMessageContaining("sum of features weight must be less than 100 !");
    }
}
