package PCRoomManager;

import java.io.Serializable;

public class User implements Serializable{
	private String id;
	private String name;
	private String password;
	private String phoneNumber;
	private String startTime;
	private int useAccumulation;
	
	
	public User(String id, String name, String password, String phoneNumber, int useAcculation){
		this.id = id;
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.useAccumulation = useAcculation;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setStartTime(String time){
		this.startTime = time;
	}
	
	public String getStartTime(){
		return startTime;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return password;
	}
	
	void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber(){
		return this.phoneNumber;
	}
	
	public void setUseAccumulation(int useAccumulation){
		this.useAccumulation = useAccumulation;
	}
	
	public int getUseAccumulation(){
		return this.useAccumulation; 
	}
	public void addUseAccumulation(int addedPay){
		 this.useAccumulation = this.useAccumulation + addedPay;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;
		System.out.println(obj +", " +id);
		if (obj != null) {
			if (obj instanceof String) {
				isEquals = id.equals(obj);
			} else if (obj instanceof User) {
				isEquals = id.equals(((User) obj).getId());
			}
		}
		
		return isEquals;
	}
}
