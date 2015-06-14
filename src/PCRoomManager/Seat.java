package PCRoomManager;

import java.io.PrintWriter;

public class Seat {
	private int computerId;
	private String name;
	private User user;
	private PrintWriter pw;
	
	public Seat(int computerId, String name, User user, PrintWriter pw){
		this.computerId = computerId;
		this.name = name;
		this.user = user;
		this.pw = pw;
	}
	
	public int getComputerId(){
		return computerId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setUser(User user){
		this.user = user;
	}
	
	public User getUser(){
		return user;
	}

	public void setPrintWriter(PrintWriter pw) {
		this.pw = pw;
	}
	
	public PrintWriter getPrintWriter(){
		return pw;
	}
}
