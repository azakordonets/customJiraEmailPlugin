package com.biercoff.customJiraEmailPlugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by biercoff on 23/09/14.
 */
public class MailSender {
    private static final Logger log = LoggerFactory.getLogger(MailSender.class);

    public void sendEmail(String to, String subject, String body, String emailType) {
        SMTPMailServer mailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
        Email email = new Email(to);
        email.setFrom(mailServer.getDefaultFrom());
        email.setSubject(subject);
        email.setMimeType("text/html");
        email.setBody(body);
        SingleMailQueueItem item = new SingleMailQueueItem(email);
        ComponentAccessor.getMailQueue().addItem(item);
        log.debug(emailType + " email was added to the queue ");
    }
}
