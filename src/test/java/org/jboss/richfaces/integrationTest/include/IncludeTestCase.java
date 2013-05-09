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
package org.jboss.richfaces.integrationTest.include;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class IncludeTestCase extends AbstractSeleniumRichfacesTestCase {

	private JQueryLocator LOC_FIELDSET_HEADER = jq(getLoc("FIELDSET_HEADER"));
	private JQueryLocator LOC_BUTTON_PREVIOUS = jq(getLoc("BUTTON_PREVIOUS"));
	private JQueryLocator LOC_BUTTON_NEXT = jq(getLoc("BUTTON_NEXT"));
	private JQueryLocator LOC_INPUT_FIRSTNAME = jq(getLoc("INPUT_FIRSTNAME"));
	private JQueryLocator LOC_INPUT_LASTNAME = jq(getLoc("INPUT_LASTNAME"));
	private JQueryLocator LOC_INPUT_COMPANY = jq(getLoc("INPUT_COMPANY"));
	private JQueryLocator LOC_INPUT_NOTES = jq(getLoc("INPUT_NOTES"));
	private JQueryLocator LOC_OUTPUT_FIRSTNAME = jq(getLoc("OUTPUT_FIRSTNAME"));
	private JQueryLocator LOC_OUTPUT_LASTNAME = jq(getLoc("OUTPUT_LASTNAME"));
	private JQueryLocator LOC_OUTPUT_COMPANY = jq(getLoc("OUTPUT_COMPANY"));
	private JQueryLocator LOC_OUTPUT_NOTES = jq(getLoc("OUTPUT_NOTES"));

	private String MSG_INPUT_SAMPLE_PREFORMATTED = getMsg("INPUT_SAMPLE_PREFORMATTED");
	private String MSG_MESSAGE_COMPANY_REQUIRED = getMsg("MESSAGE_COMPANY_REQUIRED");
	private String MSG_MESSAGE_LASTNAME_REQUIRED = getMsg("MESSAGE_LASTNAME_REQUIRED");
	private String MSG_MESSAGE_NOTES_REQUIRED = getMsg("MESSAGE_NOTES_REQUIRED");

	/**
	 * Simply fill in all inputs and checks that they will be output well on
	 * following pages.
	 */
	@Test
	public void testGoThroughSteps() {
		goThroughSteps();
	}

	/**
	 * Fill in all inputs, checks that they are output well on following pages
	 * and then go through previous pages and check that all stay still without
	 * change.
	 */
	@Test(dependsOnMethods = { "testGoThroughSteps" })
	public void testGoThroughStepsBack() {
		goThroughSteps();
		goThroughStepsBack();
	}

	/**
	 * Go through pages and on every page try to don't fill some input - checks
	 * that message 'value required' will appear. On the last page checks that
	 * every output is filled-in right.
	 */
	@Test
	public void testTryFailedValidation() {
		assertTrue(isFirstPage());

		selenium.type(LOC_INPUT_FIRSTNAME, format(MSG_INPUT_SAMPLE_PREFORMATTED, 1));
		selenium.type(LOC_INPUT_LASTNAME, format(MSG_INPUT_SAMPLE_PREFORMATTED, 2));
		selenium.click(LOC_BUTTON_NEXT);

		waitForText(MSG_MESSAGE_COMPANY_REQUIRED);

		selenium.type(LOC_INPUT_COMPANY, format(MSG_INPUT_SAMPLE_PREFORMATTED, 3));
		selenium.click(LOC_BUTTON_NEXT);

		Wait.waitSelenium.failWith("Switching to second page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isSecondPage();
			}
		});

		selenium.click(LOC_BUTTON_PREVIOUS);

		Wait.waitSelenium.failWith("Switching to first page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isFirstPage();
			}
		});

		selenium.type(LOC_INPUT_LASTNAME, "");
		selenium.click(LOC_BUTTON_NEXT);

		waitForText(MSG_MESSAGE_LASTNAME_REQUIRED);

		selenium.type(LOC_INPUT_LASTNAME, format(MSG_INPUT_SAMPLE_PREFORMATTED, 2));
		selenium.click(LOC_BUTTON_NEXT);

		Wait.waitSelenium.failWith("Switching to second page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isSecondPage();
			}
		});

		selenium.click(LOC_BUTTON_NEXT);

		waitForText(MSG_MESSAGE_NOTES_REQUIRED);

		selenium.type(LOC_INPUT_NOTES, format(MSG_INPUT_SAMPLE_PREFORMATTED, 4));
		selenium.click(LOC_BUTTON_NEXT);

		Wait.waitSelenium.failWith("Switching to last page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isLastPage();
			}
		});

		assertEquals(selenium.getText(LOC_OUTPUT_FIRSTNAME), format(MSG_INPUT_SAMPLE_PREFORMATTED, 1));
		assertEquals(selenium.getText(LOC_OUTPUT_LASTNAME), format(MSG_INPUT_SAMPLE_PREFORMATTED, 2));
		assertEquals(selenium.getText(LOC_OUTPUT_COMPANY), format(MSG_INPUT_SAMPLE_PREFORMATTED, 3));
		assertEquals(selenium.getText(LOC_OUTPUT_NOTES), format(MSG_INPUT_SAMPLE_PREFORMATTED, 4));
	}

	private void goThroughSteps() {
		assertTrue(isFirstPage());

		selenium.type(LOC_INPUT_FIRSTNAME, format(MSG_INPUT_SAMPLE_PREFORMATTED, 1));
		selenium.type(LOC_INPUT_LASTNAME, format(MSG_INPUT_SAMPLE_PREFORMATTED, 2));
		selenium.type(LOC_INPUT_COMPANY, format(MSG_INPUT_SAMPLE_PREFORMATTED, 3));
		selenium.click(LOC_BUTTON_NEXT);

		Wait.waitSelenium.failWith("Switching to second page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isSecondPage();
			}
		});

		selenium.type(LOC_INPUT_NOTES, format(MSG_INPUT_SAMPLE_PREFORMATTED, 4));
		selenium.click(LOC_BUTTON_NEXT);

		Wait.waitSelenium.failWith("Switching to last page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isLastPage();
			}
		});

		assertEquals(selenium.getText(LOC_OUTPUT_FIRSTNAME), format(MSG_INPUT_SAMPLE_PREFORMATTED, 1));
		assertEquals(selenium.getText(LOC_OUTPUT_LASTNAME), format(MSG_INPUT_SAMPLE_PREFORMATTED, 2));
		assertEquals(selenium.getText(LOC_OUTPUT_COMPANY), format(MSG_INPUT_SAMPLE_PREFORMATTED, 3));
		assertEquals(selenium.getText(LOC_OUTPUT_NOTES), format(MSG_INPUT_SAMPLE_PREFORMATTED, 4));
	}

	private void goThroughStepsBack() {
		selenium.click(LOC_BUTTON_PREVIOUS);

		Wait.waitSelenium.failWith("Switching to second page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isSecondPage();
			}
		});

		assertEquals(selenium.getValue(LOC_INPUT_NOTES), format(MSG_INPUT_SAMPLE_PREFORMATTED, 4));

		selenium.click(LOC_BUTTON_PREVIOUS);

		Wait.waitSelenium.failWith("Switching to first page failed").until(new SeleniumCondition() {
			public boolean isTrue() {
				return isFirstPage();
			}
		});

		assertEquals(selenium.getValue(LOC_INPUT_FIRSTNAME), format(MSG_INPUT_SAMPLE_PREFORMATTED, 1));
		assertEquals(selenium.getValue(LOC_INPUT_LASTNAME), format(MSG_INPUT_SAMPLE_PREFORMATTED, 2));
		assertEquals(selenium.getValue(LOC_INPUT_COMPANY), format(MSG_INPUT_SAMPLE_PREFORMATTED, 3));
	}

	private boolean isFirstPage() {
		return isButtonsPresent(false, true);
	}

	private boolean isSecondPage() {
		return isButtonsPresent(true, true);
	}

	private boolean isLastPage() {
		return isButtonsPresent(true, false);
	}

	private boolean isButtonsPresent(boolean previousPresent, boolean nextPresent) {
		return (previousPresent == selenium.isElementPresent(LOC_BUTTON_PREVIOUS))
				&& (nextPresent == selenium.isElementPresent(LOC_BUTTON_NEXT));
	}

	protected void loadPage() {
		openComponent("Include");

		scrollIntoView(LOC_FIELDSET_HEADER, true);
	}
}
