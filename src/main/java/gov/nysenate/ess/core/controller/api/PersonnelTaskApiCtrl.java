package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.service.pec.MoodleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.time.LocalDateTime;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.MOODLE_COURSE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task")
public class PersonnelTaskApiCtrl {

    private MoodleRecordService moodleRecordService;
    private PersonnelAssignedTaskDao personnelAssignedTaskDao;

    @Autowired
    public PersonnelTaskApiCtrl(MoodleRecordService moodleRecordService,
                                PersonnelAssignedTaskDao personnelAssignedTaskDao) {
        this.moodleRecordService = moodleRecordService;
        this.personnelAssignedTaskDao = personnelAssignedTaskDao;
    }

    /**
     * Personnel Employee Task - Moodle callback
     * ----------------------------------
     *
     * Api Call used by moodle to
     *
     * Usage:
     * (POST)    /api/v1/personnel/task/moodle/receive
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/moodle/receive", method = {POST})
    public void receiveMoodleCallback(HttpServletRequest request) throws IOException {
        JsonNode json = moodleRecordService.convertStreamtoJson(request.getInputStream());
        moodleRecordService.processMoodleEmployeeRecords(moodleRecordService.getMoodleRecordsFromJson(json.toString()));
    }


    /**
     * Personnel Employee Task - Moodle Generation
     * ----------------------------------
     *
     * Returns a list of all years that contain an ack doc regardless of its active status.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/moodle/generate
     *
     * @Param from, the beginning of the date range needed for the records
     * @Param to, the end of the date range needed for the records
     *
     * Organization is always senate
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/moodle/generate", method = {GET})
    public void generateMoodleReport(HttpServletRequest request,
                                     @RequestParam LocalDateTime from,
                                     @RequestParam LocalDateTime to,
                                     @RequestParam String organization) throws IOException {

        //Change specifics of moodle api call once it is available
        JsonNode json = moodleRecordService.contactMoodleForNewRecords(from, to, organization);
        moodleRecordService.processMoodleEmployeeRecords(moodleRecordService.getMoodleRecordsFromJson(json.toString()));
    }


    //personnell add / update a record to the db

    /**
     * Personnel Employee Task - Moodle Personnel Update
     * ----------------------------------
     *
     * Returns a list of all years that contain an ack doc regardless of its active status.
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/moodle/generate
     *
     * @Param from, the beginning of the date range needed for the records
     * @Param to, the end of the date range needed for the records
     *
     * Organization is always senate
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/moodle/update", method = {POST})
    public void personnelUpdateMoodleRecord(HttpServletRequest request) throws IOException {
        JsonNode json = moodleRecordService.convertStreamtoJson(request.getInputStream());

        boolean valid = moodleRecordService.verifyMoodleUpdateJson(json);

        if (valid) {
            PersonnelTaskId personnelTaskId = new PersonnelTaskId(MOODLE_COURSE, 1);
            LocalDateTime updateTime = LocalDateTime.now();
            PersonnelAssignedTask personnelAssignedTask = new PersonnelAssignedTask(json.get("empId").asInt(),
                    personnelTaskId, updateTime, json.get("updateEmpId").asInt(), json.get("completed").asBoolean());
            personnelAssignedTaskDao.updatePersonnelAssignedTask(personnelAssignedTask);
        }

    }

}
