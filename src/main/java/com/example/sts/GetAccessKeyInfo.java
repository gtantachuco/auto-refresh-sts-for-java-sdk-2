
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sts;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;

public class GetAccessKeyInfo {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetAccessKeyInfo <accessKeyId> \n\n" +
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

        getKeyInfo(stsClient, accessKeyId );
        stsClient.close();
    }

    public static void getKeyInfo(StsClient stsClient, String accessKeyId ) {

        try {
            GetAccessKeyInfoRequest accessRequest = GetAccessKeyInfoRequest.builder()
                    .accessKeyId(accessKeyId)
                    .build();

            GetAccessKeyInfoResponse accessResponse = stsClient.getAccessKeyInfo(accessRequest);
            System.out.println("The account associated with the access key is "+accessResponse.account());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
