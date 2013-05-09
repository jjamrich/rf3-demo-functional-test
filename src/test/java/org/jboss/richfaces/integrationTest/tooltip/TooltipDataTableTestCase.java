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

import org.jboss.arquillian.ajocado.geometry.Point;
import org.jboss.arquillian.ajocado.javascript.JavaScript;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class TooltipDataTableTestCase extends AbstractSeleniumRichfacesTestCase {

	private final JQueryLocator LOC_FIELDSET_HEADER_2 = jq(getLoc("FIELDSET_HEADER_2"));
	private final String LOC_TD_PREFORMATTED = getLoc("TD_PREFORMATTED");
	private final JQueryLocator LOC_OUTPUT_TOOLTIP_MAKE = jq(getLoc("OUTPUT_TOOLTIP_MAKE"));
    private final JQueryLocator LOC_OUTPUT_TOOLTIP_MODEL = jq(getLoc("OUTPUT_TOOLTIP_MODEL"));
    private final JQueryLocator LOC_OUTPUT_TOOLTIP_YEAR = jq(getLoc("OUTPUT_TOOLTIP_YEAR"));
	
    private final String LOC_TD_MAKE_RELATIVE_TO_LABEL = getLoc("TD_MAKE_RELATIVE_TO_LABEL");
	private final String LOC_TD_MAKE_RELATIVE_TO_ACTIVE_TOOLTIP_AREA = getLoc("TD_MAKE_RELATIVE_TO_ACTIVE_TOOLTIP_AREA");

	private final Point COORDS_FOR_TABLE = new Point(2, 2);
	
	// FIXME test works locally but not in Hudson, fails on line 61 -- waitForElementAppears(LOC_OUTPUT_TOOLTIP_MAKE)
	//@Test
	public void testIterateThroughTable() {
		int rows = getJQueryCount(format(LOC_TD_PREFORMATTED, 0, Column.MAKE));

		for (int row = 1; row <= rows; row++) {
		    final JQueryLocator locCellMake = jq(format(LOC_TD_PREFORMATTED, row, Column.MAKE));
			final JQueryLocator locCellModel = jq(format(LOC_TD_PREFORMATTED, row, Column.MODEL));
			final JQueryLocator locCellYear = jq(format(LOC_TD_PREFORMATTED, row, Column.YEAR));
			final JQueryLocator locActiveTooltipArea = jq(format(LOC_TD_MAKE_RELATIVE_TO_ACTIVE_TOOLTIP_AREA, locCellMake));

			if (row == 1)
				selenium.mouseMoveAt(locActiveTooltipArea, COORDS_FOR_TABLE);
			mouseOverAt(locActiveTooltipArea, COORDS_FOR_TABLE);
			waitForElementAppears(LOC_OUTPUT_TOOLTIP_MAKE);

			assertEquals(selenium.getText(LOC_OUTPUT_TOOLTIP_MAKE), selenium.getText(jq(format(
					LOC_TD_MAKE_RELATIVE_TO_LABEL, locCellMake))));
			assertEquals(selenium.getText(LOC_OUTPUT_TOOLTIP_MODEL), selenium.getText(locCellModel));
			assertEquals(selenium.getText(LOC_OUTPUT_TOOLTIP_YEAR), selenium.getText(locCellYear));

			selenium.mouseOut(locActiveTooltipArea);
			waitForElementDisappears(LOC_OUTPUT_TOOLTIP_MAKE);
		}
	}

	private static class Column {
		public static final int MAKE = 0;
		public static final int MODEL = 1;
		public static final int YEAR = 2;
	}

	private void waitForElementAppears(final JQueryLocator locator) {
		Wait.waitSelenium.until(new SeleniumCondition() {
			public boolean isTrue() {
				return isElementPresent(locator);
			}
		});
	}
	
	private void waitForElementDisappears(final JQueryLocator locator) {
		Wait.waitSelenium.until(new SeleniumCondition() {
			public boolean isTrue() {
				return !isElementPresent(locator);
			}
		});
	}
	
	private boolean isElementPresent(JQueryLocator locator) {
		return !"0".equals(selenium.getEval(new JavaScript(format("jqFind('{0}:visible').size()", locator.getRawLocator()).trim())));
	}

	protected void loadPage() {
		openComponent("ToolTip");
		openTab("Use ToolTip with DataTable");
		scrollIntoView(LOC_FIELDSET_HEADER_2, true);
		selenium.allowNativeXpath(true);
	}
}
