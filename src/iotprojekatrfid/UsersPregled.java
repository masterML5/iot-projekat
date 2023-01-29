/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotprojekatrfid;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Milos
 */
public class UsersPregled extends javax.swing.JPanel {

    private static Connection conSQL;
    private static final String connectionUrlMySQL = "jdbc:mysql://192.168.1.6:3306/iotrfid?user=test&password=test123";
    // private static final String connectionUrlMySQL = "jdbc:mysql://localhost:3306/iotrfid?user=root&password=";
    DefaultTableModel tm;

    /**
     * Creates new form UsersPregled
     */
    public UsersPregled() throws SQLException {
        initComponents();
        TableColumn colid = jTable1.getColumnModel().getColumn(0);
        TableColumn coldatum = jTable1.getColumnModel().getColumn(3);
        TableColumn colaktivan = jTable1.getColumnModel().getColumn(4);
        coldatum.setPreferredWidth(50);
        colid.setPreferredWidth(3);
        colaktivan.setPreferredWidth(3);
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        getUsers();
        final RowPopUp pop = new RowPopUp(jTable1);

        jTable1.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    pop.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        });
    }

    public void getSelectedUser(int id) throws SQLException {
        UserIzmena ui = null;
        String sql = String.format("SELECT * FROM users WHERE vazeci AND id = %d", id);
        PreparedStatement pstCheck = conSQL.prepareStatement(sql);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
            ui = new UserIzmena();
            ui.aktivan = rs.getBoolean("aktivan");
            ui.created = rs.getTimestamp("created");
            ui.ime = rs.getString("ime");
            ui.prezime = rs.getString("prezime");
            ui.slika = rs.getString("slika");
            ui.brojkartice = rs.getString("rfid_uid");

        }
        rs.close();
        aktivanCB.setSelected(ui.aktivan);
        bkLabel.setText(ui.brojkartice);
        imeField.setText(ui.ime);
        prezimeField.setText(ui.prezime);
        drLabel.setText(ui.created.toString());
        ImageIcon icon = new ImageIcon(getClass().getResource("/iotprojekatrfid/res/" + ui.slika));
        icon.setImage(icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
        slikaLabel.setIcon(icon);

    }

    public TreeMap getUsers() throws SQLException {
        Users u;
        Vector<Users> userVec;
        tm = (DefaultTableModel) jTable1.getModel();
        TreeMap<String, Vector<Users>> usersMap = new TreeMap<>();
        String sqlCheck = "SELECT * FROM users WHERE vazeci";
        PreparedStatement pstCheck = conSQL.prepareStatement(sqlCheck);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
            String id = rs.getString("rfid_uid");
            userVec = usersMap.get(id);

            if (userVec == null) {
                userVec = new Vector<>();
            }
            u = new Users();
            u.user_id = rs.getInt("id");
            u.rfid_id = rs.getString("rfid_uid");
            u.name = rs.getString("name");
            u.created = rs.getTimestamp("created");
            u.aktivan = rs.getBoolean("aktivan");

            userVec.add(u);
            usersMap.put(id, userVec);
        }
        rs.close();
        ArrayList<String[]> list;
        String akt = null;
        for (String id : usersMap.keySet()) {
            userVec = usersMap.get(id);
            list = new ArrayList<>();

            for (int i = 0; i < userVec.size(); i++) {
                u = userVec.get(i);
                if (u.aktivan) {
                    akt = "Da";
                } else {
                    akt = "Ne";
                }
                list.add(new String[]{u.user_id.toString(), u.rfid_id, u.name, u.created.toString(), akt});

            }
            for (String[] data : list) {
                tm.addRow(data);
            }

        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tm);
        jTable1.setRowSorter(sorter);

        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        sorter.sort();
        return usersMap;
    }

    public ArrayList getNames() throws SQLException {
        ArrayList<String> names = new ArrayList<>();
        String sqlCheck = "SELECT name FROM users WHERE vazeci GROUP BY name";
        PreparedStatement pstCheck = conSQL.prepareStatement(sqlCheck);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
            names.add(rs.getString("name"));
        }
        rs.close();
        return names;
    }

    class Users {

        Integer user_id;
        String rfid_id;
        String name;
        Timestamp created;
        boolean aktivan;
    }

    class UserIzmena {

        Integer user_id;
        String ime;
        String prezime;
        String name;
        String slika;
        String brojkartice;
        Timestamp created;
        boolean aktivan;

    }

    class RowPopUp extends JPopupMenu {

        public RowPopUp(JTable table) {

            JMenuItem izmena = new JMenuItem("Izmeni korisnika");

            izmena.addActionListener((ActionEvent e) -> {
                try {
                    int selectedRow = jTable1.getSelectedRow();
                    String idString = jTable1.getValueAt(selectedRow, 0).toString();
                    int id = Integer.valueOf(idString);
                    getSelectedUser(id);
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            });

            add(izmena);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bkLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        imeField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        prezimeField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        aktivanCB = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        slikaLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        drLabel = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(800, 450));
        setMinimumSize(new java.awt.Dimension(800, 450));
        setPreferredSize(new java.awt.Dimension(800, 450));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "RFID", "Ime", "Datum kreiranja", "Aktivan"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Izmena korisnika");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Broj kartice :");

        bkLabel.setText("jLabel3");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Ime :");

        imeField.setText("jTextField1");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Prezime :");

        prezimeField.setText("jTextField2");

        aktivanCB.setText("Aktivan");

        jButton1.setBackground(new java.awt.Color(204, 255, 204));
        jButton1.setText("Izmeni podatke");

        jButton2.setBackground(new java.awt.Color(255, 204, 204));
        jButton2.setText("Poništi");

        jButton3.setBackground(new java.awt.Color(153, 153, 255));
        jButton3.setText("Izmeni sliku");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Slika :");

        jLabel3.setText("Datum registracije :");

        drLabel.setText("jLabel8");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel3))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bkLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(drLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(62, 62, 62))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton3)
                                    .addComponent(imeField)
                                    .addComponent(prezimeField)
                                    .addComponent(slikaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton2))
                                    .addComponent(aktivanCB))
                                .addGap(34, 34, 34)))
                        .addGap(50, 50, 50))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(bkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(drLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(imeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prezimeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(aktivanCB)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(slikaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox aktivanCB;
    private javax.swing.JLabel bkLabel;
    private javax.swing.JLabel drLabel;
    private javax.swing.JTextField imeField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField prezimeField;
    private javax.swing.JLabel slikaLabel;
    // End of variables declaration//GEN-END:variables
}
