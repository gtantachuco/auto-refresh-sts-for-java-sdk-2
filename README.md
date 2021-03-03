# Automatically renew AWS STS (Security Token Service) tokens with AWS SDK for Java v2

A common way to obtain AWS credentials is to assume an IAM role and be given a set of temporary session keys that are only good for a certain period of time. The maximum session duration is a setting on the IAM role itself, and it is one hour by default. 

So, if you are looking for a pattern for automatically renewing your STS tokens, you have come to the right place.

## About this repo
The sample shows several ways to interact with STS via the Java SDK. These are the classes that implement the logic:

| Java class | Purpose |
| --- | ---|
| AssumeRole.java | Returns a set of temporary security credentials that you can use to access AWS resources that you might not normally have access to. |
| GetAccessKeyInfo.java | Returns the account identifier for the specified access key ID.|
| GetCallerIdentity.java | Returns details about the IAM user or role whose credentials are used to call the operation. |
| GetSessionToken.java | Returns a set of temporary credentials for an AWS account or IAM user. |
| STSServiceTest.java | JUnit test that invokes each one of these classes|

## About the StsAssumeRoleCredentialsProvider class

The code leverages the AWS SDK for Java version 2 and uses the [StsAssumeRoleCredentialsProvider](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sts/auth/StsAssumeRoleCredentialsProvider.html) credentials provider. Here is a brief summary of what the `StsAssumeRoleCredentialsProvider` class does:

> An implementation of AwsCredentialsProvider that periodically sends an AssumeRoleRequest to the AWS Security Token Service to maintain short-lived sessions to use for authentication. These sessions are updated asynchronously in the background as they get close to expiring. If the credentials are not successfully updated asynchronously in the background, calls to resolveCredentials() will begin to block in an attempt to update the credentials synchronously. This provider creates a thread in the background to periodically update credentials. If this provider is no longer needed, the background thread can be shut down using close(). This is created using builder().

For more details about Credential Providers and the changes between version 1 and version 2 of the SDK, please look at the [AWS SDK for Java documentation](https://docs.aws.amazon.com/sdk-for-java/latest/migration-guide/client-credential.html)

## See the StsAssumeRoleCredentialsProvider class in Action

For testing purposes, the [AssumeRoleRequest](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sts/model/AssumeRoleRequest.html) instance has been set to 900 seconds or 15 minutes. You can set the value according to your requirements.

```java
    public static void gtStsAssumeRoleCredentialsProvider(StsClient stsClient, String roleArn, String roleSessionName) {
       try {
            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(roleSessionName)
                .durationSeconds(900)
                .build();

            StsAssumeRoleCredentialsProvider credentialsProvider = StsAssumeRoleCredentialsProvider
                                                        .builder()
                                                        .stsClient(stsClient)
                                                        .refreshRequest(roleRequest)
                                                        .build();
     // ...
     // ...
     // ...
       }

```

Now that you have an instance of `StsAssumeRoleCredentialsProvider`, you can pass it to any class that receives an [AwsCredentialsProvider class](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/AwsCredentialsProvider.html).

Or, you can get the credentials and session token, like in the example below:

`AwsSessionCredentials myCreds = (AwsSessionCredentials) credentialsProvider.resolveCredentials();`


## Building and run the sample code

You can build the code with Maven:

`mvn clean package`

During the build process, the JUnit tests will execute. To see the auto-refresh in action, please look at the `gtAssumeRole()` method of the `STSServiceTest.java` test class:

```java
    @Test
    @Order(3)
    public void gtAssumeRole() {

        AssumeRole.gtStsAssumeRoleCredentialsProvider(stsClient, roleArn, roleSessionName);
        System.out.println("Test 3 passed");
    }
```
## Test results

Your results will be similar to this:


roleRequest.durationSeconds [900]
credentialsProvider.prefetchTime [PT5M]
credentialsProvider.staleTime [PT1M]

```
Loop # [1]
myCreds.sessionToken    [IQoJb3JpZ2luX2VjEJv//////////wEaCXVzLWVhc3QtMSJIMEYCIQCNEYSZkreek1/5tcbDR7sUcWRIjhwuysGSGC0PXlfq6gIhAPSQwr9yI9bXTElgG4DArosPz3nzdQ5A1X/VPH7e1EMgKp8CCLT//////////wEQARoMNzk2MzA4NTI4NzUyIgxiyfK2q8tNZOKs2waciNjk+T5Y/By9olYQm0i3capvuU2qma9Rr9EA2Yj6vlCuekcLauWMmDmzqBxmV/lnaIeQ1PsNHQi9KlRGx9y+w9wz24d6EF8MCUGt1eeQf2Raygzt81ufFFwLysmN7jJLc68f06g8UMmv2snUKe9xyu58dywLs/vvinKM8=]
myCreds.accessKeyId     [ASIA3SZ5Q1ZYEQLHLLEG]
myCreds.secretAccessKey [9HOl12345rw0m27E2B0Ani9JDI+elKXQ0/GWPAvm]
```

5 minutes before the expiration of your current session token, you will automatically get new session token, access key and secret key.

```
Loop # [11]
myCreds.sessionToken    [IQoJb3JpZ2luX2VjEJz//////////wEaCXVzLWVhc3QtMSJGMEQCIFrGRsJK26hnhWmpk0QsjWUKmquofRPa8DO3jcRBo5lXAiAyg7DhbcZM2tN7iYUHwylU5Xm1A//M7kkpGFv+k/qGXiqfAgi0//////////8BEAEaDDc5NjMwODUyODc1MiIMGKIDLXmhyxcEjZLZKvMBIl9HacoWMzVHzkiDw16PaC9pdExq48djfpV31X1qxpPjZO1GhuV9JcqOXmCEbkasidfPZDMxrGiKnpzfWb952SjFbpYyXWieA5MfC9VkJLZxgxoQxsmbvrDIte4s8QvQRslpkyGGwFl9+42ZxG/IHD1IBzN94SPBdIC2djHTfz5jsWG9Dapc7U76sZV6AHj8/DFZ9X/bvcxMQAX/uG91HTkkCQvCn7mi1uflCxOveuNaNH8sW4mT6PJ/1GZjr6U2tEG4cftor/Yeq5jiSCBnCzS5yEyKF8aPFuxVRlDMehVeHX8Ssb+rXtjI66tYRolOXQ+7MJL7+4EGOp4Bj57F0A1VWOvw+DvNZzkATimn4v044FfhRYQdfpttMzO7Lo/NS0sb6dKRlyX4T2rcO6FEwSJqRHqYIkthnLIe1PcFn4i41qgMckB4ed5ezwr5Z24VyS029SsqcP1FBJG6R3x78BJfO4pImRzXAmgqvRel6MLe8J79RkU8Z0T1vm78a0sldXs8XPxgfFyrb3qxeChiaIUuVhCqc/Yb69w=]
myCreds.accessKeyId     [ASIA3SZ5P3ZYHXLPY44U]
myCreds.secretAccessKey [pZhTozvPRt9JM3v8ymWBUSg6uN/rui0w9tC14QHa]
```
