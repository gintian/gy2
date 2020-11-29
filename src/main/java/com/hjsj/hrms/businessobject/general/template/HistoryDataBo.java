package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryDataBo {
	Connection con;
	String salaryid;
	UserView userview;
	String tabid;
	private String hmuster_sql="";    //当前模板处理人员的sql
	private ArrayList templateSetList=new ArrayList();   //模板指标集
	private RecordVo table_vo=null;
	/**业务类型
	 * 对人员调入的业务单独处理
	 * =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3调动,
	 * =10其它不作特殊处理的业务
	 * 如果目标库未指定的话，则按源库进行处理
	 */
	private int operationtype=10; 
	private TemplateTableBo bo=null;
	private int Infor_type=1;	//1人员，2单位，3职务
	public HistoryDataBo(Connection con)
	{
		this.con=con;
	}
	public HistoryDataBo(Connection con,String salaryid)
	{
		this.con=con;
		this.salaryid=salaryid;
	}
	public HistoryDataBo()
	{
		
	}
	public HistoryDataBo(Connection con,UserView u)
	{
		this.con=con;
		this.userview=u;
		try
		{
			this.userview=userview;
			table_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.con);
			String operationcode=this.table_vo.getString("operationcode");
			this.operationtype=findOperationType(operationcode);
			
			bo=new TemplateTableBo(con,table_vo,userview);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HistoryDataBo(String tabid,Connection con,UserView userview){
		this.con=con;
		this.userview=userview;
		this.tabid = tabid;
		try
		{
			this.userview=userview;
			table_vo=TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.con);
			String operationcode=this.table_vo.getString("operationcode");
			this.operationtype=findOperationType(operationcode);
			
			bo=new TemplateTableBo(con,table_vo,userview);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	

   /**
    * 取得归档表中的所有字段
    */
   public ArrayList getAllCells(ArrayList cellList)
   {
       HashMap map = new HashMap();
   	LazyDynaBean cellBean=null;
	LazyDynaBean headBean=null;
//	StringBuffer strsql = new StringBuffer();
//	strsql.append(" select id,tabid,a0100,basepre,chguser,lasttime,appprocess,from_tabid,from_id,operationcode,content_pdf,ext   ");
	ArrayList list = new ArrayList();
	list.add(getBean("操作","content_pdf","","0","0","0","0","0","0"));
	list.add(getBean("档案号","id","N","0","0","0","0","0","0"));
	if(this.bo.getInfor_type()==1) {
        list.add(getBean("人员库","basepre","A","0","0","0","0","0","0"));
    }
	list.add(getBean("提交用户","chguser","A","0","0","0","0","0","0"));
	list.add(getBean("提交时间","lasttime","D","0","0","0","0","0","0"));
	list.add(getBean("消息来源归档号","from_id","N","0","0","0","0","0","0"));
	if("1".equals(this.bo.getArchflag()))
	{
		list.add(getBean("附件","affixfile","A","0","0","0","0","0","0"));
	}
//	list.add(getBean("审批流程","appprocess","","0","0","0","0","0","0"));
	
	String strtemp = ",content_pdf,id,basepre,chguser,lasttime,from_id,appprocess,";//历史记录前缀字段
	DbWizard dbw=new DbWizard(this.con);
	for(int i=0;i<cellList.size();i++)
	{
		
		cellBean=(LazyDynaBean)cellList.get(i);
		headBean=new LazyDynaBean();
		headBean.set("field_type",(String)cellBean.get("field_type"));
		headBean.set("field_hz",(String)cellBean.get("field_hz"));
		headBean.set("hz",(String)cellBean.get("hz"));
		headBean.set("codeid",(String)cellBean.get("codeid"));
		headBean.set("setname",(String)cellBean.get("setname"));
		headBean.set("chgstate",(String)cellBean.get("chgstate"));
		headBean.set("hismode",(String)cellBean.get("hismode"));
		headBean.set("subflag",(String)cellBean.get("subflag"));
		headBean.set("isvar",(String)cellBean.get("isvar"));
		headBean.set("pageid",(String)cellBean.get("pageid"));
		headBean.set("gridno",(String)cellBean.get("gridno"));
		headBean.set("formula",cellBean.get("formula")==null?"":(String)cellBean.get("formula"));
		headBean.set("disformat",cellBean.get("disformat")==null?"0":cellBean.get("disformat"));
		headBean.set("sub_domain",cellBean.get("sub_domain")==null?"":(String)cellBean.get("sub_domain"));
		headBean.set("sub_domain_id",cellBean.get("sub_domain_id")==null?"":(String)cellBean.get("sub_domain_id"));
		String chgstate=(String)cellBean.get("chgstate");   // 1:变化前  2：变化后
		String isvar=(String)cellBean.get("isvar");
		String _fieldName=((String)cellBean.get("field_name")).toLowerCase();
		if(this.bo!=null&&(this.bo.getInfor_type()==2||this.bo.getInfor_type()==3)){//如果是单位或岗位
			if("1".equals(cellBean.get("subflag"))){//如果是子集
				if("codesetid".equalsIgnoreCase(cellBean.get("setname").toString().trim())|| "codeitemdesc".equalsIgnoreCase(cellBean.get("setname").toString().trim())|| "corcode".equalsIgnoreCase(cellBean.get("setname").toString().trim())|| "parentid".equalsIgnoreCase(cellBean.get("setname").toString().trim())|| "start_date".equalsIgnoreCase(cellBean.get("setname").toString().trim()))
				{
					
				}else{
					if(!this.userview.isSuper_admin()&& "0".equalsIgnoreCase(this.userview.analyseTablePriv(cellBean.get("setname").toString().trim()))&& "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                        continue;
                    }
				}
			}else{//如果是字段
				if("codesetid".equalsIgnoreCase(cellBean.get("field_name").toString().trim())|| "codeitemdesc".equalsIgnoreCase(cellBean.get("field_name").toString().trim())|| "corcode".equalsIgnoreCase(cellBean.get("field_name").toString().trim())|| "parentid".equalsIgnoreCase(cellBean.get("field_name").toString().trim())|| "start_date".equalsIgnoreCase(cellBean.get("field_name").toString().trim()))
				{
					
				}else{
					if(!this.userview.isSuper_admin()&& "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim()))&& "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                        continue;
                    }
				}
			}	
			
		}else{//如果是人员
			if("1".equals(cellBean.get("subflag"))){
				if(!this.userview.isSuper_admin()&& "0".equalsIgnoreCase(this.userview.analyseTablePriv(cellBean.get("setname").toString().trim()))&& "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                    continue;
                }
			}else{
				if(!this.userview.isSuper_admin()&& "0".equalsIgnoreCase(this.userview.analyseFieldPriv(cellBean.get("field_name").toString().trim()))&& "0".equals(this.bo.getUnrestrictedMenuPriv_Input())) {
                    continue;
                }
			}
		}
		if(strtemp.indexOf(","+_fieldName+",")!=-1){//确保不再把前缀字段加上
			continue;
		}
		//判断库中是否存在字段
		String param ="";
		if("0".equals(isvar)){
			if("1".equals(cellBean.get("subflag"))){//如果是子集
				if(cellBean.get("sub_domain_id")!=null&&cellBean.get("sub_domain_id").toString().trim().length()>0) {
                    param="t_"+cellBean.get("setname").toString().trim()+"_"+cellBean.get("sub_domain_id").toString().trim()+"_"+chgstate;
                } else {
                    param="t_"+cellBean.get("setname").toString().trim()+"_"+chgstate;
                }
			}else{//如果是字段
				if(cellBean.get("sub_domain_id")!=null&&cellBean.get("sub_domain_id").toString().trim().length()>0) {
                    param=_fieldName+"_"+cellBean.get("sub_domain_id").toString().trim()+"_"+chgstate;
                } else {
                    param=_fieldName+"_"+chgstate;
                }
			}
			if(!dbw.isExistField("template_archive",param,false)){
				continue;
			}
		}
	
		if("0".equals(isvar)){
			if(cellBean.get("sub_domain_id")!=null&&cellBean.get("sub_domain_id").toString().trim().length()>0) {
                _fieldName+="_"+cellBean.get("sub_domain_id").toString().trim()+"_"+chgstate;
            } else {
                _fieldName+="_"+chgstate;
            }
		}
		headBean.set("field_name", _fieldName);
		if("2".equals(chgstate))
		{
			if("1".equals(cellBean.get("subflag")))
			{
				headBean.set("hz",("拟["+(String)cellBean.get("hz")+"]").replaceAll("\\{","").replaceAll("\\}",""));
				headBean.set("_hz",("拟"+(String)cellBean.get("hz")).replaceAll("\\{","").replaceAll("\\}",""));
			}
			else
			{
				headBean.set("hz","拟["+(String)cellBean.get("hz")+"]");
				headBean.set("_hz","拟"+(String)cellBean.get("hz"));
			}
		}else{
			if("1".equals(cellBean.get("subflag")))
			{
				headBean.set("hz",((String)cellBean.get("hz")).replaceAll("\\{","").replaceAll("\\}",""));
				headBean.set("_hz",((String)cellBean.get("hz")).replaceAll("\\{","").replaceAll("\\}",""));
			}
		}
		
		
		list.add(headBean);
	}

       return list;
   }
   /**
    * 取得归档表中的部分字段
    */
   public ArrayList getpartCells()
   {
	ArrayList list = new ArrayList();
	list.add(getBean("操作","content_pdf","","0","0","0","0","0","0"));
	list.add(getBean("档案号","id","N","0","0","0","0","0","0"));
	list.add(getBean("人员库","basepre","A","0","0","0","0","0","0"));
	list.add(getBean("提交用户","chguser","A","0","0","0","0","0","0"));
	list.add(getBean("提交时间","lasttime","D","0","0","0","0","0","0"));
	list.add(getBean("消息来源归档号","from_id","N","0","0","0","0","0","0"));
//	list.add(getBean("审批流程","appprocess","","0","0","0","0","0","0"));
	
       return list;
   } 
	public LazyDynaBean getBean(String hz,String fieldname,String fieldType,String codeid,String setname,String chgstate,String hismode,String subflag,String isvar)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("hz",hz);
		abean.set("field_name",fieldname);
		abean.set("field_type",fieldType);
		abean.set("field_hz",hz);
		abean.set("codeid",codeid);
		abean.set("setname",setname);
		abean.set("chgstate",chgstate);
		abean.set("hismode",hismode);
		abean.set("subflag",subflag);
		abean.set("isvar",isvar);
		return abean;
	}
	
	
	
	public String getSubSql(String _codeid,ArrayList headSetList)
	{
		String value=_codeid.substring(2);
		StringBuffer str=new StringBuffer("");
		
		boolean isB0110_2=false;
		boolean isE0122_2=false;
		boolean isE01a1_2=false;
		for(int i=0;i<headSetList.size();i++){
			LazyDynaBean abean=(LazyDynaBean)headSetList.get(i);
			String field_name = abean.get("field_name").toString();
			if("b0110_2".equalsIgnoreCase(field_name.trim())||("b0110".equalsIgnoreCase(field_name)&&abean.get("chgstate")!=null&& "2".equals((String)abean.get("chgstate")))) {
                isB0110_2=true;
            } else if("e0122_2".equalsIgnoreCase(field_name.trim())||("e0122".equalsIgnoreCase(field_name)&&abean.get("chgstate")!=null&& "2".equals((String)abean.get("chgstate")))) {
                isE0122_2=true;
            } else if("e01a1_2".equalsIgnoreCase(field_name.trim())||("e01a1".equalsIgnoreCase(field_name)&&abean.get("chgstate")!=null&& "2".equals((String)abean.get("chgstate")))) {
                isE01a1_2=true;
            }
		}
		if(this.operationtype!=0)
		{
			if(this.bo.getInfor_type()==1){
				if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
				{
					str.append(" or  b0110_1 like '"+value+"%'");
					if(isB0110_2) {
                        str.append(" or  b0110_2 like '"+value+"%'");
                    }
				}
				else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
				{
					str.append(" or  e0122_1 like '"+value+"%'");
					if(isE0122_2) {
                        str.append(" or  e0122_2 like '"+value+"%'");
                    }
				}
				else if("@K".equalsIgnoreCase(_codeid.substring(0,2)))
				{
					str.append(" or  e01a1_1 like '"+value+"%'");
					if(isE01a1_2) {
                        str.append(" or  e01a1_2 like '"+value+"%'");
                    }
				}
			}
			else if(this.bo.getInfor_type()==2){
					if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						str.append(" or  b0110 like '"+value+"%'");
					}
					else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						str.append(" or  b0110 like '"+value+"%'");
					}
			}
			else if(this.bo.getInfor_type()==3){
					if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						str.append(" or  e01a1 like '"+value+"%'");
					}
					else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						str.append(" or  e01a1 like '"+value+"%'");
					} 
			}
		}
		else{
				if(this.bo.getInfor_type()==1){
					if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						if(isB0110_2) {
                            str.append(" or  b0110_2 like '"+value+"%'");
                        } else if(isE0122_2) {
                            str.append(" or  e0122_2 like '"+value+"%'");
                        }
					}
					else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						if(isE0122_2) {
                            str.append(" or  e0122_2 like '"+value+"%'");
                        } else if(isE01a1_2) {
                            str.append(" or  e01a1_2 like '"+value+"%'");
                        }
					}
					else if("@K".equalsIgnoreCase(_codeid.substring(0,2))&&isE01a1_2)
					{
						str.append(" or  e01a1_2 like '"+value+"%'"); 
					}
				}else if(this.bo.getInfor_type()==2){
					if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						str.append(" or  b0110 like '"+value+"%'"); 
					}
					else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						str.append(" or  b0110 like '"+value+"%'");
						 
					}
				}else if(this.bo.getInfor_type()==3){
					if("@K".equalsIgnoreCase(_codeid.substring(0,2)))
					{
						if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							str.append(" or  e01a1 like '"+value+"%'"); 
						}
						else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							str.append(" or  e01a1 like '"+value+"%'"); 
						}
					}
				 
			}
			
		}
		return str.toString();
	}
	//人事异动历史记录获取查询的sql、列字段、部门显示几级等数据。
	public HashMap  getHistoryTableData2(ArrayList headSetList,int operationtype,String _codeid,String condition){
		HashMap map=new HashMap();
		RowSet rowSet =null;
		try
		{
		    
			StringBuffer sql=new StringBuffer();
			StringBuffer selectColumn = new StringBuffer();//数据库中数据较多的时候把附件那个字段不查询,太费时
			
			String selectSql = "select * from template_archive where tabid=2 and 1=2";
			ContentDAO dao=new ContentDAO(this.con);
			rowSet = dao.search(selectSql);
			ResultSetMetaData selectMt=rowSet.getMetaData();
			String columns = "";
			if(Sql_switcher.searchDbServer()==2||Sql_switcher.searchDbServer()==3||Sql_switcher.searchDbServer()==5||Sql_switcher.searchDbServer()==7){
				selectColumn.append("rownum  AS num,");
				columns+="num,";
			}else{
				selectColumn.append("ROW_NUMBER() OVER(ORDER BY id)  AS num,");
				columns+="num,";
			}
			for(int i=1;i<=selectMt.getColumnCount();i++){
			    
			    String columnName = selectMt.getColumnName(i);
			    if("CONTENT_PDF".equalsIgnoreCase(columnName)||"PHOTO".equalsIgnoreCase(columnName)){//这样的大数据的字段查询是比较慢的,所以 不一次性查询出来
			     continue;   
			    }
			    if(columnName.startsWith("T_")){//这样的大数据的字段查询是比较慢的,所以 不一次性查询出来
			        continue;
			    }
			    if("basepre".equalsIgnoreCase(columnName)){
			    	columnName="(select dbname from dbName where lower(pre)=lower(basepre)) as basepre2,basepre";
			    	 columns+="basepre2,basepre,";
			    }else{
			    	columns+=columnName+",";
			    }
			    selectColumn.append(columnName+",");
			   
			}
			String columns2="";
			if(selectColumn.toString().trim().length()>0){
				columns2 = selectColumn.toString().trim();
				columns2 = columns2.substring(0, columns2.length()-1);
			}
			sql.append("select "+columns2+" from template_archive where tabid="+tabid+" ");
			
			if(!userview.isSuper_admin())
			{
				String operOrg = userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null)
				{
					sql.append(" and 1=2 ");
				}
				else if(!"UN`".equalsIgnoreCase(operOrg))
				{
					sql.append(" and ( ");  
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) { 
							 if (temp[j]!=null&&temp[j].length()>0)
							 {
								 tempSql.append(getSubSql(temp[j],headSetList));		 
							 }
						}
						if(tempSql.length()>0) {
                            sql.append(tempSql.substring(3));
                        } else {
                            sql.append("  1=2 ");
                        }
					}
					else {
                        sql.append("  1=2 ");
                    }
					sql.append(" )");
				}
			}
			
			String strsql=sql.toString();
			if(!"".equals(condition)) {
                strsql+= " and "+condition;
            }
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
                display_e0122="0";
            }
			this.hmuster_sql=strsql;
			map.put("sql_str", strsql);
			map.put("order_sql", "");
			map.put("columns", columns);
			map.put("display_e0122", display_e0122);
							
			} //while end
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
				
		}
		return map;
	}
	
	public ArrayList getHistoryTableData(ArrayList headSetList,int operationtype,String _codeid,String condition)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet =null;
		try
		{
		    
			StringBuffer sql=new StringBuffer();
			StringBuffer selectColumn = new StringBuffer();//数据库中数据较多的时候把附件那个字段不查询,太费时
			
			String selectSql = "select * from template_archive where tabid=2 and 1=2";
			ContentDAO dao=new ContentDAO(this.con);
			rowSet = dao.search(selectSql);
			ResultSetMetaData selectMt=rowSet.getMetaData();
			for(int i=1;i<=selectMt.getColumnCount();i++){
			    
			    String columnName = selectMt.getColumnName(i);
			    if("CONTENT_PDF".equalsIgnoreCase(columnName)||"PHOTO".equalsIgnoreCase(columnName)){//这样的大数据的字段查询是比较慢的,所以 不一次性查询出来
			     continue;   
			    }
			    if(columnName.startsWith("T_")){//这样的大数据的字段查询是比较慢的,所以 不一次性查询出来
			        continue;
			    }
			    selectColumn.append(columnName+",");
			}
			String columns = "";
			if(selectColumn.toString().trim().length()>0){
			    columns = selectColumn.toString().trim();
			    columns = columns.substring(0, columns.length()-1);
			}
			sql.append("select "+columns+" from template_archive where tabid="+tabid+" ");
			
			if(!userview.isSuper_admin())
			{
				String operOrg = userview.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
				if(operOrg==null)
				{
					sql.append(" and 1=2 ");
				}
				else if(!"UN`".equalsIgnoreCase(operOrg))
				{
					sql.append(" and ( ");  
					if(operOrg!=null && operOrg.length() >3)
					{
						StringBuffer tempSql = new StringBuffer(""); 
						String[] temp = operOrg.split("`");
						for (int j = 0; j < temp.length; j++) { 
							 if (temp[j]!=null&&temp[j].length()>0)
							 {
								 tempSql.append(getSubSql(temp[j],headSetList));		 
							 }
						}
						if(tempSql.length()>0) {
                            sql.append(tempSql.substring(3));
                        } else {
                            sql.append("  1=2 ");
                        }
					}
					else {
                        sql.append("  1=2 ");
                    }
					sql.append(" )");
				}
			}
			/*
			if(_codeid!=null&&_codeid.trim().length()>2)
			{
				String value=_codeid.substring(2);
				if(this.operationtype!=0){
					if(this.bo.getInfor_type()==1){
						if(_codeid.substring(0,2).equalsIgnoreCase("UN"))
						{
							sql.append(" and b0110_1 like '"+value+"%'");
						}
						else if(_codeid.substring(0,2).equalsIgnoreCase("UM"))
						{
							sql.append(" and e0122_1 like '"+value+"%'");
						}
						else if(_codeid.substring(0,2).equalsIgnoreCase("@K"))
						{
							sql.append(" and e01a1_1 like '"+value+"%'");
						}
						}else if(this.bo.getInfor_type()==2){
							if(_codeid.substring(0,2).equalsIgnoreCase("UN"))
							{
								sql.append(" and b0110 like '"+value+"%'");
							}
							else if(_codeid.substring(0,2).equalsIgnoreCase("UM"))
							{
								sql.append(" and b0110 like '"+value+"%'");
							}
						}else if(this.bo.getInfor_type()==3){
							if(_codeid.substring(0,2).equalsIgnoreCase("UN"))
							{
								sql.append(" and e01a1 like '"+value+"%'");
							}
							else if(_codeid.substring(0,2).equalsIgnoreCase("UM"))
							{
								sql.append(" and e01a1 like '"+value+"%'");
							}
							
						}
				}else{
					for(int i=0;i<headSetList.size();i++){
						LazyDynaBean abean=(LazyDynaBean)headSetList.get(i);
						String field_name = abean.get("field_name").toString();
						if(field_name.indexOf("_")!=-1){
							field_name = field_name.substring(0, field_name.indexOf("_"));
						}
						if(this.bo.getInfor_type()==1){
						if(_codeid.substring(0,2).equalsIgnoreCase("UN")&&field_name.equalsIgnoreCase("b0110"))
						{
							sql.append(" and b0110_2 like '"+value+"%'");
							break;
						}
						else if(_codeid.substring(0,2).equalsIgnoreCase("UM")&&field_name.equalsIgnoreCase("e0122"))
						{
							sql.append(" and e0122_2 like '"+value+"%'");
							break;
						}
						else if(_codeid.substring(0,2).equalsIgnoreCase("@K")&&field_name.equalsIgnoreCase("e01a1"))
						{
							sql.append(" and e01a1_2 like '"+value+"%'");
							break;
						}
					}else if(this.bo.getInfor_type()==2){
						if(_codeid.substring(0,2).equalsIgnoreCase("UN"))
						{
							sql.append(" and b0110 like '"+value+"%'");
							break;
						}
						else if(_codeid.substring(0,2).equalsIgnoreCase("UM"))
						{
							sql.append(" and b0110 like '"+value+"%'");
							break;
						}
					}else if(this.bo.getInfor_type()==3){
						if(_codeid.substring(0,2).equalsIgnoreCase("@K"))
						{
							if(_codeid.substring(0,2).equalsIgnoreCase("UN"))
							{
								sql.append(" and e01a1 like '"+value+"%'");
								break;
							}
							else if(_codeid.substring(0,2).equalsIgnoreCase("UM"))
							{
								sql.append(" and e01a1 like '"+value+"%'");
								break;
							}
						}
					}
					}
					
				}
			}*/
			String strsql=sql.toString();
			if(!"".equals(condition)) {
                strsql+= " and "+condition;
            }
			
		  
			this.hmuster_sql=strsql;
			
			 rowSet = dao.search(" select * from dbname ");
			HashMap map = new HashMap();
			while(rowSet.next()){
				map.put(rowSet.getString("pre").toLowerCase(), rowSet.getString("dbname"));
			}
			rowSet=dao.search(strsql);
 			
			ResultSetMetaData mt=rowSet.getMetaData();
			HashMap existTableColumnMap=new HashMap();
			for(int i=1;i<=mt.getColumnCount();i++) {
                existTableColumnMap.put(mt.getColumnName(i).toUpperCase(),"1");//template_set表中所有的字段
            }
			LazyDynaBean _abean=new LazyDynaBean();
			LazyDynaBean abean=null;
			int num=0;
			SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");//提交时间的显示样式
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
                display_e0122="0";
            }
			while(rowSet.next())
			{
				num++;
				String a0100="";
				String basepre="";
				_abean=new LazyDynaBean();
				_abean.set("num",String.valueOf(num));
				
				_abean.set("chg","2");
				for(int i=0;i<headSetList.size();i++)
				{
					abean=(LazyDynaBean)headSetList.get(i);
					String field_name=(String)abean.get("field_name");//字段名字。如C0109_2
					String field_type=(String)abean.get("field_type");//字段类型
					String isvar=(String)abean.get("isvar");//是否是临时变量
					String subflag=(String)abean.get("subflag");//0：字段 1：子集
					String codeid=(String)abean.get("codeid");
					String chgstate=(String)abean.get("chgstate");
					int disformat = Integer.parseInt(abean.get("disformat")==null?"0":(String)abean.get("disformat"));//日期型字段有多种显示方式。如2012-3-4,2013.9.9
					String formula = abean.get("formula")==null?"":""+abean.get("formula");
					String sub_domain_id = "";
					if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){//如果是变化前
						sub_domain_id = (String)abean.get("sub_domain_id");
						if(sub_domain_id!=null&&sub_domain_id.length()>0){
							sub_domain_id ="_"+sub_domain_id;
						}
					}
					String field_name_copy = field_name;
					if(field_name_copy.indexOf("_")!=-1){//field_name_copy把field_name的下划线去掉
						field_name_copy = field_name_copy.substring(0, field_name_copy.indexOf("_"));
					}
					if("affixfile".equals(field_name))//如果是附件
					{
						_abean.set(field_name,"");
					}
					else if("1".equals(isvar)&&existTableColumnMap.get(field_name.toUpperCase())==null)//如果是临时变量
					{
						_abean.set(field_name,"");
					}
					else if("1".equals(subflag))//如果是子集
					{
						_abean.set(field_name,"......");
					}
					else if("content_pdf".equals(field_name)){
					    _abean.set(field_name,"");
					}
					else//如果是字段
					{
						if("M".equalsIgnoreCase(field_type))//如果字段是备注型
						{
							//判断数据字典里的指标类型
							FieldItem item=DataDictionary.getFieldItem(field_name_copy);
							if(item!=null&&item.getItemtype()!=null){
								if("M".equalsIgnoreCase(item.getItemtype())){//如果字段是备注型
									_abean.set(field_name,SafeCode.encode(Sql_switcher.readMemo(rowSet,field_name).replace("\r\n","<br>").replace("`","<br>")));
								}	
								else if("D".equalsIgnoreCase(item.getItemtype()))//如果字段是日期型
								{
									/**yyyy-MM-dd*/
									String str = Sql_switcher.readMemo(rowSet,field_name);
									String values ="";
									if(str.indexOf("`")!=-1){
										String strs[] =str.split("`");
										for(int j=0;j<strs.length;j++){
											if(strs[j].trim().length()>0){
												values += formatDateValue(strs[j],formula,disformat);
												if(j<strs.length-1){
													values+="`";
												}
											}
										}
									}else{
										values = formatDateValue(str,formula,disformat);
									}
									_abean.set(field_name,SafeCode.encode(values.replace("\r\n","<br>").replace("`","<br>")));
								}
								else if("N".equalsIgnoreCase(item.getItemtype()))//如果字段是数值型
								{
									int ndec=disformat;//小数点位数
									String prefix=((formula==null)?"":formula);
									String str = Sql_switcher.readMemo(rowSet,field_name);
									String values ="";
									if(str.indexOf("`")!=-1){
										String strs[] =str.split("`");
										for(int j=0;j<strs.length;j++){
											if(strs[j].trim().length()>0){
												values += prefix+PubFunc.DoFormatDecimal(strs[j],ndec);
												if(j<strs.length-1){
													values+="`";
												}
											}
										}
									}else{
										values = prefix+PubFunc.DoFormatDecimal(str,ndec);
									}
									_abean.set(field_name,SafeCode.encode(values.replace("\r\n","<br>").replace("`","<br>")));
									
								}else{//如果字段是字符型或代码型
								//	if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
										String str = Sql_switcher.readMemo(rowSet,field_name);
										String values ="";
										if(str.indexOf("`")!=-1){
											String strs[] =str.split("`");
											for(int j=0;j<strs.length;j++){
												if(strs[j].trim().length()>0){
													if(codeid!=null&&!"0".equals(codeid)) {
                                                        values += AdminCode.getCodeName(codeid,strs[j]);
                                                    } else {
                                                        values += strs[j];
                                                    }
													if(j<strs.length-1){
														values+="`";
													}
												}
											}
										}else{
											if(codeid!=null&&!"0".equals(codeid)) {
                                                values = AdminCode.getCodeName(codeid,str);
                                            } else {
                                                values = str;
                                            }
										}
										_abean.set(field_name,SafeCode.encode(values.replace("\r\n","<br>").replace("`","<br>")));
//										}else{
//											list.add(Sql_switcher.readMemo(rset,field_name));
//										}
								}
							}
						} //如果字段不是备注 结束
						else if("D".equalsIgnoreCase(field_type))//如果是日期
						{
							if(rowSet.getDate(field_name)!=null){
								if("lasttime".equals(field_name)){
									_abean.set(field_name,df2.format(rowSet.getDate(field_name)));	
								}else {
                                    _abean.set(field_name,formatDateValue(PubFunc.FormatDate(rowSet.getDate(field_name)),formula,disformat));
                                }
							}
							else {
                                _abean.set(field_name,"");
                            }
						} //如果不是日期 结束
						else//如果是字符型或数字型
						{
							if(rowSet.getString(field_name)==null)
							{
								_abean.set(field_name,"");
							}
							else
							{
								if("A".equalsIgnoreCase(field_type)&&!"0".equals(codeid))
								{
									if("UM".equals(codeid.toUpperCase())){
										
										if("".equals(AdminCode.getCodeName(codeid,rowSet.getString(field_name)))) {
                                            codeid="UN";
                                        }
									}
									if("1".equals(isvar)&& "A".equalsIgnoreCase(field_type)&& "".equals(codeid)){
										_abean.set(field_name,rowSet.getString(field_name));
									}else{
										if("UM".equals(codeid.toUpperCase())){
										 String value="";
											if(Integer.parseInt(display_e0122)==0)
																	{
												value=AdminCode.getCodeName("UM",rowSet.getString(field_name));
																	}
																	else
																	{
																		CodeItem item=AdminCode.getCode("UM",rowSet.getString(field_name),Integer.parseInt(display_e0122));
														    	    	if(item!=null)
														    	    	{
														    	    		value=item.getCodename();
														        		}
														    	    	else
														    	    	{
														    	    		value = AdminCode.getCodeName("UM",rowSet.getString(field_name));
														    	    	}
														    	    	
																	}	
									     _abean.set(field_name,value);
										}else {
                                            _abean.set(field_name,AdminCode.getCodeName(codeid,rowSet.getString(field_name)));
                                        }
									}
								}
								else{
									if(field_name.lastIndexOf("_")!=-1&& "codesetid".equalsIgnoreCase(field_name.substring(0, field_name.lastIndexOf("_")))){
										String name ="";
										if("UN".equalsIgnoreCase(rowSet.getString(field_name))){
											name="单位";
										}else if("UM".equalsIgnoreCase(rowSet.getString(field_name))){
											name="部门";
										}else if("@K".equalsIgnoreCase(rowSet.getString(field_name))){
											name="职位";
										}
										_abean.set(field_name,name);	
									}else {
                                        _abean.set(field_name,rowSet.getString(field_name));
                                    }
								}
							}
						} //如果是字符型或数值型 结束
					} //如果是字段 结束
				} //for end
				_abean.set("task_id",rowSet.getString("task_id")!=null?rowSet.getString("task_id"):"");
				if(this.bo.getInfor_type()==1){//如果是人员
					 a0100=rowSet.getString("a0100");
					 basepre=rowSet.getString("basepre");//bug 39472 客户库里有空数据，basepre值为空，导致包空指针异常。此处进行兼容。
					 if(StringUtils.isNotBlank(basepre)){
						 basepre=(String)map.get(basepre.trim().toLowerCase());
						 _abean.set("a0100", a0100);
						 _abean.set("basepre2", rowSet.getString("basepre").trim());
						 _abean.set("basepre", basepre);
					 }else{
						 _abean.set("a0100", "");
						 if(StringUtils.isNotBlank(a0100)){
							 _abean.set("a0100", a0100);
						 }
						 _abean.set("basepre2", "");
						 _abean.set("basepre", "");
					 }
				}else if(this.bo.getInfor_type()==2){//如果是单位
					 a0100=rowSet.getString("b0110");
					 _abean.set("b0110", a0100);
				}else if(this.bo.getInfor_type()==3){//如果是岗位
					 a0100=rowSet.getString("e01a1");
					 _abean.set("b0110", a0100);
				}
				
				list.add(_abean);
			
			} //while end
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
				
		}
	
		return list;
	}
	public String getStartDate(){
		 Calendar c = Calendar.getInstance();
	      Date date = c.getTime();
	      SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
	      String date1 = simple.format(date);
	      date1 = date1.substring(0,date1.lastIndexOf("-")+1)+"01";
		return date1 ;
	}
	public String getEndDate(){
		 Calendar c = Calendar.getInstance();
	      Date date = c.getTime();
	      SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
	      String date1 = simple.format(date);
	      date1 = date1.substring(0,date1.lastIndexOf("-")+1)+ c.getActualMaximum(Calendar.DATE);
		return date1 ;
	}
	 /**
	    * 取某年某月的最后一天
	    * @param str 
	    *         某年某月
	    * @return string
	    *          返回某年某月的最后一天
	    * */
	public  String getDateByAfteri(String str) throws GeneralException
	{
		
		Calendar now = Calendar.getInstance();
		int maxDay =0;
		
		try {
		    	Date date = new SimpleDateFormat("yyyy-MM").parse(str);
		    	now.setTime(date);
		        maxDay = now.getActualMaximum(Calendar.DATE);
		        now.add(GregorianCalendar.DAY_OF_MONTH,maxDay-1);
		        
		 }catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		 return new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());
	}
	public String getHmuster_sql() {
		return hmuster_sql;
	}
	public void setHmuster_sql(String hmuster_sql) {
		this.hmuster_sql = hmuster_sql;
	}
	//维护表结构
	public void synctemplate_archiveField(String tablename){


		
		Table table=new Table(tablename);
		DbWizard dbw=new DbWizard(this.con);
		try
		{
			if(!dbw.isExistTable(tablename, false))
			{
				table.addField(getFieldItem("N","","id","主键序号",0,0,true));
				table.addField(getFieldItem("N","","tabid","表格号",0,0,false));
				table.addField(getFieldItem("A","","A0100","人员编号",8,0,false));
				table.addField(getFieldItem("A","","BasePre","库前缀",3,0,false));
				table.addField(getFieldItem("A","","chguser","最近提交的用户",50,0,false));
				table.addField(getFieldItem("D","","lasttime","最近提交的时间",10,0,false));
				table.addField(getFieldItem("M","","appprocess","审批流程",0,0,false));
				table.addField(getFieldItem("N","","from_tabid","通知模板号",0,0,false));
				table.addField(getFieldItem("N","","from_id","消息来源归档号",0,0,false));
				table.addField(getFieldItem("A","","operationcode","业务代码",4,0,false));
				table.addField(getFieldItem("N","","task_id","任务号",0,0,false));
				table.addField(getFieldItem("BN","","content_pdf","PDF文件",0,0,false));
				table.addField(getFieldItem("A","","content_ext","文件后缀名",8,0,false));
				HashMap hm=new HashMap();
				table.addField(getFieldItem("A","UN","b0110_1",ResourceFactory.getProperty("column.sys.org"),30,0,false));
				hm.put("b0110_1", "1");
				table.addField(getFieldItem("A","UM","e0122_1",ResourceFactory.getProperty("column.sys.dept"),30,0,false));
				hm.put("e0122_1", "1");
				table.addField(getFieldItem("A","@k","e01a1_1",ResourceFactory.getProperty("column.sys.pos"),30,0,false));
				hm.put("e01a1_1", "1");
				table.addField(getFieldItem("A","","a0101_1",ResourceFactory.getProperty("label.title.name"),30,0,false));  
				hm.put("a0101_1", "1");
				dbw.createTable(table);
			}
			else
			{
				 ContentDAO dao=new ContentDAO(this.con);
				 HashMap columnNameMap=new HashMap();
				 RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				 ResultSetMetaData data=rowSet.getMetaData();
				 for(int i=1;i<=data.getColumnCount();i++)
				 {
						String columnName=data.getColumnName(i).toLowerCase();
						columnNameMap.put(columnName,"1");
				 }
				
				
				if(columnNameMap.get("a0100")==null) {
                    table.addField(getFieldItem("A","","A0100","人员编号",8,0,false));
                }
				if(columnNameMap.get("basepre")==null) {
                    table.addField(getFieldItem("A","","BasePre","库前缀",3,0,false));
                }
				if(columnNameMap.get("chguser")==null) {
                    table.addField(getFieldItem("A","","chguser","最近提交的用户",50,0,false));
                }
				if(columnNameMap.get("lasttime")==null) {
                    table.addField(getFieldItem("D","","lasttime","最近提交的时间",10,0,false));
                }
				if(columnNameMap.get("appprocess")==null) {
                    table.addField(getFieldItem("M","","appprocess","审批流程",0,0,false));
                }
				if(columnNameMap.get("from_tabid")==null) {
                    table.addField(getFieldItem("N","","from_tabid","通知模板号",0,0,false));
                }
				if(columnNameMap.get("from_id")==null) {
                    table.addField(getFieldItem("N","","from_id","消息来源归档号",0,0,false));
                }
				if(columnNameMap.get("operationcode")==null) {
                    table.addField(getFieldItem("A","","operationcode","业务代码",4,0,false));
                }
				if(columnNameMap.get("task_id")==null) {
                    table.addField(getFieldItem("N","","task_id","任务号",0,0,false));
                }
				if(columnNameMap.get("content_pdf")==null) {
                    table.addField(getFieldItem("BN","","content_pdf","PDF文件",0,0,false));
                }
				if(columnNameMap.get("content_ext")==null) {
                    table.addField(getFieldItem("A","","content_ext","文件后缀名",8,0,false));
                }
				if(columnNameMap.get("b0110_1")==null) {
                    table.addField(getFieldItem("A","UN","b0110_1",ResourceFactory.getProperty("column.sys.org"),30,0,false));
                }
				if(columnNameMap.get("e0122_1")==null) {
                    table.addField(getFieldItem("A","UM","e0122_1",ResourceFactory.getProperty("column.sys.dept"),30,0,false));
                }
				if(columnNameMap.get("e01a1_1")==null) {
                    table.addField(getFieldItem("A","@k","e01a1_1",ResourceFactory.getProperty("column.sys.pos"),30,0,false));
                }
				if(columnNameMap.get("a0101_1")==null) {
                    table.addField(getFieldItem("A","","a0101_1",ResourceFactory.getProperty("label.title.name"),30,0,false));
                }
				
				if(table.getCount()>0) {
                    dbw.addColumns(table);
                }
			}
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		
		
	
	}
	private Field getFieldItem(String dataType,String codesetid,String itemid,String itemname,int length,int decimal,boolean isKey)
	{
		Field _temp=new Field(itemid,itemname);
		if("A".equalsIgnoreCase(dataType))
		{
			_temp.setDatatype(DataType.STRING);
			_temp.setLength(length);
			if(codesetid!=null&&codesetid.length()>0) {
                _temp.setCodesetid(codesetid);
            }
		}
		else if("N".equalsIgnoreCase(dataType))
		{
			
			if(decimal==0) {
                _temp.setDatatype(DataType.INT);
            } else
			{
				_temp.setDatatype(DataType.FLOAT);
				_temp.setDecimalDigits(decimal);
			}
		}
		else if("BN".equalsIgnoreCase(dataType)) {
            _temp.setDatatype(DataType.BINARY);
        } else if("M".equalsIgnoreCase(dataType)) {
            _temp.setDatatype(DataType.CLOB);
        } else if("D".equalsIgnoreCase(dataType))
		{
			_temp.setDatatype(DataType.DATE);
		}
		if(isKey){
			_temp.setNullable(false);
			_temp.setKeyable(true);
			_temp.setVisible(false);
		}
		return _temp;
	}
	   /**
	 * 格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @param ext 扩展
	 * @return
	 */
	public String formatDateValue(String value,String ext ,int disformat)
	{
		StringBuffer buf=new StringBuffer();
		if(ext!=null&&ext.indexOf("<EXPR>")!=-1)
		{
			 
			int f=ext.indexOf("<EXPR>");
			int t=ext.indexOf("</FACTOR>"); 
			String _temp=ext.substring(0,f);
			String _temp2=ext.substring(t+9);
			ext=_temp+_temp2; 
		}
		int idx=ext.indexOf(",");  //-,至今
		String prefix="",strext="";
		if(idx==-1)
		{
			String[] preCond=getPrefixCond(ext);
			prefix=preCond[0];
		}
		else
		{
			prefix=ext.substring(0,idx);
			strext=ext.substring(idx+1);
		}
		if(value==null|| "".equals(value))
		{
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		}
		else
		{
			buf.append(prefix);
		}
		value=value.replace(".","-").replace("/", "-").replace("年", "-").replace("月", "-").replace("日", "");
		value=(value.lastIndexOf("-")==value.length()-1)?value.substring(0, value.length()-1):value;
		Date date=DateUtils.getDate(value,"yyyy-MM-dd");
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		String strv[]=exchangNumToCn(year,month,day);	
		value=value.replaceAll("-",".");
		switch(disformat)
		{
		case 6: //1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 7: //91.12.3
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 8://1991.2
			buf.append(year);
			buf.append(".");
			buf.append(month);			
			break;
		case 9://1992.02
			buf.append(value.substring(0,7));
			break;
		case 10://92.2
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			break;
		case 11://98.02
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			break;
		case 12://一九九一年一月二日

			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			buf.append(strv[2]);
			buf.append("日");
			break;
		case 13://一九九一年一月
			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");			
			break;
		case 14://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 15://1991年1月
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 16://91年1月2日
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 17://91年1月
			if(year>=2000) {
                buf.append(year);
            } else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");			
			break;
		case 18://年龄
			buf.append(getAge(year,month,day));
			break;
		case 19://1991（年）
			buf.append(year);
			break;
		case 20://1 （月）
			buf.append(month);
			break;
		case 21://23 （日）
			buf.append(day);
			break;
		case 22://1999年02月
			buf.append(year);
			buf.append("年");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		case 23://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;
		case 24://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10) {
                buf.append(month);
            } else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10) {
                buf.append(day);
            } else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		return buf.toString();
	}
	/**
	 * 解释Formula字段的内容
	 * for example
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * @return
	 */
	  private String[] getPrefixCond(String formula)
	  {
		   String[] preCond=new String[3];
		   int idx=formula.indexOf("<");
		   if(idx==-1)
		   {
			   preCond[0]=formula; 
		   }
		   else
		   {
			   preCond[0]=formula.substring(0, idx);
			   preCond[2]=getPattern("FACTOR",formula)+",";
			   preCond[2]=preCond[2].replaceAll(",", "`");
			   preCond[1]=getPattern("EXPR",formula);
		   }
		   return preCond;
	  }	
	  /**
		 * 数字换算
		 * @param strV
		 * @param flag
		 * @return
		 */
		private String[] exchangNumToCn(int year,int month,int day)
		{
			String[] strarr=new String[3];
			StringBuffer buf=new StringBuffer();
			String value=String.valueOf(year);
			for(int i=0;i<value.length();i++)
			{
				switch(value.charAt(i))
				{
				case '1':
					buf.append("一");
					break;
				case '2':
					buf.append("二");
					break;
				case '3':
					buf.append("三");
					break;
				case '4':
					buf.append("四");
					break;
				case '5':
					buf.append("五");
					break;
				case '6':
					buf.append("六");
					break;
				case '7':
					buf.append("七");
					break;
				case '8':
					buf.append("八");
					break;
				case '9':
					buf.append("九");
					break;
				case '0':
					buf.append("零");
					break;
				}
			}
			strarr[0]=buf.toString();
			buf.setLength(0);
			switch(month)
			{
			case 1:
				buf.append("一");
				break;
			case 2:
				buf.append("二");
				break;
			case 3:
				buf.append("三");
				break;
			case 4:
				buf.append("四");
				break;
			case 5:
				buf.append("五");
				break;
			case 6:
				buf.append("六");
				break;
			case 7:
				buf.append("七");
				break;
			case 8:
				buf.append("八");
				break;
			case 9:
				buf.append("九");
				break;
			case 10:
				buf.append("十");
				break;			
			case 11:
				buf.append("十一");
				break;
			case 12:
				buf.append("十二");
				break;
			}
			strarr[1]=buf.toString();
			buf.setLength(0);
			switch(day)
			{
			case 1:
				buf.append("一");
				break;
			case 2:
				buf.append("二");
				break;
			case 3:
				buf.append("三");
				break;
			case 4:
				buf.append("四");
				break;
			case 5:
				buf.append("五");
				break;
			case 6:
				buf.append("六");
				break;
			case 7:
				buf.append("七");
				break;
			case 8:
				buf.append("八");
				break;
			case 9:
				buf.append("九");
				break;
			case 10:
				buf.append("十");
				break;			
			case 11:
				buf.append("十一");
				break;
			case 12:
				buf.append("十二");
				break;			
			case 13:
				buf.append("十三");
				break;			
			case 14:
				buf.append("十四");
				break;			
			case 15:
				buf.append("十五");
				break;			
			case 16:
				buf.append("十六");
				break;			
			case 17:
				buf.append("十七");
				break;			
			case 18:
				buf.append("十八");
				break;			
			case 19:
				buf.append("十九");
				break;			
			case 20:
				buf.append("二十");	
				break;			
			case 21:
				buf.append("二十一");
				break;			
			case 22:
				buf.append("二十二");	
				break;			
			case 23:
				buf.append("二十三");
				break;			
			case 24:
				buf.append("二十四");	
				break;			
			case 25:
				buf.append("二十五");
				break;			
			case 26:
				buf.append("二十六");	
				break;			
			case 27:
				buf.append("二十七");
				break;			
			case 28:
				buf.append("二十八");	
				break;			
			case 29:
				buf.append("二十九");
				break;			
			case 30:
				buf.append("三十");	
				break;			
			case 31:
				buf.append("三十一");				
				break;
			}		
			strarr[2]=buf.toString();
			return strarr;
		}
		/**
		 * 计算年龄
		 * @param nyear
		 * @param nmonth
		 * @param nday
		 * @return
		 */
		private String getAge(int nyear,int nmonth,int nday)
		{
			int ncyear,ncmonth,ncday;
			Date curdate=new Date();
			ncyear=DateUtils.getYear(curdate);
			ncmonth=DateUtils.getMonth(curdate);
			ncday=DateUtils.getDay(curdate);
			StringBuffer buf=new StringBuffer();
		
			/*
			double fcage=ncyear+ncmonth*0.01+ncday*0.0001;
			double fage=nyear+nmonth*0.01+nday*0.0001;
			long nage= Math.round(fcage-fage);
			buf.append(nage);*/
			int result =ncyear-nyear;   
	        if   (nmonth>ncmonth)   {   
	            result = result-1;   
	        }   
	        else 
	        {
	            if   (nmonth==ncmonth)  {   
	                if   (nday >ncday)   {   
	                    result   =   result   -   1;   
	                }   
	            }   
	        }
			buf.append(result);
			return buf.toString();
		}
		private String getPattern(String strPattern,String formula)
		{
			int iS,iE;
			String result="";
			String sSP="<"+strPattern+">";
			iS=formula.indexOf(sSP);
			String sEP="</"+strPattern+">";
			iE=formula.indexOf(sEP);
			if(iS>=0 && iS<iE)
			{
				result=formula.substring(iS+sSP.length(), iE);
			}
			return result;
		}	
		/**
		 * 查找业务类型 0,1,2,3,4,10
		 * 对人员调入，人员调出等业务对一些特殊的规则
		 * @param operationcode
		 * @return
		 */
		private int findOperationType(String operationcode)
		{
			StringBuffer strsql=new StringBuffer();
			strsql.append("select operationtype from operation where operationcode='");
			strsql.append(operationcode);
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.con);
			int flag=-1;
			try
			{
				RowSet rset=dao.search(strsql.toString());
				if(rset.next()) {
                    flag=rset.getInt("operationtype");
                }
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return flag;		
		}
}
