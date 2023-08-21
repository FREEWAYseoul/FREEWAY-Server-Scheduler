package team.free.freewayscheduler.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
//@Component
public class TwitterWidgetCrawler implements NotificationCrawler {

    @Value("${driver.path}")
    private String chromedriverPath;
    @Value("${twitter.url}")
    private String targetUrl = "http://www.seoulmetro.co.kr/kr/index.do?device=PC#secondPage";

    @PostConstruct
    void init() {
        String userDirectory = System.getProperty("user.dir");
        log.info("userDirectory = {}", userDirectory);
        Path path = Paths.get(userDirectory, chromedriverPath);
        System.setProperty("webdriver.chrome.driver", path.toString());
    }

    @Override
    public List<NotificationDto> crawlingTwitter() {
        ChromeOptions options = createChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        driver.get(targetUrl);

        WebElement scriptElement = driver.findElement(By.id("__NEXT_DATA__"));
        String jsonText = scriptElement.getAttribute("textContent");

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(new StdDateFormat());
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(jsonText);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        JsonNode notificationEntries = jsonNode
                .path("props")
                .path("pageProps")
                .path("timeline")
                .path("entries");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy");

        List<NotificationDto> notifications = new ArrayList<>();
        for (JsonNode notificationEntry : notificationEntries) {
            if (notificationEntry.has("content")) {
                JsonNode notification = notificationEntry.path("content").path("tweet");
                String notificationContent = notification.path("full_text").asText();
                if (notificationContent.contains("http")) {
                    notificationContent = notificationContent.split(" http")[0];
                }
                LocalDateTime notificationDate = LocalDateTime.parse(notification.path("created_at").asText(), formatter);

                NotificationDto notificationDto = new NotificationDto(notificationContent, notificationDate);
                log.info("notificationDto = {}", notificationDto);
                notifications.add(notificationDto);
            }
        }

        driver.close();
        return notifications;
    }

    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--headless");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        options.setHeadless(true);
        return options;
    }
}
