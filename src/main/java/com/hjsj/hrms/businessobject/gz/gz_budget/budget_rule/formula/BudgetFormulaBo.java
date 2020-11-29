/**
 * <p>Title:</p> 
 * <p>Description:薪资预算-计算公式-子集、指标、代码列表</p> 
 * <p>Company:HJHJ</p> 
 * <p>Create time:${date}:${time}</p> 
 * <p>@version: 5.0</p>
 * <p>@author wangrd</p>
*/
package com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BudgetFormulaBo {
	private ContentDAO dao=null;
	private Connection conn=null;
	private String sqlstr ="";
	private String formula_id="";
	private String formula_name="";
	private String tab_id="0";
	private String formula_type="";
	private String tab_type="";
	private String tab_name="";
	private String CodeSetid="";
	private String strError="";
	
	public BudgetFormulaBo(Connection conn,String cformula_id){
		this.conn = conn;
		this.formula_id = cformula_id;
		formula_id = formula_id!=null&&formula_id.length()>0?formula_id:"0";	
		dao = new ContentDAO(conn);
       try{	
			sqlstr = "select formulaname,formulaType,tab_id from gz_budget_formula  where formula_id="+ formula_id + "";			
			ArrayList dylist = dao.searchDynaList(sqlstr);
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				 DynaBean dynabean = (DynaBean) it.next();
				 formula_type = dynabean.get("formulatype").toString();	
				 tab_id= dynabean.get("tab_id").toString();		 
				 this.formula_name= dynabean.get("formulaname").toString();		 
			   }
	
			sqlstr = "select tab_type,codesetid,tab_name from gz_budget_tab  where tab_id="+ tab_id + "";			
			ArrayList dylist1 = dao.searchDynaList(sqlstr);
		    for (Iterator it = dylist1.iterator(); it.hasNext();) {
			  DynaBean dynabean = (DynaBean) it.next();
			  tab_type = dynabean.get("tab_type").toString();			 
			  CodeSetid = dynabean.get("codesetid").toString();			 
			  tab_name = dynabean.get("tab_name").toString();			 
		      }			
		       
	   }catch (Exception sqle) {
		sqle.printStackTrace();
	    }
	}	
	
	private HashMap GetSysMap(UserView userView){
		HashMap sysOptionMap=new HashMap();  //系统项参数
		BudgetSysBo bo=new BudgetSysBo(this.conn,userView);
		sysOptionMap=bo.getSysValueMap();
		return sysOptionMap;
	}		
	
	private String GetTabname(String ntab_id){
		String tab_name="";
	    try{		
			sqlstr = "select tab_name from gz_budget_tab  where tab_id="+ ntab_id + "";			
			ArrayList dylist1 = this.dao.searchDynaList(sqlstr);
		    for (Iterator it = dylist1.iterator(); it.hasNext();) {
			  DynaBean dynabean = (DynaBean) it.next();
			 tab_name = dynabean.get("tab_name").toString();			 		 
		      }	
		}catch (Exception sqle) {
			sqle.printStackTrace();
		    }
	   return  tab_name;
	}			
	
	public ArrayList GetEmptyList(){
		ArrayList list = new ArrayList();
		CommonData obj1=new CommonData("","");
		list.add(obj1);
		return list;
	 }
	//人员信息集
	public ArrayList GetFieldList(){
		ArrayList fieldList = new ArrayList();
		String sqlstr = "select fieldsetid,customdesc from fieldset where (fieldsetid like 'A%' or fieldsetid like 'B%') and useflag=1"
						+" order by "+Sql_switcher.substr("fieldsetid", "1","1")+",displayorder ";
		
		ArrayList dylist = null;
		CommonData obj1=new CommonData("","");
		fieldList.add(obj1);
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String fieldsetid = dynabean.get("fieldsetid").toString();
				String customdesc = dynabean.get("customdesc").toString();
				CommonData dataobj = new CommonData(fieldsetid+":"+customdesc,fieldsetid+":"+customdesc);
				fieldList.add(dataobj);
			}
		} catch(GeneralException e) {
			e.printStackTrace();
		}
		return fieldList;
	}	
    //其他预算表的导入项
	public ArrayList GetFieldList_imp(UserView userView){
		HashMap sysOptionMap= GetSysMap(userView);
		String Itemdesc="";
		String setid=(String)sysOptionMap.get("ysze_set");	//预算总额	
		String paramsetid=(String)sysOptionMap.get("ysparam_set");	//预算参数		
		setid = setid!=null&&setid.length()>0?setid:"";
		paramsetid = paramsetid!=null&&paramsetid.length()>0?paramsetid:"";				
		
		ArrayList fieldList = new ArrayList();
		CommonData obj1=new CommonData("","");
		fieldList.add(obj1);		

		Itemdesc =ResourceFactory.getProperty("gz.budget.formula.budgetparam");//预算参数
		if(!("".equals(paramsetid))){
			CommonData obj4=new CommonData(paramsetid+":"+Itemdesc,Itemdesc);
			fieldList.add(obj4);			
		 }	
		Itemdesc =GetTabname("1");;//预算总额
		if(!("".equals(setid))){
			CommonData obj3=new CommonData(setid+":"+Itemdesc,Itemdesc);
			fieldList.add(obj3);			
		 }
		Itemdesc =GetTabname("2");//员工名册
		CommonData obj2=new CommonData("SC01"+":"+Itemdesc,Itemdesc);
		fieldList.add(obj2);

		return fieldList;
	}	
	//计算项
	public ArrayList GetFieldList_calc(UserView userView){
		HashMap sysOptionMap= GetSysMap(userView);
		String Itemdesc="";
		String setid=(String)sysOptionMap.get("ysze_set");	//预算总额	
		String paramsetid=(String)sysOptionMap.get("ysparam_set");	//预算参数		
		setid = setid!=null&&setid.length()>0?setid:"";
		paramsetid = paramsetid!=null&&paramsetid.length()>0?paramsetid:"";				
		
		ArrayList fieldList = new ArrayList();
		CommonData obj1=new CommonData("","");
		fieldList.add(obj1);		

		Itemdesc =ResourceFactory.getProperty("gz.budget.formula.budgetparam");//预算参数
		if(!("".equals(paramsetid))){
			CommonData obj4=new CommonData(paramsetid+":"+Itemdesc,Itemdesc);
			fieldList.add(obj4);			
		 }	
		Itemdesc =GetTabname("1");;//预算总额
		if(!("".equals(setid))){
			CommonData obj3=new CommonData(setid+":"+Itemdesc,Itemdesc);
			fieldList.add(obj3);			
		 }
		Itemdesc =GetTabname("2");//员工名册
		CommonData obj2=new CommonData("SC01"+":"+Itemdesc,Itemdesc);
		fieldList.add(obj2);

		if (!("1".equals(tab_id))&& !("2".equals(tab_id))){ //非名册、总额
			String sqlstr = "select tab_id,tab_name from gz_budget_tab where tab_type in (3,4) order by seq";
			ArrayList dylist = null;
			try {
				dylist = dao.searchDynaList(sqlstr);
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					String fieldsetid = dynabean.get("tab_id").toString();
					String customdesc = dynabean.get("tab_name").toString();
					CommonData dataobj = new CommonData(fieldsetid+":"+customdesc,customdesc);
					fieldList.add(dataobj);
				}
			} catch(GeneralException e) {
				e.printStackTrace();
			}				
		}			
		return fieldList;
	}
	//行项目
	public ArrayList GetRowItemList(String fieldsetid){			
		String FirstText="";
		ArrayList fieldList = new ArrayList();
		CommonData obj1=new CommonData("","");
		fieldList.add(obj1);
		FirstText = fieldsetid.substring(0,1);
		
		if ("SC01".equalsIgnoreCase(fieldsetid)){
			CommonData obj2=new CommonData("sum","sum:"+ResourceFactory.getProperty("gz.budget.formula.mcsum"));//合计
			fieldList.add(obj2);				
		 }
		else if ((!"A".equalsIgnoreCase(FirstText)) && (!"B".equalsIgnoreCase(FirstText)) ){
		  String CodeSetid="";	
		  String cTab_id =	fieldsetid;
		  try {
			  String str = "select codesetid from gz_budget_tab  where tab_id="+ cTab_id + "";			
			  ArrayList dylist1 = dao.searchDynaList(str);
		      for (Iterator it = dylist1.iterator(); it.hasNext();) {
			    DynaBean dynabean = (DynaBean) it.next();
			    CodeSetid = dynabean.get("codesetid").toString();   
		      }		
		      str = "select codeitemid,codeitemdesc from codeitem where codesetid='"
		    	    + CodeSetid +"' order by codeitemid";
			  ArrayList dylist = null;
	
		     dylist = dao.searchDynaList(str);
			 for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean1=(DynaBean)it.next();
				String codesetid1 =dynabean1.get("codeitemid").toString();
				String customdesc = dynabean1.get("codeitemdesc").toString();
				CommonData dataobj = new CommonData(codesetid1,codesetid1+":"+customdesc);
				fieldList.add(dataobj);
			 }
			CommonData dataobj = new CommonData("*","*");
			fieldList.add(dataobj);
		 } catch(GeneralException e) {
				e.printStackTrace();
	     	}
		}	
		return fieldList;
	}	
	//列项目
	public ArrayList GetColItemList(String fieldsetid,UserView userView){
		HashMap sysOptionMap= GetSysMap(userView);
		String setid=(String)sysOptionMap.get("ysze_set");	//预算总额	
		String paramsetid=(String)sysOptionMap.get("ysparam_set");	//预算参数		
		setid = setid!=null&&setid.length()>0?setid:"";
		paramsetid = paramsetid!=null&&paramsetid.length()>0?paramsetid:"";		
		String excludeStr="";
		String s="";
		
		s=(String)sysOptionMap.get("ysze_idx_menu");	//预算总额索引
		if (s!=null) excludeStr=excludeStr+","+s;
		s=(String)sysOptionMap.get("ysparam_idx_menu");	//预算参数索引
		if (s!=null)  excludeStr=excludeStr+ ","+s;
		s=(String)sysOptionMap.get("ysze_status_menu");	//预算总额状态
		if (s!=null)  excludeStr=excludeStr+ ","+s;
		excludeStr= excludeStr+",budget_id,tab_id,A0100,I9999,SC010";
		excludeStr=excludeStr.toUpperCase();
		
		if ("1".equals(fieldsetid))
			fieldsetid=setid;
		else if ("2".equals(fieldsetid))
			fieldsetid="SC01";
		String FirstText="";
		ArrayList list = new ArrayList();	
		String ctab_type="";
		String cCodeSetid="";
		if(fieldsetid!=null&&fieldsetid.length()>0){
			FirstText = fieldsetid.substring(0,1);	
		}
			
		
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			 if ((!fieldsetid.equalsIgnoreCase(("SC01"))) && (!"A".equalsIgnoreCase(FirstText))
					   && (!"B".equalsIgnoreCase(FirstText)) )
			 {
				String CodeSetid="";	
				String cTab_id =	fieldsetid;
				String str = "select tab_type,codesetid from gz_budget_tab  where tab_id="+ cTab_id + "";			
				ArrayList dylist1 = dao.searchDynaList(str);
			    for (Iterator it = dylist1.iterator(); it.hasNext();) {
				  DynaBean dynabean = (DynaBean) it.next();
				  ctab_type = dynabean.get("tab_type").toString();   
				  cCodeSetid = dynabean.get("codesetid").toString();   				  
			     }		
			    if ("4".equals(ctab_type)){
				   fieldsetid="SC03";				
			    } else if ("3".equals(ctab_type)){
				   fieldsetid="SC02";				
			    }					 
			}	

			FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
			ArrayList dylist = new ArrayList();
			if(fieldsetid!=null&&fieldsetid.length()>0){
				dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
			}		
			
			for(int i=0;i<dylist.size();i++){
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if(!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.nybs")) 
						&& !itemdesc.equals(ResourceFactory.getProperty("hmuster.label.counts")))

				{
					if (excludeStr.indexOf(itemid.toUpperCase())<0)
					{
						CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
						list.add(dataobj);
						
					}

				}
			}
			
		    if (paramsetid.equals(fieldsetid)){	
				CommonData dataobj2 = new CommonData("Month",
						  "Month:"+ResourceFactory.getProperty("gz.budget.formula.calcmonth"));//计算月份
				list.add(dataobj2);		    	
		    } else 	if ("3".equals(ctab_type)|| "4".equals(ctab_type)){
				CommonData dataobj2 = new CommonData("*","*");
				list.add(dataobj2);			
		    }	
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//代码项
	public ArrayList GetCodeList(UserView userView,String itemid,String codeset){
		ArrayList list = new ArrayList();
		boolean bcode=true;
		if(itemid==null||itemid.length()<1){
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
			return list;
		}		
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		
		String codesetid ="";
		if(fielditem==null){
			ArrayList fieldlist = getMidVariableList(this.conn);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if(itemid.equalsIgnoreCase(item.getItemid())){
					fielditem=item;
					break;
				}
			}
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				bcode=fielditem.isCode();
				if ("sc000".equalsIgnoreCase(fielditem.getItemid())){
					HashMap sysOptionMap= GetSysMap(userView);
					codesetid=(String)sysOptionMap.get("rylb_codeset");	//人员类别	
					codesetid = codesetid!=null&&codesetid.length()>0?codesetid:"";					
				} else 	if ("itemid".equalsIgnoreCase(fielditem.getItemid())|| "itemdesc".equalsIgnoreCase(fielditem.getItemid())){
	
				    codesetid=codeset;
				}
				
				
				if(bcode ||codesetid.trim().length()>0){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
							
						}
						ArrayList dylist = null;

						dylist = dao.searchDynaList(sqlstr.toString());

						for(Iterator it=dylist.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next();
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							CommonData dataobj;
							if ("itemdesc".equalsIgnoreCase(fielditem.getItemid())){
								dataobj= new CommonData(codeitemdesc,codeitemid+":"+codeitemdesc);
							}
							else {
								dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);	
							}
							
							list.add(dataobj);
						}
						CommonData dataobj = new CommonData("","");
						list.add(0,dataobj);

					}else{
						CommonData dataobj = new CommonData("","");
						list.add(dataobj);
					}
				}else{
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}else{
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public ArrayList getMidVariableList(Connection conn){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select nid,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 ");
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("nid"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					item.setCodesetid("0");
					break;
				case 2:
					item.setItemtype("A");
					item.setCodesetid("0");
					break;
				case 3:
					item.setItemtype("D");
					item.setCodesetid("0");
					break;
				case 4:
					item.setItemtype("A");
					item.setCodesetid(rset.getString("codesetid"));
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	public boolean CheckFormula(UserView userView,String strformula,boolean bTj){
		boolean bResult=false;
		HashMap sysOptionMap= GetSysMap(userView);
		String setid=(String)sysOptionMap.get("ysze_set");	//预算总额	
		String paramsetid=(String)sysOptionMap.get("ysparam_set");	//预算参数		
		setid = setid!=null&&setid.length()>0?setid:"";
		paramsetid = paramsetid!=null&&paramsetid.length()>0?paramsetid:"";				
		
		String Tab_set="";
		switch(Integer.parseInt(tab_type))
		{
			case 1:
				Tab_set=setid;
				break;
			case 2:
				Tab_set="SC01";
				break;
			case 3:
				Tab_set="SC02";
				break;
			case 4:
				Tab_set="SC03";
				break;
			default:	
				break;
		}

		ArrayList fieldList = new ArrayList();
		fieldList.addAll(DataDictionary.getFieldList(Tab_set, 1));
		if (bTj){
			fieldList.addAll(DataDictionary.getFieldList(Tab_set, 1));
			if (!"".equals(paramsetid)){			    
			    fieldList.addAll(DataDictionary.getFieldList(paramsetid, 1));	
			}
		}
		YksjParser yp = new YksjParser(userView, fieldList, YksjParser.forSearch, YksjParser.LOGIC
				, YksjParser.forPerson,"Ht", "");			
		yp.setCon(this.conn);
		try{
			bResult = yp.Verify_where(strformula.trim());
		}catch (Exception e) {
			e.printStackTrace();
			bResult = false;
		}
		if (!bResult) {// 校验通过

			strError = yp.getStrError();
		} 
		
		return bResult;
	}	


	public String getFormula_type() {
		return formula_type;
	}



	public void setFormula_type(String formula_type) {
		this.formula_type = formula_type;
	}



	public String getTab_type() {
		return tab_type;
	}



	public void setTab_type(String tab_type) {
		this.tab_type = tab_type;
	}

	public String getTab_id() {
		return tab_id;
	}

	public void setTab_id(String tab_id) {
		this.tab_id = tab_id;
	}

	public String getFormula_name() {
		return formula_name;
	}

	public void setFormula_name(String formula_name) {
		this.formula_name = formula_name;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getStrError() {
		return strError;
	}

	public void setStrError(String strError) {
		this.strError = strError;
	}

	public String getCodeSetid() {
		return CodeSetid;
	}

	public void setCodeSetid(String codeSetid) {
		CodeSetid = codeSetid;
	}
	
}
