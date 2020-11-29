/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:查询变动信息</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-2:下午05:47:14</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchChangeInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String filterid=(String)map.get("filterid");
			String fieldstr=(String)map.get("fieldstr");
			((HashMap)this.getFormHM().get("requestPamaHM")).remove("filterid");
			((HashMap)this.getFormHM().get("requestPamaHM")).remove("fieldstr");
			//SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			//String tablename=templatebo.createChangeInfoManTable();
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList fieldItemList=templatebo.getField(SalaryCtrlParamBo.COMPARE_FIELD);
			String tablename="t#"+this.userView.getUserName()+"_gz_Bd";
			StringBuffer buf=new StringBuffer();
			buf.append("select * ");
			this.getFormHM().put("strsql", buf.toString());
			buf.setLength(0);
			buf.append("dbname,b0110,e0122,a0101,state,b01101,e01221,a01011,a0100,");
			if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"new".equalsIgnoreCase(filterid))
			{
				RecordVo vo = new RecordVo("gzitem_filter");
				vo.setString("id", filterid);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				vo = dao.findByPrimaryKey(vo);
				fieldstr=vo.getString("cfldname");
			}
			ArrayList visibleList = new ArrayList();
			for(int i=0;i<fieldItemList.size();i++)
			{
				FieldItem item = (FieldItem)fieldItemList.get(i);
				if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())
						&&!"e0122".equalsIgnoreCase(item.getItemid())&&!"a0101".equalsIgnoreCase(item.getItemid())
						&&!"state".equalsIgnoreCase(item.getItemid())&&!"a0100".equalsIgnoreCase(item.getItemid()))
				{
					if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
						continue;
				}
				buf.append(item.getItemid()+","+item.getItemid()+"1,");
				visibleList.add(item);
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			StringBuffer newb=new StringBuffer(buf.toString());
			newb.setLength(newb.length()-1);
			boolean addflag=templatebo.isAddColumn(onlyname, newb.toString());
			if(addflag&&uniquenessvalid!=null&&!"".equals(uniquenessvalid)&&!"0".equals(uniquenessvalid))
			{
				//visibleList.add(DataDictionary.getFieldItem(onlyname));
				buf.append(onlyname+",");
				this.getFormHM().put("onlyitem", DataDictionary.getFieldItem(onlyname));
			}else
			{
				this.getFormHM().put("onlyitem",null);
			}
			this.getFormHM().put("columns", buf.toString());
			buf.setLength(0);
			buf.append(" from ");
			buf.append(tablename);
			this.getFormHM().put("strwhere", buf.toString());
			this.getFormHM().put("fieldItemList", visibleList);
			this.getFormHM().put("filterid",filterid);
			this.getFormHM().put("fieldstr",fieldstr);
			this.getFormHM().put("displayE0122", display_e0122);
			this.getFormHM().put("checkall", "0");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
