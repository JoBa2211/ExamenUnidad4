package controller;

import model.EntityModel;
import model.AttributeDefinition;
import model.EntityInstanceCreationModel;
import model.EntityTableModel;
import model.GenericEntity;
import model.MainModel;
import view.MainFrame;
import view.AttributeDefinitionDialog;
import view.EntityInstanceCreationDialog;
import util.JsonExport;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Map;
import java.util.HashMap;
import model.MainModelObserver;

public class MainController implements MainModelObserver {
    private MainModel mainModel;
    private MainFrame view;
    private int currentModelIndex = 0;

    public MainController(MainFrame view, MainModel mainModel) {
        this.view = view;
        this.mainModel = mainModel;
        mainModel.addObserver(this); // Se registra como observer
        initView();
        updateEntityTable();

        // Listener para mostrar detalles al seleccionar una fila
        view.getEntityTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getEntityTable().getSelectedRow();
                mostrarDetallesDeFila(row);
            }
        });
    }

    @Override
    public void onMainModelChanged() {
        updateEntityTable();
    }

    private void initView() {
        view.setDefinirListener(e -> {
            onDefinir();
            updateEntityTable();
        });
        view.setCrearListener(e -> {
            onCrear();
            updateEntityTable();
        });
        view.setActualizarListener(e -> {
            onActualizar();
            updateEntityTable();
        });
        view.setBorrarListener(e -> {
            onBorrar();
            updateEntityTable();
        });
    }

    private void onDefinir() {
        AttributeDefinitionDialog dialog = new AttributeDefinitionDialog(view);
        AttributeDefinitionController attrController = new AttributeDefinitionController(dialog);
        if (attrController.showDialog()) {
            List<AttributeDefinitionController.AttributeDef> attrs = attrController.getAttributes();
            List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            for (AttributeDefinitionController.AttributeDef attr : attrs) {
                attributeDefinitions.add(
                    new AttributeDefinition(
                        attr.name,
                        "Integer".equals(attr.type) ? Integer.class : String.class,
                        attr.isId
                    )
                );
            }
            EntityModel entityModel = new EntityModel();
            entityModel.setAttributeDefinitions(attributeDefinitions);
            mainModel.addEntityModel(entityModel);
            // No guardar en JSON aquí, solo en la lista en memoria
            updateEntityTable();
        }
    }

    private void onCrear() {
        List<EntityModel> entityModels = mainModel.getEntityModels();
        if (entityModels.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay tipos de entidad definidos.");
            return;
        }
        List<String> entityTypeNames = new ArrayList<>();
        for (EntityModel em : entityModels) {
            String idName = "Sin identificador";
            for (AttributeDefinition attr : em.getAttributeDefinitions()) {
                if (attr.isIdentifier()) {
                    idName = attr.getName();
                    break;
                }
            }
            entityTypeNames.add(idName);
        }

        EntityInstanceCreationModel creationModel = new EntityInstanceCreationModel();
        view.EntityInstanceCreationDialog creationDialog = new EntityInstanceCreationDialog(view, entityTypeNames);
        EntityInstanceCreationController creationController = new EntityInstanceCreationController(creationDialog, entityModels, creationModel);

        if (creationController.showDialog()) {
            EntityModel selectedModel = creationModel.getSelectedEntityModel();
            java.util.Map<String, Object> values = creationModel.getAttributeValues();

            String uniqueId = "";
            for (AttributeDefinition attr : selectedModel.getAttributeDefinitions()) {
                if (attr.isIdentifier()) {
                    Object idValue = values.get(attr.getName());
                    uniqueId = idValue != null ? idValue.toString() : "";
                    break;
                }
            }
            GenericEntity entity = new SerializableGenericEntity(uniqueId, values);

            mainModel.addEntityInstance(selectedModel, entity);
            // No guardar en JSON aquí
            currentModelIndex = entityModels.indexOf(selectedModel);
        }
    }

    private void onActualizar() {
        int selectedRow = view.getEntityTable().getSelectedRow();
        System.out.println("[Actualizar] Fila seleccionada: " + selectedRow);
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Seleccione una entidad para actualizar.");
            System.out.println("[Actualizar] No hay fila seleccionada.");
            return;
        }

        Object valorPrimeraColumna = view.getEntityTable().getValueAt(selectedRow, 0);
        System.out.println("[Actualizar] Valor primera columna: " + valorPrimeraColumna);
        if (valorPrimeraColumna != null && valorPrimeraColumna.toString().startsWith("== ")) {
            JOptionPane.showMessageDialog(view, "Seleccione una entidad (no una definición) para actualizar.");
            System.out.println("[Actualizar] Es una fila de definición, no se puede actualizar.");
            return;
        }

        // 1. Determinar a qué modelo y entidad corresponde la fila seleccionada
        List<EntityModel> entityModels = mainModel.getEntityModels();
        int filaActual = 0;
        EntityModel modeloEncontrado = null;
        GenericEntity entidadEncontrada = null;
        List<AttributeDefinition> attrsEncontrados = null;
        String uniqueIdAttr = null;
        Object uniqueIdValue = null;

        for (EntityModel em : entityModels) {
            List<AttributeDefinition> attrs = em.getAttributeDefinitions();
            if (attrs.isEmpty()) continue;
            filaActual++; // Fila de definición
            int entidadesEnEsteModelo = em.getEntities().size();
            for (int i = 0; i < entidadesEnEsteModelo; i++) {
                if (filaActual == selectedRow) {
                    modeloEncontrado = em;
                    entidadEncontrada = em.getEntities().get(i);
                    attrsEncontrados = attrs;
                    // Buscar el atributo identificador y su valor actual
                    for (AttributeDefinition attr : attrs) {
                        if (attr.isIdentifier()) {
                            uniqueIdAttr = attr.getName();
                            uniqueIdValue = entidadEncontrada.getAttribute(uniqueIdAttr);
                            break;
                        }
                    }
                    break;
                }
                filaActual++;
            }
            if (modeloEncontrado != null) break;
        }

        if (modeloEncontrado == null || entidadEncontrada == null || attrsEncontrados == null) {
            JOptionPane.showMessageDialog(view, "No se pudo identificar la entidad a actualizar.");
            System.out.println("[Actualizar] No se pudo identificar la entidad a actualizar.");
            return;
        }

        // 2. Buscar la entidad en el JSON (modelo) por uniqueId
        GenericEntity entityToUpdate = null;
        for (GenericEntity entity : modeloEncontrado.getEntities()) {
            Object idVal = entity.getAttribute(uniqueIdAttr);
            if (idVal != null && idVal.equals(uniqueIdValue)) {
                entityToUpdate = entity;
                break;
            }
        }

        if (entityToUpdate == null) {
            JOptionPane.showMessageDialog(view, "No se encontró la entidad en el modelo para actualizar.");
            System.out.println("[Actualizar] No se encontró la entidad en el modelo para actualizar.");
            return;
        }

        // 3. Actualizar los atributos de la entidad con los valores de los textboxes
        Map<String, JTextField> detailFields = view.getDetailFields();
        for (AttributeDefinition attr : attrsEncontrados) {
            JTextField field = detailFields.get(attr.getName());
            if (field != null) {
                String text = field.getText();
                Object value;
                if (attr.getType() == Integer.class) {
                    try {
                        value = Integer.parseInt(text);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(view, "El valor de " + attr.getName() + " debe ser un número entero.");
                        System.out.println("[Actualizar] Error: valor no entero para " + attr.getName());
                        return;
                    }
                } else {
                    value = text;
                }
                System.out.println("[Actualizar] Actualizando atributo: " + attr.getName() + " a valor: " + value);
                entityToUpdate.setAttribute(attr.getName(), value);
            } else {
                System.out.println("[Actualizar] No se encontró JTextField para atributo: " + attr.getName());
            }
        }

        // 4. Guardar los cambios en el JSON usando el modelo
        // No guardar en JSON aquí
        updateEntityTable();
        mostrarDetallesDeFila(selectedRow); // <-- Cambia aquí
        JOptionPane.showMessageDialog(view, "Entidad actualizada correctamente (solo en memoria, no en JSON).");
    }

    private void onBorrar() {
        int selectedRow = view.getEntityTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Seleccione una entidad para borrar.");
            return;
        }

        Object valorPrimeraColumna = view.getEntityTable().getValueAt(selectedRow, 0);
        if (valorPrimeraColumna != null && valorPrimeraColumna.toString().startsWith("== ")) {
            JOptionPane.showMessageDialog(view, "Seleccione una entidad (no una definición) para borrar.");
            return;
        }

        List<EntityModel> entityModels = mainModel.getEntityModels();
        int filaActual = 0;
        for (EntityModel em : entityModels) {
            List<AttributeDefinition> attrs = em.getAttributeDefinitions();
            if (attrs.isEmpty()) continue;
            filaActual++; // Fila de definición
            int entidadesEnEsteModelo = em.getEntities().size();
            for (int i = 0; i < entidadesEnEsteModelo; i++) {
                if (filaActual == selectedRow) {
                    int confirm = JOptionPane.showConfirmDialog(view, "¿Está seguro de borrar esta entidad?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        em.getEntities().remove(i);
                        // No guardar en JSON aquí
                        updateEntityTable();
                        view.limpiarDetalles();
                        JOptionPane.showMessageDialog(view, "Entidad borrada correctamente (solo en memoria, no en JSON).");
                    }
                    return;
                }
                filaActual++;
            }
        }
        JOptionPane.showMessageDialog(view, "No se pudo borrar la entidad.");
    }

    private void updateEntityTable() {
        List<EntityModel> entityModels = mainModel.getEntityModels();
        int maxColumns = 0;
        for (EntityModel em : entityModels) {
            maxColumns = Math.max(maxColumns, em.getAttributeDefinitions().size());
        }
        if (maxColumns == 0) {
            view.setEntityTableData(new Object[0][0], new String[0]);
            return;
        }

        String[] columns = new String[maxColumns];
        for (int i = 0; i < maxColumns; i++) {
            columns[i] = "Atributo " + (i + 1);
        }

        java.util.List<Object[]> rows = new ArrayList<>();
        for (EntityModel em : entityModels) {
            java.util.List<model.AttributeDefinition> attrs = em.getAttributeDefinitions();
            if (attrs.isEmpty()) continue;

            Object[] titleRow = new Object[maxColumns];
            for (int i = 0; i < attrs.size(); i++) {
                titleRow[i] = attrs.get(i).getName();
            }
            rows.add(titleRow);

            for (model.GenericEntity entity : em.getEntities()) {
                Object[] row = new Object[maxColumns];
                for (int i = 0; i < attrs.size(); i++) {
                    row[i] = entity.getAttribute(attrs.get(i).getName());
                }
                rows.add(row);
            }
        }
        Object[][] data = rows.toArray(new Object[0][]);
        view.setEntityTableData(data, columns);
    }

    // Clase interna concreta para serialización de entidades
    public static class SerializableGenericEntity extends GenericEntity {
        public SerializableGenericEntity(String uniqueId, java.util.Map<String, Object> attributes) {
            super(uniqueId);
            this.attributes = new java.util.HashMap<>(attributes);
        }
        @Override
        public boolean isValid() {
            return attributes.size() >= 3 && uniqueId != null && !uniqueId.isEmpty();
        }
    }

    // Nuevo método: el controlador se encarga de armar el mapa de atributos y valores
    private void mostrarDetallesDeFila(int row) {
        List<EntityModel> entityModels = mainModel.getEntityModels();
        int filaActual = 0;
        for (EntityModel em : entityModels) {
            List<AttributeDefinition> attrs = em.getAttributeDefinitions();
            if (attrs.isEmpty()) continue;
            filaActual++; // Fila de definición
            int entidadesEnEsteModelo = em.getEntities().size();
            for (int i = 0; i < entidadesEnEsteModelo; i++) {
                if (filaActual == row) {
                    GenericEntity entity = em.getEntities().get(i);
                    Map<String, Object> atributosYValores = new HashMap<>();
                    for (AttributeDefinition attr : attrs) {
                        atributosYValores.put(attr.getName(), entity.getAttribute(attr.getName()));
                    }
                    view.mostrarDetalles(atributosYValores);
                    return;
                }
                filaActual++;
            }
        }
        // Si no es una entidad, limpiar detalles
        view.limpiarDetalles();
    }
}

