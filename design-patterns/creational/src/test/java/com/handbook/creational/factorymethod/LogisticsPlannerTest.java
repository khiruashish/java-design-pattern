package com.handbook.creational.factorymethod;

import com.handbook.creational.factorymethod.logistics.AirLogistics;
import com.handbook.creational.factorymethod.logistics.LogisticsPlanner;
import com.handbook.creational.factorymethod.logistics.RoadLogistics;
import com.handbook.creational.factorymethod.logistics.Transport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogisticsPlannerTest {

    @Test
    void subclassesDecideTheTransport() {
        assertThat(new RoadLogistics().plan("S", "A->B", 100)).contains("Truck", "cost INR   1800");
        assertThat(new AirLogistics().plan("S", "A->B", 1400)).contains("Plane", "ETA  2 h");
    }

    @Test
    void newTransportNeedsOnlyANewSubclass() {
        LogisticsPlanner drone = new LogisticsPlanner() {
            @Override
            protected Transport createTransport() {
                return new Transport() {
                    public String name() { return "Drone"; }
                    public int costPerKmInr() { return 40; }
                    public int speedKmph() { return 80; }
                };
            }
        };
        assertThat(drone.plan("S", "A->B", 10)).contains("Drone", "cost INR    400", "ETA  1 h");
    }
}
