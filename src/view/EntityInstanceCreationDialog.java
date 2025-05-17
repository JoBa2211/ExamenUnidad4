package view;

import model.AttributeDefinition;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityInstanceCreationDialog extends JDialog {
    private JComboBox<String> entityTypeCombo;
    private JPanel attributesPanel;
    private JButton btnOk;
    private JButton btnCancel;
    private Map<String, JComponent> attributeFields = new HashMap<>();

    public EntityInstanceCreationDialog(JFrame parent, List<String> entityTypeNames) {
        super(parent, "Crear instancia de entidad", true);
        setLayout(new BorderLayout());

        entityTypeCombo = new JComboBox<>(entityTypeNames.toArray(new String[0]));
        btnOk = new JButton("Crear");
        btnCancel = new JButton("Cancelar");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tipo de entidad:"));
        topPanel.add(entityTypeCombo);

        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(attributesPanel);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 400);
        setLocationRelativeTo(parent);
    }

    public JComboBox<String> getEntityTypeCombo() {
        return entityTypeCombo;
    }

    public JButton getBtnOk() {
        return btnOk;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public void showAttributes(List<AttributeDefinition> attributes) {
        attributesPanel.removeAll();
        attributeFields.clear();
        for (AttributeDefinition attr : attributes) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(attr.getName() + " (" + (attr.getType() == String.class ? "String" : "Integer") + "):"));
            JComponent field;
            if (attr.getType() == Integer.class) {
                field = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
            } else {
                field = new JTextField(15);
            }
            attributeFields.put(attr.getName(), field);
            row.add(field);
            attributesPanel.add(row);
        }
        attributesPanel.revalidate();
        attributesPanel.repaint();
    }

    public Map<String, JComponent> getAttributeFields() {
        return attributeFields;
    }
}
