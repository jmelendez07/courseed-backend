package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Subscription;
import com.api.flux.courseed.projections.dtos.SaveSubscriptionDto;
import com.api.flux.courseed.projections.dtos.SubscriptionDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T12:22:53-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class SubscriptionMapperImpl implements SubscriptionMapper {

    @Override
    public SubscriptionDto toSubscriptionDto(Subscription subscription) {
        if ( subscription == null ) {
            return null;
        }

        SubscriptionDto subscriptionDto = new SubscriptionDto();

        subscriptionDto.setId( subscription.getId() );
        subscriptionDto.setPlan( subscription.getPlan() );
        subscriptionDto.setState( subscription.getState() );
        subscriptionDto.setReferenceCode( subscription.getReferenceCode() );
        subscriptionDto.setTransaction( subscription.getTransaction() );
        subscriptionDto.setPrice( subscription.getPrice() );
        subscriptionDto.setCreatedAt( subscription.getCreatedAt() );
        subscriptionDto.setStartDate( subscription.getStartDate() );
        subscriptionDto.setEndDate( subscription.getEndDate() );
        subscriptionDto.setPaymentMethod( subscription.getPaymentMethod() );
        subscriptionDto.setCurrency( subscription.getCurrency() );
        subscriptionDto.setAuthorizationCode( subscription.getAuthorizationCode() );
        subscriptionDto.setResponseMessage( subscription.getResponseMessage() );

        return subscriptionDto;
    }

    @Override
    public Subscription toSubscription(SaveSubscriptionDto saveSubscriptionDto) {
        if ( saveSubscriptionDto == null ) {
            return null;
        }

        Subscription subscription = new Subscription();

        subscription.setPaymentMethod( saveSubscriptionDto.getPaymentMethod() );
        subscription.setCurrency( saveSubscriptionDto.getCurrency() );
        subscription.setAuthorizationCode( saveSubscriptionDto.getAuthorizationCode() );
        subscription.setResponseMessage( saveSubscriptionDto.getResponseMessage() );
        subscription.setUserId( saveSubscriptionDto.getUserId() );
        subscription.setPlan( saveSubscriptionDto.getPlan() );
        subscription.setState( saveSubscriptionDto.getState() );
        subscription.setReferenceCode( saveSubscriptionDto.getReferenceCode() );
        subscription.setTransaction( saveSubscriptionDto.getTransaction() );
        subscription.setPrice( saveSubscriptionDto.getPrice() );
        subscription.setStartDate( saveSubscriptionDto.getStartDate() );
        subscription.setEndDate( saveSubscriptionDto.getEndDate() );

        return subscription;
    }
}
