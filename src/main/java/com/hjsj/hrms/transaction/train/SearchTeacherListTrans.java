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
 * <p>Title:取得教师对象列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-16:15:22:55</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchTeacherListTrans extends IBusiness {

	ArrayList dynamicCol=new ArrayList();
	
	public void execute() throws GeneralException {
		
		/**
		 * 取得不是必须的动态列
		 */
		 getDynamicList();
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
				String sql="select R04.* "; 
				String sql2="  ";
				String sql3=" from R04,R41 where R41.R4106=R04.R0401 and R41.R4103='"+eduid+"'";
				StringBuffer sb=new StringBuffer();
				sb.append(sql);
				sb.append(sql2);
				sb.append(sql3);
			 	  ContentDAO dao=new ContentDAO(this.getFrameconn());
			      ArrayList list=new ArrayList();
			   
			      this.frowset = dao.search(sb.toString());
			      /**
			       * 判断是否是存在列
			       */
			      ArrayList lst=new ArrayList();
			      for(int i=0;i<dynamicCol.size();i++)
		          {
		          	BusifieldBean busb=(BusifieldBean)dynamicCol.get(i);
		          	
		          	if(this.getFrowset().findColumn(busb.getItemid())>0)
		          	{
		          		lst.add(busb);
		          		
		          	}
		          }
			      
			      dynamicCol=lst;
			      this.getFormHM().put("dynamicCol",dynamicCol);
			      
			      while(this.frowset.next())
			      {
			      	DynaBean vo=new LazyDynaBean();
			      	vo.set("r0401",PubFunc.nullToStr(this.frowset.getString("r0401")));
			      	vo.set("r0402",PubFunc.nullToStr(this.frowset.getString("r0402")));
			       	vo.set("b0110",PubFunc.nullToStr(this.frowset.getString("b0110"))); 
			        for(int i=0;i<dynamicCol.size();i++)
			          {
			          	BusifieldBean busb=(BusifieldBean)dynamicCol.get(i);
			        	if("D".equals(busb.getItemtype()))
			      		{
			        		vo.set(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getDate(busb.getItemid())));
			      		}
			        	else if("M".equals(busb.getItemtype()))
			        	{
			        		vo.set(busb.getItemid(),Sql_switcher.readMemo(this.getFrowset(),busb.getItemid()));
			        	}
			        	else
			        	{
			        		vo.set(busb.getItemid(),PubFunc.nullToStr(this.getFrowset().getString(busb.getItemid())));
			        	}
			          }
			      	list.add(vo);
			      }
			      
			      this.getFormHM().put("teacherlist",list);
			
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
		 sb.append("select * from t_hr_busifield where fieldsetid='R04' and useflag='1'");
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
		 	this.frowset = dao.search(sb.toString());
		 	String columnName="";
		 	while(this.frowset.next())
		 	{
		 		
		 		columnName=PubFunc.nullToStr(this.frowset.getString("itemid"));
		 		columnName=columnName.toLowerCase();
		 		if("r0401".equals(columnName) ||
		 		   "r0402".equals(columnName) ||
				   "b0110".equals(columnName) ||
				   "i9999".equals(columnName)
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
		 	ex.printStackTrace();
		 }
	}

}
