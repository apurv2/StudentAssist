package com.apurv.studentassist.accommodation.classes;

import java.util.List;

public class RApartmentNamesInUnivs {

	String universityName;
	int universityId;
	List<RApartmentNamesWithType> apartmentNames;

	public RApartmentNamesInUnivs() {
	}

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	public int getUniversityId() {
		return universityId;
	}

	public void setUniversityId(int universityId) {
		this.universityId = universityId;
	}

	public List<RApartmentNamesWithType> getApartmentNames() {
		return apartmentNames;
	}

	public void setApartmentNames(List<RApartmentNamesWithType> apartmentNames) {
		this.apartmentNames = apartmentNames;
	}

	public RApartmentNamesInUnivs(String universityName, int universityId,
			List<RApartmentNamesWithType> apartmentNames) {
		super();
		this.universityName = universityName;
		this.universityId = universityId;
		this.apartmentNames = apartmentNames;
	}

}
