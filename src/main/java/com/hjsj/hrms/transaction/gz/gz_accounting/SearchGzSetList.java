/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *<p>Title:列出权限范围内的所有薪资类别</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-29:下午02:43:36</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchGzSetList extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		String flow_flag=(String)this.getFormHM().get("flow_flag");
		String gz_module=(String)this.getFormHM().get("gz_module");
		String length = "";
		HashMap pamaHm=(HashMap)this.getFormHM().get("requestPamaHM");
		if("link0".equals(pamaHm.get("b_query"))){
			DbWizard dbw=new DbWizard(this.frameconn);
			if(!dbw.isExistTable("salaryhistory", false))//没有历史表的时候加上，只带初始几个主键  zhaoxg add 2015-1-20
			{
				Field field=null;
				Table table=new Table("salaryhistory");
				field=new Field("Nbase","Nbase");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				
				field=new Field("A0100","A0100");
				field.setDatatype(DataType.STRING);
				field.setLength(8);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				
				field=new Field("A00Z0","A00Z0");
				field.setDatatype(DataType.DATETIME);
				field.setLength(10);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				
				field=new Field("A00Z1","A00Z1");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				
				field=new Field("salaryid","salaryid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				
				dbw.createTable(table);
			}
		}
		String returnvalue=(String)pamaHm.get("returnvalue");
		/* 标识：2051 薪资审批，从导航图中进入薪资审批界面，点明细表进入后，点返回，回到的界面没有返回按钮导致回不到导航图界面了，不对。 xiaoyun 2014-10-23 start */
		if(returnvalue == null){
			returnvalue = "menu";
		}
		/* 标识：2051 薪资审批，从导航图中进入薪资审批界面，点明细表进入后，点返回，回到的界面没有返回按钮导致回不到导航图界面了，不对。 xiaoyun 2014-10-23 end */
		this.getFormHM().put("returnvalue",returnvalue);
		String isAdd=(String) pamaHm.get("isAdd");
		pamaHm.remove("isAdd");
		if(isAdd==null|| "".equals(isAdd)){
			isAdd="2";
		}
		this.getFormHM().put("isAdd",isAdd);
		if(gz_module==null|| "".equalsIgnoreCase(gz_module))
			gz_module="0";
		int imodule=Integer.parseInt(gz_module);
		try
		{
			    
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.getFrameconn(),this.userView,imodule);
		//	long s=System.currentTimeMillis();
			
			if(flow_flag==null||!"1".equals(flow_flag))
			{
				if(pamaHm.get("b_query")!=null&& "link".equals((String)pamaHm.get("b_query")))
					pgkbo.setShow_reject(1);
			}
			
			ArrayList list=pgkbo.searchGzSetList(flow_flag);
			//新增薪资类别时，根据数据库字段长度限制输入名称长度，赵旭光加，2013-1-22
			RowSet rs = dao.search("select * from salarytemplate where 1=2");
			ResultSetMetaData data=rs.getMetaData();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					if("cname".equals(columnName)){
						length=data.getColumnDisplaySize(i)+"";
						break;
					}
			 }
			 if(rs!=null){
				 rs.close();
			 }
			 
			this.getFormHM().put("itemid","all");
			if ("1".equals(flow_flag)) {
			  /*审批模式：将工资过滤项目默认为default ，如果为all的话，
			   * 进入审批界面时，无法区分是定位至所有项目还是定位至默认审批项目 wangrd 2013-12-04
			   */ 
			    this.getFormHM().put("itemid","default");			    
			}
			this.getFormHM().put("condid","all");
			this.getFormHM().put("proright_str","");
			this.getFormHM().put("setlist", list);
			this.getFormHM().put("setlist2", list);
			this.getFormHM().put("gz_module", gz_module);
			this.getFormHM().put("flow_flag", flow_flag);
			this.getFormHM().put("order_by", "");
			this.getFormHM().put("length", length);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}

