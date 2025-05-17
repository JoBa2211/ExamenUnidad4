package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private JButton btnDefinir;
    private JButton btnCrear;
    private JButton btnActualizar;
    private JButton btnBorrar;
    private JTable entityTable;
    private DefaultTableModel tableModel;
    private JPanel detailPanel;
    private Map<String, JTextField> detailFields = new HashMap<>();

    public MainFrame() {
        setTitle("Gestión Genérica de Entidades");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnDefinir = new JButton("Definir");
        btnCrear = new JButton("Crear");
        btnActualizar = new JButton("Actualizar");
        btnBorrar = new JButton("Borrar");

        buttonPanel.add(btnDefinir);
        buttonPanel.add(btnCrear);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnBorrar);

        add(buttonPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La tabla no es editable
            }
        };
        entityTable = new JTable(tableModel);
        entityTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(entityTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de detalles a la derecha
        detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la entidad"));
        add(detailPanel, BorderLayout.EAST);
    }

    public void setDefinirListener(ActionListener listener) {
        btnDefinir.addActionListener(listener);
    }

    public void setCrearListener(ActionListener listener) {
        btnCrear.addActionListener(listener);
    }

    public void setActualizarListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }

    public void setBorrarListener(ActionListener listener) {
        btnBorrar.addActionListener(listener);
    }

    public JTable getEntityTable() {
        return entityTable;
    }

    public void setEntityTableData(Object[][] data, String[] columns) {
        tableModel.setDataVector(data, columns);
        limpiarDetalles();
    }

    public JPanel getDetailPanel() {
        return detailPanel;
    }

    public Map<String, JTextField> getDetailFields() {
        return detailFields;
    }

    public void limpiarDetalles() {
        detailPanel.removeAll();
        detailFields.clear();
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    /**
     * Muestra los detalles de la entidad seleccionada.
     * El controlador debe pasar los nombres de los atributos y sus valores.
     */
    public void mostrarDetalles(Map<String, Object> atributosYValores) {
        limpiarDetalles();
        if (atributosYValores == null) return;
        for (Map.Entry<String, Object> entry : atributosYValores.entrySet()) {
            String attrName = entry.getKey();
            Object value = entry.getValue();
            JLabel label = new JLabel(attrName + ":");
            JTextField textField = new JTextField(value != null ? value.toString() : "", 15);
            textField.setEditable(true);
            detailPanel.add(label);
            detailPanel.add(textField);
            detailFields.put(attrName, textField);
        }
        detailPanel.revalidate();
        detailPanel.repaint();
    }
}
