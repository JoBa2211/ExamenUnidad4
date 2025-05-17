package controller;

import view.AttributeDefinitionDialog;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AttributeDefinitionController {
    private AttributeDefinitionDialog dialog;
    private boolean confirmed = false;

    public AttributeDefinitionController(AttributeDefinitionDialog dialog) {
        this.dialog = dialog;
        initListeners();
    }

    private void initListeners() {
        dialog.getBtnAdd().addActionListener(e -> dialog.addRow(null, "String", false));
        dialog.getBtnRemove().addActionListener(e -> removeSelectedRows());
        dialog.getBtnOk().addActionListener(e -> onOk());
        dialog.getBtnCancel().addActionListener(e -> {
            confirmed = false;
            dialog.setVisible(false);
        });

        // Listener para evitar que se seleccione un Integer como identificador único
        // y para asegurar que solo uno esté seleccionado
        for (AttributeDefinitionDialog.Row row : dialog.getRows()) {
            addIdCheckListener(row);
        }
    }

    // Añadir listener a la casilla de identificador único de cada fila
    private void addIdCheckListener(AttributeDefinitionDialog.Row row) {
        row.chkId.addActionListener(e -> {
            if (row.chkId.isSelected()) {
                // Si el tipo es Integer, no permitir seleccionarlo como identificador
                if ("Integer".equals(row.cmbType.getSelectedItem().toString())) {
                    JOptionPane.showMessageDialog(dialog, "El identificador único solo puede ser de tipo String.");
                    row.chkId.setSelected(false);
                    return;
                }
                // Desmarcar los demás
                for (AttributeDefinitionDialog.Row other : dialog.getRows()) {
                    if (other != row) {
                        other.chkId.setSelected(false);
                    }
                }
            }
        });
        // Listener para cambiar el tipo y desmarcar si es Integer y estaba seleccionado como ID
        row.cmbType.addActionListener(e -> {
            if ("Integer".equals(row.cmbType.getSelectedItem().toString()) && row.chkId.isSelected()) {
                JOptionPane.showMessageDialog(dialog, "El identificador único solo puede ser de tipo String.");
                row.chkId.setSelected(false);
            }
        });
    }

    private void removeSelectedRows() {
        List<AttributeDefinitionDialog.Row> toRemove = new ArrayList<>();
        for (AttributeDefinitionDialog.Row row : dialog.getRows()) {
            if (row.chkSelect.isSelected()) {
                toRemove.add(row);
            }
        }
        if (dialog.getRows().size() - toRemove.size() < 3) {
            JOptionPane.showMessageDialog(dialog, "Debe haber al menos 3 atributos.");
            return;
        }
        if (toRemove.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Seleccione al menos un atributo para eliminar.");
            return;
        }
        dialog.removeRows(toRemove);
    }

    private void onOk() {
        int idCount = 0;
        for (AttributeDefinitionDialog.Row row : dialog.getRows()) {
            if (row.chkId.isSelected()) idCount++;
        }
        if (dialog.getRows().size() < 3) {
            JOptionPane.showMessageDialog(dialog, "Debe definir al menos 3 atributos.");
            return;
        }
        if (idCount != 1) {
            JOptionPane.showMessageDialog(dialog, "Debe seleccionar exactamente un identificador único.");
            return;
        }
        for (AttributeDefinitionDialog.Row row : dialog.getRows()) {
            if (row.txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los atributos deben tener nombre.");
                return;
            }
            // Validar que el identificador único sea de tipo String
            if (row.chkId.isSelected() && !"String".equals(row.cmbType.getSelectedItem().toString())) {
                JOptionPane.showMessageDialog(dialog, "El identificador único solo puede ser de tipo String.");
                return;
            }
        }
        confirmed = true;
        dialog.setVisible(false);
    }

    public boolean showDialog() {
        // Asegura que los listeners de ID estén siempre activos para las filas iniciales y nuevas
        for (AttributeDefinitionDialog.Row row : dialog.getRows()) {
            addIdCheckListener(row);
        }
        dialog.setVisible(true);
        return confirmed;
    }

    public List<AttributeDef> getAttributes() {
        List<AttributeDef> attrs = new ArrayList<>();
        for (AttributeDefinitionDialog.Row row : dialog.getRows()) {
            attrs.add(new AttributeDef(
                row.txtName.getText().trim(),
                row.cmbType.getSelectedItem().toString(),
                row.chkId.isSelected()
            ));
        }
        return attrs;
    }

    public static class AttributeDef {
        public final String name;
        public final String type;
        public final boolean isId;

        public AttributeDef(String name, String type, boolean isId) {
            this.name = name;
            this.type = type;
            this.isId = isId;
        }
    }
}
