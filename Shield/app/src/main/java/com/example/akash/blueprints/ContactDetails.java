package com.example.akash.blueprints;

public class ContactDetails implements Comparable<ContactDetails>{

	private String sContactName, sContactNumber;
	private Boolean isChecked = false;
	private int contactPhotoId;

	@Override
	public int hashCode() {
		return (""+sContactNumber).hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if(this == other) return true;
		ContactDetails guest = (ContactDetails) other;
		return (this.sContactNumber.equals(guest.sContactNumber));
	}
	public int getContactPhotoId() {
		return contactPhotoId;
	}
	public void setContactPhotoId(int contactPhotoId) {
		this.contactPhotoId = contactPhotoId;
	}
	public String getsContactName() {
		return sContactName;
	}
	public void setsContactName(String sContactName) {
		this.sContactName = sContactName;
	}
	public String getsContactNumber() {
		return sContactNumber;
	}
	public void setsContactNumber(String sContactNumber) {
		this.sContactNumber = sContactNumber;
	}
	public Boolean getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public int compareTo(ContactDetails another) {
		return this.contactPhotoId - another.contactPhotoId;
	}
}
