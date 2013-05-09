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
package org.jboss.richfaces.integrationTest;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.jboss.arquillian.ajocado.Graphene;
import org.jboss.arquillian.ajocado.javascript.JavaScript;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public abstract class AbstractDataIterationTestCase extends AbstractSeleniumRichfacesTestCase {

	protected final JQueryLocator LOC_TABLE_COMMON = jq(getLoc("data-table-common--table"));
	protected final JQueryLocator LOC_BUTTON_FIRST_PAGE = jq(getLoc("data-scroller--button--first-page"));
	protected final JQueryLocator LOC_BUTTON_LAST_PAGE = jq(getLoc("data-scroller--button--last-page"));
	protected final JQueryLocator LOC_BUTTON_NEXT_PAGE = jq(getLoc("data-scroller--button--next-page"));
	protected final JQueryLocator LOC_BUTTON_PREVIOUS_PAGE = jq(getLoc("data-scroller--button--previous-page"));
	protected final String LOC_BUTTON_NUMBERED_PAGE_PREFORMATTED = getLoc("data-scroller--button--numbered-page-preformatted");
	protected final JQueryLocator LOC_OUTPUT_ACTIVE_PAGE = jq(getLoc("data-scroller--output--active-page"));

	/**
	 * Overwritten loading of locator properties from files bounded to test
	 * cases
	 */
	protected Properties getMessagesProperties() {
		Properties packageProperties = super.getMessagesProperties();

		Properties dataIterationCommonProperties = getNamedPropertiesForClass(AbstractSeleniumRichfacesTestCase.class,
				"data-iteration--messages");

		Properties classSpecificProperties = getNamedPropertiesForClass(this.getClass(), format("messages-{0}", this
				.getClass().getSimpleName()));

		Properties result = new Properties();
		result.putAll(dataIterationCommonProperties);
		result.putAll(packageProperties);
		result.putAll(classSpecificProperties);

		return result;
	}

	/**
	 * Overwritten loading of message properties from files bounded to test
	 * cases
	 */
	protected Properties getLocatorsProperties() {
		Properties packageProperties = super.getLocatorsProperties();

		Properties dataIterationCommonProperties = getNamedPropertiesForClass(AbstractSeleniumRichfacesTestCase.class,
				"data-iteration--locators");

		Properties classSpecificProperties = getNamedPropertiesForClass(this.getClass(), format("locators-{0}", this
				.getClass().getSimpleName()));

		Properties result = new Properties();
		result.putAll(dataIterationCommonProperties);
		result.putAll(packageProperties);
		result.putAll(classSpecificProperties);

		return result;
	}

	/**
	 * Use specified button to load required page
	 * 
	 * @param button
	 *            one of the defined buttons to control page movement
	 */
	protected void gotoPage(String button) {
		gotoPage(jq(button));
	}

	protected void gotoPage(JQueryLocator button) {
        final String previousPage = getActivePage().toString();
        
        if (previousPage.equals(selenium.getText(button))) {
            return;
        }

        if (previousPage.equals("1")
                && (LOC_BUTTON_FIRST_PAGE.getRawLocator().equals(button.getRawLocator()) 
                    || LOC_BUTTON_PREVIOUS_PAGE.getRawLocator().equals(button.getRawLocator()))) {
            return;
        }
        
        if (previousPage.equals(getLastVisiblePage().toString())
                && (LOC_BUTTON_LAST_PAGE.equals(button) || LOC_BUTTON_NEXT_PAGE.equals(button))) {
            return;
        }
        // move to specified page
        selenium.click(button);
        
        Graphene.waitModel.until(new SeleniumCondition() {
            
            public boolean isTrue() {
                return !previousPage.equals(getActivePage().toString());
            }
        });
    }

	/**
	 * Get a active page number
	 * 
	 * @return number of active page
	 */
	protected Integer getActivePage() {
		selenium.waitForCondition(new JavaScript(format("selenium.isElementPresent('{0}')", LOC_OUTPUT_ACTIVE_PAGE)), 5000);
		return Integer.valueOf(selenium.getText(LOC_OUTPUT_ACTIVE_PAGE));
	}

	/**
	 * Get a number of last page visible on page control
	 * 
	 * @return number of last page visible on page control
	 */
	protected Integer getLastVisiblePage() {
		Number pages = selenium.getCount(jq(format(LOC_BUTTON_NUMBERED_PAGE_PREFORMATTED, -1))) - 6;
		String lastVisiblePage = selenium.getText(jq(format(LOC_BUTTON_NUMBERED_PAGE_PREFORMATTED, pages.intValue())));
		return Integer.valueOf(lastVisiblePage);
	}

	/**
	 * Get text content of common table
	 * 
	 * @return text content of common table
	 */
	protected String getTableText() {
		return selenium.getText(LOC_TABLE_COMMON);
	}

	/**
	 * Checks sorting behaviour of specified column
	 * 
	 * @param columnPreformatted
	 *            column which should be used to sorting table
	 */
	protected void checkSorting(String columnPreformatted) {
		checkSortingForColumnOrder(columnPreformatted);
	}

	/**
	 * Checks sorting behaviour of specified columns in order of precedence
	 * 
	 * @param columnsPreformatted
	 *            columns in order of precedence which should be used to sorting
	 *            table
	 */
	protected void checkSortingForColumnOrder(String... columnsPreformatted) {
		final boolean navigationEnabled = selenium.isElementPresent(LOC_BUTTON_FIRST_PAGE);

		if (navigationEnabled)
			gotoPage(LOC_BUTTON_FIRST_PAGE);

		final int columns = columnsPreformatted.length;

		String[] lastText = new String[columns];
		Boolean[] sortedAscending = new Boolean[columns];

		while ((!navigationEnabled && lastText[0] == null)
				|| (navigationEnabled && getActivePage() < getLastVisiblePage())) {
			if (navigationEnabled && lastText[0] != null) {
				gotoPage(LOC_BUTTON_NEXT_PAGE);
			}

			// final int rows = getJQueryCount(format(columnsPreformatted[0], 0));
			final int rows = selenium.getCount(jq(format(columnsPreformatted[0], 0)));

			assertTrue(rows > 0, "There must be at least one row in the table");

			for (int row = 1; row <= rows; row++) {
				for (int column = 0; column < columns; column++) {
					String text = selenium.getText(jq(format(columnsPreformatted[column], row)));
					if (lastText[column] != null) {
						int comparison = text.compareTo(lastText[column]);
						try {
							Double number = Double.parseDouble(text);
							Double lastNumber = Double.parseDouble(lastText[column]);
							comparison = number.compareTo(lastNumber);
						} catch (NumberFormatException e) {
						}
						if (sortedAscending[column] == null) {
							if (comparison > 0) {
								sortedAscending[column] = true;
								break;
							} else if (comparison < 0) {
								sortedAscending[column] = false;
								break;
							}
						} else {
							if (sortedAscending[column]) {
								if (comparison < 0) {
									fail();
								}
								break;
							} else {
								if (comparison > 0) {
									fail();
								}
								break;
							}
						}
					}
					lastText[column] = text;
				}
			}
		}
	}
}
