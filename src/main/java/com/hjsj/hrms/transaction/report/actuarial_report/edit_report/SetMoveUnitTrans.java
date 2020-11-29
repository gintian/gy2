package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SetMoveUnitTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String personstate=(String)this.getFormHM().get("personstate");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String unitcodes=(String)this.getFormHM().get("unitcodes");
			String id=(String)this.getFormHM().get("id");
			String theyear=(String)this.getFormHM().get("theyear");
			String u02base=(String)this.getFormHM().get("u02base");
			String u0200s=(String)this.getFormHM().get("u0200s");
			String kmethod=(String)this.getFormHM().get("kmethod");
			String report_id =(String)this.getFormHM().get("report_id");
			u0200s= u0200s.substring(0,u0200s.length()-1);
			unitcodes= unitcodes.substring(0,unitcodes.length()-1);
			String temps[]=u0200s.split(",");
			ArrayList uplist = new ArrayList();
			report_id = report_id.substring(report_id.indexOf("_")+1);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		
			if(u02base==null|| "".equals(u02base)){
				ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
				TargetsortBo targetsortBo =new TargetsortBo(this.getFrameconn());
				HashMap map=targetsortBo.getTargetsortMap(this.getFrameconn());
				String fields="";  
					if(map!=null&&map.get(report_id)!=null)
					fields=(String)map.get(report_id);
					fields=fields+",";
					StringBuffer cloums=new StringBuffer();	
					for(int i=0;i<fieldlist.size();i++)
					{
						FieldItem itemfield=(FieldItem)fieldlist.get(i);			
						String itemid=itemfield.getItemid();						
						if(fields.indexOf(itemid+",")!=-1&&!"U0207".equalsIgnoreCase(itemid))
						{
							if(!"U0200".equalsIgnoreCase(itemid)&&!"escope".equalsIgnoreCase(itemid)&&!"U0209".equalsIgnoreCase(itemid)&&!"unitcode".equalsIgnoreCase(itemid))
							  cloums.append(itemid+",");
						}
					}
					 cloums.append("id,");
					 cloums.append("U0207,");
					 cloums.append("u0200,");
					 cloums.append("editflag,");
					// cloums.append("unitcode,");
					 cloums.append("escope,");
				for(int i=0;i<temps.length;i++){
					if(!"".equals(temps[i])){
						ArrayList list = new ArrayList();
						list.add(unitcode.trim());
						list.add(personstate.trim());
						list.add(temps[i].trim());
						list.add(id.trim());
						uplist.add(list);
						
			
				//dao.batchUpdate("update u02 set unitcode=?,U0209=? where u0200=? and id=?",uplist);
				StringBuffer sql = new StringBuffer();
				//先删后插入
				
				sql.append("insert into U02 ("+cloums+"unitcode,U0209)");
				String  temp2s[]=unitcodes.split(",");
				for(int j=0;j<temp2s.length;j++){
					if(!"".equals(temp2s[j])){
						//sql.append("select "+cloums+personstate.trim());
					//	sql.append(" from U02 where id='"+id.trim()+"' and u0200='"+temps[i].trim()+"' and escope='"+report_id+"' ");
						try {
							if(unitcode!=null&&!unitcode.trim().equals(temp2s[j].trim())){
								dao.update("delete from u02 where id='"+id.trim()+"' and u0200='"+temps[i].trim()+"' and escope='"+report_id+"' and unitcode='"+unitcode.trim()+"' " );
							dao.insert(sql.toString()+" select "+cloums+"'"+unitcode.trim()+"','"+personstate.trim()+"'  from U02 where id='"+id.trim()+"' and u0200='"+temps[i].trim()+"' and escope='"+report_id+"' and unitcode='"+temp2s[j].trim()+"' ",new ArrayList());
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				
				}
				}
				
			}else{
				ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
				TargetsortBo targetsortBo =new TargetsortBo(this.getFrameconn());
				HashMap map=targetsortBo.getTargetsortMap(this.getFrameconn());
				String fields="";  
					if(map!=null&&map.get(report_id)!=null)
					fields=(String)map.get(report_id);
					fields=fields+",";
					StringBuffer cloums=new StringBuffer();					
					for(int i=0;i<fieldlist.size();i++)
					{
						FieldItem itemfield=(FieldItem)fieldlist.get(i);			
						String itemid=itemfield.getItemid();						
						if(fields.indexOf(itemid+",")!=-1&&!"U0207".equalsIgnoreCase(itemid))
						{
							if(!"U0200".equalsIgnoreCase(itemid)&&!"escope".equalsIgnoreCase(itemid)&&!"U0209".equalsIgnoreCase(itemid)&&!"unitcode".equalsIgnoreCase(itemid))
							  cloums.append(itemid+",");
						}
					}
					 cloums.append("id,");
					 //cloums.append("U0207,");
					 cloums.append("u0200,");
					 cloums.append("editflag,");
					 cloums.append("unitcode,");
				for(int i=0;i<temps.length;i++){
					if(!"".equals(temps[i])){
//						ArrayList list = new ArrayList();
//						list.add(u02base.trim());
//						list.add(personstate.trim());
//						list.add(temps[i].trim());
//						list.add(id.trim());
//						uplist.add(list);
						StringBuffer sql = new StringBuffer();
						//先删后插入
						if(report_id!=null&&!report_id.equals(u02base.trim())){
						dao.update("delete from u02 where id='"+id.trim()+"' and u0200='"+temps[i].trim()+"' and escope='"+u02base.trim()+"' " );
						sql.append("insert into U02 ("+cloums+"escope,U0209,U0207)");
						sql.append("select "+cloums+u02base.trim()+","+personstate.trim()+",3");
						sql.append(" from U02 where id='"+id.trim()+"' and u0200='"+temps[i].trim()+"' and escope='"+report_id+"' ");
						try {
							dao.insert(sql.toString(),new ArrayList());	
						} catch (SQLException e) {
							e.printStackTrace();
						}
						}
					}
				}

			//	dao.batchUpdate("update u02 set escope=?,U0209=?  where u0200=? and id=?",uplist);
			}
			
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			//自动计算u05
			if("0".equals(kmethod)){
			String  temp2s[]=unitcodes.split(",");
			for(int i=0;i<temp2s.length;i++){
				if(!"".equals(temp2s[i])){
					ab.saveU05Values(temp2s[i],id,theyear);
				}
			}
			if(unitcode!=null&&!"".equals(unitcode))
			ab.saveU05Values(unitcode,id,theyear);
			
			}
			
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
