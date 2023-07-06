import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationsTableGUI extends JFrame {
    private JTextField txtLocationId, txtName, txtAddress;
    private JTable tblLocations;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public LocationsTableGUI() {
        initializeUI();
        connectToDatabase();
        displayLocations();
    }

    private void initializeUI() {
        txtLocationId = new JTextField();
        txtName = new JTextField();
        txtAddress = new JTextField();

        tblLocations = new JTable();
        tblLocations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLocations.getSelectionModel().addListSelectionListener(e -> selectLocation());

        JScrollPane scrollPane = new JScrollPane(tblLocations);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Location ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtLocationId, gbc);
        gbc.gridy++;
        panel.add(txtName, gbc);
        gbc.gridy++;
        panel.add(txtAddress, gbc);

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
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertLocation());

        btnModify.addActionListener(e -> modifyLocation());

        btnDelete.addActionListener(e -> deleteLocation());

        btnDisplay.addActionListener(e -> displayLocations());

        setTitle("Locations Table");
        pack();
        setLocationRelativeTo(null);
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

    private void insertLocation() {
        String locationId = txtLocationId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();

        try {
            String query = "INSERT INTO locations (location_id, name, address) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, locationId);
            statement.setString(2, name);
            statement.setString(3, address);
            statement.executeUpdate();

            clearFields();
            displayLocations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyLocation() {
        int selectedRow = tblLocations.getSelectedRow();
        if (selectedRow >= 0) {
            String locationId = txtLocationId.getText();
            String name = txtName.getText();
            String address = txtAddress.getText();

            try {
                String query = "UPDATE locations SET name=?, address=? WHERE location_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, address);
                statement.setString(3, locationId);
                statement.executeUpdate();

                clearFields();
                displayLocations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a location to modify.");
        }
    }

    private void deleteLocation() {
        int selectedRow = tblLocations.getSelectedRow();
        if (selectedRow >= 0) {
            String locationId = tblLocations.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this location?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM locations WHERE location_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, locationId);
                    statement.executeUpdate();

                    clearFields();
                    displayLocations();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a location to delete.");
        }
    }

    private void displayLocations() {
        try {
            String query = "SELECT * FROM locations";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Location> locations = new ArrayList<>();
            while (resultSet.next()) {
                String locationId = resultSet.getString("location_id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                locations.add(new Location(locationId, name, address));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Location ID", "Name", "Address"});

            for (Location location : locations) {
                model.addRow(new String[]{location.getLocationId(), location.getName(), location.getAddress()});
            }

            tblLocations.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectLocation() {
        int selectedRow = tblLocations.getSelectedRow();
        if (selectedRow >= 0) {
            String locationId = tblLocations.getValueAt(selectedRow, 0).toString();
            String name = tblLocations.getValueAt(selectedRow, 1).toString();
            String address = tblLocations.getValueAt(selectedRow, 2).toString();

            txtLocationId.setText(locationId);
            txtName.setText(name);
            txtAddress.setText(address);
        }
    }

    private void clearFields() {
        txtLocationId.setText("");
        txtName.setText("");
        txtAddress.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LocationsTableGUI::new);
    }

    private class Location {
        private String locationId;
        private String name;
        private String address;

        public Location(String locationId, String name, String address) {
            this.locationId = locationId;
            this.name = name;
            this.address = address;
        }

        public String getLocationId() {
            return locationId;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }
}
