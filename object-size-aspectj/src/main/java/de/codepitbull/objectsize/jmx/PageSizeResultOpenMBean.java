/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codepitbull.objectsize.jmx;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import de.codepitbull.objectsize.aspectj.PageSizeResult;

/**
 * OpenMBean for accessing the results of size calculations.
 * 
 * @author Jochen Mader
 *
 */
public class PageSizeResultOpenMBean implements DynamicMBean {

	private OpenMBeanInfoSupport PSOMBInfo;

	private TabularDataSupport pageData;

	private static String[] itemNames = { "page", "before", "after" };
	private static String[] itemDescriptions = { "Page class", "Before detach",
			"After detach" };
	private static OpenType[] itemTypes = { SimpleType.STRING, SimpleType.LONG,
			SimpleType.LONG };
	private static CompositeType pageType = null;

	private static String[] indexNames = { "page" };
	private static TabularType pageTabularType = null;

	static {
		try {
			pageType = new CompositeType("page", "Page size info", itemNames,
					itemDescriptions, itemTypes);

			pageTabularType = new TabularType("pages",
					"List of Page Sioze results", pageType, indexNames);

		} catch (OpenDataException e) {
			throw new RuntimeException(e);
		}

	}

	public PageSizeResultOpenMBean() throws OpenDataException {
		OpenMBeanAttributeInfoSupport[] attributes = new OpenMBeanAttributeInfoSupport[] { new OpenMBeanAttributeInfoSupport(
				"PageInfos", "Page Infos sorted by class name",
				pageTabularType, true, false, false) };

		PSOMBInfo = new OpenMBeanInfoSupport(this.getClass().getName(),
				"Page Size OMB", attributes,
				new OpenMBeanConstructorInfoSupport[0],
				new OpenMBeanOperationInfoSupport[0],
				new MBeanNotificationInfo[0]);
		pageData = new TabularDataSupport(pageTabularType);
	}

	public TabularData getPageInfos() {
		return (TabularData) pageData.clone();
	}

	public Object getAttribute(String attribute_name)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {

		if (attribute_name == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException(
					"Attribute name cannot be null"),
					"Cannot call getAttributeInfo with null attribute name");
		}
		if (attribute_name.equals("PageInfos")) {
			return getPageInfos();
		}
		throw new AttributeNotFoundException("Cannot find " + attribute_name
				+ " attribute ");
	}

	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {

		throw new AttributeNotFoundException(
				"No attribute can be set in this MBean");
	}

	public AttributeList getAttributes(String[] attributeNames) {

		if (attributeNames == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException(
					"attributeNames[] cannot be null"),
					"Cannot call getAttributes with null attribute names");
		}
		AttributeList resultList = new AttributeList();

		if (attributeNames.length == 0)
			return resultList;

		for (int i = 0; i < attributeNames.length; i++) {
			try {
				Object value = getAttribute(attributeNames[i]);
				resultList.add(new Attribute(attributeNames[i], value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (resultList);
	}

	public AttributeList setAttributes(AttributeList attributes) {
		return new AttributeList();
	}

	public Object invoke(String operationName, Object[] params,
			String[] signature) throws MBeanException, ReflectionException {

		throw new RuntimeOperationsException(new IllegalArgumentException(
				"No operations defined for this OpenMBean"),
				"No operations defined for this OpenMBean");
	}

	public MBeanInfo getMBeanInfo() {
		return PSOMBInfo;
	}

	public void addPageSizeResult(PageSizeResult pageSizeResult) {
		Object[] itemValues = { pageSizeResult.pageClass.getName(),
				pageSizeResult.sizeBeforeDetach, pageSizeResult.sizeAfterDetach };
		try {
			pageData.put(new CompositeDataSupport(pageType, itemNames,
					itemValues));
		} catch (OpenDataException e) {
			e.printStackTrace();
		}
	}
}