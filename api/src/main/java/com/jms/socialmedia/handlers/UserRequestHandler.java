package com.jms.socialmedia.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.google.gson.Gson;
import com.jms.socialmedia.dataservice.DataService;
import com.jms.socialmedia.exception.BadRequestException;
import com.jms.socialmedia.exception.DatabaseInsertException;
import com.jms.socialmedia.exception.FailedLoginAttemptException;
import com.jms.socialmedia.exception.InvalidUserLoginStateException;
import com.jms.socialmedia.exception.NotFoundException;
import com.jms.socialmedia.model.ChangePassword;
import com.jms.socialmedia.model.LoginRequest;
import com.jms.socialmedia.model.LoginSuccess;
import com.jms.socialmedia.model.NewUser;
import com.jms.socialmedia.model.User;
import com.jms.socialmedia.model.UserPage;
import com.jms.socialmedia.password.PasswordService;
import com.jms.socialmedia.token.Permission;
import com.jms.socialmedia.token.Token;
import com.jms.socialmedia.token.TokenService;

import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

public class UserRequestHandler extends RequestHandler {

	private static final String SESSION_COOKIE = "jms-social-media-session";

	private final PasswordService passwordService;
	private final Collection<Integer> adminUserIds;

	public UserRequestHandler(DataService dataService, PasswordService passwordService, TokenService tokenService,
			Collection<Integer> adminUserIds, Gson gson) {

		super(dataService, tokenService, gson);
		this.passwordService = passwordService;
		this.adminUserIds = adminUserIds;
	}

	public UserPage handleGetUserPage(Request request, Response response) {
		String username = request.params("username");
		UserPage userPage = dataService.getUserPageInfoByName(username);
		if (userPage != null) {
			userPage.addFollowersUsernames(dataService.getFollowerUsernames(username));
			userPage.addFollowingUsernames(dataService.getFollowingUsernames(username));
			return userPage;
		} else {
			throw new NotFoundException("User not found");
		}
	}

	public Boolean handleIsUsernameTaken(Request request, Response response) {
		String username = request.params("username");
		if (!username.matches("^[\\w\\d_]+$")) {
			throw new BadRequestException("Invalid Username");
		}
		return dataService.isUsernameTaken(username);
	}

	public Boolean handleIsEmailTaken(Request request, Response response) {
		String email = request.params("email");
		if (!email.matches("^[\\w\\d_.+\\-]+@[\\w\\d_.+\\-]+\\.[\\w\\d_.+\\-]+$")) {
			throw new BadRequestException("Invalid Email Address");
		}
		return dataService.isEmailTaken(email);
	}

	public LoginSuccess handleAddUser(Request request, Response response) throws IOException {
		NewUser newUser = extractBodyContent(request, NewUser.class);
		validateNewUserRequest(newUser);

		throwBadRequestExceptionIf(dataService.isUsernameTaken(newUser.getUsername()), "Username is taken");
		throwBadRequestExceptionIf(dataService.isEmailTaken(newUser.getEmail()), "Email is taken");

		int len = newUser.getPassword1().length();
		throwBadRequestExceptionIf(len < 5, "Password Too Short");
		throwBadRequestExceptionIf(len > 64, "Password Too Long");
		throwBadRequestExceptionIf(!newUser.passwordsMatch(), "Passwords do not match");

		createHashedPassword(newUser);
		if (!dataService.addUser(newUser)) {
			throw new DatabaseInsertException("Cannot create new user");
		}

		response.status(201);
		response.header("location", "/api/user/" + newUser.getUsername() + "/pageinfo");

		if (createSessionFlag(request)) {
			createSession(response, newUser);
		}
		return createLoginSuccess(newUser);
	}

	public Boolean handleEditUserPassword(Request request, Response response) throws IOException {
		ChangePassword changePassword = extractBodyContent(request, ChangePassword.class);

		authorizeRequest(request, changePassword.getUserId(), Permission.EDIT_PASSWORD);

		User user = dataService.getHashedPasswordByUserId(changePassword.getUserId());
		if (user == null || !passwordService.checkPassword(changePassword, user)) {
			throw new BadRequestException("Incorrect Old Password");
		}

		int len = changePassword.getNewPassword1().length();
		throwBadRequestExceptionIf(len < 5, "New Password Too Short");
		throwBadRequestExceptionIf(len > 64, "New Password Too Long");
		throwBadRequestExceptionIf(!changePassword.passwordsMatch(), "Passwords do not match");

		return dataService.editPassword(changePassword.getUserId(),
				passwordService.encryptPassword(changePassword.getNewPassword1()));
	}

