package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
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
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 编辑报表
 * @author Owner
 *
 */
public class EditReportListTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
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
				     if(columns.toUpperCase().indexOf("ESCOPE")==-1||columns.toUpperCase().indexOf("UNITCODE")==-1){//判断主键escope是否存在
				    	  
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
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		if(hm.get("cycleparm")!=null);
		this.getFormHM().put("cycleparm", hm.get("cycleparm"));
        String sql="select * from tt_cycle where Status='04'";
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String kmethod="";
        String cycle_id="";
        try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				kmethod=this.frowset.getString("Kmethod");
				cycle_id=this.frowset.getString("id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(kmethod==null||kmethod.length()<=0)
		  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.edit_report.nocycle"),"",""));
		String unitcode=getUnitcode(this.userView.getUserName());
		if(unitcode==null|| "".equals(unitcode))
			  throw new GeneralException("当前操作人员没有负责的部门!");	
		
		
		
		ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
		this.getFormHM().put("isCollectUnit",ab.isCollectUnit(unitcode));
		
		
		sql="select flag,Report_id from tt_calculation_ctrl where id='"+cycle_id+"' and Unitcode='"+unitcode+"'";
	    ArrayList list=new ArrayList();
		try {
			this.frowset=dao.search(sql);
			LazyDynaBean bean=new LazyDynaBean();			
			HashMap map=new HashMap();
			while(this.frowset.next())
			{
				bean=new LazyDynaBean();
				String Report_id=this.frowset.getString("Report_id");
				if(Report_id==null||Report_id.length()<=0)
					continue;
				bean.set("flag",this.frowset.getString("flag"));
				bean.set("Report_id", this.frowset.getString("Report_id"));
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);
				bean.set("report_name", getReportName(this.frowset.getString("Report_id")));
				map.put(Report_id.toUpperCase(), bean);
			}
			bean=(LazyDynaBean)map.get("U01");
			if(bean==null)
			{
				bean=new LazyDynaBean();
				bean.set("flag", "-1");
				bean.set("Report_id", "U01");
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);
				bean.set("report_name", getReportName("U01"));
			}
			list.add(bean);
			bean=(LazyDynaBean)map.get("U02_1");
			if(bean==null)
			{
				bean=new LazyDynaBean();
				bean.set("flag", "-1");
				bean.set("Report_id", "U02_1");
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);
				bean.set("report_name", getReportName("U02_1"));
			}
			list.add(bean);
			bean=(LazyDynaBean)map.get("U02_2");
			if(bean==null)
			{
				bean=new LazyDynaBean();
				bean.set("flag", "-1");
				bean.set("Report_id", "U02_2");
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);
				bean.set("report_name", getReportName("U02_2"));
			}
			list.add(bean);
			bean=(LazyDynaBean)map.get("U02_3");
			if(bean==null)
			{
				bean=new LazyDynaBean();
				bean.set("flag", "-1");
				bean.set("Report_id", "U02_3");
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);
				bean.set("report_name", getReportName("U02_3"));
			}
			list.add(bean);
			bean=(LazyDynaBean)map.get("U02_4");
			if(bean==null)
			{
				bean=new LazyDynaBean();
				bean.set("flag", "-1");
				bean.set("Report_id", "U02_4");
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);			
				bean.set("report_name", getReportName("U02_4"));
			}
			list.add(bean);
			bean=(LazyDynaBean)map.get("U03");
			if(bean==null)
			{
				bean=new LazyDynaBean();
				bean.set("flag", "-1");
				bean.set("Report_id", "U03");
				bean.set("unitcode", unitcode);
				bean.set("id", cycle_id);
				bean.set("report_name", getReportName("U03"));
			}
			list.add(bean);	
			if("0".equals(kmethod))//完整精算评估法
			{
				//完整精算评估时点一般为每年12月31日，需要各单位填报表1、表2-1、2-2、2-3、2-4、表3的全部信息
				//，并由系统给出表3和表5的校验结果和警告信息
				bean=(LazyDynaBean)map.get("U05");
				if(bean==null)
				{
					bean=new LazyDynaBean();
					bean.set("flag", "-1");
					bean.set("Report_id", "U05");
					bean.set("unitcode", unitcode);
					bean.set("id", cycle_id);
					bean.set("report_name", getReportName("U05"));
				}
				list.add(bean);	
			}else//向前滚动法
			{
				//向前滚动法一般在年中时点（例如6月30日），需要各单位填报表1、表2-1、2-2、2-3、2-4中的新增人员、
				//表3的第一张表，以及表4，系统不需给出表3和表5的校验结果和警告信息,收集最近新增人员
				bean=(LazyDynaBean)map.get("U04");
				if(bean==null)
				{
					bean=new LazyDynaBean();
					bean.set("flag", "-1");
					bean.set("Report_id", "U04");
					bean.set("unitcode", unitcode);
					bean.set("id", cycle_id);
					bean.set("report_name", getReportName("U04"));
				}
				list.add(bean);	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				.search("select "+itemid+" from u01 where id="+cycle_id+" and unitcode='"+unitcode+"' ");
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
	
//			email = _vo.getString("email");
//			telephone = _vo.getString("telephone");
//			linkman = _vo.getString("linkman");
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
				ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
				ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
				ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
				String sqlstr =" select * from tt_organization where unitcode='"+unitcode+"' "+ext_sql.toString();
				this.frowset = dao.search(sqlstr);
				if(this.frowset.next()){
					this.getFormHM().put("cancelunit", "0");	
				}else{
					this.getFormHM().put("cancelunit", "1");
				}
		}
		catch(Exception ee)
		{
		ee.printStackTrace();
		}
		//System.out.println(htmlbody.toString());
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
		this.getFormHM().put("isAllSub",ab.isAllFlag(unitcode, cycle_id, "1"));
		this.getFormHM().put("id",cycle_id);
		this.getFormHM().put("unitcode",unitcode);
		this.getFormHM().put("kmethod", kmethod);
		this.getFormHM().put("repotlist", list);
		if(ab.getCycleKmethod(cycle_id)==1){
			this.getFormHM().put("u02_3flag",ab.validateReportu02_3Fill(unitcode, cycle_id));	
		} 
		
	}
	public String getUnitcode(String userName)
	{
		String unitcode="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet recset=null;
		try
		{
			recset=dao.search("select unitcode from operuser where userName='"+userName+"'");
			if(recset.next())
				unitcode=recset.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		return unitcode;
	}
    private String getReportName(String reportid)
    {
    	if(reportid==null||reportid.length()<=0)
    		return "";
    	if("U01".equalsIgnoreCase(reportid))
    		return "表1-特别事项";
    	else if("U02_1".equalsIgnoreCase(reportid))
    		return "表2-1离休人员";
    	else if("U02_2".equalsIgnoreCase(reportid))
    		return "表2-2退休人员";
    	else if("U02_3".equalsIgnoreCase(reportid))
    		return "表2-3内退人员";
    	else if("U02_4".equalsIgnoreCase(reportid))
    		return "表2-4遗属";
    	else if("U03".equalsIgnoreCase(reportid))
    		return "表3财务信息";
    	else if("U04".equalsIgnoreCase(reportid))
    		return "表4人员统计表";
    	else if("U05".equalsIgnoreCase(reportid))
    		return "表5人员变动及人均福利对照表";
    	return "";
    }

}
