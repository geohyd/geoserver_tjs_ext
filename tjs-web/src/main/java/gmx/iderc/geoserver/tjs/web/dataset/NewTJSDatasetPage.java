/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs.web.dataset;

import gmx.iderc.geoserver.tjs.TJSExtension;
import gmx.iderc.geoserver.tjs.catalog.DataStoreInfo;
import gmx.iderc.geoserver.tjs.catalog.DatasetInfo;
import gmx.iderc.geoserver.tjs.data.TJSDataStore;
import gmx.iderc.geoserver.tjs.web.TJSBasePage;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.model.IModel;
import org.geotools.data.util.NullProgressListener;

/**
 * Page that presents a list of vector and raster store types available in the classpath in order to
 * choose what kind of data source to create, as well as which workspace to create the store in.
 *
 * <p>Meant to be called by {@link DataPage} when about to add a new datastore or coverage.
 *
 * @author Gabriel Roldan
 */
@SuppressWarnings("serial")
public class NewTJSDatasetPage extends TJSBasePage {

    DropDownChoice dataStoreChooser;
    DropDownChoice datasetNameChooser;
    IModel dataStoreModel;
    IModel datasetNameModel;
    FeedbackPanel fp;

    Form storeForm;
    static final Logger LOGGER = Logger.getLogger(NewTJSDatasetPage.class.getName());

