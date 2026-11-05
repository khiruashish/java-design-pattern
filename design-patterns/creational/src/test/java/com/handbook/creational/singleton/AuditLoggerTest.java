package com.handbook.creational.singleton;

import com.handbook.creational.singleton.healthcare.AuditLogger;
import com.handbook.creational.singleton.healthcare.PatientRecordService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuditLoggerTest {

    @Test
    void servicesShareOneAuditTrail() {
        int before = AuditLogger.getInstance().entries().size();
        new PatientRecordService().view("dr.test", "P-9");
        assertThat(AuditLogger.getInstance().entries()).hasSize(before + 1);
    }

    @Test
    void reflectionCannotCreateSecondInstance() throws Exception {
        AuditLogger.getInstance(); // make sure the instance exists
        Constructor<AuditLogger> ctor = AuditLogger.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
