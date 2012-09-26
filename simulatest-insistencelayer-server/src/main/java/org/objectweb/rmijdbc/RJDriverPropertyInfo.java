/**
 * RmiJdbc client/server JDBC Driver
 * (C) GIE Dyade (Groupe BULL / INRIA Research Center) 1997
 *
 * @version     1.0
 * @author      Pierre-Yves Gibello (pierreyves.gibello@experlog.com)
 */

package org.objectweb.rmijdbc;

import java.sql.DriverPropertyInfo;

/**
 * This class is used to make DriverPropertyInfo serializable, so the driver's
 * getPropertyInfo() return can be accessed remotely.
 */
public class RJDriverPropertyInfo implements java.io.Serializable {

	private static final long serialVersionUID = 1333094526890374586L;

	protected String name_;
	protected String value_;
	protected boolean required_;
	protected String description_;
	protected String choices_[];

	protected RJDriverPropertyInfo(DriverPropertyInfo dpi) {
		choices_ = dpi.choices;
		description_ = dpi.description;
		name_ = dpi.name;
		required_ = dpi.required;
		value_ = dpi.value;
	}

	public DriverPropertyInfo getPropertyInfo() {
		DriverPropertyInfo dpi = new DriverPropertyInfo(name_, value_);
		dpi.choices = choices_;
		dpi.description = description_;
		dpi.required = required_;
		return dpi;
	}

};
