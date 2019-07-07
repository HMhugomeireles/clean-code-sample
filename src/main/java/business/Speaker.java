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

    private String blogURL;

    private WebBrowser browser;

    private List<String> certifications;

    private String employer;

    private int registrationFee;

    private List<business.Session> sessions;

    //region getters and setters

    public Speaker setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public Speaker setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Speaker setEmail(String email) {
        this.email = email;
        return this;
    }

    public Speaker setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
        return this;
    }

    public Speaker setHasBlog(boolean hasBlog) {
        this.hasBlog = hasBlog;
        return this;
    }

    public Speaker setBlogURL(String blogURL) {
        this.blogURL = blogURL;
        return this;
    }

    public Speaker setBrowser(WebBrowser browser) {
        this.browser = browser;
        return this;
    }

    public Speaker setCertifications(List<String> certifications) {
        this.certifications = certifications;
        return this;
    }

    public Speaker setEmployer(String employer) {
        this.employer = employer;
        return this;
    }

    public Speaker setSessions(List<Session> sessions) {
        this.sessions = sessions;
        return this;
    }

    //end region

    /// <summary>
    /// register a speaker
    /// </summary>
    /// <returns>speakerID</returns>
    public int register(IRepository repository) throws NoSessionsApprovedException, SpeakerDoesntMeetRequirementsException, ArgumentNullException {
        //lets init some vars
        Integer speakerId = null;

        this.validateData();

        boolean appearsExceptional = this.appearsExceptional();
        boolean hasRedFlags = this.hasRedFlags();

        if (!appearsExceptional || hasRedFlags) {
            throw new SpeakerDoesntMeetRequirementsException("Speaker doesn't meet our arbitrary and capricious standards");
        }

        if (sessions.size() == 0) {
            throw new ArgumentNullException("Can't register speaker with no sessions to present");
        }

        boolean approved = this.anySessionsApproved();

        if (approved) {

            //if we got this far, the speaker is approved
            //let's go ahead and register him/her now.
            //First, let's calculate the registration fee.
            //More experienced speakers pay a lower fee.
            if (experienceYears <= 1) {
                registrationFee = 500;
            } else if (experienceYears >= 2 && experienceYears <= 3) {
                registrationFee = 250;
            } else if (experienceYears >= 4 && experienceYears <= 5) {
                registrationFee = 100;
            } else if (experienceYears >= 6 && experienceYears <= 9) {
                registrationFee = 50;
            } else {
                registrationFee = 0;
            }

            //Now, save the speaker and sessions to the db.
            try {
                speakerId = repository.saveSpeaker(this);
            } catch (Exception e) {
                //in case the db call fails
            }
        } else {
            throw new NoSessionsApprovedException("No sessions approved");
        }

        //if we got this far, the speaker is registered.
        return speakerId;
    }

    private boolean anySessionsApproved() {
        for (Session session : sessions) {
            boolean sessionAboutOldTechnology = isSessionAboutOldTechnology(session);
            session.setApproved(sessionAboutOldTechnology);
        }

        return sessions.stream().anyMatch(Session::isApproved);
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

        boolean isBrowserOlderThanIe9 = browser.name == WebBrowser.BrowserName.InternetExplorer && browser.majorVersion < 9;

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

    private void validateData() throws ArgumentNullException {
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
        public SpeakerDoesntMeetRequirementsException(String message) {
            super(message);
        }
    }


    public class NoSessionsApprovedException extends Exception {
        public NoSessionsApprovedException(String message) {
            super(message);
        }
    }


    public class ArgumentNullException extends Exception {
        public ArgumentNullException(String message) {
            super(message);
        }
    }
    //endregion
}