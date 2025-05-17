package controller;

import model.AttributeDefinition;
import model.EntityModel;
import model.EntityInstanceCreationModel;
import view.EntityInstanceCreationDialog;
import util.JsonExport;
import model.GenericEntity;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class EntityInstanceCreationController {
    private EntityInstanceCreationDialog dialog;
    private List<EntityModel> entityModels;
    private EntityInstanceCreationModel model;
    private boolean confirmed = false;

    public EntityInstanceCreationController(EntityInstanceCreationDialog dialog, List<EntityModel> entityModels, EntityInstanceCreationModel model) {
        this.dialog = dialog;
        this.entityModels = entityModels;
        this.model = model;
        initListeners();
    }

    private void initListeners() {
        dialog.getEntityTypeCombo().addActionListener(e -> onEntityTypeChanged());
        dialog.getBtnOk().addActionListener(e -> onOk());
        dialog.getBtnCancel().addActionListener(e -> {
            confirmed = false;
            dialog.setVisible(false);
        });
    }

    private void onEntityTypeChanged() {
        int idx = dialog.getEntityTypeCombo().getSelectedIndex();
        if (idx >= 0 && idx < entityModels.size()) {
            model.setSelectedEntityModel(entityModels.get(idx));
            dialog.showAttributes(model.getSelectedEntityModel().getAttributeDefinitions());
        }
    }

    private void onOk() {
        if (model.getSelectedEntityModel() == null) {
            JOptionPane.showMessageDialog(dialog, "Seleccione un tipo de entidad.");
            return;
        }
        List<AttributeDefinition> attrs = model.getSelectedEntityModel().getAttributeDefinitions();
        model.clearAttributeValues();
        Map<String, JComponent> fields = dialog.getAttributeFields();
        for (AttributeDefinition attr : attrs) {
            JComponent field = fields.get(attr.getName());
            Object val = null;
            if (attr.getType() == Integer.class && field instanceof JSpinner) {
                val = ((JSpinner) field).getValue();
            } else if (attr.getType() == String.class && field instanceof JTextField) {
                val = ((JTextField) field).getText();
            }
            if (val == null || (val instanceof String && ((String) val).trim().isEmpty())) {
                JOptionPane.showMessageDialog(dialog, "Debe completar el atributo: " + attr.getName());
                return;
            }
            model.setAttributeValue(attr.getName(), val);
        }

        // Verificar uniqueId en el JSON antes de confirmar
        String uniqueId = "";
        for (AttributeDefinition attr : attrs) {
            if (attr.isIdentifier()) {
                Object idValue = model.getAttributeValues().get(attr.getName());
                uniqueId = idValue != null ? idValue.toString() : "";
                break;
            }
        }
        if (!uniqueId.isEmpty()) {
            // Cargar todos los modelos desde el JSON y buscar el uniqueId
            java.util.List<model.EntityModel> allModels = JsonExport.importEntityModels();
            boolean exists = false;
            for (model.EntityModel em : allModels) {
                for (GenericEntity entity : em.getEntities()) {
                    if (entity != null && entity.getUniqueId() != null && entity.getUniqueId().equals(uniqueId)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) break;
            }
            if (exists) {
                JOptionPane.showMessageDialog(dialog, "Ya existe una entidad con el mismo identificador único: " + uniqueId);
                return;
            }
        }

        confirmed = true;
        dialog.setVisible(false);
    }

    public boolean showDialog() {
        if (!entityModels.isEmpty()) {
            dialog.getEntityTypeCombo().setSelectedIndex(0);
            model.setSelectedEntityModel(entityModels.get(0));
            dialog.showAttributes(model.getSelectedEntityModel().getAttributeDefinitions());
        }
        dialog.setVisible(true);
        return confirmed;
    }
}
