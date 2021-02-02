package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.TravelAddressView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.route.destination.DestinationView;

import java.util.List;
import java.util.stream.Collectors;

public class RouteView implements ViewObject {

    private List<LegView> outboundLegs;
    private List<LegView> returnLegs;
    private List<DestinationView> destinations;
    private MileagePerDiemsView mileagePerDiems;
    private TravelAddressView origin;

    public RouteView() {
    }

    public RouteView(Route route) {
        outboundLegs = route.getOutboundLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        returnLegs = route.getReturnLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        origin = route.origin() == null ? null : new TravelAddressView(route.origin());
        destinations = route.destinations().stream()
                .map(DestinationView::new)
                .collect(Collectors.toList());
        mileagePerDiems = new MileagePerDiemsView(route.mileagePerDiems());
    }

    public Route toRoute() {
        return new Route(
                outboundLegs.stream().map(LegView::toLeg).collect(Collectors.toList()),
                returnLegs.stream().map(LegView::toLeg).collect(Collectors.toList())
        );
    }

    public List<LegView> getOutboundLegs() {
        return outboundLegs;
    }

    public List<LegView> getReturnLegs() {
        return returnLegs;
    }

    public List<DestinationView> getDestinations() {
        return destinations;
    }

    public TravelAddressView getOrigin() {
        return origin;
    }

    public MileagePerDiemsView getMileagePerDiems() {
        return mileagePerDiems;
    }

    @Override
    public String getViewType() {
        return "route";
    }
}
