import business.Session;
import business.Speaker;
import business.Speaker.*;
import business.WebBrowser;
import dataAccessLayer.SqlServerCompactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SpeakerTests {

    //Hard coding to single concrete implementation for simplicity here.
    private SqlServerCompactRepository repository = new SqlServerCompactRepository();

    @Test
    public void Register_EmptyFirstName_ThrowsArgumentNullException() {
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setFirstName("");

        ArgumentNullException exception =
            Assertions.assertThrows(ArgumentNullException.class, () -> speaker.register(repository));

        Assertions.assertEquals("First Name is required", exception.getMessage());
    }

    @Test

    public void Register_EmptyLastName_ThrowsArgumentNullException() {
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setLastName("");

        ArgumentNullException exception =
            Assertions.assertThrows(ArgumentNullException.class, () -> speaker.register(repository));

        Assertions.assertEquals("Last name is required", exception.getMessage());
    }

    @Test
    public void Register_EmptyEmail_ThrowsArgumentNullException() {

        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setEmail("");

        ArgumentNullException exception =
            Assertions.assertThrows(ArgumentNullException.class, () -> speaker.register(repository));

        Assertions.assertEquals("Email is required", exception.getMessage());
    }

    @Test

    public void Register_WorksForPrestigiousEmployerButHasRedFlags_ReturnsSpeakerId()
        throws SpeakerDoesntMeetRequirementsException, ArgumentNullException, NoSessionsApprovedException {
        //arrange
        Speaker speaker = GetSpeakerWithRedFlags();
        speaker.setEmployer("Microsoft");

        //act
        Integer speakerId = speaker.register(new SqlServerCompactRepository());

        //assert
        Assertions.assertNotNull(speakerId);
    }

    @Test

    public void Register_HasBlogButHasRedFlags_ReturnsSpeakerId()
        throws SpeakerDoesntMeetRequirementsException, ArgumentNullException, NoSessionsApprovedException {
        //arrange
        Speaker speaker = GetSpeakerWithRedFlags();

        Integer speakerId = speaker.register(new SqlServerCompactRepository());

        //assert
        Assertions.assertNotNull(speakerId);
    }

    @Test

    public void Register_HasCertificationsButHasRedFlags_ReturnsSpeakerId()
        throws SpeakerDoesntMeetRequirementsException, ArgumentNullException, NoSessionsApprovedException {
        //arrange
        Speaker speaker = GetSpeakerWithRedFlags();
        speaker.setCertifications(Arrays.asList("cert1", "cert2", "cert3", "cert4"));

        Integer speakerId = speaker.register(new SqlServerCompactRepository());

        //assert
        Assertions.assertNotNull(speakerId);
    }

    @Test

    public void Register_SingleSessionThatIsOnOldTech_ThrowsNoSessionsApprovedException() {
        //arrange
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setSessions(Collections.singletonList(new Session("Cobol for dummies", "Intro to Cobol")));

        Assertions.assertThrows(NoSessionsApprovedException.class, () -> speaker.register(repository));
    }

    @Test

    public void Register_NoSessionsPassed_ThrowsArgumentException() {
        //arrange
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setSessions(Collections.emptyList());

        ArgumentNullException exception =
            Assertions.assertThrows(ArgumentNullException.class, () -> speaker.register(repository));

        Assertions.assertEquals("Can't register speaker with no sessions to present", exception.getMessage());
    }

    @Test

    public void Register_DoesntAppearExceptionalAndUsingOldBrowser_ThrowsNoSessionsApprovedException() {
        //arrange
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setHasBlog(false);
        speaker.setBrowser(new WebBrowser("IE", 6));

        Assertions.assertThrows(SpeakerDoesntMeetRequirementsException.class, () -> speaker.register(repository));
    }

    @Test

    public void Register_DoesntAppearExceptionalAndHasAncientEmail_ThrowsNoSessionsApprovedException() {
        //arrange
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setHasBlog(false);
        speaker.setEmail("name@aol.com");

        Assertions.assertThrows(SpeakerDoesntMeetRequirementsException.class, () -> speaker.register(repository));
    }

    //region Helpers

    private Speaker GetSpeakerThatWouldBeApproved() {

        Speaker speaker = new Speaker();

        speaker.setFirstName("First");
        speaker.setLastName("Last");
        speaker.setEmail("example@domain.com");
        speaker.setEmployer("Example Employer");
        speaker.setHasBlog(true);
        speaker.setBrowser(new WebBrowser("test", 1));

        speaker.setExperienceYears(1);
        speaker.setCertifications(new ArrayList<>());
        speaker.setBlogURL("");
        speaker.setSessions(Collections.singletonList(new Session("test title", "test description")));
        return speaker;
    }

    private Speaker GetSpeakerWithRedFlags() {
        Speaker speaker = GetSpeakerThatWouldBeApproved();
        speaker.setEmail("tom@aol.com");
        speaker.setBrowser(new WebBrowser("IE", 6));
        return speaker;
    }
    //endregion
}