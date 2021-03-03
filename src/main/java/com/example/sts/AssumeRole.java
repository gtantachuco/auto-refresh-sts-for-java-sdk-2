
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sts;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

import java.util.Locale;

/**
 * To make this code example work, create a Role that you want to assume.
 * Then define a Trust Relationship in the AWS Console. You can use this as an example:
 *
 * {
 *   "Version": "2012-10-17",
 *   "Statement": [
 *     {
 *       "Effect": "Allow",
 *       "Principal": {
 *         "AWS": "<Specify the ARN of your IAM user you are using in this code example>"
 *       },
 *       "Action": "sts:AssumeRole"
 *     }
 *   ]
 * }
 *
 *  For more information, see "Editing the Trust Relationship for an Existing Role" in the AWS Directory Service guide.
 */
public class AssumeRole {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    AssumeRole <roleArn> <roleSessionName> \n\n" +
                "Where:\n" +
                "    roleArn - the Amazon Resource Name (ARN) of the role to assume (for example, rn:aws:iam::000008047983:role/s3role). \n"+
                "    roleSessionName - an identifier for the assumed role session (for example, mysession). \n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String roleArn = args[0];
        String roleSessionName = args[1];

        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        assumeGivenRole(stsClient, roleArn, roleSessionName);
        stsClient.close();
    }

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

        System.out.println("roleRequest.durationSeconds [" + roleRequest.durationSeconds() + "]");
        System.out.println("credentialsProvider.prefetchTime [" + credentialsProvider.prefetchTime() + "]");
        System.out.println("credentialsProvider.staleTime [" + credentialsProvider.staleTime() + "]");

        
        
        try {
            for (int i = 1; i <= 20; i++) {
                System.out.println("\n\n Loop # [" + i + "]");
                AwsSessionCredentials myCreds = (AwsSessionCredentials) credentialsProvider.resolveCredentials();
                
                System.out.println("myCreds.sessionToken    [" + myCreds.sessionToken() + "]");
                System.out.println("myCreds.accessKeyId     [" + myCreds.accessKeyId() + "]");
                System.out.println("myCreds.secretAccessKey [" + myCreds.secretAccessKey() + "]");
                
                TimeUnit.SECONDS.sleep(60);
            }    
            

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        
        

       } catch (StsException e) {
           e.printStackTrace();
           System.err.println(e.getMessage());
           System.exit(1);
       }
   }

    public static void assumeGivenRole(StsClient stsClient, String roleArn, String roleSessionName) {

       try {

            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(roleSessionName)
                .durationSeconds(7200)
                .build();

           AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
           Credentials myCreds = roleResponse.credentials();

           // Display the time when the temp creds expire
           Instant exTime = myCreds.expiration();
           String tokenInfo = myCreds.sessionToken();

           // Convert the Instant to readable date
           DateTimeFormatter formatter =
                   DateTimeFormatter.ofLocalizedDateTime( FormatStyle.FULL )
                           .withLocale( Locale.US)
                           .withZone( ZoneId.of("America/Chicago") );
//                           .withZone( ZoneId.systemDefault() );

           System.out.println("The token "+tokenInfo + "  expires on " + formatter.format( exTime ) );

       } catch (StsException e) {
           e.printStackTrace();
           System.err.println(e.getMessage());
           System.exit(1);
       }
   }
    
    
}
