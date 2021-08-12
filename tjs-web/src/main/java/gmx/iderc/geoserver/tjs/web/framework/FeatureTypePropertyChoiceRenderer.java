/* Copyright (c) 2001 - 2009 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package gmx.iderc.geoserver.tjs.web.framework;

import java.util.*;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.WorkspaceInfo;

/** Simple choice renderer for {@link WorkspaceInfo} */
@SuppressWarnings("serial")
public class FeatureTypePropertyChoiceRenderer<T> implements IChoiceRenderer {

    public Object getDisplayValue(Object object) {
        return ((AttributeTypeInfo) object).getName();
    }

    public String getIdValue(Object object, int index) {
        return ((AttributeTypeInfo) object).getName();
    }

    @Override
    public T getObject(String id, IModel choices) {
        // TODO JDU
        return ((IModel<? extends List<? extends T>>) choices)
                .getObject()
                .get(Integer.parseInt(id));
    }
}
