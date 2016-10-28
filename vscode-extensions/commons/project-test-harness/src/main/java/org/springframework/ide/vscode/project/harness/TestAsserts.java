package org.springframework.ide.vscode.project.harness;

import static org.junit.Assert.fail;

public class TestAsserts {

	public static void assertContains(String needle, String haystack) {
		if (haystack==null || !haystack.contains(needle)) {
			fail("Not found: "+needle+"\n in \n"+haystack);
		}
	}

}
