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
package org.jboss.richfaces.integrationTest.keepAlive;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.testng.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.jboss.arquillian.ajocado.dom.Attribute;
import org.jboss.arquillian.ajocado.dom.Event;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.attribute.AttributeLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class KeepAliveTestCase extends AbstractSeleniumRichfacesTestCase {
	private final JQueryLocator LOC_INPUT_INCORRECT1 = jq(getLoc("INPUT_INCORRECT1"));
	private final JQueryLocator LOC_INPUT_INCORRECT2 = jq(getLoc("INPUT_INCORRECT2"));
	private final JQueryLocator LOC_BUTTON_INCORRECT = jq(getLoc("BUTTON_INCORRECT"));
	private final JQueryLocator LOC_OUTPUT_INCORRECT = jq(getLoc("OUTPUT_INCORRECT"));
	private final JQueryLocator LOC_INPUT_CORRECT1 = jq(getLoc("INPUT_CORRECT1"));
	private final JQueryLocator LOC_INPUT_CORRECT2 = jq(getLoc("INPUT_CORRECT2"));
	private final JQueryLocator LOC_BUTTON_CORRECT = jq(getLoc("BUTTON_CORRECT"));
	private final JQueryLocator LOC_OUTPUT_CORRECT = jq(getLoc("OUTPUT_CORRECT"));

	private final String MSG_INPUT_FIRST_NUMBER = getMsg("INPUT_FIRST_NUMBER");
	private final String MSG_INPUT_SECOND_NUMBER = getMsg("INPUT_SECOND_NUMBER");
	private final String MSG_OUTPUT_RESULT_NUMBER = getMsg("OUTPUT_RESULT_NUMBER");

	/**
	 * Type two summands to incorrect way form, wait for button became enabled
	 * and checks that after click will appear no result.
	 */
	@Test
	public void testUsingIncorrectWay() {

		// fill first two summands
		scrollIntoView(LOC_INPUT_INCORRECT1, true);
		
		selenium.type(LOC_INPUT_INCORRECT1, MSG_INPUT_FIRST_NUMBER);
		selenium.fireEvent(LOC_INPUT_INCORRECT1, Event.KEYUP);

		selenium.type(LOC_INPUT_INCORRECT2, MSG_INPUT_SECOND_NUMBER);
		selenium.fireEvent(LOC_INPUT_INCORRECT2, Event.KEYUP);

		// wait for "equal sign" button became enabled (lost disabled status)
		Wait.waitSelenium.failWith("Button \"=\" never became enabled").until(new ButtonDisabled(LOC_BUTTON_INCORRECT));

		// try to count result
		selenium.click(LOC_BUTTON_INCORRECT);

		// TODO: try to find exact way to catch "progress done" event
		// waiting for summarization is processed
		waitFor(5000);

		// get a result and validate it
		String result = selenium.getText(LOC_OUTPUT_INCORRECT);
		assertTrue(StringUtils.isBlank(result), "Result should be blank");
	}

	/**
	 * Type two summands into correct way form, waits for button became enabled
	 * and check that after click will appear right result.
	 */
	@Test
	public void testUsingCorrectWay() {
		// fill first two summands
		scrollIntoView(LOC_INPUT_CORRECT1, true);
		
		selenium.type(LOC_INPUT_CORRECT1, MSG_INPUT_FIRST_NUMBER);
		selenium.fireEvent(LOC_INPUT_CORRECT1, Event.KEYUP);

		selenium.type(LOC_INPUT_CORRECT2, MSG_INPUT_SECOND_NUMBER);
		selenium.fireEvent(LOC_INPUT_CORRECT2, Event.KEYUP);

		// wait for "equal sign" button became enabled (lost disabled status)
		Wait.waitSelenium.failWith("Button \"=\" never became enabled").until(new ButtonDisabled(LOC_BUTTON_CORRECT));

		// try to count result
		selenium.click(LOC_BUTTON_CORRECT);

		// waiting for result became right - If this not happen, waiting
		// timeouts and test fail
		Wait.waitSelenium.failWith("Result never became correct").until(new SeleniumCondition() {
			public boolean isTrue() {
				String result = selenium.getText(LOC_OUTPUT_CORRECT);
				return MSG_OUTPUT_RESULT_NUMBER.equals(result);
			}
		});
	}
	
	private class ButtonDisabled implements SeleniumCondition {
		private JQueryLocator locButton;
		
		public ButtonDisabled(JQueryLocator locButton) {
			this.locButton = locButton;
		}
		
		public boolean isTrue() {
			final AttributeLocator<JQueryLocator> attrDisabled = locButton.getAttribute(new Attribute("disabled"));
			
			
			if (!selenium.isAttributePresent(attrDisabled)) {
				return true;
			}
			
			return "false".equals(selenium.getAttribute(attrDisabled));
		}
		
	}

	protected void loadPage() {
		openComponent("Keep Alive");
	}
}
