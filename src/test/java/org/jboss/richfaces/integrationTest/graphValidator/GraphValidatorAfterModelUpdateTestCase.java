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
package org.jboss.richfaces.integrationTest.graphValidator;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;
import static org.testng.Assert.assertEquals;

import org.jboss.arquillian.ajocado.dom.Attribute;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class GraphValidatorAfterModelUpdateTestCase extends AbstractSeleniumRichfacesTestCase {
	private final String LOC_FIELDSET_HEADER_ACTIVITIES = getLoc("LOC_FIELDSET_HEADER_ACTIVITIES");
	private final String LOC_BUTTON_SUBMIT_ACTIVITIES = getLoc("BUTTON_SUBMIT_ACTIVITIES");
	private final String LOC_OUTPUT_VALIDATION_MESSAGE = getLoc("OUTPUT_VALIDATION_MESSAGE");
	private final String LOC_CLASS_VALIDATION_MESSAGE = getLoc("CLASS_VALIDATION_MESSAGE");
	private final String LOC_INPUT_ACTIVITY_HOURS_PREFORMATTED = getLoc("INPUT_ACTIVITY_HOURS_PREFORMATTED");

	private final String MSG_CLASS_VALID = getMsg("CLASS_VALID");
	private final String MSG_CLASS_INVALID = getMsg("CLASS_INVALID");
	private final String MSG_INPUT_VALID = getMsg("INPUT_VALID");
	private final String MSG_INPUT_INVALID_TOO_GREAT = getMsg("INPUT_INVALID_TOO_GREAT");
	private final String MSG_INPUT_INVALID_SUM_TOO_GREAT = getMsg("INPUT_INVALID_SUM_TOO_GREAT");
	private final String MSG_OUTPUT_PLEASE_FILL_AT_LEAST_ONE_ENTRY = getMsg("OUTPUT_PLEASE_FILL_AT_LEAST_ONE_ENTRY");
	private final String MSG_OUTPUT_CHANGES_STORED_SUCCESSFULLY = getMsg("OUTPUT_CHANGES_STORED_SUCCESSFULLY");
	private final String MSG_OUTPUT_INVALID_VALUES = getMsg("OUTPUT_INVALID_VALUES");
	private final String MSG_OUTPUT_INVALID_SUM_TOO_GREAT = getMsg("OUTPUT_INVALID_SUM_TOO_GREAT");

	/**
	 * Do no changes to form and checks that validate message 'Please fill at
	 * least one entry' appear
	 */
	@Test
	public void testNoChangeIntoForm() {
		submitAndWaitForMessageAppears();

		validateMessages(MSG_CLASS_INVALID, MSG_OUTPUT_PLEASE_FILL_AT_LEAST_ONE_ENTRY);
	}

	/**
	 * Enter first input and checks that changes will store successfully
	 */
	@Test
	public void testChangeStoredSuccessfully() {
		typeAndSubmit(format(LOC_INPUT_ACTIVITY_HOURS_PREFORMATTED, 1), MSG_INPUT_VALID);

		validateMessages(MSG_CLASS_VALID, MSG_OUTPUT_CHANGES_STORED_SUCCESSFULLY);
	}

	/**
	 * Enter only one value that is too great and checks that validation message
	 * appear
	 */
	@Test
	public void testOneValueTooGreat() {
		typeAndSubmit(format(LOC_INPUT_ACTIVITY_HOURS_PREFORMATTED, 1), MSG_INPUT_INVALID_TOO_GREAT);

		validateMessages(MSG_CLASS_INVALID, MSG_OUTPUT_INVALID_VALUES);
	}

	/**
	 * Enter several values, which is in sum greater than allow maximum and
	 * checks that validation message appear.
	 */
	@Test
	public void testSumOfValuesTooGreat() {
		for (int i = 1; i <= 3; i++) {
			selenium.type(jq(format(LOC_INPUT_ACTIVITY_HOURS_PREFORMATTED, i)), MSG_INPUT_INVALID_SUM_TOO_GREAT);
		}
		submitAndWaitForMessageAppears();

		validateMessages(MSG_CLASS_INVALID, MSG_OUTPUT_INVALID_SUM_TOO_GREAT);
	}

	private void typeAndSubmit(String locator, String text) {
		selenium.type(jq(locator), text);
		submitAndWaitForMessageAppears();
	}

	private void submitAndWaitForMessageAppears() {
		selenium.click(jq(LOC_BUTTON_SUBMIT_ACTIVITIES));

		Wait.waitSelenium.failWith("Validation message never appeared").until(new SeleniumCondition() {
			public boolean isTrue() {
				return selenium.isElementPresent(jq(LOC_OUTPUT_VALIDATION_MESSAGE));
			}
		});
	}

	private void validateMessages(String className, String text) {
		assertEquals(selenium.getAttribute(jq(LOC_CLASS_VALIDATION_MESSAGE), Attribute.CLASS), className,
				"Validation message's class is invalid");
		assertEquals(selenium.getText(jq(LOC_OUTPUT_VALIDATION_MESSAGE)), text, "Given validation message isn't expected");
	}

	protected void loadPage() {
		openComponent("Graph Validator");

		scrollIntoView(LOC_FIELDSET_HEADER_ACTIVITIES, true);
	}
}
