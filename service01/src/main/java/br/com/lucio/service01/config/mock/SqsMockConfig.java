package br.com.lucio.service01.config.mock;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class SqsMockConfig {

    private static final Logger log = LoggerFactory.getLogger(SqsMockConfig.class);

    public SqsMockConfig(AmazonSNS snsClient,
                         @Qualifier("productEventsTopic") Topic productEventsTopic) {
        AmazonSQS sqsClient = AmazonSQSClient.builder()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://localhost:4566",
                                Regions.US_EAST_1.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        String productEventsQueueUrl = sqsClient.createQueue(new CreateQueueRequest("product-events")).getQueueUrl();

        Topics.subscribeQueue(snsClient, sqsClient, productEventsTopic.getTopicArn(), productEventsQueueUrl);
    }

}
