/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotprojekatrfid;

import java.sql.SQLException;

/**
 *
 * @author Milos
 */
public class IOTProjekatRFID {

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
        new Evidencija().setVisible(true);
    }
    
}
