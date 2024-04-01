/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.microprofile.test.config;

import java.net.URL;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.microprofile.test.config.resource.OptionalConfigPropertyInjectionResource;
import org.jboss.resteasy.microprofile.test.util.TestEnvironment;
import org.jboss.resteasy.setup.SystemPropertySetupTask;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for injection of optional MicroProfile Config properties.
 * @tpSince RESTEasy 4.6.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(OptionalConfigPropertyInjectionTest.PropertySetupTask.class)
public class OptionalConfigPropertyInjectionTest {

    public static class PropertySetupTask extends SystemPropertySetupTask {
        public PropertySetupTask() {
            super(Map.of(OptionalConfigPropertyInjectionResource.PRESENT_OPTIONAL_PROPERTY_NAME,
                    OptionalConfigPropertyInjectionResource.OPTIONAL_PROPERTY_VALUE));
        }
    }

    private static Client client;

    @ArquillianResource
    private URL url;

    @Deployment
    public static Archive<?> deploy() {
        return TestEnvironment.createWar(OptionalConfigPropertyInjectionTest.class)
                .addClasses(OptionalConfigPropertyInjectionResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails This test checks injection of optional config properties when:
     *                - optional property does not exist
     *                - optional property exists
     * @tpSince RESTEasy 4.6.0
     */
    @Test
    public void testOptionalPropertiesInjection() throws Exception {

        String missingOptionalPropertyValue = client.target(
                TestEnvironment.generateUri(url, "test-app",
                        OptionalConfigPropertyInjectionResource.MISSING_OPTIONAL_PROPERTY_PATH))
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get(String.class);
        Assertions.assertNull(missingOptionalPropertyValue);

        String presentOptionalPropertyValue = client.target(
                TestEnvironment.generateUri(url, "test-app",
                        OptionalConfigPropertyInjectionResource.PRESENT_OPTIONAL_PROPERTY_PATH))
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get(String.class);
        Assertions.assertEquals(OptionalConfigPropertyInjectionResource.OPTIONAL_PROPERTY_VALUE, presentOptionalPropertyValue);
    }

}
