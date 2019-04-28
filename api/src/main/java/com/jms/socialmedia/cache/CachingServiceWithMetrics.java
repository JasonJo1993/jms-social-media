package com.jms.socialmedia.cache;

import java.util.Collection;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.jms.socialmedia.model.Comment;
import com.jms.socialmedia.model.Post;

public class CachingServiceWithMetrics implements CachingService {

	private final CachingService cachingService;
	private final Timer getPostFromCacheTimer;
	private final Timer putPostIntoCacheTimer;
	private final Timer removePostFromCacheTimer;
	private final Timer getCommentsFromCacheTimer;
	private final Timer getCommentFromCacheTimer;
	private final Timer putCommentIntoCacheTimer;
	private final Timer putCommentsFromPostIntoCacheTimer;
	private final Timer removeCommentFromCacheTimer;

	public CachingServiceWithMetrics(CachingService cachingService, MetricRegistry metricRegistry) {
		this(cachingService, metricRegistry, cachingService.getClass().getSimpleName());
	}

	public CachingServiceWithMetrics(CachingService cachingService, MetricRegistry metricRegistry, String metricsName) {
		this.cachingService = cachingService;
		this.getPostFromCacheTimer = metricRegistry.timer(metricsName + ".getPostFromCache");
		this.putPostIntoCacheTimer = metricRegistry.timer(metricsName + ".putPostIntoCache");
		this.removePostFromCacheTimer = metricRegistry.timer(metricsName + ".removePostFromCache");
		this.getCommentsFromCacheTimer = metricRegistry.timer(metricsName + ".getCommentsFromCache");
		this.getCommentFromCacheTimer = metricRegistry.timer(metricsName + ".getCommentFromCache");
		this.putCommentIntoCacheTimer = metricRegistry.timer(metricsName + ".putCommentIntoCache");
		this.putCommentsFromPostIntoCacheTimer = metricRegistry.timer(metricsName + ".putCommentsFromPostIntoCache");
		this.removeCommentFromCacheTimer = metricRegistry.timer(metricsName + ".removeCommentFromCache");
	}

	@Override
	public Post getPostFromCache(int postId) {
		try (Timer.Context context = getPostFromCacheTimer.time()) {
			return cachingService.getPostFromCache(postId);
		}
	}

	@Override
	public void putPostIntoCache(Post post) {
		try (Timer.Context context = putPostIntoCacheTimer.time()) {
			cachingService.putPostIntoCache(post);
		}
	}

	@Override
	public void removePostFromCache(int postId) {
		try (Timer.Context context = removePostFromCacheTimer.time()) {
			cachingService.removePostFromCache(postId);
		}
	}

	@Override
	public Collection<Comment> getCommentsFromCache(int postId) {
		try (Timer.Context context = getCommentsFromCacheTimer.time()) {
			return cachingService.getCommentsFromCache(postId);
		}
	}

	@Override
	public Comment getCommentFromCache(int commentId) {
		try (Timer.Context context = getCommentFromCacheTimer.time()) {
			return cachingService.getCommentFromCache(commentId);
		}
	}

	@Override
	public void putCommentIntoCache(Comment comment) {
		try (Timer.Context context = putCommentIntoCacheTimer.time()) {
			cachingService.putCommentIntoCache(comment);
		}
	}

	@Override
	public void putCommentsFromPostIntoCache(int postId, Collection<Comment> comments) {
		try (Timer.Context context = putCommentsFromPostIntoCacheTimer.time()) {
			cachingService.putCommentsFromPostIntoCache(postId, comments);
		}
	}

	@Override
	public void removeCommentFromCache(int commentId) {
		try (Timer.Context context = removeCommentFromCacheTimer.time()) {
			cachingService.removeCommentFromCache(commentId);
		}
	}
}