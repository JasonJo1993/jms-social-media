package com.mytwitter.mybatis;

import java.util.Collection;

import org.apache.ibatis.annotations.Param;

import com.mytwitter.model.Comment;
import com.mytwitter.model.FullPost;
import com.mytwitter.model.Post;

public interface PostsMapper {

	int getNumberOfPosts();
	
	FullPost getPost(int postId);
	
	Collection<Post> getPosts(@Param(value="userId") int userId, @Param(value="username") String username, 
			@Param(value="tag") String tag, @Param(value="onDate") String onDate, 
			@Param(value="beforeDate") String beforeDate, @Param(value="afterDate")String afterDate);
	
	int addPost(Post post);
	
	int addComment(Comment comment);
	
	Collection<Comment> getComments(int postId);
}
