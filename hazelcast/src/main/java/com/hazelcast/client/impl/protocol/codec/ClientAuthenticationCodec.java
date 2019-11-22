/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.Generated;
import com.hazelcast.client.impl.protocol.codec.builtin.*;
import com.hazelcast.client.impl.protocol.codec.custom.*;

import javax.annotation.Nullable;

import static com.hazelcast.client.impl.protocol.ClientMessage.*;
import static com.hazelcast.client.impl.protocol.codec.builtin.FixedSizeTypesCodec.*;

/*
 * This file is auto-generated by the Hazelcast Client Protocol Code Generator.
 * To change this file, edit the templates or the protocol
 * definitions on the https://github.com/hazelcast/hazelcast-client-protocol
 * and regenerate it.
 */

/**
 * TODO DOC
 */
@Generated("282a83eaef417a5503b7012435390ee6")
public final class ClientAuthenticationCodec {
    //hex: 0x000100
    public static final int REQUEST_MESSAGE_TYPE = 256;
    //hex: 0x000101
    public static final int RESPONSE_MESSAGE_TYPE = 257;
    private static final int REQUEST_UUID_FIELD_OFFSET = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_SERIALIZATION_VERSION_FIELD_OFFSET = REQUEST_UUID_FIELD_OFFSET + UUID_SIZE_IN_BYTES;
    private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_SERIALIZATION_VERSION_FIELD_OFFSET + BYTE_SIZE_IN_BYTES;
    private static final int RESPONSE_STATUS_FIELD_OFFSET = RESPONSE_BACKUP_ACKS_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_UUID_FIELD_OFFSET = RESPONSE_STATUS_FIELD_OFFSET + BYTE_SIZE_IN_BYTES;
    private static final int RESPONSE_SERIALIZATION_VERSION_FIELD_OFFSET = RESPONSE_UUID_FIELD_OFFSET + UUID_SIZE_IN_BYTES;
    private static final int RESPONSE_PARTITION_COUNT_FIELD_OFFSET = RESPONSE_SERIALIZATION_VERSION_FIELD_OFFSET + BYTE_SIZE_IN_BYTES;
    private static final int RESPONSE_CLUSTER_ID_FIELD_OFFSET = RESPONSE_PARTITION_COUNT_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = RESPONSE_CLUSTER_ID_FIELD_OFFSET + UUID_SIZE_IN_BYTES;

    private ClientAuthenticationCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * Cluster name that client will connect to.
         */
        public java.lang.String clusterName;

        /**
         * Name of the user for authentication.
         * Used in case Client Identity Config, otherwise it should be passed null.
         */
        public @Nullable java.lang.String username;

        /**
         * Password for the user.
         * Used in case Client Identity Config, otherwise it should be passed null.
         */
        public @Nullable java.lang.String password;

        /**
         * Unique string identifying the connected client uniquely.
         */
        public @Nullable java.util.UUID uuid;

        /**
         * The type of the client. E.g. JAVA, CPP, CSHARP, etc.
         */
        public java.lang.String clientType;

        /**
         * client side supported version to inform server side
         */
        public byte serializationVersion;

        /**
         * The Hazelcast version of the client. (e.g. 3.7.2)
         */
        public java.lang.String clientHazelcastVersion;

        /**
         * the name of the client instance
         */
        public java.lang.String clientName;

