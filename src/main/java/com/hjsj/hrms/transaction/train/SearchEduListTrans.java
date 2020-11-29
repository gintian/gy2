package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:获得培训列表交易</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-14:18:00:22</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchEduListTrans extends IBusiness {

	ArrayList dynamicCol=new ArrayList();
	
	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
			
		
		/**
		 * 取得不是必须的动态列
		 */
		 getDynamicList();
		 
		 StringBuffer strsql=new StringBuffer();
		    
		    strsql.append("select * ");
			   
		    strsql.append("  from R31 where R3127='04' or R3127='06'");
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    ArrayList list=new ArrayList();
		    try
		    {
		      this.frowset = dao.search(strsql.toString());
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
		          RecordVo vo=new RecordVo("R31");
		          vo.setString("r3101",PubFunc.nullToStr(this.getFrowset().getString("r3101")));
		          vo.setString("r3126",PubFunc.nullToStr(this.getFrowset().getString("r3126")));  
		          vo.setString("r3128",PubFunc.nullToStr(this.getFrowset().getString("r3128")));     
		          vo.setDouble("r3110",this.getFrowset().getDouble("r3110")); 
		          vo.setDate("r3115",PubFunc.FormatDate(this.getFrowset().getDate("r3115")));   
		          vo.setDate("r3116",PubFunc.FormatDate(this.getFrowset().getDate("r3116")));   
		          vo.setString("r3127",PubFunc.nullToStr(this.getFrowset().getString("r3127")));     
		          vo.setString("r3130",PubFunc.nullToStr(this.getFrowset().getString("r3130"))); 
		          for(int i=0;i<dynamicCol.size();i++)
		          {
		          	BusifieldBean busb=(BusifieldBean)dynamicCol.get(i);
		          	if("D".equals(busb.getItemtype()))
		      		{
		          		vo.setString(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getDate(busb.getItemid())));
		      		}
		        	else if("M".equals(busb.getItemtype()))
		        	{
		        		vo.setString(busb.getItemid(),Sql_switcher.readMemo(this.getFrowset(),busb.getItemid()));
		        	}		          	
		      		else
		      		{
		      			vo.setString(busb.getItemid(),PubFunc.nullToStr(this.getFrowset().getString(busb.getItemid())));
		      		}
		          }
		          list.add(vo);
		      }
		      this.getFormHM().put("edulist",list);
		    }
		    catch(Exception sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
	     }
		/**
		 * 
		 *得到动态列函数
		 */
		public void  getDynamicList()
		{
			 StringBuffer sb=new StringBuffer();
			 sb.append("select * from t_hr_busifield where fieldsetid='R31' and useflag='1'");
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
					   "r3101".equals(columnName) ||
					   "r3110".equals(columnName) ||
					   "r3111".equals(columnName) ||
					   "r3112".equals(columnName) ||
					   "r3115".equals(columnName) ||
					   "r3116".equals(columnName) ||
					   "r3118".equals(columnName) ||
					   "r3119".equals(columnName) ||
					   "r3120".equals(columnName) ||
					   "r3121".equals(columnName) ||
					   "r3122".equals(columnName) ||
					   "r3125".equals(columnName) ||
					   "r3126".equals(columnName) ||
					   "r3127".equals(columnName) ||
					   "r3128".equals(columnName) ||
					   "r3130".equals(columnName) ||
					   "r3131".equals(columnName))
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
			 			//System.out.println("-->com.hjsj.hrms.transaction.train-->SearchEduListTrans-->getDynamicList"+columnName);
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


