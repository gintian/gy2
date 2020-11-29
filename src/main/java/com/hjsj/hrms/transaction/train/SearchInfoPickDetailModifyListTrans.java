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

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-15:17:55:12</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchInfoPickDetailModifyListTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		/**
		 * 得到明细添加动态字段
		 */
		 DoCodeBean addlist=new DoCodeBean();
		
		 ArrayList listtemp=addlist.getDynamicList(this.getFrameconn(),1);
		
		 /**
		  * 明细列表动态列
		  */
		this.getFormHM().put("dynamicColDetail",listtemp);
		ArrayList dynamicColDetail=listtemp;
			
	    String id="0";
			
		id=this.getFormHM().get("newr19id").toString();
					
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
		        		vo.set(busb.getItemid(),PubFunc.FormatDate(this.getFrowset().getDate(busb.getItemid().replaceAll("-","."))));
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
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("pickInfoDetaillst", list);
		}
	}

}
