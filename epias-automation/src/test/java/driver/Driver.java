package driver;

import com.thoughtworks.gauge.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

public class Driver {

    public static WebDriver driver;


    @BeforeScenario
    public void initializeDriver() {
        driver = DriverFactory.getDriver();


    }

    // @AfterStep
    public void screenshot() {
        try {
            Thread.sleep(10);
            takesScreenshot(String.valueOf(System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takesScreenshot(String pictureName) {
        try {
            String fileName = "screenshot-images/" + pictureName + ".png";
            File file = new File("reports/html-report/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, file);
            Gauge.writeMessage("<img src='../" + fileName + "' width='800' height='480'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterScenario
    public void closeDriverAfterScenario(){

        driver.close();

    }
    @AfterSuite
    public void closeDriver(){
        driver.quit();

    }

}
