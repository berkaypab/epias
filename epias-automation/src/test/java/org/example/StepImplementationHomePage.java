package org.example;

import com.thoughtworks.gauge.Step;

public class StepImplementationHomePage extends StepImplementation {


    @Step("Navigate to main page")
    public void navigateToMainPage() {
        String baseURL = System.getenv("APP_URL");
        driver.get(baseURL);
        logger.info("Navigated to main page url :" + baseURL);

    }
}
