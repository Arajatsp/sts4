/*******************************************************************************
 * Copyright (c) 2022 VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.commons.protocol.java;

public record ProjectBuild(String type, String buildFile)  {
	
	public static final String MAVEN_PROJECT_TYPE = "maven";
	public static final String GRADLE_PROJECT_TYPE = "gradle";
	
	public static ProjectBuild createMavenBuild(String buildFile) {
		return new ProjectBuild(MAVEN_PROJECT_TYPE, buildFile);
	}
	
	public static ProjectBuild createGradleBuild(String buildFile) {
		return new ProjectBuild(GRADLE_PROJECT_TYPE, buildFile);
	}
	
	@Override
	public String toString() {
		return "ProjectBuild [type=" + type + ", buildFile=" + buildFile + "]";
	}
	
	

}
