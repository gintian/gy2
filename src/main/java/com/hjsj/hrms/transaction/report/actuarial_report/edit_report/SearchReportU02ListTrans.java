package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchReportU02ListTrans  extends IBusiness {


	public void execute() throws GeneralException {
		
			String unitcode=(String)this.getFormHM().get("unitcode");
			String id=(String)this.getFormHM().get("id");
			RecordVo vo = new RecordVo("tt_cycle");
			vo.setInt("id", Integer.parseInt(id));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				vo=dao.findByPrimaryKey(vo);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			String idstatus = vo.getString("status");
			int kmethod = vo.getInt("kmethod");
			SimpleDateFormat mf=new SimpleDateFormat("yyyy");
			String theyear =mf.format(vo.getDate("bos_date"));
			String flag=(String)this.getFormHM().get("flag");
			String Report_id=(String)this.getFormHM().get("report_id");
			Report_id = Report_id.trim();
			this.getFormHM().put("report_id",Report_id.toUpperCase());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");	
			String from_model = (String)hm.get("from_model");
			String subquerysql = hm.get("subquerysql")==null?"":(String)hm.get("subquerysql");
			if("".equals(subquerysql)){
				this.getFormHM().remove("subquerysql");
			}
			subquerysql =SafeCode.decode(subquerysql);
			hm.remove("subquerysql");
			this.getFormHM().remove("like");
			this.getFormHM().put("subquerysql", subquerysql);
			ArrayList fieldlist = DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
			TargetsortBo targetsortBo =new TargetsortBo(this.getFrameconn());
			HashMap map=targetsortBo.getTargetsortMap(this.getFrameconn());
			String fields="";
			String escope="";
			if("U02_1".equalsIgnoreCase(Report_id))
			{
				fields=(String)map.get("1");
				escope="1";
				if(fields==null||fields.length()<=0)
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_1")+"没有定义指标","",""));
			}else if("U02_2".equalsIgnoreCase(Report_id))
			{
				fields=(String)map.get("2");
				escope="2";
				if(fields==null||fields.length()<=0)
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_2")+"没有定义指标","",""));
			}else if("U02_3".equalsIgnoreCase(Report_id))
			{
				fields=(String)map.get("3");
				escope="3";
				if(fields==null||fields.length()<=0)
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_3")+"没有定义指标","",""));
			}else if("U02_4".equalsIgnoreCase(Report_id))
			{
				fields=(String)map.get("4");
				escope="4";
				if(fields==null||fields.length()<=0)
					 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_4")+"没有定义指标","",""));
			}	
			try
			{
			fields=fields+",";
			ArrayList list=new ArrayList();		
			StringBuffer cloums=new StringBuffer();
			EditReport editReport=new EditReport();
			ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem itemfield=(FieldItem)fieldlist.get(i);			
				String itemid=itemfield.getItemid();
				Field field=(Field)itemfield.cloneField();				
				field.setLabel(editReport.reChangeLine(field.getLabel()));
				if(fields.indexOf(itemid+",")!=-1)
				{
					cloums.append(itemid+",");
					if(field.getDataType()==10||field.getDatatype()==11||field.getDatatype()==12)
						field.setFormat("yyyy.MM");
					if("u0207".equalsIgnoreCase(itemid))
						field.setReadonly(true);
					if("u0243".equalsIgnoreCase(itemid))
						field.setReadonly(true);
					list.add(field);
				}
			}		
			Field field=new Field("editflag");
			field.setVisible(false);
			list.add(field);
			field=new Field("id");
			field.setVisible(false);
			list.add(field);
			field=new Field("escope");
			field.setVisible(false);
			list.add(field);
			field=new Field("unitname");
			if("1".equals(ab.isCollectUnit(unitcode))){
				field.setLabel("单位名称");
				field.setVisible(true);	
				list.add(0, field);
			}
			else{
			field.setVisible(false);
			list.add(field);
			}
			field=new Field("unitcode");
			field.setVisible(false);
			list.add(field);
			if(cloums.length()>0)
				cloums.setLength(cloums.length()-1);
			StringBuffer sql=new StringBuffer();
		   String txt_sql = ab.throwunit(id, "");
			sql.append("select "+cloums.toString()+",escope,(select unitname from tt_organization where unitcode=u02.unitcode )unitname,unitcode,id,editflag from U02 u02 ");
			if("1".equals(ab.isCollectUnit(unitcode))) //汇总单位 -dengcan
				sql.append(" where unitcode like '"+unitcode+"%' and unitcode in (select unitcode from tt_organization where 1=1 "+txt_sql+") and id='"+id+"' and escope='"+escope+"'");
			else
			{
				sql.append(" where unitcode='"+unitcode+"' and id='"+id+"' and escope='"+escope+"'");
			}
			if(opt!=null&& "x".equals(opt)){
				sql.append(" and (editflag=2 or editflag=3)");
				
			}else{
				this.getFormHM().put("opt2", opt);
			}
			if(subquerysql!=null&&subquerysql.length()>0)
				sql.append(subquerysql);
			sql.append(" order by U0200 desc");
			if("1".equals(ab.isRootUnit(this.getUserView().getUserName()))){
				this.getFormHM().put("rootUnit", "1");
			}else{
				this.getFormHM().put("rootUnit", "0");
			}
			
				this.getFormHM().put("flagSub",ab.isSub(Report_id, ab.getSelfUnitCode(), id, "1"));	
			
			this.getFormHM().put("sql", sql.toString());
			//System.out.println(sql.toString());
			this.getFormHM().put("opt", opt);
			this.getFormHM().put("selfUnitcode", ab.getSelfUnitCode());
			this.getFormHM().put("from_model", from_model);
			this.getFormHM().put("idstatus", idstatus);
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("fieldlistU02", list);
			this.getFormHM().put("theyear", theyear);
			this.getFormHM().put("kmethod", ""+kmethod);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    
}