	public LoginSuccess handleSessionRetrieval(Request request, Response response) throws IOException {

		String sessionKey = request.cookie(SESSION_COOKIE);
		if (StringUtils.isNotBlank(sessionKey)) {

			User user = dataService.getUserBySessionId(sessionKey);

			if (user != null) {
				return createLoginSuccess(user);
			}
		}
		return null;
	}

	public LoginSuccess handleLogin(Request request, Response response) throws IOException {

		if (request.cookie(SESSION_COOKIE) != null) {
			throw new InvalidUserLoginStateException("A User is already logged in");
		}

		LoginRequest loginRequest = extractBodyContent(request, LoginRequest.class);
		validateLoginRequest(loginRequest);

		User user = dataService.getUserLoginInfoByString(loginRequest.getUsernameOrEmail());

		if (user == null) {
			throw new FailedLoginAttemptException("Incorrect Username or Password");
		}

		if (!passwordService.checkPassword(loginRequest, user)) {
			throw new FailedLoginAttemptException("Incorrect Username or Password");
		}

		if (createSessionFlag(request)) {
			createSession(response, user);
		}
		return createLoginSuccess(user);

	}

	public Object handleLogout(Request request, Response response) {

		dataService.removeSessionId(request.cookie(SESSION_COOKIE));
		response.removeCookie(SESSION_COOKIE);
		return "ok";
	}

	private void createSession(Response response, User user) {
		String sessionKey = UUID.randomUUID().toString();
		if (!dataService.addUserSession(user.getUserId(), sessionKey)) {
			throw new DatabaseInsertException("Cannot create user session");
		}
		response.cookie("/", SESSION_COOKIE, sessionKey, 24 * 60 * 60 * 180, false);
	}

	private LoginSuccess createLoginSuccess(User user) throws IOException {
		LoginSuccess loginSuccess = new LoginSuccess();
		loginSuccess.setUserId(user.getUserId());
		loginSuccess.setUsername(user.getUsername());
		loginSuccess.setFirstname(user.getFullName().split(" ")[0]);

		Token.Builder tokenBuilder = Token.newBuilder()
				.setUserId(user.getUserId())
				.setUsername(user.getUsername());

		if (adminUserIds.contains(user.getUserId())) {
			tokenBuilder.addPermissions(Permission.ADMIN);
		} else {
			tokenBuilder.addPermissions(Permission.getRegularPermissions());
		}

		loginSuccess.setToken(tokenService.createTokenString(tokenBuilder.build()));
		return loginSuccess;
	}

	private void createHashedPassword(NewUser newUser) {
		String hashedPassword = passwordService.encryptPassword(newUser.getPassword1());
		newUser.setPassword1(null);
		newUser.setPassword2(null);
		newUser.setHashedPassword(hashedPassword);
	}

	private boolean createSessionFlag(Request request) {
		String createSessionFlag = request.queryParams("createSession");
		return createSessionFlag != null && !createSessionFlag.equalsIgnoreCase("false");
	}

	private void validateLoginRequest(LoginRequest loginRequest) {
		StringBuilder sb = new StringBuilder();
		checkParameter(loginRequest.getUsernameOrEmail(), "Login Request requires a 'username' or 'email'", sb);
		checkParameter(loginRequest.getPassword(), "Login Request requires a 'password'", sb);
		throwExceptionIfNecessary(sb);
	}

	private void validateNewUserRequest(NewUser newUser) {
		StringBuilder sb = new StringBuilder();
		checkParameter(newUser.getUsername(), "New User Request requires a 'username'", sb);
		checkParameter(newUser.getFullName(), "New User Request requires a 'fullName'", sb);
		checkParameter(newUser.getEmail(), "New User Request requires a 'email'", sb);
		checkParameter(newUser.getPassword1(), "New User Request requires a 'password1'", sb);
		checkParameter(newUser.getPassword2(), "New User Request requires a 'password2'", sb);
		checkParameter(newUser.getBirthDate(), "New User Request requires a 'birthDate'", sb);
		throwExceptionIfNecessary(sb);
	}
}
