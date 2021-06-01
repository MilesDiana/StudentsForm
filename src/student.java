import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class student {
    private JTextField txtName;
    private JTextField txtId;
    private JTextField txtCourse;
    private JTextField txtGrade;
    private JButton saveButton;
    private JButton retrieveFromIDButton;
    private JLabel name;
    private JLabel studentId;
    private JLabel courseName;
    private JLabel grade;
    private JPanel studentDetails;

    Connection con;
    PreparedStatement pst;

    public void Connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/students?serverTimezone=EAT", "root","");
            System.out.println("Connection established");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public student() {
        Connect();

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name,id,course,grade;

                name = txtName.getText();
                id = txtId.getText();
                course = txtCourse.getText();
                grade = txtGrade.getText();

                try {
                    pst = con.prepareStatement("insert into details(sname,reg,course,grade)values(?,?,?,?)");
                    pst.setString(1, name);
                    pst.setString(2, id);
                    pst.setString(3, course);
                    pst.setString(4, grade);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Record Added!");

                    txtName.setText("");
                    txtId.setText("");
                    txtCourse.setText("");
                    txtGrade.setText("");
                    txtName.requestFocus();
                }

                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }

            }
        });

        retrieveFromIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String pid = txtId.getText();
                    pst = con.prepareStatement("select sname,reg,course,grade from details where reg = ?");
                    pst.setString(1, pid);
                    ResultSet rs = pst.executeQuery();

                    if(rs.next()==true)
                    {
                        String name = rs.getString(1);
                        String id = rs.getString(2);
                        String course = rs.getString(3);
                        String grade = rs.getString(4);

                        txtName.setText(name);
                        txtId.setText(id);
                        txtCourse.setText(course);
                        txtGrade.setText(grade);
                    }
                    else
                    {
                        txtName.setText("");
                        txtId.setText("");
                        txtCourse.setText("");
                        txtGrade.setText("");
                        JOptionPane.showMessageDialog(null,"Invalid Student ID");

                    }
                }

                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }

            }
        });
    }

    public static ResultSet RetrieveData() throws Exception {
        //static mysql connection
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        String mysqlUrl = "jdbc:mysql://localhost/students?serverTimezone=EAT";
        Connection con = DriverManager.getConnection(mysqlUrl, "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("Select * from details");
        return rs;
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("student");
        frame.setContentPane(new student().studentDetails);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        ResultSet rs = RetrieveData();

        while(rs.next()) {
            JSONObject record = new JSONObject();
            record.put("sname", rs.getString("sname"));
            record.put("grade", rs.getString("grade"));
            array.add(record);
        }
        jsonObject.put("Student_Grades", array);
        try {
            FileWriter file = new FileWriter("C:/Users/Diana/Desktop/fineline.json");
            file.write(jsonObject.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON file created");
    }
}
