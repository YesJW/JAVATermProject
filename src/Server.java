import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;


public class Server extends JFrame{

    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    public JTextArea textA1,textA2, textA3;
    public ServerSocket serverSocket = null;
    public ArrayList<ServerThread> list = new ArrayList<>();
    public LinkedHashSet<String> name = new LinkedHashSet<>();


    public Server(){ //서버 생성자 정의
        setTitle("Server");
        setSize(500,300);
        setLocation(800,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //TextArea의 테두리를 두껍게 하기 위한 테두리 생성
        Border b1 = BorderFactory.createLineBorder(Color.black, 3);
        Border b2 = BorderFactory.createEmptyBorder(7, 7, 7, 7);
        textA1 = new JTextArea(15,10);
        textA1.setEditable(false);
        textA2 = new JTextArea(10,8);
        textA2.setEditable(false);
        textA2.setText("===현재 접속자===");
        textA3 = new JTextArea(1,1);
        textA3.setEditable(false);
        textA3.setText("현재 접속중인 인원: "+list.size());
        //테두리 적용
        textA1.setBorder(BorderFactory.createCompoundBorder(b1,b2));
        textA2.setBorder(BorderFactory.createCompoundBorder(b1,b2));
        textA3.setBorder(BorderFactory.createCompoundBorder(b1,b2));
        textA1.setText("===================서버 로그======================\n");
        add(textA1, BorderLayout.CENTER);
        add(textA2,BorderLayout.WEST);
        add(textA3,BorderLayout.SOUTH);
        setVisible(true);
        setResizable(true);
        startServer();
    }
    public void startServer(){
        try{
            serverSocket = new ServerSocket(5000); //서버 소켓

            while(true){
                socket = serverSocket.accept();
                System.out.println("서버에 연결됨.");
                ServerThread t = new ServerThread(socket); // 서버에 연결되면 쓰레드를 생성하고 실행
                list.add(t);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendAll(String str){ //각 클라이언트에게 메세지 전달
        for(ServerThread s : list){
            s.send(str);
        }
    }
    public static void main(String[] args) {
        new Server();
    }

    public class ServerThread extends Thread{
        Socket socket;
        BufferedReader in;
        PrintWriter out;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                while(true)
                {
                    String str = in.readLine();
                    if(str.contains("입장하였습니다."))
                    { //클라이언트 입장시
                        LocalDateTime now = LocalDateTime.now(); //현재 시간을 받아옴
                        String formatedNow = now.format(DateTimeFormatter.ofPattern("H시 mm분")); //받아온 시간의 형식을 지정
                        String s[] = str.split("\\s");
                        name.add(s[0]); // 접속자 추가
                        textA2.setText("===현재 접속중인 인원===\n");
                        for(String na : name)
                        {
                            textA2.append(na+"\n"); //접속중인 인원 표시
                        }
                        textA1.append(str+"     ["+formatedNow+"]\n"); //클라이언트가 들어 온 시간을 서버에 표시
                    }
                    if(str.contains("퇴장하였습니다."))
                    {
                        LocalDateTime now = LocalDateTime.now(); //현재 시간을 받아옴
                        System.out.println(now);
                        String formatedNow = now.format(DateTimeFormatter.ofPattern("H시 mm분")); //받아온 시간의 형식을 지정
                        String s[] = str.split("\\s");
                        name.remove(s[0]); //종료한 사용자 제거
                        textA2.setText("");
                        textA2.setText("===현재 접속자===\n");
                        for(String na : name)
                        {
                            textA2.append(na);
                        }
                        textA1.append(str+"     ["+formatedNow+"]\n");
                    }
                    textA3.setText("현재 접속중인 인원: "+list.size());
                    sendAll(str);

                }
            } catch (IOException e) {
                list.remove(this);
                textA3.setText("현재 접속중인 인원: "+list.size());
            }
            finally {
                try{
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(String str) {
            out.println(str);
        }
    }

}