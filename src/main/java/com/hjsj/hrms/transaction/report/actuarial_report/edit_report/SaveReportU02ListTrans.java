package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SaveReportU02ListTrans extends IBusiness {


	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");
		String id=(String)this.getFormHM().get("id");
		String Report_id=(String)this.getFormHM().get("report_id");	  
		String oper=(String)this.getFormHM().get("oper");	  
		String kmethod=(String)this.getFormHM().get("kmethod");
		String stateflag = (String)this.getFormHM().get("flag");
	//	String olditemdesc=(String)this.getFormHM().get("olditemdesc");
		String escope=Report_id.split("_")[1];
		RecordVo vo=new RecordVo("U02");
		DbNameBo bo=new DbNameBo(this.getFrameconn());
		/*ArrayList editlistU02=(ArrayList)this.getFormHM().get("editlistU02");
		for(int i=0;i<editlistU02.size();i++)
		{
			FieldItem field=(FieldItem)editlistU02.get(i);
			String name=field.getItemid();
			String value=field.getValue();
			if(value==null||value.length()<=0)
				continue;
			if(field.getItemtype().equalsIgnoreCase("N"))
			{
				if(field.getDecimalwidth()>0)
				{
					value=PubFunc.round(value,field.getDecimalwidth());
					vo.setString(name, value);
				}else
				{
					vo.setInt(name, Integer.parseInt(value));
				}
			}else
			{
				vo.setString(name, value);
			}
		}*/
		EditReport editReport=new EditReport();
    	ArrayList fieldlist=editReport.getU02FieldList(this.getFrameconn(),Report_id,false);
    	String value="";
    	String viewvalue="";
    	String U0207="";
//    	if(oper!=null&&!oper.equals("")&&oper.equalsIgnoreCase("saveClose")){
    		U0207 ="3";
//    	}
//    	if(kmethod!=null&&kmethod.equals("0"))
//		{
//			if(editReport.isBeforeCycle(this.getFrameconn(),id))
//			{
//				U0207="3";	 
//			}else
//			{
//				U0207="1";	
//			}
//		}else
//		{
//			U0207="3";	
//		}    	
    	for(int i=0;i<fieldlist.size();i++) {
    		FieldItem field=(FieldItem)fieldlist.get(i); 	
		 	String itemid=field.getItemid();
		 	if("u0239".equalsIgnoreCase(itemid)){
		 		vo.setString(field.getItemid(), (String)this.getFormHM().get(field.getItemid()));
		 		break;
		 	}
    		}
    	LazyDynaBean beanRule=editReport.getUpdownRuleBean(this.getFrameconn(), fieldlist, Report_id, U0207);
    	for(int i=0;i<fieldlist.size();i++) 
		{
		 	FieldItem field=(FieldItem)fieldlist.get(i); 	
		 	value="";
		 	viewvalue="";
		 	String itemid=field.getItemid();
		 	Field _field=field.cloneField();
		 	if("U0200".equalsIgnoreCase(field.getItemid()))
		 		continue;
		 	if("U0207".equalsIgnoreCase(field.getItemid()))
		 		continue;
//			if(field.getItemid().equalsIgnoreCase(olditemdesc))
//		 		continue;
		 	
		 	value=(String)this.getFormHM().get(field.getItemid());
		 	if(value==null||value.length()<=0)
		 	{
		 		if(_field.isFillable())
		 			throw new GeneralException("必填项不能为空！");
		 		continue;
		 	}
		 	if("U0201".equalsIgnoreCase(field.getItemid()))
		 	{
		 		if(bo.doVerify(value.trim())=='n')
		 			throw new GeneralException("身份证号码填写错误，新增数据失败！");
		 	}
		 	
		 	
		 	if("N".equalsIgnoreCase(field.getItemtype())&&value!=null&&value.length()>0)
		 	{
		 		String rule_u=(String)beanRule.get(itemid+"_u");
		 		if(rule_u!=null&&rule_u.length()>0)
		 		{
		 			float rule_f=Float.parseFloat(rule_u);
		 			if(rule_f<=0)
		 				continue;
		 			float value_f=Float.parseFloat(value);
		 			if(rule_f<value_f){
		 				if(vo.getString("u0239")==null||vo.getString("u0239").length()<2)
		 				throw new GeneralException(field.getItemdesc()+"输入数据违反上限规则,规则为不大于"+rule_u+"，数据偏高,新增数据失败,若福利水平核实无误，请在备注栏 加以说明，方可保存并上报数据！");
		 			}
		 			vo.setString(field.getItemid(), value);
		 		}else
		 		{
		 			vo.setString(field.getItemid(), value);
		 		}
		 	}else if("D".equalsIgnoreCase(field.getItemtype())&&value!=null&&value.length()>0)//大于等于下限，小于等于上限
		 	{
		 		String rule_u=(String)beanRule.get(itemid+"_u");
		 		String rule_d=(String)beanRule.get(itemid+"_d");
		 		value=value.replace(".", "-");
		 		KqUtilsClass utils=new KqUtilsClass();
		 		if(rule_u!=null&&rule_u.length()>0)
		 		{
		 			rule_u=rule_u.replace(".", "-");
		 			Date rule_u_D=DateUtils.getDate(rule_u,"yyyy-MM-dd");
		 			Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
		 			if(utils.getPartMinute(vlue_D, rule_u_D)<0)
		 			{
		 				if(vo.getString("u0239")==null||vo.getString("u0239").length()<2)
		 				throw new GeneralException(field.getItemdesc()+"输入数据违反上限规则,规则为不大于"+rule_u+"，日期偏高,新增数据失败,请核实信息的正确性或添加相关备注信息！");
		 			}else
		 			  vo.setDate(field.getItemid(), vlue_D);
		 		}
		 		if(rule_d!=null&&rule_d.length()>0)
		 		{
		 			rule_d=rule_d.replace(".", "-");
		 			Date rule_d_D=DateUtils.getDate(rule_d,"yyyy-MM-dd");
		 			Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
		 			if(utils.getPartMinute(vlue_D, rule_d_D)>0)
		 			{
		 				if(vo.getString("u0239")==null||vo.getString("u0239").length()<2)
		 				throw new GeneralException(field.getItemdesc()+"输入数据违反下限规则,规则为不小于"+rule_d+"，日期偏低,新增数据失败,请核实信息的正确性或添加相关备注信息！");
		 			}else
		 			  vo.setDate(field.getItemid(), vlue_D);
		 		}
		 		if((rule_d==null||rule_d.length()<=0)&&(rule_d==null||rule_d.length()<=0))
		 		{
		 			Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
		 			vo.setDate(field.getItemid(), vlue_D);
		 		}	 
		 	}else
		 	{
		 		vo.setString(field.getItemid(), value);
		 	} 	
		}
		vo.setString("id", id);
		vo.setString("unitcode", unitcode);
		if("U02_1".equalsIgnoreCase(Report_id))
		   vo.setString("escope", "1");
		else if("U02_2".equalsIgnoreCase(Report_id))
		   vo.setString("escope", "2");
		else if("U02_3".equalsIgnoreCase(Report_id))
		   vo.setString("escope", "3");
		else if("U02_4".equalsIgnoreCase(Report_id))
		   vo.setString("escope", "4");
		vo.setString("editflag", "1");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap u0201u0203map = null;
		u0201u0203map = new HashMap();
		u0201u0203map = getU0201u0203map(vo.getString("escope"),vo.getString("id"),vo.getString("unitcode"),dao);
		if(u0201u0203map!=null&&u0201u0203map.get(vo.getString("u0201").trim()+vo.getString("u0203").trim())!=null)
 			throw new GeneralException(vo.getString("u0203")+"身份证号码重复，新增数据失败！");
	    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		String insertid=idg.getId(("U02.U0200").toUpperCase());				
		vo.setString("u0200", insertid);
//		String idstr ="";
//		idstr =editReport.getId(this.getFrameconn(), escope, olditemdesc, "");
//		vo.setString(olditemdesc.toLowerCase(), idstr);
		vo.setString("u0207", U0207);

		String flag="true";
		Date age=vo.getDate("u0204");
		String sex=vo.getString("u0205");
		//if((vo.getString("u0239")==null||vo.getString("u0239").length()<2)&&Report_id.equals("U02_3"))
		//{
		//	if(!editReport.getCheckAgeLegal(this.getFrameconn(),id,Report_id,age,sex))
		//	{
		//		throw new GeneralException("新增数据失败! "+vo.getString("u0203")+",该内退人年龄不符合规定，请添加相关备注信息！");
		//	}
		//}
		
		try
		{
			
			
			if(stateflag!=null&&!"2".equals(stateflag)){
				dao.addValueObject(vo);
				//通过公式校验
			ArrayList list = new ArrayList();
			list.add(vo);
			String  infoStr = getShInfo(list);
			if(infoStr.length()>0){
				dao.deleteValueObject(vo);
				throw new GeneralException(infoStr);
			}
			dao.update("delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id="+id+" and report_id='"+Report_id+"'");
			String sql="insert into  tt_calculation_ctrl (report_id,id,flag,unitcode) values ('"+Report_id+"',"+id+",0,'"+unitcode+"')";
			dao.update(sql);
			}else{
				vo.setString("editflag", "2");
				dao.addValueObject(vo);	
			}

		}catch(Exception e)
		{
		  e.printStackTrace();
		  flag="false";
		  throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("oper",oper);
		this.getFormHM().put("flag",flag);
	}
	  private HashMap getU0201u0203map(String escope,String id,String unitcode,ContentDAO dao){
	    	HashMap map = new HashMap();
	    	if(escope!=null&&escope.length()>0&&id!=null&&id.length()>0&&unitcode!=null&&unitcode.length()>0){
	    	String sql="select escope,u0207,u0203,u0201 from u02 where   id='"+id+"' and escope="+escope+" and unitcode='"+unitcode+"'" ;
	    	try {
				RowSet rs=dao.search(sql);
				while(rs.next())
				{
					if(rs.getString("u0201")!=null&&rs.getString("u0201").length()>0&&rs.getString("u0203")!=null&&rs.getString("u0203").length()>0)
					map.put(rs.getString("u0201")+rs.getString("u0203"), "1");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	}
	    	return map;
	    
	    }
	  /**
	     * Description: 公式验证 返回错误提示信息（无则返回""）
	     * @Version1.0 
	     * Nov 26, 2012 8:46:23 PM Jianghe created
	     * @param list
	     * @return
	     * @throws GeneralException
	     */
	    public String getShInfo(ArrayList list) throws GeneralException
		{
	    	String message="";
			HashMap returnMap = new HashMap();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ResultSet rs = null;
			String conWhere = "";
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo=(RecordVo)list.get(i);
				if(vo!=null){
					conWhere += " (u0200='"+vo.getString("u0200")+"' and id='"+vo.getString("id")+"') "+" or";
				}
				
			}
			if(conWhere.length()>0)
				conWhere = conWhere.substring(0,conWhere.length()-2);
			
			try
			{
				ArrayList formulaList = this.getSpFormulaList();
				ArrayList varlist =this.getUItemList();
				YksjParser yp=null;
				yp = new YksjParser(this.userView ,varlist,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.setCon(this.getFrameconn());
				for(int i=0;i<formulaList.size();i++)
				{
					String strName = "";
					String information = "";
					StringBuffer sql = new StringBuffer();
					LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
					String formula=(String)bean.get("formula");
					String formulaname=(String)bean.get("name");
	                information=(String)bean.get("information");
					if(formula==null|| "".equals(formula))
						continue;
					yp.run(formula.trim());
					String wherestr = yp.getSQL();//公式的结果
					sql.append(" select * from u02 where ");
					if(wherestr.trim().length()>0)
						sql.append("("+wherestr+")");
					if(wherestr.trim().length()>0)
						sql.append(" and ");
					if(conWhere.trim().length()>0)
					sql.append(conWhere);
					rs=dao.search(sql.toString());
					while(rs.next())
					{
						strName += rs.getString("u0203")+",";
						if(strName.length()>0){
						strName = strName.substring(0,strName.length()-1);
						message= message + strName+"\n"+information+"\n";
						return message;
					    }
					}	
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return message;
		}
	    public ArrayList getUItemList()
		{
			ArrayList list = new ArrayList();
			try
			{
				ArrayList fielditemlist = new ArrayList();

				fielditemlist = DataDictionary.getFieldList("U02",
						Constant.USED_FIELD_SET);
				for (int i = 0; i < fielditemlist.size(); i++) {
					if (fielditemlist.get(i) == null)
						continue;
					FieldItem fielditem = (FieldItem) fielditemlist.get(i);
					list.add(fielditem);
				}
				FieldItem item = new FieldItem();
				item.setItemid("escope");
				item.setItemdesc("人员范围");
				item.setItemtype("A");
				item.setCodesetid("61");
				list.add(item);
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
			
		}
	    public ArrayList getSpFormulaList()
		{
			ArrayList list = new ArrayList();
			try
			{
				StringBuffer sql = new StringBuffer();
				sql.append("select chkid,name,validflag,formula,information  from hrpchkformula where 1=1  ");
					sql.append("and flag=3  ");
				sql.append(" order by seq");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RowSet rs = null;
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("chkid",rs.getString("chkid"));
					bean.set("name",rs.getString("name"));
					bean.set("validflag", rs.getString("validflag"));
					bean.set("information", rs.getString("information"));
					bean.set("formula", Sql_switcher.readMemo(rs,"formula"));
					//bean.set("formula", )
					list.add(bean);
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
}
