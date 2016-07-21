package gov.nysenate.ess.supply.integration.requisition;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.unit.requisition.RequisitionFixture;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class SqlRequisitionDaoTests extends SupplyTests {

    @Autowired private RequisitionDao requisitionDao;

    @Test
    public void canInsertRequisition() {
        requisitionDao.saveRequisition(RequisitionFixture.getPendingRequisition());
    }

    @Test
    public void canGetRequisition() {
        requisitionDao.getRequisitionById(2);
    }

    @Test
    public void canSearchRequisitions() {
        Range<LocalDateTime> dateRange = Range.closed(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        requisitionDao.searchRequisitions("A42FB", "any", EnumSet.allOf(RequisitionStatus.class),
                                          dateRange, "ordered_date_time", "any", LimitOffset.ALL);
    }

    @Test
    public void canSearchOrderHistory() {
        Range<LocalDateTime> dateRange = Range.closed(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        requisitionDao.searchOrderHistory("A42FB", 1, EnumSet.allOf(RequisitionStatus.class),
                                          dateRange, "ordered_date_time", LimitOffset.ALL);
    }

}



//    @Before
//    public void setup() {
//        LocalDateTime createdDateTime = LocalDateTime.now();
//        requisition = new Requisition(createdDateTime, RequisitionFixture.getPendingVersion());
//    }
//
//    @Test
//    public void canInsertRequisition() {
//        int id = requisitionDao.saveRequisition(requisition);
//        assertThat(id, is(greaterThan(0)));
//    }
//
//    // TODO: can update requisition
//
//    @Test
//    public void canGetRequisitionById() {
//        int id = requisitionDao.saveRequisition(requisition);
//        Requisition actual = requisitionDao.getRequisitionById(id);
//        // only tests that the sql executes without errors
//        // cant do much testing unless we create versions with real employees
//    }
//
//    @Test
//    public void canSearchRequisitions() {
//        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);
//        LocalDateTime toDate = LocalDateTime.now();
//        Range<LocalDateTime> dateRange = Range.closed(fromDate, toDate);
//        PaginatedList<Requisition> requisitions = requisitionDao.searchRequisitions("all", "all", EnumSet.allOf(RequisitionStatus.class),
//                                                                                    dateRange, "processed_date_time", LimitOffset.ALL);
//        assertThat(requisitions.getResults().size(), is(greaterThan(1)));
//    }
//
//    @Test
//    public void canSearchOrderHistory() {
//        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);
//        LocalDateTime toDate = LocalDateTime.now();
//        Range<LocalDateTime> dateRange = Range.closed(fromDate, toDate);
//        PaginatedList<Requisition> requisitions = requisitionDao.searchOrderHistory("A42FB-W", 11168, EnumSet.allOf(RequisitionStatus.class),
//                                                                                    dateRange, "ordered_date_time", LimitOffset.ALL);
//        assertThat(requisitions.getResults().size(), is(greaterThan(1)));
//    }
//}
