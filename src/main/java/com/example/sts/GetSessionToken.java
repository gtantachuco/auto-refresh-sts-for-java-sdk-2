
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sts;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;

public class GetSessionToken {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                "Usage:\n" +
                "    GetSessionToken <accessKeyId> \n\n" +
                "Where:\n" +
                "    accessKeyId - the identifier of an access key (for example, XXXXX3JWY3BXW7POHDLA). \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String accessKeyId = args[0];
        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getToken(stsClient, accessKeyId);
        stsClient.close();
    }

    public static void getToken(StsClient stsClient, String accessKeyId ) {

        try {
            GetSessionTokenRequest tokenRequest = GetSessionTokenRequest.builder()
                    .durationSeconds(1500)
                    .build();

            GetSessionTokenResponse tokenResponse = stsClient.getSessionToken(tokenRequest);
            System.out.println("The token value is "+tokenResponse.credentials().sessionToken());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
