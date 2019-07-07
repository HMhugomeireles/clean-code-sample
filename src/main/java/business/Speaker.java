package business;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;

/// <summary>
/// Represents a single speaker
/// </summary>
public class Speaker {
    private String firstName;

    private String lastName;

    private String email;

    private int experienceYears;

    private boolean hasBlog;

    private WebBrowser browser;

    private List<String> certifications;

    private String employer;

    private List<business.Session> sessions;

    //region getters and setters

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setHasBlog(boolean hasBlog) {
        this.hasBlog = hasBlog;
    }

    public void setBrowser(WebBrowser browser) {
        this.browser = browser;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    //end region

    /// <summary>
    /// register a speaker
    /// </summary>
    /// <returns>speakerID</returns>
    public int register(IRepository repository) throws NoSessionsApprovedException, SpeakerDoesntMeetRequirementsException, ArgumentNullException {
        this.validateRegistration();

        //if we got this far, the speaker has been accepted and is registered.
        return repository.saveSpeaker(this);
    }

    private void validateRegistration() throws ArgumentNullException, SpeakerDoesntMeetRequirementsException, NoSessionsApprovedException {
        this.validatePersonalData();

        if (sessions.isEmpty()) {
            throw new ArgumentNullException("Can't register speaker with no sessions to present");
        }

        boolean appearsExceptional = this.appearsExceptional();
        boolean hasRedFlags = this.hasRedFlags();

        boolean isQualified = appearsExceptional || !hasRedFlags;

        if (!isQualified) {
            throw new SpeakerDoesntMeetRequirementsException("Speaker doesn't meet our standards");
        }

        this.sessionsApproved();
    }

    private void sessionsApproved() throws NoSessionsApprovedException {
        for (Session session : sessions) {
            boolean sessionAboutOldTechnology = isSessionAboutOldTechnology(session);
            session.setApproved(!sessionAboutOldTechnology);
        }

        boolean anySessionApproved = sessions.stream().anyMatch(Session::isApproved);

        if (!anySessionApproved) {
            throw new NoSessionsApprovedException("No sessions approved");
        }

    }

    private boolean isSessionAboutOldTechnology(Session session) {
        List<String> oldTechnologies = Arrays.asList("Cobol", "Punch Cards", "Commodore", "VBScript");
        return oldTechnologies.stream().anyMatch(tech -> session.getTitle().contains(tech) || session.getDescription().contains(tech));
    }

    private boolean hasRedFlags() {
        //need to get just the domain from the email
        String emailDomain = email.split("@")[1];

        List<String> ancientDomains = Arrays.asList("aol.com", "hotmail.com", "prodigy.com", "CompuServe.com");
        boolean isEmailDomainAncient = ancientDomains.contains(emailDomain);

        boolean isBrowserOlderThanIe9 = browser.name == WebBrowser.BrowserName.INTERNET_EXPLORER && browser.majorVersion < 9;

        return isEmailDomainAncient || isBrowserOlderThanIe9;
    }

    private boolean appearsExceptional() {
        if (experienceYears > 10) {
            return true;
        }

        if (hasBlog) {
            return true;
        }

        if (certifications.size() > 3) {
            return true;
        }

        List<String> preferredEmployers = Arrays.asList("Microsoft", "Google", "Fog Creek Software", "37Signals");

        return preferredEmployers.contains(employer);
    }

    private void validatePersonalData() throws ArgumentNullException {
        if (Strings.isNullOrEmpty(firstName)) {
            throw new ArgumentNullException("First Name is required");
        }
        if (Strings.isNullOrEmpty(lastName)) {
            throw new ArgumentNullException("Last name is required");
        }
        if (Strings.isNullOrEmpty(email)) {
            throw new ArgumentNullException("Email is required");
        }
    }

    //region Custom Exceptions
    public class SpeakerDoesntMeetRequirementsException extends Exception {
        SpeakerDoesntMeetRequirementsException(String message) {
            super(message);
        }
    }


    public class NoSessionsApprovedException extends Exception {
        NoSessionsApprovedException(String message) {
            super(message);
        }
    }


    public class ArgumentNullException extends Exception {
        ArgumentNullException(String message) {
            super(message);
        }
    }
    //endregion
}