import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentsTableGUI extends JFrame {
    private JTextField txtPaymentId;
    private JTextField txtRentalId;
    private JTextField txtAmount;
    private JTextField txtPaymentDate;
    private JButton btnAdd;
    private JButton btnModify;
    private JButton btnDelete;
    private JTable tblPayments;
    private JScrollPane scrollPane;

    private Connection connection;

    public PaymentsTableGUI() {
        initializeGUI();
        connectToDatabase();
        displayPayments();
    }

    private void initializeGUI() {
        setTitle("Payments Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Payment ID:"), constraints);

        txtPaymentId = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtPaymentId, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        panel.add(new JLabel("Rental ID:"), constraints);

        txtRentalId = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtRentalId, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        panel.add(new JLabel("Amount:"), constraints);

        txtAmount = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtAmount, constraints);

        constraints.gridy = 3;
        constraints.gridx = 0;
        panel.add(new JLabel("Payment Date:"), constraints);

        txtPaymentDate = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtPaymentDate, constraints);

        btnAdd = new JButton("Add");
        constraints.gridy = 4;
        constraints.gridx = 0;
        panel.add(btnAdd, constraints);

        btnModify = new JButton("Modify");
        constraints.gridx = 1;
        panel.add(btnModify, constraints);

        btnDelete = new JButton("Delete");
        constraints.gridy = 5;
        constraints.gridx = 0;
        panel.add(btnDelete, constraints);

        add(panel, BorderLayout.NORTH);

        tblPayments = new JTable();
        scrollPane = new JScrollPane(tblPayments);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPayment();
            }
        });

        btnModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyPayment();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePayment();
            }
        });

        tblPayments.getSelectionModel().addListSelectionListener(e -> selectPayment());
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "saicharan";
        String password = "saicharan";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayPayments() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM payments");

            List<Object[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                Object[] row = new Object[4];
                row[0] = resultSet.getInt("payment_id");
                row[1] = resultSet.getInt("rental_id");
                row[2] = resultSet.getDouble("amount");
                row[3] = resultSet.getString("payment_date");
                rows.add(row);
            }

            Object[][] rowData = new Object[rows.size()][];
            for (int i = 0; i < rows.size(); i++) {
                rowData[i] = rows.get(i);
            }

            String[] columnNames = {"Payment ID", "Rental ID", "Amount", "Payment Date"};
            DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
            tblPayments.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPayment() {
        String paymentId = txtPaymentId.getText();
        String rentalId = txtRentalId.getText();
        String amount = txtAmount.getText();
        String paymentDate = txtPaymentDate.getText();

        try {
            String sql = "INSERT INTO payments (payment_id, rental_id, amount, payment_date) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, paymentId);
            statement.setString(2, rentalId);
            statement.setString(3, amount);
            statement.setString(4, paymentDate);
            statement.executeUpdate();

            displayPayments();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyPayment() {
        int selectedRowIndex = tblPayments.getSelectedRow();
        if (selectedRowIndex == -1) {
            JOptionPane.showMessageDialog(this, "No payment selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String paymentId = txtPaymentId.getText();
        String rentalId = txtRentalId.getText();
        String amount = txtAmount.getText();
        String paymentDate = txtPaymentDate.getText();

        try {
            String sql = "UPDATE payments SET payment_id = ?, rental_id = ?, amount = ?, payment_date = ? WHERE payment_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, paymentId);
            statement.setString(2, rentalId);
            statement.setString(3, amount);
            statement.setString(4, paymentDate);
            statement.setString(5, paymentId);
            statement.executeUpdate();

            displayPayments();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePayment() {
        int selectedRowIndex = tblPayments.getSelectedRow();
        if (selectedRowIndex == -1) {
            JOptionPane.showMessageDialog(this, "No payment selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String paymentId = txtPaymentId.getText();

        try {
            String sql = "DELETE FROM payments WHERE payment_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, paymentId);
            statement.executeUpdate();

            displayPayments();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectPayment() {
        int selectedRowIndex = tblPayments.getSelectedRow();
        if (selectedRowIndex != -1) {
            DefaultTableModel model = (DefaultTableModel) tblPayments.getModel();
            String paymentId = model.getValueAt(selectedRowIndex, 0).toString();
            String rentalId = model.getValueAt(selectedRowIndex, 1).toString();
            String amount = model.getValueAt(selectedRowIndex, 2).toString();
            String paymentDate = model.getValueAt(selectedRowIndex, 3).toString();

            txtPaymentId.setText(paymentId);
            txtRentalId.setText(rentalId);
            txtAmount.setText(amount);
            txtPaymentDate.setText(paymentDate);
        }
    }

    private void clearFields() {
        txtPaymentId.setText("");
        txtRentalId.setText("");
        txtAmount.setText("");
        txtPaymentDate.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PaymentsTableGUI().setVisible(true);
            }
        });
    }
}
