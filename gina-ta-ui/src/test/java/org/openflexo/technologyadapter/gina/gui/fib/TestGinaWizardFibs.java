/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-rt-technologyadapter-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.gina.gui.fib;

import org.junit.Test;
import org.openflexo.gina.test.GenericFIBTestCase;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

public class TestGinaWizardFibs extends GenericFIBTestCase {

	public static void main(String[] args) {
		System.out.println(
				generateFIBTestCaseClass(((FileResourceImpl) ResourceLocator.locateResource("Fib/Wizard")).getFile(), "Fib/Wizard/"));
	}

	@Test
	public void testCreateNewGINAFIBComponent() {
		validateFIB("Fib/Wizard/CreateNewGINAFIBComponent.fib");
	}

	@Test
	public void testDescribeFMLControlledFIBVirtualModel() {
		validateFIB("Fib/Wizard/DescribeFMLControlledFIBVirtualModel.fib");
	}

	@Test
	public void testDescribeNewComponent() {
		validateFIB("Fib/Wizard/DescribeNewComponent.fib");
	}

	@Test
	public void testChooseStrategy() {
		validateFIB("Fib/Wizard/GivesFMLControlledFIBVirtualModelNature/ChooseStrategy.fib");
	}

	@Test
	public void testCreateNewGINAFIBComponent2() {
		validateFIB("Fib/Wizard/GivesFMLControlledFIBVirtualModelNature/CreateNewGINAFIBComponent.fib");
	}

	@Test
	public void testUseExistingGINAFIBComponent2() {
		validateFIB("Fib/Wizard/GivesFMLControlledFIBVirtualModelNature/UseExistingGINAFIBComponent.fib");
	}

	@Test
	public void testUseExistingGINAFIBComponent() {
		validateFIB("Fib/Wizard/UseExistingGINAFIBComponent.fib");
	}

}
