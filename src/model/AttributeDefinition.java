package model;

public class AttributeDefinition {
    private String name;
    private String typeName; // Serializa este campo
    private boolean isIdentifier;

    // No serializar el tipo directamente
    private transient Class<?> type;

    public AttributeDefinition(String name, Class<?> type, boolean isIdentifier) {
        this.name = name;
        this.type = type;
        this.typeName = type.getSimpleName();
        this.isIdentifier = isIdentifier;
    }

    // Constructor vacío para Gson
    public AttributeDefinition() {}

    public static AttributeDefinition create(String name, Class<?> type, boolean isIdentifier) {
        return new AttributeDefinition(name, type, isIdentifier);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        // Siempre reconstruye el tipo a partir de typeName
        if ("Integer".equals(typeName)) {
            return Integer.class;
        }
        return String.class;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isIdentifier() {
        return isIdentifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Class<?> type) {
        this.type = type;
        this.typeName = type.getSimpleName();
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        // No es necesario asignar type aquí, se reconstruye en getType()
    }

    public void setIdentifier(boolean identifier) {
        isIdentifier = identifier;
    }
}
