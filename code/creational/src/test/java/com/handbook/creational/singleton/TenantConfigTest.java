package com.handbook.creational.singleton;

import com.handbook.creational.singleton.tenant.TenantConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantConfigTest {

    @Test
    void oneInstancePerTenant() {
        assertThat(TenantConfig.forTenant("IN")).isSameAs(TenantConfig.forTenant("IN"));
        assertThat(TenantConfig.forTenant("IN")).isNotSameAs(TenantConfig.forTenant("DE"));
        assertThat(TenantConfig.forTenant("DE").currency()).isEqualTo("EUR");
    }
}
