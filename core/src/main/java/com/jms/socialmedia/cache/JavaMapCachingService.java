package com.jms.socialmedia.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import com.jms.socialmedia.model.Comment;
import com.jms.socialmedia.model.Post;

public class JavaMapCachingService implements CachingService {

	private final Map<Integer, Post> postsById;
	private final Map<Integer, Comment> commentsById;
	private final Map<Integer, Collection<Comment>> commentsByPostId;

	public JavaMapCachingService() {
		this(new HashMap<>(), new HashMap<>(), new HashMap<>());
	}

	public JavaMapCachingService(Map<Integer, Post> postsById, Map<Integer, Comment> commentsById, Map<Integer, Collection<Comment>> commentsByPostId) {
		this.postsById = postsById;
		this.commentsById = commentsById;
		this.commentsByPostId = commentsByPostId;
	}

	@Override
	public Post getPostFromCache(int postId) {
		return postsById.get(postId);
	}

	@Override
	public void putPostIntoCache(Post post) {
		postsById.put(post.getPostId(), post);
	}

	@Override
	public void removePostFromCache(int postId) {
		Post post = postsById.remove(postId);
		if (post != null) {
			Collection<Comment> comments = commentsByPostId.remove(post.getPostId());
			if (comments != null) {
				for (Comment comment : comments) {
					commentsById.remove(comment.getCommentId());
				}
			}
		}
	}

	@Override
	public Collection<Comment> getCommentsFromCache(int postId) {
		return commentsByPostId.get(postId);
	}

	@Override
	public Comment getCommentFromCache(int commentId) {
		return commentsById.get(commentId);
	}

	@Override
	public void putCommentIntoCache(Comment comment) {
		commentsById.put(comment.getCommentId(), comment);
		Collection<Comment> comments = getCommentsFromCache(comment.getPostId());
		if (comments != null) {
			comments.add(comment);
		}
	}

	@Override
	public void putCommentsFromPostIntoCache(int postId, Collection<Comment> comments) {
		commentsByPostId.put(postId, new HashSet<>(comments));
		for (Comment comment : comments) {
			commentsById.put(comment.getCommentId(), comment);
		}
	}

	@Override
	public void removeCommentFromCache(int commentId) {
		Comment comment = commentsById.remove(commentId);
		if (comment != null) {
			Collection<Comment> comments = getCommentsFromCache(comment.getPostId());
			if (comments != null) {
				comments.remove(comment);
			}
		}
	}
}