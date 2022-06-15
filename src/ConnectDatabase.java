import java.sql.*;

public class ConnectDatabase {
    Connection con;
    public ConnectDatabase(){
        con = makeConnection();
    }

    public static Connection makeConnection() {
        String url = "jdbc:mysql://localhost:3306/user_db?characterEncoding=UTF-8 &	serverTimezone=UTC";
        String id = "root";
        String password = "1234";
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("드라이버 적재 성공");
            con = DriverManager.getConnection(url, id, password);
            System.out.println("데이터베이스 연결 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("연결에 실패하였습니다.");
        }
        return con;
    }

    public void SignUp(String id, String pw, String name){ //회원 가입(db에 데이터 추가)
        Statement st;
        Connection con = makeConnection();
        try{
            st = con.createStatement();
            String s = "INSERT INTO users (ID, PW, NAME) VALUES ";
            s += "('" + id + "', '" + pw + "', '" + name + "');";
            System.out.println(s);
            int i = st.executeUpdate(s);
            if (i == 1)
                System.out.println("레코드 추가 성공");
            else
                System.out.println("레코드 추가 실패");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String id, String pw){ //로그인
        Connection con = makeConnection();
        Statement st;
        ResultSet rs;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT ID, PW FROM users");

            while(rs.next()){
                if((rs.getString("ID").equals(id)) && (rs.getString("PW").equals(pw)))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean check_Signin(String id) { //이미 존재하는 회원인지 체크
        try {
            Connection con = makeConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID FROM users");
            while (rs.next()) {
                if (rs.getString("ID").equals(id))
                    return true;
                else
                    return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getName(String id){ // 닉네임을 얻는 메소드
        Connection con = makeConnection();
        Statement st;
        ResultSet rs;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT ID, NAME FROM users");

            while(rs.next()){
                if((rs.getString("ID").equals(id))){
                    return rs.getString("NAME");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addImg(String img){
        Statement st;
        Connection con = makeConnection();
        try{
            st = con.createStatement();
            String s = "INSERT INTO image (img) VALUES ";
            s += "('" + img + "');";
            System.out.println(s);
            int i = st.executeUpdate(s);
            if (i == 1)
                System.out.println("레코드 추가 성공");
            else
                System.out.println("레코드 추가 실패");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}