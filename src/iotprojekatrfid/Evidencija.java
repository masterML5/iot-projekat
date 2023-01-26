/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotprojekatrfid;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;


/**
 *
 * @author milosjelic
 */
public class Evidencija extends javax.swing.JFrame {
private static Connection conSQL;
private static final String connectionUrlMySQL = "jdbc:mysql://192.168.1.6:3306/iotprojekat?user=test&password=test123";
    private static int id;
    private UsersPregled up;
    private DataPregled dp;
    
    
    private String datum;
    private String username;
    private int tabbedCount;
    
   

    /**
     * Creates new form Interface
     */
    public Evidencija() throws SQLException {
        initComponents();
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        this.username = username;
        LocalDate today = LocalDate.now();
        
        datum = today.toString();
        
        dateLabel.setText(today.toString());
        // AutoCompleteDecorator.decorate(kategorijeComboBox);
        // kategorijeComboBox.setModel(new DefaultComboBoxModel<>(sveKategorije.toArray(new String[0])));
        up = new UsersPregled();
        dp = new DataPregled();

//        jTabbedPane1.addTab("Prijem", icon, prijem);
//        jTabbedPane1.addTab("Izdavanje", icon, izdavanje);
//        jTabbedPane1.addTab("Otpis", icon, otpis);

         
         jTabbedPane1.add(up, "Korisnici");
         initTabComponent(0);
         jTabbedPane1.add(dp, "Evidencija pregled");
         initTabComponent(1);
    }

    Evidencija(String username) throws SQLException {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        dateLabel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        naslovLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemKorisnici = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemData = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        dateLabel.setText("0000-00-00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(771, Short.MAX_VALUE)
                .addComponent(dateLabel))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dateLabel)
        );

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(842, 495));

        naslovLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        naslovLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        naslovLabel.setText("IOT Projekat Raspberry Pi 4 + MFRC522");

        infoLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Korisnici");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenu2MouseReleased(evt);
            }
        });
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItemKorisnici.setText("Korisnici");
        jMenuItemKorisnici.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemKorisniciActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemKorisnici);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Evidencija");

        jMenuItemData.setText("Korisnici");
        jMenuItemData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenuItemDataMouseReleased(evt);
            }
        });
        jMenu4.add(jMenuItemData);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 847, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(naslovLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(naslovLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabel)
                .addContainerGap())
        );

        jPanel1.getAccessibleContext().setAccessibleParent(this);
        jTabbedPane1.getAccessibleContext().setAccessibleName("Prijem");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed

    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenu2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseReleased
        
        
    }//GEN-LAST:event_jMenu2MouseReleased

    private void jMenuItemKorisniciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemKorisniciActionPerformed
        up = new UsersPregled();
        jTabbedPane1.add(up, "Korisnici");
        tabbedCount = jTabbedPane1.getTabCount();
        initTabComponent(tabbedCount-1);
        jTabbedPane1.setSelectedIndex(tabbedCount -1);
    }//GEN-LAST:event_jMenuItemKorisniciActionPerformed

    private void jMenuItemDataMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemDataMouseReleased
    try {
        dp = new DataPregled();
        jTabbedPane1.add(dp, "Evidencija pregled");
        tabbedCount = jTabbedPane1.getTabCount();
        initTabComponent(tabbedCount-1);
        jTabbedPane1.setSelectedIndex(tabbedCount -1);
    } catch (SQLException ex) {
        Logger.getLogger(Evidencija.class.getName()).log(Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_jMenuItemDataMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Evidencija.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Evidencija().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Evidencija.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    

    String getDatum() {
        String datum1 = datum;

        return datum1;
    }
     private void initTabComponent(int i) {
        jTabbedPane1.setTabComponentAt(i,new ButtonTabComponent(jTabbedPane1));
    } 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemData;
    private javax.swing.JMenuItem jMenuItemKorisnici;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel naslovLabel;
    // End of variables declaration//GEN-END:variables

    protected ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}