/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.gina;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.annotations.FMLAttribute.AttributeKind;
import org.openflexo.foundation.fml.annotations.SeeAlso;
import org.openflexo.foundation.fml.annotations.UsageExample;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FreeModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.ModelSlotObject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.DeserializationFinalizer;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.validation.ValidationError;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.technologyadapter.gina.fml.FIBComponentRole;
import org.openflexo.technologyadapter.gina.fml.editionaction.ConfigureGINAFIBComponent;
import org.openflexo.technologyadapter.gina.model.GINAFIBComponent;
import org.openflexo.technologyadapter.gina.rm.GINAFIBComponentResource;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link ModelSlot} used to reference a {@link GINAFIBComponent}
 * 
 * @author Sylvain Guérin
 * 
 */
@DeclareFlexoRoles({ FIBComponentRole.class })
@DeclareEditionActions({ ConfigureGINAFIBComponent.class })
@DeclareFetchRequests({})
@ModelEntity
@ImplementationClass(FIBComponentModelSlot.FIBComponentModelSlotImpl.class)
@XMLElement
@FML(
		value = "FIBComponent",
		description = "<html>This ModelSlot represents access to a GINAFIBComponent, a graphical user interface<br>"
				+ "declared from a .fib template, whose variables are bound to the model through a list of assignments"
				+ "</html>",
		examples = { @UsageExample(
				example = "GINAFIBComponent ui with GINA::FIBComponent(templateComponentURI=\"http://acme.org/MyComponent.fib\","
						+ "assignments={VariableAssignment:(variable=\"data\",value=this)});",
				description = "Declares a model slot called 'ui' bound to the 'MyComponent.fib' template, whose 'data' variable is this instance") },
		references = { @SeeAlso(ConfigureGINAFIBComponent.class) })
public interface FIBComponentModelSlot extends FreeModelSlot<GINAFIBComponent, GINAFIBComponentResource> {

	@PropertyIdentifier(type = String.class)
	public static final String TEMPLATE_COMPONENT_URI_KEY = "templateComponentURI";
	@PropertyIdentifier(type = FlexoResource.class)
	public static final String TEMPLATE_RESOURCE_KEY = "templateResource";
	@PropertyIdentifier(type = VariableAssignment.class, cardinality = Cardinality.LIST)
	public static final String ASSIGNMENTS_KEY = "assignments";

	@Getter(value = TEMPLATE_COMPONENT_URI_KEY)
	@XMLAttribute
	/**
	 * BEWARE: this URI is a plain string attribute, resolved lazily by {@link #getTemplateResource()} against the resource manager. When it
	 * resolves to nothing, {@link org.openflexo.technologyadapter.gina.fml.FMLControlledFIBVirtualModelNature#hasNature} starts returning
	 * false and the VirtualModel quietly loses its user interface, <b>with nothing logged</b> - the application just shows an empty panel.
	 * 
	 * <p>
	 * {@link FIBComponentModelSlotMustReferenceNonNullTemplateResource} below does report it as a validation error, so the break is silent
	 * at runtime rather than in validation - but that guard only fires if something actually validates the VirtualModel AND the GINA
	 * technology adapter is activated, since the FML validation model is built from the activated adapters' model-slot classes. A suite that
	 * only loads its models, or that activates other adapters, sees nothing. Note also that the rule's message key
	 * <code>fib_component_model_slot_must_reference_nonnull_template_resource</code> is absent from the GINA dictionaries, so the error
	 * arrives un-localized.
	 */
	@FMLAttribute(value = TEMPLATE_COMPONENT_URI_KEY, required = true, description = "<html>URI of the .fib template component."
			+ "<br>An unresolved URI costs the VirtualModel its FML-controlled-FIB nature, with nothing logged at run-time.</html>")
	public String getTemplateComponentURI();

	@Setter(TEMPLATE_COMPONENT_URI_KEY)
	public void setTemplateComponentURI(String templateComponentURI);

	@Getter(TEMPLATE_RESOURCE_KEY)
	public GINAFIBComponentResource getTemplateResource();

	@Setter(TEMPLATE_RESOURCE_KEY)
	public void setTemplateResource(GINAFIBComponentResource templateResource);

