import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainPage() {
        // Set frame properties
        setTitle("Car Rental Charges Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create label
        JLabel welcomeLabel = new JLabel("Welcome to the Car Rental Database");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu usersMenu = new JMenu("Users Details");
        JMenu vehiclesMenu = new JMenu("Vehicles Details");
        JMenu rentalsMenu = new JMenu("Rentals Details");
        JMenu locationsMenu = new JMenu("Locations Details");
        JMenu paymentsMenu = new JMenu("Payments Details");

        // Create menu items for Users menu
        JMenuItem viewUsersDetails = new JMenuItem("View Users Details");
        viewUsersDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UsersTableGUI();
            }
        });

        // Create menu items for Vehicles menu
        JMenuItem viewVehiclesDetails = new JMenuItem("View Vehicles Details");
        viewVehiclesDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VehiclesTableGUI();
            }
        });

        // Create menu items for Rentals menu
        JMenuItem viewRentalsDetails = new JMenuItem("View Rentals Details");
        viewRentalsDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RentalsTableGUI();
            }
        });

        // Create menu items for Locations menu
        JMenuItem viewLocationsDetails = new JMenuItem("View Locations Details");
        viewLocationsDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LocationsTableGUI();
            }
        });
        
        // Create menu items for Payments menu
        JMenuItem viewPaymentsDetails = new JMenuItem("View Payments Details");
        viewPaymentsDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PaymentsTableGUI();
            }
        });

        // Add menu items to respective menus
        usersMenu.add(viewUsersDetails);
        vehiclesMenu.add(viewVehiclesDetails);
        rentalsMenu.add(viewRentalsDetails);
        locationsMenu.add(viewLocationsDetails);
        paymentsMenu.add(viewPaymentsDetails);

        // Add menus to the menu bar
        menuBar.add(usersMenu);
        menuBar.add(vehiclesMenu);
        menuBar.add(rentalsMenu);
        menuBar.add(locationsMenu);
        menuBar.add(paymentsMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Set frame size and visibility
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainPage();
            }
        });
    }
}
