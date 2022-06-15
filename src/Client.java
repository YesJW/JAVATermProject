import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Client extends JFrame implements Runnable{
    JTextField textF;
    JScrollPane scrollPane;
    JTextPane textA;
    String name;
    Socket socket;
    PrintWriter out;
    BufferedReader in;

    ConnectDatabase db = new ConnectDatabase();
    public Client(){ //생성자 정의
        join_S();
        new LoginPage();
    }

    //회원가입 페이지지
   public class LoginPage extends JFrame{
        JTextField f1;
        JPasswordField f2;
        JLabel l1,l2,l3;
        JButton b1,b2;
        ImageIcon image;
        Image icon;
        public LoginPage(){
            setTitle("Login Page");
            setSize(300,400);
            setLocation(800,300);
            setLayout(null);
            Font font = new Font("맑은 고딕",1,15);
            f1 = new JTextField();
            f2 = new JPasswordField();
            l1 = new JLabel();
            l2 = new JLabel();
            l1.setText("아이디 : ");
            l1.setFont(font);
            l2.setText(("비밀번호 : "));
            l2.setFont(font);
            b1 = new JButton("Login");
            b2 = new JButton("Sign up");
            image = new ImageIcon("logo.jpg");
            icon = image.getImage().getScaledInstance(285, 400, Image.SCALE_SMOOTH);
            image = new ImageIcon(icon);
            l3 = new JLabel();
            l3.setIcon(image);
            l3.setBounds(0,0,300,400);
            l1.setBounds(52,120,100,20);
            f1.setBounds(110,120,100,20);
            l2.setBounds(38,160,100,20);
            f2.setBounds(110,160,100,20);
            b1.setBounds(60,240,80,20);
            b2.setBounds(150,240,80,20);
            add(f1); add(f2); add(l1); add(l2); add(b1); add(b2); add(l3);
            setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //로그인 버튼 이벤트 처리
            b1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean b = db.login(f1.getText(), String.valueOf(f2.getPassword())); //디비로부터 데이터가 있는지 검사

                    if(b) //디비에 데이터가 있다면
                    {
                        name = getName(f1.getText());

                        setVisible(false);
                        new Chat();
                    }
                    else //데이터가 없다면
                    {
                        JOptionPane jOptionPane=new JOptionPane();
                        jOptionPane.showMessageDialog(null, "로그인에 실패하였습니다.");
                    }
                }
            });

            //회원가입 버튼 클릭
            b2.addActionListener(e -> {
                setVisible(false);
                new Signup();
            });
        }
        public String getName(String name){
            return db.getName(name);
        }
    }

    public class Signup extends JFrame{ //회원가입 페이지
        JTextField f1, f2, f3;
        JLabel l1,l2,l3,l4;
        JButton b1,b2;
        ImageIcon image;
        Image icon;
        public Signup(){
            setTitle("Signup Page");
            setSize(300,400);
            setLayout(null);
            setLocation(800,300);
            Font font = new Font("맑은 고딕",1,15);
            f1 = new JTextField();
            f2 = new JTextField();
            f3 = new JTextField();
            l1 = new JLabel();
            l2 = new JLabel();
            l4 = new JLabel();
            l1.setText("아이디 : ");
            l1.setFont(font);
            l2.setText("비밀번호 : ");
            l2.setFont(font);
            l4.setText("닉네임 : ");
            l4.setFont(font);
            b1 = new JButton("sign up");
            b2 = new JButton("back");
            image = new ImageIcon("logo.jpg");
            icon = image.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
            image = new ImageIcon(icon);
            l3 = new JLabel();
            l3.setIcon(image);
            l3.setBounds(90,50,100,50);
            l1.setBounds(52,120,100,20);
            f1.setBounds(110,120,100,20);
            l2.setBounds(38,160,100,20);
            f2.setBounds(110,160,100,20);
            l4.setBounds(50,200,100,20);
            f3.setBounds(110,200,100,20);
            b1.setBounds(60,260,80,20);
            b2.setBounds(150,260,80,20);
            add(f1); add(f2); add(l1); add(l2); add(b1); add(b2); add(l3); add(l4); add(f3);
            setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JOptionPane jOptionPane=new JOptionPane();
            //회원가입 이벤트 처리
            b1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean b = db.check_Signin(f1.getText()); //디비로부터 이미 존재하는 아이디인지 확인
                    if(b)
                    {
                        jOptionPane.showMessageDialog(null, "이미 존재하는 아이디 입니다.");
                    }
                    else{
                        db.SignUp(f1.getText(),f2.getText(),f3.getText());
                        jOptionPane.showMessageDialog(null, "회원가입에 성공하였습니다.");
                        setVisible(false);
                        new LoginPage();
                    }
                }
            });
            b2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    new LoginPage();
                }
            });
        }

    }

    public class Chat extends JFrame implements ActionListener{ //채팅 화면
        JPanel panel;
        JButton quit,sendImage;

        public Chat(){
            setTitle(name);
            setSize(500,400);
            Container con = getContentPane();
            con.setLayout(new BorderLayout());
            panel = new JPanel();
            panel.setLayout(new BorderLayout());
            quit = new JButton("종료");
            sendImage = new JButton("이미지 전송");
            textA = new JTextPane();
            scrollPane = new JScrollPane(textA);
            textF = new JTextField();
            textA.setEditable(false);
            panel.add(textF,BorderLayout.CENTER);
            panel.add(sendImage,BorderLayout.WEST);
            panel.add(quit,BorderLayout.EAST);
            con.add("Center",scrollPane);
            con.add("South",panel);
            textF.addActionListener(this); //채팅 입력 이벤트
            setVisible(true);
            setResizable(true);
            setLocation(800,300);
            addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){ //JFrame 화면을 닫으면 발생하는 이벤트
                    out.println(name+" 님이 퇴장하였습니다.");
                    System.exit(0);
                }
            });
            String str = name+": 님이 입장하였습니다.";
            String str2[] = str.split(":");
            out.println(str2[0]+" 님이 입장하였습니다.");

            quit.addActionListener(e -> {
                out.println(name+" 님이 퇴장하였습니다.");
                System.exit(0);
            });

            sendImage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
                    fileChooser.setFileFilter(filter);
                    int returnVal = fileChooser.showOpenDialog(null);
                    if (returnVal == 0) {
                        // 파일 선택
                        String img = fileChooser.getSelectedFile().getAbsolutePath();
                        String[] imgPath;
                        imgPath = img.split("\\\\");
                        String imgP = "";
                        for(String s : imgPath)
                        {
                            imgP += s+"\\";
                            System.out.println(s+"\n");
                        }
                        out.println(name+"#"+imgP);
                        //textA.insertIcon(new ImageIcon("C:\\Users\\ssk01\\Documents\\)JtgeTh.jpg"));

                        System.out.println(imgP);
                        //db.addImg(img);
                    }
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) { //채팅 입력 이벤트 처리
            LocalDateTime now = LocalDateTime.now(); //현재 시간을 받아옴
            System.out.println(now);
            String formatedNow = now.format(DateTimeFormatter.ofPattern("H시 mm분")); //받아온 시간의 형식 지정

            String str = textF.getText();
            if(str.equals("")) //빈 채팅일 경우 메시지 박스 출력
            {
                JOptionPane jOptionPane = new JOptionPane();
                jOptionPane.showMessageDialog(null, "내용을 입력해주세요.");
            }
            else {
                out.println(name + " : " + str + "            [" + formatedNow + "]"); // 서버로 메세지 전송
                System.out.println("서버로 전송됨");
                textF.setText("");
            }
        }


    }
    //메인 함수
    public static void main(String[] args) throws IOException {
        new Client();
    }

    public void join_S(){ //서버에 연결하는 메소드
        try{
            socket = new Socket("localhost",5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(this);
        t.start();
    }


    @Override
    public void run() {
        while(true){
            try{
                String str = in.readLine();
                if(str.contains(".jpg") || str.contains(".JPG") || str.contains(".gif")||str.contains(".png")) //사진을 전송하였을 경우
                {
                    String st[] = str.split("#");
                    Document doc = textA.getDocument();
                    int len = st[1].length() - 1;

                    try {
                        ImageIcon img = new ImageIcon(st[1].substring(0,len));
                        Image icon = img.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                        img = new ImageIcon(icon);
                        textA.insertIcon(img);
                        doc.insertString(doc.getLength(),(System.lineSeparator()),null);
                        textA.setCaretPosition(textA.getDocument().getLength());
                        doc.insertString(doc.getLength(),(System.lineSeparator()),null);
                        doc.insertString(doc.getLength(),st[0]+" 님이 사진을 전송하였습니다."+System.lineSeparator(),null);
                    } catch (BadLocationException b) {
                        b.printStackTrace();
                    }
                }
                else
                {
                    try {
                        Document doc = textA.getDocument();
                        doc.insertString(doc.getLength(), str+"\n", null);
                        textA.setCaretPosition(textA.getDocument().getLength());
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}