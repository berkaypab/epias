package org.example;


import WebAutomationBase.helper.ElementHelper;
import WebAutomationBase.helper.StoreHelper;
import WebAutomationBase.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import driver.Driver;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class StepImplementation extends Driver {
    private HashMap<String, Double> productionMap;
    private TreeMap<String, Double> sortedProductionMap;
    private HashMap<String, Double> consumptionMap;
    private TreeMap<String, Double> sortedConsumptionMap;
    public Logger logger = LoggerFactory.getLogger(StepImplementation.class);
    public final Actions action;

    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;


    public StepImplementation() {
        action = new Actions(driver);
        productionMap = new HashMap<>();
        sortedProductionMap = new TreeMap<>();
        consumptionMap = new HashMap<>();
        sortedConsumptionMap = new TreeMap<>();
    }

    public WebElement findElement(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        webDriverWait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        highlightElement(webElement);
        return webElement;
    }

    public List<WebElement> findElements(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);

        return driver.findElements(infoParam);
    }


    public void clickElement(WebElement element) {
        element.click();
    }

    public void clickElementBy(String key) {
        findElement(key).click();
    }


    public void sendKeys(String text, String key) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazıldı.");
        }
    }

    public void clearTextArea(String key) {
        findElement(key).clear();
        logger.info("Text area cleared ");
    }


    public String getText(String key) {
        logger.info("Getting text from given locator ");
        return findElement(key).getText();
    }


    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Javascript driverın başlatılması
     *
     * @return
     */
    public JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }

    /**
     * Javascript scriptlerinin çalışması için gerekli fonksiyon
     *
     * @param script
     * @param wait
     * @return
     */
    public Object executeJS(String script, boolean wait) {
        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }


    /**
     * Belirli bir locasyona sayfanın kaydırılması
     *
     * @param x
     * @param y
     */
    public void scrollTo(int x, int y) {
        String script = String.format("window.scrollTo(%d, %d);", x, y);
        executeJS(script, true);
    }


    /**
     * Belirli bir elementin olduğu locasyona websayfasının kaydırılması
     *
     * @param key
     * @return
     */
    public WebElement scrollToElementToBeVisible(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        WebElement webElement = driver.findElement(ElementHelper.getElementInfoToBy(elementInfo));
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
        }
        return webElement;
    }

    public WebElement scrollToElementToBeVisibleWithElement(WebElement element) {
        if (element != null) {
            scrollTo(element.getLocation().getX(), element.getLocation().getY() - 100);
        }
        return element;
    }


    //For highlighting the element to be located after scroll
    public static void highlightElement(WebElement ele) {
        try {
            for (int i = 0; i < 3; i++) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].setAttribute('style', arguments[1]);", ele, "color: red; border: 2px solid red;");
            }
        } catch (Throwable t) {
            System.err.println("Error came : " + t.getMessage());
        }
    }

    public void hoverElementBy(String key) {
        WebElement webElement = findElement(key);
        action.moveToElement(webElement).build().perform();
    }

    @Step("Hover by given element <key>")
    public void hoverByGivenElement(String key) {
        hoverElementBy(key);
    }

    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    public Boolean isEnabled(String key) {
        logger.info("Checking is element enable on the page ");

        return findElement(key).isEnabled();
    }

    public boolean isDisplayedBy(By by) {
        return driver.findElement(by).isDisplayed();
    }

    public void selectByVisibleText(String key, String text) {

        Select select = new Select(findElement(key));
        select.selectByVisibleText(text);
        logger.info("Selected from dropdown");
    }

    private Select select(String key) {
        return new Select(findElement(key));

    }

    public void selectByValue(String key, String value) {
        select(key).selectByValue(value);
        logger.info("Selected from dropdown");
    }

    @Step("Select month from <key> dropdown by <value>")
    public void selectMonthFromKeyByValue(String key, String value) {
        selectByValue(key, value);
    }

    @Step("Select year from <key> dropdown by <value>")
    public void selectYearFromKeyByValue(String key, String value) {
        selectByValue(key, value);
    }

    @Step("Select from <key> list with <text>")
    public void select(String key, String text) {
        selectByVisibleText(key, text);

    }

    @Step("Select <key> list with <option>")
    public void s(String key, String option) {
        List<WebElement> list = findElements(key);
        for (WebElement l : list) {
            if (l.getText().equals(option)) {
                l.click();
                break;
            }
        }

    }

    /**
     * Dynamic sleep
     *
     * @param seconds
     */
    public void sleepSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
            logger.info("Waited " + seconds + " seconds ");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());

        }
    }

    /**
     * Navigate Home Page
     */

    @Step("Navigate to home page")
    public void navigateToHomePage() {
        driver.navigate().to(System.getenv("APP_URL"));
        logger.info("Navigated to homepage ");
    }

    /**
     * Navigate to Given Page
     */


    @Step("Navigate to given page <string>")
    public void navigateToGivenPage(String string) {
        driver.navigate().to(string);
        logger.info("Navigated to given url :" + string);
    }


    @Step("Given element <key> dynamic text area range should be max <limit>")
    public void textRangeAndWarningMessageShouldBe(String key, String limit) {
        //limit assertion char length max=40
        assertEquals("Wrong character limitation ", findElement(key).getAttribute("maxlength"), limit);
    }

    @Step("Given element <key> dynamic text area warning message should be")
    public void warningMessageShouldBe(String key) {
        //Warning message assertion
        assertEquals("You have reached the maximum character length.", findElement(key).getText());

    }


    /**
     * Scroll to given element
     *
     * @param key
     */
    @Step({"<key> alanına kaydır"})
    public void scrollToElement(String key) {
        scrollToElementToBeVisible(key);
        logger.info(key + " elementinin olduğu alana kaydırıldı");


    }

    public String getAttributeValue(String key, String value) {
        String attributeValueOfGivenElement = "";
        if (findElement(key) != null) {
            attributeValueOfGivenElement = findElement(key).getAttribute(value);
        }
        return attributeValueOfGivenElement;
    }


    @Step({"<key> has next degeri true ise her sayfada bulunan <elements> li key ile 60 urun yuklendigini dogrula",
            "If <key> element's attribute value true then verify 60 products loaded in every page with <elements>"})
    public void validateProductsSizeEachScrollDown(String key, String elements) throws InterruptedException {
        List<WebElement> listOfElements = null;
        final int itemSizeInEveryPage = 60;
        int tempCounter = 0;
        int page = 0;
        int index = 0;
        int listSize;
        while (getAttributeValue(key, "value").equals("True")) {
            if (page == 16) break;
            page++;
            index += itemSizeInEveryPage;
            listOfElements = findElements(elements);
            listSize = listOfElements.size();
            assertEquals(itemSizeInEveryPage, (listSize - tempCounter));
            scrollToElementToBeVisibleWithElement(listOfElements.get(index - 1));
            tempCounter = listSize;
            Thread.sleep(2000);
        }

    }


    @Step({"Send <text> Keys to given element <key>"})
    public void sendKeysToGivenElement(String text, String key) {
        sendKeys(text, key);
        logger.info(key + " elementinin olduğu alana kaydırıldı");

    }

    @Step({"Select address from auto suggestive dropdown with <key>"})
    public void selectFromAutoSuggestiveDropDown(String key) {
        String optionToSelect = "Mexico City, CDMX, Mexico";
        List<WebElement> contentList = findElements(key);
        WebElement tempElement = null;
        for (WebElement e : contentList) {
            String currentOption = e.getAttribute("textContent");
            if (currentOption.equals(optionToSelect)) {
                tempElement = e.findElement(By.xpath("//parent::div"));
                // Actions ac = new Actions(driver);
                //ac.moveToElement(tempElement).click().build().perform();
                clickElement(tempElement);
                break;
            }


        }

    }

    /**
     * Check if element "key" contains text "expectedText"
     *
     * @param key
     * @param expectedText
     */
    @Step({"Check if element <key> contains text <expectedText>",
            "<key> elementi <text> değerini içeriyor mu kontrol et"})
    public void checkElementContainsText(String key, String expectedText) {
        boolean containsText = findElement(key).getText().contains(expectedText);
        assertTrue("Expected text is not contained", containsText);
        logger.info(key + " elementi" + expectedText + "değerini içeriyor.");
    }

    /**
     * check title contains given text
     *
     * @param expectedText
     */
    @Step({"Check if title contains text <expectedText>",
            "Title <text> değerini içeriyor mu kontrol et"})
    public void checkElementContainsText(String expectedText) {
        boolean containsText = getTitle().contains(expectedText);
        assertTrue("Expected text is not contained", containsText);
        logger.info(" Title" + expectedText + "değerini içeriyor.");
    }

    /**
     * js click with xpath
     *
     * @param xpath
     */
    @Step({"Click with javascript to xpath <xpath>",
            "Javascript ile xpath tıkla <xpath>"})
    public void javascriptClickerWithXpath(String xpath) {
        assertTrue("Element bulunamadı", isDisplayedBy(By.xpath(xpath)));
        javaScriptClicker(driver.findElement(By.xpath(xpath)));
        logger.info("Javascript ile " + xpath + " tıklandı.");
    }

    /**
     * js click with css locator
     *
     * @param css
     */
    @Step({"Click with javascript to css <css>",
            "Javascript ile css tıkla <css>"})
    public void javascriptClickerWithCss(String css) {
        assertTrue("Element bulunamadı", isDisplayedBy(By.cssSelector(css)));
        javaScriptClicker(driver.findElement(By.cssSelector(css)));
        logger.info("Javascript ile " + css + " tıklandı.");
    }

    /**
     * check is display given element on page
     *
     * @param key
     */
    @Step({"Element with <key> is displayed on website",
            "<key> li element sayfada görüntüleniyor mu kontrol et "})
    public void isDisplayedSection(String key) {
        WebElement element = findElementWithKey(key);
        assertTrue("Section bulunamadı", element.isDisplayed());
        logger.info(key + "li section sayfada bulunuyor.");
    }

    /**
     * check is enable given element on page
     *
     * @param key
     */
    @Step({"Element with <key> is enable on website",
            "<key> li element sayfada enable mı kontrol et "})
    public void isEnableSection(String key) {
        WebElement element = findElementWithKey(key);
        assertTrue("Section bulunamadı", element.isEnabled());
        logger.info(key + "li section sayfada bulunuyor.");
    }


    /**
     * Choose Random Element
     *
     * @param key
     */
    @Step("<key> menu listesinden rasgele seç")
    public void chooseRandomElementFromList(String key) {
        randomPick(key);
    }

    /**
     * js clicker
     *
     * @param
     * @param element
     */

    public void javaScriptClicker(WebElement element) {

        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", element);
    }

    @Step("Click with js method <key>")
    public void jsClicker(String key) {
        WebElement element = findElement(key);
        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", element);
    }


    /**
     * switch tabs and verify
     *
     * @param key
     */
    @Step("Check if new tab has verified url <key>")
    public void switchTabsUsingPartOfUrl(String key) {
        String currentHandle = null;
        try {
            final Set<String> handles = driver.getWindowHandles();
            if (handles.size() > 1) {
                currentHandle = driver.getWindowHandle();
            }
            if (currentHandle != null) {
                for (final String handle : handles) {
                    driver.switchTo().window(handle);
                    if (driver.getCurrentUrl().contains(key) && !currentHandle.equals(handle)) {
                        break;
                    }
                }
            } else {
                for (final String handle : handles) {
                    driver.switchTo().window(handle);
                    if (driver.getCurrentUrl().contains(key)) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Switching tabs failed");
        }
    }


    /**
     * scroll to given field
     *
     * @param key
     */
    @Step({"Scroll to <key> field",
            "<key> alanına js ile kaydır"})
    public void scrollToElementWithJs(String key) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", findElement(key));
        waitByMilliSeconds(3000);
    }


    /**
     * check element isExist on page
     *
     * @param key
     * @param message
     */
    @Step({"Check if element <key> exists else print message <message>",
            "Element <key> var mı kontrol et yoksa hata mesajı ver <message>"})
    public void getElementWithKeyIfExistsWithMessage(String key, String message) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By by = ElementHelper.getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                logger.info(key + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assert.fail(message);
    }

    /**
     * find given text on List and click on it
     *
     * @param key
     * @param text
     */
    public void findTextAndClick(String key, String text) {

        List<WebElement> searchText = findElements(key);
        for (int i = 0; i < searchText.size(); i++) {
            if (searchText.get(i).getText().trim().contains(text)) {
                searchText.get(i).click();
                logger.info("Bulunan elemente tıklandı.");
                break;
            }
        }
    }

    /**
     * hover to given el
     *
     * @param element
     */
    public void hoverElement(WebElement element) {
        action.moveToElement(element).build().perform();
    }

    @Step({"Click to element <key>",
            "Elementine tıkla <key>"})
    public void clickElement(String key) {
        if (!key.equals("")) {
            WebElement element = findElement(key);
            hoverElement(element);
            waitByMilliSeconds(500);
            clickElement(element);
            logger.info(key + " elementine tıklandı.");
        }
    }


    @Step("Store values in production hash map")
    public void storeProductionValuesInMap() {
        storeInGivenMap(productionMap);
    }

    @Step("Store values in consumption hash map")
    public void storeConsumptionValuesInMap() {
        storeInGivenMap(consumptionMap);
    }

    private void storeInGivenMap(HashMap<String, Double> map) {
        readSpecificTableDataAndStore(map);
    }

    public void printMapValues(TreeMap<String, Double> sorted) {

        logger.info("Printing map values");
        for (Map.Entry<String, Double> entry : sorted.entrySet())
            System.out.println("Key = " + entry.getKey()
                    + ", Value = "
                    + entry.getValue());
    }

    @Step("Calculate hourly difference between production and consumption")
    public void differenceBetweenProductionAndConsumption() {
        Set<Map.Entry<String, Double>> productionEntries
                = sortedProductionMap.entrySet();
        Iterator<Map.Entry<String, Double>> iteratorProduction
                = productionEntries.iterator();

        Set<Map.Entry<String, Double>> consumptionEntries
                = sortedConsumptionMap.entrySet();
        Iterator<Map.Entry<String, Double>> iteratorConsumption
                = consumptionEntries.iterator();

        // Additional step here
        // To Initialize object holding for
        // key-value pairs to null
        Map.Entry<String, Double> entryProduction = null;
        Map.Entry<String, Double> entryConsumption = null;

        // Holds true till there is no element remaining in
        // the object using hasNExt() method
        while (iteratorProduction.hasNext() && iteratorConsumption.hasNext()) {

            // Moving onto next pairs using next() method
            entryProduction = iteratorProduction.next();
            entryConsumption = iteratorConsumption.next();

            // Printing the key-value pairs
            // using getKet() and getValue() methods
            double doubleValueCons = entryConsumption.getValue();
            double doubleValueProd = entryProduction.getValue();
            double result = doubleValueProd - doubleValueCons;
            final DecimalFormat df1 = new DecimalFormat("#.##");

            //System.out.println("production : " + entryProduction.getKey() + "->" + entryProduction.getValue());
            //System.out.println("consumption : " + entryConsumption.getKey() + "->" + entryConsumption.getValue());
            System.out.println("Hourly Difference : " + entryProduction.getKey() + "->" + df1.format(result));
        }
    }

    public void readSpecificTableDataAndStore(HashMap<String, Double> map) {
        List<WebElement> colListTime = driver.findElements(By.xpath("//tbody[@id='j_idt226:dt_data']/tr/td[2]"));
        List<String> listOfColListTime = new ArrayList<>();
        for (WebElement el : colListTime) {
            listOfColListTime.add(el.getText());
        }
        List<Double> listOfColTotalEnergy = new ArrayList<>();
        List<WebElement> colListTotalEnergy = driver.findElements(By.xpath("//tbody[@id='j_idt226:dt_data']/tr/td[3]"));
        for (WebElement a : colListTotalEnergy) {
            listOfColTotalEnergy.add(Double.parseDouble(a.getText().replace(".", "").replace(",", ".")));

        }

        for (int i = 0; i < colListTime.size(); i++) {
            map.put(listOfColListTime.get(i), listOfColTotalEnergy.get(i));
        }
    }

    @Step("Sort production map and print key value pairs")
    public void sortProductionMapAndPrint() {
        sortedProductionMap = sortByKey(productionMap);
        logger.info("Elektrik uretim saatlik degerleri");
        printMapValues(sortedProductionMap);
    }

    @Step("Sort consumption map and print key value pairs")
    public void sortConsumptionMapAndPrint() {
        sortedConsumptionMap = sortByKey(consumptionMap);
        logger.info("Elektrik tuketim saatlik degerleri");
        printMapValues(sortedConsumptionMap);
    }

    public TreeMap<String, Double> sortByKey(HashMap<String, Double> map) {
        return new TreeMap<>(map);
    }


    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            logger.info(milliseconds + " milisaniye bekleniyor.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void randomPick(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = random.nextInt(elements.size());
        elements.get(index).click();
    }

    @Step("Pick <value>.day of month from <key>")
    public void pickDateOfDaysFromPicker(String value, String key) {
        pickDayFromDateTimePicker(key, value);

    }

    public void pickDayFromDateTimePicker(String key, String value) {
        List<WebElement> days = findElements(key);
        for (WebElement day : days) {
            if (day.getText().equals(value)) {
                day.click();
                break;
            }
        }
    }

    @Step("Check if <key> element's attribute value <active>")
    public void asdf(String key, String active) {
        WebElement element = findElementWithKey(key);
        String a = element.getAttribute(active);
        assertEquals("active", a);
    }


    @Step("Check position <key1> relative to <key2>")
    public Boolean checkRelativePosition(String key1, String key2) {
        WebElement parent = findElementWithKey(key1);
        WebElement child = findElementWithKey(key2);
        boolean isAbove = false;
        if (!(parent.getLocation() == null && child.getLocation() == null)) {
            if (parent.getLocation().getY() - child.getLocation().getY() < 0) {
                isAbove = true;
                logger.info(key1 + " element is above the" + key2);
            }

            if (parent.getLocation().getY() - child.getLocation().getY() > 0) {
                isAbove = false;
                logger.info(key1 + " element is under the " + key2);
            }
        }
        return isAbove;
    }


    @Step({"Check if element <key> has attribute <attribute>",
            "<key> elementi <attribute> niteliğine sahip mi"})
    public void checkElementAttributeExists(String key, String attribute) {
        WebElement element = findElement(key);
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (element.getAttribute(attribute) != null) {
                logger.info(key + " elementi " + attribute + " niteliğine sahip.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assert.fail("Element DOESN't have the attribute: '" + attribute + "'");
    }

    @Step("Wait until image uploaded and click checkbox <key>")
    public void waitUntilImageUploadedAndClickCheckBox(String key) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        boolean elm = wait.until(ExpectedConditions.attributeContains(By.cssSelector("img[class='custom-image__uploaded-image']"), "src", "image/"));
        if (elm) findElement(key).click();
    }

    @Step("Upload file to given <key>")
    public void uploadImage(String key) throws AWTException, InterruptedException {
        Robot robot = new Robot();
        String userDirectory = System.getProperty("user.dir");
        String path = userDirectory + "\\ppberkay.png";

        // disable the internal click by overriding the method in dom
        executeJS("HTMLInputElement.prototype.click = function () {" +
                "    if (this.type !== 'file') {" +
                "        HTMLElement.prototype.click.call(this);" +
                "    }" +
                "    else if (!this.parentNode) {" +
                "        this.style.display = 'none';" +
                "        this.ownerDocument.documentElement.appendChild(this);" +
                "        this.addEventListener('change', () => this.remove());" +
                "    }" +
                "}", true);
        javaScriptClicker(findElement(key));
        Thread.sleep(2000);
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
        findElement(key).sendKeys(path);
    }

    @Step("Wait until elements loaded <key> and verify sorting")
    public void waitUntilElementsLoadedAndCheckSortingSuccessful(String key) {

        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        List<WebElement> elements = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(infoParam, 59));
        Integer[] actual = new Integer[elements.size()];
        Integer[] sorted = new Integer[elements.size()];
        int i = 0;
        for (WebElement e : elements) {
            actual[i] = sorted[i] = Integer.parseInt((e.getText()).replace(",", ""));
            i++;
        }
        Arrays.sort(sorted, Collections.reverseOrder());
        for (int k = 0; k < elements.size(); k++) {
            assertEquals(actual[k], sorted[k]);
        }


    }

    @Step("<key> product is exist on cart page")
    public void isExistOnCartPage(String key) {
        assertTrue(findElements(key).size() > 0);

    }


    @Step("Hover all elements in <key> with js")
    public void hoverAllMenuLinksWithJs(String key) {
        List<WebElement> elements = findElements(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (WebElement element : elements) {
            js.executeScript("var element = arguments[0];"
                    + "var mouseEventObj = document.createEvent('MouseEvents');"
                    + "mouseEventObj.initEvent( 'mouseover', true, true );"
                    + "element.dispatchEvent(mouseEventObj);", element);

        }

    }

    @Step("Check broken links with <key> element")
    public void brokenLinkCheck(String key) {
        String url;
        int respCode;
        HttpURLConnection huc;
        Iterator<WebElement> it = findElements(key).iterator();
        while (it.hasNext()) {
            url = it.next().getAttribute("href");
            if (!url.startsWith(System.getenv("APP_URL"))) continue;
            if (url.isEmpty()) continue;

            try {
                huc = (HttpURLConnection) (new URL(url).openConnection());
                huc.setRequestMethod("HEAD");
                huc.connect();
                respCode = huc.getResponseCode();
                assertFalse(respCode >= 400);
                logger.info(url + " is a valid link");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
