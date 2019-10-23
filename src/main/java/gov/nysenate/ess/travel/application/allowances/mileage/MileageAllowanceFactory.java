package gov.nysenate.ess.travel.application.allowances.mileage;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MileageAllowanceFactory {

    private MileageAllowanceService mileageService;

    @Autowired
    public MileageAllowanceFactory(MileageAllowanceService mileageService) {
        this.mileageService = mileageService;
    }

    public MileageAllowances calculateMileageAllowance(Route route) throws InterruptedException, ApiException, IOException {
        List<MileageAllowance> outboundLegAllowances = new ArrayList<>();
        List<MileageAllowance> returnLegAllowances = new ArrayList<>();

        for (Leg leg : route.getOutgoingLegs()) {
            if (leg.getModeOfTransportation().qualifiesForMileageReimbursement()) {
                MileageAllowance lma = calculateLegAllowance(leg);
                outboundLegAllowances.add(lma);
            }
        }

        for (Leg leg : route.getReturnLegs()) {
            if (leg.getModeOfTransportation().qualifiesForMileageReimbursement()) {
                MileageAllowance lma = calculateLegAllowance(leg);
                returnLegAllowances.add(lma);
            }
        }

        return new MileageAllowances(outboundLegAllowances, returnLegAllowances);
    }

    private MileageAllowance calculateLegAllowance(Leg leg) throws InterruptedException, ApiException, IOException {
        double miles = mileageService.drivingDistance(leg.getFrom(), leg.getTo());
        BigDecimal mileageRate = mileageService.getIrsRate(leg.getTravelDate());
        return new MileageAllowance(UUID.randomUUID(), leg, miles, mileageRate);
    }
}
