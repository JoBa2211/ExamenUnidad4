package view;

import model.EntityModel;
import model.GenericEntity;
import model.AttributeDefinition;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EntityListDialog extends JDialog {
    private JTable table;
    private JButton btnClose;

    public EntityListDialog(JFrame parent, List<EntityModel> entityModels) {
        super(parent, "Lista de Entidades", true);
        setLayout(new BorderLayout());

        DefaultTableModel tableModel = buildTableModel(entityModels);
        table = new JTable(tableModel);

        btnClose = new JButton("Cerrar");
        btnClose.addActionListener(e -> setVisible(false));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnClose, BorderLayout.SOUTH);

        setSize(700, 400);
        setLocationRelativeTo(parent);
    }

    private DefaultTableModel buildTableModel(List<EntityModel> entityModels) {
        // Asume que solo se muestra un tipo de entidad a la vez (puedes adaptar para mostrar todas)
        // Aquí mostramos todas las entidades de todos los modelos, una debajo de otra
        DefaultTableModel model = new DefaultTableModel();
        // Construir columnas dinámicamente
        if (entityModels.isEmpty()) return model;

        // Encuentra todos los atributos posibles (puedes mejorar esto según tu lógica)
        for (EntityModel em : entityModels) {
            List<AttributeDefinition> attrs = em.getAttributeDefinitions();
            String[] columns = attrs.stream().map(AttributeDefinition::getName).toArray(String[]::new);
            model.setColumnIdentifiers(columns);

            for (GenericEntity entity : em.getEntities()) {
                Object[] row = new Object[attrs.size()];
                for (int i = 0; i < attrs.size(); i++) {
                    row[i] = entity.getAttribute(attrs.get(i).getName());
                }
                model.addRow(row);
            }
        }
        return model;
    }
}
