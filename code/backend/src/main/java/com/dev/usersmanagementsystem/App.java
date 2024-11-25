package com.dev.usersmanagementsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
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
    private final String configFilePath = "";
    private Properties properties;
    private WebDriver driver;
    private Connection conn;
//    private static final InfluxDB INFLXUDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root")
//            .setDatabase("ubi_url_responses");
    private static InfluxDB influxDB;
    public void setup(String password,String username,String dbHost,String dbName) {
        //WebDriverManager.chromedriver().setup();
        //ChromeOptions opt = new ChromeOptions();
        WebDriverManager.edgedriver().setup();
        EdgeOptions opt = new EdgeOptions();
        opt.addArguments("--remote-allow-origins=*");
        driver = new EdgeDriver(opt);
//        jdbc:mysql://localhost:3306/
        dbHost="localhost";
        String url = "jdbc:mysql://"+dbHost+":"+"3306";
        // JDBC URL format: jdbc:mysql://<hostname>:<port>/<dbname>
//        influxDB=InfluxDBFactory.connect(url,username,password).setDatabase(dbName);
        try{
             conn = connectToMySQL(url, username, password, dbName);

            if (conn != null) {
                System.out.println("Connection successful!");
                // You can now use the connection object to execute queries.
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        //driver = new ChromeDriver(opt);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }


    public static Connection connectToMySQL(String url, String username, String password, String dbName) {
        try {
            // JDBC URL format: jdbc:mysql://<hostname>:<port>/<dbname>
            String jdbcUrl = url + "/" + dbName;

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Return the connection
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null or handle accordingly
        }
    }
    public void insertResponseTimeData(Connection conn, long endTime, String errorLog, long responseTime,
                                       long startTime, String status, String title, String url) {
        // SQL insert statement
        String insertQuery = "INSERT INTO response_time (End_time, ErrorLog, Response_time, Start_time, Status, Title, URL) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Use a try-with-resources to ensure PreparedStatement is closed properly
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            // Set the values for each placeholder (?)
            stmt.setLong(1, endTime);
            stmt.setString(2, errorLog);
            stmt.setLong(3, responseTime);
            stmt.setLong(4, startTime);
            stmt.setString(5, status);
            stmt.setString(6, title);
            stmt.setString(7, url);

            // Execute the insert statement
            int rowsAffected = stmt.executeUpdate();

            System.out.println("Rows affected: " + rowsAffected);  // Output the number of rows inserted

        } catch (Exception e) {
            // Handle any SQL exception
            e.printStackTrace();
            System.err.println("Error inserting data into response_time table: " + e.getMessage());
        }
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
//            throw new RuntimeException("config.properties not found at config file path" + configFilePath);
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
//        ConfigFileReader();
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
                try{
                    String url = step.get("url").asText();
                    System.out.println(url);
                    long start=System.currentTimeMillis()/1000;
                    navigateAndWait(url);
                    long end=System.currentTimeMillis()/1000;
                    insertResponseTimeData(conn,end,"",end-start,start,"Success","","");
                } catch (Exception e) {
                    insertResponseTimeData(conn,0,e.toString(),0,0,"Failed","","");

                }

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
                                long start=System.currentTimeMillis()/1000;
                                String xpath = SelectorText.replace("xpath/", "");
                                System.out.println(xpath);
                                //Thread.sleep(2000);
                                waitForElementVisibility(By.xpath(xpath));

                                clickOnElement(By.xpath(xpath));
                                long end=System.currentTimeMillis()/1000;
                                insertResponseTimeData(conn,end,"",end-start,start,"Success","","");
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
                                long start=System.currentTimeMillis()/1000;
                                waitForElementVisibility(By.cssSelector(csspath));
                                clickOnElement(By.cssSelector(csspath));
                                long end=System.currentTimeMillis()/1000;
                                insertResponseTimeData(conn,end,"",end-start,start,"Success","","");

                            } else {
                                System.out.println("ID not found.");
                                insertResponseTimeData(conn,0,e.toString(),0,0,"Failed","","");

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
                        try{
                            if(SelectorText.startsWith("xpath"))
                            {
                                String xpath = SelectorText.replace("xpath/", "");
                                System.out.println(xpath);
                                String Text = step.get("value").asText();
                                long startTime=System.currentTimeMillis()/1000;
                                waitForElementVisibility(By.xpath(xpath));
                                long endTime=System.currentTimeMillis()/1000;
                                insertResponseTimeData(conn,endTime,"",endTime-startTime,startTime,"Success","","");

                                enterText(By.xpath(xpath),Text);

                                break;
                            }

                        } catch (Exception e) {
                            String errorLog=e.toString();
                            insertResponseTimeData(conn,0,errorLog,0,0,"Failed","","");

                        }


                    }
                }
            }

        }

    }
}
