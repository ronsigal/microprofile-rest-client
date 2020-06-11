/*
 * Copyright 2019 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.microprofile.rest.client.tck.timeout;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.rest.client.tck.WiremockArquillianTest;
import org.eclipse.microprofile.rest.client.tck.interfaces.SimpleGetApi;
import org.eclipse.microprofile.rest.client.tck.interfaces.SimpleGetApiWithConfigKey;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import javax.inject.Inject;

import static org.testng.Assert.assertTrue;

public class TimeoutViaMPConfigWithConfigKeyTest extends TimeoutTestBase {

    @Inject
    @RestClient
    private SimpleGetApiWithConfigKey api;

    @Deployment
    public static Archive<?> createDeployment() {
        String timeoutProps =
                "myConfigKey/mp-rest/uri=" + UNUSED_URL + System.lineSeparator() +
                "myConfigKey/mp-rest/connectTimeout=7000" + System.lineSeparator() +
                "myConfigKey/mp-rest/readTimeout=7000";
        StringAsset mpConfig = new StringAsset(timeoutProps);
        return ShrinkWrap.create(WebArchive.class, TimeoutViaMPConfigWithConfigKeyTest.class.getSimpleName()+".war")
            .addAsWebInfResource(mpConfig, "classes/META-INF/microprofile-config.properties")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(SimpleGetApi.class,
                        SimpleGetApiWithConfigKey.class,
                        TimeoutTestBase.class,
                        WiremockArquillianTest.class);
    }

    @Override
    protected SimpleGetApi getClientWithReadTimeout() {
        return api;
    }

    @Override
    protected SimpleGetApi getClientWithConnectTimeout() {
        return api;
    }

    @Override
    protected void checkTimeElapsed(long elapsed) {
        assertTrue(elapsed >= 7);
        // allow an extra 10 seconds cushion for slower test machines
        final long elapsedLimit = 5 + TIMEOUT_CUSHION;
        assertTrue(elapsed < elapsedLimit, "Elapsed time expected under " + elapsedLimit + " secs, but was " + elapsed + " secs.");
//        assertTrue(elapsed < 17);
    }
}
