package com.bridgelabz.fundooNotes.user.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bridgelabz.fundooNotes.note.model.Note;

@Entity
@Table(name="User")
public class User
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="firstName")
	private String firstName;
	
	@Column(name="lastName")
	private String lastName;
	
	@Column(name="emailid")
	private String emailid;
	
	@Column(name="phoneno")
	private String phoneno;
	
	@Column(name="password")
	private String password;
	
	@Column(name="updateDate")
	private String updateDate;
	
	@Column(name="registerDate")
	private String registerDate;
	
	@Column(name="isVerified")
	private String isVerified;

	@OneToMany(targetEntity=Note.class, cascade =CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="id")
	List<Note> note;
	
	public List<Note> getNote() {
		return note;
	}


	public void setNote(List<Note> note) {
		this.note = note;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmailid() {
		return emailid;
	}


	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}


	public String getPhoneno() {
		return phoneno;
	}


	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}


	public String getRegisterDate() {
		return registerDate;
	}


	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}


	public String getIsVerified() {
		return isVerified;
	}


	public void setIsVerified(String isVerified) {
		this.isVerified = isVerified;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", emailid=" + emailid
				+ ", phoneno=" + phoneno + ", password=" + password + ", updateDate=" + updateDate + ", registerDate="
				+ registerDate + ", isVerified=" + isVerified + ", note=" + note + "]";
	}

	

}
