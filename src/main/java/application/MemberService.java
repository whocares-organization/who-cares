package application;

import domain.Member;
import persistence.MemberRepository;

import java.util.List;
import java.util.logging.Logger;

/**
 * Provides services and business logic related to library members.
 *
 * <p>This class connects the presentation layer with the data layer ({@link MemberRepository})
 * and handles operations such as member registration, authentication, removal, and retrieval.</p>
 */
public class MemberService {

    private MemberRepository repository;
    private static final Logger LOGGER = Logger.getLogger(MemberService.class.getName());

    public MemberService() {
    }

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public MemberRepository getRepository() {
        return repository;
    }

    public void setRepository(MemberRepository repository) {
        this.repository = repository;
    }

    // ===========================================================
    // Register Member
    // ===========================================================
    public Boolean registerMember(Member member) {
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
        LOGGER.info("Member '" + member.getUserName() + "' registered successfully");
        return true;
    }

    // ===========================================================
    // Remove Member
    // ===========================================================
    public Boolean removeMember(Member member) {
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

    // ===========================================================
    // Search / Get Members
    // ===========================================================
    public Member findMemberByEmail(String userName) {
        if (userName == null || userName.isBlank()) {
            LOGGER.warning("Cannot search for null or empty name");
            return null;
        }
        return repository.findMemberByEmail(userName);
    }

    public List<Member> getAllMembers() {
        return repository.findAll();
    }

    // ===========================================================
    // Login / Logout Logic (without status)
    // ===========================================================
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
        return true;
    }

    public boolean logout(String userName) {
        Member member = repository.findMemberByEmail(userName);

        if (member == null) {
            LOGGER.warning("Logout failed: member '" + userName + "' not found");
            return false;
        }

        LOGGER.info("Logout successful for member '" + userName + "'");
        return true;
    }
}
