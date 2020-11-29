package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class SaveFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String formula = (String)hm.get("formula");
		formula=formula!=null?formula:"";
		formula=SafeCode.decode(formula);
		formula = formula.replaceAll("'", "\"");
		formula=PubFunc.keyWord_reback(formula);
		String itemid = formula.substring(0,formula.indexOf(","));
		String expre = formula.substring(formula.indexOf(",")+1,formula.length());
		String expArr[] = expre.split("-");
		if(expArr.length==2&&expArr[1]!=null&&expArr[1].trim().length()>5){
			String exp = expRe(dao,itemid);
			exp=exp!=null?exp:"";
			expArr[1]=expArr[1].replaceAll("/", "::");
			expArr[1]=expArr[1].replaceAll("=", "::");
			String arr[] = expArr[1].split("::");
			if(arr.length==4&&exp.trim().length()>0){
				if(exp!=null&&exp.trim().length()>1){
					expArr[0]="4"+expArr[0].substring(1);
					String ex = arr[0]+"/"+arr[1]+"="+arr[2]+"/"+arr[3];
					expre = expArr[0]+"-"+ex;
				}
			}else if(arr.length==5&&exp.trim().length()>0&&!"4".equals(exp.substring(0, 1))){
				if(exp!=null&&exp.trim().length()>1){
					exp="4"+exp.substring(1);
					String ex = arr[2]+"/"+arr[4]+"="+arr[1]+"/"+arr[3];
					expre = exp+"-"+ex;
				}else{
					String ex = arr[2]+"/"+arr[4]+"="+arr[1]+"/"+arr[3];
					expre = "3::-"+ex;
				}
			}else{
				if("3".equals(expre.substring(0,1))){
					String ex = arr[2]+"/"+arr[4]+"="+arr[1]+"/"+arr[3];
					expre = "3::-"+ex;
				}
			}
		}
		ProjectSet projectset = new ProjectSet();
		projectset.saveProject(dao,itemid,expre);
	}
	private String expRe(ContentDAO dao,String fielditemid){
		String expre = "";
		try {
			RowSet rs = dao.search("select expression from fielditem where itemid='"+fielditemid.toUpperCase()+"'");
			if(rs.next()){
				expre = rs.getString("expression");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return expre;
	}
}
