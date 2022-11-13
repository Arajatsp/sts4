/*******************************************************************************
 * Copyright (c) 2020 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.validation.generations.json;

public class Links {

	private Link self;
	private Link releases;
	private Link generations;
	private Link parent;
	private Link project;
	private Link repository;

	public Link getSelf() {
		return self;
	}

	public Link getReleases() {
		return releases;
	}

	public Link getGenerations() {
		return generations;
	}

	public Link getParent() {
		return parent;
	}

	public Link getProject() {
		return project;
	}
	
	public Link getRepository() {
		return repository;
	}
}
