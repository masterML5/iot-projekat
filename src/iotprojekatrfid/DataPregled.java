/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotprojekatrfid;

import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Milos
 */
public class DataPregled extends javax.swing.JPanel {

    private static Connection conSQL;
    private static final String connectionUrlMySQL = "jdbc:mysql://192.168.1.6:3306/iotrfid?user=test&password=test123";
    //private static final String connectionUrlMySQL = "jdbc:mysql://localhost:3306/iotrfid?user=root&password=";
    DefaultTableModel tm = new DefaultTableModel();
    UsersPregled up;
    private static int brojRedova;
    private static int brojRedovaCheck;
    private static boolean iteracija = true;
    TableRowSorter<TableModel> sorter;

    /**
     * Creates new form DataPregled
     *
     * @throws java.sql.SQLException
     */
    public DataPregled() throws SQLException, InterruptedException {
        initComponents();
        
        up = new UsersPregled();
        tm = (DefaultTableModel) jTable1.getModel();
        sorter = new TableRowSorter<>(tm);
        jTable1.setRowSorter(sorter);
        getData();
        lastEnter();
        ArrayList names = up.getNames();
        for (int i = 0; i < names.size(); i++) {
            jComboBox1.addItem((String) names.get(i));
        }
        TableColumn colid = jTable1.getColumnModel().getColumn(0);
        colid.setPreferredWidth(5);

        listenForUpdates();

        // puniTabelu();
    }

    private void listenForUpdates() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try (Connection conn = DriverManager.getConnection(connectionUrlMySQL);) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as broj FROM attendance ");
                    while (rs.next()) {
                        brojRedova = rs.getInt("broj");
                    }
                    if (iteracija) {
                        brojRedovaCheck = brojRedova;
                        iteracija = false;
                    }

                    if (brojRedova > brojRedovaCheck) {
                        getData();
                        lastEnter();
                    }
                    brojRedovaCheck = brojRedova;
                } catch (SQLException e) {
                    System.out.println(e);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DataPregled.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000 * 5);
    }

    class Podaci {

        String user_id;
        String rfid_id;
        String name;
        Timestamp check_in;
    }

    class OpstiPodaci {

        String user_id;
        Timestamp check_in;
        String rfid_id;
        String ime;
        String prezime;
        String slika;
    }

    private void lastEnter() throws SQLException {
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        OpstiPodaci op = null;

        String sql = "SELECT att.user_id, att.clock_in, u.name, u.rfid_uid, u.ime, u.prezime, u.slika "
                + "FROM attendance att JOIN users u ON u.vazeci WHERE att.user_id = u.id ORDER BY att.clock_in DESC LIMIT 1";
        PreparedStatement pstCheck = conSQL.prepareStatement(sql);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
            String uid = rs.getString("att.user_id");
            op = new OpstiPodaci();
            op.user_id = uid;
            op.check_in = rs.getTimestamp("att.clock_in");
            op.ime = rs.getString("u.ime");
            op.prezime = rs.getString("u.prezime");
            op.rfid_id = rs.getString("u.rfid_uid");
            op.slika = rs.getString("u.slika");

        }
        rs.close();
        imePrezimeLabel.setText(op.ime + " " + op.prezime);
        brKarticeLabel.setText(op.rfid_id);
        vremePrijaveLabel.setText(op.check_in.toString());

        ImageIcon icon = new ImageIcon(getClass().getResource("/iotprojekatrfid/res/" + op.slika));
        icon.setImage(icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
        slikaLabel.setIcon(icon);

    }

    private void getData() throws SQLException, InterruptedException {
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        tm.setRowCount(0);

        Podaci p;
        Vector<Podaci> pVec;
        TreeMap<String, Vector<Podaci>> dataMap = new TreeMap<>();
        String sql = "SELECT att.user_id, att.clock_in, u.name, u.rfid_uid FROM attendance att JOIN users u ON u.vazeci WHERE att.user_id = u.id ORDER BY att.clock_in DESC";
        PreparedStatement pstCheck = conSQL.prepareStatement(sql);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
            String id = rs.getString("att.user_id");
            pVec = dataMap.get(id);

            if (pVec == null) {
                pVec = new Vector<>();
            }
            p = new Podaci();
            p.user_id = rs.getString("att.user_id");
            p.rfid_id = rs.getString("u.rfid_uid");
            p.name = rs.getString("u.name");
            p.check_in = rs.getTimestamp("att.clock_in");
            pVec.add(p);
            dataMap.put(id, pVec);
        }
        rs.close();
        ArrayList<String[]> list;

        for (String id : dataMap.keySet()) {
            pVec = dataMap.get(id);
            list = new ArrayList<>();

            for (int i = 0; i < pVec.size(); i++) {
                p = pVec.get(i);

                list.add(new String[]{p.user_id, p.rfid_id, p.name, p.check_in.toString()});

            }

            for (String[] data : list) {

                tm.addRow(data);
            }

        }

        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(3, SortOrder.DESCENDING)));
        sorter.sort();

        // tm.fireTableDataChanged();
        //jTable1.repaint();
    }

    public void filter(String query) {
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<>(tm);
        jTable1.setRowSorter(tr);
        tr.setRowFilter(RowFilter.regexFilter(query));

    }

    public void filterKorisnik(String queryStatus) {
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<>(tm);
        jTable1.setRowSorter(tr);
        if (!"Bez filtera".equals(queryStatus)) {
            tr.setRowFilter(RowFilter.regexFilter(queryStatus));
        } else {
            jTable1.setRowSorter(tr);
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
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        imePrezimeLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        brKarticeLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        vremePrijaveLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        slikaLabel = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(800, 450));
        setMinimumSize(new java.awt.Dimension(800, 450));
        setPreferredSize(new java.awt.Dimension(800, 450));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User id", "RFID ", "Ime", "Vreme"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
            jTable1.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Evidencija prolaska");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bez filtera" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel2.setText("Filtriraj po korisniku");

        jLabel3.setText("Pretraga");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Poslednji prijavljen :");

        jLabel5.setText("Ime Prezime");

        imePrezimeLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        imePrezimeLabel.setText("jLabel6");

        jLabel7.setText("Broj kartice");

        brKarticeLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        brKarticeLabel.setText("jLabel8");

        jLabel9.setText("Vreme prijave");

        vremePrijaveLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        vremePrijaveLabel.setText("jLabel10");

        jLabel11.setText("Slika");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imePrezimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(brKarticeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vremePrijaveLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(slikaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imePrezimeLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(brKarticeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vremePrijaveLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slikaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String query = jTextField1.getText().trim();
        filter(query);
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        String query = jComboBox1.getSelectedItem().toString();
        filterKorisnik(query);
    }//GEN-LAST:event_jComboBox1ItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel brKarticeLabel;
    private javax.swing.JLabel imePrezimeLabel;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel slikaLabel;
    private javax.swing.JLabel vremePrijaveLabel;
    // End of variables declaration//GEN-END:variables
}
