package com.handbook.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/** The architecture of the examples, enforced by ArchUnit on every build. */
class ArchitectureRulesTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.handbook.architecture");

    @Test
    void groceryLayersOnlyCallDownwards() {
        layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("Web").definedBy("..layered.grocery.web..")
                .layer("Service").definedBy("..layered.grocery.service..")
                .layer("Repository").definedBy("..layered.grocery.repository..")
                .whereLayer("Web").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Web")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(classes);
    }

    @Test
    void hexagonCoreNeverDependsOnAdapters() {
        noClasses().that().resideInAPackage("..hexagonal.checkout.domain..")
                .should().dependOnClassesThat().resideInAPackage("..hexagonal.checkout.adapter..")
                .check(classes);
        noClasses().that().resideInAPackage("..hexagonal.checkout.port..")
                .should().dependOnClassesThat().resideInAPackage("..hexagonal.checkout.adapter..")
                .check(classes);
    }

    @Test
    void cleanArchitectureDependencyRulePointsInwards() {
        noClasses().that().resideInAPackage("..clean.coupon.entities..")
                .should().dependOnClassesThat().resideInAnyPackage("..clean.coupon.usecase..", "..clean.coupon.adapters..")
                .check(classes);
        noClasses().that().resideInAPackage("..clean.coupon.usecase..")
                .should().dependOnClassesThat().resideInAPackage("..clean.coupon.adapters..")
                .check(classes);
    }

    @Test
    void billingModuleDoesNotDependOnOrdering() {
        noClasses().that().resideInAPackage("..modular.shop.billing..")
                .should().dependOnClassesThat().resideInAPackage("..modular.shop.ordering..")
                .check(classes);
    }
}
