package com.api.flux.courseed.services.eventlisteners;

import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.api.flux.courseed.persistence.documents.Content;
import com.api.flux.courseed.persistence.documents.Reaction;
import com.api.flux.courseed.persistence.documents.Review;
import com.api.flux.courseed.persistence.documents.View;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CourseDeletionListener {
    private final MongoClient mongoClient;
    private final ReactiveMongoTemplate mongoTemplate;

    public CourseDeletionListener(MongoClient mongoClient, ReactiveMongoTemplate mongoTemplate) {
        this.mongoClient = mongoClient;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void watchCourseDeletions() {
        MongoDatabase database = mongoClient.getDatabase("courseed");
        MongoCollection<Document> courseCollection = database.getCollection("courses");
        Flux<ChangeStreamDocument<Document>> changeStreamFlux = Flux.from(courseCollection.watch());

        changeStreamFlux
            .filter(event -> event.getOperationType() == OperationType.DELETE)
            .flatMap(event -> {
                String deletedCourseId = event.getDocumentKey().getObjectId("_id").getValue().toString();
                return cascadeDeleteCourseData(deletedCourseId);
            })
            .subscribe();
    }

    private Mono<Void> cascadeDeleteCourseData(String courseId) {
        return Mono.when(
            mongoTemplate.remove(Query.query(Criteria.where("courseId").is(courseId)), Review.class),
            mongoTemplate.remove(Query.query(Criteria.where("courseId").is(courseId)), View.class),
            mongoTemplate.remove(Query.query(Criteria.where("courseId").is(courseId)), Reaction.class),
            mongoTemplate.remove(Query.query(Criteria.where("courseId").is(courseId)), Content.class)
        ).then();
    }
}
