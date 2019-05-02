package foodie.app.rubikkube.foodie.apiUtils;

import app.wi.lakhanipilgrimage.api.SOService;

public class ApiUtils {


    public static String BASE_URL = "";
    public static SOService getSOService() {
       BASE_URL = "http://34.220.151.44";
        return RetrofitClient.getClient(BASE_URL).create(SOService.class);
    }

}