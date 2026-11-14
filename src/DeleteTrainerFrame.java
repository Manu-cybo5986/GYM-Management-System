import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URL;

public class DeleteTrainerFrame extends JFrame {
    private JTextField searchField, nameField, specField;
    private JButton searchButton, deleteButton;

    public DeleteTrainerFrame(String role, String searchPhone) {
        setTitle("Gym Management System - Delete Trainer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout()); headerPanel.setPreferredSize(new Dimension(0, 80)); headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)); leftHeaderPanel.setBackground(Color.WHITE);
        URL imgURL = getClass().getResource("/poornim logo2.jpeg");
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage().getScaledInstance(150, 30, Image.SCALE_SMOOTH);
            leftHeaderPanel.add(new JLabel(new ImageIcon(img)));
        }
        JLabel headerTitle = new JLabel("❌ Delete Trainer Record"); headerTitle.setFont(new Font("Arial", Font.BOLD, 24)); headerTitle.setForeground(new Color(231, 76, 60));
        leftHeaderPanel.add(headerTitle); headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15)); rightHeaderPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("⬅ Back"); backBtn.setBackground(new Color(149, 165, 166)); backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> { new ViewTrainersFrame(role).setVisible(true); dispose(); });
        rightHeaderPanel.add(backBtn); headerPanel.add(rightHeaderPanel, BorderLayout.EAST); add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout()); centerPanel.setBackground(new Color(245, 247, 250));
        JPanel formCard = new JPanel(new GridBagLayout()); formCard.setBackground(Color.WHITE); formCard.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.fill = GridBagConstraints.HORIZONTAL;
        Font labelFont = new Font("Arial", Font.BOLD, 20); Font fieldFont = new Font("Arial", Font.PLAIN, 18); Dimension fieldSize = new Dimension(400, 45);

        gbc.insets = new Insets(10, 20, 20, 20);
        formCard.add(createLabel("Trainer Phone:", labelFont), setGbc(gbc, 0, 0));
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0)); searchField = createTextField(new Dimension(250, 45), fieldFont);
        searchButton = new JButton("Search"); searchButton.setBackground(new Color(52, 152, 219)); searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchField, BorderLayout.CENTER); searchPanel.add(searchButton, BorderLayout.EAST);
        formCard.add(searchPanel, setGbc(gbc, 1, 0));

        gbc.insets = new Insets(15, 20, 15, 20);
        formCard.add(createLabel("Full Name:", labelFont), setGbc(gbc, 0, 1)); nameField = createTextField(fieldSize, fieldFont); nameField.setEditable(false); formCard.add(nameField, setGbc(gbc, 1, 1));
        formCard.add(createLabel("Specialty:", labelFont), setGbc(gbc, 0, 2)); specField = createTextField(fieldSize, fieldFont); specField.setEditable(false); formCard.add(specField, setGbc(gbc, 1, 2));

        deleteButton = new JButton("Permanently Delete"); deleteButton.setBackground(Color.RED); deleteButton.setForeground(Color.WHITE); deleteButton.setPreferredSize(new Dimension(400, 50)); deleteButton.setEnabled(false);
        formCard.add(new JLabel(""), setGbc(gbc, 0, 3)); formCard.add(deleteButton, setGbc(gbc, 1, 3));

        centerPanel.add(formCard); add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new GridBagLayout()); footerPanel.setBackground(new ColorUIResource(Color.DARK_GRAY)); footerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel footerLabel = new JLabel("<html><center>By Manu Khandelwal<br>2025pietcsmanu091@poornima.org</center></html>"); footerLabel.setFont(new Font("Times Roman", Font.BOLD, 14)); footerLabel.setForeground(Color.YELLOW);
        footerPanel.add(footerLabel); add(footerPanel, BorderLayout.SOUTH);

        // --- DATABASE ACTIONS ---
        searchButton.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM trainers WHERE phone_number = ?");
                pstmt.setString(1, searchField.getText());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("full_name"));
                    specField.setText(rs.getString("specialization"));
                    deleteButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "No trainer found!"); nameField.setText(""); specField.setText(""); deleteButton.setEnabled(false);
                }
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage()); }
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete this trainer?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM trainers WHERE phone_number = ?");
                    pstmt.setString(1, searchField.getText());
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Trainer Deleted!");
                    new ViewTrainersFrame(role).setVisible(true); dispose();
                } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage()); }
            }
        });

        if (searchPhone != null && !searchPhone.isEmpty()) { searchField.setText(searchPhone); searchButton.doClick(); }
    }

    private JLabel createLabel(String t, Font f) { JLabel l = new JLabel(t); l.setFont(f); return l; }
    private JTextField createTextField(Dimension s, Font f) { JTextField t = new JTextField(); t.setPreferredSize(s); t.setFont(f); return t; }
    private GridBagConstraints setGbc(GridBagConstraints g, int x, int y) { g.gridx = x; g.gridy = y; return g; }
}