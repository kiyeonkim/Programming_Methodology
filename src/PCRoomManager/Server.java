package PCRoomManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollBar;

import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class Server {
	public static ArrayList<User> userArrList = new ArrayList<User>();
	public static HashMap hm = new HashMap();
	public static ArrayList<Seat> seatArrList = new ArrayList<Seat>();
	ArrayList<JButton> btnArrList = new ArrayList<JButton>();
	private int connectedSeat = 0;

	public static void main(String[] args){
		try {
			ServerSocket server = new ServerSocket(9999);
			System.out.println("접속대기");
			Server mainServer = new Server();
			mainServer.initManageFrame();
			mainServer.initSeatFrame();
			while(true){
				Socket sock = server.accept();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
				ManageThread manageThread = new ManageThread(mainServer, sock, mainServer.accept(pw), hm);
				manageThread.start();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	//더미데이터 생성 
	public static void setUserData(){
		for(int i = 0; i < 200; i++){
			userArrList.add(new User("TestUserId_"+i, "TestUserName_"+i, "1234", "010-xxxx-xxxx", 0));
		}
	}
	
	//새로 생성된 pw를 받아서 새로운 seat에 배정
	public Seat accept(PrintWriter pw) {
		Seat seat = seatArrList.get(connectedSeat);
		seat.setPrintWriter(pw);
		connectedSeat++;
		return seat;
	}
	//좌석의 위치를 받아서 seatframe을 보여주기 
	class SeatFrameViewer implements ActionListener {
		private int position;

		public SeatFrameViewer(int position) {
			this.position = position;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			showSeatFrame(position);
		}
	}
	public static JTextArea stateTextArea = new JTextArea();
	
	public void initManageFrame() {
		JFrame manageFrame = new JFrame();
		JMenuBar menubar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem itemOpen = new JMenuItem("Open");
		JMenuItem itemSave = new JMenuItem("Save");
		JMenuItem itemDummy = new JMenuItem("InputDummy");
		JMenuItem itemExit = new JMenuItem("Exit");
		JPanel seatPanel = new JPanel();
		seatPanel.setBackground(Color.DARK_GRAY);

		for (int i = 0; i < 20; i++){
			seatArrList.add(new Seat(i, String.format("%02d", i+1), null, null));
		}

		manageFrame.setTitle("PC방 매니저 프로그램");
		manageFrame.getContentPane().setLayout(new BorderLayout());
		manageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuFile.add(itemOpen);
		menuFile.add(itemSave);
		menuFile.add(itemDummy);
		menuFile.add(itemExit);

		menubar.add(menuFile);

		manageFrame.getContentPane().add(seatPanel, BorderLayout.CENTER);
		seatPanel.setLayout(null);

		JButton[] seatButtons = new JButton[20];
		int[] xpos = new int[]{53, 206, 362, 524, 690};
		int[] ypos = new int[]{52, 142, 229, 314};
		for (int i = 0; i < seatButtons.length; i++) {
			seatButtons[i] = new JButton(String.format("좌석 %02d", (i+1)));
			seatButtons[i].addActionListener(new SeatFrameViewer(i));
			seatButtons[i].setBounds(xpos[i%xpos.length], ypos[i/5], 117, 58);
			seatPanel.add(seatButtons[i]);
		}

		JScrollPane stateScroll = new JScrollPane(stateTextArea);
		stateScroll.setBounds(53, 442, 426, 266);
		seatPanel.add(stateScroll);

		JLabel stateLabel = new JLabel("상태확인창");
		stateLabel.setForeground(Color.WHITE);
		stateLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		stateLabel.setBounds(53, 418, 117, 16);
		seatPanel.add(stateLabel);

		JLabel seatLabel = new JLabel("좌석 배치도");
		seatLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		seatLabel.setForeground(Color.WHITE);
		seatLabel.setBounds(53, 24, 117, 16);
		seatPanel.add(seatLabel);

		JButton stateClearBtn = new JButton("상태창 초기화");
		stateClearBtn.setBounds(524, 610, 120, 100);
		seatPanel.add(stateClearBtn);

		JButton memberManageBtn = new JButton("회원 관리");
		memberManageBtn.setBounds(690, 610, 120, 100);
		seatPanel.add(memberManageBtn);
		
		JLabel searchMemberLabel = new JLabel("회원 정보 조회");
		searchMemberLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		searchMemberLabel.setForeground(Color.WHITE);
		searchMemberLabel.setBounds(524, 401, 200, 50);
		seatPanel.add(searchMemberLabel);
		
		searchMemberTxtf = new JTextField();
		searchMemberTxtf.setBounds(523, 477, 121, 61);
		seatPanel.add(searchMemberTxtf);
		searchMemberTxtf.setColumns(10);
		
		JButton searchMemberBtn = new JButton("조회");
		searchMemberBtn.setBounds(690, 478, 120, 61);
		seatPanel.add(searchMemberBtn);

		manageFrame.getContentPane().add(menubar,BorderLayout.NORTH);
		manageFrame.setSize(870, 800);
		manageFrame.setVisible(true);

		//액션 리스너
		searchMemberBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				User user;
				for(int i = 0; i < userArrList.size(); i++){
					if(userArrList.get(i).getId().equals(searchMemberTxtf.getText())){
						user = userArrList.get(i);
						stateTextArea.append("<< 조회 결과 >>\n");
						stateTextArea.append("이름 : "+user.getName()+"\n");
						stateTextArea.append("전화번호 : "+user.getPhoneNumber()+"\n");
						stateTextArea.append("적립금액 : "+user.getUseAccumulation()+"원\n");
						searchMemberTxtf.setText("");
						break;
					}
					if(i == userArrList.size()-1){
						JOptionPane.showConfirmDialog(null, "해당 아이디가 없습니다.","조회 오류",JOptionPane.CLOSED_OPTION);
					}
				}
			}
		});
		
		
		memberManageBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				initModiMemberFrame();
			}
		});
		
		stateClearBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				stateTextArea.setText("");
			}
		});
		itemDummy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setUserData();
				JOptionPane.showConfirmDialog(null, "더미데이터 입력 완료!","데이터 입력",JOptionPane.CLOSED_OPTION);
			}
		});
		
		//유저 정보들 읽어서 넣기 
		itemOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser filechooser = new JFileChooser();
				int answer = filechooser.showOpenDialog(null);
				int i = 0;
				if(answer == filechooser.APPROVE_OPTION){
					try {
						FileInputStream fis = new FileInputStream(filechooser.getSelectedFile());
						ObjectInputStream ois = new ObjectInputStream(fis);
						try {
							Server.userArrList = (ArrayList<User>) ois.readObject();
							JOptionPane.showConfirmDialog(null, "불러오기 완료!","파일 불러오기",JOptionPane.CLOSED_OPTION);
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1){
						e1.printStackTrace();
					}
				}
			}
		});

		//저장
		itemSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				JFileChooser filechooser = new JFileChooser();
				filechooser.setCurrentDirectory(new File("/Users/KimKiYeon/Desktop"));
				int retrival = filechooser.showSaveDialog(null);
				if(retrival == JFileChooser.APPROVE_OPTION) {
					try {
						FileOutputStream fos = new FileOutputStream(filechooser.getSelectedFile());
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(Server.userArrList);
						fos.close();
						JOptionPane.showConfirmDialog(null, "파일저장 완료!","파일저장",JOptionPane.CLOSED_OPTION);
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}
			}
		});

		//종료
		itemExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}
	
	JFrame memberManageFrame;
	
	private static JTextField idTxtf;
	private static JFrame frame = new JFrame();
	private static final JLabel IdLabel = new JLabel("ID");
	private static final JLabel nameLabel = new JLabel("이름");
	private static JTextField nameModiTxtf;
	private static JTextField passwordTxtf;
	private static JTextField phoneTxtf;
	
	public void initModiMemberFrame(){
		
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		idTxtf = new JTextField();
		idTxtf.setBounds(144, 50, 134, 28);
		panel.add(idTxtf);
		idTxtf.setColumns(10);
		IdLabel.setBounds(27, 39, 69, 50);
		
		panel.add(IdLabel);
		nameLabel.setBounds(27, 84, 69, 50);
		
		panel.add(nameLabel);
		
		nameModiTxtf = new JTextField();
		nameModiTxtf.setBounds(144, 95, 134, 28);
		panel.add(nameModiTxtf);
		nameModiTxtf.setColumns(10);
		
		JLabel passwordLabel = new JLabel("패스워드");
		passwordLabel.setBounds(27, 124, 53, 50);
		panel.add(passwordLabel);
		
		passwordTxtf = new JTextField();
		passwordTxtf.setBounds(144, 135, 134, 28);
		panel.add(passwordTxtf);
		passwordTxtf.setColumns(10);
		
		JLabel phoneLabel = new JLabel("휴대전화번호");
		phoneLabel.setBounds(27, 168, 75, 50);
		panel.add(phoneLabel);
		
		phoneTxtf = new JTextField();
		phoneTxtf.setBounds(144, 179, 134, 28);
		panel.add(phoneTxtf);
		phoneTxtf.setColumns(10);
		
		JLabel memberModiLabel = new JLabel("회원 정보 수정");
		memberModiLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		memberModiLabel.setBounds(94, 10, 108, 28);
		panel.add(memberModiLabel);
		
		JButton confirmModifyBtn = new JButton("회원 정보 수정 확인");
		confirmModifyBtn.setBounds(27, 219, 251, 43);
		panel.add(confirmModifyBtn);
		
		frame.setVisible(true);
		
		confirmModifyBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				User user;
				for(int i = 0; i < userArrList.size(); i++){
					if(userArrList.get(i).getId().equals(idTxtf.getText())){
						user = userArrList.get(i);
						user.setName(nameModiTxtf.getText());
						user.setPassword(passwordTxtf.getText());
						user.setPhoneNumber(phoneTxtf.getText());
						JOptionPane.showConfirmDialog(null, "회원 정보 변경 완료!.","완료",JOptionPane.CLOSED_OPTION);
						frame.dispose();
					}else{
						JOptionPane.showConfirmDialog(null, "해당 ID정보가 없습니다.","오류",JOptionPane.CLOSED_OPTION);
					}
				}
			}
		});
	}
	//컴퓨터 id 에 따라 각각의 pw를 반환 
	public PrintWriter searchPrintWriter(String computerId){
		return (PrintWriter) hm.get(computerId);
	}

	public void  seatInfoFrame(){
		JFrame seatFrame  = new JFrame();
	}

	private JFrame seatFrame;
	private JTextField startTimeTxtf;
	private JTextField nameTxtf;
	private JTextField accumulatePointTxtf;
	private JLabel seatNumberLabel;
	private JTextField searchMemberTxtf;

	public void initSeatFrame() {
		seatFrame = new JFrame();

		seatFrame.setSize(300, 200);
		seatFrame.setLocationRelativeTo(null);
		seatFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel panel = new JPanel();
		seatFrame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JLabel memberNameLabel = new JLabel("회원 이름");
		memberNameLabel.setBounds(17, 27, 66, 50);
		panel.add(memberNameLabel);

		JLabel startTimeLabel = new JLabel("사용 시작 시간");
		startTimeLabel.setBounds(17, 78, 84, 32);
		panel.add(startTimeLabel);

		JLabel accumulatePointLabel = new JLabel("적립 포인트");
		accumulatePointLabel.setBounds(17, 116, 200, 50);
		panel.add(accumulatePointLabel);

		seatNumberLabel = new JLabel();
		seatNumberLabel.setBounds(122, 6, 61, 25);
		panel.add(seatNumberLabel);

		startTimeTxtf = new JTextField();
		startTimeTxtf.setBounds(132, 80, 134, 28);
		startTimeTxtf.setEnabled(false);
		panel.add(startTimeTxtf);
		startTimeTxtf.setColumns(10);

		nameTxtf = new JTextField();
		nameTxtf.setBounds(132, 40, 134, 28);
		nameTxtf.setEnabled(false);
		panel.add(nameTxtf);
		nameTxtf.setColumns(10);

		accumulatePointTxtf = new JTextField();
		accumulatePointTxtf.setBounds(132, 127, 134, 28);
		accumulatePointTxtf.setEnabled(false);
		panel.add(accumulatePointTxtf);
		accumulatePointTxtf.setColumns(10);
	}
	
	public void setUserInformation(int computerId, User user) {
		seatArrList.get(computerId).setUser(user);
	}

	public void syncSeatInFrame(Seat seat) {
		User user = seat.getUser();
		if (user != null) {
			startTimeTxtf.setText(user.getStartTime());
			nameTxtf.setText(user.getName());
			accumulatePointTxtf.setText(Integer.toString(user.getUseAccumulation()));
			seatNumberLabel.setText(seat.getName()+"");
		} else {
			startTimeTxtf.setText("");
			nameTxtf.setText("");
			accumulatePointTxtf.setText("");
			seatNumberLabel.setText("");
		}
	}
	
	public static void clearSeatInformation(int computerId){
		User user = null;
		seatArrList.get(computerId).setUser(user);
	}
	
	public void showSeatFrame(int computerId) {
		Seat seat = seatArrList.get(computerId);
		syncSeatInFrame(seat);
		seatFrame.setVisible(true);
	}
	
	public  static String printCurrentTime(){
		SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
		String curTime = sd.format(new Date());
		
		return curTime;
	}
}

