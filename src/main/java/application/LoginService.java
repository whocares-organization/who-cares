package application;

import domain.Admin;
import domain.Member;
import domain.Person;

/**
 * Authentication facade for admins and members.
 *
 * <p>Delegates credential checks to {@link AdminService} and {@link MemberService}
 * and returns the authenticated {@link Person} or {@code null}.</p>
 */
public class LoginService {
	private AdminService adminService;
	private MemberService memberService;
	
	/**
	 * Creates a new {@code LoginService} which delegates to the provided services.
	 *
	 * @param adminService the admin service used for admin authentication
	 * @param memberService the member service used for member authentication
	 */
	public LoginService(AdminService adminService, MemberService memberService) {
		this.adminService = adminService;
		this.memberService = memberService;
	}
	
	/**
	 * Attempts to authenticate an admin.
	 * @param username admin username/email
	 * @param password plain password
	 * @return the authenticated Admin or null if credentials invalid
	 */
	public Person loginAdmin(String username, String password) {
		boolean isAdmin = adminService.login(username, password);
		if (!isAdmin) {
			return null;
		}
		return adminService.findAdminByEmail(username);
	}
	
	/**
	 * Attempts to authenticate a member.
	 * @param username member username/email
	 * @param password plain password
	 * @return the authenticated Member or null if credentials invalid
	 */
	public Person loginMember(String username, String password) {
		boolean isMember = memberService.login(username, password);
		if (!isMember) {
			return null;
		}
		return memberService.findMemberByEmail(username);
	}
}