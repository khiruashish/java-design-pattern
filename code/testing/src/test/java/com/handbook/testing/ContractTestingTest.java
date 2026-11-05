package com.handbook.testing;

import com.handbook.testing.contract.broker.ContractBroker;
import com.handbook.testing.contract.broker.ContractBroker.Verification;
import com.handbook.testing.contract.core.Contract;
import com.handbook.testing.contract.core.Contract.AnyInteger;
import com.handbook.testing.contract.core.Contract.Interaction;
import com.handbook.testing.contract.core.Contract.Response;
import com.handbook.testing.contract.messages.SchemaCompatibility;
import com.handbook.testing.contract.messages.SchemaCompatibility.ConsumerNeeds;
import com.handbook.testing.contract.messages.SchemaCompatibility.Field;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContractTestingTest {

    @Test
    void missingFieldFailsVerification() {
        Contract c = new Contract("a", "b").uponReceiving(new Interaction("x", "s", "/x", 200, Map.of("n", new AnyInteger())));
        assertThat(c.verify(s -> { }, p -> new Response(200, Map.of("m", 1)))).singleElement().asString().startsWith("FAIL");
        assertThat(c.verify(s -> { }, p -> new Response(200, Map.of("n", 5)))).singleElement().asString().startsWith("PASS");
    }

    @Test
    void addingAnOptionalFieldIsCompatibleRemovingOneIsNot() {
        List<ConsumerNeeds> consumers = List.of(new ConsumerNeeds("c", Map.of("id", "string")));
        assertThat(SchemaCompatibility.check(Map.of("id", new Field("string", true), "extra", new Field("int", false)), consumers)).isEmpty();
        assertThat(SchemaCompatibility.check(Map.of("ident", new Field("string", true)), consumers)).hasSize(1);
    }

    @Test
    void cannotDeployWithoutVerificationAgainstDeployedConsumers() {
        ContractBroker b = new ContractBroker();
        b.deployed("prod", "c", "1");
        b.record(new Verification("c", "2", "p", "9", true));
        assertThat(b.canIDeploy("p", "9", "prod", List.of("c"))).startsWith("NO");
        b.deployed("prod", "c", "2");
        assertThat(b.canIDeploy("p", "9", "prod", List.of("c"))).isEqualTo("YES");
    }
}
