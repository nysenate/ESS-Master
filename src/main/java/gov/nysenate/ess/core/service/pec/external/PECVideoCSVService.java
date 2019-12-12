package gov.nysenate.ess.core.service.pec.external;

import com.google.common.collect.*;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.dao.personnel.rch.ResponsibilityHeadDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PECVideoCSVService {

    private static final Logger logger = LoggerFactory.getLogger(PECVideoCSVService.class);

    private final EmployeeDao employeeDao;
    private final PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private final EmployeeInfoService employeeInfoService;
    private final EmpTransactionService transactionService;
    private final ResponsibilityHeadDao rchDao;

    private final String csvFileDir;

    private static final Pattern csvEthicsReportPattern =
            Pattern.compile("ExportDocsEthics(\\d{4})_V(\\d)");

    private static final Pattern csvHarrassmentReportPattern =
            Pattern.compile("ExportDocsHarassment(\\d{4})_V(\\d)");

    private static final Pattern csvLiveTrainingDHPReportPattern =
            Pattern.compile("LiveTrainingDHP(\\d{4})");

    private final int ETHICS_VID_ID_NUM;
    private final int HARASSMENT_VID_ID_NUM;

    private static final DateTimeFormatter liveDTF = DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm");
    private static final DateTimeFormatter liveDTFAlt = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm");


    @Autowired
    public PECVideoCSVService(EmployeeDao employeeDao,
                              PersonnelTaskAssignmentDao personnelTaskAssignmentDao,
                              EmployeeInfoService employeeInfoService,
                              EmpTransactionService transactionService,
                              ResponsibilityHeadDao rchDao,
                              @Value("${data.dir:}") String dataDir,
                              @Value("${pec.import.harassment_video_task_id:}") int harrassmentVidId,
                              @Value("${pec.import.ethics_video_task_id:}") int ethicsVidId) {
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        this.employeeInfoService = employeeInfoService;
        this.transactionService = transactionService;
        this.rchDao = rchDao;
        this.csvFileDir = Paths.get(dataDir, "pec_csv_import").toString();
        this.ETHICS_VID_ID_NUM = ethicsVidId;
        this.HARASSMENT_VID_ID_NUM = harrassmentVidId;
    }

    public void processCSVReports() throws IOException {
        File[] files = getFilesFromDirectory(csvFileDir);

        for (File file : files) {
            String fileName = file.getName().replaceAll(".csv", "").replaceAll(".xlsx", "");
            Matcher ethicsMatcher = csvEthicsReportPattern.matcher(fileName);
            Matcher harassmentMatcher = csvHarrassmentReportPattern.matcher(fileName);
            Matcher liveTrainingDHPMatcher = csvLiveTrainingDHPReportPattern.matcher(fileName);

            if (ethicsMatcher.matches()) {
                logger.info("Processing Ethics CSV report");
                handleGeneratedReportCSV(file, ETHICS_VID_ID_NUM, 10); //IDS may change once we get the videos
            } else if (harassmentMatcher.matches()) {
                logger.info("Processing Harassment CSV report");
                handleGeneratedReportCSV(file, HARASSMENT_VID_ID_NUM, 11);
            } else if (liveTrainingDHPMatcher.matches()) {
                logger.info("Processing Live Training DHP CSV report");
                handleLiveTrainingReportCSV(file);
            }
        }
    }

    private void handleGeneratedReportCSV(File genRepCSV, int id, int dateColumnNumber) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(genRepCSV.getAbsolutePath()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            //0 Name
            //1 EmpID
            //2 Office
            //3 Location
            //4 Phone
            //5 Email
            //6 Training
            //7 Watched Video
            //8 EC1
            //9 EC2
            //10 Date 02/21/2018 11:27:18 EST
            if (csvRecord.get(0).equals("Name")) {
                continue;
            }

            try {
                int fileEmpId = Integer.parseInt(csvRecord.get(1));

                String dateFromFile = csvRecord.get(dateColumnNumber)
                        .replaceAll("EST", "").replaceAll("EDT", "").trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(dateFromFile, formatter);

                Employee employee = employeeDao.getEmployeeById(fileEmpId);

                updateDB(employee, dateTime, id);
            }
            catch (EmployeeNotFoundEx e) {
                logger.warn("Could not find employee in ESS.  empId: {}\trecord: {}", e.getEmpId(), csvRecord);
            }
            catch (NumberFormatException e) {
                logger.warn("CSV report has problematic data in the EmpID field in record: {}", csvRecord);
            }
        }
    }

    private void handleLiveTrainingReportCSV(File liveTrainingCSV) throws IOException {
        //this will handle the custom csv file when it comes in
        Reader reader = Files.newBufferedReader(Paths.get(liveTrainingCSV.getAbsolutePath()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
        int failedToMatchCount = 0;
        List<String> noMatchEmps = new ArrayList<>();
        List<String> multMatchEmps = new ArrayList<>();
        Set<String> unidentifiedOffices = new HashSet<>();
        Multimap<String, ResponsibilityHead> multiMatchOffices = HashMultimap.create();
        List<ResponsibilityHead> rchs = rchDao.rchSearch("", LimitOffset.ALL, SortOrder.NONE).getResults();
        for (CSVRecord csvRecord : csvParser) {
            //0 EMPLOYEE NAME
            //1 OFFICE
            //2 TRAINING DATE
            if (csvRecord.get(0).equals("EMPLOYEE NAME")) {
                continue;
            }
            String name = csvRecord.get(0);
            String office = csvRecord.get(1);
            String trainingDateTimeStr = csvRecord.get(2);

            LocalDateTime trainingDateTime = parseLiveTrainingDateTime(trainingDateTimeStr);

            EmployeeSearchBuilder employeeSearchBuilder = new EmployeeSearchBuilder();
            employeeSearchBuilder.setName(name.replaceAll("," , ""));
            List<Employee> potentialEmployees = employeeInfoService.searchEmployees(employeeSearchBuilder, LimitOffset.ALL).getResults();

            List<ResponsibilityHead> rchMatches = rchs.stream()
                    .filter(rch -> rchMatches(office, rch))
                    .collect(Collectors.toList());

            if (rchMatches.size() == 0) {
//                unidentifiedOffices.add(office);
            } else if (rchMatches.size() > 1) {
                multiMatchOffices.putAll(office, rchMatches);
            }

            // If there are multiple matches,
            // attempt to narrow down the list using the office field and more precise name matching.
            if (potentialEmployees.size() > 1) {
                logger.info("Multiple matches for name:\"{}\" office:\"{}\".  Attempting to narrow down results",
                        name, office);
                Map<Integer, Employee> pempmap = Maps.uniqueIndex(potentialEmployees, Employee::getEmployeeId);

                // Group employees using different matching techniques
                Set<Integer> firstLastMatches = potentialEmployees.stream()
                        .filter(emp -> exactEmpFirstLastNameMatch(name, emp))
                        .map(Employee::getEmployeeId)
                        .collect(Collectors.toSet());
                Set<Integer> fullNameMatches = potentialEmployees.stream()
                        .filter(emp -> exactEmpFullNameMatch(name, emp))
                        .map(Employee::getEmployeeId)
                        .collect(Collectors.toSet());
                Set<Integer> officeMatches = potentialEmployees.stream()
                        .filter(emp -> rchMatchesAtTime(rchMatches, trainingDateTime, emp))
                        .map(Employee::getEmployeeId)
                        .collect(Collectors.toSet());

                // Match sets and intersections of match sets in order of most to least reliable
                ImmutableMap<String, Set<Integer>> matchSets = ImmutableMap.<String, Set<Integer>>builder()
                        .put("Office", officeMatches)
                        .put("Office/fullName", Sets.intersection(officeMatches, fullNameMatches))
                        .put("Office/firstLast", Sets.intersection(officeMatches, firstLastMatches))
                        .put("fullName", fullNameMatches)
                        .put("firstLast", firstLastMatches)
                        .build();

                if (officeMatches.size() == 0) {
                    unidentifiedOffices.add(office);
                }

                for (Map.Entry<String, Set<Integer>> matchSet : matchSets.entrySet()) {
                    // If a match set has successfully narrowed it down to 1 emp, set that as new potential emps
                    if (matchSet.getValue().size() == 1) {
                        potentialEmployees = matchSet.getValue().stream()
                                .map(pempmap::get)
                                .collect(Collectors.toList());
                        logger.info("Matched to emp#{} using {}",
                                potentialEmployees.get(0).getEmployeeId(), matchSet.getKey());
                        break;
                    }
                }
            }

            if (potentialEmployees.size() == 1) {
                //Singular employee, insert the update to the db
                updateDB(potentialEmployees.get(0), trainingDateTime, HARASSMENT_VID_ID_NUM);
            }
            else if (potentialEmployees.size() == 0) {
                //WARN of an employee that does not exist / could not be found
                logger.warn("NO employees were found for the name \"{}\" in record: {}", name, csvRecord);
                failedToMatchCount++;
                noMatchEmps.add(name);
            }
            else {
                //Multiple potential employees were found.
                logger.warn("Multiple employees were found for the name \"{}\" in record: {}", name, csvRecord);
                failedToMatchCount++;
                multMatchEmps.add(name);
            }
        }
        logger.warn(failedToMatchCount + " live training records failed to match");

        logger.warn("No match emps:\n{}", String.join("\n", noMatchEmps));
        logger.warn("Multiple match emps:\n{}", String.join("\n", multMatchEmps));

        logger.warn("No match offices:\n{}", String.join("\n", unidentifiedOffices));
        logger.warn("Multimatch offices:\n{}",
                multiMatchOffices.asMap().entrySet().stream()
                        .map(ent -> ent.getKey() + ": " +
                                ent.getValue().stream().map(ResponsibilityHead::getCode).collect(Collectors.joining(" ")))
                        .collect(Collectors.joining("\n"))
        );
    }

    private void updateDB(Employee employee, LocalDateTime dateTime, int id) {
        PersonnelTaskAssignment taskToInsert = new PersonnelTaskAssignment(
                id,
                employee.getEmployeeId(),
                employee.getEmployeeId(),
                dateTime,
                true,
                true
        );
        personnelTaskAssignmentDao.updateAssignment(taskToInsert);
    }

    private File[] getFilesFromDirectory(String path) {
        return new File(path).listFiles();
    }

    private LocalDateTime parseLiveTrainingDateTime(String dateString) {
        try {
            return LocalDateTime.parse(dateString, liveDTF);
        }
        catch (DateTimeParseException e) {
            //This probably means the date is a single digit day. Throw exceptions from here
            return LocalDateTime.parse(dateString, liveDTFAlt);
        }
    }

    private String normalizeOffice(String office) {
        if (office == null) {
            return "";
        }
        return StringUtils.replacePattern(StringUtils.upperCase(office), "[^A-Z]", "");
    }

    private boolean officeEquals(String office, String otherOffice) {
        return StringUtils.equalsIgnoreCase(normalizeOffice(office), normalizeOffice(otherOffice));
    }

    private boolean rchMatches(String office, ResponsibilityHead rch) {
        return officeEquals(office, rch.getCode()) ||
                officeEquals(office, rch.getName()) ||
                officeEquals(office, rch.getShortName());
    }

    private boolean rchMatchesAtTime(List<ResponsibilityHead> rchMatches, LocalDateTime dateTime, Employee emp) {
        TransactionHistory transHistory = transactionService.getTransHistory(emp.getEmployeeId());
        // Get rch codes for the training day.
        Set<String> rchCodes = new HashSet<>(
                transHistory.getEffectiveEntriesDuring(
                        "CDRESPCTRHD", Range.singleton(dateTime.toLocalDate()), true)
                        .values()
        );

        // There should be at most one of these...
        if (rchCodes.size() > 1) {
            logger.warn("Multiple rch codes per day?? empId:{}, name:{}, codes:{}",
                    emp.getEmployeeId(), emp.getFullName(), rchCodes);
        }

        Set<String> matchCodes = rchMatches.stream().map(ResponsibilityHead::getCode).collect(Collectors.toSet());

        return Sets.intersection(rchCodes, matchCodes).size() > 0;
    }

    private String normalizeName(String name) {
        return StringUtils.replacePattern(StringUtils.upperCase(name), "[^A-Z ]", "");
    }

    private boolean nameMatches(String lhs, String rhs) {
        return StringUtils.equalsIgnoreCase(
                normalizeName(lhs),
                normalizeOffice(rhs)
        );
    }

    private boolean exactEmpFirstLastNameMatch(String inputName, Employee emp) {
        return nameMatches(
                inputName,
                emp.getLastName() + " " + emp.getFirstName()
        );
    }

    private boolean exactEmpFullNameMatch(String inputName, Employee emp) {
        return nameMatches(inputName, emp.getFullName());
    }
}
