package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchDefinitionTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		  StringBuffer stsql=new StringBuffer();
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  
		  //关键指标分类列表
		  ArrayList list=new ArrayList();
		  try
		  {
		    	stsql.append("select * from ds_key_factortype ");
		        this.frowset = dao.search(stsql.toString());
		        while(this.frowset.next())
		        {
		            RecordVo vo=new RecordVo("ds_key_factortype");
		            vo.setString("typeid",this.getFrowset().getString("typeid"));//分类号
		            vo.setString("name",this.getFrowset().getString("name"));//分类名称
		            vo.setInt("status",this.getFrowset().getInt("status"));//有效控制
		            list.add(vo);
		       }

		    }
		    catch(Exception sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    this.getFormHM().put("keylist",list);
		    this.getFormHM().put("sel","1");//有效控制
		   
	}

}
