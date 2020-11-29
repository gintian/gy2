package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
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

public class UpdateReportU02ListTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm=this.getFormHM();		
		ArrayList list=(ArrayList)hm.get("data_table_record");	
		ArrayList volist=new ArrayList();
		DbNameBo bo=new DbNameBo(this.getFrameconn());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			EditReport editReport=new EditReport();
			String value="";
			String viewvalue="";
			String id="";
			String report_id="";
			String unitcode="";
			String u0200="";
			HashMap u0201u0203map = null;
			for(int  r=0;r<list.size();r++)
			{
				RecordVo vo=(RecordVo)list.get(r);
				if(u0201u0203map==null){
					u0201u0203map = new HashMap();
					u0201u0203map = getU0201u0203map(vo.getString("escope"),vo.getString("id"),vo.getString("unitcode"),dao);
				}
				if(vo.getString("editflag")!=null&& "0".equalsIgnoreCase(vo.getString("editflag")))
					throw new GeneralException("修改数据为不可编辑状态,保存数据失败！");
				
			 		if(bo.doVerify(vo.getString("u0201").trim())=='n')
			 			throw new GeneralException(vo.getString("u0203")+"身份证号码填写错误，更新数据失败！");
			 		
				LazyDynaBean bean=getBean(vo.getString("u0200"),vo.getString("id"),vo.getString("unitcode"),vo.getString("escope"),dao);
			
				String escope=vo.getString("escope").trim();	
				String U0207=(String)bean.get("u0207")==null?"":(String)bean.get("u0207");
				String name=bean.get("u0203")==null?"":(String)bean.get("u0203");
				report_id="U02_"+escope;
				id=vo.getString("id");
				u0200=vo.getString("u0200");
				unitcode=vo.getString("unitcode");
				String remark = vo.getString("u0239");
				if(!(bean.get("u0201")+name).equals(vo.getString("u0201").trim()+vo.getString("u0203").trim())){
					if(u0201u0203map!=null&&u0201u0203map.get(vo.getString("u0201").trim()+vo.getString("u0203").trim())!=null)
			 			throw new GeneralException(vo.getString("u0203")+"身份证号码重复，更新数据失败！");
				}
			    ArrayList fieldlist=editReport.getU02FieldList(this.getFrameconn(),"U02_"+escope,false);
			    LazyDynaBean beanRule=editReport.getUpdownRuleBean(this.getFrameconn(), fieldlist, "U02_"+escope, U0207);
			    for(int i=0;i<fieldlist.size();i++) 
				{
				 	FieldItem field=(FieldItem)fieldlist.get(i); 	
				 	value="";
				 	viewvalue="";
				 	String itemid=field.getItemid();
				 	
				 	if("U0200".equalsIgnoreCase(field.getItemid()))
				 		continue;
				 	value=vo.getString(field.getItemid());
				 	if(value==null||value.length()<=0)
				 		continue;
				 	if("N".equalsIgnoreCase(field.getItemtype()))
				 	{
				 		String rule_u=(String)beanRule.get(itemid+"_u");
				 		if(rule_u!=null&&rule_u.length()>0)
				 		{
				 			float rule_f=Float.parseFloat(rule_u);
				 			float value_f=Float.parseFloat(value);
				 			if(rule_f<value_f){
				 				if("".equals(remark)||remark.length()<2)
				 				throw new GeneralException(name+":"+field.getItemdesc()+"输入数据违反上下限规则,规则为不大于"+rule_u+"，数据偏高,修改数据失败,若福利水平核实无误，请在备注栏 加以说明，方可保存并上报数据！");
				 			}
				 		
				 		}
				 	}else if("D".equalsIgnoreCase(field.getItemtype()))//大于等于下限，小于等于上限
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
				 				if("".equals(remark)||remark.length()<2)
				 				throw new GeneralException(name+":"+field.getItemdesc()+"输入数据违反上限规则,规则为不大于"+rule_u+"，日期偏高,修改数据失败,请添加相关备注信息！");
				 			}
				 		}
				 		if(rule_d!=null&&rule_d.length()>0)
				 		{
				 			rule_d=rule_d.replace(".", "-");
				 			Date rule_d_D=DateUtils.getDate(rule_d,"yyyy-MM-dd");
				 			Date vlue_D=DateUtils.getDate(value,"yyyy-MM-dd");
				 			if(utils.getPartMinute(vlue_D, rule_d_D)>0)
				 			{
				 				if("".equals(remark)||remark.length()<2)
				 				throw new GeneralException(name+":"+field.getItemdesc()+"输入数据违反下限规则,规则为不小于"+rule_d+"，日期偏低,修改数据失败,请添加相关备注信息！");
				 			}
				 		}
				 	}	
				}
			    Date age=vo.getDate("u0204");
				String sex=vo.getString("u0205");
				//if((remark.equals("")||remark.length()<2)&&report_id.equals("U02_3"))
				//{
				//	if(!editReport.getCheckAgeLegal(this.getFrameconn(),id,report_id,age,sex))
				//	{
				//		throw new GeneralException("新增数据失败! "+vo.getString("u0203")+",该内退人年龄不符合规定，请添加相关备注信息！");
				//	}
				//}
				
				
				if(vo!=null)
			    volist.add(vo);
			}
	  try{
			 DbWizard dbWizard = new DbWizard(this.getFrameconn());
			    if(!dbWizard.isExistTable("t#_u022",false))
				{
			    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			    	dao.update("create table t#_u022 as select * from u02 where 1=2");
			    	else
			    	dao.update("select * into t#_u022 from u02 where 1=2");
			    	
			    	Table table = new Table("t#_u022");
			    	table.addField(getField("username", "A", 30, false));	
			    	if(table.size()>0)
			    		dbWizard.addColumns(table);
				}else{
					String delsql = "delete from t#_u022 where username="+"'"+this.userView.getUserName()+"'";
					dao.update(delsql);
				}
      
            ArrayList templist = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
				RecordVo revo = new RecordVo("t#_u022");
				RecordVo voi = (RecordVo)list.get(i);
				for (int j = 0; j < this.getUItemList().size(); j++) {
					FieldItem field=(FieldItem)this.getUItemList().get(j);
					String name=field.getItemid();
					String fieldvalue=field.getValue();
					if("N".equalsIgnoreCase(field.getItemtype()))
					{
						if(field.getDecimalwidth()>0)
						{
							fieldvalue=PubFunc.round(fieldvalue,field.getDecimalwidth());
							revo.setDouble(name, voi.getDouble(name));
						}else
						{
							revo.setInt(name, voi.getInt(name));
						}
					}else if("D".equalsIgnoreCase(field.getItemtype())){
						revo.setDate(name, voi.getDate(name));
					}
					else
					{
						revo.setString(name, voi.getString(name));
					}
					
				}
			    revo.setString("username", this.userView.getUserName());
			    revo.setString("id", voi.getString("id"));
			    revo.setString("unitcode", voi.getString("unitcode"));
			    revo.setString("editflag", voi.getString("editflag"));
			    dao.addValueObject(revo);
			    templist.add(revo);
			}
           // dao.addValueObject(templist);
			//通过公式校验
			String  infoStr = getShInfo(" username="+"'"+this.getUserView().getUserName()+"'");
			if(infoStr.length()>0){
				throw new GeneralException(infoStr);
			}
		} catch (Exception e) {
		  throw GeneralExceptionHandler.Handle(e); 
	  }	
			
		try
		{
			dao.updateValueObject(volist);
//			String sql="delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id='"+id+"' and report_id='"+report_id+"'";
//			dao.delete(sql, new ArrayList());
//			sql="insert into tt_calculation_ctrl(unitcode,id,report_id,flag)values('"+unitcode+"','"+id+"','"+report_id+"','0')";
//	        dao.insert(sql, new ArrayList());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

    }
    private LazyDynaBean getBean(String u0200,String id,String unitcode,String escope,ContentDAO dao)
    {
    	String sql="select escope,u0207,u0203,u0201 from u02 where u0200='"+u0200+"' and id='"+id+"' and unitcode='"+unitcode+"' and escope='"+escope+"'";
    	LazyDynaBean bean=new LazyDynaBean();
    	try {
			RowSet rs=dao.search(sql);
			if(rs.next())
			{
				bean.set("escope", rs.getString("escope")==null?"":rs.getString("escope"));
				bean.set("u0207", rs.getString("u0207")==null?"":rs.getString("u0207"));
				bean.set("u0203", rs.getString("u0203")==null?"":rs.getString("u0203"));
				bean.set("u0201", rs.getString("u0201")==null?"":rs.getString("u0201"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return bean;
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
    /**
     * Description: 公式验证 返回错误提示信息（无则返回""）
     * @Version1.0 
     * Nov 26, 2012 8:46:23 PM Jianghe created
     * @param list
     * @return
     * @throws GeneralException
     */
    public String getShInfo(String conWhere) throws GeneralException
	{
    	String message="";
		HashMap returnMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ResultSet rs = null;
		//String conWhere = "";
		//for (int i = 0; i < list.size(); i++) {
		//	RecordVo vo=(RecordVo)list.get(i);
		//	if(vo!=null){
		//		conWhere += " (u0200='"+vo.getString("u0200")+"' and id='"+vo.getString("id")+"') "+" or";
		//	}
		//	
		//}
		//if(conWhere.length()>0)
		//	conWhere = conWhere.substring(0,conWhere.length()-2);
		
		try
		{
			ArrayList formulaList = this.getSpFormulaList();
			ArrayList varlist =this.getUItemList();
			YksjParser yp=null;
			yp = new YksjParser(this.userView ,varlist,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
			yp.setCon(this.getFrameconn());
			for(int i=0;i<formulaList.size();i++)
			{
				String message1 = "";
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
				sql.append(" select * from t#_u022 where ");
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
					
					
				}	
				if(strName.length()>0){
						strName = strName.substring(0,strName.length()-1);
						message1= strName+"\n"+information+"\n";
						//return message;
						if(message.length()>0)
						    message+="\n"+message1;
						else
							message+=message1;
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
    /**
	 * 新建指标计算公式临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("F".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key)
		    obj.setNullable(false);
		obj.setKeyable(key);	
		return obj;
    }
}
