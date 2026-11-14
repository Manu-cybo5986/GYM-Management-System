import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;

public class UpdateMemberFrame extends JFrame {
    private JTextField searchField, nameField, ageField;
    private JComboBox<String> planBox;
    private JButton searchButton, updateButton, backButton;

    public UpdateMemberFrame(String role, String searchPhone) {
        setTitle("Gym Management System - Update Record");
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
        JLabel headerTitle = new JLabel("✏️ Update Member Record");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(new Color(41, 128, 185));
        leftHeaderPanel.add(headerTitle); headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightHeaderPanel.setBackground(Color.WHITE);
        backButton = new JButton("⬅ Back");
        backButton.setBackground(new Color(149, 165, 166)); backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> { new ViewMembersFrame(role).setVisible(true); dispose(); });
        rightHeaderPanel.add(backButton); headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- FORM ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE); formCard.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 20); Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(400, 45);

        gbc.gridwidth = 1; gbc.insets = new Insets(10, 20, 20, 20);
        formCard.add(createLabel("Search Phone:", labelFont), setGbc(gbc, 0, 0));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchField = createTextField(new Dimension(250, 45), fieldFont);
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219)); searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchField, BorderLayout.CENTER); searchPanel.add(searchButton, BorderLayout.EAST);
        formCard.add(searchPanel, setGbc(gbc, 1, 0));

        gbc.insets = new Insets(15, 20, 15, 20);
        formCard.add(createLabel("Full Name:", labelFont), setGbc(gbc, 0, 1));
        nameField = createTextField(fieldSize, fieldFont); formCard.add(nameField, setGbc(gbc, 1, 1));

        formCard.add(createLabel("Age:", labelFont), setGbc(gbc, 0, 2));
        ageField = createTextField(fieldSize, fieldFont); formCard.add(ageField, setGbc(gbc, 1, 2));

        formCard.add(createLabel("Membership Plan:", labelFont), setGbc(gbc, 0, 3));
        String[] plans = {"1 Month - ₹1500", "3 Months - ₹4000", "6 Months - ₹7500", "1 Year (Pro) - ₹12000"};
        planBox = new JComboBox<>(plans); planBox.setPreferredSize(fieldSize); planBox.setFont(fieldFont);
        formCard.add(planBox, setGbc(gbc, 1, 3));

        updateButton = new JButton("🔄 Update Record");
        updateButton.setBackground(new Color(243, 156, 18)); updateButton.setForeground(Color.WHITE);
        updateButton.setPreferredSize(new Dimension(400, 50));
        formCard.add(new JLabel(""), setGbc(gbc, 0, 4)); formCard.add(updateButton, setGbc(gbc, 1, 4));

        centerPanel.add(formCard); add(centerPanel, BorderLayout.CENTER);
        setFieldsEnabled(false);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>");
        footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- DYNAMIC DATABASE ACTIONS ---
        searchButton.addActionListener(e -> {
            String phone = searchField.getText();
            if (phone.isEmpty()) return;

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM members WHERE phone_number = ?");
                pstmt.setString(1, phone);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("full_name"));
                    ageField.setText(String.valueOf(rs.getInt("age")));
                    planBox.setSelectedItem(rs.getString("membership_plan"));
                    setFieldsEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "No record found for this phone number!");
                    setFieldsEnabled(false);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE members SET full_name = ?, age = ?, membership_plan = ? WHERE phone_number = ?");
                pstmt.setString(1, nameField.getText());
                pstmt.setInt(2, Integer.parseInt(ageField.getText()));
                pstmt.setString(3, planBox.getSelectedItem().toString());
                pstmt.setString(4, searchField.getText());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record Updated Successfully!");
                new ViewMembersFrame(role).setVisible(true); dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Auto-search execution from table parameter
        if (searchPhone != null && !searchPhone.isEmpty()) {
            searchField.setText(searchPhone);
            searchButton.doClick();
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        nameField.setEnabled(enabled); ageField.setEnabled(enabled);
        planBox.setEnabled(enabled); updateButton.setEnabled(enabled);
    }
    private JLabel createLabel(String t, Font f) { JLabel l = new JLabel(t); l.setFont(f); return l; }
    private JTextField createTextField(Dimension s, Font f) { JTextField t = new JTextField(); t.setPreferredSize(s); t.setFont(f); return t; }
    private GridBagConstraints setGbc(GridBagConstraints g, int x, int y) { g.gridx = x; g.gridy = y; return g; }
}