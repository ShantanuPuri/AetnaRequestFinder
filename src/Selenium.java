import java.util.regex.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
 
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "Payer, Title, Condition, Number, Policy";
	
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
  }

//  @Test
// Each loop of execute retrieves the data for a single medical CPB
  public String execute (int skip, int list_no) throws Exception {
	  String payer = "Aetna";
	  String currentTitle = "";
	  String[] splitTitle;
	  String condition = "";
	  String[] splitNumber;
	  String number = "";
	  String comma = "";

	  // stores current directory 
	  String fileName = "AetnaCPB_" + list_no + ".csv"; 
	  FileWriter fileWriter = null;
  	  fileWriter = new FileWriter(fileName);
  	  
      // writes the file header to the csv
  	  fileWriter.append(FILE_HEADER.toString());
  	  
  	  // adds a new line separator to the csv after the header and flushes it 
  	  fileWriter.append(NEW_LINE_SEPARATOR);
  	  fileWriter.flush();
  	  
  	  String concatPolicy = "";
  	  String concatBackground = "";

  	// skip is 0 unless changed; condition is always true
    if (skip == 0) {
    	
    	// uses the baseUrl string to get to the Medical CPB Bulletin (Alphabetical) webpage 
    	driver.get(baseUrl);
    	
    	// clicks on "I accept" in the Clinical Policy Bulletin popup that appears each time 
    	// the Aetna site is accessed through a new browser 
    	driver.findElement(By.cssSelector("a.interlink.right-link")).click();
    	// clicks on the first Medical CPB 
    	List<WebElement> links = driver.findElements(By.cssSelector("li[type='Disc'] > a"));
    	
//      Uncomment and type a substring of the Medical CPB to search for a specific CPB 
//    	driver.findElement(By.partialLinkText("Aortic")).click();
   
    	currentTitle = links.get(list_no).getText();
    	links.get(list_no).click();
    	
    	// source stores the entire page source 
    	String source = driver.getPageSource();
    	// document stores the page source, after it has been parsed by Jsoup
    	Document document = Jsoup.parse(source);
    	
    	
    	// creates an ArrayList to store all h2 
    	ArrayList<Element> headers = new ArrayList<Element>();
    	ArrayList<Element> h3ers = new ArrayList<Element>();
    	
    	// headerElements stores all h2 tags in the source parsed using Jsoup
    	Elements headerElements = document.select("h2");
    	// adds all elements stored in headerElements to the headers ArrayList
    	for (Element e : headerElements) {
    		headers.add(e);
    	}

    	// Hashset to prevent duplication problem
    	HashSet<String> hs = new HashSet<String>();
    	Elements all = document.body().select("*");
    	Iterator<Element> iterator = all.iterator();
		Element current;
		
		Integer liCount = 0;
		boolean liInside = false;
		boolean showLines = false;
		// for loop to iterate over ArrayList containing all headers 
    	for (int i = 0; i < headers.size() - 1; i++) {
    		current = iterator.next();
    		// while loop to iterate over all elements until next header element is found
    		while (!current.equals(headers.get(i))) {
    			
    			if (!hs.contains(current.text())) {
    				// if current element is not already in the hashSet, we add it to the hashSet
    				hs.add(current.text());
    				
    					// handles all list elements
    					if (current.tagName().equals("ol") || current.tagName().equals("ul") && showLines) {
    						// calculating indentation spaces
    						String spaces = "";
    						if (liInside) {
    							liCount += 1;
    							for (int x = 0; x < liCount; x++) {
    								spaces += "      ";
    							}
    						} else {
    							liCount -= 1;
    						}
    						// selects all the list elements 
    						Elements elements = current.select("li");
    						// creates an iterator to iterate over the list elements
    				    	Iterator<Element> listIterator = elements.iterator();
    				    	
    				    	while (listIterator.hasNext()) {
    				    		// while listIterator has more elements, enter loop
    				    		// i == 1 (Policy is always second header)
    				    		if (i == 1) {
    				    			comma = listIterator.next().text();
    				    			comma = commaWorkaround(comma);
    				    			concatPolicy += comma;
    				    		}
    				    		// i == 2 (Background is always third header)
    				    		else if (i == 2) {
    				    			comma = listIterator.next().text();
    				    			comma = commaWorkaround(comma);
    				    			concatBackground += comma;
    				    		}
    				    		else {
    				    			listIterator.next();
    				    		}
    				    	}
    				    	
    						liInside = false;

    					}
    					else if (current.tagName().equals("li") && showLines) {
    						liInside = true;
    					}
    					
    					// handles all paragraph elements
    					else if (current.tagName().equals("p") && showLines) {
				    		if (i == 1) {
				    			comma = current.text();
				    			comma = commaWorkaround(comma);
    				    		concatPolicy += comma;
				    		}
				    		else if (i == 2) {
				    			comma = current.text();
				    			comma = commaWorkaround(comma);
				    			concatBackground += comma;				    		
				    			}
    					}
    					
    					// handles all link elements
    					else if (current.tagName().equals("a") && showLines) {
				    		if (i == 1) {
				    			comma = current.text();
				    			comma = commaWorkaround(comma);
    				    		concatPolicy += comma;
				    		}
				    		else if (i == 2) {
				    			comma = current.text();
				    			comma = commaWorkaround(comma);
				    			concatBackground += comma;					    		}
//    						writer.println("!!!!!!!!!!!!!!!!!! LINK DIVISION END !!!!!!!!!!!!!!!!!!");
    					}
    					
    					// handles all table elements
    					else if (current.tagName().equals("table")) {
    						
    						// handles the last table, which contains all the relevant CPT/HCPCS/ICD-10 codes
    						if (current.toString().contains("CPT Codes / HCPCS Codes / ICD-10 Codes")) {

    						}
    					}
    				}
    			
        		if (iterator.hasNext()) {
        			current = iterator.next();
        		}
    		}
    	    
    		if (!showLines) {
    			showLines = true;
    		}	
    	}
    }

    // separates number from Title 
    splitTitle = currentTitle.split("-"); 
    condition = splitTitle[0];
    number = splitTitle[1];
    splitNumber = number.split("\\W+");
    number = splitNumber[2];
    
    
    // WRITES OUTPUT TO CSV FILE
    
    fileWriter.append(payer);
    fileWriter.append(COMMA_DELIMITER);
    
    fileWriter.append(currentTitle);
    fileWriter.append(COMMA_DELIMITER);
    
    fileWriter.append(condition);
    fileWriter.append(COMMA_DELIMITER);
    
    fileWriter.append(number);
    fileWriter.append(COMMA_DELIMITER);
        
    fileWriter.append(concatPolicy);
    fileWriter.append(COMMA_DELIMITER);
    
    fileWriter.append(NEW_LINE_SEPARATOR);
    fileWriter.append(NEW_LINE_SEPARATOR);

    fileWriter.append(concatBackground);
    fileWriter.append(NEW_LINE_SEPARATOR);
    
    fileWriter.close();
//    Selenium.lineByLine();
	return baseUrl;  
	}
  
  private static String commaWorkaround(String s) {
	    return "\"" + s + "\"";
	}
  
/*
	  private static void lineByLine() {
      try (BufferedReader br = new BufferedReader(new FileReader("/Users/shantanupuri26/Documents/Shantanu/FastAuth/AetnaRequester/AetnaCPB_output.csv"))) {
    	 ArrayList<String> testList = new ArrayList<String>();
    	 String currentLine;
         int counter = 0;
         while ((currentLine = br.readLine()) != null) {
        	if (counter == 0) {
                System.out.println(counter + ": " + currentLine);
        		testList.add(currentLine);
        	}
        	else {
                currentLine = commaWorkaround(currentLine);
                System.out.println(counter + ": " + currentLine);
                testList.add(currentLine);
        	}
            counter++;
         }

         
      } catch (IOException e) {
         e.printStackTrace();
      }
   } 
   */

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