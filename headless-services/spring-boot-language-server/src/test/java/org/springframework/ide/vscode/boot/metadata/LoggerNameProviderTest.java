/*******************************************************************************
 * Copyright (c) 2017, 2019 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ide.vscode.commons.maven.java.MavenJavaProject;
import org.springframework.ide.vscode.project.harness.ProjectsHarness;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Kris De Volder
 */
public class LoggerNameProviderTest {

	private static final String[] JBOSS_RESULTS = {
			// org.jboss is not really a package...
//			"org.jboss", //1
			"org.jboss.logging", //2
			"org.jboss.logging.JBossLogManagerLogger", //3
			"org.jboss.logging.JBossLogManagerProvider", //4
			"org.jboss.logging.JBossLogRecord", //5
			"org.springframework.instrument.classloading.jboss", //6
			"org.springframework.instrument.classloading.jboss.JBossClassLoaderAdapter", //7
			"org.springframework.instrument.classloading.jboss.JBossLoadTimeWeaver", //8
			"org.springframework.instrument.classloading.jboss.JBossMCAdapter", //9
			"org.springframework.instrument.classloading.jboss.JBossMCTranslatorAdapter", //10
			"org.springframework.instrument.classloading.jboss.JBossModulesAdapter" //11
	};

	private ProjectsHarness projects = ProjectsHarness.INSTANCE;
	private MavenJavaProject project;

	@BeforeEach
	public void setup() throws Exception {
		CachingValueProvider.TIMEOUT = Duration.ofSeconds(20);
//		System.out.println("Setting test up.");
		project = projects.mavenProject("tricky-getters-boot-1.3.1-app");
//		System.out.println("Done setting test up: ");
	}

	@AfterEach
	public void teardown() throws Exception {
		CachingValueProvider.restoreDefaults();
	}

    @Test
    void directResults() throws Exception {
//		System.out.println("directResults() - start");
        LoggerNameProvider p = create();
        String query = "jboss";
        List<String> directQueryResults = getResults(p, query);

//		dumpResults("jboss - DIRECT", directQueryResults);

        /*
         * Commented out due to search results from JDK present
         */
        //		assertElements(directQueryResults, JBOSS_RESULTS);
        assertElementsAtLeast(directQueryResults, JBOSS_RESULTS);
//		System.out.println("directResults() - end");
    }

    @Test
    void cachedResults() throws Exception {
//		System.out.println("cachedResults() - start");
        LoggerNameProvider p = create();
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            String query = "jboss";
            List<String> directQueryResults = getResults(p, query);

            /*
             * Commented out due to search results from JDK present
             */
//			assertElements(directQueryResults, JBOSS_RESULTS);
            assertElementsAtLeast(directQueryResults, JBOSS_RESULTS);

            long duration = System.currentTimeMillis() - startTime;
//            System.out.println(i + ": " + duration + " ms");
        }
//		System.out.println("cachedResults() - end");
    }

    @Test
    void incrementalResults() throws Exception {
//		System.out.println("incrementalResults() - start");
        String fullQuery = "jboss";

        LoggerNameProvider p = create();
        for (int i = 0; i <= fullQuery.length(); i++) {
            String query = fullQuery.substring(0, i);
            List<String> results = getResults(p, query);
//			dumpResults(query, results);
            if (i == fullQuery.length()) {
//                System.out.println("Verifying final result!");
                //Not checking for exact equals because... quircks of JDT search engine means it
                // will actually finds less results than if we derive them by filtering incrementally.
                //If all works well, we should never find fewer results than Eclipse does.
                assertElementsAtLeast(results, JBOSS_RESULTS);
            }
        }
//		System.out.println("incrementalResults() - end");
    }

	private LoggerNameProvider create() {
		return (LoggerNameProvider) LoggerNameProvider.factory(null, null).apply(ImmutableMap.of());
	}

	private void assertElementsAtLeast(List<String> results, String[] expecteds) {
		Set<String> actuals = ImmutableSet.copyOf(results);
		StringBuilder missing = new StringBuilder();
		boolean hasMissing = false;
		for (String e : expecteds) {
			if (!actuals.contains(e)) {
				missing.append(e+"\n");
				hasMissing = true;
			}
		}
		assertFalse(hasMissing, "Missing elements:\n"+missing);
	}

	@SuppressWarnings("unused")
	private void assertElements(List<String> _actual, String... _expected) {
		String expected = toSortedString(Arrays.asList(_expected));
		String actual = toSortedString(_actual);
		assertEquals(expected, actual);
	}

	private String toSortedString(List<String> list) {
		ArrayList<String> sorted = new ArrayList<>(list);
		Collections.sort(sorted);
		StringBuilder buf = new StringBuilder();
		for (String string : sorted) {
			buf.append(string+"\n");
		}
		return buf.toString();
	}

	private List<String> getResults(LoggerNameProvider p, String query) {
		return p.getValues(project, query).toStream()
		.map((h) -> h.getValue().toString())
		.collect(Collectors.toList());
	}

	@SuppressWarnings("unused")
	private void dumpResults(String string, Collection<String> r) {
		System.out.println(">>> "+string);
		String[] strings = r.toArray(new String[r.size()]);
		Arrays.sort(strings);
		int i = 0;
		for (String s : strings) {
			System.out.println("\""+s+"\", //"+(++i));
//			System.out.println((++i)+" : "+s);
		}
		System.out.println("<<< "+string);
	}

}
