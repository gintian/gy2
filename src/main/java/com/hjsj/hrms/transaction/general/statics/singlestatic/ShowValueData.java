/*
 * Created on 2006-2-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.statics.singlestatic;


import com.hjsj.hrms.businessobject.general.statics.singlestatic.SingleStaticBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowValueData extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//得到前台数据
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String datavalue=(String)hm.get("datavalue");
		String realdata=(String)hm.get("realdata");
		String fieldname=(String)hm.get("fieldname");
		String dbpre=(String)hm.get("dbpre");
		String userbases=(String)hm.get("userbases");
		String setname=(String)hm.get("setname");
		String time = (String)hm.get("time");
		String flag = (String)hm.get("flag");
		String select = (String)hm.get("select");
		//通过数据字典得到字段类型以及有关信息
		FieldItem fielditem = DataDictionary.getFieldItem(fieldname);
		String itemtype=fielditem.getItemtype();
		String fieldsetid = fielditem.getFieldsetid();
		String fielddesc = fielditem.getItemdesc();
		String fieldset = fieldsetid.substring(0,1);
		String query ="";
		String codesetid= userView.getManagePrivCode();
        String codevalue= userView.getManagePrivCodeValue();
		StringBuffer  strexpr=new StringBuffer();
        StringBuffer  strfactor=new StringBuffer();
        String sql = "";
		String find=(String)this.getFormHM().get("find");        
		boolean bfind=false;
	    if(find==null|| "".equals(find)|| "0".equals(find))
	    {
	    	bfind=false;
	    }else{
	    	bfind=true;
	    }        
        //生成权限只有在显示人员的情况下权限才起作用
        if("UN".equals(codesetid))
        {
            strfactor.append("B0110=");
            strfactor.append(codevalue);
            strfactor.append("*`");
            strexpr.append("1");
        }
        else if("UM".equals(codesetid))
        {
            strfactor.append("E0122=");
            strfactor.append(codevalue);
            strfactor.append("*`");
            strexpr.append("1");            
        }
        else if("@K".equals(codesetid))
        {
            strfactor.append("E01A1=");
            strfactor.append(codevalue);
            strfactor.append("*`");
            strexpr.append("1");            
        }
        else
        {
           strfactor.append("B0110=*`");
           strexpr.append("1");  
        }
        ArrayList fieldlist=new ArrayList();
        if(!userView.isSuper_admin())
        {
          //  query=" and"+userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist).substring(18);  
        	query=" and "+dbpre+"a01.a0100 in (select "+dbpre+"A01.a0100 "+userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist)+")";
        }
        else
        {            
            query="";        	
        }

        ArrayList valuelist=new ArrayList();
        SingleStaticBo singlestaticbo=new SingleStaticBo(this.getFrameconn());
        sql = singlestaticbo.getshowsql(datavalue,dbpre,setname,fieldname,time,flag,query,realdata); 
        String[] temp = sql.split("#",3);
        String strsql = temp[0];
        String where_str = temp[1];
        String columns = temp[2];
        /**查询结果*/
        StringBuffer res_sql=new StringBuffer();
        if(bfind)
        {
        	if(this.userView.getStatus()==0){
				if("A".equals(fieldset.substring(0,1)))
				{
					res_sql.append(dbpre+"A01.A0100 IN (SELECT A0100 FROM ");
					res_sql.append(this.userView.getUserId()+dbpre+"Result )");
				}else if("B".equals(fieldset.substring(0,1))){
					res_sql.append("B01.B0110 IN (SELECT B0110 FROM ");
					res_sql.append(this.userView.getUserId()+"BResult )");
				}else if("K".equals(fieldset.substring(0,1))){
					res_sql.append("K01.e01a1 IN (SELECT e01a1 FROM ");
					res_sql.append(this.userView.getUserId()+"KResult )");
				}  
        	}else if (this.userView.getStatus()==4){
        		if("A".equals(setname.substring(0,1)))
				{
        			res_sql.append(dbpre+"A01.A0100 IN (SELECT obj_id FROM t_sys_result");
        			res_sql.append(" where upper(nbase)='"+dbpre.toUpperCase()+"' and flag=0)");
				}else if("B".equals(setname.substring(0,1))){
					res_sql.append("B01.B0110 IN (SELECT B0110 FROM t_sys_result");
					res_sql.append(" where upper(nbase)='B' and flag=1)");
				}else if("K".equals(setname.substring(0,1))){
					res_sql.append("K01.e01a1 IN (SELECT e01a1 FROM t_sys_result");
					res_sql.append(" where upper(nbase)='K' and flag=2)");
				}	
        	}
			where_str=where_str+" and "+res_sql.toString();
        }
        if(!"b".equals(dbpre)&&!"k".equals(dbpre)){
        	String tmpsql="select "+columns+" ";
        	where_str=where_str.toUpperCase();
        	strsql=strsql.toUpperCase();
        	String strdbpre=dbpre.toUpperCase();
        	if(userbases.indexOf("`")==-1){
        		where_str=" from ("+(strsql+" "+where_str).replaceAll(strdbpre, userbases).replaceAll("``", userbases)+") tt";
			}else{
				StringBuffer sb= new StringBuffer();
				String[] tmpbases=userbases.split("`");
				for(int i=0;i<tmpbases.length;i++){
					String base=tmpbases[i];
					if(base.length()==3){
						if(sb.length()>0){
							sb.append(" union all "+(strsql+" "+where_str).replaceAll(strdbpre, base).replaceAll("``", base));
						}else{
							sb.append((strsql+" "+where_str).replaceAll(strdbpre, base).replaceAll("``", base));
						}
					}
				}
				where_str=" from ("+sb.toString()+") tt";
			}
        	strsql=tmpsql;
        }
        this.getFormHM().put("strsql",strsql);
        this.getFormHM().put("where_str",where_str);
        this.getFormHM().put("columns",columns);
		this.getFormHM().put("valuelist",valuelist);
		this.getFormHM().put("dbpre",dbpre);
		this.getFormHM().put("itemdesc",fielddesc);
		this.getFormHM().put("select",select);
		int ndecimal=fielditem.getDecimalwidth();
	    int len=fielditem.getItemlength();
	    String format="";	    
		if("N".equals(itemtype))
	    {
	          if (ndecimal > 0) 
	   	      {
	   	    		format = "#.";
	   	    	    for (int i = 0;i<ndecimal;i++)
	   	    	    	format += "#";
	   	      } 
	   	      else 
	   	      {
	   	    		format = "###"; 
	   	      }
	    }
	    this.getFormHM().put("formatstr", format);
	}
  }
