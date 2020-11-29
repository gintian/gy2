/*
 * Created on 2005-11-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SavePersonenrolldetailinfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList zpfieldlist=(ArrayList)this.getFormHM().get("zpfieldlist");
		//RecordVo constantuser_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		ArrayList valuelist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		String setname=(String)this.getFormHM().get("setname");
		RecordVo constandb_vo=(RecordVo)ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String userbase=constandb_vo.getString("str_value");
		String A0100=(String)this.getFormHM().get("a0100");
		String I9999=(String)this.getFormHM().get("i9999");
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		StringBuffer sqlvalues=new StringBuffer();
		String actiontype=(String)this.getFormHM().get("actiontype");
		if("new".equalsIgnoreCase(actiontype)){
		strsql.append("insert into ");
		strsql.append(userbase + setname);
		strsql.append("(");
		for(int i=0;i<zpfieldlist.size();i++)
		{
			FieldItem fieldItem=(FieldItem)zpfieldlist.get(i);
			strsql.append(fieldItem.getItemid() + ",");
			if("D".equalsIgnoreCase(fieldItem.getItemtype()))
				 if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
				 	valuelist.add(null);
				  else
				  	valuelist.add(fieldItem.getValue());
			else
			valuelist.add(fieldItem.getValue());		
			sqlvalues.append("?,");
		}
		 strsql.append("State,CreateTime,ModTime,CreateUserName,ModUserName,A0100,I9999) values(");
		sqlvalues.append("?,?,?,?,?,?,?)");
		
	    strsql.append(sqlvalues.toString());
	    valuelist.add("0");
	    valuelist.add(DateStyle.getSystemTime());
	    valuelist.add(DateStyle.getSystemTime());
	    valuelist.add(null);
	    valuelist.add(null);
	    valuelist.add(A0100);
	    valuelist.add(getUserI9999(userbase + setname,A0100,this.getFrameconn()));
	    try{
	       dao.insert(strsql.toString(),valuelist);
	    }catch(Exception e){e.printStackTrace();}
	    }else
	    {
			strsql.append("update  ");
			strsql.append(userbase + setname);
			strsql.append(" set ");
			for(int i=0;i<zpfieldlist.size();i++)
			{
				FieldItem fieldItem=(FieldItem)zpfieldlist.get(i);
				if(i==zpfieldlist.size()-1)
				    strsql.append(fieldItem.getItemid() + "=?");
				else
					strsql.append(fieldItem.getItemid() + "=?,");
				if("D".equalsIgnoreCase(fieldItem.getItemtype()))
					 if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					 	valuelist.add(null);
					  else
					  	valuelist.add(fieldItem.getValue());
				else
				valuelist.add(fieldItem.getValue());	
			}		
			strsql.append( " where a0100='" + A0100 + "' and i9999=" + I9999);
		    try{
		       dao.update(strsql.toString(),valuelist);
		    }catch(Exception e){e.printStackTrace();}
	    }
	}
//	获得主集的A0000的最大排序号
	public  String getUserI9999(String strTableName,String userid,Connection conn){
		StringBuffer strsql=new StringBuffer();
		strsql.append("select max(I9999) as I9999 from ");
		strsql.append(strTableName);
		strsql.append(" where a0100");
		strsql.append("='");
		strsql.append(userid);
		strsql.append("'");
		List rs=ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
		int id;
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			if(rec.get("i9999")!=null)
				id =Integer.parseInt(rec.get("i9999")!=null && rec.get("i9999").toString().trim().length()>0?rec.get("i9999").toString():"0") + 1;
			else
				id=1;
		} else
			id = 1;
		return String.valueOf(id);
	}
	public String getUserId(String tableName) throws GeneralException{
		String strsql = "select max(A0100) as a0100 from " + tableName + " order by A0100";
		List rs=ExecuteSQL.executeMyQuery(strsql,this.getFrameconn());
		int userPlace;
		String userNumber;
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			if(rec.get("a0100")!=null)
			   userPlace =Integer.parseInt(rec.get("a0100")!=null && rec.get("a0100").toString().trim().length()>0?rec.get("a0100").toString():"0") + 1;
			else
			   userPlace=1;
		} else
			userPlace = 1;
		userNumber = Integer.toString(userPlace);
		for (int i = 0; i < 8 - (Integer.toString(userPlace)).length(); i++)
			userNumber = "0" + userNumber;
		cat.debug("userNumber ->" + userNumber);
		
		return userNumber;
	}
}
