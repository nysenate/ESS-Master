package gov.nysenate.ess.travel.application.uncompleted;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.AllowancesDtoView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowanceFactory;
import gov.nysenate.ess.travel.application.*;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesView;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesView;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.utils.UploadProcessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application/uncompleted")
public class UncompletedTravelAppCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(UncompletedTravelAppCtrl.class);

    @Autowired private TravelApplicationService applicationService;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private InMemoryTravelAppDao appDao;
    @Autowired private UploadProcessor uploadProcessor;
    @Autowired private MileageAllowanceFactory mileageAllowanceFactory;

    /**
     * Initialize a mostly empty travel app, containing just the traveling {@link Employee}
     * and the submitter {@link Employee}
     * <p>
     * (POST) /api/v1/travel/application/uncompleted/init
     * <p>
     * Request Params: empId (int) - The travelers employee id.
     *
     * @param empId
     * @return An application with the traveler and submitter initialized.
     */
    @RequestMapping(value = "/init", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse initTravelApplication(@RequestParam int empId) {
        Employee traveler = employeeInfoService.getEmployee(empId);
        Employee submitter = employeeInfoService.getEmployee(getSubjectEmployeeId());
        TravelApplicationView uncompletedApp = appDao.getUncompletedAppByEmpId(traveler.getEmployeeId());
        if (uncompletedApp == null) {
            TravelApplication app = applicationService.initTravelApplication(traveler, submitter);
            uncompletedApp = new TravelApplicationView(app);
            uncompletedApp = appDao.saveUncompleteTravelApp(uncompletedApp);
        }
        return new ViewObjectResponse<>(uncompletedApp);
    }

    /**
     * Submits an application to be reviewed.
     * @param id The Uncompleted Application Id to be submitted.
     *
     * <p>
     *  (PUT) /api/v1/travel/application/uncompleted/{id}/purpose
     * </p>
     *
     * Path Params: id (string) - The id of an uncompleted travel application to submit.
     *
     * @return
     */
    @RequestMapping(value = "/{id}/submit", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitTravelApp(@PathVariable String id) {
        TravelApplicationView appView = appDao.getUncompletedAppById(UUID.fromString(id));
        TravelApplication app = appView.toTravelApplication();

        applicationService.insertTravelApplication(app);
        // Delete uncompleted version.
        appDao.deleteApplication(app.getTraveler().getEmployeeId());
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * Deletes the uncompleted travel application belonging the the specified employee.
     *
     * This allows users to reset their application and start over.
     *
     * <p>
     *  (DELETE) /api/v1/travel/application/uncompleted/{empId}
     * </p>
     *      Path Params: empId (int) - The employee Id who's uncompleted applications should be deleted.
     *
     * @param empId
     * @return
     */
    @RequestMapping(value = "/{empId}", method = RequestMethod.DELETE)
    public BaseResponse cancelApplication(@PathVariable int empId) {
        appDao.deleteApplication(empId);
        return new SimpleResponse(true, "Successfully canceled travel application", "travel-app-cancel");
    }

    /**
     * Saves a purpose to an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/purpose
     * <p>
     *     Path Params: id (string) - The id of an uncompleted travel application to update with the given purpose.<br>
     *     Body : The purpose of travel.
     *
     * @param id
     * @param purpose
     * @return The application with its purpose updated.
     */
    @RequestMapping(value = "/{id}/purpose", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse savePurpose(@PathVariable String id, @RequestBody String purpose) {
        TravelApplicationView app = appDao.getUncompletedAppById(UUID.fromString(id));
        app.setPurposeOfTravel(purpose);
        app = appDao.saveUncompleteTravelApp(app);
        return new ViewObjectResponse<>(app);
    }

    /**
     * Saves the outbound segments of an uncompleted application.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/outbound
     * <p>
     *     Path Params: id (String) - The id of an uncompleted travel application to update with the given outbound segments.<br>
     *     Body : An array of SegmentView's
     */
    @RequestMapping(value = "/{id}/outbound", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveOutboundSegments(@PathVariable String id, @RequestBody RouteView route) {
        List<Leg> outboundLegs = route.toRoute().getOutgoingLegs();
        TravelApplication travelApplication = applicationService.addOutboundLegs(UUID.fromString(id), outboundLegs);

        TravelApplicationView app = new TravelApplicationView(travelApplication);
        app = appDao.saveUncompleteTravelApp(app);
        return new ViewObjectResponse<>(app);
    }

    /**
     * Saves the return segments of an uncompleted application and calculates the Route and Accommodations.
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/return
     * <p>
     *     Path Params: id (String) - The id of an uncompleted travel application to update with the given return segments.<br>
     *     Body : An array of SegmentView's
     */
    @RequestMapping(value = "/{id}/return", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveReturnSegments(@PathVariable String id, @RequestBody RouteView route) throws IOException, ApiException, InterruptedException {
        TravelApplication travelApplication = applicationService.addReturnLegs(UUID.fromString(id), route.toRoute().getReturnLegs());
        TravelApplicationView updatedView = new TravelApplicationView(travelApplication);
        updatedView = appDao.saveUncompleteTravelApp(updatedView);
        return new ViewObjectResponse<>(updatedView);
    }

    /**
     * Sets the user specified expenses on a travel application
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/expenses
     * <p>
     */
    @RequestMapping(value = "/{id}/expenses", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveExpenses(@PathVariable String id, @RequestBody AllowancesDtoView allowancesDto) {
        TravelApplicationView appView = appDao.getUncompletedAppById(UUID.fromString(id));
        appView.setTollsAllowance(allowancesDto.tollsAllowance);
        appView.setParkingAllowance(allowancesDto.parkingAllowance);
        appView.setAlternateAllowance(allowancesDto.alternateAllowance);
        appView.setRegistrationAllowance(allowancesDto.registrationAllowance);
        appView.setTrainAndAirplaneAllowance(allowancesDto.trainAndAirplaneAllowance);

        TravelApplication app = appView.toTravelApplication();
        appView = new TravelApplicationView(app);
        appView = appDao.saveUncompleteTravelApp(appView);
        return new ViewObjectResponse<>(appView);
    }

    /**
     * Updates a travel application with the provided meal allowances
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/meal-allowances
     * <p>
     */
    @RequestMapping(value = "/{id}/meal-allowances", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateMealAllowances(@PathVariable String id, @RequestBody MealAllowancesView mealAllowancesView) {
        TravelApplicationView appView = appDao.getUncompletedAppById(UUID.fromString(id));
        appView.setMealAllowance(mealAllowancesView);
        TravelApplication app = appView.toTravelApplication();
        appView = new TravelApplicationView(app);
        appView = appDao.saveUncompleteTravelApp(appView);
        return new ViewObjectResponse<>(appView);
    }

    /**
     * Updates a travel application with the provided lodging allowances
     * <p>
     * (PUT) /api/v1/travel/application/uncompleted/{id}/lodging-allowances
     * <p>
     */
    @RequestMapping(value = "/{id}/lodging-allowances", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateMealAllowances(@PathVariable String id, @RequestBody LodgingAllowancesView lodgingAllowancesView) {
        TravelApplicationView appView = appDao.getUncompletedAppById(UUID.fromString(id));
        appView.setLodgingAllowance(lodgingAllowancesView);
        TravelApplication app = appView.toTravelApplication();
        appView = new TravelApplicationView(app);
        appView = appDao.saveUncompleteTravelApp(appView);
        return new ViewObjectResponse<>(appView);
    }


    // TODO: Move attachment API's to their own controller? Would need to update purpose page logic.

    @ResponseBody
    @RequestMapping(value = "/{id}/attachment/{attachmentId}", method = RequestMethod.GET)
    public void getAttachment(@PathVariable int id, @PathVariable String attachmentId, HttpServletResponse response) throws IOException {
        File attachment = uploadProcessor.getAttachmentFile(attachmentId);
        if (!attachment.exists()) {
            response.sendError(404, "Cannot find file for attachment " + attachmentId);
            return;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(attachment);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            logger.error("Error fetching travel attachment " + attachmentId, e);
            response.sendError(500, e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Upload one or more files to attach to a application.
     */
    @RequestMapping(value = "/{id}/attachment", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse addAttachments(@PathVariable String id, @RequestParam("file") MultipartFile[] files) throws IOException {
        TravelApplicationView appView = appDao.getUncompletedAppById(UUID.fromString(id));
        TravelApplication app = appView.toTravelApplication();

        List<TravelAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            attachments.add(uploadProcessor.uploadTravelAttachment(file));
        }
        app.addAttachments(attachments);
        appView = new TravelApplicationView(app);
        appView = appDao.saveUncompleteTravelApp(appView);
        return new ViewObjectResponse<>(appView);
    }


    /**
     * Delete an attachment
     * @param id
     * @param attachmentId
     * @return
     */
    @RequestMapping(value = "/{id}/attachment/{attachmentId}", method = RequestMethod.DELETE)
    public BaseResponse deleteAttachment(@PathVariable String id, @PathVariable String attachmentId) {
        TravelApplicationView appView = appDao.getUncompletedAppById(UUID.fromString(id));
        TravelApplication app = appView.toTravelApplication();
        app.deleteAttachment(attachmentId);
        appView = new TravelApplicationView(app);
        appView = appDao.saveUncompleteTravelApp(appView);
        return new ViewObjectResponse<>(appView);
    }

    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }
}
