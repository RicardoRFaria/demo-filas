package com.ricardofaria.demofilas.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class QueueConfig(@Value("\${aws.region}") private val region: String,
                  @Value("\${aws.accessKeyId}") private val accessKeyId: String,
                  @Value("\${aws.secretKey}") private val secretAccessKey: String,
                  @Value("\${aws.localstack.endpoint}") private val localstackEndpoint: String) {

    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))
    }

    @Bean
    fun sqsClient(awsCredentialsProvider: AwsCredentialsProvider): SqsClient {
        return SqsClient.builder()
                .endpointOverride(URI.create(localstackEndpoint))
                .region(Region.of(region)).credentialsProvider(awsCredentialsProvider).build()
    }

}