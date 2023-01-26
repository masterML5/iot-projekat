/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotprojekatrfid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.Executors;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Milos
 */
public class DataPregled extends javax.swing.JPanel {

    private static Connection conSQL;
    private static final String connectionUrlMySQL = "jdbc:mysql://192.168.1.6:3306/iotrfid?user=test&password=test123";
    DefaultTableModel tm = new DefaultTableModel();
    UsersPregled up;
    private static int brojRedova;
    private static int brojRedovaCheck;
    private static boolean iteracija = true;
    int test;
    /**
     * Creates new form DataPregled
     */

    public DataPregled() throws SQLException {
        initComponents();
        
        up = new UsersPregled();

        getData();
        ArrayList names = up.getNames();
        for (int i = 0; i < names.size(); i++) {
            jComboBox1.addItem((String) names.get(i));
        }

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
                    if(iteracija){
                        brojRedovaCheck = brojRedova;
                        iteracija = false;
                    }

                    if (brojRedova > brojRedovaCheck) {
                        getData();
                    }
                    brojRedovaCheck = brojRedova;
                } catch (SQLException e) {
                    System.out.println(e);
                }
            }
        };
        // set the timer to run the task every 5 minutes
        timer.scheduleAtFixedRate(task, 0, 1000 * 5);
    }

    class Podaci {

        String user_id;
        String rfid_id;
        String name;
        Timestamp check_in;
    }

    private TreeMap getData() throws SQLException {
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        tm.setRowCount(0);
        tm = (DefaultTableModel) jTable1.getModel();
        System.out.println("BROJ REDOVA pocetak" + tm.getRowCount());
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
        test = 0;
        for (String id : dataMap.keySet()) {
            pVec = dataMap.get(id);
            list = new ArrayList<>();

            for (int i = 0; i < pVec.size(); i++) {
                p = pVec.get(i);

                list.add(new String[]{p.user_id, p.rfid_id, p.name, p.check_in.toString()});

            }
             
            for (String[] data : list) {
                test++;
                tm.addRow(data);
            }

        }
        System.out.println(test);
        System.out.println(tm.getRowCount() + " BR KRAJ");
        tm.fireTableDataChanged();
        jTable1.repaint();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tm);
        jTable1.setRowSorter(sorter);

        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(3, SortOrder.DESCENDING)));
        sorter.sort();
        return dataMap;
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(0, 70, Short.MAX_VALUE))
                            .addComponent(jTextField1)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(276, Short.MAX_VALUE))))
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
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
