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
package org.jboss.richfaces.integrationTest.tooltip;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.arquillian.ajocado.geometry.Point;
import org.jboss.arquillian.ajocado.javascript.JavaScript;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumRetriever;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class TooltipTestCase extends AbstractSeleniumRichfacesTestCase {

	private final String LOC_FIELDSET_HEADER_1 = getLoc("FIELDSET_HEADER_1");
	private final JQueryLocator LOC_PANEL_SAMPLE_1 = jq(getLoc("PANEL_SAMPLE_1"));
	private final JQueryLocator LOC_PANEL_SAMPLE_2 = jq(getLoc("PANEL_SAMPLE_2"));
	private final JQueryLocator LOC_PANEL_SAMPLE_3 = jq(getLoc("PANEL_SAMPLE_3"));
	private final JQueryLocator LOC_PANEL_SAMPLE_4 = jq(getLoc("PANEL_SAMPLE_4"));
	private final JQueryLocator LOC_SPAN_TOOLTIPS_REQUESTED = jq(getLoc("SPAN_TOOLTIPS_REQUESTED"));

	private final Point COORDS_AT_PANEL = new Point(5,5);
	private final String MSG_OUTPUT_TOOLTIP_1 = getMsg("OUTPUT_TOOLTIP_1");
	private final String MSG_OUTPUT_TOOLTIP_2 = getMsg("OUTPUT_TOOLTIP_2");
	private final String MSG_OUTPUT_TOOLTIP_3_4_WAITING = getMsg("OUTPUT_TOOLTIP_3_4_WAITING");
	private final String MSG_OUTPUT_TOOLTIP_3_4_TEXT = getMsg("OUTPUT_TOOLTIP_3_4_TEXT");
	private final Pattern MSG_REGEXP_TOOLTIPS_REQUESTED = Pattern.compile(getMsg("REGEXP_TOOLTIPS_REQUESTED"));

	final String jqFindTooltip = "jqFind('{0}:visible:textEquals({1})')";
	final String jqFindTooltipWait = format(jqFindTooltip, "strong", MSG_OUTPUT_TOOLTIP_3_4_WAITING);
	final String jqFindTooltipText = format(jqFindTooltip, "span", MSG_OUTPUT_TOOLTIP_3_4_TEXT);

	final String conditionTooltipWaitAppears = format("{0}.size() > 0", format(jqFindTooltip, "strong",
			MSG_OUTPUT_TOOLTIP_3_4_WAITING));
	final String conditionTooltipTextAppears = format("{0}.size() > 0", format(jqFindTooltip, "span",
			MSG_OUTPUT_TOOLTIP_3_4_TEXT));
	final String conditionTooltipTextDisappears = format("{0}.size() == 0", format(jqFindTooltip, "span",
			MSG_OUTPUT_TOOLTIP_3_4_TEXT));

	/**
	 * Hover mouse at first panel and so invokes tooltip displaying. Checks that
	 * the tooltip text will display and next hover mouse out of panel to close
	 * tooltip. Do this all above three times.
	 */
	@Test
	public void testDefaultToolTip() {

		for (int i = 0; i < 3; i++) {
			assertFalse(selenium.isTextPresent(MSG_OUTPUT_TOOLTIP_1));

			if (i == 0) /*
						 * satisfies that mouseOverAt will work as expected -
						 * without this mouseOverAt do nothing
						 */
				selenium.mouseMoveAt(LOC_PANEL_SAMPLE_1, COORDS_AT_PANEL);
			mouseOverAt(LOC_PANEL_SAMPLE_1, COORDS_AT_PANEL);

			waitForText(MSG_OUTPUT_TOOLTIP_1);

			selenium.mouseOut(LOC_PANEL_SAMPLE_1);
			waitForTextDisappears(MSG_OUTPUT_TOOLTIP_1);
		}
	}

	/**
	 * Hover mouse at second panel (top right) and so invokes deplayed tooltip
	 * displaying. Checks that expected text will display and then hover mouse
	 * out of panel to close tooltip. Do this all above three times.
	 */
	@Test
	public void testFollowMouseDelayed() {

		for (int i = 0; i < 3; i++) {
			assertFalse(selenium.isTextPresent(MSG_OUTPUT_TOOLTIP_2));

			if (i == 0)
				selenium.mouseMoveAt(LOC_PANEL_SAMPLE_2, COORDS_AT_PANEL);
			mouseOverAt(LOC_PANEL_SAMPLE_2, COORDS_AT_PANEL);

			waitForText(MSG_OUTPUT_TOOLTIP_2);

			selenium.mouseOut(LOC_PANEL_SAMPLE_2);
			waitForTextDisappears(MSG_OUTPUT_TOOLTIP_2);
		}
	}

	/**
	 * Hover mouse at third (bottom left) panel and so invokes server rendered
	 * tooltip diplaying. Checks that expected text will display and then hover
	 * mouse out to close tooltip. Do all above three times and checks that
	 * tooltips requested counter is counting right.
	 */
	// FIXME works locally but not in Hudson, fails on line 128 -- waitForTooltipChanges(tooltipsRequested, i == 0)
	//@Test
	public void testSeparateServerRequests() {
		Integer tooltipsRequested = null;

		for (int i = 0; i < 3; i++) {
			assertEquals(selenium.getEval(new JavaScript(format("{0}.size()", jqFindTooltipWait))), 0l);
			assertEquals(selenium.getEval(new JavaScript(format("{0}.size()", jqFindTooltipText))), 0l);

			if (i == 0)
				selenium.mouseMoveAt(LOC_PANEL_SAMPLE_3, COORDS_AT_PANEL);
			mouseOverAt(LOC_PANEL_SAMPLE_3, COORDS_AT_PANEL);
			tooltipsRequested = waitForTooltipChanges(tooltipsRequested, i == 0);

			selenium.mouseOut(LOC_PANEL_SAMPLE_3);
			selenium.waitForCondition(new JavaScript(conditionTooltipTextDisappears), 3000l);
		}
	}

	/**
	 * Click at third (bottom left) panel and so invokes server rendered
	 * tooltip diplaying. Checks that expected text will display and then hover
	 * mouse out to close tooltip. Do all above three times and checks that
	 * tooltips requested counter is counting right.
	 */
	@Test
	public void testMouseClickActivation() {
		Integer tooltipsRequested = null;

		for (int i = 0; i < 3; i++) {
			assertEquals(selenium.getEval(new JavaScript(format("{0}.size()", jqFindTooltipWait))), 0l);
			assertEquals(selenium.getEval(new JavaScript(format("{0}.size()", jqFindTooltipText))), 0l);

			selenium.clickAt(LOC_PANEL_SAMPLE_4, COORDS_AT_PANEL);
			tooltipsRequested = waitForTooltipChanges(tooltipsRequested, i == 0);
			
			selenium.mouseOut(LOC_PANEL_SAMPLE_4);
			selenium.waitForCondition(new JavaScript(conditionTooltipTextDisappears), 3000l);
		}
	}

	private Integer waitForTooltipChanges(Integer tooltipsRequestedOld, boolean firstLoop) {
		Integer tooltipsRequested = null;

		if (firstLoop) {
			selenium.waitForCondition(new JavaScript(conditionTooltipWaitAppears), 3000l);
			selenium.waitForCondition(new JavaScript(conditionTooltipTextAppears), 3000l);

			tooltipsRequested = retrieveRequestedTooltips.retrieve();
		} else {
			selenium.waitForCondition(new JavaScript(conditionTooltipTextAppears), 3000l);
			
			tooltipsRequested = Wait.waitSelenium.waitForChangeAndReturn(tooltipsRequestedOld, retrieveRequestedTooltips);
			assertEquals(tooltipsRequested, Integer.valueOf(tooltipsRequestedOld + 1));
		}

		return tooltipsRequested;
	}

	private SeleniumRetriever<Integer> retrieveRequestedTooltips = new SeleniumRetriever<Integer>() {
	    Integer val;
		public Integer retrieve() {
			String text = Wait.waitSelenium.interval(10).timeout(2000).waitForChangeAndReturn(null, new SeleniumRetriever<String>() {
                String val;
				public String retrieve() {
				    String retrieved = getTextOrNull(LOC_SPAN_TOOLTIPS_REQUESTED);
					return retrieved;
				}

                public void initializeValue() {
                    val = retrieve();
                }

                public void setValue(String value) {
                    val = value;                    
                }

                public String getValue() {
                    return val;
                }
			});
			Matcher matcher = MSG_REGEXP_TOOLTIPS_REQUESTED.matcher(text);
			if (!matcher.matches()) {
				fail();
			}
			return Integer.valueOf(text);
		}

        public void initializeValue() {
            val = retrieve();
        }

        public void setValue(Integer value) {
            val = value;
        }

        public Integer getValue() {
            return val;
        }
	};

	private void waitForTextDisappears(final String text) {
		Wait.waitSelenium.until(new SeleniumCondition() {
			public boolean isTrue() {
				return !selenium.isElementPresent(jq(text));
			}
		});
	}

	protected void loadPage() {
		openComponent("ToolTip");
		openTab("Usage");
		scrollIntoView(LOC_FIELDSET_HEADER_1, true);
		selenium.allowNativeXpath(true);
	}
}
