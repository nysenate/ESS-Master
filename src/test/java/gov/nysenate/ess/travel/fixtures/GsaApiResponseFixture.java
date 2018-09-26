package gov.nysenate.ess.travel.fixtures;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Contains raw text json responses from the GSA Api to be used in tests.
 */
public class GsaApiResponseFixture {

    public static String fy2018_zip10008_response() throws IOException {
        String path = GsaApiResponseFixture.class.getClassLoader().getResource("travel/gsa_api_responses/gsa_response_for_fy2018_zip10008.txt").getFile();
        return FileUtils.readFileToString(new File(path));
    }

    public static String fy2018_zip10940_response() throws IOException {
        String path = GsaApiResponseFixture.class.getClassLoader().getResource("travel/gsa_api_responses/gsa_response_for_fy2018_zip10940.txt").getFile();
        return FileUtils.readFileToString(new File(path));
    }
}
