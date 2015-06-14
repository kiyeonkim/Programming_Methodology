package PCRoomManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Client {
	private static String userId;
	private static String computerId;
	JFrame clientFrame = new JFrame();
	
	JPanel	btnPanel = new JPanel();
	JButton registBtn = new JButton("회원가입");
	JButton loginBtn = new JButton("로그인");
	
	JPanel signPanel = new JPanel();
	JLabel nameLabel = new JLabel();
	Border nameLabelBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
	
	JPanel inputPanel = new JPanel();
	JTextField idText = new JTextField();
	JPasswordField pwText = new JPasswordField();
	JLabel idLabel = new JLabel("", SwingConstants.CENTER);
	JLabel pwLabel = new JLabel("", SwingConstants.CENTER);
	
	public static void main(String[] args){
		if(args.length != 2){	
			System.out.println("사용법 : java PcClient id 접속할서버ip");
			System.exit(1);
		}
		
		Client c = new Client();

		Socket sock = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		try {
			setComputerId(args[0]);
			sock = new Socket(args[1], 9999);
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			c.initFrame(pw);
			System.out.println(">>"+args[0]);
			pw.println(args[0]);
			pw.flush();
			ClientThread ct = new ClientThread(sock, br, pw);
			ct.start();
			ct.join();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		} finally{
			try{
				if(pw != null){
					pw.close();
				}
			}catch(Exception e){}
			
			try {
				if(br != null){
					br.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				if(sock != null){
					sock.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			System.out.println("접속 종료");
		}
	}
	public static void setUserId(String id){
		userId = id;
	}
	public static String getUserId(){
		return userId;
	}
	public static void setComputerId(String id){
		computerId = id;
	}
	public static String getComputerId(){
		return computerId;
	}
	
	public void initFrame(final PrintWriter pw){
		PrintWriter framePw = pw;
		
		// 프레임 설정
		clientFrame.setTitle("client view");
		clientFrame.setSize(300, 300);
		clientFrame.getContentPane().setLayout(new BorderLayout());
		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 회원가입 로그인 버튼 
		btnPanel.setLayout(new FlowLayout());
		btnPanel.add(registBtn);
		btnPanel.add(loginBtn);

		// 프로그램 제목
		signPanel.setPreferredSize(new Dimension(300,80));
		nameLabel.setText("   PC Clinet   ");
		nameLabel.setFont(new Font("Arial", 30, 30));
		nameLabel.setBorder(nameLabelBorder);
		signPanel.add(nameLabel);
		
		// 정보 입력창 
		inputPanel.setPreferredSize(new Dimension(300, 30));		
		inputPanel.setLayout(new GridLayout(2, 2));
		idLabel.setText("ID : ");
		pwLabel.setText("PassWord : ");
		inputPanel.add(idLabel);
		inputPanel.add(idText);
		inputPanel.add(pwLabel);
		inputPanel.add(pwText);
		
		//전체 다 달기 
		clientFrame.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		clientFrame.getContentPane().add(signPanel, BorderLayout.NORTH);
		clientFrame.getContentPane().add(inputPanel, BorderLayout.CENTER);
		clientFrame.setVisible(true);
		
		// 액션리스너 
		//로그인 버튼
		loginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				pw.println("login_member>>"+idText.getText()+">>"+pwText.getText()+">>"+getComputerId());
				pw.flush();
				idText.setText("");
				pwText.setText("");
			}
		});
		
		//회원등록
		registBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JDialog d = new JDialog();
				JPanel p = new JPanel();
				d.getContentPane().setLayout(new BorderLayout());
				
				JLabel inputId = new JLabel("ID : ");
				JLabel inputName = new JLabel("Name : ");
				JLabel inputPw = new JLabel("Password : ");
				JLabel inputPhoneNumber = new JLabel("PhoneNumber : ");
				
				final JTextField txfId = new JTextField();
				final JTextField txfName = new JTextField();
				final JTextField txfPw = new JTextField();
				final JTextField txfPhoneNumber = new JTextField();
				JButton confirmBtn = new JButton("가입");
				
				p.setLayout(new GridLayout(4,4));
				p.add(inputId);
				p.add(txfId);
				p.add(inputName); 
				p.add(txfName);
				p.add(inputPw);
				p.add(txfPw);
				p.add(inputPhoneNumber);
				p.add(txfPhoneNumber);
				
				d.getContentPane().add(p, BorderLayout.CENTER);
				d.getContentPane().add(confirmBtn, BorderLayout.SOUTH);
				d.setBounds(100, 100, 300, 200);
				d.setTitle("회원가입");
				d.setVisible(true);
				
				confirmBtn.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try {
							pw.println("regist_member>>"+txfId.getText()
									+">>"+txfName.getText()
									+">>"+txfPw.getText()
									+">>"+txfPhoneNumber.getText());
							pw.flush();
							JOptionPane.showConfirmDialog(null, "회원가입 완료!","회원가입",JOptionPane.CLOSED_OPTION);
							d.dispose();
						} catch (Exception e2) {
							// TODO: handle exception
							System.out.println(e);
						}
					}
				});
			}
		});
	}
	
	public static void initLoginFrame(final PrintWriter pw){
		PrintWriter loginFramePw = pw;
		final String startTime = Client.printCurrentTime();
		
		
		final JFrame loginFrame = new JFrame();
		JPanel panel = new JPanel();

		JButton overBtn = new JButton("사용종료");
		JLabel	centerLabel = new JLabel("<html>"+"사용자 : "+Client.getUserId()+"<br>"
				+"시작시간 : "+startTime+"<br>"
				+"요금 : 30분당 500원<br><br>"
				+"카운터로 메시지 보내기"
				+"</html>");
		
		final JTextField sendMsgTxtf = new JTextField();
		JButton sendBtn = new JButton("전송");
		
		centerLabel.setFont(new Font("Serif", Font.BOLD, 20));
		centerLabel.setHorizontalAlignment(JLabel.CENTER);
		centerLabel.setVerticalAlignment(JLabel.CENTER);
		
		loginFrame.getContentPane().setLayout(new GridLayout(4,1));
		
		loginFrame.getContentPane().add(centerLabel);
		loginFrame.getContentPane().add(sendMsgTxtf);
		loginFrame.getContentPane().add(sendBtn);
		loginFrame.getContentPane().add(overBtn);
		loginFrame.setSize(300, 500);
		loginFrame.setVisible(true);
		
		//버튼 액션리스너 
		overBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String pay = Client.calPay(startTime);
				pw.println("result_pay>>"+"6000"+">>user_id>>"+Client.getUserId()+">>computer_id>>"+computerId);
				pw.flush();
				JOptionPane.showConfirmDialog(null, "이용하신 금액은 "+"6000"+"원 입니다.","요금정산",JOptionPane.CLOSED_OPTION);
				loginFrame.dispose();
			}
		});
		
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				pw.println("send_com_id>>"+computerId+">>msg>>"+sendMsgTxtf.getText());
				pw.flush();
				sendMsgTxtf.setText("");
			}
		});
	}
	
	public static String calPay(String startTime){
		String resultPay = "0";
		int startHour;
		int startMinute;
		int curHour;
		int curMinute;
		
		String[] splitStartTime = startTime.split(":");
		startHour = Integer.parseInt(splitStartTime[0]);
		startMinute = Integer.parseInt(splitStartTime[1]);
		
		String currentTime = Client.printCurrentTime();
		String[] splitCurrentTime = currentTime.split(":");
		curHour = Integer.parseInt(splitCurrentTime[0]);
		curMinute = Integer.parseInt(splitCurrentTime[1]);
		
		resultPay = String.valueOf((((curHour*60) + curMinute) - ((startHour*60) + startMinute))/30*500);
		
		return resultPay;
	}
	
	public  static String printCurrentTime(){
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		String curTime = sd.format(new Date());
		
		return curTime;
	}
}

class ClientThread extends Thread{
	private Socket sock = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
		
	public ClientThread(Socket sock, BufferedReader br, PrintWriter pw){
		this.sock = sock;
		this.br = br;
		this.pw = pw;
	}
	
	public void run(){
		try {
			String line = null;
			while((line = br.readLine()) != null){
				System.out.println("클라이언트 수신=>"+line);
				
				if(line.contains("password_valid>>")){
					String[] split_line = line.split(">>");
					Client.setUserId(split_line[2]);
					Client.initLoginFrame(pw);
				}else if(line.contains("password_invalid>>")){
					try{
						JOptionPane.showConfirmDialog(null, "비밀번호 오류!!","오류",JOptionPane.CLOSED_OPTION);
					}catch(Exception e){
						System.out.println(e);
					}
				}else if(line.contains("id_invalid>>")){
					try{
						JOptionPane.showConfirmDialog(null, "아이디가 없습니다!!","오류",JOptionPane.CLOSED_OPTION);
					}catch(Exception e){
						System.out.println(e);
					}
				}else if(line.contains("id_duplication>>")){
					try{
						JOptionPane.showConfirmDialog(null, "중복된 아이디가 있습니다!!","오류",JOptionPane.CLOSED_OPTION);
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
}