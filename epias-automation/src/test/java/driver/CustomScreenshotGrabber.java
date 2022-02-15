package driver;

import com.thoughtworks.gauge.screenshot.CustomScreenshotWriter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/*

public class CustomScreenshotGrabber implements CustomScreenshotWriter {

    @Override
    public String takeScreenshot() {

        TakesScreenshot driver = (TakesScreenshot) DriverFactory.getDriver();
        String screenshotFileName = String.format("screenshot-%s.png", UUID.randomUUID().toString());
        try {
            Files.write(Path.of(System.getenv("gauge_screenshots_dir"), screenshotFileName),
                    driver.getScreenshotAs(OutputType.BYTES));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return screenshotFileName;
    }

}*/
