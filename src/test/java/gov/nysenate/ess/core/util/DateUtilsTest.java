package gov.nysenate.ess.core.util;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static java.time.DayOfWeek.*;
import static gov.nysenate.ess.core.util.DateUtils.*;

@Category(UnitTest.class)
@WorkInProgress(author = "sam", since = "2/17/2017", desc = "needs to cover more methods")
public class DateUtilsTest {

    @Test
    public void testIsWeekday() throws Exception {
        LocalDate monday = LocalDate.of(2017, 1, 16);
        LocalDate saturday = LocalDate.of(2017, 1, 21);
        assertEquals(MONDAY, monday.getDayOfWeek());
        assertEquals(SATURDAY, saturday.getDayOfWeek());
        assertTrue(saturday.isAfter(monday));
        assertEquals(5, ChronoUnit.DAYS.between(monday, saturday));

        for (LocalDate date = monday; date.isBefore(saturday); date = date.plusDays(1)) {
            assertTrue(String.format("%s(%s) should be considered a weekday", date, date.getDayOfWeek()),
                    isWeekday(date));
        }
        for (LocalDate date = saturday; date.isBefore(saturday.plusDays(2)); date = date.plusDays(1)) {
            assertTrue(String.format("%s(%s) should not be considered a weekday", date, date.getDayOfWeek()),
                    !isWeekday(date));
        }
    }

    @Test
    public void testGetNumberOfWeekdays() throws Exception {
        LocalDate monday = LocalDate.of(2017, 1, 16);
        assertEquals(MONDAY, monday.getDayOfWeek());
        LocalDate wednesday = LocalDate.of(2017, 1, 18);
        assertEquals(WEDNESDAY, wednesday.getDayOfWeek());
        LocalDate saturday = LocalDate.of(2017, 1, 21);
        assertEquals(SATURDAY, saturday.getDayOfWeek());

        Range<LocalDate> monWeekRange = Range.closedOpen(monday, monday.plusWeeks(1));
        assertEquals(5, getNumberOfWeekdays(monWeekRange));

        Range<LocalDate> wedsWeekRange = Range.closedOpen(wednesday, wednesday.plusWeeks(1));
        assertEquals(5, getNumberOfWeekdays(wedsWeekRange));

        Range<LocalDate> lessThanWeekRange = Range.closed(monday, wednesday);
        assertEquals(3, getNumberOfWeekdays(lessThanWeekRange));

        Range<LocalDate> ltwWeekendRange = Range.closed(saturday, wednesday.plusWeeks(1));
        assertEquals(3, getNumberOfWeekdays(ltwWeekendRange));

        Range<LocalDate> satWeekRange = Range.closedOpen(saturday, saturday.plusWeeks(1));
        assertEquals(5, getNumberOfWeekdays(satWeekRange));

        Range<LocalDate> longOffsetRange = Range.closedOpen(saturday.minusDays(3), saturday.plusWeeks(5).plusDays(2));
        assertEquals(28, getNumberOfWeekdays(longOffsetRange));

        Range<LocalDate> range2016 = Range.closedOpen(LocalDate.ofYearDay(2016, 1), LocalDate.ofYearDay(2017, 1));
        assertEquals(261, getNumberOfWeekdays(range2016));

        Range<LocalDate> range2017 = Range.closedOpen(LocalDate.ofYearDay(2017, 1), LocalDate.ofYearDay(2018, 1));
        assertEquals(260, getNumberOfWeekdays(range2017));
    }

}