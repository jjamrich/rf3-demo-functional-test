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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jboss.arquillian.ajocado.Graphene;
import org.jboss.arquillian.ajocado.css.CssProperty;
import org.jboss.arquillian.ajocado.dom.Event;
import org.jboss.arquillian.ajocado.geometry.Point;
import org.jboss.arquillian.ajocado.javascript.JavaScript;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.attribute.AttributeLocator;
import org.jboss.arquillian.ajocado.locator.element.ElementLocator;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.retrievers.AttributeRetriever;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.testng.ITestContext;
import org.testng.TestRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * 
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>, <a
 *         href="mailto:pjha@redhat.com">Prabhat Jha</a>
 * @version $Revision$
 * 
 */

public abstract class AbstractSeleniumRichfacesTestCase extends AbstractGrapheneTestCase {

    protected Properties locatorsProperties;
    protected Properties messagesProperties;
	
    /**
     * context root can be used to obtaining full URL paths, is set to actual
     * tested application's context root
     */
    // protected String contextRoot;

    /**
     * ContextPath will be used to retrieve pages from right URL. Don't hesitate
     * to use it in cases of building absolute URLs.
     */
    //protected String contextPath;
    
    /**
     * Introduce some maven build properties
     */
    protected String mavenProjectBuildDirectory;	// usually ${project}/target
    protected String mavenResourcesDir;				// usually ${project}/target/test-classes
    protected boolean seleniumDebug;					// if used specified debug mode of selenium testing
    protected String browser;

    /**
     * predefined waitings to use in inheritors
     */
    // protected Waiting waitModelUpdate = Wait.interval(100).timeout(30000);
    // protected Waiting waitGuiInteraction = Wait.interval(100).timeout(500);

	/**
	 * Test listener used to logging to selenium's server.log via getEval()
	 * method (see {@link SeleniumLoggingTestListener})
	 * 
	 * Don't forget to use SeleniumLoggingTestListener.setSelenium(Selenium) to
	 * initialize selenium-side logging properly
	 */
	private static volatile SeleniumLoggingTestListener loggingTestListener;
	
	public AbstractSeleniumRichfacesTestCase() {
        locatorsProperties = getLocatorsProperties();
        messagesProperties = getMessagesProperties();
	}

    @BeforeSuite
    protected void registerSeleniumInListeners(ITestContext context) {
        loggingTestListener = new SeleniumLoggingTestListener();
        
        TestRunner runner = (TestRunner) context;
        runner.addTestListener(loggingTestListener);        
    }

//	@BeforeClass
//	@Parameters( { "context.root", "context.path", "browser", "selenium.host", "selenium.port", "selenium.debug",
//			"selenium.maximize", "maven.resources.dir", "maven.project.build.directory" })
//	public void initializeParameters(String contextRoot, String contextPath, String browser, String seleniumDebug,
//			String mavenResourcesDir, String mavenProjectBuildDirectory) {
//		this.contextRoot = contextRoot;
//		this.contextPath = contextPath;
//		this.mavenResourcesDir = mavenResourcesDir;
//		this.mavenProjectBuildDirectory = mavenProjectBuildDirectory;
//		this.seleniumDebug = Boolean.valueOf(seleniumDebug);
//		this.browser = browser;
//	}

	/**
	 * Initializes context before each class run.
	 * 
	 * Parameters will be obtained from TestNG.
	 * 
	 * @param contextRoot
	 *            server's context root, e.g. http://localhost:8080/
	 * @param contextPath
	 *            context path to application in context of server's root (e.g.
	 *            /myapp)
	 * @param browser
	 *            used browser (e.g. "*firefox", see selenium reference API)
	 * @param seleniumPort
	 *            specifies on which port should selenium server run
	 */
	// @BeforeClass(dependsOnMethods = { "initializeParameters", "isTestBrowserEnabled" })
    @BeforeClass
	@Parameters( { "selenium.host", "selenium.port", "selenium.maximize" })
	public void initializeBrowser(String seleniumHost, String seleniumPort, String seleniumMaximize) {
		// selenium = RichfacesSelenium.getInstance(seleniumHost, Integer.valueOf(seleniumPort), browser, contextRoot); // done by Drone
		// selenium.start();
		// allowInitialXpath();
		// loadCustomLocationStrategies();
		loggingTestListener.setSelenium(selenium);
		
		if (Boolean.valueOf(seleniumMaximize)) {
			// focus and maximaze tested window
			selenium.windowFocus();
			selenium.windowMaximize();
		}
	}

