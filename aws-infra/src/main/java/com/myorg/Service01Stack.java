package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {
    public Service01Stack(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        Map<String, String> environments = new HashMap<>();
        environments.put("SPRING_DATASOURCE_URL", "jdbc:mysql://" + Fn.importValue("rds-endpoint") + ":3306/aws_project01?createDatabaseIfNotExist=true");
        environments.put("SPRING_DATASOURCE_URL", "admin");
        environments.put("SPRING_DATASOURCE_PASSWORD",  Fn.importValue("rds-password"));

        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder
                .create(this, "ALB01")
                .serviceName("service-01")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("aws_project01")
                                .image(ContainerImage.fromRegistry("rodrigolucio/service01:0.0.3"))
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                                .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                                        .logGroupName("Service01")
                                                        .removalPolicy(RemovalPolicy.DESTROY)
                                                        .build())
                                        .streamPrefix("Service01")
                                        .build()))
                                .environment(environments)
                                .build())
                .publicLoadBalancer(true)
                .build();

            service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                    .path("/actuactor/health")
                    .port("8080")
                    .healthyHttpCodes("200")
                    .build());

        // Configurações do auto scaling
        ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
                        .minCapacity(2) //Min 2 instâncias da aplicação
                        .maxCapacity(4) //Max 4 instâncias da aplicação
                .build());

        //Se o limite de CPU ultrapassar 50% num intervalo de 60s então ele cria uma nova instancia, no limite maximo de 4 instâncias
        //Se durante 60s, tiver um limite de CPU abaixo de 50%, ele destroi as instancias que foram criadas automaticamente
        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling", CpuUtilizationScalingProps.builder()
                        .targetUtilizationPercent(50)
                        .scaleInCooldown(Duration.seconds(60))
                        .scaleOutCooldown(Duration.seconds(60))
                .build());
    }
}
