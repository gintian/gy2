package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 *<p>Title:GzGetSortEmpTable</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-14:下午01:20:22</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class GzGetSortEmpTable extends IBusiness
{
	public void execute() throws GeneralException
	{
		try{
			StringBuffer sort_table = new StringBuffer();
			sort_table.append("<table width=\"100%\" border=\"0\" class=\"ListTable\"><tr><td class=\"TableRow\" align=\"left\">&nbsp;</td>");
			sort_table.append("<td width=\"60%\" class=\"TableRow\" align=\"center\">指标名称</td>");
			sort_table.append("<td width=\"30%\" class=\"TableRow\" align=\"center\">状态");
			sort_table.append("</td></tr>");
			String itemid = (String)this.getFormHM().get("itemid");
			String operation = (String)this.getFormHM().get("operation");
			String sort_table_detail = (String)this.getFormHM().get("sort_table_detail");
			String salaryid = (String)this.getFormHM().get("salaryid");			
			if("add".equalsIgnoreCase(operation))
			{
				if(!(itemid==null || "".equalsIgnoreCase(itemid)))
				{
					if(sort_table_detail==null || "".equalsIgnoreCase(sort_table_detail))
					{	
						sort_table_detail = itemid+":0";
						String itemdesc = this.getItemdesc(salaryid,itemid);
						sort_table.append(this.getPartTable(itemid,1,"0",itemdesc).toString());
					}else{
						sort_table_detail=sort_table_detail+","+itemid+":0";
						String[] temps = sort_table_detail.split(",");
						for(int i=0;i<temps.length;i++)
						{
							String[] arr = temps[i].split(":");
							String itemdesc = this.getItemdesc(salaryid,arr[0]);
							sort_table.append(this.getPartTable(arr[0],i+1,arr[1],itemdesc).toString());
						}
					}
				}
			}else {
				if(sort_table_detail==null || "".equalsIgnoreCase(sort_table_detail))
				{
					sort_table.append("");
				}else{
					String[] temps = sort_table_detail.split(",");
					for(int i=0;i<temps.length;i++)
					{
						String[] arr = temps[i].split(":");
						String itemdesc = this.getItemdesc(salaryid,arr[0]);
						sort_table.append(this.getPartTable(arr[0],i+1,arr[1],itemdesc).toString());
					}
				}
			}
			sort_table.append("</table>");
			String flag = (String)this.getFormHM().get("flag");
			flag=flag!=null&&flag.trim().length()>0?flag:"";
			if(flag.trim().length()>0){
				this.getFormHM().put("itemid",itemid);
				String itemdesc = this.getItemdesc(salaryid,itemid);
				this.getFormHM().put("itemdesc",itemdesc);
				this.getFormHM().put("flag","ok");
			}else{
				this.getFormHM().put("flag","no");
			}
			this.getFormHM().put("sort_table",SafeCode.encode(sort_table.toString()));
			this.getFormHM().put("sort_table_detail",sort_table_detail);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public StringBuffer getPartTable(String itemid,int id,String sortmode,String itemdesc)
	{
		StringBuffer temp = new StringBuffer();
		temp.append("<tr><td  align=\"left\" class=\"RecordRow\" nowrap>"+id+"</td>");
		temp.append("<td align=\"left\" onclick=\"tr_bgcolor('"+itemid+"_updown','"+itemid+"')\" ondblclick=\"deletefield()\" class=\"RecordRow\" nowrap>"+itemdesc+"</td>");
		temp.append("<td align=\"left\" class=\"RecordRow\" nowrap>");
		temp.append("<select name=\""+itemid+"_updown\" onchange=\"updown('"+itemid+"')\">");
		if("0".equalsIgnoreCase(sortmode))
		{
			temp.append("<option value=\"0\" selected >升序</option>");
			temp.append("<option value=\"1\" >降序</option>");
		}else{
			temp.append("<option value=\"0\" >升序</option>");
			temp.append("<option value=\"1\" selected >降序</option>");
		}
		temp.append("</select>");
		temp.append("</td></tr>");
		return temp;
	}

	public String getItemdesc(String salaryid,String itemid)
	{
		String itemdesc = "";
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		ArrayList itemlist = gzbo.getFieldlist();
		for(int i=0;i<itemlist.size();i++)
		{
			Field fi = (Field)itemlist.get(i);
			if(fi.getName().equalsIgnoreCase(itemid))
			{
				itemdesc = fi.getLabel();
				break;
			}
			
		}
		return itemdesc;
	}
}
