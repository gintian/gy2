/*
 * 创建日期 2005-8-19
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * 信息采集主表修改页面搜索操作
 */
public class SearchInfoModifyTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		
		  DoCodeBean addlist=new DoCodeBean();
		  ArrayList infoAddList=addlist.getDynamicList(this.getFrameconn());
		 
		/**
		 * 得到搜索id
		 */
		  HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
	      String id=(String)hm.get("a_id");
	      String sql="select * from R19 where R1901='"+id+"'";
	      this.getFormHM().put("infoId",id);
	      ContentDAO dao=new ContentDAO(this.getFrameconn());
	      try
		  {
//	      	    CachedRowSet rs;
//	            rs = new CachedRowSetImpl();
//	            ps = this.getFrameconn().prepareStatement(sql, 1004, 1007);
//	            ResultSet rst = ps.executeQuery();
//	            rs.populate(rst);
	      	this.frowset=dao.search(sql);
            
	      	ArrayList list=new ArrayList();
	      	/**
	      	 * 判断列是否存在
	      	 */
	      	for(int i=0;i<infoAddList.size();i++)
	      	{
	      		BusifieldBean bsb=(BusifieldBean)infoAddList.get(i);
	      		if(this.frowset.findColumn(bsb.getItemid())>0)
	      		{
	      			list.add(bsb);
	      		}
	      	}
	      	infoAddList=list;
	      	String tableName="";
	      	ArrayList lst=new ArrayList();
	      	/**
	      	 * 得到编码处理对象
	      	 */
	      	DoCodeBean doCodeBean=new DoCodeBean();
	      	RelatingFactory relatingFactory=new RelatingFactory();
	      	relatingFactory.getInstance();
	      	if(this.frowset.next())
	      	{
	      		String value="";
	      		tableName=PubFunc.nullToStr(this.frowset.getString("r1910"));
	      		
	      		this.getFormHM().put("pickTableName",tableName);
	      		for(int i=0;i<infoAddList.size();i++)
	      		{
	      			BusifieldBean bsb=(BusifieldBean)infoAddList.get(i);
	      			if("0".equals(bsb.getCodesetid()))
	      			{
	      				value=this.frowset.getString(bsb.getItemid());
	      				if("D".equals(bsb.getItemtype()))
	      				{
	      					bsb.setValue(PubFunc.DoFormatDate(value));
	      				}
			        	else if("M".equals(bsb.getItemtype()))
			        	{
			        		bsb.setValue(Sql_switcher.readMemo(this.getFrowset(),bsb.getItemid()));
			        	}		        	
	      				else
	      				{
	      					bsb.setValue(value);	
	      				}
	      			}
	      			else
	      			{
	      				if("1".equals(bsb.getCodeflag()))
	      				{
	      					value=this.frowset.getString(bsb.getItemid());
	      					RelatingcodeBean rcb=relatingFactory.getDisplayField(bsb);
	      					bsb.setValue(value);
	      					bsb.setViewvalue(doCodeBean.getRelCodeName(rcb,value));
	      				}
	      				else
	      				{
	      					value=this.frowset.getString(bsb.getItemid());
	      					bsb.setValue(value);
	      					String codeid=bsb.getCodesetid();
	      					bsb.setViewvalue(doCodeBean.getCodeName(codeid,value));
	      				}
	      			}
	      			lst.add(bsb);
	      		}
	      	}
	      	else
	      	{
	      		 this.getFormHM().put("pickTableName","");
	      	}
	      	infoAddList=lst;
	      	this.getFormHM().put("infoAddList",infoAddList);
		  }
	      catch(Exception ex)
		  {
	      	ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);	      	
		  }
	}
}
