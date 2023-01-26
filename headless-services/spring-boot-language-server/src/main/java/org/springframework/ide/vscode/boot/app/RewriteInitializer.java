/*******************************************************************************
 * Copyright (c) 2023 VMware, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.app;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ide.vscode.boot.java.rewrite.RewriteRefactorings;
import org.springframework.ide.vscode.commons.languageserver.quickfix.QuickfixRegistry;
import org.springframework.ide.vscode.commons.languageserver.util.SimpleLanguageServer;

@Configuration(proxyBeanMethods = false)
public class RewriteInitializer implements InitializingBean {
	
	@Autowired
	private SimpleLanguageServer server;

	@Autowired(required = false)
	private RewriteRefactorings rewriteRefactorings;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (rewriteRefactorings != null) {
			QuickfixRegistry registry = server.getQuickfixRegistry();
			registry.register(RewriteRefactorings.REWRITE_RECIPE_QUICKFIX, rewriteRefactorings);
		}
	}

}