	/**
	 * Uses selenium.addLocationStrategy to implement own strategies to locate
	 * items in the tested page
	 */
//	private void loadCustomLocationStrategies() {
//		// jQuery location strategy
//		try {
//			String jqueryLocationStrategy = IOUtils.toString(new FileReader(
//					"src/test/resources/selenium-location-strategies/jquery-strategy.js"));
//			selenium.addLocationStrategy(new ElementLocationStrategy("jquery"), new JavaScript(jqueryLocationStrategy));
//		} catch (IOException ex) {
//			throw new IllegalStateException(ex);
//		}
//	}

	/**
	 * Setup initial type of XPath to non-native version.
	 * 
	 * Use to return XPath settings to initial type.
	 */
//    protected void allowInitialXpath() {
//        selenium.allowNativeXpath(false);
//    }

	/**
	 * Finalize context after each class run.
	 */
	@AfterClass
	public void finalizeBrowser() {
		loggingTestListener.setSelenium(null);
		selenium.close();
		selenium.stop();
		selenium = null;
	}

	/*
	@Parameters( { "internet-explorer-enabled", "firefox-enabled" })
	@BeforeClass(dependsOnMethods="initializeParameters")
	public void isTestBrowserEnabled(@Optional("true") String internetExplorerEnabled, @Optional("true") String firefoxEnabled) {
		boolean isTestBrowserEnabled = false;

		if (Boolean.valueOf(internetExplorerEnabled) && browserIsInternetExplorer()) {
			isTestBrowserEnabled = true;
		}

		if (Boolean.valueOf(firefoxEnabled) && browserIsFirefox()) {
			isTestBrowserEnabled = true;
		}

		if (!isTestBrowserEnabled) {
			throw new SkipException("The test isn't enabled for current browser");
		}
	}
*/

	@BeforeMethod(alwaysRun = true)
	public void callLoadPage() {
		loadPage();
	}
	
	protected abstract void loadPage();

    private final String[] INTERNET_EXPLORER_PREFIXES = new String[] { "*iexplore", "*piiexplore", "*iehta" };
    private final String[] FIREFOX_PREFIXES = new String[] { "*firefox", "*pifirefox", "*chrome" };
    
    public boolean browserIsInternetExplorer() {
		return containsBrowserOneOfPrefixes(browser, INTERNET_EXPLORER_PREFIXES);
	}
    
    public boolean browserIsFirefox() {
		return containsBrowserOneOfPrefixes(browser, FIREFOX_PREFIXES);
	}
    
    private boolean containsBrowserOneOfPrefixes(String browser, String[] prefixes) {
    	for (String prefix : prefixes) {
			if (StringUtils.defaultString(browser).startsWith(prefix)) {
				return true;
			}
		}
		return false;
    }

    /**
     * Default implementation of obtaining properties for each class.
     * 
     * @see AbstractSeleniumTestCase (method getLoc(String,String))
     */
    protected Properties getLocatorsProperties() {
        return getNamedPropertiesForClass(this.getClass(), "locators");
    }
    
    /**
     * Default implementation of obtaining properties for each class.
     * 
     * @see AbstractSeleniumTestCase (method getMess(String,String))
     */
    protected Properties getMessagesProperties() {
        return getNamedPropertiesForClass(this.getClass(), "messages");
    }

