package model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class EntityTableModel extends AbstractTableModel {
    private List<AttributeDefinition> columns;
    private List<GenericEntity> entities;

    public EntityTableModel(List<AttributeDefinition> columns, List<GenericEntity> entities) {
        this.columns = columns;
        this.entities = entities;
    }

    @Override
    public int getRowCount() {
        return entities == null ? 0 : entities.size();
    }

    @Override
    public int getColumnCount() {
        return columns == null ? 0 : columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getName();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GenericEntity entity = entities.get(rowIndex);
        String attrName = columns.get(columnIndex).getName();
        return entity.getAttribute(attrName);
    }
}
