package gov.nysenate.ess.web;

import gov.nysenate.ess.core.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(SillyTest.class)
public class LogTests extends WebTests
{
    private static final Logger logger = LoggerFactory.getLogger(LogTests.class);

    @Test
    public void logTest() {
        logger.info("This is a debug log!");
    }
}
