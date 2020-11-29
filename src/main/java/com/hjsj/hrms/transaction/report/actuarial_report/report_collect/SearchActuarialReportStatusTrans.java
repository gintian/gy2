package com.hjsj.hrms.transaction.report.actuarial_report.report_collect;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 6, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SearchActuarialReportStatusTrans extends IBusiness {


	public void execute() throws GeneralException {
		//为u02表加escope主键
		 DatabaseMetaData dbmeta=null;
		 String[] types={"TABLE"};
		    ResultSet rset=null;
		    ResultSet rr=null;
		    try
		    {
		      dbmeta = this.getFrameconn().getMetaData();
		      rset=dbmeta.getTables(null,null,null,types);
			      while(rset.next())
			      {	
			          if("u02".equalsIgnoreCase(rset.getString("TABLE_NAME"))){
				      rr=dbmeta.getPrimaryKeys(null,null,rset.getString("TABLE_NAME"));
				      String columns="";
				      DbWizard dbwizard=new DbWizard(this.getFrameconn());
				      while(rr.next())
				      {
				    	  columns+=rr.getString("COLUMN_NAME")+",";
				      }	
				      rr.close();
				     if(columns.toUpperCase().indexOf("ESCOPE")==-1||columns.toUpperCase().indexOf("UNITCODE")==-1){//判断主键escope,unitcode是否存在
				    	  
				    	  Table table=new Table("u02");
				    	  dbwizard.dropPrimaryKey(table.getName());
				    	  Field field=new Field("escope","escope");
				    	  field.setDatatype(DataType.STRING);
				    	  field.setLength(1);
				    	  field.setVisible(false);
				    	  field.setNullable(false);
				    	  field.setKeyable(true);
				    	  table.addField(field);
				    	  field=new Field("unitcode","unitcode");
				    	  field.setDatatype(DataType.STRING);
				    	  field.setLength(30);
				    	  field.setVisible(false);
				    	  field.setNullable(false);
				    	  field.setKeyable(true);
							table.addField(field);
				    	  field=new Field("u0200","u0200");
				    	  field.setDatatype(DataType.STRING);
				    	  field.setLength(10);
				    	  field.setVisible(false);
				    	  field.setNullable(false);
				    	  field.setKeyable(true);
							table.addField(field);
							field=new Field("id","id");
					    	  field.setDatatype(DataType.INT);
					    	  field.setVisible(false);
					    	  field.setNullable(false);
					    	  field.setKeyable(true);
								table.addField(field);
							dbwizard.alterColumns(table);
							dbwizard.addPrimaryKey(table);
				     }
				      break;
			          }
			      }		
		      
			      rset.close();
		    }
		    catch(Exception ee)
		    {
		    	ee.printStackTrace();
		    }
		try
		{
			String cycle_id=(String)this.getFormHM().get("cycle_id");
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			
			int flag=isBasicUnit();
			if(flag==0)
			{
				 throw new GeneralException("当前操作人员没有负责的部门!");	
			}
//			if(flag==1)
//			{
//				 throw new GeneralException("当前操作人员为基层单位负责人，不能操作报表汇总功能!");	
//			}	
		 
			ArrayList cycleList=ab.getCycleList(1);
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String unitCode="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(hm.get("a_code")!=null&&((String)hm.get("a_code")).length()>0)
			{
				unitCode=(String)hm.get("a_code");
				hm.remove("a_code");
			}
			else
				unitCode=(String)this.getFormHM().get("unitCode");
			
			if(cycle_id!=null&&cycle_id.trim().length()>0)
			{
				if(!ab.isExistCycle(cycleList,cycle_id))
				{
					if(cycleList.size()>0)
						cycle_id=((CommonData)cycleList.get(0)).getDataValue();
					else
						cycle_id="";
				}
			}
			else if(cycleList.size()>0)
			{
				cycle_id=((CommonData)cycleList.get(0)).getDataValue();
			}
			
			
			ArrayList actuarialReportStatusList=new ArrayList();
			String unitName="";
			if(unitCode!=null&&unitCode.length()>0&&cycle_id.length()>0)
			{
				actuarialReportStatusList=ab.getActuarialReportStatusList(unitCode,cycle_id);
				RecordVo vo=new RecordVo("tt_organization");
				
				vo.setString("unitcode", unitCode);
				vo=dao.findByPrimaryKey(vo);
				unitName=vo.getString("unitname");
			}
			//获得表：参数部门id,首先判断部门id是否是汇总单位，通过部门id获得下级单位
			String tableHtml="";
			this.getFormHM().remove("tableHtml");
			if("1".equals(ab.isCollectUnit(unitCode))&&cycle_id!=null&&!"".equals(cycle_id)){
				
				ArrayList list = new ArrayList();
				 dao=new ContentDAO(this.getFrameconn());
				 Calendar d=Calendar.getInstance();
					int yy=d.get(Calendar.YEAR);
					int mm=d.get(Calendar.MONTH)+1;
					int dd=d.get(Calendar.DATE);
				 if(cycle_id!=null&&cycle_id.trim().length()>0)
					{
						RecordVo _vo = new RecordVo("tt_cycle");
						_vo.setInt("id", Integer.parseInt(cycle_id));
						_vo =dao.findByPrimaryKey(_vo);
						
						
							//Date date=;
							d.setTime(_vo.getDate("bos_date"));
							yy=d.get(Calendar.YEAR);
							mm=d.get(Calendar.MONTH)+1;
							dd=d.get(Calendar.DATE);
						
					}
					StringBuffer ext_sql = new StringBuffer();
					ext_sql.append(" and ( "+Sql_switcher.year("tt.end_date")+">"+yy);
					ext_sql.append(" or ( "+Sql_switcher.year("tt.end_date")+"="+yy+" and "+Sql_switcher.month("tt.end_date")+">"+mm+" ) ");
					ext_sql.append(" or ( "+Sql_switcher.year("tt.end_date")+"="+yy+" and "+Sql_switcher.month("tt.end_date")+"="+mm+" and "+Sql_switcher.day("tt.end_date")+">="+dd+" ) ) ");
					ext_sql.append(" and ( "+Sql_switcher.year("tt.start_date")+"<"+yy);
					ext_sql.append(" or ( "+Sql_switcher.year("tt.start_date")+"="+yy+" and "+Sql_switcher.month("tt.start_date")+"<"+mm+" ) ");
					ext_sql.append(" or ( "+Sql_switcher.year("tt.start_date")+"="+yy+" and "+Sql_switcher.month("tt.start_date")+"="+mm+" and "+Sql_switcher.day("tt.start_date")+"<="+dd+" ) ) ");	 			
					
			
				//三种情况，未上报，已上报，驳回
				String strsql1 =" select t.unitcode,t.unitname from (   select distinct(tt.unitcode),tt.unitname,tt.a0000 from tt_organization tt,tt_calculation_ctrl ca where ca.id="+cycle_id+" and ca.unitcode=tt.unitcode and ca.flag in(-1,0)  and tt.unitcode in (select unitcode from tt_organization where parentid='"+unitCode+"' and unitcode!=parentid) "+ext_sql.toString()+" )t order by t.a0000";
				//String strsql1 =" select distinct(tt.unitcode),tt.unitname from tt_organization tt  where  tt.unitcode in (select unitcode from tt_organization where parentid='"+unitCode+"' and unitcode!=parentid)";
				
				String strsql2 =" select t.unitcode,t.unitname from (  select distinct(tt.unitcode),tt.unitname,tt.a0000 from tt_organization tt,tt_calculation_ctrl ca where ca.id="+cycle_id+" and ca.unitcode=tt.unitcode and ca.flag=1  and tt.unitcode in (select unitcode from tt_organization where parentid='"+unitCode+"' and unitcode!=parentid)"+ext_sql.toString()+" )t order by t.a0000";
				String strsql3 =" select t.unitcode,t.unitname from ( select distinct(tt.unitcode),tt.unitname,tt.a0000 from tt_organization tt,tt_calculation_ctrl ca where ca.id="+cycle_id+" and ca.unitcode=tt.unitcode and ca.flag=2  and tt.unitcode in (select unitcode from tt_organization where parentid='"+unitCode+"' and unitcode!=parentid)"+ext_sql.toString()+" )t order by t.a0000";
				//获得该部门下级所有的部门
				String strsql = "select t.unitcode,t.unitname from ( select distinct(tt.unitcode),tt.unitname,tt.a0000 from tt_organization tt  where  tt.unitcode in (select unitcode from tt_organization where parentid='"+unitCode+"' and unitcode!=parentid)"+ext_sql.toString()+" )t order by t.a0000";
				
				RowSet rs =dao.search(strsql1);
				int i =0;
				LazyDynaBean bean = new LazyDynaBean();//未上报-1表示，上报0表示，驳回2表示
				 rs =dao.search(strsql3);
				 bean = new LazyDynaBean();//未上报-1表示，上报1表示，驳回2表示
				bean.set("flag", "2");
				String unitnames2 ="";
				while(rs.next()){
					i++;
					unitnames2+=rs.getString("unitname")+",";
				}
				bean.set("size",""+i);
				bean.set("unitnames",unitnames2);
				list.add(bean);
				 rs =dao.search(strsql1);
				 i =0;
				// bean = new LazyDynaBean();//未上报-1表示
				//bean.set("flag", "-1");
				String unitnames0 ="";
					while(rs.next()){
						if(unitnames2.indexOf(rs.getString("unitname"))!=-1)
							continue;
						i++;
						unitnames0+=rs.getString("unitname")+",";
					}
					//bean.set("size",""+i);
					//bean.set("unitnames",unitnames0);
					//list.add(bean);
					 rs =dao.search(strsql2);
					 i =0;
					 bean = new LazyDynaBean();//上报1表示
					bean.set("flag", "1");
					 String unitnames1 ="";
					while(rs.next()){
						if(unitnames2.indexOf(rs.getString("unitname"))!=-1||unitnames0.indexOf(rs.getString("unitname"))!=-1)
							continue;
						i++;
						unitnames1+=rs.getString("unitname")+",";
					}
					bean.set("size",""+i);
					bean.set("unitnames",unitnames1);
					list.add(bean);
					 rs =dao.search(strsql);
					 i =0;
					 bean = new LazyDynaBean();//未上报
					bean.set("flag", "-1");
					 String unitnames3 ="";
					while(rs.next()){
						if(unitnames2.indexOf(rs.getString("unitname"))!=-1||unitnames1.indexOf(rs.getString("unitname"))!=-1)
							continue;
						i++;
						unitnames3+=rs.getString("unitname")+",";
					}
					bean.set("size",""+i);
					bean.set("unitnames",unitnames3);
					list.add(bean);
				 tableHtml = ab.getSubunitInfo(list);
			}
			EditReport edit = new EditReport();
			ArrayList listu01  = edit.getU01FieldList(this.getFrameconn());
			StringBuffer htmlbody =new StringBuffer();
			StringBuffer htmlbody2 = new StringBuffer();
			String paracopy ="";
			String paracopy2 ="";
			String isfillpara = "";
			String isfillpara2 = "";
			try
			{
				if(listu01.size()>0){
					htmlbody.append("<tr>");
					htmlbody2.append("<tr><td>");
					int j =0;
					int m =0;
					for(int i =0;i<listu01.size();i++){
						FieldItem fielditem = (FieldItem)listu01.get(i);
						if(fielditem==null)
							continue;
					String itemid =	fielditem.getItemid();
					String desc =fielditem.getItemdesc();
					desc = desc.replace(",","");
					String type =fielditem.getItemtype();
					int length =fielditem.getItemlength();
					boolean isfill =fielditem.isFillable();
					Object value=new Object();
					ResultSet rs = dao
					.search("select "+itemid+" from u01 where id='"+cycle_id+"' and unitcode='"+unitCode+"' ");
					if("D".equalsIgnoreCase(type)){
						if(isfill)
							isfillpara2 = isfillpara2+desc+",";
						paracopy2=paracopy2+desc+",";
					}else{
						if(isfill)
							isfillpara = isfillpara+desc+",";
					paracopy=paracopy+desc+",";
					}
					if(rs.next()){
						
						if(j%3==0){
							htmlbody.append("</tr>");
							htmlbody.append("<tr>");
						}
						ResultSetMetaData data=rs.getMetaData();
							 int columnType=data.getColumnType(1);
							 if("D".equals(type)){
								 if(columnType==java.sql.Types.TIMESTAMP){
									 value= rs.getTimestamp(itemid)==null?"": PubFunc.FormatDate(String.valueOf(rs.getTimestamp(itemid)));
								 }else if(columnType==java.sql.Types.DATE){
									 value= rs.getDate(itemid)==null?"": PubFunc.FormatDate(rs.getDate(itemid));
								 }else if(columnType==java.sql.Types.TIME){
									 value= rs.getTime(itemid)==null?"": PubFunc.FormatDate(String.valueOf(rs.getTime(itemid))); 
								 }
								 if(isfill){
								 htmlbody.append("<td align=\"left\" width=\"10%\"><font color=red>*</font>"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"   dropDown=\"dropDownDate\" value=\""+value+"\" maxlength=\""+length+"\" >&nbsp;</td>");
								 }else{
									 htmlbody.append("<td align=\"left\" width=\"10%\">"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"   dropDown=\"dropDownDate\" value=\""+value+"\" maxlength=\""+length+"\" >&nbsp;</td>");
										 
								 }
								 }
							 if("N".equals(type)){
								 DecimalFormat myformat1 = new DecimalFormat("###########.#####");//
									switch(columnType)
									{
									case java.sql.Types.FLOAT:
										value= rs.getFloat(itemid)==0.0?"":""+rs.getFloat(itemid);
										value= myformat1.format(value);
										break;
										case java.sql.Types.DOUBLE:
											value= rs.getDouble(itemid)==0.0?"":""+rs.getDouble(itemid);
											value= myformat1.format(value);
											break;
										case java.sql.Types.NUMERIC:
											 value= myformat1.format(rs.getDouble(itemid));
											break;	
									}
									 if(isfill){
									 htmlbody.append("<td align=\"left\" width=\"10%\"><font color=red>*</font>"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"    value=\""+value+"\" maxlength=\""+length+"\" >&nbsp;</td>");
									 }else{
										 htmlbody.append("<td align=\"left\" width=\"10%\">"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"    value=\""+value+"\" maxlength=\""+length+"\" >&nbsp;</td>");
											 
									 }
								}
							 if("A".equals(type)){
								 value = rs.getString(itemid)==null?"":rs.getString(itemid);
								 if(isfill){
								 htmlbody.append("<td align=\"left\" width=\"10%\"><font color=red>*</font>"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"    value=\""+value+"\" maxlength=\""+length+"\" >&nbsp;</td>");
								 }else{
									 htmlbody.append("<td align=\"left\" width=\"10%\">"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"    value=\""+value+"\" maxlength=\""+length+"\" >&nbsp;</td>");
										 
								 }
							 }
							 if(m%3==0){
									htmlbody2.append("</td></tr>");
									htmlbody2.append("<tr><td>");
								}
								 htmlbody2.append(desc+"："+value+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					}else{
						if(j%3==0){
							htmlbody.append("</tr>");
							htmlbody.append("<tr>");
						}
						 if("D".equals(type)){
							 if(isfill){
							 htmlbody.append("<td align=\"left\" width=\"10%\"><font color=red>*</font>"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"   dropDown=\"dropDownDate\"   maxlength=\""+length+"\" >&nbsp;</td>");
							 }
							 else
							 {
								 htmlbody.append("<td align=\"left\" width=\"10%\">"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"   dropDown=\"dropDownDate\"   maxlength=\""+length+"\" >&nbsp;</td>");
									 
							 }
							
							}
						 if("N".equals(type)){
							 if(isfill){
							 htmlbody.append("<td align=\"left\" width=\"10%\"><font color=red>*</font>"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"     maxlength=\""+length+"\" >&nbsp;</td>");
							 }else{
								 htmlbody.append("<td align=\"left\" width=\"10%\">"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"     maxlength=\""+length+"\" >&nbsp;</td>");
									 
							 }
							}
						 if("A".equals(type)){
							 if(isfill){
							 htmlbody.append("<td align=\"left\" width=\"10%\"><font color=red>*</font>"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"    maxlength=\""+length+"\" >&nbsp;</td>");
							 }else{
								 htmlbody.append("<td align=\"left\" width=\"10%\">"+desc+"：</td><td width=\"10%\"><input type=\"text\" name="+desc+"   extra=\"editor\"    maxlength=\""+length+"\" >&nbsp;</td>");
									 
							 }
						 }
						
						if(m%3==0){
							htmlbody2.append("</td></tr>");
							htmlbody2.append("<tr><td>");
						}
						 htmlbody2.append(desc+"：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					}
					j++;
					m++;
					}
					htmlbody.append("</tr>");
					htmlbody2.append("</td></tr>");
				}
		
//				email = _vo.getString("email");
//				telephone = _vo.getString("telephone");
//				linkman = _vo.getString("linkman");
			}
			catch(Exception ee)
			{
			ee.printStackTrace();
			}
			//System.out.println(htmlbody2.toString());
			if(paracopy.length()>0)
				paracopy=paracopy.substring(0,paracopy.length()-1);
			if(paracopy2.length()>0)
				paracopy2=paracopy2.substring(0,paracopy2.length()-1);
			if(isfillpara.length()>0)
				isfillpara=isfillpara.substring(0,isfillpara.length()-1);
			if(isfillpara2.length()>0)
				isfillpara2=isfillpara2.substring(0,isfillpara2.length()-1);
			this.getFormHM().put("isfillpara", isfillpara);
			this.getFormHM().put("isfillpara2", isfillpara2);
			this.getFormHM().put("htmlbody", htmlbody.toString());
			this.getFormHM().put("htmlbody2", htmlbody2.toString());
			this.getFormHM().put("paracopy", paracopy);
			this.getFormHM().put("paracopy2", paracopy2);
			this.getFormHM().put("isAllSub", ab.isAllFlag(unitCode, cycle_id, "1"));
			this.getFormHM().put("isAllEdit", ab.isAllFlag(unitCode, cycle_id,"0"));
			this.getFormHM().put("isAllSub_child",ab.isAllSub_child(unitCode,cycle_id));
			this.getFormHM().put("isUnderUnit", ab.isUnderUnit(unitCode));
			this.getFormHM().put("selfUnitcode",ab.getSelfUnitCode());
			this.getFormHM().put("isCollectUnit", ab.isCollectUnit(unitCode));
			this.getFormHM().put("isTopUnit", ab.isTopUnit(unitCode));
			this.getFormHM().put("unitName",unitName);
			this.getFormHM().put("actuarialReportStatusList",actuarialReportStatusList);
			this.getFormHM().put("unitCode", unitCode);
			this.getFormHM().put("cycleList",cycleList);
			this.getFormHM().put("cycle_id",cycle_id);
			this.getFormHM().put("cycleStatus", ab.getCycleStatus(cycle_id));
			this.getFormHM().put("tableHtml", tableHtml);
			this.getFormHM().put("rootUnit", ab.isRootUnit(this.getUserView().getUserName()));
			this.getFormHM().put("kmethod", ""+ab.getCycleKmethod(cycle_id));
			if(ab.getCycleKmethod(cycle_id)==1){
				this.getFormHM().put("u02_3flag",ab.validateReportu02_3Fill(unitCode, cycle_id));	
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	public int  isBasicUnit()
	{
		int flag=0;  // 0没有负责单位  1：为基层单位  2：为汇总单位
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String unitcode="";
			RowSet recset=dao.search("select unitcode from operuser where lower(userName)='"+this.userView.getUserName().toLowerCase()+"'");
			if(recset.next())
				unitcode=recset.getString(1);
			
			if(unitcode!=null&&unitcode.trim().length()>0)
			{
				recset=dao.search("select count(*) from tt_organization where parentid='"+unitcode+"'");
				int count=0;
				if(recset.next())
				{
					count=recset.getInt(1);
				}
				if(count==0)
					flag=1;
				else
					flag=2;
			}
			if(recset!=null)
				recset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	

}
