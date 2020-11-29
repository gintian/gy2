/*
 * 创建日期 2005-8-25
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong
 *  删除明细的搜索
 */
public class SearchDeleteDetailTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		/**
		 * 得到明细添加动态字段
		 */
		 DoCodeBean addlist=new DoCodeBean();
		// this.getFormHM().put("infoAddList",addlist.getDynamicList(this.getFrameconn()));
		 ArrayList listtemp=addlist.getDynamicList(this.getFrameconn(),1);
		
		 /**
		  * 明细列表动态列
		  */
		 this.getFormHM().put("dynamicColDetail",listtemp);
		 ArrayList dynamicColDetail=listtemp;
			
		 if("0".equals(this.getFormHM().get("firstFlag").toString()))
		 {
	    		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
     			String id ="0";
				if(this.getFormHM().get("r19id")!=null)
				{
					id=this.getFormHM().get("r19id").toString();
				}
				if("0".equals(id))
				{
					return;
				}
		
			String sql = "select * from R22 where R2201='"+id+"'";
			
			StringBuffer strsql = new StringBuffer();
			
			strsql.append(sql);
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList list = new ArrayList();
			try {
				this.frowset = dao.search(strsql.toString());
				/*
				 * 得到采集表对象列表
				 */
				 /**
			       * 判断是否是存在列
			       */
		      ArrayList lst=new ArrayList();
		      for(int i=0;i<dynamicColDetail.size();i++)
	          {
	          	BusifieldBean busb=(BusifieldBean)dynamicColDetail.get(i);
	          	if(this.getFrowset().findColumn(busb.getItemid())>0)
	          	{
	          		lst.add(busb);
	          	}
	          }
		      dynamicColDetail=lst;
			
			    while (this.frowset.next()) {
				
				DynaBean vo=new LazyDynaBean();
				
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
			this.getFormHM().put("pickInfoDetaillst", list);
			} catch (Exception sqle) {
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			} 
		}
		else
		{
			this.getFormHM().put("pickInfoDetaillst", new ArrayList());
		}

	}


}
