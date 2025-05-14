package com.api.flux.courseed.services.eventlisteners;

import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.api.flux.courseed.persistence.documents.Reaction;
import com.api.flux.courseed.persistence.documents.Review;
import com.api.flux.courseed.persistence.documents.SearchHistory;
import com.api.flux.courseed.persistence.documents.Subscription;
import com.api.flux.courseed.persistence.documents.View;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserDeletionListener {
    
    private final MongoClient mongoClient;
    private final ReactiveMongoTemplate mongoTemplate;

    public UserDeletionListener(MongoClient mongoClient, ReactiveMongoTemplate mongoTemplate) {
        this.mongoClient = mongoClient;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void watchUserDeletions() {
        MongoDatabase database = mongoClient.getDatabase("courseed");
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Flux<ChangeStreamDocument<Document>> changeStreamFlux = Flux.from(usersCollection.watch());

        changeStreamFlux
            .filter(event -> event.getOperationType() == OperationType.DELETE)
            .flatMap(event -> {
                String deletedUserId = event.getDocumentKey().getObjectId("_id").getValue().toString();
                return cascadeDeleteUserData(deletedUserId);
            })
            .subscribe();
    }

    private Mono<Void> cascadeDeleteUserData(String userId) {
        return Mono.when(
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), Review.class),
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), View.class),
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), Reaction.class),
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), SearchHistory.class),
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), Subscription.class)
        ).then();
    }

}
