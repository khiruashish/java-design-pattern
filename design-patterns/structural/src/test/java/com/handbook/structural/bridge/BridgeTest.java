package com.handbook.structural.bridge;

import com.handbook.structural.bridge.reporting.CsvRenderer;
import com.handbook.structural.bridge.reporting.InventoryReport;
import com.handbook.structural.bridge.reporting.ReportRenderer;
import com.handbook.structural.bridge.reporting.SalesReport;
import com.handbook.structural.bridge.social.AnnouncementPost;
import com.handbook.structural.bridge.social.Microblog;
import com.handbook.structural.bridge.social.SocialPlatform;
import com.handbook.structural.bridge.streaming.SetTopBox;
import com.handbook.structural.bridge.streaming.VoiceRemote;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BridgeTest {

    @Test
    void anyReportWorksWithAnyRenderer() {
        ReportRenderer json = (title, cols, rows) -> "{\"title\":\"" + title + "\",\"rows\":" + rows.size() + "}";
        assertThat(new SalesReport(json).generate()).isEqualTo("{\"title\":\"Q3 Sales by Region\",\"rows\":2}");
        assertThat(new InventoryReport(new CsvRenderer()).generate()).contains("\"Basmati Rice, 5kg\"");
    }

    @Test
    void voiceRemoteRespectsDeviceLimits() {
        SetTopBox box = new SetTopBox();
        VoiceRemote remote = new VoiceRemote(box);
        remote.say("louder");
        remote.say("louder");
        assertThat(box.volume()).isEqualTo(30);
    }

    @Test
    void postsAreTrimmedToPlatformLimit() {
        String shared = new AnnouncementPost(new Microblog(), "x", "y".repeat(200)).share();
        assertThat(shared).contains("text(80 chars)");
        SocialPlatform fake = new SocialPlatform() {
            public String name() { return "Fake"; }
            public int maxLength() { return 1000; }
            public String publish(String text, List<String> media) { return text; }
        };
        assertThat(new AnnouncementPost(fake, "hi", "there").share()).isEqualTo("Fake: HI - there #launch");
    }
}
