package team.free.freewayscheduler.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NitterCrawlerTest {

    @Autowired
    private NitterCrawler nitterCrawler;

    @Test
    void test() {
        nitterCrawler.crawlingTwitter();
    }
}