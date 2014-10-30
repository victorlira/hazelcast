/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.client.cache.impl;

import com.hazelcast.cache.impl.AbstractHazelcastCachingProvider;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.util.ExceptionUtil;

import java.net.URI;
import java.net.URL;
import java.util.Properties;

/**
 * Provides client cachingProvider implementation.
 *
 * @see javax.cache.spi.CachingProvider
 */
public final class HazelcastClientCachingProvider extends AbstractHazelcastCachingProvider {

    public HazelcastClientCachingProvider() {
        super();
    }

    /**
     * Helper method for creating caching provider for testing etc
     * @param hazelcastInstance
     * @return HazelcastClientCachingProvider
     */
    public static HazelcastClientCachingProvider createCachingProvider(HazelcastInstance hazelcastInstance) {
        final HazelcastClientCachingProvider cachingProvider = new HazelcastClientCachingProvider();
        cachingProvider.hazelcastInstance = hazelcastInstance;
        return cachingProvider;
    }

    protected synchronized void initHazelcast() {
        //There is no HazelcastInstanceFactory for client so using synchronized
        if (hazelcastInstance == null) {
            hazelcastInstance = HazelcastClient.newHazelcastClient();
        }
    }

    @Override
    protected HazelcastClientCacheManager createHazelcastCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        final HazelcastInstance instance;
        //uri is null or default or a non hazelcast one, then we use the internal shared instance
        if (uri == null || uri.equals(getDefaultURI()) || !(HAZELCAST_CONFIG_URI_SCHEMA.equals(uri.getScheme())
                || HAZELCAST_NAME_URI_SCHEMA.equals(uri.getScheme()))) {
            if (hazelcastInstance == null) {
                initHazelcast();
            }
            instance = hazelcastInstance;
        } else if (HAZELCAST_NAME_URI_SCHEMA.equals(uri.getScheme())) {
            //named instance
            instance = HazelcastClient.getHazelcastClientByName(uri.getRawSchemeSpecificPart());
        } else {
            //it means that it is a Hazelcast config schema
            final String rawURLStr = uri.getRawSchemeSpecificPart();
            try {
                URL configURL = new URL(rawURLStr);
                final ClientConfig config = new XmlClientConfigBuilder(configURL).build();
                instance = HazelcastClient.newHazelcastClient(config);
            } catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        return new HazelcastClientCacheManager(this, instance, uri, classLoader, properties);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HazelcastClientCachingProvider{");
        sb.append("hazelcastInstance=").append(hazelcastInstance);
        sb.append('}');
        return sb.toString();
    }
}
