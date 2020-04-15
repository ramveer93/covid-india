package com.covid.tracker.model;

public class DistrictResult {
	private String stateName;
	private int activeCases;
	private int totalCount;
	private int curedCount;
	private int deathCount;
	private String districtName;
	private int districtPositiveCase;
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public int getActiveCases() {
		return activeCases;
	}
	public void setActiveCases(int activeCases) {
		this.activeCases = activeCases;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getCuredCount() {
		return curedCount;
	}
	public void setCuredCount(int curedCount) {
		this.curedCount = curedCount;
	}
	public int getDeathCount() {
		return deathCount;
	}
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public int getDistrictPositiveCase() {
		return districtPositiveCase;
	}
	public void setDistrictPositiveCase(int districtPositiveCase) {
		this.districtPositiveCase = districtPositiveCase;
	}
	@Override
	public String toString() {
		return "DistrictResult [stateName=" + stateName + ", activeCases=" + activeCases + ", totalCount=" + totalCount
				+ ", curedCount=" + curedCount + ", deathCount=" + deathCount + ", districtName=" + districtName
				+ ", districtPositiveCase=" + districtPositiveCase + "]";
	}
	

}
