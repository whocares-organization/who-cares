package persistence;

import domain.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing {@link Member} entities in memory.
 *
 * <p>This repository provides CRUD-like operations for members,
 * including adding, finding, removing, and clearing records.</p>
 */
public class MemberRepository {

    /** In-memory storage for all registered members. */
    private static ArrayList<Member> members = new ArrayList<>();

    /**
     * Adds a new member to the repository if not already present.
     *
     * @param member the {@link Member} object to add
     */
    public static void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        boolean exists = members.stream()
                .anyMatch(m -> m.getMemberId().equals(member.getMemberId()) 
                            || m.getName().equalsIgnoreCase(member.getName()));
        if (!exists) {
            members.add(member);
        } else {
            System.out.println("Member already exists: " + member.getName());
        }
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
     * Finds a member by name (case-insensitive).
     *
     * @param name the member's name
     * @return the matching {@link Member} or null if not found
     */
    public static Member findByName(String name) {
        return members.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
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
                .filter(m -> m.getMemberId().equals(memberId))
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
    public static void clear() {
        members.clear();
    }
}
