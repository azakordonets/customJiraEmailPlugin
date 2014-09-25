package com.biercoff.customJiraEmailPlugin;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by biercoff on 23/09/14.
 */
public class EmailTriggers {
    private static final Logger log = LoggerFactory.getLogger(CustomFieldListener.class);
    private MailSender mail;
    private Properties props;

    public EmailTriggers() {
        mail = new MailSender();
        loadProperties();
    }

    public void checkCustomFieldOnCreate(CustomFieldManager customFieldManager, Issue issue) {
        CustomField customField = customFieldManager.getCustomFieldObjectByName("{Custom field Name}");
        String value = customField.getValue(issue).toString();
        if (value.equals("?")) {
            log.debug("Custom field is needed");
            String subject = "Custom field is required for " + issue.getKey();
            String body = getMessageBody(issue);
            mail.sendEmail(props.getProperty("custom.field.emails"), subject, body, "{Custom Field change}");
        }
    }

    public void checkCustomFieldOnUpdate(IssueEvent issueEvent, Issue issue) throws GenericEntityException {
        if (fieldWasChanged("${custom field name}}", issueEvent, "${value that will trigger email}}")) {
            log.debug("Field was changed");
            String subject = "${Field name} is might be needed for " + issue.getKey();
            String body = getMessageBody(issue);
            mail.sendEmail(props.getProperty("custom.field.emails"), subject, body, "{custom field change}");
        }
    }


    protected boolean fieldWasChanged(String fieldName, IssueEvent issueEvent, String fieldValue) throws GenericEntityException {
        boolean result = false;

        List<GenericValue> changeItemList = issueEvent.getChangeLog().getRelated("ChildChangeItem");

        Iterator<GenericValue> changeItemListIterator = changeItemList.iterator();
        while (changeItemListIterator.hasNext()) {
            GenericValue changeItem = (GenericValue) changeItemListIterator.next();
            String currentFieldName = changeItem.get("field").toString();
            if (currentFieldName.equals(fieldName)) // Name of custom field.
            {
                Object oldValue = changeItem.get("oldstring");
                Object newValue = changeItem.get("newstring");
                if (oldValue!= null && newValue != null){
                    if (!oldValue.equals(newValue) && newValue.equals(fieldValue)) result = true;
                    break;
                }else if (oldValue == null && newValue != null) {
                    if (newValue.equals(fieldValue)) result = true;
                    break;
                }
            }
        }
        return result;
    }


    protected String getMessageBody(Issue issue) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<body>\n" +
                "<h1>Issue details</h1>\n" +
                "<li>Summary:\t " + issue.getSummary() + "</li>\n" +
                "<li>URL:\t http://localhost:2990/jira/browse/" + issue.getKey() + "</li>\n" +
                "<li>Assignee:\t " + issue.getAssignee() + "</li>\n" +
                "<li>Reporter:\t " + issue.getReporter() + "</li>\n" +
                "<li>Description:\n</li> " + issue.getDescription() + "\n" +
                "</body>\n" +
                "</html>\n";
    }


    protected void loadProperties()  {
        props = new Properties();
        try {
            InputStream input = EmailTriggers.class.getResourceAsStream("/notification.properties");
            props.load(input);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
