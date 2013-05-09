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
package org.jboss.richfaces.integrationTest.beanValidator;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.jboss.arquillian.ajocado.format.SimplifiedFormat.format;

import org.jboss.arquillian.ajocado.Graphene;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class BeanValidatorTestCase extends AbstractSeleniumRichfacesTestCase {

	private String LOC_FIELDSET_HEADER = getLoc("FIELDSET_HEADER");
	private String LOC_BUTTON_SUBMIT = getLoc("BUTTON_SUBMIT");
	private String LOC_VALIDATION_MESSAGE_RELATIVE = getLoc("VALIDATION_MESSAGE_RELATIVE");
	
	private final String INPUT_LOC_FORMAT = "input[id$={0}]";
    
	private String LOC_INPUT_NAME = getLoc("INPUT_NAME");
	private String LOC_INPUT_AGE = getLoc("INPUT_AGE");
	private String LOC_INPUT_EMAIL = getLoc("INPUT_EMAIL");
	
	private final String MSG_INPUT_VALUE_IS_LESS_THAN_MINIMUM = getMsg("INPUT_VALUE_IS_LESS_THAN_MINIMUM");
	private final String MSG_INPUT_VALUE_IS_GREATER_THAN_MAXIMUM = getMsg("INPUT_VALUE_IS_GREATER_THAN_MAXIMUM");
	private final String MSG_INPUT_VALID_NAME = getMsg("INPUT_VALID_NAME");
	private final String MSG_INPUT_MUST_BE_GREATER_THAN_OR_EQUAL = getMsg("INPUT_MUST_BE_GREATER_THAN_OR_EQUAL");
	private final String MSG_INPUT_MUST_BE_LESS_THAN_OR_EQUAL = getMsg("INPUT_MUST_BE_LESS_THAN_OR_EQUAL");
	private final String MSG_INPUT_IS_NOT_NUMBER = getMsg("INPUT_IS_NOT_NUMBER");
	private final String MSG_INPUT_VALID_AGE = getMsg("INPUT_VALID_AGE");
	private final String MSG_INPUT_NOT_WELL_FORMED_EMAIL_1 = getMsg("INPUT_NOT_WELL_FORMED_EMAIL_1");
	private final String MSG_INPUT_NOT_WELL_FORMED_EMAIL_2 = getMsg("INPUT_NOT_WELL_FORMED_EMAIL_2");
	private final String MSG_INPUT_WELL_FORMED_EMAIL = getMsg("INPUT_WELL_FORMED_EMAIL");
	private final String MSG_OUTPUT_LENGTH_MUST_BE_BETWEEN = getMsg("OUTPUT_LENGTH_MUST_BE_BETWEEN");
	private final String MSG_OUTPUT_STRING_CONTAIN_ONLY_SPACES = getMsg("OUTPUT_STRING_CONTAIN_ONLY_SPACES");
	private final String MSG_OUTPUT_MAY_NOT_BE_NULL = getMsg("OUTPUT_MAY_NOT_BE_NULL");
	private final String MSG_OUTPUT_MUST_BE_GREATER_THAN_OR_EQUAL = getMsg("OUTPUT_MUST_BE_GREATER_THAN_OR_EQUAL");
	private final String MSG_OUTPUT_MUST_BE_LESS_THAN_OR_EQUAL = getMsg("OUTPUT_MUST_BE_LESS_THAN_OR_EQUAL");
	private final String MSG_OUTPUT_MUST_BE_A_NUMBER = getMsg("OUTPUT_MUST_BE_A_NUMBER");
	private final String MSG_OUTPUT_MAY_NOT_BE_NULL_OR_EMPTY = getMsg("OUTPUT_MAY_NOT_BE_NULL_OR_EMPTY");
	private final String MSG_OUTPUT_NOT_WELL_FORMED_EMAIL = getMsg("OUTPUT_NOT_WELL_FORMED_EMAIL");
	
	/**
	 * Try type no chars in input name and checks that value required message
	 * will appear.
	 */
	@Test
	public void testNameValueRequired() {
		final String validationMessage = format(MSG_OUTPUT_MAY_NOT_BE_NULL_OR_EMPTY, LOC_INPUT_NAME);
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_NAME)), "");
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_NAME))).text(validationMessage));
	}

	/**
	 * Try type string of length less than minimum length in name input and
	 * checks that validation message will appear.
	 */
	@Test
	public void testNameMinimumLength() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_NAME)), MSG_INPUT_VALUE_IS_LESS_THAN_MINIMUM);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_NAME))).text(MSG_OUTPUT_LENGTH_MUST_BE_BETWEEN));
	}

	/**
	 * Try type string of length greater than maximum length in name input and
	 * checks that validation message will appear.
	 */
	@Test
	public void testNameMaximumLength() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_NAME)), MSG_INPUT_VALUE_IS_GREATER_THAN_MAXIMUM);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_NAME))).text(MSG_OUTPUT_LENGTH_MUST_BE_BETWEEN));
	}

	/**
	 * Enter only one space to name input and checks that validation message
	 * will appear.
	 */
	@Test
	public void testNameOnlySpacesPattern() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_NAME)), " ");
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_NAME))).text(MSG_OUTPUT_STRING_CONTAIN_ONLY_SPACES));
	}

	/**
	 * Try type the invalid name input and checks that after typing valid input
	 * will validation message disappear.
	 */
	@Test
	public void testNameMessageDisappers() {
		// first violate validation error
		testNameValueRequired();
		// then try valid input
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_NAME)), MSG_INPUT_VALID_NAME);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_NAME))).text(""));
	}

	/**
	 * Try type no chars in age input and checks that the validation message
	 * value required will appear.
	 */
	@Test
	public void testAgeValueRequired() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_AGE)), "");
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_AGE))).text(MSG_OUTPUT_MAY_NOT_BE_NULL));
	}

	/**
	 * Try input age less than minimum and checks that the validation message
	 * will appear.
	 */
	@Test
	public void testAgeMinimumValue() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_AGE)), MSG_INPUT_MUST_BE_LESS_THAN_OR_EQUAL);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_AGE))).text(MSG_OUTPUT_MUST_BE_LESS_THAN_OR_EQUAL));
	}

	/**
	 * Try input age greater than maximum and checks that the validation message
	 * will appear.
	 */
	@Test
	public void testAgeMaximumValue() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_AGE)), MSG_INPUT_MUST_BE_GREATER_THAN_OR_EQUAL);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_AGE))).text(MSG_OUTPUT_MUST_BE_GREATER_THAN_OR_EQUAL));
	}

	/**
	 * Try input age as non-Integer (alphabetical chars) and checks that
	 * validation message will appear.
	 */
	@Test
	public void testAgeIntegerOnly() {
		final String validationMessage = format(MSG_OUTPUT_MUST_BE_A_NUMBER, LOC_INPUT_AGE);
		Graphene.waitGui.until(Graphene.elementPresent.locator(jq(LOC_INPUT_AGE)));
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_AGE)), MSG_INPUT_IS_NOT_NUMBER);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_AGE))).text(validationMessage));
	}

	/**
	 * Try input no input first and wait for validation message appears and then
	 * type a valid input and checks that validation message will disappear.
	 */
	@Test
	public void testAgeMessageDisappers() {
		// first violate validation error
		testAgeValueRequired();
		// then try valid input
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_AGE)), MSG_INPUT_VALID_AGE);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_AGE))).text(""));
	}

	/**
	 * Try to enter empty email address and checks that validation message
	 * appears.
	 */
	@Test
	public void testEmailMayNotBeNullOrEmpty() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_EMAIL)), "");
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_EMAIL))).text(MSG_OUTPUT_MAY_NOT_BE_NULL_OR_EMPTY));
	}

	/**
	 * Try to enter one space like a email address and checks that validation
	 * message appears.
	 */
	@Test
	public void testEmailOnlySpaces() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_EMAIL)), " ");
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_EMAIL))).text(MSG_OUTPUT_NOT_WELL_FORMED_EMAIL));
	}

	/**
	 * Enter bad email address (only domain name suffix) and checks that
	 * validation message appears.
	 */
	@Test
	public void testEmailBad1() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_EMAIL)), MSG_INPUT_NOT_WELL_FORMED_EMAIL_1);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_EMAIL))).text(MSG_OUTPUT_NOT_WELL_FORMED_EMAIL));
	}

	/**
	 * Enter bad email address (only @ char with domain name suffix) and checks
	 * that testValidation message appears.
	 */
	@Test
	public void testEmailBad2() {
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_EMAIL)), MSG_INPUT_NOT_WELL_FORMED_EMAIL_2);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_EMAIL))).text(MSG_OUTPUT_NOT_WELL_FORMED_EMAIL));
	}

	/**
	 * Enter bad email address, wait for validation message appear and then try
	 * to enter valid email address and checks that validation message appear.
	 */
	@Test
	public void testEmailMessageDisappers() {
		// first violate validation error
		testEmailMayNotBeNullOrEmpty();
		// then try valid input
		typeAndSubmit(jq(format(INPUT_LOC_FORMAT, LOC_INPUT_EMAIL)), MSG_INPUT_WELL_FORMED_EMAIL);
		Graphene.waitAjax.until(Graphene.textEquals.locator(jq(getMessageFor(LOC_INPUT_EMAIL))).text(""));
	}
	
	private void typeAndSubmit(JQueryLocator locator, String value) {
		selenium.type(locator, value);
		selenium.click(jq(LOC_BUTTON_SUBMIT));
	}

	private String getMessageFor(String locator) {
		return format(LOC_VALIDATION_MESSAGE_RELATIVE, locator);
	}

	protected void loadPage() {
		openComponent("Bean Validator");

		scrollIntoView(LOC_FIELDSET_HEADER, true);
	}
}