    /**
     * Loads properties from specified resource file in context of specified
     * class' package.
     * 
     * @param tClass
     *            named resource will be searched in context of this class
     * @param name
     *            name of resource contained in current class' package
     * @return loaded properties
     * @throws IllegalStateException
     *             if an error occurred when reading resource file
     */
    protected <T> Properties getNamedPropertiesForClass(Class<T> tClass, String name) throws IllegalStateException {
        String propFile = tClass.getPackage().getName();
        propFile = propFile.replace('.', '/');
        propFile = String.format("%s/%s.properties", propFile, name);

        try {
            return getProperties(propFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Loads properties from specified resource file
     * 
     * @param resource
     *            where in classpath the file is located
     * @return loaded properties
     * @throws IOException
     *             if an error occurred when reading resource file
     */
    protected static Properties getProperties(String resource) throws IOException {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        InputStream is = cl.getResourceAsStream(resource);

        Properties props = new Properties();

        if (is == null) {
            is = AbstractSeleniumRichfacesTestCase.class.getResourceAsStream(resource);
        }

        if (is != null) {
            props.load(is);
        }

        return props;
    }
	
    /**
     * An abstract implementation of test for testing source code of examples.
     * 
     * @param fieldset
     *            the number of the example
     * @param linkLabel
     *            the label of link, e.g. "View Source"
     * @param expected
     *            an array of strings that should be in the snippet
     */
    protected void abstractTestSource(int fieldset, String linkLabel, String[] expected) {
        final String prefix = format("fieldset:eq({0}) > div > div:has(span:textEndsWith({1}))", fieldset-1, linkLabel);

        scrollIntoView(prefix, true);

        assertTrue(Graphene.elementNotVisible.locator(jq(prefix + " > div")).isTrue(),
            "Source should not be visible -- it has to contain 'display: none;'.");

        // click on 'View xxx Source'
        // waitForElement(prefix + " > span:eq(1)");
        Graphene.waitGui.until(Graphene.elementPresent.locator(jq(prefix + " > span:eq(1)")));
        selenium.click(jq(prefix + " > span:eq(1)"));

        // waitForElement(prefix + " div[class*=viewsourcebody]");
        Graphene.waitGui.until(Graphene.elementPresent.locator(jq(prefix + " div[class*=viewsourcebody]")));

        // assertTrue(selenium.isVisible(jq(prefix + " > div")), "Source should be visible -- it should not contain 'display: none;'.");
        Graphene.waitGui.failWith("Source should be visible -- it should not contain 'display: none;'.")
            .until(Graphene.elementVisible.locator(jq(prefix + " > div")));

        String source = selenium.getText(jq(prefix + " div.viewsourcediv"));
        for (String str : expected) {
            assertTrue(source.contains(str), "The code should contain \"" + str + "\".");
        }

        // click on 'Hide'
        selenium.click(jq(prefix + " > span:eq(0)"));

        // wait while 'style' attribute changes
        /*
        Wait.until(new Condition() {
            public boolean isTrue() {
                return !isDisplayed(prefix + " > div");
            }
        });*/
        Graphene.waitGui.until(Graphene.elementVisible.locator(jq(prefix + " > div")));

        // assertFalse(selenium.isVisible(jq(prefix + " > div")), "Source should be hidden.");
        Graphene.waitGui.failWith("Source should be hidden.").until(Graphene.elementVisible.locator(jq(prefix + " > div")));
    }

    /**
     * <p>
     * Opens new page on contextPath's location and then selects in menu the
     * item specified by componentName and waits for Component's page is opened.
     * </p>
     * 
     * <p>
     * If componen's page is already opened, skip phase of menu selection.
     * </p>
     * 
     * <p>
     * For opening specified tab of Component's page use
     * {@link #openTab(String)}.
     * </p>
     * 
     * @param componentName
     *            name of component given from components' menu on the left of
     *            RF Live Demo application
     */
	protected void openComponent(final String componentName) {

		// final JQueryLocator LOC_MENU_ITEM = jq(format("jquery=table.left_menu td.text a > span:textEquals('{0}')", componentName));
	    final JQueryLocator LOC_MENU_ITEM = jq(format("table.left_menu td.text a > span:contains('{0}')", componentName));

		// TODO needs to open clean page, see {@link
		// https://jira.jboss.org/jira/browse/RF-7640}
		selenium.getEval(new JavaScript("selenium.doDeleteAllVisibleCookies()"));

		// open context path of application
        selenium.open(contextPath);

		// wait for new page is opened
		selenium.waitForPageToLoad(5000l);

		/*
		Wait.until(new Condition() {
			public boolean isTrue() {
				return selenium.isElementPresent(LOC_MENU_ITEM);
			}
		});*/
		Graphene.waitGui.until(Graphene.elementPresent.locator(LOC_MENU_ITEM));

		// click the menu item
		selenium.click(LOC_MENU_ITEM);

		// wait for component's page opened
		/*
		waitModelUpdate.until(new Condition() {
			public boolean isTrue() {
				return isComponentPageActive(componentName);
			}
		});
		*/
		Graphene.waitModel.until(new SeleniumCondition() {
            public boolean isTrue() {
                return isComponentPageActive(componentName);
            }
        });
	}

    private boolean isComponentPageActive(String componentName) {
        final String LOC_OUTPUT_COMPONENT_NAME = "body table.left_menu *.panel_documents strong";
        // return componentName.equals(getTextOrNull(LOC_OUTPUT_COMPONENT_NAME));
        return Graphene.textEquals.locator(jq(LOC_OUTPUT_COMPONENT_NAME)).text(componentName).isTrue();
    }

    /**
     * <p>
     * Opens specified tab on the Component's page.
     * </p>
     * 
     * <p>
     * If tab is already opened, skip phase of tab selection.
     * </p>
     * 
     * <p>
     * Use this method after opening page by {@link #openComponent(String)}.
     * </p>
     * 
     * @param tabTitle
     *            title on the tab header, which should be opened.
     */
    protected void openTab(String tabTitle) {

        final JQueryLocator LOC_TAB = jq(format(
                "form[id$='_form'] td.rich-tab-header:contains('{0}')", tabTitle));

        if (selenium.belongsClass(LOC_TAB, "rich-tab-active")) {
            return;
        }

        Graphene.waitGui.until(Graphene.elementPresent.locator(LOC_TAB));
        selenium.click(LOC_TAB);

        /*
        waitModelUpdate.until(new Condition() {
            public boolean isTrue() {
                return belongsClass("rich-tab-active", LOC_TAB);
            }
        });
        */
        Graphene.waitModel.until(new SeleniumCondition() {            
            public boolean isTrue() {
                return selenium.belongsClass(LOC_TAB, "rich-tab-active");
            }
        });
        
    }

    /**
     * Controls that for each method should be started new browser session.
     * 
     * It means that after each method except last is browser session stopped
     * and before each method except first is started clean browser session.
     */
    protected void setCleanSessionForEachMethod(boolean cleanSessionForEachMethod) {
        this.cleanSessionForEachMethod = cleanSessionForEachMethod;
    }

    private boolean cleanSessionForEachMethod = false;

    @BeforeMethod
    public void startBrowserIfNotFirstTestMethod(ITestContext context) {
        if (cleanSessionForEachMethod) {
            if (0 < getRunnedTestCount(context)) {
                selenium.start();
            }
        }
    }

    @AfterMethod
    public void stopBrowserIfNotLastTestMethod(ITestContext context) {
        if (cleanSessionForEachMethod) {
            if (getRunnedTestCount(context) + 1 < context.getAllTestMethods().length) {
                selenium.stop();
            }
        }
    }

    private int getRunnedTestCount(ITestContext context) {
        return context.getPassedTests().size() + context.getSkippedTests().size() + context.getFailedTests().size();
    }
    
    /**
     * Aligns screen to top (resp. bottom) of element given by locator.
     * 
     * TODO should be reimplemented to use of JQuery.scrollTo
     * 
     * @param locator
     *            of element which should be screen aligned to
     * @param alignToTop
     *            should be top border of screen aligned to top border of
     *            element
     */
    public void scrollIntoView(String locator, boolean alignToTop) {
        selenium.scrollIntoView(jq(locator), alignToTop);
    }

    public void scrollIntoView(JQueryLocator locator, boolean alignToTop) {
        selenium.scrollIntoView(locator, alignToTop);
    }

    /**
     * From given properties class gets property using "property" key or if
     * value with given key doesn't exist, returns substitution'
     * 
     * @param properties
     *            loaded properties
     * @param property
     *            key that will be found in properties
     * @param subst
     *            substitution which will be used in the case that property with
     *            given key doesn't exist
     * @throws IllegalStateException
     *             when property wasn't found and substitution isn't set
     * @return property value for given key or substitution if it doesn't exist
     */
    private String getProperty(Properties properties, String property, String subst) {
        if (properties == null || properties.getProperty(property) == null) {
            if (StringUtils.isEmpty(subst)) {
                throw new IllegalStateException("property '" + property + "' wasn't found and substitution isn't set");
            } else {
                return subst;
            }
        } else {
            return properties.getProperty(property);
        }
    }

    /**
     * Gets the property from locatorsProperties
     * 
     * @param prop
     *            the name of the property
     * @param subst
     *            the value which is returned in the case the property isn't set
     * @throws IllegalStateException
     *             when property wasn't found and substitution isn't set
     * @return the property
     * @see org.jboss.test.selenium.AbstractSeleniumTestCase#getLocatorsProperties()
     *      getLocatorsProperties()
     */
    public String getLoc(String prop, String subst) {
        return getProperty(locatorsProperties, prop, subst);
    }

    /**
     * Gets the property from locatorsProperties
     * 
     * @param prop
     *            the name of the property
     * @throws IllegalStateException
     *             when property wasn't found
     * @return the property
     * @see org.jboss.test.selenium.AbstractSeleniumTestCase#getLocatorsProperties()
     *      getLocatorsProperties()
     */
    public String getLoc(String prop) {
        return getLoc(prop, null);
    }

    /**
     * Gets the property from messagesProperties and use it to format Message
     * with given arguments
     * 
     * @param prop
     *            the name of the property with message format.
     * @param args
     *            an array of atributes to be formatted and substituted to prop
     * @throws IllegalStateException
     *             when property wasn't found
     * @return the property
     * @see org.jboss.test.selenium.AbstractSeleniumTestCase#getLocatorsProperties()
     *      getLocatorsProperties()
     */
    public String formatLoc(String prop, Object... args) {
        return format(getLoc(prop, null), args);
    }

    /**
     * Gets the property from messagesProperties.
     * 
     * @param prop
     *            the name of the property
     * @param subst
     *            the value which is returned in the case the property isn't set
     * @throws IllegalStateException
     *             when property wasn't found and substitution isn't set
     * @return the property
     * @see org.jboss.test.selenium.AbstractSeleniumTestCase#getMessagesProperties()
     *      getMessagesProperties()
     */
    public String getMsg(String prop, String subst) {
        return getProperty(messagesProperties, prop, subst);
    }

    /**
     * Gets the property from messagesProperties.
     * 
     * @param prop
     *            the name of the property
     * @throws IllegalStateException
     *             when property wasn't found
     * @return the property
     * @see org.jboss.test.selenium.AbstractSeleniumTestCase#getMessagesProperties()
     *      getMessagesProperties()
     */
    public String getMsg(String prop) {
        return getMsg(prop, null);
    }

    /**
     * Waits for specified time in ms. Used mostly in AJAX based tests.
     * 
     * @param time
     *            the time (in ms) to be waited for.
     */
    public void waitFor(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public String waitForAttributeChangesAndReturn(AttributeLocator<JQueryLocator> attributeLocator, String attributeValue) {
        waitForAttributeChanges(attributeLocator, attributeValue);
        return selenium.getAttribute(attributeLocator);
    }
    
    /**
     * Wait for attribute of element given by attributeLocator changes from
     * attributeValue to another value and returns this new value.
     * 
     * @param attributeLocator
     *            locator of attribute
     * @param attributeValue
     *            current value attribute what we are waiting for change
     * @return new value of attribute
     */
    public void waitForAttributeChanges(final AttributeLocator<JQueryLocator> attributeLocator, String attributeValue) {
        
        AttributeRetriever retriever = Graphene.retrieveAttribute.attributeLocator(attributeLocator);
        retriever.setValue(attributeValue);
        JavaScript js = retriever.getJavaScriptRetrieve();

        selenium.waitForCondition(js);
        
        /*
        return Wait.waitForChangeAndReturn(attributeValue, new Retrieve<String>() {
            public String retrieve() {
                return selenium.getAttribute(attributeLocator);
            }
        });
        */
    }

    public String waitForTextChangesAndReturn(final JQueryLocator loc, final String text) {
        Wait.waitSelenium.until(new SeleniumCondition() {
            public boolean isTrue() {
                return !text.equals(selenium.getText(loc));
            }
        });
        return selenium.getText(loc);
    }

    public void waitForText(String text) {
        final String expectedText = text;
        Wait.waitSelenium.until(new SeleniumCondition() {            
            public boolean isTrue() {
                return selenium.isTextPresent(expectedText);
            }
        });
    }

    public void waitForElement(String locator) {
        waitForElement(jq(locator));
    }

    public void waitForElement(JQueryLocator locator) {
        Wait.waitSelenium.until(Graphene.elementPresent.locator(locator));
    }

    public void waitForTextEquals(String locator, String text) {
        waitForTextEquals(jq(locator), text);
    }

    public void waitForTextEquals(JQueryLocator locator, String text) {
        Wait.waitSelenium.until(Graphene.textEquals.locator(locator).text(text));
    }

    public boolean belongsClass(String expectedClass, String locator) {
        return belongsClass(expectedClass, jq(locator));
    }

    public boolean belongsClass(String expectedClass, JQueryLocator locator) {
        return selenium.belongsClass(locator, expectedClass);
    }

    public String getStyle(ElementLocator<JQueryLocator> locator, CssProperty property) { 
        return selenium.getStyle(locator, property);
    }
    
    public String getTextOrNull(ElementLocator<JQueryLocator> locator) {
        String val = selenium.getText(locator);
        if (val!= null && !"".equals(val)) {
            return val;
        }
        return null;
    }

    public int getJQueryCount(String locator) {
        return getJQueryCount(jq(locator));
    }

    public int getJQueryCount(JQueryLocator locator) {
        return selenium.getCount(locator);
    }

    public boolean isDisplayed(String locator) {
        return isDisplayed(jq(locator));
    }

    public boolean isDisplayed(JQueryLocator locator) {
        return Graphene.elementVisible.locator(locator).isTrue();
    }
    
    public void mouseOverAt(ElementLocator<JQueryLocator> locator, Point coords) {
        selenium.mouseOverAt(locator, coords);
    }
}
