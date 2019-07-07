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

    private int exp;

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

    public Speaker setExp(int exp) {
        this.exp = exp;
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

        validateData();

        List<String> preferredEmployers = Arrays.asList("Microsoft", "Google", "Fog Creek Software", "37Signals");

        boolean speakerAppearsExceptional = ((exp > 10 || hasBlog || certifications.size() > 3 || preferredEmployers.contains(employer)));

        if (!speakerAppearsExceptional) {
            //need to get just the domain from the email
            String emailDomain = email.split("@")[1];

            List<String> domains = Arrays.asList("aol.com", "hotmail.com", "prodigy.com", "CompuServe.com");
            if (!domains.contains(emailDomain) && (!(browser.name == WebBrowser.BrowserName.InternetExplorer
                && browser.majorVersion < 9))) {
                speakerAppearsExceptional = true;
            }
        }

        if (speakerAppearsExceptional) {
            boolean appr = false;
            if (sessions.size() != 0) {
                List<String> ot = Arrays.asList("Cobol", "Punch Cards", "Commodore", "VBScript");
                for (Session session : sessions) {

                    for (String tech : ot) {

                        if (session.title.contains(tech) || session.description.contains(tech)) {
                            session.approved = false;
                            break;
                        } else {
                            session.approved = true;
                            appr = true;
                        }
                    }
                }
            } else {
                throw new ArgumentNullException("Can't register speaker with no sessions to present");
            }

            if (appr) {

                //if we got this far, the speaker is approved
                //let's go ahead and register him/her now.
                //First, let's calculate the registration fee.
                //More experienced speakers pay a lower fee.
                if (exp <= 1) {
                    registrationFee = 500;
                } else if (exp >= 2 && exp <= 3) {
                    registrationFee = 250;
                } else if (exp >= 4 && exp <= 5) {
                    registrationFee = 100;
                } else if (exp >= 6 && exp <= 9) {
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
        } else {
            throw new SpeakerDoesntMeetRequirementsException("Speaker doesn't meet our arbitrary and capricious standards");
        }

        //if we got this far, the speaker is registered.
        return speakerId;
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