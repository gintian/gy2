
package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title:ModuleQueryTrans</p>
 * <p>Description:模板查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 10, 2005:4:43:44 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ModuleQueryTrans extends IBusiness {

    /**
     * 
     */
    public ModuleQueryTrans() {
        super();
        // TODO Auto-generated constructor stub
    }
    /**分析字符串是否为数值型*/
    private boolean isnumber(String strvalue)
    {
        boolean bflag=true;
        try
        {
            Float.parseFloat(strvalue.replaceAll("-","."));
        }
        catch(NumberFormatException ne)
        {
            bflag=false;
        }
        return bflag;
    }
    /**
     * 
     * @param item
     * @param strexpr
     * @param strfactor
     * @param pos
     * @return
     */
    private int analyFieldCodeValue(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos,String strInfr)
    {
        String str_Hz=item.getViewvalue();
        String nameTemp = str_Hz;
        if(str_Hz.endsWith("*") || str_Hz.endsWith("?") || str_Hz.endsWith("？"))
            nameTemp=str_Hz.substring(0,str_Hz.length()-1);
        
        String sql="";
        if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())
                || "@K".equalsIgnoreCase(item.getCodesetid())) {
          sql="select codeitemid from organization";	
        } else {
        	sql="select codeitemid from codeitem";	
        }
        
        sql += " where codesetid='"+item.getCodesetid()+"' and (codeitemdesc like '%"+nameTemp+"%'";
        if(str_Hz.endsWith("*") || str_Hz.endsWith("?") || str_Hz.endsWith("？"))
        	sql += " or codeitemid like '" + str_Hz.substring(0, str_Hz.length() - 1) + "%'";
        else
        	sql += " or codeitemid='" + str_Hz + "'";
        
        sql += ")";
        List list=ExecuteSQL.executeMyQuery(sql);
        if(list!=null&&list.size()>0)
        {
        	if(pos>1)
        		strexpr.append("*");
        	strexpr.append("("); 
        	for(int i=0;i<list.size();i++)
        	{
        		LazyDynaBean rec=(LazyDynaBean)list.get(i);
        		String codeitemid=(String)rec.get("codeitemid");
        		strexpr.append(pos++);
        		strexpr.append("+");
        		strfactor.append(item.getItemid().toUpperCase());
        		strfactor.append("=");
        		strfactor.append(""+codeitemid+"");
        		strfactor.append("`");
        	}
        	strexpr.setLength(strexpr.length()-1);
        	strexpr.append(")");
        	return list.size();
        }else
        {
        	return 0;
        }
        
    }
    /**分析日期型字段*/
    private int analyFieldDate(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos)
    {
    	 String s_str_date=item.getValue().trim();
         String e_str_date=item.getViewvalue().trim();
         s_str_date=s_str_date.replaceAll("\\.","-");
         e_str_date=e_str_date.replaceAll("\\.","-");
         //item.setValue(s_str_date);
         //item.setViewvalue(e_str_date);
       
         try
         {
             Date s_date=DateStyle.parseDate(s_str_date);
             Date e_date=DateStyle.parseDate(e_str_date);          	
 	        /**起始日期及终止日期格式全对*/
 	        if(s_date!=null&&e_date!=null)
 	        {
 	            if(strexpr.length()==0)
 	            {
 	              strexpr.append(pos);
 	              strexpr.append("*");
 	              strexpr.append(pos+1);
 	            
 	            }
 	            else
 	            {
 	                strexpr.append("*(");                
 	                strexpr.append(pos);
 	                strexpr.append("*");
 	                strexpr.append(pos+1);  
 	                strexpr.append(")");
 	            }
 	            strfactor.append(item.getItemid().toUpperCase());
 	            strfactor.append(">=");
 	            strfactor.append(item.getValue().replaceAll("-","."));
 	            strfactor.append("`");
 	            strfactor.append(item.getItemid().toUpperCase());
 	            strfactor.append("<=");
 	            strfactor.append(item.getViewvalue().replaceAll("-",".")); 
 	            strfactor.append("`");   
 	            return 2;
 	        }
 	        else if (isnumber(s_str_date) && isnumber(e_str_date))
 	        {
 	            if(strexpr.length()==0)
 	            {
 	              strexpr.append(pos);
 	              strexpr.append("*");
 	              strexpr.append(pos+1);
 	            
 	            }
 	            else
 	            {
 	                strexpr.append("*(");                
 	                strexpr.append(pos);
 	                strexpr.append("*");
 	                strexpr.append(pos+1);  
 	                strexpr.append(")");
 	            }
 	            strfactor.append(item.getItemid().toUpperCase());
 	            strfactor.append(">=$YRS[");
 	            strfactor.append(item.getValue());
 	            strfactor.append("]`");
 	            strfactor.append(item.getItemid().toUpperCase());
 	            strfactor.append("<=$YRS[");
 	            strfactor.append(item.getViewvalue()); 
 	            strfactor.append("]`");
 	            return 2;
 	        }
 	        else
 	        {
 	        	if(s_str_date.length()>0){
 	        		s_date=DateStyle.parseDate(s_str_date);
 	        		if(s_date!=null)
 	    	        {
 	    	            if(strexpr.length()==0)
 	    	            {
 	    	              strexpr.append(pos);
 	    	            
 	    	            }
 	    	            else
 	    	            {
 	    	                strexpr.append("*");                
 	    	                strexpr.append(pos);
 	    	            }
 	    	            strfactor.append(item.getItemid().toUpperCase());
 	    	            strfactor.append(">=");
 	    	            strfactor.append(item.getValue().replaceAll("-","."));
 	    	            strfactor.append("`");  
 	    	            return 1;
 	    	        }
 	    	        else if (isnumber(s_str_date))
 	    	        {
 	    	            if(strexpr.length()==0)
 	    	            {
 	    	              strexpr.append(pos);
 	    	            
 	    	            }
 	    	            else
 	    	            {
 	    	                strexpr.append("*");                
 	    	                strexpr.append(pos);
 	    	            }
 	    	            strfactor.append(item.getItemid().toUpperCase());
 	    	            strfactor.append(">=$YRS[");
 	    	            strfactor.append(item.getValue());
 	    	            strfactor.append("]`");
 	    	            return 1;
 	    	        }
 	                
 	        	}
 	        	if(e_str_date.length()>0){
 	        		e_date=DateStyle.parseDate(e_str_date); 
 	        		
 	        		if(e_date!=null)
 	    	        {
 	    	            if(strexpr.length()==0)
 	    	            {
 	    	              strexpr.append(pos);
 	    	            
 	    	            }
 	    	            else
 	    	            {
 	    	                strexpr.append("*");                
 	    	                strexpr.append(pos);
 	    	            }
 	    	            strfactor.append(item.getItemid().toUpperCase());
 	    	            strfactor.append("<=");
 	    	            strfactor.append(item.getViewvalue().replaceAll("-",".")); 
 	    	            strfactor.append("`");   
 	    	            return 1;
 	    	        }
 	    	        else if (isnumber(e_str_date))
 	    	        {
 	    	            if(strexpr.length()==0)
 	    	            {
 	    	              strexpr.append(pos);
 	    	            
 	    	            }
 	    	            else
 	    	            {
 	    	                strexpr.append("*");                
 	    	                strexpr.append(pos);
 	    	            }
 	    	            strfactor.append(item.getItemid().toUpperCase());
 	    	            strfactor.append("<=$YRS[");
 	    	            strfactor.append(item.getViewvalue()); 
 	    	            strfactor.append("]`");
 	    	            return 1;
 	    	        }
 	        	}
 	        }
         }
         catch(Exception ex)
         {
         	return -1;
         }
         return 0;
    }
    
    /**组合查询SQL*/
    private String combine_SQL(ArrayList list,String like,String dbpre,String strInfr,String result) throws GeneralException
    {
        int j=1;
    	boolean bresult=true;
    	boolean blike=false;
    	if("1".equals(result))
    		bresult=false;
    	//liuy 2015-4-15 8763：花名册应该只能看见19个人，可是简单查询，杨模糊查询，能查出100多条（模糊授权未按高级授权走） begin
    	if("1".equals(like))
    		blike=true;
    	//liuy 2015-4-15 end
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        StringBuffer stra0101s=new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            //System.out.println(item.getItemdesc());
            /**如果值未填的话，default是否为不查*/
            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype())))
                continue;
            if(("".equals(item.getValue())&& "".equals(item.getViewvalue()))&&("D".equals(item.getItemtype())))
                continue;
            if("a0101".equalsIgnoreCase(item.getItemid())&&item.getValue().trim().indexOf("|")!=-1){//zgd 2014-3-18 快速查询下姓名列多值查询
    			if(item.getValue().trim().length()==1)//zgd 2014-4-11 在姓名列只有'|'时，跳出
    				continue;
            	stra0101s.append(item.getItemid().toUpperCase());
    			stra0101s.append("=");
    			stra0101s.append(PubFunc.getStr(item.getValue()));
    			stra0101s.append("`");
    			continue;
    		}
            
            if("D".equals(item.getItemtype()))
            {
                int sf=analyFieldDate(item,strexpr,strfactor,j);
                if(sf==-1)
                {
                	throw new GeneralException("输入的日期格式错误或范围不完整，请重新输入！");
                }
                j=j+sf;
            }else if(!"0".equals(item.getCodesetid())&&StringUtils.isNotEmpty(item.getViewvalue())
            		&&(blike || item.getViewvalue().equalsIgnoreCase(item.getValue()))) {
            	int sf=analyFieldCodeValue(item,strexpr,strfactor,j,strInfr);
            	j=j+sf;
            }
            else
            {
	            /**组合表达式串*/
	            if(j==1)
	            {
	                strexpr.append(j);
	            }
	            else
	            {
	                strexpr.append("*");
	                strexpr.append(j);                
	            }
	            
	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
	            {
	            		String q_v=item.getValue().trim();
	                    if("1".equals(like)&&(!(q_v==null|| "".equals(q_v))))
	                    {
			                strfactor.append(item.getItemid().toUpperCase());
			                if("0".equals(item.getCodesetid()))
			                	strfactor.append("=*");
			                else
			                	strfactor.append("=");			                	
			                strfactor.append(PubFunc.getStr(item.getValue()));
			                strfactor.append("*`");	   
	                    }
	                    else
	                    {
			                strfactor.append(item.getItemid().toUpperCase());
			                strfactor.append("=");
			                strfactor.append(PubFunc.getStr(item.getValue()));
			                strfactor.append("`");	  	                        
	                    }
	            }
	            else
	            {
	                strfactor.append(item.getItemid().toUpperCase());
	                strfactor.append("=");
	                strfactor.append(PubFunc.getStr(item.getValue()));
	                strfactor.append("`");
	            }
	            ++j;	            
            }
        }//for i loop end.
        cat.debug("factor="+strfactor.toString());
        cat.debug("expression="+strexpr.toString());
        ArrayList fieldlist=new ArrayList();
        String wherea0101s="";
        if(!userView.isSuper_admin()&& "1".equals(strInfr))
        {
        	if(stra0101s!=null&&!"".equals(stra0101s.toString())){//赵国栋 2014-3-18 姓名列多值查询模糊查找开始找不到，现在支持全名多值查找
        		wherea0101s=userView.getPrivSQLExpression("1"+"|"+stra0101s.toString(), dbpre, false,false,bresult, fieldlist);
        		if(wherea0101s!=null&&wherea0101s.indexOf("WHERE")!=-1)
        		{
        			wherea0101s =" and "+dbpre+"A01.a0100 "+" in (select "+dbpre+"A01.a0100 "+wherea0101s+")";//LiWeichao 2011-07-18 14:53:23
        		}
        	}
        	String strpriv=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(), dbpre, false,blike,bresult, fieldlist);
            cat.debug("priv_strsql="+strpriv);
            return strpriv + wherea0101s;
        }
        else
        {
        	if(stra0101s!=null&&!"".equals(stra0101s.toString())){
        		FactorList factorlist2=new FactorList("1",stra0101s.toString(),dbpre,false,false,bresult,Integer.parseInt(strInfr),userView.getUserId());
        		factorlist2.setSuper_admin(userView.isSuper_admin());
        		wherea0101s=factorlist2.getSqlExpression();
        		if(wherea0101s!=null&&wherea0101s.indexOf("WHERE")!=-1)
        		{
        			wherea0101s = " and "+dbpre+"A01.a0100 "+" in (select "+dbpre+"A01.a0100 "+wherea0101s+")";//LiWeichao 2011-07-18 14:53:23
        		}
        	}
        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,blike,bresult,Integer.parseInt(strInfr),userView.getUserId());
            //fieldlist=factorlist.getFieldList();
        	factorlist.setSuper_admin(userView.isSuper_admin());
            return factorlist.getSqlExpression()+wherea0101s;
        }
    }
    /**
     * 根据输入的查询条件取得查询值
     * @param list
     * @return
     */
    private String query_Field(ArrayList list)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            if(item.getValue()==null|| "".equals(item.getValue()))
                continue;            
            if(j!=0)
                strfields.append(",");
            ++j;
            strfields.append(item.getItemid());
        }
        return strfields.toString();
    }
    /**
     * 查询单位信息，区分查部门＼查单位＼都查
     * @param qobj
     * @param strWhere
     * @return
     */
    private String getQueryObjWhere(String qobj)
    {
    	String strfilter=null;
    	if("1".equals(qobj))
    	{
    		strfilter=" B01.B0110 in (select codeitemid from organization where codesetid='UM')";
    	}
    	else //if(qobj.equals("2"))
    		strfilter=" B01.B0110 in (select codeitemid from organization where codesetid='UN')";
    	return strfilter;
    }
    
    /**
     * 求单位过滤条件，根据管理范围
     * @param flag=2 单位，=3职位
     * @return
     */
    private String getUnitPosFilterCond(int flag)
    {
    	if(userView.isSuper_admin())
    		return "";
    	StringBuffer strcond=new StringBuffer();

    	String codeid=userView.getManagePrivCode();
    	String codevalue=userView.getManagePrivCodeValue();
    	if(codeid==null|| "".equals(codeid))
    		return "";
    	if(flag==2)
    		strcond.append(" B01.B0110 like ");
    	else
    		strcond.append(" K01.E01A1 like ");
    	strcond.append("'");
    	strcond.append(codevalue);
    	strcond.append("%");
    	strcond.append("'");
    	return strcond.toString();
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {

        ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
        String like=(String)this.getFormHM().get("like");
        String result=(String)this.getFormHM().get("result");
        String qobj=(String)this.getFormHM().get("qobj");
        String dbpre=(String)this.getFormHM().get("dbpre");
    	String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
        //HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		rebackKeyword(list);
        
        /**=1人员,=2单位,=3职位*/
        String strInfkind=(String)this.getFormHM().get("type");//(String)hm.get("inforkind");
        if(strInfkind==null|| "".equals(strInfkind))
            strInfkind="1";
        if(list==null)
            return;
        if(like==null|| "".equals(like))
            like="0";
        
        InfoUtils infoUtils = new InfoUtils();
        String privCodeValue = "";
        String privCode = "";
        if(!this.userView.isSuper_admin()) {
        	privCodeValue = this.userView.getManagePrivCodeValue();
        	privCode = this.userView.getManagePrivCode();
        }
        
        String kind = "2";
        if("UM".equalsIgnoreCase(privCode))
        	kind = "1";
        else if("@k".equalsIgnoreCase(privCode))
        	kind = "0";
         
        if(dbpre==null|| "2".equals(strInfkind)|| "3".equals(strInfkind))//单位，职位
        {
        	dbpre="";
            String strwhere=combine_SQL(list,like,dbpre,strInfkind,result);
            if("2".equals(strInfkind)&&(!"0".equals(qobj)))
            {
            	if(strwhere.length()>0)
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            			strwhere=strwhere+" and "+getQueryObjWhere(qobj);
            		else
            			strwhere=strwhere+" WHERE "+getQueryObjWhere(qobj);
            	}
            }
            /**对单位还得加上管理范围*/
            if("2".equals(strInfkind)|| "3".equals(strInfkind))
            {
            	String filtercond=getUnitPosFilterCond(Integer.parseInt(strInfkind));
            	if(filtercond.length()!=0)
            	{
            		if(strwhere.indexOf("WHERE")!=-1)
            			strwhere=strwhere+" and "+filtercond;
            		else
            			strwhere=strwhere+" WHERE "+filtercond;
            	}
            }        
           
            ArrayList resultlist=privFieldList(list,"1");
            /**同样根据是否填定查询值，取得查询指标*/
            String fields=query_Field(resultlist);
            StringBuffer strsql=new StringBuffer();
            if("2".equals(strInfkind))
            {
    	        strsql.append("select distinct b01.b0110 ");
                this.getFormHM().put("distinct","B01.B0110");    
                this.getFormHM().put("keys","B01.B0110");  
            }
            else if("3".equals(strInfkind))
            {
    	        strsql.append("select distinct K01.e01a1 ");    
                this.getFormHM().put("distinct","K01.E01A1"); 
                this.getFormHM().put("keys","K01.E01A1"); 
            }            
            if(fields==null|| "".equals(fields))
            {
                strsql.append(" ");
            }
            else
            {
            	strsql.append(",");
            	strsql.append(fields);        	
            }
            strsql.append(" ");
            if("0".equalsIgnoreCase(result))
            /**保存查询结果*/
            	saveQueryResult(strInfkind,dbpre,strsql.toString()+" "+strwhere);
            else
            	filterQueryResult(strInfkind,dbpre,strsql.toString()+" "+strwhere);
            
            this.getFormHM().put("cond_sql",strsql.toString());
            fields = getFillOutFields(strInfkind,fields);
            this.getFormHM().put("columns",fields);
            this.getFormHM().put("strwhere",strwhere);
            this.getFormHM().put("resultlist",resultlist);
            /**浏览信息用的卡片*/
            this.getFormHM().put("tabid", searchCard(strInfkind));     
            
        }else if("All".equals(dbpre)&& "1".equals(strInfkind))//全部人员库
        {
        	ArrayList dblist=this.userView.getPrivDbList();
        	if(dblist==null||dblist.size()<=0)
        	{
        		throw new GeneralException("没有人员库权限！");
        	}
        	
        	StringBuffer bursql=new StringBuffer();
        	ArrayList resultlist=privFieldList(list,"1");
            /**同样根据是否填定查询值，取得查询指标*/
            String fields=query_Field(resultlist);
            StringBuffer columns=new StringBuffer();
            ArrayList mainlist=getMainFieldList(strInfkind);
            FieldItem itemtemp = new FieldItem();
			itemtemp.setFieldsetid("");
			itemtemp.setItemdesc("人员库");
			itemtemp.setItemid("dbname");
			itemtemp.setPriv_status(1);
			itemtemp.setItemtype("A");
			itemtemp.setCodesetid("0");
       	     mainlist.add(0,itemtemp);
         //   mainlist.add("{}");
        	/**未定义主集指标项*/
            columns.append("a0000,");            
        	if(mainlist.size()==0)
        	{
        		columns.append("a0101,b0110,e0122,e01a1 ");
        	}
        	else
        	{
        		resultlist=mainlist;		       
		        fields=getMainQueryFields(mainlist);
		        columns.append("b0110,e0122 ");
        	}
        	if(fields==null|| "".equals(fields))
            {
        		columns.append(" ");
            }
            else
            {
            	columns.append(",");
            	columns.append(fields);        	
            }
        	columns.append(" ");
        	for(int i=0;i<dblist.size();i++)
        	{
        		 String nbase=dblist.get(i).toString();
        		 String strwhere=combine_SQL(list,like,nbase,strInfkind,result);
        		 String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,dbpre,privCodeValue,true,kind,"org","","All");
                 if(StringUtils.isNotEmpty(term_Sql))
                	 strwhere = strwhere.replaceFirst("FROM " + dbpre + "A01", "FROM (" + term_Sql + ") " + dbpre + "A01");
        		 
                 String queryResultSql = "select (select dbname from dbname WHERE pre ='"+nbase+"'  ) as dbname, "+nbase+"a01.a0100 "+" "+strwhere;
        		 if("0".equalsIgnoreCase(result))
        	         /**保存查询结果*/
        	         saveQueryResult(strInfkind,nbase, queryResultSql);        		                                      
        	     else
        	         filterQueryResult(strInfkind,nbase, queryResultSql);
        		 
        		 bursql.append("select "+i+" as i,(select dbname from dbname WHERE pre ='"+nbase+"'  ) as dbname,'"+nbase+"' as nbase,"+nbase+"a01.a0100 ,"+columns.toString());
        		 bursql.append(strwhere);
        		 bursql.append(" union all ");
        	}
        	bursql.setLength(bursql.length()-11);
        	this.getFormHM().put("distinct","");	 
            this.getFormHM().put("keys",""); 
        	this.getFormHM().put("cond_sql",bursql.toString());
            fields = getFillOutFields(strInfkind,fields);
            this.getFormHM().put("columns","dbname,nbase,"+fields);
            this.getFormHM().put("strwhere","");
            this.getFormHM().put("resultlist",resultlist);
             /**浏览信息用的卡片*/
            this.getFormHM().put("tabid", searchCard(strInfkind));
            this.getFormHM().put("order", "order by i,A0000");
        }else
        {
			//验证人员库前缀是否正确
			validateNbase(dbpre);

			String strwhere=combine_SQL(list,like,dbpre,strInfkind,result);
            String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,dbpre,privCodeValue,true,kind,"org","","All");
            if(StringUtils.isNotEmpty(term_Sql))
           	 strwhere = strwhere.replaceFirst("FROM " + dbpre + "A01", "FROM (" + term_Sql + ") " + dbpre + "A01");
            
        	ArrayList resultlist=privFieldList(list,"1");
            /**同样根据是否填定查询值，取得查询指标*/
            String fields=query_Field(resultlist);
            StringBuffer strsql=new StringBuffer();
        	ArrayList mainlist=getMainFieldList(strInfkind);
 			
			FieldItem itemtemp = new FieldItem();
			itemtemp.setFieldsetid("");
			itemtemp.setItemdesc("人员库");
			itemtemp.setItemid("dbname");
			itemtemp.setPriv_status(1);
			itemtemp.setItemtype("A");
			itemtemp.setCodesetid("0");
       	     mainlist.add(0,itemtemp);
        	/**未定义主集指标项*/
        	if(mainlist.size()==0)
        	{
		        strsql.append("select distinct a0000,(select dbname from dbname WHERE pre ='"+dbpre+"'  )' as dbname,"+dbpre+"' as nbase,");
		        strsql.append(dbpre);
		        strsql.append("a01.a0100 ,");
		        strsql.append(dbpre);        
		        strsql.append("a01.b0110 b0110,");
	            strsql.append(dbpre);
	            strsql.append("a01.e0122 e0122,");	        
		        strsql.append(dbpre);
		        strsql.append("a01.e01a1 e01a1,a0101 "); 
        	}
        	else
        	{
        		resultlist=mainlist;
		        strsql.append("select distinct a0000,(select dbname from dbname WHERE pre ='"+dbpre+"'  ) as dbname,'"+dbpre+"' as nbase,");
		        strsql.append(dbpre);
		        strsql.append("a01.a0100 ,");
		        strsql.append(dbpre);        
		        strsql.append("a01.b0110 b0110,");
	            strsql.append(dbpre);
	            strsql.append("a01.e0122 e0122");			        
		        fields=getMainQueryFields(mainlist);
        	}
            this.getFormHM().put("distinct",dbpre+"a01.a0100");	 
            this.getFormHM().put("keys",dbpre+"a01.a0100"); 
            this.getFormHM().put("order", " order by A0000");
            if(fields==null|| "".equals(fields))
            {
                strsql.append(" ");
            }
            else
            {
            	strsql.append(",");
            	strsql.append(fields);        	
            }
            strsql.append(" ");
            if("0".equalsIgnoreCase(result))
            /**保存查询结果*/
            	saveQueryResult(strInfkind,dbpre,strsql.toString()+" "+strwhere);
            else
            	filterQueryResult(strInfkind,dbpre,strsql.toString()+" "+strwhere);
            
            this.getFormHM().put("cond_sql",strsql.toString());
            fields = getFillOutFields(strInfkind,fields);
            this.getFormHM().put("columns","dbname,nbase,"+fields);
            this.getFormHM().put("strwhere",strwhere);
            this.getFormHM().put("resultlist",resultlist);
            /**浏览信息用的卡片*/
            this.getFormHM().put("tabid", searchCard(strInfkind));
        }
           
    }

    /**
     * 根据主集定义，取得对应的查询字段
     * @param list
     * @return
     */
    private String getMainQueryFields(ArrayList list)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
			if(",b0110,e0a1,e0122,nbase,dbname,".indexOf("," + item.getItemid().toLowerCase() + ",")!=-1)
				continue; 
			
            if(j!=0)
                strfields.append(",");
            ++j;
          
            strfields.append(item.getItemid());
        }
        return strfields.toString();    	
    }
    /**
     * 根据信息群类别，查询定义的登记表格号
     * @param infortype =1人员 =2单位 3=职位 
     * @return
     */
    private String searchCard(String infortype)
    {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				 cardid="-1";
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }
    
    /**
     * 过滤查询结果
     * @param type
     * @param dbpre
     * @param sql
     */
    private void filterQueryResult(String type, String dbpre,String sql)throws GeneralException
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(this.userView.getStatus()==4)
		{
			String tabldName = "t_sys_result";
			Table table = new Table(tabldName);
			DbWizard dbWizard = new DbWizard(this.getFrameconn());
			if (!dbWizard.isExistTable(table)) {
				return;
			}
			/**=0 人员=1 单位=2 岗位*/

			String flag="0";
			if("2".equalsIgnoreCase(type))
			{
				flag="1";
			}
			else if("3".equalsIgnoreCase(type))
			{
				flag="2";
			}
			StringBuffer str = new StringBuffer("delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'");
			if("1".equalsIgnoreCase(type))
			{
				str.append(" and UPPER(nbase)='"+dbpre.toUpperCase()+"'");
			}
			if("2".equals(type))
	    	{
				str.append(" and obj_id not in ");
				str.append(" (select ");
				str.append("B0110  from (");
				str.append(sql);
		    	str.append(") myset)");
	    	}
    		else if("3".equals(type))
    		{
    			str.append(" and obj_id not in ");
    			str.append(" (select ");
    			str.append("E01A1  from (");
    			str.append(sql);
    			str.append(") myset)");		
    		}
    		else 
	    	{
    			str.append(" and obj_id not in ");
    			str.append(" (select ");
    			str.append("A0100  from (");
    			str.append(sql);
    			str.append(") myset)");			
    		}
			try
			{
		    	dao.update(str.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
	    	if("2".equals(type))
		    	dbpre="B";
	    	if("3".equals(type))
		    	dbpre="K";
	    	String tablename=this.userView.getUserName()+dbpre+"result";
		    StringBuffer delsql=new StringBuffer();
	    	
	    	try
	    	{
		    	delsql.append("delete from  ");
	    		delsql.append(tablename);
		    	if("2".equals(type))
		    	{
		    		delsql.append(" where B0110 not in ");
		    		delsql.append(" (select ");
			    	delsql.append("B0110  from (");
			    	delsql.append(sql);
			    	delsql.append(") myset)");
		    	}
	    		else if("3".equals(type))
	    		{
	    			delsql.append(" where E01A1 not in ");
		    		delsql.append(" (select ");
		    		delsql.append("E01A1  from (");
		    		delsql.append(sql);
		    		delsql.append(") myset)");		
	    		}
	    		else 
		    	{
	    			delsql.append(" where A0100 not in ");
	    			delsql.append(" (select ");
	    			delsql.append("A0100  from (");
		    		delsql.append(sql);
	    			delsql.append(") myset)");			
	    		}
	    		dao.update(delsql.toString());
	    	}
	    	catch(Exception ex)
    		{
	    		ex.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(ex);
    		}
		}
		
    }
    
	/**
	 * 保存查询结果
	 * @param type
	 * @param dbpre
	 */
	private void saveQueryResult(String type, String dbpre,String sql)throws GeneralException
	{
		if(this.userView.getStatus()==4)
		{
			try
			{
				String tabldName = "t_sys_result";
				Table table = new Table(tabldName);
				DbWizard dbWizard = new DbWizard(this.getFrameconn());
				if (!dbWizard.isExistTable(table)) {
					return;
				}
				/**=0 人员=1 单位=2 岗位*/

				String flag="0";
				if("2".equalsIgnoreCase(type))
				{
					flag="1";
				}
				else if("3".equalsIgnoreCase(type))
				{
					flag="2";
				}
				String str = "delete from " + tabldName+" where flag="+flag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
				if("1".equalsIgnoreCase(type))
				{
					str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";
				}
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.delete(str, new ArrayList());
				StringBuffer buf_sql = new StringBuffer("");
				if ("1".equals(type)) {
					buf_sql.append("insert into " + tabldName);
					buf_sql.append("(username,nbase,obj_id,flag) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'"+dbpre.toUpperCase()+"' as nbase,A0100 as obj_id, 0 as flag");
					buf_sql.append(" from ("+sql+") myset");
				} else if ("2".equals(type)) {
					buf_sql.append("insert into " + tabldName + " (username,nbase,obj_id,flag) ");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'B',b0110 as obj_id,1 as flag from ("+sql+") myset");
				} else if ("3".equals(type)) {
					buf_sql.append("insert into " + tabldName+ " (username,nbase,obj_id,flag)");
					buf_sql.append("select '"+userView.getUserName()+"' as username,'K',e01a1 as obj_id,2 as flag from("+sql+") myset");
				}
				dao.insert(buf_sql.toString(), new ArrayList());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
	    	if("2".equals(type))
	    		dbpre="B";
	    	if("3".equals(type))
	    		dbpre="K";
	    	String tablename=this.userView.getUserName()+dbpre+"result";
	    	AutoCreateQueryResultTable.execute(this.getFrameconn(), tablename, type);
	    	StringBuffer inssql=new StringBuffer();
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	try
	    	{
		    	inssql.append("insert into ");
		    	inssql.append(tablename);
		    	inssql.append("(");
		    	if("2".equals(type))
			    {
		    		inssql.append("B0110)");
			    	inssql.append(" select ");
			    	inssql.append("B0110  from (");
			    	inssql.append(sql);
				    inssql.append(") myset");
		    	}
	    		else if("3".equals(type))
	    		{
		    		inssql.append("E01A1)");
		    		inssql.append(" select ");
		    		inssql.append("E01A1  from (");
		    		inssql.append(sql);
		    		inssql.append(") myset");				
	    		}
	    		else 
	    		{
		    		inssql.append("A0100)");
		    		inssql.append(" select ");
		    		inssql.append("A0100  from (");
		    		inssql.append(sql);
		    		inssql.append(") myset");				
	    		}
		    	//DbWizard dbWizard = new DbWizard(this.getFrameconn()); //如果不存在表 就创建 
		    	//if(!dbWizard.isExistTable(tablename,false)){
		    	//	UserObjectBo userObjectBo = new UserObjectBo(this.frameconn);
				//	userObjectBo.createResultTable(dbpre,type,this.userView.getUserName());
				//	dao.update("delete from "+tablename);			
		    	//	dao.update(inssql.toString());
		    	//}else{		    		
		    		dao.update("delete from "+tablename);			
		    		dao.update(inssql.toString());
		    	//}
	    	}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(ex);
	    	}
		}
	}
    
	/**
	 * 取得需要填充的指标串
	 * @param strInfkind
	 * @param fields
	 * @return
	 */
	private String getFillOutFields(String strInfkind,String fields) {
		if("2".equals(strInfkind))
        {
	        if(fields==null|| "".equals(fields))
	        	fields="b0110,";        	
	        else
	        	fields="b0110,"+fields+",";        	
        }
        else if("3".equals(strInfkind))
        {
	        if(fields==null|| "".equals(fields))
	        	fields="e01a1,";        	
	        else
	        	fields="e01a1,"+fields+",";        	
        }
        else
        {
	        if(fields==null|| "".equals(fields))
	        	fields="a0100,b0110,e0122,e01a1,a0101,";        	
	        else
	        	fields="a0100,b0110,e0122,e01a1,a0101,"+fields+",";
        }
		return fields;
	}
	
	/**
	 * 取得定义的主集指标
	 * @return
	 */
	private ArrayList getMainFieldList(String flag)
	{
		ArrayList mainset=new ArrayList();		
		/**取得人员主集已定义的指标*/
		if("1".equals(flag))
		{
			SaveInfo_paramXml infoxml=new SaveInfo_paramXml(this.getFrameconn());
			mainset=infoxml.getMainSetFieldList();
			/**如果未定义，则固定四项指标，单位、部门、职位以及姓名*/
			if(mainset.size()==0)
			{
				mainset.add(DataDictionary.getFieldItem("b0110"));
				mainset.add(DataDictionary.getFieldItem("e0122"));
				mainset.add(DataDictionary.getFieldItem("e01a1"));
				mainset.add(DataDictionary.getFieldItem("a0101"));
			}			
			for(int i=0;i<mainset.size();i++)
			{
				FieldItem fielditem=(FieldItem)mainset.get(i);
				String fieldname=fielditem.getItemid();
				fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			}

		}
		return mainset;
	}
    /**
     * 
     * @param fieldlist
     * @param flag 1:人员2：单位3：职位
     * @return
     */
	private ArrayList privFieldList(ArrayList fieldlist,String flag) {
		ArrayList list=new ArrayList();


		/**权限分析*/
		for(int j=0;j<fieldlist.size();j++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(j);
			String fieldname=fielditem.getItemid();
			//System.out.println("--->fieldname="+fieldname);
			/**为空则不用显示*/
            if(fielditem.getValue()==null|| "".equals(fielditem.getValue()))
                continue; 			
			if("e01a1".equals(fieldname)&& "3".equals(flag))
				continue;
			else if("b0110".equals(fieldname)&& "2".equals(flag))
				continue;
			else 
			{
				if("b0110,e0122,e01a1,a0101".indexOf(fieldname)!=-1)
					continue;
			}
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			list.add(fielditem);
		}

		
		return list;
	}  
	
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            String value = item.getValue();
            String viewvalue = item.getViewvalue();
            value = PubFunc.hireKeyWord_filter_reback(value);
            viewvalue = PubFunc.hireKeyWord_filter_reback(viewvalue);
            item.setValue(value);
            item.setViewvalue(viewvalue);
        }
	}

	/**
	 * 安全：验证人员库参数是否合法
	 * @param nbase 人员库
	 * @throws GeneralException
	 */
	private void validateNbase(String nbase) throws GeneralException {
		if (StringUtils.isBlank(nbase)) {
			throw new GeneralException("人员库不能为空！");
		}
		boolean nbaseExist = false;
		String aNbase = "";

		ArrayList dbPreList = DataDictionary.getDbpreList();
		for (int i=0; i<dbPreList.size(); i++) {
			aNbase = (String)dbPreList.get(i);
			if (aNbase.equalsIgnoreCase(nbase)) {
				nbaseExist = true;
				break;
			}
		}
		if (!nbaseExist) {
			throw new GeneralException("系统中不存在人员库" + nbase + "！");
		}
	}
}
