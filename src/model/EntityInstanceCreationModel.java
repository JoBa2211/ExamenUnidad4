package model;

import java.util.HashMap;
import java.util.Map;

public class EntityInstanceCreationModel {
    private EntityModel selectedEntityModel;
    private Map<String, Object> attributeValues = new HashMap<>();

    public EntityModel getSelectedEntityModel() {
        return selectedEntityModel;
    }

    public void setSelectedEntityModel(EntityModel selectedEntityModel) {
        this.selectedEntityModel = selectedEntityModel;
    }

    public Map<String, Object> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValue(String attributeName, Object value) {
        attributeValues.put(attributeName, value);
    }

    public void clearAttributeValues() {
        attributeValues.clear();
    }
}
