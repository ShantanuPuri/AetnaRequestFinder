import java.util.regex.Pattern;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

public class Selenium {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
	public Selenium() {

	}

//  @Before
  public void setUp() throws Exception {
	// Change second parameter to match location of geckodriver file
	System.setProperty("webdriver.gecko.driver", "/Users/shantanupuri26/Downloads/geckodriver");
	DesiredCapabilities capabilities = DesiredCapabilities.firefox();
	capabilities.setCapability("marionette", true);
    driver = new FirefoxDriver(capabilities);
    baseUrl = "https://www.aetna.com/health-care-professionals/clinical-policy-bulletins/medical-clinical-policy-bulletins/alphabetical-order.html";
    //driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

//  @Test
  public String execute (int skip) throws Exception {
    System.out.println("executing");

    if (skip == 0) {	
    	driver.get(baseUrl);
    	
    	// clicks on "I accept" in popup box
    	driver.findElement(By.cssSelector("a.interlink.right-link")).click();
    	// clicks on first Medical CPB 
    	driver.findElement(By.cssSelector("li[type='Disc'] > a")).click();
    	
//      Uncomment and type a substring of the Medical CPB to search for specific CPB 
//    	driver.findElement(By.partialLinkText("Lipectomy")).click();
    	
    	// source stores the page source 
    	String source = driver.getPageSource();
    	// document stores the page source, after it has been parsed by Jsoup
    	Document document = Jsoup.parse(source);
    	
    	// creates an ArrayList to store all h2 
    	ArrayList<Element> headers = new ArrayList<Element>();      
    	
    	// headerElements stores all h2 tags in the source parsed using Jsoup
    	Elements headerElements = document.select("h2");
    	for (Element e : headerElements) {
    		headers.add(e);
    	}
    	
//    	for (Element e : headers) {
//    		System.out.println(e.text());
//    	}

    	// Hashset to prevent duplication problem
    	HashSet<String> hs = new HashSet<String>();
    	Elements all = document.body().select("*");
    	Iterator<Element> iterator = all.iterator();
		Element current;
		
		// for loop to iterate over ArrayList containing all headers 
    	for (int i = 0; i < headers.size() - 1; i++) {
    		current = iterator.next();
    		// while loop to iterate over all elements until next header element is found
    		while (!current.equals(headers.get(i))) {
    			if (!hs.contains(current.text())) {
    				// if current element is not already in the hashSet, we add it to the hashSet
    				hs.add(current.text());
    				// output of page
    				System.out.println(current.text());
    			}
    			else {
    				// test to see how many duplicate elements are occurring
//    				System.out.println("**********************DUPLICATE*****************");	
    			}
        		if (iterator.hasNext()) {
        			current = iterator.next();
        		}
    		}
    		// test to see when the next header is encountered
    		System.out.println("############################################################################");
    		// prints header in caps to precede the text under that header 
    		System.out.println(current.text().toUpperCase());
    	}

    }
    // returns inputted base URL to indicate parsing is complete 
	return baseUrl;
  }

  
//  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
//      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}