	@Getter(value = ASSIGNMENTS_KEY, cardinality = Cardinality.LIST, inverse = VariableAssignment.OWNER_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	@FMLAttribute(value = ASSIGNMENTS_KEY, kind = AttributeKind.InstancesList)
	public List<VariableAssignment> getAssignments();

	@Setter(ASSIGNMENTS_KEY)
	public void setAssignments(List<VariableAssignment> assignments);

	@Adder(ASSIGNMENTS_KEY)
	public void addToAssignments(VariableAssignment aAssignment);

	@Remover(ASSIGNMENTS_KEY)
	public void removeFromAssignments(VariableAssignment aAssignment);

	public VariableAssignment createAssignment();

	public VariableAssignment deleteAssignment(VariableAssignment assignment);

	@Override
	public GINATechnologyAdapter getModelSlotTechnologyAdapter();

	public static abstract class FIBComponentModelSlotImpl extends FreeModelSlotImpl<GINAFIBComponent, GINAFIBComponentResource>
			implements FIBComponentModelSlot {

		protected String templateComponentURI;
		private GINAFIBComponentResource templateResource;

		@Override
		public Class<GINATechnologyAdapter> getTechnologyAdapterClass() {
			return GINATechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			if (FIBComponentRole.class.isAssignableFrom(flexoRoleClass)) {
				return "component";
			}
			return "";
		}

		@Override
		public Type getType() {
			return GINAFIBComponent.class;
		}

		@Override
		public GINATechnologyAdapter getModelSlotTechnologyAdapter() {
			return (GINATechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		@Override
		public String getTemplateComponentURI() {
			if (getTemplateResource() != null) {
				return getTemplateResource().getURI();
			}
			return templateComponentURI;
		}

		@Override
		public void setTemplateComponentURI(String templateComponentURI) {
			if ((templateComponentURI == null && this.templateComponentURI != null)
					|| (templateComponentURI != null && !templateComponentURI.equals(this.templateComponentURI))) {
				String oldValue = this.templateComponentURI;
				this.templateComponentURI = templateComponentURI;
				getPropertyChangeSupport().firePropertyChange("templateComponentURI", oldValue, templateComponentURI);
			}
		}

		@Override
		public GINAFIBComponentResource getTemplateResource() {
			if (templateResource == null && StringUtils.isNotEmpty(templateComponentURI)
					&& getServiceManager().getResourceManager() != null) {
				// System.out.println("Looking up " + templateDocumentURI);
				templateResource = (GINAFIBComponentResource) getServiceManager().getResourceManager().getResource(templateComponentURI);
				// System.out.println("templateResource = " + returned);
				// for (FlexoResource r : getServiceManager().getResourceManager().getRegisteredResources()) {
				// System.out.println("> " + r.getURI());
				// }
			}
			return templateResource;
		}

		@Override
		public void setTemplateResource(GINAFIBComponentResource templateResource) {
			if (templateResource != this.templateResource) {
				GINAFIBComponentResource oldValue = this.templateResource;
				this.templateResource = templateResource;
				this.templateComponentURI = null;
				getPropertyChangeSupport().firePropertyChange("templateResource", oldValue, templateResource);
			}
		}

		@Override
		public VariableAssignment createAssignment() {
			System.out.println("Called createAssignment()");
			VariableAssignment newAssignment = getFMLModelFactory().newInstance(VariableAssignment.class);
			newAssignment.setVariable("data" + (getAssignments().size() > 0 ? getAssignments().size() + 1 : ""));
			addToAssignments(newAssignment);
			return newAssignment;
		}

		@Override
		public VariableAssignment deleteAssignment(VariableAssignment assignment) {
			System.out.println("Called deleteAssignment() with " + assignment);
			removeFromAssignments(assignment);
			return assignment;
		}

	}

	@ModelEntity
	@ImplementationClass(VariableAssignment.VariableAssignmentImpl.class)
	@XMLElement(xmlTag = "VariableAssignment")
	@FML("VariableAssignment")
	public static interface VariableAssignment extends ModelSlotObject<GINAFIBComponent> {
		@PropertyIdentifier(type = FIBComponentModelSlot.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = String.class)
		public static final String VARIABLE_KEY = "variable";
		@PropertyIdentifier(type = String.class)
		public static final String VARIABLE_TYPE_KEY = "variableType";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VALUE_KEY = "value";
		@PropertyIdentifier(type = Boolean.class)
		public static final String MANDATORY_KEY = "mandatory";

		@Getter(value = OWNER_KEY)
		@CloningStrategy(StrategyType.IGNORE)
		public FIBComponentModelSlot getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBComponentModelSlot customColumn);

		@Getter(value = VARIABLE_KEY)
		@XMLAttribute
		@FMLAttribute(value = VARIABLE_KEY, required = true, description = "<html>name of the variable declared by the .fib component</html>")
		public String getVariable();

		@Setter(VARIABLE_KEY)
		public void setVariable(String variable);

		/**
		 * Deliberately NOT exposed as an {@link FMLAttribute}: it is only used as a fallback when the value binding is unset or invalid
		 * (see GINAFIBComponent), and a Type-valued FML attribute would need an updater translating it into the current typing space.
		 */
		@Getter(value = VARIABLE_TYPE_KEY, isStringConvertable = true)
		@XMLAttribute
		public Type getVariableType();

		@Setter(VARIABLE_TYPE_KEY)
		public void setVariableType(Type type);

		@Getter(value = VALUE_KEY)
		@XMLAttribute
		@FMLAttribute(value = VALUE_KEY, required = true, description = "<html>value assigned to that variable</html>")
		public DataBinding<Object> getValue();

		@Setter(VALUE_KEY)
		public void setValue(DataBinding<Object> value);

		@Getter(value = MANDATORY_KEY, defaultValue = "false")
		@XMLAttribute
		public boolean isMandatory();

		@Setter(MANDATORY_KEY)
		public void setMandatory(boolean mandatory);

		@Override
		@DeserializationFinalizer
		public void finalizeDeserialization();

		public static abstract class VariableAssignmentImpl extends FMLObjectImpl implements VariableAssignment {

			private DataBinding<Object> value;

			@Override
			public void setOwner(FIBComponentModelSlot referencedComponent) {
				performSuperSetter(OWNER_KEY, referencedComponent);
				if (value != null) {
					value.setOwner(referencedComponent);
				}
			}

			@Override
			public DataBinding<Object> getValue() {
				if (value == null) {
					value = new DataBinding<>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
				}
				return value;
			}

			@Override
			public void setValue(DataBinding<Object> value) {
				if (value != null) {
					value.setOwner(getOwner()); // Warning, still null while deserializing
					value.setDeclaredType(Object.class);
					value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					this.value = value;
				}
				else {
					getValue();
				}
			}

			@Override
			public void finalizeDeserialization() {

				super.finalizeDeserialization();

				if (value != null) {
					value.setOwner(getOwner());
					// value.decode();
				}
			}

			@Override
			public BindingModel getBindingModel() {
				if (getOwner() != null) {
					return getOwner().getBindingModel();
				}
				return null;
			}

			@Override
			public FMLCompilationUnit getResourceData() {
				if (getOwner() != null) {
					return getOwner().getResourceData();
				}
				return null;
			}

			@Override
			public FIBComponentModelSlot getModelSlot() {
				return getOwner();
			}

			@Override
			public TechnologyAdapter getModelSlotTechnologyAdapter() {
				return getOwner().getModelSlotTechnologyAdapter();
			}

			@Override
			public FlexoConcept getFlexoConcept() {
				if (getOwner() != null) {
					return getOwner().getFlexoConcept();
				}
				return null;
			}

			@Override
			public VirtualModel getOwningVirtualModel() {
				if (getOwner() != null) {
					return getOwner().getOwningVirtualModel();
				}
				return null;
			}

		}
	}

	@DefineValidationRule
	class FIBComponentModelSlotMustReferenceNonNullTemplateResource
			extends ValidationRule<FIBComponentModelSlotMustReferenceNonNullTemplateResource, FIBComponentModelSlot> {
		public FIBComponentModelSlotMustReferenceNonNullTemplateResource() {
			super(FIBComponentModelSlot.class, "fib_component_model_slot_must_reference_nonnull_template_resource");
		}

		@Override
		public ValidationIssue<FIBComponentModelSlotMustReferenceNonNullTemplateResource, FIBComponentModelSlot> applyValidation(
				FIBComponentModelSlot modelSlot) {
			if (modelSlot.getTemplateResource() == null) {
				return new ValidationError<>(this, modelSlot, "fib_component_model_slot_must_reference_nonnull_template_resource",
						Collections.emptyList());
			}
			return null;
		}
	}

}
