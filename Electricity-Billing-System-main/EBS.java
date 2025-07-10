import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EBS{

    // In-Memory Storage
    private static HashMap<String, ArrayList<String>> paymentHistory = new HashMap<>();
    private static HashMap<String, String> registeredUsers = new HashMap<>();

    // Default Rates for Electricity
    private static double rateForFirst100Units = 1.5;
    private static double rateForNext100Units = 2.0;
    private static double rateAbove200Units = 3.0;

    // Main Frame UI Components
    private static final Color backgroundColor = new Color(240, 240, 255);
    private static final Color buttonColor = new Color(70, 130, 180);
    private static final Color buttonHoverColor = new Color(100, 150, 200, 253);
    private static final Color thankYouColor = new Color(34, 139, 34);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Electricity Billing System");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1));
        frame.getContentPane().setBackground(backgroundColor);

        JButton registerButton = createStyledButton("Register and Calculate Bill");
        JButton historyButton = createStyledButton("View Payment History");
        JButton usersButton = createStyledButton("View Registered Users");
        JButton updateRatesButton = createStyledButton("Update Electricity Rates");
        JButton exitButton = createStyledButton("Exit");

        frame.add(registerButton);
        frame.add(historyButton);
        frame.add(usersButton);
        frame.add(updateRatesButton);
        frame.add(exitButton);

        registerButton.addActionListener(e -> registerAndCalculateBill());
        historyButton.addActionListener(e -> viewPaymentHistory());
        usersButton.addActionListener(e -> viewRegisteredUsers());
        updateRatesButton.addActionListener(e -> updateRates());
        exitButton.addActionListener(e -> exitProgram());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(buttonHoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(buttonColor);
            }
        });
        return button;
    }

    private static void registerAndCalculateBill() {
        JFrame registerFrame = new JFrame("Register and Calculate Bill");
        registerFrame.setSize(400, 400);
        registerFrame.setLayout(new GridLayout(7, 2));
        registerFrame.getContentPane().setBackground(backgroundColor);

        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField unitsField = new JTextField();

        registerFrame.add(new JLabel("Customer Name:"));
        registerFrame.add(nameField);
        registerFrame.add(new JLabel("Customer ID:"));
        registerFrame.add(idField);
        registerFrame.add(new JLabel("Address:"));
        registerFrame.add(addressField);
        registerFrame.add(new JLabel("Contact Number:"));
        registerFrame.add(contactField);
        registerFrame.add(new JLabel("Units Consumed:"));
        registerFrame.add(unitsField);

        JButton calculateButton = createStyledButton("Calculate Bill");
        JButton cancelButton = createStyledButton("Cancel");
        registerFrame.add(calculateButton);
        registerFrame.add(cancelButton);

        calculateButton.addActionListener(e -> {
            String name = nameField.getText();
            String customerID = idField.getText();
            String address = addressField.getText();
            String contact = contactField.getText();
            String unitsStr = unitsField.getText();

            if (name.isEmpty() || customerID.isEmpty() || address.isEmpty() || contact.isEmpty() || unitsStr.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "All fields are required!");
                return;
            }

            if (!isNumeric(unitsStr)) {
                JOptionPane.showMessageDialog(registerFrame, "Units Consumed must be numeric!");
                return;
            }

            int units = Integer.parseInt(unitsStr);
            double bill = calculateBillAmount(units);
            JOptionPane.showMessageDialog(registerFrame, "Total Bill: ₹" + String.format("%.2f", bill));

            savePayment(customerID, name, units, bill);
            registerUser(customerID, name);

            registerFrame.dispose();
        });

        cancelButton.addActionListener(e -> registerFrame.dispose());

        registerFrame.setLocationRelativeTo(null);
        registerFrame.setVisible(true);
    }

    private static void viewPaymentHistory() {
        if (registeredUsers.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No registered users.");
            return;
        }

        String[] userArray = registeredUsers.keySet().toArray(new String[0]);
        String selectedUserID = (String) JOptionPane.showInputDialog(
                null,
                "Select a User to View Payment History:",
                "Select User",
                JOptionPane.QUESTION_MESSAGE,
                null,
                userArray,
                userArray[0]
        );

        if (selectedUserID == null) {
            return;
        }

        if (!paymentHistory.containsKey(selectedUserID)) {
            JOptionPane.showMessageDialog(null, "No payment history found for Customer ID: " + selectedUserID);
        } else {
            ArrayList<String> history = paymentHistory.get(selectedUserID);
            StringBuilder message = new StringBuilder("Payment History for " + registeredUsers.get(selectedUserID) + ":\n");
            for (String record : history) {
                message.append(record).append("\n");
            }
            JOptionPane.showMessageDialog(null, message.toString());
        }
    }

    private static void viewRegisteredUsers() {
        if (registeredUsers.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No registered users.");
            return;
        }

        StringBuilder message = new StringBuilder("Registered Users:\n");
        for (String id : registeredUsers.keySet()) {
            message.append("Customer ID: ").append(id).append(", Name: ").append(registeredUsers.get(id)).append("\n");
        }
        JOptionPane.showMessageDialog(null, message.toString());
    }

    private static void updateRates() {
        JFrame updateFrame = new JFrame("Update Electricity Rates");
        updateFrame.setSize(400, 300);
        updateFrame.setLayout(new GridLayout(4, 2));
        updateFrame.getContentPane().setBackground(backgroundColor);

        JTextField rate100Field = new JTextField(String.valueOf(rateForFirst100Units));
        JTextField rate200Field = new JTextField(String.valueOf(rateForNext100Units));
        JTextField rateAbove200Field = new JTextField(String.valueOf(rateAbove200Units));

        updateFrame.add(new JLabel("Rate for First 100 Units:"));
        updateFrame.add(rate100Field);
        updateFrame.add(new JLabel("Rate for Next 100 Units:"));
        updateFrame.add(rate200Field);
        updateFrame.add(new JLabel("Rate for Above 200 Units:"));
        updateFrame.add(rateAbove200Field);

        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");
        updateFrame.add(saveButton);
        updateFrame.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                rateForFirst100Units = Double.parseDouble(rate100Field.getText());
                rateForNext100Units = Double.parseDouble(rate200Field.getText());
                rateAbove200Units = Double.parseDouble(rateAbove200Field.getText());
                JOptionPane.showMessageDialog(updateFrame, "Rates updated successfully!");
                updateFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(updateFrame, "Please enter valid numeric values for the rates.");
            }
        });

        cancelButton.addActionListener(e -> updateFrame.dispose());

        updateFrame.setLocationRelativeTo(null);
        updateFrame.setVisible(true);
    }


    private static void exitProgram() {
        JFrame thankYouFrame = new JFrame();
        thankYouFrame.setSize(300, 100);
        thankYouFrame.setLocationRelativeTo(null);
        thankYouFrame.setUndecorated(true);
        thankYouFrame.setLayout(new BorderLayout());

        JLabel thankYouLabel = new JLabel("Thank You!", SwingConstants.CENTER);
        thankYouLabel.setFont(new Font("Arial", Font.BOLD, 24));
        thankYouLabel.setForeground(thankYouColor);
        thankYouFrame.add(thankYouLabel, BorderLayout.CENTER);

        thankYouFrame.setVisible(true);

        new Timer(900, e -> {
            thankYouFrame.dispose();
            System.exit(0);
        }).start();
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static double calculateBillAmount(int units) {
        if (units <= 100) {
            return units * rateForFirst100Units;
        } else if (units <= 200) {
            return 100 * rateForFirst100Units + (units - 100) * rateForNext100Units;
        } else {
            return 100 * rateForFirst100Units + 100 * rateForNext100Units + (units - 200) * rateAbove200Units;
        }
    }

    private static void savePayment(String customerID, String name, int units, double bill) {
        if (!paymentHistory.containsKey(customerID)) {
            paymentHistory.put(customerID, new ArrayList<>());
        }
        paymentHistory.get(customerID).add("Units: " + units + ", Bill: ₹" + String.format("%.2f", bill));
    }

    private static void registerUser(String customerID, String name) {
        registeredUsers.put(customerID, name);
    }
}