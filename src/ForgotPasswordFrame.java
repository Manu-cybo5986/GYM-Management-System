import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URL;

public class ForgotPasswordFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField newPasswordField;

    public ForgotPasswordFrame() {
        setTitle("Gym Management System - Forgot Password");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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

        JLabel headerTitle = new JLabel("🔒 Reset Password");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle);
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightHeaderPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("⬅ Back to Login");
        backBtn.setBackground(new Color(149, 165, 166)); backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        rightHeaderPanel.add(backBtn);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- FORM ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        JPanel formCard = new JPanel(new GridLayout(3, 2, 20, 20));
        formCard.setBackground(Color.WHITE); formCard.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);

        JLabel userLabel = new JLabel("Enter Username:"); userLabel.setFont(labelFont);
        usernameField = new JTextField(); usernameField.setFont(fieldFont);
        formCard.add(userLabel); formCard.add(usernameField);

        JLabel passLabel = new JLabel("Enter New Password:"); passLabel.setFont(labelFont);
        newPasswordField = new JPasswordField(); newPasswordField.setFont(fieldFont);
        formCard.add(passLabel); formCard.add(newPasswordField);

        JButton updateBtn = new JButton("Update Password");
        updateBtn.setFont(new Font("Arial", Font.BOLD, 18));
        updateBtn.setBackground(new Color(39, 174, 96)); updateBtn.setForeground(Color.WHITE);
        formCard.add(new JLabel("")); formCard.add(updateBtn);

        centerPanel.add(formCard); add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- DYNAMIC DATABASE ACTIONS ---
        updateBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String newPassword = new String(newPasswordField.getPassword());

            if (username.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newPassword);
                pstmt.setString(2, username);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Password Updated Successfully! Please login with your new password.");
                    new LoginFrame().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username not found in database!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}