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
package org.jboss.richfaces.integrationTest.style;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertEquals;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.arquillian.ajocado.css.CssProperty;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.utils.ColorUtils;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class StyleTestCase extends AbstractSeleniumRichfacesTestCase {

	private String LOC_FIELDSET_HEADER = getLoc("FIELDSET_HEADER");
	private String LOC_LINK_DEEP_MARINE = getLoc("LINK_DEEP_MARINE");
	private String LOC_LINK_BLUE_SKY = getLoc("LINK_BLUE_SKY");
	private String LOC_LINK_JAPAN_CHERRY = getLoc("LINK_JAPAN_CHERRY");
	private JQueryLocator LOC_PANEL = jq(getLoc("PANEL"));

	private String MSG_COLOR_DEEP_MARINE = getMsg("COLOR_DEEP_MARINE");
	private String MSG_COLOR_BLUE_SKY = getMsg("COLOR_BLUE_SKY");
	private String MSG_COLOR_JAPAN_CHERRY = getMsg("COLOR_JAPAN_CHERRY");

	Map<String, String> relation = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = 1576758688687867261L;
		{
			put(LOC_LINK_DEEP_MARINE, MSG_COLOR_DEEP_MARINE);
			put(LOC_LINK_BLUE_SKY, MSG_COLOR_BLUE_SKY);
			put(LOC_LINK_JAPAN_CHERRY, MSG_COLOR_JAPAN_CHERRY);
		}
	};

	/**
	 * Clicks on the skin names and waits for background-color changes
	 * appropriately.
	 */
	@Test
	public void testBackgroundColor() {
		for (final String locLink : relation.keySet()) {
			final String msgColor = relation.get(locLink);

			selenium.click(jq(locLink));
			selenium.waitForPageToLoad(Wait.DEFAULT_TIMEOUT);
			scrollIntoView(LOC_FIELDSET_HEADER, true);

			Color expected = ColorUtils.convertToAWTColor(msgColor);
			Color actual = ColorUtils.convertToAWTColor(getStyle(LOC_PANEL, CssProperty.BACKGROUND_COLOR));
			assertEquals(actual, expected, format("background-color for '{0}' skin does not match", selenium
					.getText(jq(locLink))));
		}
	}

	protected void loadPage() {
		openComponent("Style");

		scrollIntoView(LOC_FIELDSET_HEADER, true);
	}
}
