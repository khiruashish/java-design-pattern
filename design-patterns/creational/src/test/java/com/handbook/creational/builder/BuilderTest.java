package com.handbook.creational.builder;

import com.handbook.creational.builder.api.ApiRequest;
import com.handbook.creational.builder.grocery.GroceryOrder;
import com.handbook.creational.builder.telecom.BrochureBuilder;
import com.handbook.creational.builder.telecom.MobilePlan;
import com.handbook.creational.builder.telecom.MobilePlanBuilder;
import com.handbook.creational.builder.telecom.PlanDirector;
import com.handbook.creational.builder.travel.FlightSearch;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuilderTest {

    @Nested
    class GroceryOrderTest {
        @Test
        void buildsWithDefaultsAndCalculatesSubtotal() {
            GroceryOrder order = GroceryOrder.builder("C1", "addr").item("A", 2, 50).build();
            assertThat(order.subtotalInr()).isEqualTo(100);
            assertThat(order.toString()).contains("slot=TODAY_6_TO_8_PM", "coupon=-");
        }

        @Test
        void itemsCannotBeModifiedAfterBuild() {
            GroceryOrder order = GroceryOrder.builder("C1", "addr").item("A", 1, 10).build();
            assertThatThrownBy(() -> order.items().add(new GroceryOrder.Item("B", 1, 1)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void rejectsInvalidCombinations() {
            assertThatThrownBy(() -> GroceryOrder.builder("C1", "addr").build())
                    .hasMessageContaining("at least one item");
            assertThatThrownBy(() -> GroceryOrder.builder("C1", "addr").item("TV", 1, 5000)
                    .slot(GroceryOrder.Slot.EXPRESS_10_MIN).build())
                    .hasMessageContaining("Express");
        }
    }

    @Nested
    class FlightSearchTest {
        @Test
        void stepBuilderFillsDefaults() {
            FlightSearch s = FlightSearch.builder().from("A").to("B").departing(LocalDate.of(2027, 1, 1)).build();
            assertThat(s.adults()).isEqualTo(1);
            assertThat(s.cabin()).isEqualTo(FlightSearch.CabinClass.ECONOMY);
        }

        @Test
        void validatesInCompactConstructor() {
            assertThatThrownBy(() -> FlightSearch.builder().from("A").to("A")
                    .departing(LocalDate.of(2027, 1, 1)).build())
                    .hasMessage("Origin and destination must differ");
        }
    }

    @Nested
    class TelecomTest {
        @Test
        void sameRecipeProducesDifferentRepresentations() {
            PlanDirector director = new PlanDirector();
            MobilePlanBuilder plan = new MobilePlanBuilder();
            BrochureBuilder text = new BrochureBuilder();
            director.familyPlan(plan);
            director.familyPlan(text);
            MobilePlan result = plan.result();
            assertThat(result.monthlyPriceInr()).isEqualTo(999);
            assertThat(result.addOns()).hasSize(2);
            assertThat(text.result()).contains("FAMILY MAX", "INR 999/month");
        }
    }

    @Nested
    class ApiRequestTest {
        @Test
        void toBuilderCopiesWithoutChangingOriginal() {
            ApiRequest base = ApiRequest.builder("https://x").header("A", "1").build();
            ApiRequest copy = base.toBuilder().header("B", "2").build();
            assertThat(base.headers()).containsOnlyKeys("A");
            assertThat(copy.headers()).containsOnlyKeys("A", "B");
        }

        @Test
        void enforcesIdempotencyKeyForRetriedPosts() {
            assertThatThrownBy(() -> ApiRequest.builder("https://x").post("{}").retries(1).build())
                    .hasMessageContaining("Idempotency-Key");
            assertThat(ApiRequest.builder("https://x").post("{}").retries(1)
                    .header("Idempotency-Key", "k").build().maxRetries()).isEqualTo(1);
        }

        @Test
        void rejectsPlainHttp() {
            assertThatThrownBy(() -> ApiRequest.builder("http://x").build()).hasMessageContaining("HTTPS");
        }
    }
}
