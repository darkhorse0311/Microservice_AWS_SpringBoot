package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueEncryption;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class Service02Stack extends Stack {

    public static final String ACTUATOR_HEALTH = "/actuator/health";
    public static final String ACTUATOR_PORT = "8081";

    public Service02Stack(final Construct scope, final String id, Cluster cluster, SnsTopic productEventsToppic, Table productEventsDynamo) {
        this(scope, id, null, cluster, productEventsToppic, productEventsDynamo);
    }

    public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic productEventsToppic, Table productEventsDynamo) {
        super(scope, id, props);

        Queue productEventsDql = Queue.Builder.create(this, "ProductEventsDlq")
                .queueName("product-events-dql")
                .enforceSsl(false)
                .encryption(QueueEncryption.UNENCRYPTED)
                .build();

        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(productEventsDql)
                .maxReceiveCount(3) //Quantas exceptions vai acontecer para que a mensagem caia na dql
                .build();

        Queue productEventsQueue = Queue.Builder.create(this, "ProductEvents")
                .queueName("product-events")
                .enforceSsl(false)
                .encryption(QueueEncryption.UNENCRYPTED)
                .deadLetterQueue(deadLetterQueue)
                .build();

        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(productEventsQueue).build();
        productEventsToppic.getTopic().addSubscription(sqsSubscription);

        Map<String, String> environments = new HashMap<>();
        environments.put("AWS_REGION", "us-east-1");
        environments.put("AWS_SQS_QUEUE_PRODUCT_EVENTS_NAME", productEventsQueue.getQueueName());

        ApplicationLoadBalancedFargateService service02 = ApplicationLoadBalancedFargateService.Builder
                .create(this, "ALB02")
                .serviceName("service-02")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .listenerPort(8081)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("aws_project02")
                                .image(ContainerImage.fromRegistry("rodrigolucio/service02:0.0.10"))
                                .containerPort(8081)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "Service02LogGroup")
                                                .logGroupName("Service02")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .streamPrefix("Service02")
                                        .build()))
                                .environment(environments)
                                .build())
                .publicLoadBalancer(true)
                .build();

        service02.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path(ACTUATOR_HEALTH)
                .port(ACTUATOR_PORT)
                .healthyHttpCodes("200")
                .build());

        // Configurações do auto scaling
        ScalableTaskCount scalableTaskCount = service02.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(2) //Min 2 instâncias da aplicação
                .maxCapacity(4) //Max 4 instâncias da aplicação
                .build());

        //Se o limite de CPU ultrapassar 50% num intervalo de 60s então ele cria uma nova instancia, no limite maximo de 4 instâncias
        //Se durante 60s, tiver um limite de CPU abaixo de 50%, ele destroi as instancias que foram criadas automaticamente
        scalableTaskCount.scaleOnCpuUtilization("Service02AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)
                .scaleInCooldown(Duration.seconds(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build());

        productEventsQueue.grantConsumeMessages(service02.getTaskDefinition().getTaskRole());
        productEventsDynamo.grantReadWriteData(service02.getTaskDefinition().getTaskRole());

    }
}
