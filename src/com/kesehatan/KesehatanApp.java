package com.kesehatan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class KesehatanApp extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtUmur, txtPenyakit, txtAlamat;
    private JComboBox<String> cmbJenisKelamin;
    private JButton btnAdd, btnUpdate, btnDelete, btnView;

    public KesehatanApp() {
        setTitle("Aplikasi Kesehatan - Data Pasien");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabel
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Umur", "Jenis Kelamin", "Penyakit", "Alamat"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel Input
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.add(new JLabel("Nama:"));
        txtNama = new JTextField();
        inputPanel.add(txtNama);

        inputPanel.add(new JLabel("Umur:"));
        txtUmur = new JTextField();
        inputPanel.add(txtUmur);

        inputPanel.add(new JLabel("Jenis Kelamin:"));
        cmbJenisKelamin = new JComboBox<>(new String[]{"Laki-Laki", "Perempuan"});
        inputPanel.add(cmbJenisKelamin);

        inputPanel.add(new JLabel("Penyakit:"));
        txtPenyakit = new JTextField();
        inputPanel.add(txtPenyakit);

        inputPanel.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        inputPanel.add(txtAlamat);

        add(inputPanel, BorderLayout.NORTH);

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Tambah");
        btnUpdate = new JButton("Ubah");
        btnDelete = new JButton("Hapus");
        btnView = new JButton("Lihat");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnView);

        add(buttonPanel, BorderLayout.SOUTH);

        // Event Handling
        btnAdd.addActionListener(e -> insertData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnView.addActionListener(e -> viewData());

        selectData(); // Load initial data
    }

    private void insertData() {
        String nama = txtNama.getText();
        int umur = Integer.parseInt(txtUmur.getText());
        String jenisKelamin = cmbJenisKelamin.getSelectedItem().toString();
        String penyakit = txtPenyakit.getText();
        String alamat = txtAlamat.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO pasien (nama, umur, jenis_kelamin, penyakit, alamat) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nama);
            pstmt.setInt(2, umur);
            pstmt.setString(3, jenisKelamin);
            pstmt.setString(4, penyakit);
            pstmt.setString(5, alamat);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            resetForm();  // Reset form setelah data ditambahkan
            selectData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin diubah!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nama = txtNama.getText();
        int umur = Integer.parseInt(txtUmur.getText());
        String jenisKelamin = cmbJenisKelamin.getSelectedItem().toString();
        String penyakit = txtPenyakit.getText();
        String alamat = txtAlamat.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE pasien SET nama = ?, umur = ?, jenis_kelamin = ?, penyakit = ?, alamat = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nama);
            pstmt.setInt(2, umur);
            pstmt.setString(3, jenisKelamin);
            pstmt.setString(4, penyakit);
            pstmt.setString(5, alamat);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
            selectData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin dihapus!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM pasien WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            selectData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectData() {
        tableModel.setRowCount(0); // Clear table
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM pasien";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getInt("umur"),
                        rs.getString("jenis_kelamin"),
                        rs.getString("penyakit"),
                        rs.getString("alamat")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void viewData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin dilihat!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM pasien WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                txtNama.setText(rs.getString("nama"));
                txtUmur.setText(String.valueOf(rs.getInt("umur")));
                cmbJenisKelamin.setSelectedItem(rs.getString("jenis_kelamin"));
                txtPenyakit.setText(rs.getString("penyakit"));
                txtAlamat.setText(rs.getString("alamat"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtUmur.setText("");
        txtPenyakit.setText("");
        txtAlamat.setText("");
        cmbJenisKelamin.setSelectedIndex(0);  // Set combo box to the first item
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KesehatanApp().setVisible(true));
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Koneksi ke database berhasil!");
            } else {
                System.out.println("Koneksi ke database gagal!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
