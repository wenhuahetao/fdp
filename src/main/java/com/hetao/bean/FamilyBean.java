package com.hetao.bean;

import java.util.List;

public class FamilyBean {

	private List<FamilyCols> familyCols;
	
	public List<FamilyCols> getFamilyCols() {
		return familyCols;
	}

	public void setFamilyCols(List<FamilyCols> familyCols) {
		this.familyCols = familyCols;
	}

	public static class FamilyCols {
		private String family_td;
		private String family_cols_td;
		public String getFamily_td() {
			return family_td;
		}
		public void setFamily_td(String family_td) {
			this.family_td = family_td;
		}
		public String getFamily_cols_td() {
			return family_cols_td;
		}
		public void setFamily_cols_td(String family_cols_td) {
			this.family_cols_td = family_cols_td;
		}
		
	}
}
