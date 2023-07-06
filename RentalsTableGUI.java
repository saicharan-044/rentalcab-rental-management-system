import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalsTableGUI extends JFrame {
    private JTextField txtRentalId;
    private JTextField txtUserId;
    private JTextField txtVehicleId;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtDistanceTraveled;
    private JButton btnAdd;
    private JButton btnModify;
    private JButton btnDelete;
    private JTable tblRentals;
    private JScrollPane scrollPane;

    private Connection connection;

    public RentalsTableGUI() {
        initializeGUI();
        connectToDatabase();
        displayRentals();
    }

    private void initializeGUI() {
        setTitle("Rentals Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Rental ID:"), constraints);

        txtRentalId = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtRentalId, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        panel.add(new JLabel("User ID:"), constraints);

        txtUserId = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtUserId, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        panel.add(new JLabel("Vehicle ID:"), constraints);

        txtVehicleId = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtVehicleId, constraints);

        constraints.gridy = 3;
        constraints.gridx = 0;
        panel.add(new JLabel("Start Date:"), constraints);

        txtStartDate = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtStartDate, constraints);

        constraints.gridy = 4;
        constraints.gridx = 0;
        panel.add(new JLabel("End Date:"), constraints);

        txtEndDate = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtEndDate, constraints);

        constraints.gridy = 5;
        constraints.gridx = 0;
        panel.add(new JLabel("Distance Traveled:"), constraints);

        txtDistanceTraveled = new JTextField(10);
        constraints.gridx = 1;
        panel.add(txtDistanceTraveled, constraints);

        btnAdd = new JButton("Add");
        constraints.gridy = 6;
        constraints.gridx = 0;
        panel.add(btnAdd, constraints);

        btnModify = new JButton("Modify");
        constraints.gridx = 1;
        panel.add(btnModify, constraints);

        btnDelete = new JButton("Delete");
        constraints.gridy = 7;
        constraints.gridx = 0;
        panel.add(btnDelete, constraints);

        add(panel, BorderLayout.NORTH);

        tblRentals = new JTable();
        scrollPane = new JScrollPane(tblRentals);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRental();
            }
        });

        btnModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyRental();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRental();
            }
        });

        tblRentals.getSelectionModel().addListSelectionListener(e -> selectRental());
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

    private void displayRentals() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Rentals");

            List<Object[]> rows = new ArrayList<>();
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getInt("rental_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("vehicle_id"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getDouble("distance_traveled")
                };
                rows.add(row);
            }

            String[] columnNames = {"Rental ID", "User ID", "Vehicle ID", "Start Date", "End Date", "Distance Traveled"};
            DefaultTableModel model = new DefaultTableModel(rows.toArray(new Object[0][]), columnNames);
            tblRentals.setModel(model);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRental() {
        try {
            int rentalId = Integer.parseInt(txtRentalId.getText());
            int userId = Integer.parseInt(txtUserId.getText());
            int vehicleId = Integer.parseInt(txtVehicleId.getText());
            Date startDate = Date.valueOf(txtStartDate.getText());
            Date endDate = Date.valueOf(txtEndDate.getText());
            double distanceTraveled = Double.parseDouble(txtDistanceTraveled.getText());

            String query = "INSERT INTO Rentals (rental_id, user_id, vehicle_id, start_date, end_date, distance_traveled) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, rentalId);
            statement.setInt(2, userId);
            statement.setInt(3, vehicleId);
            statement.setDate(4, startDate);
            statement.setDate(5, endDate);
            statement.setDouble(6, distanceTraveled);
            statement.executeUpdate();

            statement.close();

            clearInputFields();
            displayRentals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyRental() {
        try {
            int rentalId = Integer.parseInt(txtRentalId.getText());
            int userId = Integer.parseInt(txtUserId.getText());
            int vehicleId = Integer.parseInt(txtVehicleId.getText());
            Date startDate = Date.valueOf(txtStartDate.getText());
            Date endDate = Date.valueOf(txtEndDate.getText());
            double distanceTraveled = Double.parseDouble(txtDistanceTraveled.getText());

            String query = "UPDATE Rentals SET user_id = ?, vehicle_id = ?, start_date = ?, end_date = ?, distance_traveled = ? WHERE rental_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, vehicleId);
            statement.setDate(3, startDate);
            statement.setDate(4, endDate);
            statement.setDouble(5, distanceTraveled);
            statement.setInt(6, rentalId);
            statement.executeUpdate();

            statement.close();

            clearInputFields();
            displayRentals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRental() {
        try {
            int rentalId = Integer.parseInt(txtRentalId.getText());

            String query = "DELETE FROM Rentals WHERE rental_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, rentalId);
            statement.executeUpdate();

            statement.close();

            clearInputFields();
            displayRentals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectRental() {
        int selectedRow = tblRentals.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) tblRentals.getModel();
            txtRentalId.setText(model.getValueAt(selectedRow, 0).toString());
            txtUserId.setText(model.getValueAt(selectedRow, 1).toString());
            txtVehicleId.setText(model.getValueAt(selectedRow, 2).toString());
            txtStartDate.setText(model.getValueAt(selectedRow, 3).toString());
            txtEndDate.setText(model.getValueAt(selectedRow, 4).toString());
            txtDistanceTraveled.setText(model.getValueAt(selectedRow, 5).toString());
        }
    }

    private void clearInputFields() {
        txtRentalId.setText("");
        txtUserId.setText("");
        txtVehicleId.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        txtDistanceTraveled.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RentalsTableGUI().setVisible(true);
            }
        });
    }
}
