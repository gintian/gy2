package com.hjsj.hrms.transaction.pos.standardpos;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String fieldstr=(String)this.getFormHM().get("fieldstr");
		ArrayList fieldList=(ArrayList)this.getFormHM().get("fieldList");
		String a_code=(String)this.getFormHM().get("a_code");
		String codesetid ="";
		String codeitemid="";
		if(a_code.length()>2){
			codesetid = a_code.substring(0,2);
			codeitemid=a_code.substring(2);
		}else{
			codesetid = a_code;
		}
		String kind=(String)this.getFormHM().get("kind");
		String orglike=(String)this.getFormHM().get("orglike");
		String querylike=(String)this.getFormHM().get("querylike");
		String query=(String)this.getFormHM().get("query");
		ArrayList factorlist=(ArrayList)this.getFormHM().get("selectfieldlist");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		/*String uplevel=(String)this.getFormHM().get("uplevel");
		if(uplevel==null||uplevel.length()==0)
    		uplevel="0";*/
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String backdate = (String)map.get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		//String backdate =sdf.format(new Date());
		InfoUtils infoUtils=new InfoUtils();
		StringBuffer columns=new StringBuffer();
		columns.append(fieldstr);	
	    StringBuffer wheresql=new StringBuffer();
	    wheresql.append(" where 1=1");
	    if(query!=null&& "1".equals(query))
	    {
	    	String whereQuery=getWhereSql(factorlist,querylike);
	    	if(whereQuery!=null&&whereQuery.length()>0)
	    	{
	    		wheresql.append(" and  "+whereQuery);
//	    		orglike="1";
//	    		this.getFormHM().put("orglike", "1");	
	    	}
            this.getFormHM().put("isShowCondition", "");	
	    }else
	    {
	    	if(query!=null&& "0".equals(query))
	    	{
	    		this.getFormHM().put("scanfieldlist",infoUtils.clearFieldValueList(factorlist));
	    	}
	    	this.getFormHM().put("query", "");	    	
	    	this.getFormHM().put("isShowCondition", "none");	
	    }
	    
	    String sql =getLeftJoinSql(fieldList,wheresql.toString(),codesetid,codeitemid,backdate,orglike);
	    String codemess="";
	    CodeItem codeitem=AdminCode.getCode(codesetid, codeitemid,5);
	    if(codeitem!=null)
			  codemess=codeitem.getCodename();
		this.getFormHM().put("codemess", codemess);
		this.getFormHM().put("sqlstr", sql);
		this.getFormHM().put("wherestr", "");
		this.getFormHM().put("columnstr", columns.toString());
		this.getFormHM().put("orderby", /*"order by e01a1"*/"order by a0000");
	}	
	/**
	 * 得到条件下的where,语句
	 * **/
	public String getWhereSql(ArrayList factorlist,String like) throws GeneralException
	{
		InfoUtils infoUtils=new InfoUtils();
		String whereTrem=infoUtils.combine_SQL(this.userView,factorlist,like,"","3");
		if(whereTrem!=null&&whereTrem.length()>0)
		{
			
			whereTrem="(AAA.e01a1 in(select K01.e01a1 "+whereTrem+"))";		
		}else
			return "";
		/*if(whereTrem!=null&&whereTrem.length()>0)
		{
			if(whereTrem.indexOf("WHERE")!=-1)
				   whereTrem=whereTrem.substring(whereTrem.indexOf("WHERE")+5);
				else
					return "";
		}		*/
		return whereTrem;
	}
	/**
	 * 
	 * @param candid_setid  子集
	 * @param candid_codesetid 指标项
	 * @param candid_value  指标值
	 * @param display_list  显示项
	 * @param code  
	 * @param kind
	 * @return
	 */
	
	public String getLeftJoinSql(ArrayList fieldList,String where,String codesetid,String codeitemid,String backdate,String orglike)
	{
			
		StringBuffer select_str=new StringBuffer("select codeitemid h0100");
		StringBuffer sqlfrom=new StringBuffer(" from codeitem");
		StringBuffer sqlwhere=new StringBuffer(" where codesetid='"+codesetid+"' and ");
		sqlwhere.append(com.hrms.hjsj.utils.Sql_switcher.dateValue(backdate)+" between start_date and end_date "); 
		sqlwhere.append("and codeitemid in (select H0100 from h01)"); 
		HashMap setMap = new HashMap();
		setMap.put("codeitem", null);
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldList.get(i);
			String itemid=fieldItem.getItemid();
			String setid = fieldItem.getFieldsetid();
			select_str.append(","+itemid);
			if(!setMap.containsKey(setid)){
				setMap.put(setid, null);
				sqlfrom.append(" left join "+setid+" on codeitem.codeitemid="+setid+".H0100");
				if(!"H01".equalsIgnoreCase(setid)){
					sqlwhere.append(" and "+setid+".i9999=(select max(i9999) from "+setid+" t"+i+" where t"+i+".H0100="+setid+".H0100)");
				}
			}
		}
		
		if("1".equals(orglike)){
			sqlwhere.append(" and codeitemid like '"+codeitemid+"%'");
		}else{
			sqlwhere.append(" and parentid='"+codeitemid+"'");
		}
		return select_str.append(sqlfrom).append(sqlwhere).toString();
	}
}
