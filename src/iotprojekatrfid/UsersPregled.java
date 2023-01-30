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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    //private static final String connectionUrlMySQL = "jdbc:mysql://192.168.74.64:3306/iotrfid?user=test&password=test123";
    private static final String connectionUrlMySQL = "jdbc:mysql://192.168.1.6:3306/iotrfid?user=test&password=test123";
    //private static final String connectionUrlMySQL = "jdbc:mysql://localhost:3306/iotrfid?user=root&password=";
    private static boolean izmenaSlika = false;
    private static String path;
    private static String destination;
    private static String nameSlika;
    DefaultTableModel tm;

    /**
     * Creates new form UsersPregled
     * @throws java.sql.SQLException
     */
    public UsersPregled() throws SQLException {
        initComponents();
        tm = (DefaultTableModel) jTable1.getModel();
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

            @Override
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
        jLabel6.setText(ui.slika);

    }

    public void getUsers() throws SQLException {
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        tm.setRowCount(0);
        Users u;
        Vector<Users> userVec;
      
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

    private void insertImage(String path, String destination) {
        try {

            FileChannel source = new FileInputStream(path).getChannel();
            FileChannel dest = new FileOutputStream(destination).getChannel();
            dest.transferFrom(source, 0, source.size());

            source.close();
            dest.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private void editUserData(UserIzmena uiedit) throws SQLException{
        
      //  String sql = String.format("UPDATE users SET ime = '%s', prezime = '%s', slika = '%s', aktivan = %b, edited = NOW() WHERE"
       //         + "  rfid_uid = '%s'", uiedit.ime,uiedit.prezime,uiedit.slika,uiedit.aktivan,uiedit.brojkartice);
        String sqlUpdate = "UPDATE users SET ime = ?, prezime = ?, slika = ?, aktivan = ?, edited = NOW() WHERE rfid_uid = ? AND vazeci";
        PreparedStatement statement = conSQL.prepareStatement(sqlUpdate);
        statement.setString(1, uiedit.ime);
        statement.setString(2, uiedit.prezime);
        statement.setString(3, uiedit.slika);
        statement.setBoolean(4, uiedit.aktivan);
        statement.setString(5, uiedit.brojkartice);
        int rows = statement.executeUpdate();
        if(rows > 0){
            JOptionPane.showMessageDialog(null,"Uspesno ste izmenili korisnika","Izmena",JOptionPane.INFORMATION_MESSAGE);
            conSQL.commit();
            getUsers();
        }else{
            JOptionPane.showMessageDialog(null,"Doslo je do greske, podaci nisu izmenjeni","Greska",JOptionPane.ERROR_MESSAGE);
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
        jLabel6 = new javax.swing.JLabel();

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

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Ime :");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Prezime :");

        aktivanCB.setText("Aktivan");

        jButton1.setBackground(new java.awt.Color(204, 255, 204));
        jButton1.setText("Izmeni podatke");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 204, 204));
        jButton2.setText("Poništi");

        jButton3.setBackground(new java.awt.Color(153, 153, 255));
        jButton3.setText("Izmeni sliku");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton3MouseReleased(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Slika :");

        jLabel3.setText("Datum registracije :");

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
                            .addComponent(drLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                                    .addComponent(imeField)
                                    .addComponent(prezimeField)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(slikaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton2))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jButton3)
                                            .addGap(33, 33, 33)
                                            .addComponent(aktivanCB))))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(drLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aktivanCB)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(slikaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
        fc.setFileFilter(filter);
        int result = fc.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "Prekid", "Prekid", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        path = fc.getSelectedFile().getAbsolutePath();
        nameSlika = fc.getSelectedFile().getName();

        File folder = new File(System.getProperty("user.dir") + "/src/iotprojekatrfid/res/");

        destination = folder + File.separator + nameSlika;
        
        izmenaSlika = true;
        jLabel6.setText(nameSlika);
        
    }//GEN-LAST:event_jButton3MouseReleased

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
       
        if(!bkLabel.equals("")){
        try {
            UserIzmena uiedit = new UserIzmena();
            uiedit.aktivan = aktivanCB.isSelected();
            uiedit.ime = imeField.getText();
            uiedit.prezime = prezimeField.getText();
            if(izmenaSlika){
            uiedit.slika = nameSlika;
            insertImage(path,destination);
            }else{
            uiedit.slika = jLabel6.getText();
            }
            uiedit.brojkartice = bkLabel.getText();
            editUserData(uiedit);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        }else{
            JOptionPane.showMessageDialog(null,"Niste izabrali korisnika","Greska",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1MouseReleased

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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField prezimeField;
    private javax.swing.JLabel slikaLabel;
    // End of variables declaration//GEN-END:variables
}
