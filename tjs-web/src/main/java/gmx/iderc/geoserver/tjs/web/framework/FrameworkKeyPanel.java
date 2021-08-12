/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs.web.framework;

import gmx.iderc.geoserver.tjs.catalog.FrameworkInfo;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.FeatureTypeInfo;

/** A label + workspace dropdown form panel */
@SuppressWarnings("serial")
public class FrameworkKeyPanel extends Panel {

    FormComponentFeedbackBorder feedback;

    private DropDownChoice featureTypeChoice;
    private DropDownChoice keyPropertyChoice;
    private DropDownChoice keyTitlePropertyChoice;

    IModel frameworkKeyModel;
    IModel frameworkKeyTitleModel;
    IModel featureTypeModel;

    public FrameworkKeyPanel(
            final String id,
            final IModel frameworkModel,
            final boolean required,
            TextField nameTextField) {
        // make the value of the combo field the model of this panel, for easy
        // value retriaval
        super(id, frameworkModel);

        featureTypeModel = new PropertyModel(frameworkModel, "featureType");
        frameworkKeyModel = new PropertyModel(frameworkModel, "frameworkKey");
        frameworkKeyTitleModel = new PropertyModel(frameworkModel, "frameworkKeyTitle");
        final IModel featureTypeLabelModel = new ResourceModel("featureType", "Feature Type");

        // the label
        String requiredMark = required ? " *" : "";
        Label label = new Label("paramName", featureTypeLabelModel.getObject() + requiredMark);
        add(label);

        // the drop down field, with a decorator for validations
        featureTypeChoice =
                new DropDownChoice(
                        "paramValue",
                        featureTypeModel,
                        new FeatureTypesModel(),
                        // new FeatureTypeChoiceRenderer()
                        new ChoiceRenderer("name", "id"));
        featureTypeChoice.setRequired(required);
        // set the label to be the paramLabelModel otherwise a validation error would look like
        // "Parameter 'paramValue' is required"
        featureTypeChoice.setLabel(featureTypeLabelModel);
        featureTypeChoice.setOutputMarkupId(true);

        featureTypeChoice.add(
                new AjaxFormComponentUpdatingBehavior("change") {

                    protected void onUpdate(AjaxRequestTarget target) {
                        // Reset the phone model dropdown when the vendor changes
                        FrameworkInfo frameworkInfo = (FrameworkInfo) frameworkModel.getObject();
                        FeatureTypeInfo featureType =
                                (FeatureTypeInfo) featureTypeChoice.getModelObject();
                        frameworkInfo.setFeatureType(featureType);

                        IModel attributesModel = new FeatureTypePropertiesModel(featureTypeModel);
                        List<AttributeTypeInfo> atts =
                                (List<AttributeTypeInfo>)
                                        new FeatureTypePropertiesModel(featureTypeModel).load();
                        keyPropertyChoice.setChoices(attributesModel);
                        // target.addComponent(keyPropertyChoice);
                        target.add(keyPropertyChoice);

                        keyTitlePropertyChoice.setChoices(attributesModel);
                        // target.addComponent(keyTitlePropertyChoice);
                        target.add(keyTitlePropertyChoice);

                        /**
                         * I choose to set the same name of the selected feature type for create a
                         * frameworkURI match with the frameworkURI of the WFS service source.
                         * Thanks to this, when we send a DescribeFrameworks request, you can find
                         * easily the WFS service with the frameworkURI. Otherwise, I don't find how
                         * can I do this. Maybe this is a mistake to do this ?
                         */
                        nameTextField.getModel().setObject(featureType.getName());
                        target.add(nameTextField);
                    }
                });

        // feedback = new FormComponentFeedbackBorder("border");

        // feedback.add(featureTypeChoice);
        add(featureTypeChoice);

        final IModel keyLabelModel = new ResourceModel("frameworkKey", "Framework Key");
        Label keyColumnlabel = new Label("frameworkKeyLabel", keyLabelModel);
        add(keyColumnlabel);

        IModel attributesModel = new FeatureTypePropertiesModel(featureTypeModel);
        List<AttributeTypeInfo> atts =
                (List<AttributeTypeInfo>) new FeatureTypePropertiesModel(featureTypeModel).load();
        keyPropertyChoice =
                new DropDownChoice(
                        "frameworkKeyValue",
                        frameworkKeyModel,
                        attributesModel,
                        new ChoiceRenderer());
        keyPropertyChoice.setRequired(required);
        keyPropertyChoice.setOutputMarkupId(true);
        // feedback.add(keyPropertyChoice);
        add(keyPropertyChoice);

        final IModel keyTitleLabelModel =
                new ResourceModel("frameworkKeyTitle", "Framework Key Title");
        Label keyTitleColumnlabel = new Label("frameworkKeyTitleLabel", keyTitleLabelModel);
        add(keyTitleColumnlabel);

        keyTitlePropertyChoice =
                new DropDownChoice(
                        "frameworkKeyTitleValue",
                        frameworkKeyTitleModel,
                        attributesModel,
                        new ChoiceRenderer());
        keyTitlePropertyChoice.setRequired(false);
        keyTitlePropertyChoice.setOutputMarkupId(true);
        // feedback.add(keyTitlePropertyChoice);
        add(keyTitlePropertyChoice);

        // add(feedback);
    }
}
