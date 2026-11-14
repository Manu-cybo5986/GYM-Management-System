import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URL;

public class AddMemberFrame extends JFrame {
    private JTextField nameField, phoneField, ageField;
    private JComboBox<String> genderBox, planBox;
    private JButton saveButton, backButton;
    private String currentUserRole;

    public AddMemberFrame(String role) {
        this.currentUserRole = role;

        setTitle("Gym Management System - Add Member");
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

        JLabel headerTitle = new JLabel("➕ Add New Member");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle);
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightHeaderPanel.setBackground(Color.WHITE);
        backButton = new JButton("⬅ Back to Dashboard");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        rightHeaderPanel.add(backButton);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- FORM AREA ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);

        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(400, 45);

        JLabel formTitle = new JLabel("📝 Enter Details", SwingConstants.CENTER);
        formTitle.setFont(new Font("Arial", Font.BOLD, 30));
        formTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 30, 0);
        formCard.add(formTitle, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(15, 20, 15, 20);

        formCard.add(createLabel("Full Name:", labelFont), setGbc(gbc, 0, 1));
        nameField = createTextField(fieldSize, fieldFont); formCard.add(nameField, setGbc(gbc, 1, 1));

        formCard.add(createLabel("Phone Number:", labelFont), setGbc(gbc, 0, 2));
        phoneField = createTextField(fieldSize, fieldFont); formCard.add(phoneField, setGbc(gbc, 1, 2));

        formCard.add(createLabel("Age:", labelFont), setGbc(gbc, 0, 3));
        ageField = createTextField(fieldSize, fieldFont); formCard.add(ageField, setGbc(gbc, 1, 3));

        formCard.add(createLabel("Gender:", labelFont), setGbc(gbc, 0, 4));
        String[] genders = {"Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders); genderBox.setPreferredSize(fieldSize); genderBox.setFont(fieldFont);
        formCard.add(genderBox, setGbc(gbc, 1, 4));

        formCard.add(createLabel("Membership Plan:", labelFont), setGbc(gbc, 0, 5));
        String[] plans = {"1 Month - ₹1500", "3 Months - ₹4000", "6 Months - ₹7500", "1 Year (Pro) - ₹12000"};
        planBox = new JComboBox<>(plans); planBox.setPreferredSize(fieldSize); planBox.setFont(fieldFont);
        formCard.add(planBox, setGbc(gbc, 1, 5));

        saveButton = new JButton("💾 Save Record");
        saveButton.setBackground(new Color(39, 174, 96)); saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 18)); saveButton.setPreferredSize(new Dimension(400, 50));
        formCard.add(new JLabel(""), setGbc(gbc, 0, 6)); formCard.add(saveButton, setGbc(gbc, 1, 6));

        centerPanel.add(formCard);
        add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY));
        footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- DYNAMIC DATABASE ACTIONS ---
        backButton.addActionListener(e -> { new DashboardFrame(currentUserRole).setVisible(true); dispose(); });

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String ageText = ageField.getText();
            String gender = genderBox.getSelectedItem().toString();
            String plan = planBox.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || ageText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all details!", "Error", JOptionPane.ERROR_MESSAGE); return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO members (full_name, phone_number, age, gender, membership_plan, assigned_trainer) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setInt(3, Integer.parseInt(ageText));
                pstmt.setString(4, gender);
                pstmt.setString(5, plan);
                pstmt.setString(6, "Not Assigned"); // Default trainer string

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Member added successfully to Database!");
                nameField.setText(""); phoneField.setText(""); ageField.setText("");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error (Duplicate Phone?): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel createLabel(String t, Font f) { JLabel l = new JLabel(t); l.setFont(f); l.setForeground(new Color(44, 62, 80)); return l; }
    private JTextField createTextField(Dimension s, Font f) { JTextField t = new JTextField(); t.setPreferredSize(s); t.setFont(f); return t; }
    private GridBagConstraints setGbc(GridBagConstraints g, int x, int y) { g.gridx = x; g.gridy = y; return g; }
}