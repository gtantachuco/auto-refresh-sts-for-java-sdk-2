
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sts;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.StsException;

public class GetCallerIdentity {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getCallerId(stsClient);
        stsClient.close();
    }

    public static void getCallerId(StsClient stsClient) {

        try {
            GetCallerIdentityResponse response = stsClient.getCallerIdentity();

            System.out.println("The user id is " +response.userId());
            System.out.println("The ARN value is " +response.arn());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