        /**
         * User defined labels of the client instance
         */
        public java.util.List<java.lang.String> labels;
    }

    public static ClientMessage encodeRequest(java.lang.String clusterName, @Nullable java.lang.String username, @Nullable java.lang.String password, @Nullable java.util.UUID uuid, java.lang.String clientType, byte serializationVersion, java.lang.String clientHazelcastVersion, java.lang.String clientName, java.util.Collection<java.lang.String> labels) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(true);
        clientMessage.setOperationName("Client.Authentication");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeUUID(initialFrame.content, REQUEST_UUID_FIELD_OFFSET, uuid);
        encodeByte(initialFrame.content, REQUEST_SERIALIZATION_VERSION_FIELD_OFFSET, serializationVersion);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, clusterName);
        CodecUtil.encodeNullable(clientMessage, username, StringCodec::encode);
        CodecUtil.encodeNullable(clientMessage, password, StringCodec::encode);
        StringCodec.encode(clientMessage, clientType);
        StringCodec.encode(clientMessage, clientHazelcastVersion);
        StringCodec.encode(clientMessage, clientName);
        ListMultiFrameCodec.encode(clientMessage, labels, StringCodec::encode);
        return clientMessage;
    }

    public static ClientAuthenticationCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ClientMessage.ForwardFrameIterator iterator = clientMessage.frameIterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.uuid = decodeUUID(initialFrame.content, REQUEST_UUID_FIELD_OFFSET);
        request.serializationVersion = decodeByte(initialFrame.content, REQUEST_SERIALIZATION_VERSION_FIELD_OFFSET);
        request.clusterName = StringCodec.decode(iterator);
        request.username = CodecUtil.decodeNullable(iterator, StringCodec::decode);
        request.password = CodecUtil.decodeNullable(iterator, StringCodec::decode);
        request.clientType = StringCodec.decode(iterator);
        request.clientHazelcastVersion = StringCodec.decode(iterator);
        request.clientName = StringCodec.decode(iterator);
        request.labels = ListMultiFrameCodec.decode(iterator, StringCodec::decode);
        return request;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class ResponseParameters {

        /**
         * TODO DOC
         */
        public byte status;

        /**
         * TODO DOC
         */
        public @Nullable com.hazelcast.cluster.Address address;

        /**
         * Unique string identifying the connected client uniquely.
         */
        public @Nullable java.util.UUID uuid;

        /**
         * client side supported version to inform server side
         */
        public byte serializationVersion;

        /**
         * TODO DOC
         */
        public java.lang.String serverHazelcastVersion;

        /**
         * Partition count of the cluster.
         */
        public int partitionCount;

        /**
         * UUID of the cluster that the client authenticated.
         */
        public java.util.UUID clusterId;
    }

    public static ClientMessage encodeResponse(byte status, @Nullable com.hazelcast.cluster.Address address, @Nullable java.util.UUID uuid, byte serializationVersion, java.lang.String serverHazelcastVersion, int partitionCount, java.util.UUID clusterId) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        encodeByte(initialFrame.content, RESPONSE_STATUS_FIELD_OFFSET, status);
        encodeUUID(initialFrame.content, RESPONSE_UUID_FIELD_OFFSET, uuid);
        encodeByte(initialFrame.content, RESPONSE_SERIALIZATION_VERSION_FIELD_OFFSET, serializationVersion);
        encodeInt(initialFrame.content, RESPONSE_PARTITION_COUNT_FIELD_OFFSET, partitionCount);
        encodeUUID(initialFrame.content, RESPONSE_CLUSTER_ID_FIELD_OFFSET, clusterId);
        clientMessage.add(initialFrame);

        CodecUtil.encodeNullable(clientMessage, address, AddressCodec::encode);
        StringCodec.encode(clientMessage, serverHazelcastVersion);
        return clientMessage;
    }

    public static ClientAuthenticationCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ClientMessage.ForwardFrameIterator iterator = clientMessage.frameIterator();
        ResponseParameters response = new ResponseParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        response.status = decodeByte(initialFrame.content, RESPONSE_STATUS_FIELD_OFFSET);
        response.uuid = decodeUUID(initialFrame.content, RESPONSE_UUID_FIELD_OFFSET);
        response.serializationVersion = decodeByte(initialFrame.content, RESPONSE_SERIALIZATION_VERSION_FIELD_OFFSET);
        response.partitionCount = decodeInt(initialFrame.content, RESPONSE_PARTITION_COUNT_FIELD_OFFSET);
        response.clusterId = decodeUUID(initialFrame.content, RESPONSE_CLUSTER_ID_FIELD_OFFSET);
        response.address = CodecUtil.decodeNullable(iterator, AddressCodec::decode);
        response.serverHazelcastVersion = StringCodec.decode(iterator);
        return response;
    }

}
