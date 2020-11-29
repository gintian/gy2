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
 * <p>Title:培训活动资料信息</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-17:15:35:38</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchEduInfoListTrans extends IBusiness {
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
				  String sql="select R07.* "; 
				  String sql2=" from R07,R41 where R07.R0701=R41.R4114 and R41.R4103='";
				  String sql3=eduid;
				  String sql4="'";
				  StringBuffer sb=new StringBuffer();
				  sb.append(sql);
				  sb.append(sql2);
				  sb.append(sql3);
				  sb.append(sql4);
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
			      	vo.set("r0701",PubFunc.nullToStr(this.frowset.getString("r0701"))); 
			      	vo.set("r0702",PubFunc.nullToStr(this.frowset.getString("r0702")));  
			      	 
			    	for(int i=0;i<dynamicCol.size();i++)
			      	{
			      		BusifieldBean busb=(BusifieldBean)dynamicCol.get(i);
			      		if("D".equals(busb.getItemtype()))
			      		{
			      			vo.set(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getString(busb.getItemid())));
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
			      this.getFormHM().put("eduInfolist",list);
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
		 sb.append("select * from t_hr_busifield where fieldsetid='R07' and useflag='1'");
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
		 	this.frowset = dao.search(sb.toString());
		 	String columnName="";
		 	while(this.frowset.next())
		 	{
		 		columnName=PubFunc.nullToStr(this.frowset.getString("itemid"));
		 		columnName=columnName.toLowerCase();
		 		if("r0701".equals(columnName) ||
		 		   "r0702".equals(columnName) ||
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
		 			dynamicCol.add(busb);
		 		}
		 	}
		 	
		 	
		 }
		 catch(Exception ex)
		 {
		 	System.out.println("-->com.hjsj.hrms.transaction.train-->SearchEduInfoListTrans-->getDynamicList->error");
		 	ex.printStackTrace();
		 }
	}


}