    @SuppressWarnings("serial")
    public NewTJSDatasetPage() {

        final boolean thereAreDataStores = !getTJSCatalog().getDataStores().isEmpty();

        if (!thereAreDataStores) {
            super.error(
                    (String)
                            new ResourceModel("NewTJSDatasetPage.noDataStoreErrorMessage")
                                    .getObject());
        }

        final DatasetInfo newDatasetInfo = getTJSCatalog().getFactory().newDataSetInfo();
        final IModel datasetModel = new CompoundPropertyModel(newDatasetInfo);

        storeForm =
                new Form("tjsNewDatasetForm") {

                    @Override
                    protected void onSubmit() {
                        DatasetInfo newDatasetInfo = (DatasetInfo) datasetModel.getObject();
                        storeForm.setResponsePage(
                                new DatasetNewPage(
                                        newDatasetInfo.getDataStore().getId(),
                                        newDatasetInfo.getDatasetName()));
                    }
                };
        add(storeForm);

        final IModel dataStoreLabelModel = new ResourceModel("dataStore", "Tematic Data Store");
        Label label = new Label("dataStoreChooserLabel", dataStoreLabelModel.getObject() + "*");
        storeForm.add(label);

        dataStoreModel = new PropertyModel(datasetModel, "dataStore");
        datasetNameModel = new PropertyModel(datasetModel, "datasetName");

        // the drop down field, with a decorator for validations
        /*dataStoreChooser =
        new DropDownChoice(
                "dataStoreChooser",
                dataStoreModel,
                new DataStoreInfoListModel(),
                new DataStoreChoiceRenderer());*/
        dataStoreChooser =
                new DropDownChoice(
                        "dataStoreChooser",
                        dataStoreModel,
                        new DataStoreInfoListModel(),
                        new ChoiceRenderer("name", "id"));
        dataStoreChooser.setRequired(true);
        // set the label to be the paramLabelModel otherwise a validation error would look like
        // "Parameter 'paramValue' is required"
        dataStoreChooser.setLabel(dataStoreLabelModel);
        dataStoreChooser.setOutputMarkupId(true);

        dataStoreChooser.add(
                new AjaxFormComponentUpdatingBehavior("onchange") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        DatasetInfo datasetInfo = (DatasetInfo) datasetModel.getObject();
                        DataStoreInfo dataStore = (DataStoreInfo) dataStoreChooser.getModelObject();
                        datasetInfo.setDataStore(dataStore);

                        IModel datasetNamesModel = new DatasetNamesModel(dataStoreModel);
                        datasetNameChooser.setChoices(datasetNamesModel);
                        target.add(datasetNameChooser);
                        storeForm.getFeedbackMessages().clear();
                        if (datasetNameChooser.getChoices().size() <= 0) {
                            storeForm.error(
                                    "Cannot list table, maybe an error on datastore configuration");
                        }
                        target.add(fp);
                    }
                });
        storeForm.add(dataStoreChooser);

        final IModel datasetNameLabelModel = new ResourceModel("datasetName", "Dataset");
        Label datasetNamelabel =
                new Label("datasetNameChooserLabel", datasetNameLabelModel.getObject() + "*");
        storeForm.add(datasetNamelabel);
        datasetNameChooser =
                new DropDownChoice(
                        "datasetNameChooser",
                        datasetNameModel,
                        new DatasetNamesModel(null),
                        new ChoiceRenderer());
        // new DatasetNamesChoiceRenderer());
        datasetNameChooser.setRequired(true);
        // set the label to be the paramLabelModel otherwise a validation error would look like
        // "Parameter 'paramValue' is required"
        datasetNameChooser.setLabel(dataStoreLabelModel);
        datasetNameChooser.setOutputMarkupId(true);
        datasetNameChooser.add(
                new AjaxFormComponentUpdatingBehavior("onchange") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        DatasetInfo datasetInfo = (DatasetInfo) datasetModel.getObject();
                        String datasetName = (String) datasetNameChooser.getModelObject();
                        datasetInfo.setDatasetName(datasetName);
                    }
                });

        storeForm.add(datasetNameChooser);
        fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        storeForm.add(fp);

        SubmitLink submitLink = new SubmitLink("submit", storeForm);
        storeForm.add(submitLink);
        storeForm.setDefaultButton(submitLink);

        AjaxLink cancelLink =
                new AjaxLink("cancel") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(DatasetPage.class);
                    }
                };
        storeForm.add(cancelLink);
    }

    class DataStoreInfoListModel extends LoadableDetachableModel {

        @Override
        protected Object load() {
            List<DataStoreInfo> stores =
                    new ArrayList<DataStoreInfo>(TJSExtension.getTJSCatalog().getDataStores());
            Collections.sort(stores, new DataStoreInfoComparator());
            return stores;
        }

        class DataStoreInfoComparator implements Comparator<DataStoreInfo> {

            public int compare(DataStoreInfo w1, DataStoreInfo w2) {
                return w1.getName().compareToIgnoreCase(w2.getName());
            }
        }
    }

    /*class DataStoreChoiceRenderer<T> implements IChoiceRenderer {

        public Object getDisplayValue(Object object) {
            return ((DataStoreInfo) object).getName();
        }

        public String getIdValue(Object object, int index) {
            return ((DataStoreInfo) object).getId();
        }

        @Override
        public DataStoreInfoListModel getObject(String id, IModel choices) {
            return (DataStoreInfoListModel)
                    ((ArrayList<DataStoreInfoListModel>) choices.getObject()).get(0);
        }
    }*/

    class DatasetNamesModel extends LoadableDetachableModel {

        IModel dataStoreModel;

        public DatasetNamesModel(IModel dataStoreModel) {
            this.dataStoreModel = dataStoreModel;
        }

        @Override
        protected Object load() {
            try {
                if (dataStoreModel != null) {
                    DataStoreInfo dataStoreInfo = (DataStoreInfo) dataStoreModel.getObject();
                    if (dataStoreInfo != null) {
                        TJSDataStore store =
                                dataStoreInfo.getTJSDataStore(new NullProgressListener());
                        String[] names = store.getAllAvaliableDatasources();
                        Arrays.sort(names);
                        List<String> nameList = Arrays.asList(names);
                        return nameList;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Cannot load list of database table : " + e);
            }
            return new ArrayList<String>();
        }
    }

    /*class DatasetNamesChoiceRenderer<T> implements IChoiceRenderer {

        public Object getDisplayValue(Object object) {
            return (String) object;
        }

        public String getIdValue(Object object, int index) {
            return (String) object;
        }

        @Override
        public T getObject(String id, IModel choices) {
            return ((IModel<? extends List<? extends T>>) choices)
                    .getObject()
                    .get(Integer.parseInt(id));
        }
    }*/
}
