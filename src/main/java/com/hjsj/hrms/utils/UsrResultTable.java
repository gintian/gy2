package com.hjsj.hrms.utils;

public class UsrResultTable {
	private String username = "";
	private String infor = "";
	private String dbpre = "";
	public UsrResultTable(){
		
	}
	public UsrResultTable(String username,String infor,String dbpre){
		this.username = username;
		this.infor = infor;
		this.dbpre = dbpre;
	}
	public String usrResult(){
		String usrPreResult = "";
		if(infor!=null&& "2".equals(infor))
			dbpre="B";
		else if(infor!=null&& "2".equals(infor))
			dbpre="K";
		if(username!=null&&username.trim().length()>0){
			if(isNumber(username.substring(0,1))){
				usrPreResult=username+dbpre+"result";
			}else{
				usrPreResult=dbpre+username+"result";
			}
		}
		return usrPreResult;
	}
	public boolean isNumber(String str){
		boolean checkfalg = false;
		try{
			if(str!=null&&str.trim().length()>0)
				Integer.parseInt(str.substring(0,1));
			checkfalg=true;
		}catch (Exception e){
			checkfalg = false;
		}
		return checkfalg;
	}
}
