package model;

import java.util.ArrayList;
import java.util.List;

public class EntityModel {
    private List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
    private List<GenericEntity> entities = new ArrayList<>();

    public List<AttributeDefinition> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(List<AttributeDefinition> attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    public List<GenericEntity> getEntities() {
        return entities;
    }

    public void addEntity(GenericEntity entity) {
        entities.add(entity);
    }

    public void removeEntity(GenericEntity entity) {
        entities.remove(entity);
    }

    public static EntityModel create(List<AttributeDefinition> attributeDefinitions) {
        EntityModel model = new EntityModel();
        model.setAttributeDefinitions(attributeDefinitions);
        return model;
    }
}
