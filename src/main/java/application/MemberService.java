package application;

import domain.Member;
import persistence.MemberRepository;
import java.util.List;

/**
 * Service class that handles operations related to library members.
 *
 * <p>This class acts as an intermediary between the application logic
 * and the {@link MemberRepository}, providing high-level operations such as
 * registering new members, retrieving all registered members, and searching for
 * members by name.</p>
 */
public class MemberService {

    /**
     * Registers a new library member by adding them to the repository.
     *
     * <p>This method stores the given {@link Member} object in the
     * {@link MemberRepository} and displays a confirmation message.</p>
     *
     * @param member the member to register
     */
    public void registerMember(Member member) {
        MemberRepository.addMember(member);
        System.out.println("Member registered: " + member.getName());
    }

    /**
     * Retrieves all registered members from the repository.
     *
     * @return a list containing all members
     */
    public List<Member> getAllMembers() {
        return MemberRepository.findAll();
    }

    /**
     * Searches for a member in the repository by their name.
     *
     * @param name the name of the member to search for
     * @return the matching {@link Member} object if found, or {@code null} if no match exists
     */
    public Member findMember(String name) {
        return MemberRepository.findByName(name);
    }
}
