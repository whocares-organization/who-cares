package application;

import domain.Member;
import domain.UserStatus;
import persistence.MemberRepository;

import java.util.logging.Logger;

/**
 * Provides services and business logic related to library members.
 *
 * <p>Connects the presentation layer with the data layer ({@link persistence.MemberRepository})
 * and handles operations such as member registration, authentication, removal, and retrieval.</p>
 */
public class MemberService {

    private MemberRepository repository;
    private static final Logger LOGGER = Logger.getLogger(MemberService.class.getName());
 private static final String MEMBER_PREFIX = "Member '";
    /**
     * Creates a {@code MemberService} with no repository configured.
     */
    public MemberService() { }

    /**
     * Creates a {@code MemberService} with the given repository.
     *
     * @param repository the repository to use
     */
    public MemberService(persistence.MemberRepository repository) { this.repository = repository; }

    /**
     * Returns the underlying repository.
     *
     * @return the repository instance
     */
    public persistence.MemberRepository getRepository() { return repository; }

    /**
     * Sets the repository used by this service.
     *
     * @param repository the repository to set
     */
    public void setRepository(persistence.MemberRepository repository) { this.repository = repository; }

    /**
     * Registers a new member.
     *
     * @param member the member to register
     * @return {@code true} on success, {@code false} if already exists, {@code null} on invalid input
     */
    public Boolean registerMember(domain.Member member) {
        if (member == null) {
            LOGGER.warning("Cannot register null member");
            return null;
        }
        
        if (member.getUserName() == null || member.getUserName().isBlank()) {
            LOGGER.warning("Cannot register member with empty username");
            return null;
        }

        if (member.getPassword() == null || member.getPassword().isBlank()) {
            LOGGER.warning("Cannot register member with empty password");
            return null;
        }
       
        Member existing = repository.findMemberByEmail(member.getUserName());
        if (existing != null) {
            LOGGER.warning("Member with username '" + member.getUserName() + "' already exists");
            return false;
        }
      
        repository.addMember(member);
       LOGGER.info(MEMBER_PREFIX + member.getUserName() + "' registered successfully");
        return true;
    }

    /**
     * Removes an existing member.
     *
     * @param member the member to remove
     * @return {@code true} on success, {@code false} if not found, {@code null} on invalid input
     */
    public Boolean removeMember(domain.Member member) {
        if (member == null) {
            LOGGER.warning("Cannot remove null member");
            return null;
        }

        Member existing = repository.findMemberByEmail(member.getUserName());
        if (existing == null) {
            LOGGER.warning("Cannot remove non-existing member '" + member.getUserName() + "'");
            return false;
        }

        repository.removeMember(member);
        LOGGER.info("Member '" + member.getUserName() + "' removed successfully");
        return true;
    }

    /**
     * Finds a member by email/username.
     *
     * @param userName the email/username
     * @return the member if found; otherwise {@code null}
     */
    public domain.Member findMemberByEmail(String userName) {
        if (userName == null || userName.isBlank()) {
            LOGGER.warning("Cannot search for null or empty name");
            return null;
        }
        return repository.findMemberByEmail(userName);
    }

    /**
     * Retrieves all members.
     *
     * @return list of all members
     */
    public java.util.List<domain.Member> getAllMembers() {
        return repository.findAll();
    }
    
    /**
     * Unregisters (removes) a member by ID.
     *
     * @param memberId the member identifier
     * @return {@code true} on success, {@code false} if not found, {@code null} on invalid input
     */
    public Boolean unregisterMember(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            LOGGER.warning("Cannot unregister null/empty member ID");
            return null;
        }

        Member existing = repository.findById(memberId);

        if (existing == null) {
            LOGGER.warning("Member with ID '" + memberId + "' not found");
            return false;
        }

        repository.removeMember(existing);
        LOGGER.info("Member '" + memberId + "' removed successfully");
        return true;
    }

    /**
     * Finds a member by ID.
     *
     * @param memberId the member identifier
     * @return the member if found; otherwise {@code null}
     */
    public domain.Member findMemberById(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            LOGGER.warning("Cannot search for null/empty ID");
            return null;
        }

        return repository.findById(memberId);
    }


    /**
     * Attempts to log in a member.
     *
     * @param userName the member username/email
     * @param password the member password
     * @return {@code true} if authenticated; {@code false} otherwise
     */
    public boolean login(String userName, String password) {
        Member member = repository.findMemberByEmail(userName);

        if (member == null) {
            LOGGER.warning("Login failed: member '" + userName + "' not found");
            return false;
        }

        if (!member.checkPassword(password)) {
            LOGGER.warning("Login failed: wrong password for member '" + userName + "'");
            return false;
        }

        LOGGER.info("Login successful for member '" + userName + "'");
        member.setStatus(UserStatus.ONLINE);
        return true;
    }

    /**
     * Logs out a member.
     *
     * @param userName the member username/email
     * @return {@code true} if the member transitioned to OFFLINE; {@code false} otherwise; {@code null} on invalid input
     */
    public Boolean logout(String userName) {
    	
    	if (userName == null || userName.isBlank()) {
			LOGGER.warning("Cannot logout with null or empty username");
			return null;
		}
    	
        Member member = repository.findMemberByEmail(userName);

        if (member == null) {
            LOGGER.warning("Logout failed: member '" + userName + "' not found");
            return false;
        }
        
        if (member.getStatus() != UserStatus.ONLINE) {
            LOGGER.info("User '" + userName + "' is already offline");
            return false;
        }

        LOGGER.info("Logout successful for member '" + userName + "'");
        member.setStatus(UserStatus.OFFLINE);
        return true;
    }

    

}
