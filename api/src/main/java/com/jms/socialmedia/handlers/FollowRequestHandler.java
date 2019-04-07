package com.jms.socialmedia.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.jms.socialmedia.dataservice.DataService;
import com.jms.socialmedia.exception.BadRequestException;
import com.jms.socialmedia.model.FollowRequest;
import com.jms.socialmedia.model.User;

import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

public class FollowRequestHandler extends RequestHandler {

	private Random random = new Random();
	
	public FollowRequestHandler(DataService dataService, Gson gson) {
		super(dataService, gson);
	}

	public Boolean handleFollowUser(Request request, Response response) throws IOException {
		FollowRequest followRequest = extractBodyContent(request, FollowRequest.class);
		authorizeRequest(request, followRequest.getFollowerUserId(), "Follow User");
		if (followRequest.getFollowerUserId().equals(followRequest.getFollowingUserId())) {
			throw new BadRequestException("A User cannot follow themselves");
		}
		return dataService.followUser(followRequest.getFollowerUserId(), followRequest.getFollowingUserId());
	}

	public Boolean handleUnfollowUser(Request request, Response response) throws IOException {
		FollowRequest followRequest = extractBodyContent(request, FollowRequest.class);
		authorizeRequest(request, followRequest.getFollowerUserId(), "Unollow User");
		return dataService.unfollowUser(followRequest.getFollowerUserId(), followRequest.getFollowingUserId());
	}

	public Collection<Integer> handleGetFollowingUserIds(Request request, Response response) {

		Integer userId = Integer.parseInt(request.params("userid"));
		return dataService.getFollowingUserIds(userId);
	}
	
	public Collection<User> handleGetUsersToFollow(Request request, Response response) {

		Integer userId = Integer.parseInt(request.params("userid"));
		
		List<User> users = new ArrayList<>(dataService.getUsersToFollow(userId));
		
		int noOfUsers = users.size();
		
		String strMax = request.queryParams("max");
		int max = StringUtils.isBlank(strMax) ? noOfUsers : Integer.parseInt(strMax);
		max = max > noOfUsers ? noOfUsers : max;

		Collection<User> randomizedUsers = new HashSet<>();
		while (randomizedUsers.size() < max) {
			randomizedUsers.add(users.get(random.nextInt(noOfUsers)));
		}
		return randomizedUsers;
	}
}