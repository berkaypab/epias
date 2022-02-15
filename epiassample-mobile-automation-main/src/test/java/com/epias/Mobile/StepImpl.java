package com.epias.Mobile;

import com.epias.Mobile.helper.StoreHelper;
import com.epias.Mobile.model.SelectorInfo;
import com.epias.Mobile.utils.JavaMail;
import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.time.Duration.ofMillis;

public class StepImpl extends HookImpl {
    List<MobileElement> productList = new ArrayList<>();
    ArrayList<String> listAddedString = new ArrayList<>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    int counter = 0;

    public List<MobileElement> findElements(By by) throws Exception {
        List<MobileElement> webElementList = null;
        try {
            webElementList = appiumFluentWait.until(new ExpectedCondition<List<MobileElement>>() {
                @Nullable
                @Override
                public List<MobileElement> apply(@Nullable WebDriver driver) {
                    List<MobileElement> elements = driver.findElements(by);
                    return elements.size() > 0 ? elements : null;
                }
            });
            if (webElementList == null) {
                throw new NullPointerException(String.format("by = %s Web element list not found", by.toString()));
            }
        } catch (Exception e) {
            throw e;
        }
        return webElementList;
    }

    public MobileElement findElement(By by) throws Exception {
        MobileElement mobileElement;
        try {
            mobileElement = findElements(by).get(0);
        } catch (Exception e) {
            throw e;
        }
        return mobileElement;
    }

