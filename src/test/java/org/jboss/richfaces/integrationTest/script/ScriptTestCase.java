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
package org.jboss.richfaces.integrationTest.script;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.testng.Assert.assertEquals;

import org.jboss.arquillian.ajocado.css.CssProperty;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class ScriptTestCase extends AbstractSeleniumRichfacesTestCase {

	private JQueryLocator LOC_FIELDSET_HEADER = jq(getLoc("FIELDSET_HEADER"));
	private JQueryLocator LOC_BUTTON_HIDE = jq(getLoc("BUTTON_HIDE"));
	private JQueryLocator LOC_BUTTON_SHOW = jq(getLoc("BUTTON_SHOW"));
	private JQueryLocator LOC_INPUT_NAME = jq(getLoc("INPUT_NAME"));
	private JQueryLocator LOC_INPUT_JOB = jq(getLoc("INPUT_JOB"));
	private JQueryLocator LOC_BUTTON_SUBMIT = jq(getLoc("BUTTON_SUBMIT"));
	private JQueryLocator LOC_OUTPUT_NAME = jq(getLoc("OUTPUT_NAME"));
	private JQueryLocator LOC_OUTPUT_JOB = jq(getLoc("OUTPUT_JOB"));
	private JQueryLocator LOC_PANEL_HIDABLE = jq(getLoc("PANEL_HIDABLE"));

	private String MSG_INPUT_NAME = getMsg("INPUT_NAME");
	private String MSG_INPUT_JOB = getMsg("INPUT_JOB");
	private String MSG_REGEXP_PREFIX_NAME = getMsg("REGEXP_PREFIX_NAME");
	private String MSG_REGEXP_PREFIX_JOB = getMsg("REGEXP_PREFIX_JOB");

	/**
	 * Simply click to hide button and checks that panel disappears
	 */
	@Test
	public void testHide() {
		hide();
	}

	/**
	 * Simply hide panel and next click to show button and checks that panel
	 * appears again
	 */
	@Test
	public void testHideAndShow() {
		hide();
		show();
	}

	/**
	 * Fill name and job in and checks that after submit the output will be
	 * changed right.
	 */
	@Test
	public void testFillIn() {
		fillIn();
		submit();
		validateOutput();
	}

	/**
	 * Fill name and job in, submit, hide and show the panel again and next
	 * checks, that output is valid. Then submit again and again check output.
	 */
	@Test
	public void testFillInHideAndShow() {
		fillIn();
		submit();
		hide();
		show();
		validateOutput();
		submit();
		validateOutput();
	}

	private void fillIn() {
		selenium.type(LOC_INPUT_NAME, MSG_INPUT_NAME);
		selenium.type(LOC_INPUT_JOB, MSG_INPUT_JOB);
	}

	private void submit() {
		// remember output values before submit
		final String name = getName();
		final String job = getJob();

		// click on Submit
		selenium.click(LOC_BUTTON_SUBMIT);

		// wait until output changes or 5 sec
		Wait.waitSelenium.dontFail().timeout(5000).until(new SeleniumCondition() {
			public boolean isTrue() {
				// name or job changes
				return !name.equals(getName()) || !job.equals(getJob());
			}
		});
	}

	private void validateOutput() {
		assertEquals(MSG_INPUT_NAME, getName(), "Output name isn't right");
		assertEquals(MSG_INPUT_JOB, getJob(), "Output job isn't right");
	}

	private String getName() {
		return selenium.getText(LOC_OUTPUT_NAME).replaceFirst(MSG_REGEXP_PREFIX_NAME, "");
	}

	private String getJob() {
		return selenium.getText(LOC_OUTPUT_JOB).replaceFirst(MSG_REGEXP_PREFIX_JOB, "");
	}

	private void hide() {
		selenium.click(LOC_BUTTON_HIDE);

		Wait.waitSelenium.failWith("Hidable panel never hides").until(new SeleniumCondition() {
			public boolean isTrue() {
				return "none".equals(getStyle(LOC_PANEL_HIDABLE, CssProperty.DISPLAY));
			}
		});
	}

	private void show() {
		selenium.click(LOC_BUTTON_SHOW);

		Wait.waitSelenium.failWith("Hidable panel never shows again").until(new SeleniumCondition() {
			public boolean isTrue() {
				return "block".equals(getStyle(LOC_PANEL_HIDABLE, CssProperty.DISPLAY));
			}
		});
	}

	protected void loadPage() {
		openComponent("Script");

		scrollIntoView(LOC_FIELDSET_HEADER, true);
	}
}
