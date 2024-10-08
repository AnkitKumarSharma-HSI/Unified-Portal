package com.dev.usersmanagementsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Hello world!
 *
 */
public class App {
    private final String configFilePath = "src/main/java/com/dev/usersmanagementsystem/config.properties";
    private Properties properties;
    private WebDriver driver;
    public void setup() {
        //WebDriverManager.chromedriver().setup();
        //ChromeOptions opt = new ChromeOptions();
        WebDriverManager.edgedriver().setup();
        EdgeOptions opt = new EdgeOptions();
        opt.addArguments("--remote-allow-origins=*");
        driver = new EdgeDriver(opt);
        //driver = new ChromeDriver(opt);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    public int getNodeLength(JsonNode eventsNode)
    {
        int count = 0;
        if(eventsNode.isArray() && eventsNode!=null)
        {
            for (JsonNode ignored : eventsNode) {
                count++;
            }
        }
        return count;
    }

    //public void checkAssertedEvents(String url)

    public void ConfigFileReader() {
        File ConfigFile = new File(configFilePath);
        try {
            FileInputStream configFileReader = new FileInputStream(ConfigFile);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("config.properties not found at config file path" + configFilePath);
        }

    }

    private void waitForElementVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(locator)));
    }

    private void enterText(By Selector, String text) {

        WebElement element = driver.findElement(Selector);
        if (element!=null)
        {
            element.clear();
            element.sendKeys(text);
        }

    }

    private void navigateAndWait(String url) {
        System.out.println("Url: "+url);
        if (url != null) {
            driver.get(url);
        }
        waitForElementVisibility(By.xpath("/html/body"));
    }
    private void clickOnElement(By locator)
    {
        waitForElementVisibility(locator);
        WebElement button = driver.findElement(locator);
        button.click();

    }
//    public static void main(String[] args) throws InterruptedException
//    {
//        System.out.println("Hello World!");
//        App obj = new App();
//        try {
//            obj.setup();
//            obj.runCode();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//    }

    public void runCode(String jsonContent) throws IOException, InterruptedException {
        ConfigFileReader();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        JsonNode stepsNode = rootNode.get("steps");
        System.out.println("Steps: " + stepsNode);
        int nodelen = getNodeLength(stepsNode);
        System.out.println("Length of Steps are " + nodelen);

        for(JsonNode step:stepsNode)
        {
            System.out.println("Step: "+step);
            String type = step.get("type").asText();
            if(type.equals("navigate"))
            {
                String url = step.get("url").asText();
                System.out.println(url);
                navigateAndWait(url);

            }

            if(type.equals("click"))
            {
                JsonNode selectorsGroup = step.get("selectors");
                JsonNode AssertedEvents = step.get("assertedEvents");



                for(JsonNode selectors:selectorsGroup)
                {
                    for(JsonNode selector:selectors)
                    {
                        String SelectorText = selector.asText();
                        System.out.println("Selector: "+SelectorText);
                        try {
                            if(SelectorText.startsWith("xpath"))
                            {
                                String xpath = SelectorText.replace("xpath/", "");
                                System.out.println(xpath);
                                //Thread.sleep(2000);
                                waitForElementVisibility(By.xpath(xpath));
                                clickOnElement(By.xpath(xpath));
                                break;

                            }
                        }
                        catch(NoSuchElementException e)
                        {   //break;
                            String regex = "[^\"]+component-[^\"]+";

                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(SelectorText);
                            System.out.println("Xpath Didn't Worked Trying With Css Selector");


                            if (matcher.find()) {
                                String csspath = matcher.group(); // Extracted ID part
                                csspath = "#" + csspath;
                                System.out.println("Extracted ID: " + csspath);
                                //System.out.println(csspath);

                                waitForElementVisibility(By.cssSelector(csspath));
                                clickOnElement(By.cssSelector(csspath));
                            } else {
                                System.out.println("ID not found.");
                            }

                        }
                    }

                }


            }
            if(type.equals("change"))
            {
                JsonNode selectorsGroup = step.get("selectors");

                for(JsonNode selectors:selectorsGroup)
                {
                    for(JsonNode selector:selectors)
                    {
                        String SelectorText = selector.asText();
                        System.out.println("Selector: "+SelectorText);
                        if(SelectorText.startsWith("xpath"))
                        {
                            String xpath = SelectorText.replace("xpath/", "");
                            System.out.println(xpath);
                            String Text = step.get("value").asText();
                            waitForElementVisibility(By.xpath(xpath));
                            enterText(By.xpath(xpath),Text);
                            break;
                        }

                    }
                }
            }

        }

    }
}
