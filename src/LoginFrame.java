import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;

public class LoginFrame extends JFrame {
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public LoginFrame() {
        setTitle("Gym Management System - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(null);
        headerPanel.setPreferredSize(new Dimension(1920, 140));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        URL imgURL = getClass().getResource("/poornim logo2.jpeg");
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(600, 120, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setBounds(480, 10, 600, 120);
            headerPanel.add(imageLabel);
        } else {
            JLabel errorLabel = new JLabel("🏋️ FitLife Gym Management", SwingConstants.LEFT);
            errorLabel.setBounds(30, 45, 600, 40);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 28));
            errorLabel.setForeground(new Color(41, 128, 185));
            headerPanel.add(errorLabel);
        }
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTER FORM ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 22); Font fieldFont = new Font("Arial", Font.PLAIN, 20); Dimension fieldSize = new Dimension(450, 50);

        JLabel formTitle = new JLabel("Gym Login Portal", SwingConstants.CENTER); formTitle.setFont(new Font("Arial", Font.BOLD, 36));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 40, 0); formPanel.add(formTitle, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(15, 15, 15, 15);
        formPanel.add(createLabel("Username:", labelFont), setGbc(gbc, 0, 1));
        userTextField = new JTextField(); userTextField.setPreferredSize(fieldSize); userTextField.setFont(fieldFont); formPanel.add(userTextField, setGbc(gbc, 1, 1));

        formPanel.add(createLabel("Password:", labelFont), setGbc(gbc, 0, 2));
        passwordField = new JPasswordField(); passwordField.setPreferredSize(fieldSize); passwordField.setFont(fieldFont); formPanel.add(passwordField, setGbc(gbc, 1, 2));

        formPanel.add(createLabel("Role:", labelFont), setGbc(gbc, 0, 3));
        // NEW: Added Trainer to the roles array
        String[] roles = {"Admin", "Trainer", "Member"};
        roleComboBox = new JComboBox<>(roles); roleComboBox.setPreferredSize(fieldSize); roleComboBox.setFont(fieldFont); formPanel.add(roleComboBox, setGbc(gbc, 1, 3));

        JButton loginButton = new JButton("Login"); loginButton.setPreferredSize(new Dimension(450, 65));
        loginButton.setBackground(new Color(41, 128, 185)); loginButton.setForeground(Color.WHITE); loginButton.setFont(new Font("Arial", Font.BOLD, 24)); loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(30, 15, 10, 15); formPanel.add(loginButton, setGbc(gbc, 1, 4));

        JButton forgotPasswordButton = new JButton("🔒 Forgot Password?"); forgotPasswordButton.setContentAreaFilled(false); forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setForeground(new Color(41, 128, 185)); forgotPasswordButton.setFont(new Font("Arial", Font.BOLD, 16)); forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(0, 15, 15, 15); formPanel.add(forgotPasswordButton, setGbc(gbc, 1, 5));

        centerPanel.add(formPanel); add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout()); footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- ACTIONS ---
        loginButton.addActionListener(e -> {
            String username = userTextField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleComboBox.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!"); return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ? AND role = ?");
                pstmt.setString(1, username); pstmt.setString(2, password); pstmt.setString(3, role);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    new DashboardFrame(role).setVisible(true); dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username, Password, or Role!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        forgotPasswordButton.addActionListener(e -> { new ForgotPasswordFrame().setVisible(true); dispose(); });
    }

    private JLabel createLabel(String t, Font f) { JLabel l = new JLabel(t); l.setFont(f); return l; }
    private GridBagConstraints setGbc(GridBagConstraints g, int x, int y) { g.gridx = x; g.gridy = y; return g; }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)); }
}