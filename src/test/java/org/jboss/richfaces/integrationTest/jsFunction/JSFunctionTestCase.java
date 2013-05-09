/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.jboss.richfaces.integrationTest.jsFunction;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.jboss.arquillian.ajocado.dom.Event;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class JSFunctionTestCase extends AbstractSeleniumRichfacesTestCase {
	private final String LOC_SPAN_HOVER_ACTIVATED = getLoc("SPAN_HOVER_ACTIVATED");
	private final JQueryLocator LOC_OUTPUT_NAME = jq(getLoc("OUTPUT_NAME"));

	private final String[] MSG_NAMES = StringUtils.split(getMsg("NAMES"), ',');

	/**
	 * Hovers over all of the hover activated spans and waits for output name
	 * changed right followed by hovering out and waiting for output will be
	 * blank.
	 */
	@Test
	public void testHoveringNames() {
		for (final String msgName : MSG_NAMES) {
			final JQueryLocator span = jq(format(LOC_SPAN_HOVER_ACTIVATED, msgName));

			assertTrue(StringUtils.isBlank(selenium.getText(LOC_OUTPUT_NAME)), "Output name should be blank");

			selenium.fireEvent(span, Event.MOUSEOVER);

			Wait.waitSelenium.failWith(format("Output name never changed to '{0}'", msgName)).until(new SeleniumCondition() {
				public boolean isTrue() {
					return msgName.equals(selenium.getText(LOC_OUTPUT_NAME));
				}
			});

			selenium.fireEvent(span, Event.MOUSEOUT);

			Wait.waitSelenium.failWith("Output name never changed to blank").until(new SeleniumCondition() {
				public boolean isTrue() {
					return StringUtils.isBlank(selenium.getText(LOC_OUTPUT_NAME));
				}
			});
		}
	}

	protected void loadPage() {
		openComponent("JS Function");

	}
}
