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
package org.jboss.richfaces.integrationTest.dataTable;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jboss.arquillian.ajocado.dom.Event;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractDataIterationTestCase;
import org.testng.annotations.Test;

public class ModifiableDataModelTestCase extends AbstractDataIterationTestCase {

	private final JQueryLocator LOC_TH_KEY = jq(getLoc("TH_KEY"));
	private final String LOC_TD_KEY_PREFORMATTED = getLoc("TD_KEY_PREFORMATTED");
	private final JQueryLocator LOC_INPUT_ASSIGNEE = jq(getLoc("INPUT_ASSIGNEE"));
	private final String LOC_TD_ASSIGNEE_PREFORMATTED = getLoc("TD_ASSIGNEE_PREFORMATTED");

	private final String MSG_TAB_TO_OPEN = getMsg("TAB_TO_OPEN");
	private final String MSG_INPUT_ASSIGNEE = getMsg("INPUT_ASSIGNEE");
	private final String MSG_COUNT_ASSIGNEE_PAGES = getMsg("COUNT_ASSIGNEE_PAGES");
	private final String MSG_COUNT_ASSIGNEE_ROWS = getMsg("COUNT_ASSIGNEE_ROWS");
	private final String MSG_CLASS_ACTIVE_PAGE = getMsg("CLASS_ACTIVE_PAGE");

	/**
	 * Sort table by keys and checks that column key is sorted well on all pages
	 */
	@Test
	public void testSorting() {

		// clicks on "key" column-header to sort table by keys
		final String tableContentBeforeSorting = selenium.getText(LOC_TABLE_COMMON);

		selenium.click(LOC_TH_KEY);

		Wait.waitSelenium.failWith("Table content never changed").dontFail().until(new SeleniumCondition() {
			public boolean isTrue() {
				return !tableContentBeforeSorting.equals(selenium.getText(LOC_TABLE_COMMON));
			}
		});

		// check if keys on all pages are sorted in right way
		final int pageCount = selenium.getCount(jq(format(LOC_BUTTON_NUMBERED_PAGE_PREFORMATTED, 0)));

		String lastText = null; // remembers last cell text

		for (int page = 1; page <= pageCount; page++) {
			// switch to the wanted page
			final JQueryLocator locButtonPage = jq(format(LOC_BUTTON_NUMBERED_PAGE_PREFORMATTED, page));

			selenium.click(locButtonPage);

			Wait.waitSelenium.until(new SeleniumCondition() {
				public boolean isTrue() {
					return belongsClass(MSG_CLASS_ACTIVE_PAGE, locButtonPage);
				}
			});

			// count how many rows (cells in one column) table have
			final int cellCount = selenium.getCount(jq(format(LOC_TD_KEY_PREFORMATTED, 0)));

			// checks that columns are correctly sorted
			for (int row = 1; row <= cellCount; row++) {
				final JQueryLocator locCell = jq(format(LOC_TD_KEY_PREFORMATTED, row));

				String cellText = selenium.getText(locCell);

				if (lastText != null && cellText.compareToIgnoreCase(lastText) <= 0) {
					fail();
				}

				lastText = cellText;
			}
		}
	}

	/**
	 * Enter the search input to filter table and checks that only values
	 * containing this input is contained on all displayed pages
	 */
	@Test
	public void testFiltering() {

		// types a name to the assignee column input
		final String tableContentBeforeSorting = selenium.getText(LOC_TABLE_COMMON);

		selenium.type(LOC_INPUT_ASSIGNEE, MSG_INPUT_ASSIGNEE);
		selenium.fireEvent(LOC_INPUT_ASSIGNEE, Event.KEYUP);

		Wait.waitSelenium.failWith("Table content never changed").dontFail().until(new SeleniumCondition() {
			public boolean isTrue() {
				return !tableContentBeforeSorting.equals(selenium.getText(LOC_TABLE_COMMON));
			}
		});

		// check how many pages should be displayed
		int pageCount = selenium.getCount(jq(format(LOC_BUTTON_NUMBERED_PAGE_PREFORMATTED, 0)));
		assertEquals(pageCount, Integer.parseInt(MSG_COUNT_ASSIGNEE_PAGES));

		// checks how many columns should be displayed
		int cellCount = selenium.getCount(jq(format(LOC_TD_KEY_PREFORMATTED, 0)));
		assertEquals(cellCount, Integer.parseInt(MSG_COUNT_ASSIGNEE_ROWS));

		// checks that assignee column contains assignee name
		String assigneeName = selenium.getText(jq(LOC_TD_ASSIGNEE_PREFORMATTED)); // JJa ? Is preformated locator correct locator? 
		assertTrue(assigneeName.contains(MSG_INPUT_ASSIGNEE));
	}

	protected void loadPage() {
		openComponent("Data Table");
		openTab(MSG_TAB_TO_OPEN);
		scrollIntoView(LOC_TABLE_COMMON, true);
	}
}
