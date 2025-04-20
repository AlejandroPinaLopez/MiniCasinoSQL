/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minicasino_sql;

/**
 *
 * @author M0RD3K41
 */
public class UserInformation {
    
    private int id;
    private String username;
    private double balance;

    public UserInformation(int id, String username, double balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    // Getters y Setters (importante para acceder a la información)
    public int getId() { return id; }
    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; } // Si necesitas actualizar el balance
    
}
