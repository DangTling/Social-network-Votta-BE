package com.example.backend.repository;

import com.example.backend.models.Post;
import com.example.backend.models.User;
import com.example.backend.response.TopCreator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findPostByPostedBy(User postedBy);
    Post findPostById(String id);
//    List<Post> findPostsByPostedByInOrderByCreatedAtDesc(Collection<User> postedBy, Pageable pageable);
    long countByPostedByInAndBelongsToIsNull(Collection<User> postedBy);
    List<Post> findPostsByPostedByInAndBelongsToIsNullOrderByCreatedAtDesc(Collection<User> postedBy, Pageable pageable);
    List<Post> findPostsByPostedByIsNotOrderByCreatedAtDesc(User currentUser, Pageable pageable);
    long countByBelongsToIsNotNull();
    List<Post> findPostsByBelongsTo_MembersOrderByCreatedAtDesc(User user, Pageable pageable);
    long countByBelongsTo_MembersOrderByCreatedAtDesc(User user);

    List<Post> findPostsByBelongsTo_IdAndPendingFalseOrBelongsTo_IdAndPendingNullOrderByCreatedAtDesc(List<String> ids, List<String> ids2, Pageable pageable);
    long countPostsByBelongsTo_IdAndPendingFalseOrBelongsTo_IdAndPendingNull(List<String> ids, List<String> ids2);

    @Query("{ 'belongsTo': { $exists: true }, 'belongsTo.members': { $ne: ?0 } }")
    List<Post> findPostsByBelongsToMembersNotContaining(String userId, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { 'belongsTo.members': { $ne: ?0 }, 'belongsTo': { $exists: true } } }",
            "{ $count: 'count' }"
    })
    long countPostsByBelongsToMembersNotContaining(String userId);

    List<Post> findPostsByBelongsTo_IdAndPendingFalseOrPendingNullAndBelongsTo_IdOrderByCreatedAtDesc(String communityId, String belongsTo_id2, Pageable pageable);
    long countPostsByBelongsTo_IdAndPendingFalseOrPendingNullAndBelongsTo_IdOrderByCreatedAt(String belongsTo_id, String belongsTo_id2);

    List<Post> findPostsByBelongsTo_IdAndPendingTrueOrderByCreatedAtDesc(String communityId, Pageable pageable);
    long countPostsByBelongsTo_IdAndPendingTrue(String communityId);

    List<Post> findPostsBySavedInContainsOrderByCreatedAt(String userId, Pageable pageable);
    long countPostsBySavedInContains(String userId);

    @Aggregation(pipeline = {
            "{ $match: { 'createdAt': { $gte: ?0, $lte: ?1 } } }",
            "{ $addFields: { totalLikes: { $size: '$like' }, totalComments: { $size: '$replies' } } }",
            "{ $addFields: { totalInteractions: { $add: ['$totalLikes', '$totalComments'] } } }",
            "{ $sort: { totalInteractions: -1, createdAt: -1 } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    List<Post> findTopPost(Date startDate, Date endDate, int skip, int limit);

    @Aggregation(pipeline = {
            "{ $match: { 'createdAt': { $gte: ?0, $lte: ?1 } } }",
            "{ $count: 'totalPosts' }"
    })
    long countTopPosts(Date startDate, Date endDate);

    List<Post> findPostsByCaptionContainsIgnoreCaseOrTagsContainsIgnoreCaseOrderByCreatedAtDesc(String captionQuery, String tagsQuery, Pageable pageable);

    long countPostsByCaptionContainsIgnoreCaseOrTagsContainsIgnoreCase(String captionQuery, String tagsQuery);
}
