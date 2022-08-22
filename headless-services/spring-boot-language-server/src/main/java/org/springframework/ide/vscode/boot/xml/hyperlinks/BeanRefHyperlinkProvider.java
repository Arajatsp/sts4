/*******************************************************************************
 * Copyright (c) 2019, 2021 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.xml.hyperlinks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.lemminx.dom.DOMAttr;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.WorkspaceSymbol;
import org.springframework.ide.vscode.boot.app.SpringSymbolIndex;
import org.springframework.ide.vscode.boot.java.beans.BeansSymbolAddOnInformation;
import org.springframework.ide.vscode.boot.java.handlers.EnhancedSymbolInformation;
import org.springframework.ide.vscode.boot.java.handlers.SymbolAddOnInformation;
import org.springframework.ide.vscode.commons.java.IJavaProject;
import org.springframework.ide.vscode.commons.languageserver.java.JavaProjectFinder;
import org.springframework.ide.vscode.commons.util.text.TextDocument;

/**
 * @author Alex Boyko
 */
public class BeanRefHyperlinkProvider implements XMLHyperlinkProvider {
	
	private final JavaProjectFinder projectFinder;
	private final SpringSymbolIndex symbolIndex;

	public BeanRefHyperlinkProvider(JavaProjectFinder projectFinder, SpringSymbolIndex symbolIndex) {
		this.projectFinder = projectFinder;
		this.symbolIndex = symbolIndex;
	}

	@Override
	public Location getDefinition(TextDocument doc, String namespace, DOMNode node, DOMAttr attributeAt) {
		Optional<IJavaProject> foundProject = this.projectFinder.find(doc.getId());
		if (foundProject.isPresent()) {
			final IJavaProject project = foundProject.get();
			String projectLocation = project.getLocationUri() != null ? project.getLocationUri().toString() : "";
			
			// make sure the project and the symbol location share the same prefix "file:///"
			// looks like project locations are containing a "file:/" only
			if (!projectLocation.startsWith("file:///")) {
				projectLocation = "file:///" + projectLocation.substring("file:/".length());
			}
			
			// make sure that only exact project locations are matched
			if (!projectLocation.endsWith("/")) {
				projectLocation = projectLocation + "/";
			}
			
			List<WorkspaceSymbol> symbols = symbolIndex.getSymbols(data -> symbolsFilter(data, attributeAt.getValue())).collect(Collectors.toList());
			if (!symbols.isEmpty()) {
				for (WorkspaceSymbol symbol : symbols) {
					if (symbol.getLocation().isLeft()) {
						Location location = symbol.getLocation().getLeft();
						String uri = location.getUri();
						
						if (uri != null && uri.startsWith(projectLocation)) {
							return location;
						}
					}
				}
				// TODO: need better handling for the WorkspaceSymbolLocation case
				for (WorkspaceSymbol symbol : symbols) {
					if (symbol.getLocation().isLeft()) {
						return symbol.getLocation().getLeft();
					}
				}
			}
		}
		return null;
	}
	
	private boolean symbolsFilter(EnhancedSymbolInformation data, String beanId) {
		SymbolAddOnInformation[] additionalInformation = data.getAdditionalInformation();
		if (beanId != null && additionalInformation != null) {
			for (SymbolAddOnInformation info : additionalInformation) {
				if (info instanceof BeansSymbolAddOnInformation) {
					return beanId.equals(((BeansSymbolAddOnInformation)info).getBeanID());
				}
			}
		}
		return false;
	}

}
