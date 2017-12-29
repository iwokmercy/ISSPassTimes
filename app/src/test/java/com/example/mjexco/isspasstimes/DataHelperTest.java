package com.example.mjexco.isspasstimes;

import com.example.mjexco.isspasstimes.helpers.DataHelper;
import com.example.mjexco.isspasstimes.objects.Response;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Class to test DataHelper getIssPassTimes(...)
 */

public class DataHelperTest {
    private DataHelper helper;
    private double lat, lon;
    private DataHelper.DataHelperListener listener;
    private List<Response> response;
    private boolean dataRetrievalPassed;

    @Before
    public void setUp(){
        helper = new DataHelper();
        lat = 40.7128;
        lon = 74.0060;
        listener = new DataHelper.DataHelperListener() {
            @Override
            public void onDataRetrieved(List<Response> responseList) {
                response = responseList;
                dataRetrievalPassed = true;
            }

            @Override
            public void onDataFailed() {
                response = null;
                dataRetrievalPassed = false;
            }
        };
    }

    @Test
    public void checkSetUp(){
        //check that helper and listener was initialized
        assertNotNull("Helper not initialized", helper);
        assertNotNull("Listener not initialized", listener);

        //check that lat and lon values were correctly assigned
        assertEquals("Value incorrect", 40.7128, lat, 0);
        assertEquals("Value incorrect", 74.0060, lon, 0);
    }

    @Test
    public void testDataRetrieval(){
        //call method
        helper.getIssPassTimes(lat, lon, listener);
        //check service response to determine which listener was triggered
        if(dataRetrievalPassed){
            //onDataRetrieved was triggered so response should not be null
            assertNotNull("List retrieved successfully", response);
        } else {
            //onDataFailed was triggered so response should be null
            assertNull(response);
        }
    }
}