class ManageThread extends Thread {
	private Server server;
	private Socket sock;
	private Seat seat;
	private HashMap hm;
	private BufferedReader br;
	private String computerId;
	private boolean initFlag;

	public ManageThread(Server server, Socket sock, Seat seat, HashMap hm) {
		this.server = server;
		this.sock = sock;
		this.seat = seat;
		this.hm = hm;
		try {
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			computerId = br.readLine();
			System.out.println("computer id < "+computerId+" > connect!");
			synchronized (hm) {
				this.hm.put(computerId, seat);
			}
			initFlag = true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	public void run(){
		try {
			String line = null;
			PrintWriter pw = seat.getPrintWriter();
			while((line = br.readLine()) != null){
				//회원 등록
				if(line.contains("regist_member>>")){
					int initAccumulation = 0;
					String[] split_line = line.split(">>");
					
					boolean isDuplicated = false;
					String newUserName = split_line[1];
					for (User user: Server.userArrList) {
						if (user.getId().equals(newUserName)) {
							isDuplicated = true;
							pw.println("id_duplication>>");
							pw.flush();
							break;
						}
					}
					
					if (!isDuplicated) {
						User newUser = new User(
								newUserName,
								split_line[2],
								split_line[3],
								split_line[4],
								initAccumulation);
						Server.userArrList.add(newUser);
					}
					//로그인 기능 
				} else if(line.contains("login_member>>")){
					String[] split_line = line.split(">>");

//					for(int i = 0; i < split_line.length; i++){
//						System.out.println(i+" index =>"+split_line[i]);
//					}

					for(int i = 0; i < Server.userArrList.size(); i++){
						User user = Server.userArrList.get(i);
						if(user.getId().equals(split_line[1])){
							if(user.getPassword().equals(split_line[2])){
								pw.println("password_valid>>"+"id>>"+split_line[1]);
								pw.flush();
								//시작시간 배치 
								user.setStartTime(server.printCurrentTime());
								server.setUserInformation(Integer.parseInt(split_line[3]), user); 
								server.syncSeatInFrame(server.seatArrList.get(Integer.parseInt(split_line[3])));
								break;
							}else{
								pw.println("password_invalid>>");
								pw.flush();
								break;
							}
						}
						if(i == Server.userArrList.size()-1){
							pw.println("id_invalid>>");
							pw.flush();
						}
					}
					//결제금액 수신 
				}else if(line.contains("result_pay>>")){
					String[] split_line = line.split(">>");
					
//					for(int i = 0; i < split_line.length; i++){
//						System.out.println(i+"index =>"+split_line[i]);
//					}
					for(int i = 0; i < Server.userArrList.size(); i++){
						if(Server.userArrList.get(i).getId().equals(split_line[3])){
							Server.userArrList.get(i).addUseAccumulation(Integer.parseInt(split_line[1]));
							Server.clearSeatInformation(Integer.parseInt(split_line[5]));
							JOptionPane.showConfirmDialog(null, (Integer.parseInt(split_line[5])+1)+"번 좌석 결제금액"+split_line[1]+"원","회원 사용 종료",JOptionPane.CLOSED_OPTION);
							break;
						}
					}
				}else if(line.contains("send_com_id>>")){
					String[] split_line = line.split(">>");
//					for(int i = 0; i < split_line.length; i++){
//						System.out.println(i+"index =>"+split_line[i]);
//					}
					Server.stateTextArea.append(split_line[1]+"번 자리 : "+split_line[3]+"\n");
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		} finally {
			synchronized (hm) {
				hm.remove(computerId);
			}
			try {
				if(this.sock != null){
					this.sock.close();
					this.initFlag = false;
				}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
	}
}