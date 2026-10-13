package com.handbook.creational.abstractfactory;

import com.handbook.creational.abstractfactory.cloud.AwsFactory;
import com.handbook.creational.abstractfactory.cloud.CloudFactory;
import com.handbook.creational.abstractfactory.cloud.InvoiceArchiver;
import com.handbook.creational.abstractfactory.cloud.MessageQueue;
import com.handbook.creational.abstractfactory.cloud.ObjectStorage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceArchiverTest {

    @Test
    void usesBothProductsFromTheSameFamily() {
        assertThat(new InvoiceArchiver(new AwsFactory("eu-west-1")).archive("INV-1", 10))
                .containsExactly("s3://erp-eu-west-1/INV-1.pdf (10 KB)",
                        "SQS eu-west-1/invoice-archived <- INV-1");
    }

    @Test
    void anInMemoryFamilyMakesTheClientEasyToTest() {
        List<String> calls = new ArrayList<>();
        CloudFactory inMemory = new CloudFactory() {
            public ObjectStorage objectStorage() {
                return (key, size) -> { calls.add("upload " + key); return "mem://" + key; };
            }
            public MessageQueue messageQueue() {
                return (topic, payload) -> { calls.add("publish " + payload); return "ok"; };
            }
        };
        new InvoiceArchiver(inMemory).archive("INV-9", 1);
        assertThat(calls).containsExactly("upload INV-9.pdf", "publish INV-9");
    }
}
