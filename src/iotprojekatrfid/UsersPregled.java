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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

/**
 *
 * @author Milos
 */
public class UsersPregled extends javax.swing.JPanel {

    private static Connection conSQL;
    private static final String connectionUrlMySQL = "jdbc:mysql://192.168.1.6:3306/iotprojekat?user=test&password=test123";

    /**
     * Creates new form UsersPregled
     */
    public UsersPregled() {
        initComponents();
        try {
            conSQL = DriverManager.getConnection(connectionUrlMySQL);
            conSQL.setAutoCommit(false);
        } catch (SQLException ex) {
            System.out.println(ex);

        }
    }

    public TreeMap getUsers() throws SQLException {
        Users u;
        Vector<Users> userVec;
        TreeMap<String, Vector<Users>> usersMap = new TreeMap<>();
        String sqlCheck = "SELECT * FROM users WHERE aktivan AND vazeci";
        PreparedStatement pstCheck = conSQL.prepareStatement(sqlCheck);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
            String id = rs.getString("rfid_uid");
            userVec = usersMap.get(id);

            if (userVec == null) {
                userVec = new Vector<>();
            }
            u = new Users();
            u.rfid_id = rs.getString("rfid_uid");
            u.name = rs.getString("name");
            u.created = rs.getTimestamp("created");
            userVec.add(u);
            usersMap.put(id, userVec);
        }
        return usersMap;
    }
    public ArrayList getNames() throws SQLException{
        ArrayList<String> names = new ArrayList<>();
         String sqlCheck = "SELECT name FROM users WHERE aktivan AND vazeci GROUP BY name";
        PreparedStatement pstCheck = conSQL.prepareStatement(sqlCheck);
        ResultSet rs = pstCheck.executeQuery();
        while (rs.next()) {
         names.add(rs.getString("name"));
        }
        rs.close();
        return names;
    }

    class Users {

        String rfid_id;
        String name;
        Timestamp created;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new java.awt.Dimension(800, 450));
        setMinimumSize(new java.awt.Dimension(800, 450));
        setPreferredSize(new java.awt.Dimension(800, 450));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
