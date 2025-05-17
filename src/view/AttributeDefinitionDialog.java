package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AttributeDefinitionDialog extends JDialog {
    private JPanel attributesPanel;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnOk;
    private JButton btnCancel;
    private List<Row> rows = new ArrayList<>();

    public AttributeDefinitionDialog(JFrame parent) {
        super(parent, "Definir atributos", true);
        setLayout(new BorderLayout());

        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(attributesPanel);

        btnAdd = new JButton("Agregar atributo");
        btnRemove = new JButton("Eliminar atributo");
        btnOk = new JButton("Aceptar");
        btnCancel = new JButton("Cancelar");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Agrega 3 filas por defecto
        for (int i = 0; i < 3; i++) addRow(null, "String", false);

        setSize(700, 350);
        setLocationRelativeTo(parent);
    }

    public void addRow(String name, String type, boolean isId) {
        Row row = new Row(name, type, isId);
        rows.add(row);
        attributesPanel.add(row.panel);
        attributesPanel.revalidate();
        attributesPanel.repaint();
    }

    public void removeRows(List<Row> toRemove) {
        for (Row row : toRemove) {
            attributesPanel.remove(row.panel);
            rows.remove(row);
        }
        attributesPanel.revalidate();
        attributesPanel.repaint();
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnRemove() {
        return btnRemove;
    }

    public JButton getBtnOk() {
        return btnOk;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public List<Row> getRows() {
        return rows;
    }

    // Clase interna para una fila de atributo
    public static class Row {
        public JPanel panel;
        public JCheckBox chkSelect;
        public JTextField txtName;
        public JComboBox<String> cmbType;
        public JCheckBox chkId;

        public Row(String name, String type, boolean isId) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            chkSelect = new JCheckBox();
            txtName = new JTextField(name == null ? "" : name, 12);
            cmbType = new JComboBox<>(new String[]{"String", "Integer"});
            cmbType.setSelectedItem(type);
            chkId = new JCheckBox("Identificador único");
            chkId.setSelected(isId);

            panel.add(chkSelect);
            panel.add(new JLabel("Nombre:"));
            panel.add(txtName);
            panel.add(new JLabel("Tipo:"));
            panel.add(cmbType);
            panel.add(chkId);
        }
    }
}
