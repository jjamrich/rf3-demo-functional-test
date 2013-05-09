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
package org.jboss.richfaces.integrationTest.mediaOutput;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.ajocado.dom.Attribute;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.utils.URLUtils;
import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public class MediaOutputTestCase extends AbstractSeleniumRichfacesTestCase {

	private String LOC_FIELDSET_HEADER = getLoc("FIELDSET_HEADER");
	private JQueryLocator LOC_ATTRIBUTE_IMAGE = jq(getLoc("ATTRIBUTE_IMAGE"));
	private JQueryLocator LOC_ATTRIBUTE_FLASH = jq(getLoc("ATTRIBUTE_FLASH"));

	private String MSG_MD5DIGEST_IMAGE = getMsg("MD5DIGEST_IMAGE");
	private String MSG_MD5DIGEST_FLASH = getMsg("MD5DIGEST_FLASH");

	/**
	 * Gets a image's source URL and obtains it's MD5 digest - checks that the
	 * digest is same as expected.
	 */
	@Test
	public void testImageMd5Digest() {
		String imageSrc = selenium.getAttribute(LOC_ATTRIBUTE_IMAGE, Attribute.SRC);

		URL url = URLUtils.buildUrl(selenium.getLocation(), imageSrc);

        try {
        	assertEquals(URLUtils.resourceMd5Digest(url.toString()), MSG_MD5DIGEST_IMAGE);
        } catch (IOException e) {
        	fail("Getting resources from URL failed");
        }
	}

	/**
	 * Gets a flash object's data URL and obtains it's MD5 digest - checks that
	 * the digest is same as expected.
	 */
	@Test
	public void testFlashMd5Digest() {
		String flashHref = selenium.getAttribute(LOC_ATTRIBUTE_FLASH, Attribute.HREF);

		URL url = URLUtils.buildUrl(selenium.getLocation(), flashHref);

        try {
        	assertEquals(URLUtils.resourceMd5Digest(url.toString()), MSG_MD5DIGEST_FLASH);
        } catch (IOException e) {
        	fail("Getting resources from URL failed");
        }
	}

	protected void loadPage() {
		openComponent("Media Output");

		scrollIntoView(LOC_FIELDSET_HEADER, true);
	}
}
