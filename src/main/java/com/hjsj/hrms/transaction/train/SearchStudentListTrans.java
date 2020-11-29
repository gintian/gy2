package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训学员列表交易类</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-17:14:28:50</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchStudentListTrans extends IBusiness {

	ArrayList dynamicCol=new ArrayList();
	
	public void execute() throws GeneralException {
	/**
	 * 得到活动编号
	 */
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String eduid="0";
	if(hm.get("a_id")!=null)
	{
		eduid=hm.get("a_id").toString();
		this.getFormHM().put("eduid",eduid);
	}
	else
	{
		if(this.getFormHM().get("eduid")!=null)
		{
			eduid=this.getFormHM().get("eduid").toString();
		}
	}
	try
	{
			  String sql="select * "; 
			  String sql2=" from R40 ";
			  String sql3=" where R4005='"+eduid+"'";
			  StringBuffer sb=new StringBuffer();
			  sb.append(sql);
			  sb.append(sql2);
			  sb.append(sql3);
		 	  ContentDAO dao=new ContentDAO(this.getFrameconn());
		      ArrayList list=new ArrayList();
		   
		      this.frowset = dao.search(sb.toString());
		      while(this.frowset.next())
		      {
		      	DynaBean vo=new LazyDynaBean();
		      	vo.set("r4001",PubFunc.nullToStr(this.frowset.getString("r4001"))); 
		      	vo.set("r4002",PubFunc.nullToStr(this.frowset.getString("r4002")));  
		      	vo.set("r4005",PubFunc.nullToStr(this.frowset.getString("r4005")));  
		      	vo.set("b0110",PubFunc.nullToStr(this.frowset.getString("b0110")));  
		      	vo.set("e0122",PubFunc.nullToStr(this.frowset.getString("e0122")));  
		      	vo.set("r4006",PubFunc.FormatDate(this.frowset.getDate("r4006")));  
		      	vo.set("r4007",PubFunc.FormatDate(this.frowset.getDate("r4007")));  
		      	vo.set("r4008",new Integer(PubFunc.chgNullInt(this.frowset.getString("r4008"))));  
		      	vo.set("r4009",PubFunc.nullToStr(Sql_switcher.readMemo(frowset, "r4009")));
		      	
		      	//System.out.println(Sql_switcher.readMemo(frowset, "r4009"));
		      	vo.set("r4010",new Double(this.frowset.getDouble("r4010")));   
		      	list.add(vo);
		      }
		      
		      this.getFormHM().put("studentlist",list);
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);	
	}
	
}
	/**
	 * 
	 *得到动态列函数
	 */
	public void  getDynamicList()
	{
		 StringBuffer sb=new StringBuffer();
		 sb.append("select * from t_hr_busifield where fieldsetid='R40' and useflag='1'");
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
		 	this.frowset = dao.search(sb.toString());
		 	String columnName="";
		 	while(this.frowset.next())
		 	{
		 		
		 		columnName=PubFunc.nullToStr(this.frowset.getString("itemid"));
		 		columnName=columnName.toLowerCase();
		 		if("b0110".equals(columnName) ||
		 		   "e0122".equals(columnName) ||
				   "r4001".equals(columnName) ||
				   "r4002".equals(columnName) ||
				   "r4005".equals(columnName) ||
				   "r4006".equals(columnName) ||
				   "r4007".equals(columnName) ||
				   "r4008".equals(columnName) ||
				   "r4009".equals(columnName) ||
				   "r4010".equals(columnName)
				   )
		 		{
		 			
		 		}
		 		else
		 		{
		 			BusifieldBean busb=BusifieldBean.InstanceFactory();
		 			
		 			busb.setItemid(columnName.trim());
		 			busb.setFieldsetid(PubFunc.nullToStr(this.frowset.getString("fieldsetid")).trim());
		 			busb.setCodesetid(PubFunc.NullToZero(this.frowset.getString("codesetid")).trim());
		 			busb.setCodeflag(PubFunc.nullToStr(this.frowset.getString("codeflag")).trim());
		 			busb.setItemtype(PubFunc.nullToStr(this.frowset.getString("itemtype")));
		 			//System.out.println("-->com.hjsj.hrms.transaction.train-->SearchTeacherListTrans-->getDynamicList"+columnName);
		 			dynamicCol.add(busb);
		 		}
		 	}
		 	
		 	
		 }
		 catch(Exception ex)
		 {
		 	System.out.println("-->com.hjsj.hrms.transaction.train-->SearchTeacherListTrans-->getDynamicList->error");
		 	ex.printStackTrace();
		 }
	}


}
