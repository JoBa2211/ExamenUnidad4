package model;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericEntity {
    // Identificador único (siempre String)
    protected String uniqueId;

    // Mapa de nombre de atributo a valor (puede ser String o Integer)
    protected Map<String, Object> attributes = new HashMap<>();

    public GenericEntity(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // Método abstracto para validar la entidad (por ejemplo, mínimo 3 atributos)
    public abstract boolean isValid();
}
