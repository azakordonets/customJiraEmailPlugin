package com.biercoff.customJiraEmailPlugin;

/**
 * Created by biercoff on 17/09/14.
 */

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


public class CustomFieldListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(CustomFieldListener.class);
    private final CustomFieldManager customFieldManager;
    private final EventPublisher eventPublisher;
    private final MailSender mail;
    private final EmailTriggers triggers;


    /**
     * Constructor.
     * @param customFieldManager
     * @param eventPublisher injected {@code EventPublisher} implementation.
     */
    public CustomFieldListener(CustomFieldManager customFieldManager, EventPublisher eventPublisher) {
        this.customFieldManager = customFieldManager;
        this.eventPublisher = eventPublisher;
        triggers = new EmailTriggers();
        mail = new MailSender();
    }

    /**
     * Called when the plugin has been enabled.
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // register ourselves with the EventPublisher
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        // unregister ourselves with the EventPublisher
        eventPublisher.unregister(this);
    }


    /**
     * Receives any {@code IssueEvent}s sent by JIRA.
     * @param issueEvent the IssueEvent passed to us
     */
    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) throws GenericEntityException {
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();

        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            triggers.checkCustomFieldOnCreate(customFieldManager, issue);
        } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            triggers.checkCustomFieldOnUpdate(issueEvent, issue);
        }
    }

}
