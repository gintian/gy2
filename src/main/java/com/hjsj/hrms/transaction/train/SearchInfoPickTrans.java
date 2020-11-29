package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:修改信息显示及详细信息显示
 * </p>
 * <p>
 * create time:2005-6-13:15:14:42
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SearchInfoPickTrans extends IBusiness {

	ArrayList dynamicCol=new ArrayList();
	public void execute() throws GeneralException {
		/**
		 * 取得不是必须的动态列
		 */
		getDynamicList(this.getFrameconn());
		 
		DateStyle first_date=new DateStyle();		//建议开始时间
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = (String) hm.get("a_id");
		String flag = (String) this.getFormHM().get("judge");
		/**
		 * 按新增按钮时，则不进行查询，直接退出
		 *  
		 */
		if ("1".equals(flag))
		{
			this.getFormHM().put("pickTableName","");
			this.getFormHM().put("factNum","");
			return;
		}
		cat.debug("------>R22_id=====" + id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("R22");
		/**
		 * 修改显示操作
		 */
		if ("0".equals(flag)) {
		try {
				vo.setString("r2202", id);
				vo = dao.findByPrimaryKey(vo);
//				String adviceDate="";
//				adviceDate=vo.getDate("r2205");
//				first_date.setDateString(adviceDate);
				first_date.setDateString(DateUtils.format(vo.getDate("r2205"),"yyyy-MM-dd"));
				this.getFormHM().put("first_date",first_date);

			} catch (Exception sqle) {
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			} finally {
				this.getFormHM().put("infoPickDetailTb", vo);
			}
		}
		/**
		 * 详细信息展示
		 */
		if ("2".equals(flag)) {
			showInfoPick(id);
		}

	}
	
	
	/**
	 * 执行详细信息操作
	 * 
	 */
	public void showInfoPick(String id) throws GeneralException
	{
		DoCodeBean addlist=new DoCodeBean();
			// this.getFormHM().put("infoAddList",addlist.getDynamicList(this.getFrameconn()));
		ArrayList listtemp=addlist.getDynamicList(this.getFrameconn(),1);
			 /**
			  * 明细列表动态列
			  */
		this.getFormHM().put("dynamicColDetail",listtemp);
		ArrayList dynamicColDetail=listtemp;
				
		String sql = "select * ";
		String sql2="  from R19,R22 where R19.R1901=R22.R2201 and R19.R1901='"+id+"'";
			

		StringBuffer strsql = new StringBuffer();

		strsql.append(sql);
		strsql.append(sql2);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
			this.frowset = dao.search(strsql.toString());
			/*
			 * 得到采集表对象列表
			 */
			  /**判断是否是存在列*/
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
		      ArrayList lst2=new ArrayList();
		      for(int i=0;i<dynamicColDetail.size();i++)
	          {
	          	BusifieldBean busb=(BusifieldBean)dynamicColDetail.get(i);
	          	
	          	if(this.getFrowset().findColumn(busb.getItemid())>0)
	          	{
	          		lst2.add(busb);
	          	}
	          }
		      
		      dynamicColDetail=lst2;
		      this.getFormHM().put("dynamicColDetail",dynamicColDetail);
		      
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
		        		vo.set(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getDate(busb.getItemid())));
		      		}
		        	else
		        	{
		        		vo.set(busb.getItemid(),PubFunc.nullToStr(this.getFrowset().getString(busb.getItemid())));
		        	}
		          }	
				  vo.set("r2202",PubFunc.nullToStr(this.frowset.getString("r2202")));
				  vo.set("r2201",PubFunc.nullToStr(this.frowset.getString("r2201")));
				  vo.set("r2206",PubFunc.nullToStr(this.frowset.getString("r2206")));
					
				  for(int i=0;i<dynamicColDetail.size();i++)
			      {
			          	BusifieldBean busb=(BusifieldBean)dynamicColDetail.get(i);
			        	if("D".equals(busb.getItemtype()))
			      		{
			        		vo.set(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getDate(busb.getItemid())));
			      		}
			        	else
			        	{
			        		vo.set(busb.getItemid(),PubFunc.nullToStr(this.getFrowset().getString(busb.getItemid())));
			        	}
			          }			  
				
				list.add(vo);
			}
			this.getFormHM().put("pickInfolst", list);
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
		 			//System.out.println("-->com.hjsj.hrms.transaction.train-->SearchTeacherListTrans-->getDynamicList"+columnName);
		 			dynamicCol.add(busb);
		 		}
		 	}
		 	
		 	
		 }
		 catch(Exception ex)
		 {
		 	System.out.println("-->com.hjsj.hrms.transaction.train-->SearchInfoPickListTrans-->getDynamicList->error");
		 	ex.printStackTrace();
		 }
		 return dynamicCol;
	}
	

}