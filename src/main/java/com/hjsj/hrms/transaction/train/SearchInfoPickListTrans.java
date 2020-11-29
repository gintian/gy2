package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:信息采集列表操作</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-13:10:39:39</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchInfoPickListTrans extends IBusiness {

	 ArrayList dynamicCol=new ArrayList();
	/*
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		/**
		 * 取得不是必须的动态列
		 */
		 getDynamicList(this.getFrameconn());
		
		/**
		 * 得到float类型及int文本框页面字段限定属性
		 */
		String timeAttribute []=GetTrainCoadeTbInfo.getNumAttribute(this.getFrameconn(),"R22","R2206");
		if(timeAttribute.length>=2)
		{
			this.getFormHM().put("timeLength",Integer.toString(Integer.parseInt(timeAttribute[0])+Integer.parseInt(timeAttribute[1])+1));
			this.getFormHM().put("timeDecimalwidth",timeAttribute[1]);
		}
		
		if(this.userView.getStatus()!=4)
		{
			//throw new GeneralException("","非自助平台用户不能使用该功能!","","");
		}
		String sql = "select * ";
		String sql2="  from R19  where R19.R1909='"+this.userView.getUserFullName()+"' and R19.R1907='02'";
			

		StringBuffer strsql = new StringBuffer();

		strsql.append(sql);
		strsql.append(sql2);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
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
			  /**得到采集表对象列表*/
			  while (this.frowset.next()) {
				 DynaBean vo=new LazyDynaBean();
				 vo.set("r1901",PubFunc.nullToStr(this.frowset.getString("r1901")));
				 vo.set("r1906",PubFunc.FormatDate(this.frowset.getDate("r1906")));
				 vo.set("r1909",PubFunc.nullToStr(this.frowset.getString("r1909")));
				 vo.set("r1910",PubFunc.nullToStr(this.frowset.getString("r1910")));
				 for(int i=0;i<dynamicCol.size();i++)
		         {
		          	BusifieldBean busb=(BusifieldBean)dynamicCol.get(i);
		        	if("D".equals(busb.getItemtype()))
		      		{
		        		vo.set(busb.getItemid(),PubFunc.FormatDate((this.getFrowset().getDate(busb.getItemid()))));
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
			this.getFormHM().put("infoPicklist", list);
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
	
	/**
	 * 
	 *得到动态列函数
	 */
	public ArrayList  getDynamicList(Connection con)
	{
		 RelatingFactory relatingFactory=new RelatingFactory();
		 relatingFactory.getInstance();
		 StringBuffer sb=new StringBuffer();
		 sb.append("select * from t_hr_busifield where fieldsetid='R19' and useflag='1'");
		 ContentDAO dao=new ContentDAO(con);
		 try
		 {
		 	this.frowset = dao.search(sb.toString());
		 	String columnName="";
		 	while(this.frowset.next())
		 	{
		 		columnName=PubFunc.nullToStr(this.frowset.getString("itemid"));
		 		columnName=columnName.toLowerCase();
		 		if("r1901".equals(columnName) ||
		 		   "r1906".equals(columnName) ||
				   "r1907".equals(columnName) ||
				   "r1908".equals(columnName) ||
				   "r1909".equals(columnName) ||
				   "r1910".equals(columnName) ||
				   "r1902".equals(columnName) ||
				   "b0110".equals(columnName) ||
				   "e0122".equals(columnName)
				   )
		 		{
		 			
		 		}
		 		else
		 		{
		 			BusifieldBean busb=BusifieldBean.InstanceFactory();
		 			
		 			busb.setItemid(columnName.trim());
		 			busb.setFieldsetid(PubFunc.nullToStr(this.frowset.getString("fieldsetid")).trim());
		 			String codeid=PubFunc.NullToZero(this.frowset.getString("codesetid")).trim();
		 			busb.setCodesetid(codeid);
		 			String codeflag=PubFunc.nullToStr(this.frowset.getString("codeflag")).trim();
		 			busb.setCodeflag(codeflag);
		 			busb.setItemtype(PubFunc.nullToStr(this.frowset.getString("itemtype")));
		 			busb.setItemlength(PubFunc.NullToZero(this.frowset.getString("itemlength")));
		 			busb.setDecimalwidth(PubFunc.NullToZero(this.frowset.getString("decimalwidth")));
		 			if("1".equals(codeflag) && !"0".equals(codeid))
		 			{
		 				RelatingcodeBean rcb=relatingFactory.getDisplayField(busb);
		 				busb.setRelTableName(rcb.getCodetable());
		 				busb.setRelFieldId(rcb.getCodevalue());
		 				busb.setRelFieldDesc(rcb.getCodedesc());
		 			}
		 			dynamicCol.add(busb);
		 		}
		 	}
		 }
		 catch(Exception ex)
		 {
		 	ex.printStackTrace();
		 }
		 return dynamicCol;
	}
}
