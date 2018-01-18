package api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import ru.yandex.qatools.allure.annotations.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public final class NASAApiCalls {

    private NASAApiCalls() {

    }

    @Step("GET Request")
    public static Response doGetRequest(String url, String dateType, String sol, String apiKey) {
        return given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).
                when().get(url + "?" + dateType + "=" + sol + "&page=1&api_key=" + apiKey).
                then().statusCode(200).contentType(ContentType.JSON).extract().response();
    }

    @Step("Save meta data")
    public static List<String> getImageMetaData(Response response) {
        return response.path("photos[0,1,2,3,4,5,6,7,8,9]");
    }

    @Step("Save first 10 images")
    public static boolean saveImageFiles(Response response, String dir) {
        List<String> imgs = response.path("photos.img_src[0,1,2,3,4,5,6,7,8,9]");
        return saveImages(imgs, dir);
    }

    @Step("Compare images for context")
    public static boolean compareImagesInDir(String dirA, String dirB) {
        File[] marsImgs = new File(dirA).listFiles();
        File[] earthImgs = new File(dirB).listFiles();

        boolean equality = false;

        assert earthImgs != null;
        assert marsImgs != null;

        imageCheck:
        if (marsImgs.length == earthImgs.length) {
            for (int i = 0; i < marsImgs.length; i++) {
                if (!areImagesEqual(marsImgs[i], earthImgs[i])) {
                    System.err.println("Images context is not equal");
                    break imageCheck;
                }
            }
            equality = true;
        } else {
            System.err.println("Quantities of images in directories are not equal");
        }
        cleanFolders(dirA, dirB);

        return equality;
    }

    @Step("Compare images for metadata")
    public static boolean compareMetaData(List<String> marsMeta, List<String> earthMeta) {
        return CollectionUtils.isEqualCollection(marsMeta, earthMeta);
    }

    @Step("Get earth date")
    public static String getEarthDate(Response response) {
        return response.path("photos[0].earth_date");
    }

    private static boolean saveImages(List<String> images, String folderName) {
        try {
            Files.createDirectories(Paths.get(folderName));
            for (String s : images) {
                URL url = new URL(s);
                BufferedImage img = ImageIO.read(url);
                File file = new File(folderName + "\\" + captureName(s, "(\\w*).[\\w]{3}$") + ".jpg");
                ImageIO.write(img, "jpg", file);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean areImagesEqual(File fileA, File fileB) {
        try {
            BufferedImage biA = ImageIO.read(fileA);
            DataBuffer dbA = biA.getData().getDataBuffer();
            int sizeA = dbA.getSize();
            BufferedImage biB = ImageIO.read(fileB);
            DataBuffer dbB = biB.getData().getDataBuffer();
            int sizeB = dbB.getSize();

            if (sizeA == sizeB) {
                for (int i = 0; i < sizeA; i++) {
                    if (dbA.getElem(i) != dbB.getElem(i)) {
                        return false;
                    }
                }
                return true;
            } else {
                System.err.println("Image size is not equal");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to compare image files");
            return false;
        }
    }

    private static void cleanFolders(String... folders) {
        for (String f : folders) {
            try {
                FileUtils.deleteDirectory(new File(f));
//                FileUtils.cleanDirectory(new File(f));
            } catch (IOException e) {
                System.err.println("Error while removing dirs");
                e.printStackTrace();
            }
        }
    }

    private static String captureName(String largeText, String regex) {
        Pattern ptn =
                Pattern.compile(regex);
        Matcher mtch = ptn.matcher(largeText);
        List<String> ips = new ArrayList<>();
        while (mtch.find()) {
            ips.add(mtch.group());
        }
        return Arrays.toString(ips.toArray());
    }
}
