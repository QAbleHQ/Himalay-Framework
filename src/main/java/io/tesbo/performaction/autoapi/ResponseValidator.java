package io.tesbo.performaction.autoapi;

import com.jayway.jsonpath.JsonPath;
import io.tesbo.framework.exception.ResponseTimeAssertionException;
import io.tesbo.framework.readers.GetApiConfig;
import io.tesbo.framework.runner.TestRunner;
import io.tesbo.performaction.autoweb.testng_logs;

import org.json.JSONObject;
import org.testng.Assert;

public class ResponseValidator {

    JSONObject response;
    testng_logs logs = new testng_logs();


    public ResponseValidator(JSONObject response) {
        this.response = response;
    }


    public void responseShouldContains(String jsonPath, Object excepted) {

        Object object = JsonPath.parse(response.toString()).read(jsonPath);
        logs.test_step("Validating " + jsonPath + " value : " + object + " is equal to expected value : " + excepted);

        Assert.assertTrue(excepted.equals(object));
        logs.test_step("Test Passed");



    }


    public void validateResponseTime(Object apiResponseTime) {
        Long responseTime = (Long) apiResponseTime;
        json_file_reader reader = new json_file_reader();

        if (reader.isTimeAssertionEnable(TestRunner.currentConfig)) {


            if (responseTime <= reader.getTimeToCompare(TestRunner.currentConfig)) {

            } else {
                try {
                    throw new ResponseTimeAssertionException("Api Response time is " + apiResponseTime + " more time then expected : " + reader.getTimeToCompare(TestRunner.currentConfig));
                } catch (ResponseTimeAssertionException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void validateResponseTime(Object ExpectedTime, Object ActualTime) {
        Long expectedTime = (Long) ExpectedTime;
        Long actualTime = (Long) ActualTime;
        json_file_reader reader = new json_file_reader();

        if (actualTime <= expectedTime) {

        } else {
            try {
                throw new ResponseTimeAssertionException("Api Response time is " + actualTime + " more time then expected : " + expectedTime);
            } catch (ResponseTimeAssertionException e) {
                e.printStackTrace();
            }
        }
    }

    public void statusCodeShouldBe(int statusCode) {

        logs.test_step("Validating status code is equal to expected value : " + statusCode);
        Assert.assertTrue(Integer.parseInt(response.get("statusCode").toString()) == statusCode);
        logs.test_step("Test Passed");

    }


    public String getDataFromBody(String jsonPath) {
        return JsonPath.parse(response.toString()).read(jsonPath).toString();

    }


    public void validateSchema(String expectedJSonSchema) {
        UnityJSONParser parser = new UnityJSONParser(expectedJSonSchema);




        for (String singlePath : parser.getPathList()) {

            logs.test_step("Single path to match " + singlePath);
            Object object = JsonPath.parse(response).read(singlePath);

        }
    }
    public void validateSchemaFromRequestFile(String request_name) {
        UnityJSONParser parser = new UnityJSONParser(response.toString());

        GetApiConfig apiConfig = new GetApiConfig(request_name);
        for (String singlePath : parser.getPathList()) {
            Object object = JsonPath.parse(apiConfig.getSchema()).read(singlePath);
        }
    }


}
