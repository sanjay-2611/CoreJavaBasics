package demoblaze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import commonUtils.*;
import io.github.bonigarcia.wdm.WebDriverManager;

public class caseStudy {
	WebDriver driver;
	Properties prop;
	ExtentReports reports;
	ExtentSparkReporter spark;
	ExtentTest extentTest;
	WebDriverWait wait;
	@BeforeClass(groups={"featureOne","featureTwo"})
	public void open()
	{
		reports= new ExtentReports();
		spark =new ExtentSparkReporter("target\\demoblaze.html");
		reports.attachReporter(spark);
		 wait= new WebDriverWait(driver, Duration.ofSeconds(30));
	}
	@Parameters("browser")
	@BeforeTest(groups={"featureOne","featureTwo"})
	public void startup(String Strbrowser) throws IOException {
		if(Strbrowser.equalsIgnoreCase("chrome")) {
		WebDriverManager.chromedriver().setup();
		driver=new ChromeDriver();
		}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
		driver.manage().window().maximize();
		String path=System.getProperty("user.dir")+"\\src\\test\\resources\\ConfigFiles\\config.properties";
		 prop=new Properties();
		FileInputStream obtained = new FileInputStream(path);
		prop.load(obtained);
		driver.get("https://www.demoblaze.com");
	}
  @Test(priority=1,groups={"featureOne","featureTwo"})
  public void login() throws InterruptedException, InvalidFormatException, IOException {
	  extentTest=reports.createTest("Login");
	  driver.findElement(By.id("login2")).click();
//	  Thread.sleep(2000);
	  driver.findElement(By.id("loginusername")).sendKeys(prop.getProperty("uname"));
	  driver.findElement(By.id("loginpassword")).sendKeys(prop.getProperty("pass"));
	  driver.findElement(By.xpath("//button[text()='Log in']")).click();
	  driver.findElement(By.xpath(readXLData("Home"))).click();
//	  Thread.sleep(5000);
//	  WebElement welcome=driver.findElement(By.className("nav-link"));
//	  wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nameofuser")));
	  WebElement wel= driver.findElement(By.id("nameofuser"));
	  wait.until(ExpectedConditions.elementToBeClickable(wel));
	  Assert.assertEquals(wel.getText(), "Welcome kekran");
  }

  @Test(dataProvider="data", priority=2,groups="featureTwo")
  public void selectMultipleItems(String item) throws InterruptedException, InvalidFormatException, IOException
  {
	  extentTest=reports.createTest("Add Multiple Item");
	  driver.findElement(By.xpath(readXLData("Home"))).click();
	  driver.findElement(By.linkText(item)).click();

	  driver.findElement(By.xpath("//a[contains(text(),'Add to cart')]")).click();

	  wait.until(ExpectedConditions.alertIsPresent());
	  Alert alert=driver.switchTo().alert();
	  
	  alert.accept();
	  driver.findElement(By.xpath(readXLData("Cart"))).click();

	  int count=0;
	  List<WebElement> cartlist=driver.findElements(By.xpath("//td[2]"));
	  for(WebElement cartslist: cartlist)
	  {
		  if(cartslist.getText().equalsIgnoreCase(item)) {
		  Assert.assertEquals(cartslist.getText(), item);
		  count++;
		  }		  
	  }
	  if(count==0)
		  Assert.assertFalse(true);
	  
  }
  @Test(priority=3,groups="featureTwo")
  public void DeleteItemInCart() throws InvalidFormatException, IOException, InterruptedException
  {
	  extentTest=reports.createTest("Delete Item");
	  driver.findElement(By.xpath(readXLData("Home"))).click();
	  driver.findElement(By.xpath(readXLData("Cart"))).click();
	  List<WebElement> beforeDelete = driver.findElements(By.xpath("//tr[@class='success']"));
	  int before=beforeDelete.size();
//	  driver.findElement(By.xpath(readXLData("Delete"))).click();
//	  Thread.sleep(5000);
	  List<WebElement> AfterDelete = driver.findElements(By.xpath("//tr[@class='success']"));
//	  
	  int after= AfterDelete.size();
//	  Thread.sleep(2000);
	 
	 Assert.assertNotEquals(before, after);
  }
  @Test(priority=4,groups={"featureOne","featureTwo"})
  public void PlaceOrder() throws InterruptedException {
	  extentTest=reports.createTest("Place Order");
	  driver.findElement(By.xpath("//button[contains(text(),'Place Order')]")).click();
	  Thread.sleep(2000);
	  driver.findElement(By.xpath("//input[@id='name']")).sendKeys("Jack Sparrow");
	  driver.findElement(By.xpath("//input[@id='country']")).sendKeys("Japan");
	  driver.findElement(By.xpath("//input[@id='city']")).sendKeys("Hiroshima");
	  driver.findElement(By.xpath("//input[@id='card']")).sendKeys("1234567890");
	  driver.findElement(By.xpath("//input[@id='month']")).sendKeys("February");
	  driver.findElement(By.xpath("//input[@id='year']")).sendKeys("2025");
	  driver.findElement(By.xpath("//button[text()='Purchase']")).click();
	  Thread.sleep(2000);
	  Assert.assertEquals(driver.findElement(By.xpath("(//h2)[3]")).getText(), "Thank you for your purchase!");
	  driver.findElement(By.xpath("//button[text()='OK']")).click();
	  
  }
  @DataProvider(name="data")
  public Object[][] ProListCSV() throws CsvValidationException, IOException{
	  String path=System.getProperty("user.dir")+"//src//test//resources//testData//singledata.csv";
	  String[] cols;
	  CSVReader reader = new CSVReader(new FileReader(path));
	  ArrayList<Object> dataList=new ArrayList<Object>();
	  while((cols=reader.readNext())!=null)
	  {
		  Object[] record= {cols[0]};
		  dataList.add(record);
	  }
	  return dataList.toArray(new Object[dataList.size()][]);
	  
  }
  
 
  public String readXLData(String PathName) throws InvalidFormatException, IOException {
	  String objPath="";
	  String path=System.getProperty("user.dir")+"//src//test//resources//testData//ExcelData.xlsx";
	  XSSFWorkbook workbook= new XSSFWorkbook(new File(path));
	  XSSFSheet sheet=workbook.getSheet("sheet1");
	  int numRows=sheet.getLastRowNum();
	  for(int i=0; i<=numRows; i++)
	  {
		  XSSFRow row=sheet.getRow(i);
		  if(row.getCell(0).getStringCellValue().equalsIgnoreCase(PathName))
			  objPath=row.getCell(1).getStringCellValue();
	  }
	  return objPath;
}
  
  @AfterMethod(groups={"featureOne","featureTwo"})
	 public void close(ITestResult result)
	 {
		 if(ITestResult.FAILURE== result.getStatus()) {
				extentTest.log(Status.FAIL, result.getThrowable().getMessage());
				String strPath=Utility.getScreenshotPath(driver);
				extentTest.addScreenCaptureFromPath(strPath);
			}		 
	 }
  
  @AfterTest(groups={"featureOne","featureTwo"})
  public void teardown() {
	  driver.close();
	  reports.flush();
  }
}