    public MobileElement findElementByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        MobileElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Element not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElement;
    }

    public List<MobileElement> findElementsByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        List<MobileElement> mobileElements = null;
        try {
            mobileElements = findElements(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Elements not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElements;
    }

    @Step({"<key> li elementi bul ve tıkla", "Click element by <key>"})
    public void clickByKey(String key) {
        doesElementExistByKey(key, 5);
        doesElementClickableByKey(key, 5);
        findElementByKey(key).click();
        logger.info(key + " clicked.");
    }

    @Step({"Check if <key> element enable"})
    public void checkIsEnabledElement(String key) {
        Assert.assertTrue(key + " element can't find on page!", findElementByKey(key).isEnabled());
        logger.info(key + "element is enable");
    }

    @Step({"Değeri <text> e eşit olan elementli bul",
            "Find element text equals <text>"})
    public void findByText(String text) throws Exception {
        findElement(By.xpath(".//*[contains(@text,'" + text + "')]"));
        counter++;
    }

    @Step("Print category number to console")
    public void printNumberOfCategory() {
        logger.info("There are " + counter + "category");
    }

    @Step("<key> get the number of the categories")
    public void getCategoryNumber(String key) {
        List<MobileElement> actualList = findElementsByKey(key);

        int elementsSize = actualList.size();
        for (int i = 0; i < elementsSize; i++) {
            MobileElement el = actualList.get(i);
            if (listAddedString.isEmpty() || !listAddedString.contains(el.getAttribute("content-desc"))) {
                listAddedString.add(el.getAttribute("content-desc"));
            }

        }

    }

    @Step("Print list values")
    public void printListValues() {
        for (int i = 0; i < listAddedString.size(); i++) {
            logger.info(i + 1 + ".category : " + listAddedString.get(i));
        }
        logger.info("Category List Size : " + listAddedString.size());
        System.out.println("Category List Size : " + listAddedString.size());
    }

    @Step({"<key> li elementi bul ve değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void saveTextByKey(String key, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, findElementByKey(key).getText());
        logger.info(saveKey + " variable is stored with: " + findElementByKey(key).getText());
    }

    @Step("Send email to <email>")
    public static void sendMailToRecepient(String email) throws Exception {
        JavaMail.sendMail(email);
    }

    @Step({"<key> li elementi bul ve değerini <saveKey> saklanan değer ile karşılaştır",
            "Find element by <key> and compare saved key <saveKey>"})
    public void equalsSaveTextByKey(String key, String saveKey) {
        Assert.assertEquals(StoreHelper.INSTANCE.getValue(saveKey), findElementByKey(key).getText());
        logger.info("Element by " + key + " is equal to " + StoreHelper.INSTANCE.getValue(saveKey));
        logger.info("Element by " + key + " contains: " + findElementByKey(key).getText() + " and Equals to " + saveKey + ": " + StoreHelper.INSTANCE.getValue(saveKey));
    }

    @Step({"<seconds> saniye bekle", "Wait <second> seconds"})
    public void waitBySecond(int seconds) {
        try {
            logger.info(seconds + " seconds waiting..");
            Thread.sleep(seconds * 1000);
            logger.info(seconds + " seconds waited.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void swipeUpAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;
            System.out.println(width + "  " + height);

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 20) / 100;
            int swipeEndHeight = (height * 60) / 100;
            System.out.println("Start width: " + swipeStartWidth + " - Start height: " + swipeStartHeight + " - End height: " + swipeEndHeight);

            new TouchAction((AndroidDriver) appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 70) / 100;
            int swipeEndHeight = (height * 30) / 100;

            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        }
    }

    public void swipeDownAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;
            System.out.println("Android1 : " + width + " - " + height);
            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 60) / 100;
            int swipeEndHeight = (height * 20) / 100;
            System.out.println("Start width: " + swipeStartWidth + " - Start height: " + swipeStartHeight + " - End height: " + swipeEndHeight);
            //appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 40) / 100;
            int swipeEndHeight = (height * 20) / 100;
            // appiumDriver.swipe(swipeStartWidth, swipeStartHeight, swipeEndWidth, swipeEndHeight, 1000);
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeStartHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeEndHeight))
                    .release()
                    .perform();
        }
    }

    @Step({"<times> kere aşağıya kaydır", "Swipe times <times>"})
    public void swipe(int times) {
        for (int i = 0; i < times; i++) {
            swipeDownAccordingToPhoneSize();
            waitBySecond(2);
        }
    }

    @Step({"<times> kere yukarı doğru kaydır", "Swipe up times <times>"})
    public void swipeUP(int times) {
        for (int i = 0; i < times; i++) {
            swipeUpAccordingToPhoneSize();
            waitBySecond(1);
        }
    }

    @Step({"Geri butonuna bas", "Navigate back"})
    public void clickByBackButton() {
        if (!localAndroid) {
            backPage();
        } else {
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent().withKey(AndroidKey.BACK));
        }
    }


    @Step("<StartX>,<StartY> oranlarından <EndX>,<EndY> oranlarına <times> kere swipe et")
    public void pointToPointSwipe(int startX, int startY, int endX, int endY, int count) {
        Dimension d = appiumDriver.manage().window().getSize();
        int height = d.height;
        int width = d.width;

        startX = (width * startX) / 100;
        startY = (height * startY) / 100;
        endX = (width * endX) / 100;
        endY = (height * endY) / 100;
        for (int i = 0; i < count; i++) {
            TouchAction action = new TouchAction(appiumDriver);
            action.press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(endX, endY))
                    .release().perform();
        }
    }

    @Step({"uygulamayı yeniden başlat", "Restart the application"})
    public void restart() {
        appiumDriver.closeApp();
        appiumDriver.launchApp();
        logger.info("application restarted");
        waitBySecond(5);
    }

    private void backPage() {
        appiumDriver.navigate().back();
    }

    @Step("<key> li elementin yüklenmesini <time> saniye bekle")
    public boolean doesElementExistByKey(String key, int time) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        try {
            WebDriverWait elementExist = new WebDriverWait(appiumDriver, time);
            elementExist.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
            return true;
        } catch (Exception e) {
            logger.info(key + " element not found");
            return false;
        }
    }

    public boolean doesElementClickableByKey(String key, int time) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        try {
            WebDriverWait elementExist = new WebDriverWait(appiumDriver, time);
            elementExist.until(ExpectedConditions.elementToBeClickable(selectorInfo.getBy()));
            return true;
        } catch (Exception e) {
            logger.info(key + " aranan elementi bulamadı");
            return false;
        }
    }

    @Step({"<key> sliderda ,<startPointX> noktasından ,<finishPointX> noktasına kaydır", "In slider <key> slide from Xcoordinate : <startPointX> to Xcoordinate : <finishPointX>"})
    public void sliderSwipe(String key, int startPointX, int finishPointX) {
        int coordinateX = appiumDriver.manage().window().getSize().width;
        int pointY = findElementByKey(key).getCenter().y;
        int firstPointX = (coordinateX * startPointX) / 1000;
        int lastPointX = (coordinateX * finishPointX) / 1000;
        TouchAction action = new TouchAction(appiumDriver);
        action
                .press(PointOption.point(firstPointX, pointY))
                .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                .moveTo(PointOption.point(lastPointX, pointY))
                .release().perform();
    }

    @Step({"<key> li elementi rastgele sec", "Choose random in <key>"})
    public void chooseRandomProduct(String key) {
        List<MobileElement> elements = findElementsByKey(key);
        int elementsSize = elements.size();
        int height = appiumDriver.manage().window().getSize().height;
        for (int i = 0; i < elementsSize; i++) {
            MobileElement element = elements.get(i);
            int y = element.getCenter().getY();
            if (y > 0 && y < (height - 100)) {
                productList.add(element);
            }
        }
        Random random = new Random();
        int randomNumber = random.nextInt(productList.size());
        productList.get(randomNumber).click();
    }

    @Step({"Rastgele swipe yap", "Random swipe"})
    public void swipeRandom() {
        Random random = new Random();
        for (int i = 0; i < random.nextInt(3); i++) {
            swipeUpAccordingToPhoneSize();
            waitBySecond(1);
        }
    }

    @Step({"<key> listesinden <quantity> tanesini random seç", "In <key> list randomly select different <quantity> items"})
    public void chooseRandomDifferentProduct(String key, int quantity) {
        List<MobileElement> productList = new ArrayList<>();
        List<Integer> chosenNumbers = new ArrayList<>();
        List<MobileElement> elements = findElementsByKey(key);
        int elementsSize = elements.size();

        for (int i = 0; i < elementsSize; i++) {
            MobileElement element = elements.get(i);
            productList.add(element);
        }

        for (int i = 0; i < quantity; i++) {
            Random random = new Random();
            int randomNumber = random.nextInt(productList.size());
            if (chosenNumbers.isEmpty() || !chosenNumbers.contains(randomNumber)) {
                chosenNumbers.add(randomNumber);
            }
        }
        for (int i = 0; i < quantity; i++) {
            productList.get(chosenNumbers.get(i)).click();
            waitBySecond(2);
        }
    }

    @Step({"<key> li elementi bul <times> kere tıkla"})
    public void findAndClick(String key, int times) {
        MobileElement mobileElement = findElementByKey(key);
        for (int i = 0; i < times; i++) {
            mobileElement.click();
        }
    }

    @Step({"<key> li elementi bul ve listedeki elementlerin hepsine tıkla", "Find by <key> and click all of them"})
    public void findAndClickAll(String key) {
        List<MobileElement> elements = findElementsByKey(key);
        MobileElement me = findElementByKey(key);
        for (int i = 0; i < elements.size(); i++) {
            me.click();
            waitBySecond(2);
        }
    }

    @Step({"<key> li elementi bul ve <times> kere sil"})
    public void clickAndDelete(String key, int times) {
        MobileElement mobileElement = findElementByKey(key);
        for (int i = 0; i < times; i++) {
            mobileElement.sendKeys(Keys.BACK_SPACE);
        }
    }

    @Step({"Check if element <key> not exists",
            "Element yok mu kontrol et <key>"})
    public void checkElementNotExists(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        int loopCount = 0;
        while (loopCount < 70) {
            if (appiumDriver.findElements(selectorInfo.getBy()).size() == 0) {
                logger.info(key + " verified element not exist");
                return;
            }
            loopCount++;
        }
        Assert.fail("Element '" + key + "' still exist.");
    }

    @Step("Close Keyboard")
    public void closeKeyboard() {
        appiumDriver.hideKeyboard();
    }


}