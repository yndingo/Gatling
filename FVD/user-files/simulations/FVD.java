
import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class FVD extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://fvd.ru")
    .inferHtmlResources()
  ;
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
    Map.entry("accept-encoding", "gzip, deflate, br"),
    Map.entry("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7"),
    Map.entry("sec-ch-ua", "Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120"),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "Windows"),
    Map.entry("sec-fetch-dest", "document"),
    Map.entry("sec-fetch-mode", "navigate"),
    Map.entry("sec-fetch-site", "none"),
    Map.entry("sec-fetch-user", "?1"),
    Map.entry("upgrade-insecure-requests", "1"),
    Map.entry("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
  );
  
  private Map<CharSequence, String> headers_1 = Map.ofEntries(
    Map.entry("accept", "application/json, text/javascript, */*; q=0.01"),
    Map.entry("accept-encoding", "gzip, deflate, br"),
    Map.entry("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7"),
    Map.entry("content-type", "application/json; charset=UTF-8"),
    Map.entry("origin", "https://fvd.ru"),
    Map.entry("sec-ch-ua", "Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120"),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "Windows"),
    Map.entry("sec-fetch-dest", "empty"),
    Map.entry("sec-fetch-mode", "cors"),
    Map.entry("sec-fetch-site", "same-origin"),
    Map.entry("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"),
    Map.entry("x-requested-with", "XMLHttpRequest")
  );
  

	int itemID = 93186;
	private ScenarioBuilder scn = scenario("FVD")		
		.exec(
		  http("FVDopen")
			.get("/")
			.headers(headers_0)
			.check(status().is(200)
				//,regex((?<=buy_click\(event,)(.*?)(?=\);)).findRandom().saveAs("randomProductPid")
				,regex("(?<=buy_click\\(event,)(.*?)(?=\\);)").findRandom().saveAs("randomProductPid")
			)
			//.resources()
		)		
		.exec(
			http("FVDLogin")
            .post("/default.aspx/IShopLogin")
            .headers(headers_1)
            .body(RawFileBody("sitelogin/SiteLogin.json"))
			.check(status().is(200)
				,substring("\"resultState\":0,\"count\":1,\"value\":\"\",\"script\":\"\"").find(0).exists()				
				)
			//.check(bodyString().saveAs("responseBody"))
		)
		.exec(
			http("FVDAddItem")
            .post("/advprice/bs.aspx/AddInvoiceID")
            .headers(headers_1)
            //.body(StringBody("{\"strID\":93186,\"strCOMP\":\"\",\"strIDSTORE\":\"\"}"))
			//.body(StringBody("{\"strID\":"+itemID+",\"strCOMP\":\"\",\"strIDSTORE\":\"\"}"))
			.body(StringBody("{\"strID\":#{randomProductPid},\"strCOMP\":\"\",\"strIDSTORE\":\"\"}"))
			.check(status().is(200)
				//,substring("\"resultState\":0,\"count\":1,\"value\":\"\",\"script\":\"\"").find(0).exists()
				)
			.check(bodyString().saveAs("responseBody"))
		)
		

		.exec(
			session -> {
              System.out.println("Response Body:");			  
              System.out.println(session.getString("responseBody"));
			  System.out.println("Variable:");
			  System.out.println("#{randomProductPid}");
			  System.out.println(session.getString("randomProductPid"));
              return session;
          }); 
		

  {
	  //setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
	  setUp(
		scn.injectOpen(
			//разгон с 0 до 3 рпс в течении 10 секунд
			rampUsersPerSec(0).to(3).during(10)
			//затем постоянная нагрузка 2рпс в течении 30 секунд
			,constantUsersPerSec(2).during(30)		
		)
	  )
	  .protocols(httpProtocol);
  }
}
