package team.free.freewayscheduler.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
//@Component
public class SeoulMetroTwitterCrawler implements NotificationCrawler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a z");

    @Value("${driver.path}")
    private String chromedriverPath;
    @Value("${target.url}")
    private String targetUrl = "https://nitter.net/seoul_metro";

    @Override
    public List<NotificationDto> crawlingTwitter() {
        List<NotificationDto> notifications = new ArrayList<>();

        Path path = Paths.get(System.getProperty("user.dir"), chromedriverPath);

        System.setProperty("webdriver.chrome.driver", path.toString());
        ChromeOptions options = createChromeOptions();

        ChromeDriver driver = new ChromeDriver(options);
        driver.get(targetUrl);

        List<WebElement> notificationElements = driver.findElements(By.className("timeline-item"));

        for (WebElement notificationElement : notificationElements) {
            WebElement contentElement = notificationElement.findElement(By.className("tweet-content"));
            String notificationContent = contentElement.getText();
            if (notificationContent.contains("http")) {
                notificationContent = notificationContent.split(" http")[0];
            }
            if (notificationContent.contains("🔗")) {
                notificationContent = notificationContent.split("🔗")[0];
            }

            WebElement dateElement = notificationElement.findElement(By.className("tweet-date"));
            LocalDateTime notificationDate = LocalDateTime.parse(dateElement.getText(), FORMATTER);

            notifications.add(new NotificationDto(notificationContent, notificationDate));
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
        return options;
    }
}
