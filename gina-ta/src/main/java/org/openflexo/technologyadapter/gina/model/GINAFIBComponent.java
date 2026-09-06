/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.gina.model;

import java.lang.reflect.Type;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBVariable;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.technologyadapter.gina.FIBComponentModelSlot;
import org.openflexo.technologyadapter.gina.FIBComponentModelSlot.VariableAssignment;
import org.openflexo.technologyadapter.gina.GINATechnologyAdapter;
import org.openflexo.technologyadapter.gina.model.GINAFIBComponent.GINAFIBComponentImpl;
import org.openflexo.technologyadapter.gina.rm.GINAFIBComponentResource;

/**
 * A {@link FlexoObject} (an object of model federation infrastructure) that references a {@link FIBComponent}<br>
 * (Access layer between Openflexo-Core and GINA framework)
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(GINAFIBComponentImpl.class)
@XMLElement
public interface GINAFIBComponent
		extends TechnologyObject<GINATechnologyAdapter>, ResourceData<GINAFIBComponent>, BindingEvaluationContext {

	// public static final String RESOURCE_KEY = "resource";
	public static final String COMPONENT_KEY = "component";
	public static final String TECHNOLOGY_ADAPTER_KEY = "technologyAdapter";

	@Getter(COMPONENT_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public FIBComponent getComponent();

	@Setter(COMPONENT_KEY)
	public void setComponent(FIBComponent aComponent);

	@Override
	// @Getter(value = RESOURCE_KEY)
	public GINAFIBComponentResource getResource();

	// @Setter(value = RESOURCE_KEY)
	@Override
	public void setResource(FlexoResource<GINAFIBComponent> aResource);

	@Override
	@Getter(value = TECHNOLOGY_ADAPTER_KEY, ignoreType = true)
	public GINATechnologyAdapter getTechnologyAdapter();

	@Setter(value = TECHNOLOGY_ADAPTER_KEY)
	public void setTechnologyAdapter(GINATechnologyAdapter technologyAdapter);

	/**
	 * Ensure that the whole binding context (BindingFactory and BindingModel) is bound to an {@link VirtualModel} using the specifications
	 * given by a {@link FIBComponentModelSlot}
	 * 
	 * <p>
	 * Call this before validating or showing the component: without it every binding on the <code>data</code> variable is reported broken.
	 * 
	 * <p>
	 * <b>It also swaps the expression parser.</b> The {@link FMLFIBBindingFactory} installed here inherits
	 * <code>FMLBindingFactory.parseExpression()</code>, which delegates to the <em>FML</em> parser - not the grammar the FIB editor accepted
	 * the bindings with. Consequences measured on a real component:
	 * <ul>
	 * <li>a widget named with a <b>leading capital</b> is read as a type name, and every binding through it fails to parse
	 * (<code>expecting: '(' while parsing (ProcessBrowser.selected != null)</code>). Name widgets in lowerCamelCase;</li>
	 * <li>a binding matches a behaviour on <b>arity</b>, not on visibility: a non-public behaviour is perfectly reachable, while
	 * <code>data.doIt()</code> against a model declaring only <code>doIt(X)</code> is not;</li>
	 * <li><code>x.delete()</code> never <em>parses</em>: <code>delete</code> is a keyword that fml.sablecc leaves out of
	 * <code>authorized_kw_in_composite_ident</code>, so any member of that name is unreachable - expose a differently-named behaviour;</li>
	 * <li>a <b>widget-derived value cannot be passed as an argument</b>: <code>data.doSomething(browser.selected)</code> does not resolve
	 * although <code>browser.selected.doSomething()</code> does, and the same argument taken from a model path does. Design the behaviour so
	 * that the selection is the receiver, or give it a no-argument form.</li>
	 * <li>a <code>controller.*</code> binding needs the controller class on the classpath. The components declare
	 * {@link org.openflexo.technologyadapter.gina.controller.FMLControlledFIBController}, which lives in <b>gina-ta-ui</b> and extends
	 * FlexoFIBController, where <code>iconForObject(Object)</code> is declared and alive. A headless suite carrying only gina-ta reports
	 * every such binding unresolved - a classpath artefact, not a defect.</li>
	 * </ul>
	 * 
	 * @param concept
	 * @param modelSlot
	 */
	public void bindTo(FlexoConcept concept, FIBComponentModelSlot modelSlot);

	public abstract static class GINAFIBComponentImpl extends FlexoObjectImpl implements GINAFIBComponent {

		@Override
		public GINATechnologyAdapter getTechnologyAdapter() {
			if (getResource() != null) {
				return getResource().getTechnologyAdapter();
			}
			return null;
		}

		/**
		 * Ensure that the whole binding context (BindingFactory and BindingModel) is bound to an {@link VirtualModel} using the
		 * specifications given by a {@link FIBComponentModelSlot}
		 * 
		 * @param virtualModel
		 * @param modelSlot
		 */
		@Override
		public void bindTo(FlexoConcept concept, FIBComponentModelSlot modelSlot) {
			// System.out.println("******* bindTo " + virtualModel + " using " + modelSlot);

			if (getComponent() == null) {
				return;
			}

			getComponent().setBindingFactory(new FMLFIBBindingFactory(concept.getDeclaringCompilationUnit().getVirtualModel()));

			getComponent().setCustomTypeManager(getResource().getServiceManager().getTechnologyAdapterService());

			if (modelSlot != null) {
				for (VariableAssignment variableAssign : modelSlot.getAssignments()) {
					FIBVariable<?> returned = getComponent().getVariable(variableAssign.getVariable());

					if (returned == null) {
						returned = getComponent().getModelFactory().newFIBVariable(getComponent(), variableAssign.getVariable());
					}
					DataBinding<?> value = variableAssign.getValue();

					if (value != null && value.isSet() && value.isValid()) {
						Type analyzedType = value.getAnalyzedType();

						// System.out.println("analyzedType=" + analyzedType);
						// System.out.println("returned.getType()=" + returned.getType());

						if (TypeUtils.isTypeAssignableFrom(analyzedType, returned.getType())) {
							// Type is conform, does nothing
						}
						else /*if (!TypeUtils.isTypeAssignableFrom(returned.getType(), analyzedType))*/ {
							returned.setType(analyzedType);
							// We force type of variable to be type analyzed from binding
							// System.out.println("****** Force type from " + variableAssign.getVariable() + " to " + returned.getType()
							// + " a " + analyzedType);
						}
						// returned.setType(analyzedType);
					}

					else {
						returned.setType(variableAssign.getVariableType());
					}
				}
			}

			// Bacause type may have changed, we have to revalidate all bindings of the component
			getComponent().revalidateBindings();

		}

		@Override
		public boolean isModified() {
			if (getComponent() != null) {
				return getComponent().isModified();
			}
			return false;
		}
	}
}
