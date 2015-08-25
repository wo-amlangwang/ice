/*******************************************************************************
 * Copyright (c) 2015 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jordan Deyton - Initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.ice.reactor.hdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ice.io.hdf.HdfIOFactory;
import org.eclipse.ice.reactor.LWRComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

public class LWRIOHandler extends HdfIOFactory {

	/**
	 * Logger for handling event messages and other information.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(LWRIOHandler.class);

	/**
	 * Reads data from an input HDF5 file into an LWR.
	 * 
	 * @param uri
	 *            The data source URI.
	 * @return A valid reactor if the file could be completely read,
	 *         {@code null} if the file could not be opened.
	 */
	public List<LWRComponent> readHDF5(URI uri) {

		List<LWRComponent> components = new ArrayList<LWRComponent>();

		// HDF5 constants. Writing out "HDF5Constants." every time is annoying.
		int H5O_TYPE_GROUP = HDF5Constants.H5O_TYPE_GROUP;

		// The status of the previous HDF5 operation. Generally, if it is
		// negative, there was some error.
		int status = -1;

		// Other IDs for HDF5 components.
		int fileId, groupId;

		try {
			// Open the H5 file to read.
			fileId = openFile(uri);

			// Try to get the first group in the file.
			int rootGroupId = openGroup(fileId, "/");
			List<String> children = getChildNames(rootGroupId, H5O_TYPE_GROUP);
			if (children.isEmpty()) {
				throwException("Finding root node.", status);
			}
			// Close the root group.
			closeGroup(rootGroupId);

			LWRComponentReader reader = new LWRComponentReader(this);

			for (String groupName : children) {
				// Add the root prefix.
				String groupPath = "/" + groupName;

				// Open the group, try to read it, and close the group.
				groupId = openGroup(fileId, groupPath);
				try {
					LWRComponent component = reader.read(groupId);
					// If successfully read, add it to the list.
					if (component != null) {
						components.add(component);
					}
				} catch (NullPointerException | HDF5Exception e) {
					logger.error(getClass().getName() + " error: "
							+ "Error while reading group \"" + groupPath
							+ "\".", e);
				}
				closeGroup(groupId);
			}

			// Close the H5file.
			closeFile(fileId);

		} catch (NullPointerException | HDF5LibraryException e) {
			logger.error(
					getClass().getName() + " error: "
							+ "Error while reading the file \"" + uri + "\".",
					e);
		}

		return components;
	}

	/*
	 * Overrides a method from HdfIOFactory.
	 */
	@Override
	public List<Class<?>> getSupportedClasses() {
		List<Class<?>> supportedClasses = new ArrayList<Class<?>>();
		// TODO
		return supportedClasses;
	}

	/*
	 * Overrides a method from HdfIOFactory.
	 */
	@Override
	public String getTag(Class<?> supportedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Overrides a method from HdfIOFactory.
	 */
	@Override
	public Object read(int groupId, String tag)
			throws NullPointerException, HDF5Exception, HDF5LibraryException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Overrides a method from HdfIOFactory.
	 */
	@Override
	public void writeObjectData(int groupId, Object object)
			throws NullPointerException, HDF5Exception, HDF5LibraryException {
		// TODO Auto-generated method stub

	}
}