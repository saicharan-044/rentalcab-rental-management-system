import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiclesTableGUI extends JFrame {
    private JTextField txtVehicleId, txtMake, txtModel, txtRentalRate;
    private JTable tblVehicles;
    private JButton btnAdd, btnModify, btnDelete;

    private Connection connection;

    public VehiclesTableGUI() {
        initializeUI();
        connectToDatabase();
        displayVehicles();
    }

    private void initializeUI() {
        txtVehicleId = new JTextField();
        txtMake = new JTextField();
        txtModel = new JTextField();
        txtRentalRate = new JTextField();

        tblVehicles = new JTable();
        tblVehicles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVehicles.getSelectionModel().addListSelectionListener(e -> selectVehicle());

        JScrollPane scrollPane = new JScrollPane(tblVehicles);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Vehicle ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Make:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Model:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Rental Rate:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtVehicleId, gbc);
        gbc.gridy++;
        panel.add(txtMake, gbc);
        gbc.gridy++;
        panel.add(txtModel, gbc);
        gbc.gridy++;
        panel.add(txtRentalRate, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertVehicle());

        btnModify.addActionListener(e -> modifyVehicle());

        btnDelete.addActionListener(e -> deleteVehicle());

        setTitle("Vehicles");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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

    private void insertVehicle() {
        String vehicleId = txtVehicleId.getText();
        String make = txtMake.getText();
        String model = txtModel.getText();
        String rentalRate = txtRentalRate.getText();

        try {
            String query = "INSERT INTO Vehicles (vehicle_id, make, model, rental_rate) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, vehicleId);
            statement.setString(2, make);
            statement.setString(3, model);
            statement.setString(4, rentalRate);
            statement.executeUpdate();

            clearFields();
            displayVehicles();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyVehicle() {
        int selectedRow = tblVehicles.getSelectedRow();
        if (selectedRow >= 0) {
            String vehicleId = txtVehicleId.getText();
            String make = txtMake.getText();
            String model = txtModel.getText();
            String rentalRate = txtRentalRate.getText();

            try {
                String query = "UPDATE Vehicles SET make=?, model=?, rental_rate=? WHERE vehicle_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, make);
                statement.setString(2, model);
                statement.setString(3, rentalRate);
                statement.setString(4, vehicleId);
                statement.executeUpdate();

                clearFields();
                displayVehicles();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to modify.");
        }
    }

    private void deleteVehicle() {
        int selectedRow = tblVehicles.getSelectedRow();
        if (selectedRow >= 0) {
            String vehicleId = tblVehicles.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this vehicle?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Vehicles WHERE vehicle_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, vehicleId);
                    statement.executeUpdate();

                    clearFields();
                    displayVehicles();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete.");
        }
    }

    private void displayVehicles() {
        try {
            String query = "SELECT * FROM Vehicles";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Vehicle> vehicles = new ArrayList<>();
            while (resultSet.next()) {
                String vehicleId = resultSet.getString("vehicle_id");
                String make = resultSet.getString("make");
                String model = resultSet.getString("model");
                String rentalRate = resultSet.getString("rental_rate");
                vehicles.add(new Vehicle(vehicleId, make, model, rentalRate));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Vehicle ID", "Make", "Model", "Rental Rate"});

            for (Vehicle vehicle : vehicles) {
                model.addRow(new String[]{vehicle.getVehicleId(), vehicle.getMake(), vehicle.getModel(), vehicle.getRentalRate()});
            }

            tblVehicles.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectVehicle() {
        int selectedRow = tblVehicles.getSelectedRow();
        if (selectedRow >= 0) {
            String vehicleId = tblVehicles.getValueAt(selectedRow, 0).toString();
            String make = tblVehicles.getValueAt(selectedRow, 1).toString();
            String model = tblVehicles.getValueAt(selectedRow, 2).toString();
            String rentalRate = tblVehicles.getValueAt(selectedRow, 3).toString();

            txtVehicleId.setText(vehicleId);
            txtMake.setText(make);
            txtModel.setText(model);
            txtRentalRate.setText(rentalRate);
        }
    }

    private void clearFields() {
        txtVehicleId.setText("");
        txtMake.setText("");
        txtModel.setText("");
        txtRentalRate.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehiclesTableGUI::new);
    }

    private class Vehicle {
        private String vehicleId;
        private String make;
        private String model;
        private String rentalRate;

        public Vehicle(String vehicleId, String make, String model, String rentalRate) {
            this.vehicleId = vehicleId;
            this.make = make;
            this.model = model;
            this.rentalRate = rentalRate;
        }

        public String getVehicleId() {
            return vehicleId;
        }

        public String getMake() {
            return make;
        }

        public String getModel() {
            return model;
        }

        public String getRentalRate() {
            return rentalRate;
        }
    }
}
