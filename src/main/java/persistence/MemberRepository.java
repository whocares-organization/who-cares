package persistence;

import domain.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory repository for {@link Member} entities.
 *
 * <p>Provides basic operations for storing and looking up members.</p>
 */
public class MemberRepository {

    /** In-memory storage for all registered members. */
    private static ArrayList<Member> members = new ArrayList<>();

    /**
     * Creates a new {@code MemberRepository} with empty storage.
     */
    public MemberRepository() { }

    /**
     * Adds a new member to the repository if not already present.
     *
     * @param member the {@link Member} object to add
     */
    public static void addMember(Member member) {
    	members.add(member);
    }

    /**
     * Retrieves a list of all registered members.
     *
     * @return a copy of the list of all members
     */
    public static List<Member> findAll() {
        return new ArrayList<>(members);
    }

    /**
     * Finds a member by userName/ Email (case-insensitive).
     *
     * @param userName the member's userName
     * @return the matching {@link Member} or null if not found
     */
    public static Member findMemberByEmail(String userName) {
        return members.stream()
                .filter(m -> m.getUserName().equalsIgnoreCase(userName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a member by their unique ID.
     *
     * @param memberId the member's unique ID
     * @return the matching {@link Member} or null if not found
     */
    public static Member findById(String memberId) {
        return members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes a member from the repository.
     *
     * @param member the {@link Member} to remove
     * @return true if successfully removed, false otherwise
     */
    public static boolean removeMember(Member member) {
        return members.remove(member);
    }

    /**
     * Clears all members from the repository (useful for testing).
     */
    public static void clearMembers() {
        members.clear();
    }
}