package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class N11SearchSimulation extends Simulation {

    // HTTP Configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.n11.com")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .acceptLanguageHeader("tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7")
            .acceptEncodingHeader("gzip, deflate, br")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .header("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
            .header("sec-ch-ua-mobile", "?0")
            .header("sec-ch-ua-platform", "\"Windows\"")
            .header("Sec-Fetch-Dest", "document")
            .header("Sec-Fetch-Mode", "navigate")
            .header("Sec-Fetch-Site", "none")
            .header("Sec-Fetch-User", "?1")
            .header("Referer", "https://www.n11.com")
            .disableCaching()
            .disableWarmUp();


    // Search Scenario
    private String searchTerm = "köpek maması 15kg";
    private ScenarioBuilder searchScenario = scenario("Search Product Scenario")
            .exec(
                    http("Home Page")
                            .get("/")
                            .check(status().is(200))
            )
            .pause(2)
            .exec(
                    http("Search Request")
                            .get("/arama?q=" + searchTerm)
                            .check(status().is(200))
                            .check(css("div.productList").exists())
            )
            .pause(1)
            .exec(
                    http("Filter Results")
                            .get("/arama?q=" + searchTerm + "&srt=SALES_VOLUME")
                            .check(status().is(200))
            );

    {
        setUp(
                searchScenario.injectOpen(
                        atOnceUsers(1)
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lte(5000),
                        global().successfulRequests().percent().gte(95.0)
                );
    }
}