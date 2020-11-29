/*
 * Created on 2005-11-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SavePersonenrollinfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList zpfieldlist=(ArrayList)this.getFormHM().get("zpfieldlist");
		RecordVo constantuser_vo=ConstantParamter.getRealConstantVo("SS_LOGIN_USER_PWD");
		ArrayList valuelist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		String setname=(String)this.getFormHM().get("setname");
		RecordVo constandb_vo=(RecordVo)ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String userbase=constandb_vo.getString("str_value");
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		StringBuffer sql=new StringBuffer();
		boolean havenameuser=false;
		String useraccount="";
		StringBuffer sqlvalues=new StringBuffer();
		String actiontype=(String)this.getFormHM().get("actiontype");
		String usernamefield=constantuser_vo.getString("str_value");
		if(usernamefield !=null && usernamefield.indexOf(",")>0)
			 usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
		else
			 usernamefield="username";
		    String A0100=(String)this.getFormHM().get("a0100");
		   	strsql.append("update ");
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
					  	valuelist.add(PubFunc.DateStringChangeValuelist(fieldItem.getValue()));
				else
				{	
					if("N".equalsIgnoreCase(fieldItem.getItemtype()))
					{
						if(fieldItem.getValue().trim().getBytes().length>0)
						{
							valuelist.add(fieldItem.getValue().trim());
						}
						else
						{
							valuelist.add(null);
						}
							
					}
					else
					{
						//如果输入的字符长度超过了字段长度，自动截取
						if(fieldItem.getValue().trim().getBytes().length>fieldItem.getItemlength())
						{
							valuelist.add(fieldItem.getValue().substring(0,fieldItem.getItemlength()/2));
						}
						else
						{
							valuelist.add(fieldItem.getValue().trim());
						}
					}
				}
			}
			strsql.append(" where a0100='" + A0100 + "'");
		    try{
		       dao.update(strsql.toString(),valuelist);
		    }catch(Exception e){e.printStackTrace();}
		this.getFormHM().put("a0100",A0100);
		this.getFormHM().put("existusermessage","");
		this.getFormHM().put("actiontype","update");
	}
//	获得主集的A0000的最大排序号
	public  String getUserA0000(String strTableName){
		String strsql = "select max(A0000) as a0000 from " + strTableName;
		int userId=1;
		try
		{
			List rs=ExecuteSQL.executeMyQuery(strsql,this.getFrameconn());
			String userNumber;
			if (!rs.isEmpty()) {
				LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				if(rec.get("a0000")!=null)
					userId =Integer.parseInt(rec.get("a0000").toString()) + 1;
				else
					userId=1;
			} else
				userId = 1;			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  //new ExecuteSQL().freeConn();
		}
		return String.valueOf(userId);
	}
	public String getUserId(String tableName) throws GeneralException{
		String strsql = "select max(A0100) as a0100 from " + tableName + " order by A0100";
		List rs=ExecuteSQL.executeMyQuery(strsql,this.getFrameconn());
		int userPlace;
		String userNumber;
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			if(rec.get("a0100")!=null)
			   userPlace =Integer.parseInt(rec.get("a0100").toString()) + 1;
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
