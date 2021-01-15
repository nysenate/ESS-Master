package gov.nysenate.ess.travel.notifications.email;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.travel.application.TravelApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TravelAppApprovalEmail {

    private SendMailService sendMailService;
    private Configuration freemarkerCfg;
    private static final String template = "travel_app_approval_notice.ftlh";

    @Autowired
    public TravelAppApprovalEmail(SendMailService sendMailService, Configuration freemarkerCfg) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
    }

    public MimeMessage createEmail(TravelAppEmailView view, Employee toEmployee) {
        String subject = generateSubject(view);
        String body = generateBody(view, toEmployee);
        return sendMailService.newHtmlMessage(toEmployee.getEmail(), subject, body);
    }

    private String generateSubject(TravelAppEmailView view) {
        return "Approved Travel Application for " + view.getTravelerFullName() +
                " on " + view.getDatesOfTravel();
    }

    private String generateBody(TravelAppEmailView view, Employee recipient) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.builder()
                .put("view", view)
                .put("recipient", recipient)
                .build();
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(template);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

}
