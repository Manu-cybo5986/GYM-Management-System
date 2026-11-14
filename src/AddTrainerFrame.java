import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URL;

public class AddTrainerFrame extends JFrame {
    private JTextField nameField, phoneField, experienceField, salaryField;
    private JComboBox<String> specializationBox, shiftBox;
    private JButton saveButton, backButton;
    private String currentUserRole;

    public AddTrainerFrame(String role) {
        this.currentUserRole = role;

        setTitle("Gym Management System - Add Trainer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        leftHeaderPanel.setBackground(Color.WHITE);
        URL imgURL = getClass().getResource("/poornim logo2.jpeg");
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(150, 30, Image.SCALE_SMOOTH);
            leftHeaderPanel.add(new JLabel(new ImageIcon(img)));
        }

        JLabel headerTitle = new JLabel("🏋️ FitLife Gym - Trainer Registration");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle); headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightHeaderPanel.setBackground(Color.WHITE);
        backButton = new JButton("⬅ Back to Dashboard");
        backButton.setBackground(new Color(149, 165, 166)); backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        rightHeaderPanel.add(backButton); headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- FORM AREA ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 20); Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(400, 45);

        JLabel formTitle = new JLabel("📝 Enter New Trainer Details", SwingConstants.CENTER);
        formTitle.setFont(new Font("Arial", Font.BOLD, 30));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(formTitle, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(15, 20, 15, 20);

        formCard.add(createLabel("Full Name:", labelFont), setGbc(gbc, 0, 1));
        nameField = createTextField(fieldSize, fieldFont); formCard.add(nameField, setGbc(gbc, 1, 1));

        formCard.add(createLabel("Phone Number:", labelFont), setGbc(gbc, 0, 2));
        phoneField = createTextField(fieldSize, fieldFont); formCard.add(phoneField, setGbc(gbc, 1, 2));

        formCard.add(createLabel("Specialization:", labelFont), setGbc(gbc, 0, 3));
        String[] specializations = {"Bodybuilding & Powerlifting", "Cardio & Fat Loss", "Yoga & Flexibility", "CrossFit", "General Fitness"};
        specializationBox = new JComboBox<>(specializations); specializationBox.setPreferredSize(fieldSize); specializationBox.setFont(fieldFont);
        formCard.add(specializationBox, setGbc(gbc, 1, 3));

        formCard.add(createLabel("Experience (Years):", labelFont), setGbc(gbc, 0, 4));
        experienceField = createTextField(fieldSize, fieldFont); formCard.add(experienceField, setGbc(gbc, 1, 4));

        formCard.add(createLabel("Shift Timing:", labelFont), setGbc(gbc, 0, 5));
        String[] shifts = {"Morning (6 AM - 2 PM)", "Evening (2 PM - 10 PM)", "Full Day (Split Shift)"};
        shiftBox = new JComboBox<>(shifts); shiftBox.setPreferredSize(fieldSize); shiftBox.setFont(fieldFont);
        formCard.add(shiftBox, setGbc(gbc, 1, 5));

        formCard.add(createLabel("Monthly Salary (₹):", labelFont), setGbc(gbc, 0, 6));
        salaryField = createTextField(fieldSize, fieldFont); formCard.add(salaryField, setGbc(gbc, 1, 6));

        saveButton = new JButton("💾 Save Trainer");
        saveButton.setBackground(new Color(39, 174, 96)); saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(400, 50));
        formCard.add(new JLabel(""), setGbc(gbc, 0, 7)); formCard.add(saveButton, setGbc(gbc, 1, 7));

        centerPanel.add(formCard); add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- DATABASE ACTIONS ---
        backButton.addActionListener(e -> { new DashboardFrame(currentUserRole).setVisible(true); dispose(); });

        saveButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || experienceField.getText().isEmpty() || salaryField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all details!", "Error", JOptionPane.ERROR_MESSAGE); return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO trainers (full_name, phone_number, specialization, experience_years, shift_timing, monthly_salary) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, phoneField.getText());
                pstmt.setString(3, specializationBox.getSelectedItem().toString()); // <--- FIXED PARAMETER
                pstmt.setInt(4, Integer.parseInt(experienceField.getText()));
                pstmt.setString(5, shiftBox.getSelectedItem().toString());
                pstmt.setDouble(6, Double.parseDouble(salaryField.getText()));

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Trainer added successfully to Database!");

                nameField.setText(""); phoneField.setText(""); experienceField.setText(""); salaryField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error (Duplicate Phone?): " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Experience and Salary must be valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel createLabel(String t, Font f) { JLabel l = new JLabel(t); l.setFont(f); return l; }
    private JTextField createTextField(Dimension s, Font f) { JTextField t = new JTextField(); t.setPreferredSize(s); t.setFont(f); return t; }
    private GridBagConstraints setGbc(GridBagConstraints g, int x, int y) { g.gridx = x; g.gridy = y; return g; }
}