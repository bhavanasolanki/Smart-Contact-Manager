package com.smart.entities;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Table(name="CONTACT")
public class Contact {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int Cid;
	
	private String name;
	private String email;
	private String nickname;
	private String occupation;
	private String imageurl;
	private String phoneNo;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getCid() {
		return Cid;
	}
	public void setCid(int cid) {
		Cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	@Override
	public String toString() {
		return "Contact [Cid=" + Cid + ", name=" + name + ", email=" + email + ", nickname=" + nickname
				+ ", occupation=" + occupation + ", imageurl=" + imageurl + ", phoneNo=" + phoneNo + "]";
	}
	public Contact(int cid, String name, String email, String nickname, String occupation, String imageurl,
			String phoneNo) {
		super();
		Cid = cid;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.occupation = occupation;
		this.imageurl = imageurl;
		this.phoneNo = phoneNo;
	}
	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.Cid==((Contact)obj).getCid();
	}
	
	

}

