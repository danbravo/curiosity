package apiTests;

import io.restassured.response.Response;
import org.testng.annotations.*;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import java.util.List;

import static api.NASAApiCalls.*;
import static org.testng.Assert.assertTrue;

@Listeners(core.Listener.class)
@Features({"GET API request"})
@Stories({"Get Curiosity photos and metadata (API call), verify they are equal"})
public class VerifyImages {

    private static final String URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos";
    private static final String API_KEY = "htvAOXAKOjVwYu6L8ixPzLRp0G1SdaEKWcqCRTHJ";
    private static final String MARS_DIR = "imgByMarsTime";
    private static final String EARTH_DIR = "imgByEarthTime";
    private List<String> marsMeta;
    private List<String> earthMeta;

    @Parameters({"sol"})
    @Test(priority = 1)
    public void getDataBySolTime(@Optional("1000") String sol) {
        Response response = doGetRequest(URL, "sol", sol, API_KEY);
        marsMeta = getImageMetaData(response);
        assertTrue(saveImageFiles(response, MARS_DIR));
    }

    @Parameters({"sol"})
    @Test(priority = 2)
    public void getDataByEarthTime(@Optional("1000") String sol) {
        String earthDate = getEarthDate(doGetRequest(URL, "sol", sol, API_KEY));
        Response response = doGetRequest(URL, "earth_date", earthDate, API_KEY);
        earthMeta = getImageMetaData(response);
        assertTrue(saveImageFiles(response, EARTH_DIR));
    }

    @Test(priority = 3)
    public void compareImageContext() {
        assertTrue(compareImagesInDir(MARS_DIR, EARTH_DIR));
    }

    @Test(priority = 4)
    public void compareImageMetaData() {
        assertTrue(compareMetaData(marsMeta, earthMeta));
    }
}
