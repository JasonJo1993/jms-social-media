package com.jms.socialmedia.cache;

import org.junit.Before;
import org.junit.Test;

import com.jms.socialmedia.model.Comment;
import com.jms.socialmedia.model.Post;
import com.jms.socialmedia.model.User;

import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

public class JavaMapCachingServiceTest {

	private JavaMapCachingService javaMapCachingService;

	@Before
	public void setUp() throws Exception {
		javaMapCachingService = new JavaMapCachingService();
	}

	@Test
	public void testPosts() {
		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(3), is(nullValue()));

		Post post1 = new Post(1);
		javaMapCachingService.putPostIntoCache(post1);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(3), is(nullValue()));

		Post post2 = new Post(2);
		javaMapCachingService.putPostIntoCache(post2);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(post2));
		assertThat(javaMapCachingService.getPostFromCache(3), is(nullValue()));

		Post post3 = new Post(3);
		javaMapCachingService.putPostIntoCache(post3);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(post2));
		assertThat(javaMapCachingService.getPostFromCache(3), is(post3));

		javaMapCachingService.removePostFromCache(2);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(3), is(post3));
	}

	@Test
	public void testPostsAndComments() {

		Comment comment1 = new Comment(31, 1, null, null); // Comment ID 31, Post ID 1
		Comment comment2 = new Comment(32, 1, null, null);
		Comment comment3 = new Comment(33, 2, null, null);
		Comment comment4 = new Comment(34, 2, null, null);

		Set<Comment> commentsSet1 = Set.of(comment1, comment2);
		Set<Comment> commentsSet2 = Set.of(comment3, comment4);

		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(nullValue()));

		Post post1 = new Post(1);
		javaMapCachingService.putPostIntoCache(post1);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getPostFromCache(3), is(nullValue()));

		javaMapCachingService.putCommentsFromPostIntoCache(1, commentsSet1);

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(commentsSet1));
		assertThat(javaMapCachingService.getCommentsFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(nullValue()));

		Post post2 = new Post(2);
		javaMapCachingService.putPostIntoCache(post2);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(post2));
		assertThat(javaMapCachingService.getPostFromCache(3), is(nullValue()));

		javaMapCachingService.putCommentsFromPostIntoCache(2, commentsSet2);

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(commentsSet1));
		assertThat(javaMapCachingService.getCommentsFromCache(2), is(commentsSet2));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(comment3));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));

		javaMapCachingService.removePostFromCache(2);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(commentsSet1));
		assertThat(javaMapCachingService.getCommentsFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(nullValue()));

		javaMapCachingService.putCommentIntoCache(comment4);
		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));

		javaMapCachingService.removeCommentFromCache(32);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));

		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(1));
		assertThat(javaMapCachingService.getCommentsFromCache(1).contains(comment1), is(true));
		assertThat(javaMapCachingService.getCommentsFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));

		javaMapCachingService.removePostFromCache(5);

		assertThat(javaMapCachingService.getPostFromCache(1), is(post1));
		assertThat(javaMapCachingService.getPostFromCache(2), is(nullValue()));

		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(1));
		assertThat(javaMapCachingService.getCommentsFromCache(1).contains(comment1), is(true));
		assertThat(javaMapCachingService.getCommentsFromCache(2), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));
	}

	@Test
	public void testAddComments() {
		Comment comment1 = new Comment(31, 1, null, null); // Comment ID 31, Post ID 1
		Comment comment2 = new Comment(32, 1, null, null);
		Comment comment3 = new Comment(33, 1, null, null);
		Comment comment4 = new Comment(34, 2, null, null);

		Set<Comment> commentsSet1 = Set.of(comment1, comment2);

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(nullValue()));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(nullValue()));

		javaMapCachingService.putCommentsFromPostIntoCache(1, commentsSet1);

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(commentsSet1));
		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(2));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(nullValue()));

		javaMapCachingService.putCommentIntoCache(comment3);

		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(3));
		assertThat(javaMapCachingService.getCommentsFromCache(1).containsAll(commentsSet1), is(true));
		assertThat(javaMapCachingService.getCommentsFromCache(1).contains(comment3), is(true));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(comment3));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(nullValue()));

		javaMapCachingService.putCommentIntoCache(comment4);

		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(3));
		assertThat(javaMapCachingService.getCommentsFromCache(1).containsAll(commentsSet1), is(true));
		assertThat(javaMapCachingService.getCommentsFromCache(1).contains(comment3), is(true));
		assertThat(javaMapCachingService.getCommentsFromCache(1).contains(comment4), is(false));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(comment3));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));

		javaMapCachingService.removeCommentFromCache(33);

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(commentsSet1));
		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(2));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));
		assertThat(javaMapCachingService.getCommentFromCache(37), is(nullValue()));
		
		javaMapCachingService.removeCommentFromCache(37);

		assertThat(javaMapCachingService.getCommentsFromCache(1), is(commentsSet1));
		assertThat(javaMapCachingService.getCommentsFromCache(1).size(), is(2));

		assertThat(javaMapCachingService.getCommentFromCache(31), is(comment1));
		assertThat(javaMapCachingService.getCommentFromCache(32), is(comment2));
		assertThat(javaMapCachingService.getCommentFromCache(33), is(nullValue()));
		assertThat(javaMapCachingService.getCommentFromCache(34), is(comment4));
		assertThat(javaMapCachingService.getCommentFromCache(37), is(nullValue()));
	}
	
	@Test
	public void testAddUserSessions() {
		User user1 = new User(1, "User1", "Full Name 1", "Hashed Password");
		User user2 = new User(2, "User2", "Full Name 2", "Hashed Password");

		String sessionKey1 = "sessionKey1";
		String sessionKey2 = "sessionKey2";

		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey1), is(nullValue()));
		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey2), is(nullValue()));

		javaMapCachingService.putUserSessionIntoCache(sessionKey1, user1);

		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey1), is(user1));
		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey2), is(nullValue()));

		javaMapCachingService.putUserSessionIntoCache(sessionKey2, user2);

		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey1), is(user1));
		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey2), is(user2));

		javaMapCachingService.removeUserSessionFromCache(sessionKey2);

		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey1), is(user1));
		assertThat(javaMapCachingService.getUserSessionFromCache(sessionKey2), is(nullValue()));
	}

	@Test
	public void testRemoveComment() {
		Comment comment = new Comment(1, 3, "Old Text", null);
		javaMapCachingService.putCommentIntoCache(comment);

		assertThat(javaMapCachingService.getCommentFromCache(1), is(comment));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));

		javaMapCachingService.removeCommentFromCache(1);
		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
		assertThat(javaMapCachingService.getCommentsFromCache(3), is(nullValue()));
	}

	@Test
	public void testEditPost() {
		Post post = new Post(1);
		post.setText("Old Text");
		javaMapCachingService.putPostIntoCache(post);
		assertThat(javaMapCachingService.getPostFromCache(1).getText(), is("Old Text"));

		javaMapCachingService.editPostInCache(1, "New Text");
		assertThat(javaMapCachingService.getPostFromCache(1).getText(), is("New Text"));
	}

	@Test
	public void testEditPostThatIsNotThere() {

		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
		javaMapCachingService.editPostInCache(1, "New Text");
		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
	}

	@Test
	public void testLikePost() {
		Post post = new Post(1);
		javaMapCachingService.putPostIntoCache(post);
		assertThat(javaMapCachingService.getPostFromCache(1).getLikes(), is(Collections.emptySet()));

		javaMapCachingService.likePostInCache(1, "Me");
		assertThat(javaMapCachingService.getPostFromCache(1).getLikes(), is(Collections.singleton("Me")));
	}

	@Test
	public void testLikePostThatIsNotThere() {

		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
		javaMapCachingService.likePostInCache(1, "Me");
		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
	}

	@Test
	public void testUnlikePost() {
		Post post = new Post(1);
		post.addLike("Me");
		javaMapCachingService.putPostIntoCache(post);
		assertThat(javaMapCachingService.getPostFromCache(1).getLikes(), is(Collections.singleton("Me")));

		javaMapCachingService.unlikePostInCache(1, "Me");
		assertThat(javaMapCachingService.getPostFromCache(1).getLikes(), is(Collections.emptySet()));
	}

	@Test
	public void testUnlikePostThatIsNotThere() {

		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
		javaMapCachingService.unlikePostInCache(1, "Me");
		assertThat(javaMapCachingService.getPostFromCache(1), is(nullValue()));
	}

	@Test
	public void testEditComment() {
		Comment comment = new Comment(1, 3, "Old Text", null);
		javaMapCachingService.putCommentIntoCache(comment);
		assertThat(javaMapCachingService.getCommentFromCache(1).getText(), is("Old Text"));

		javaMapCachingService.editCommentInCache(1, "New Text");
		assertThat(javaMapCachingService.getCommentFromCache(1).getText(), is("New Text"));
	}

	@Test
	public void testEditCommentThatIsNotThere() {

		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
		javaMapCachingService.editCommentInCache(1, "New Text");
		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
	}

	@Test
	public void testLikeComment() {
		Comment comment = new Comment(1, 3, "Old Text", null);
		javaMapCachingService.putCommentIntoCache(comment);
		assertThat(javaMapCachingService.getCommentFromCache(1).getLikes(), is(Collections.emptySet()));

		javaMapCachingService.likeCommentInCache(1, "Me");
		assertThat(javaMapCachingService.getCommentFromCache(1).getLikes(), is(Collections.singleton("Me")));
	}

	@Test
	public void testLikeCommentThatIsNotThere() {

		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
		javaMapCachingService.likeCommentInCache(1, "Me");
		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
	}

	@Test
	public void testUnlikeComment() {
		Comment comment = new Comment(1, 3, "Old Text", null);
		comment.addLike("Me");
		javaMapCachingService.putCommentIntoCache(comment);
		assertThat(javaMapCachingService.getCommentFromCache(1).getLikes(), is(Collections.singleton("Me")));

		javaMapCachingService.unlikeCommentInCache(1, "Me");
		assertThat(javaMapCachingService.getCommentFromCache(1).getLikes(), is(Collections.emptySet()));
	}

	@Test
	public void testUnlikeCommentThatIsNotThere() {

		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
		javaMapCachingService.unlikeCommentInCache(1, "Me");
		assertThat(javaMapCachingService.getCommentFromCache(1), is(nullValue()));
	}
}
