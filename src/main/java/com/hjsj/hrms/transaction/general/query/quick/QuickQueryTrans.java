/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.quick;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.tools.ant.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:QuickQueryTrans</p>
 * <p>Description:快速查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-26:14:31:56</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class QuickQueryTrans extends IBusiness {
	/**是否跨库查询*/
	private boolean bm_dbase=false;
    /**分析字符串是否为数值型*/
    private boolean isnumber(String strvalue)
    {
        boolean bflag=true;
        try
        {
            Float.parseFloat(strvalue);
        }
        catch(NumberFormatException ne)
        {
            bflag=false;
        }
        return bflag;
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
    /**分析日期型字段*/
    private int analyFieldDate(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos)
    {
        String s_str_date=item.getValue();
        String e_str_date=item.getViewvalue();
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
	            strfactor.append("=");
	            strfactor.append("`");
	            return 1;
	        }
        }
        catch(Exception ex)
        {
        	return 1;
        }
    }
    
    /**
     * @param like 糊模查询
     * @param strExpr
     * @param strFactor
     * @throws GeneralException 
     */
    private void parseQueryCond(ArrayList list,String like,StringBuffer strexpr,StringBuffer strfactor) throws GeneralException
    {
        int j=1;

        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            /**如果值未填的话，default是否为不查*/
            if((item.getValue()==null|| "".equals(item.getValue()))&&(!"D".equals(item.getItemtype())))
                continue;
            if(("".equals(item.getValue())&& "".equals(item.getViewvalue()))&&("D".equals(item.getItemtype())))
                continue; 
            
            if("D".equals(item.getItemtype()))
            {
            	int sf=analyFieldDate(item,strexpr,strfactor,j);
                if(sf==1)
                {
                	throw new GeneralException("输入的日期格式错误或范围不完整！");
                }
                j=j+sf;
            }
            else
            {
	           
	            
	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
	            {
	                    if("1".equals(like))
	                    {
			                strfactor.append(item.getItemid().toUpperCase());
			                strfactor.append("=*");
			                strfactor.append(PubFunc.getStr(item.getValue()));
			                strfactor.append("*`");	   
	                    }
	                    else
	                    {
	                    	if(PubFunc.getStr(item.getValue()).indexOf("｜")!=-1){//changxy 20161024   23115 中国国际电视总公司：花名册中快速查询功能，用竖线“|”一次查找多个人的功能丢失
	                    		String[] str=PubFunc.getStr(item.getValue()).split("｜");
	                    		String exp = "(";
	                    		if(j!=1)
	                    			exp = "*(";
	                    		for (int k = 0; k < str.length; k++) {
	                    			strfactor.append(item.getItemid().toUpperCase());
		                    		strfactor.append("=");
		                    		strfactor.append(str[k]);
		                    		strfactor.append("`");
		                    		if(k<str.length-1)
		                    			exp+=j+"+";
		                    		else
		                    			exp+=j;
		                    		j++;
								}
	                    		exp+=")";
	                    		strexpr.append(exp);
	                    		continue;
	                    	}else{
	                    		strfactor.append(item.getItemid().toUpperCase());
	                    		strfactor.append("=");
	                    		strfactor.append(PubFunc.getStr(item.getValue()));
	                    		strfactor.append("`");	  	                        
	                    	}
	                    }
	            }
	            else
	            {
	                strfactor.append(item.getItemid().toUpperCase());
	                strfactor.append("=");
	                strfactor.append(PubFunc.getStr(item.getValue()));
	                strfactor.append("`");
	            }
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
	            ++j;	            
            }
        }//for i loop end.
        cat.debug("factor="+strfactor.toString());
        cat.debug("expression="+strexpr.toString());    	
    }
    
    /**
     * 
     * @param list
     * @param like
     * @param result
     * @param history
     * @return
     */
    private String combine_SQL(ArrayList list,String like,String result,String history,String[] dbpre,String strInfr) throws GeneralException
    {
    	StringBuffer strexpr=new StringBuffer();
    	StringBuffer strfactor=new StringBuffer(); 
    	parseQueryCond(list,like,strexpr,strfactor);
    	 ArrayList resultlist=privFieldList(list,strInfr);
         /**同样根据是否填定查询值，取得查询指标*/
         String fields=query_Field(resultlist);
    	String strWhere=null;
    	String strSelect=null;
    	StringBuffer strSql=new StringBuffer();
    	boolean bresult=true;
    	boolean bhistory=true;
    	boolean likeb=true;//是否模糊，根据前台选择的参数来，原来是一直 模糊的，不知道别的地方有影响没？原来默认是全模糊，所以默认模糊吧，lizw 2011-08-10
    	if("1".equals(result))
    		bresult=false;
    	if("0".equals(history))
    		bhistory=false;
    	if(like!=null&&!"1".equals(like))
    		likeb=false;
        ArrayList fieldlist=new ArrayList();
        if(!userView.isSuper_admin()&& "1".equals(strInfr))
        {
        	for(int i=0;i<dbpre.length;i++)
        	{
               strWhere=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre[i],bhistory,bresult,fieldlist);
         	   strSelect=getSelectString(list,strInfr,dbpre[i]);
        	   strSql.append(strSelect);
        	   StringBuffer leftjoinStr=new StringBuffer("");
        	   if(fields!=null&&fields.trim().length()>0)
        	   {
        		   
        		   String[] ar=fields.split(",");
        		   for(int j=0;j<ar.length;j++)
        		   {
        			   if(ar[j]!=null&&!"".equals(ar[j]))
        			   {
        				   FieldItem fielditem = DataDictionary.getFieldItem(ar[j]);
        				   if(fielditem!=null)
        				   {
        					   if(!"A01".equalsIgnoreCase(fielditem.getFieldsetid()))
        					   {
        						   leftjoinStr.append(" left join (");
        						   leftjoinStr.append(" select * from "+dbpre[i]+fielditem.getFieldsetid()+" A ");
        						   leftjoinStr.append(" where A.i9999=(select max(B.i9999) from "+dbpre[i]+fielditem.getFieldsetid()+" B ");
        						   leftjoinStr.append(" where A.a0100=B.a0100)) "+dbpre[i]+fielditem.getFieldsetid());
        						   leftjoinStr.append(" on "+dbpre[i]+"A01.a0100="+dbpre[i]+fielditem.getFieldsetid()+".A0100 ");
        					   }
        				   }
        			   }
        		   }
        	   }
        	   if(leftjoinStr.length()>0)
        	   {
        		   strSql.append(strWhere.substring(0, 12));
        		   strSql.append(leftjoinStr.toString());
        		   strSql.append(strWhere.substring(12));
        	   }
        	   else{
            	   strSql.append(strWhere);
        	   }
        	   if(this.userView.getManagePrivCode()!=null&&this.userView.getManagePrivCode().length()>0)
         	   {
            	   if("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
            		   strSql.append(" and "+dbpre[i]+"A01.B0110 like '");
            	   else if("UM".equalsIgnoreCase(this.userView.getManagePrivCode()))
            		   strSql.append(" and "+dbpre[i]+"A01.E0122 like '");
            	   else if("@K".equalsIgnoreCase(this.userView.getManagePrivCode()))
            		   strSql.append(" and "+dbpre[i]+"A01.E01A1 like '");
             	   strSql.append(this.userView.getManagePrivCodeValue());
             	   strSql.append("%'");
         	   }
        	   strSql.append(" UNION ");                
        	}
     	    strSql.setLength(strSql.length()-7); 
//     	   strSql.append(" order by dbase desc,A0000"); 
     	    this.getFormHM().put("orderby", "order by dbase desc,A0000");
        }
        else
        {
           if("1".equals(strInfr))
           {
        	   if(dbpre==null||dbpre.length==0)
        		   throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
        	   for(int i=0;i<dbpre.length;i++)
        	   {
            	   FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre[i],bhistory,likeb,bresult,Integer.parseInt(strInfr),userView.getUserId());
            	   strWhere=factorlist.getSqlExpression();
            	   strSelect=getSelectString(list,strInfr,dbpre[i]);
            	   strSql.append(strSelect);
            	   StringBuffer leftjoinStr=new StringBuffer("");
            	   if(fields!=null&&fields.trim().length()>0)
            	   {
            		   
            		   String[] ar=fields.split(",");
            		   String subSet = null;
            		   String rightTab = null;
            		   for(int j=0;j<ar.length;j++)
            		   {
            			   if(ar[j]!=null&&!"".equals(ar[j]))
            			   {
            				   FieldItem fielditem = DataDictionary.getFieldItem(ar[j]);
            				   if(fielditem!=null)
            				   {
            					   if(!"A01".equalsIgnoreCase(fielditem.getFieldsetid()))
            					   {
            					       subSet = dbpre[i]+fielditem.getFieldsetid();
            						   leftjoinStr.append(" left join (");
            						   leftjoinStr.append(" select * from " + subSet + " A ");
            						   leftjoinStr.append(" where A.i9999=(select max(B.i9999) from " + subSet + " B ");
            						   leftjoinStr.append(" where A.a0100=B.a0100)) ");
            						   //zxj 原组出的SQL在查询历史数据情况下有错误，现分开处理
            						   if(bhistory)
            						   {
            						       rightTab = "C" + i + j;
            						       leftjoinStr.append(rightTab);
            						       leftjoinStr.append(" on "+dbpre[i]+"A01.a0100=" + rightTab + ".A0100 ");
            						   }
            						   else
            						   {
            						       leftjoinStr.append(subSet);
            						       leftjoinStr.append(" on "+dbpre[i]+"A01.a0100=" + subSet + ".A0100 ");
            						   }
            					   }
            				   }
            			   }
            		   }
            	   }
            	   if(leftjoinStr.length()>0)
            	   {
            		   strSql.append(strWhere.substring(0, 12));
            		   strSql.append(leftjoinStr.toString());
            		   strSql.append(strWhere.substring(12));
            	   }
            	   else{
                	   strSql.append(strWhere);
            	   }
            	   strSql.append(" UNION ");
        	   }
        	   strSql.setLength(strSql.length()-7);
//        	   strSql.append(" order by dbase desc ,A0000");
        	   this.getFormHM().put("orderby", "order by dbase desc,A0000");
           }
           else
           {
        	   FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),"",bhistory,likeb,bresult,Integer.parseInt(strInfr),userView.getUserId());
        	   strWhere=factorlist.getSqlExpression();
        	   if("3".equals(strInfr))
              	{
           		String str=strWhere.toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01", "K01");
           		strWhere=str;
           	   }
        	   strSelect=getSelectString(list,strInfr,"");
        	   strSql.append(strSelect);
        	   strSql.append(strWhere); 
        	   String backdate=DateUtils.format(new Date(), "yyyy-MM-dd");
        	   if("2".equals(strInfr))
        	   {
				   String conditionSql = " select codeitemid from organization  where codesetid<>'@K' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
				   if(strWhere.indexOf("where")!=-1||strWhere.indexOf("WHERE")!=-1)
				      strSql.append(" and B01.B0110 in("+conditionSql+")");
				   else
    				   strSql.append(" where B01.B0110 in("+conditionSql+")");
        	   }else
        	   {
        		   String conditionSql = " select codeitemid from organization  where codesetid='@K' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date";
        		   if(strWhere.indexOf("where")!=-1||strWhere.indexOf("WHERE")!=-1)
        		     strSql.append(" and K01.E01A1 in("+conditionSql+")");
        		   else
    				   strSql.append(" where K01.E01A1 in("+conditionSql+")");
        	   }
        	   if(!userView.isSuper_admin()){
        		   if("2".equals(strInfr))
        		   {
        			   /***
        			    * cmq changed at 20121001 for 单位和岗位权限范围控制规则优先级
        			    *业务范围->操作单位->人员范围
        			    */
        			   //strSql.append(" and B01.B0110 like '");        			  
        			   //strSql.append(this.userView.getManagePrivCodeValue());
        			   //strSql.append("%'");   
        			   strSql.append(" and ");
        			   strSql.append(this.userView.getUnitPosWhereByPriv(2));
        			   
        		   }else if("3".equals(strInfr)){
        			   //strSql.append(" and K01.E01A1 like '");        			   
        			   //strSql.append(this.userView.getManagePrivCodeValue());
        			   //strSql.append("%'");
        			   strSql.append(" and ");
        			   strSql.append(this.userView.getUnitPosWhereByPriv(3));        			   
        		   }
        	   }
        	   this.getFormHM().put("orderby", "");
			   
         	  
           }
        }
        return strSql.toString();
    }    
    
    private String getSelectString(ArrayList list,String strInfkind,String dbpre)
    {
        ArrayList resultlist=privFieldList(list,strInfkind);
        /**同样根据是否填定查询值，取得查询指标*/
        String fields=query_Field(resultlist);
        FieldItem fielditem = DataDictionary.getFieldItem(fields);
        StringBuffer colums = new StringBuffer();

        StringBuffer strsql=new StringBuffer();
        if("2".equals(strInfkind))
        {
	        strsql.append("select B01.B0110 B0110");
	        colums.append("B0110");
        }
        else if("3".equals(strInfkind))
        {
	        strsql.append("select K01.E01A1 E01A1");    
	        colums.append("E01A1");
        }
        else
        {
	        strsql.append("select "+dbpre+"A01.A0000,");
	        strsql.append(dbpre);
	        strsql.append("A01.A0100 ,'");
	        strsql.append(dbpre);
	        strsql.append("' as dbase,");
	        strsql.append(dbpre);        
	        strsql.append("A01.B0110 B0110,"+dbpre+"A01.E0122,");
	        strsql.append(dbpre);
	        strsql.append("A01.E01A1 E01A1,"+dbpre+"A01.A0101 ");    
	        colums.append("A0000,A0100,dbase,B0110,E0122,E01A1,A0101");
        }
        if(fields==null|| "".equals(fields))
        {
            strsql.append(" ");
        }
        else
        {
        	strsql.append(",");
        	if("1".equals(strInfkind)){
        		 strsql.append(dbpre);         
        	}
        	if(fielditem!=null){
        		strsql.append(fielditem.getFieldsetid()+".");
        	}else{
        		strsql.append(fields.substring(0,3)+".");
        	}
        	strsql.append(fields); 
    		colums.append(","+fields);
        }
        strsql.append(" ");
        this.getFormHM().put("cloums", colums.toString());
        return strsql.toString();
    }
    
    /**
     * 取得查询指标项目
     * @param querylist
     * @param strInfkind
     * @return
     */
    private ArrayList getFieldList(ArrayList querylist ,String strInfkind)
    {
    	ArrayList list=new ArrayList();
    	Field temp=null;
		if("1".equals(strInfkind))
		{
				temp=new Field("dbase",ResourceFactory.getProperty("label.dbase"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(true);
			    temp.setVisible(this.bm_dbase);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				temp.setLength(30);
				temp.setCodesetid("@@");
				list.add(temp);		
				
				temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(true);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				temp.setLength(30);
				list.add(temp);
				temp=new Field("a0000",ResourceFactory.getProperty("recidx.label"));
				temp.setDatatype(DataType.INT);
				temp.setKeyable(false);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				list.add(temp);	
				temp=new Field("b0110",ResourceFactory.getProperty("lable.statistic.companyname"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(true);
			    temp.setNullable(false);
			    temp.setSortable(false);
			    temp.setLength(50);
			    temp.setCodesetid("UN");
				list.add(temp);			
				FieldItem item=DataDictionary.getFieldItem("e0122");
				temp=item.cloneField();
				temp.setVisible(true);
			    temp.setSortable(true);	
				temp.setKeyable(false);
			    temp.setNullable(false);
			    temp.setLength(50);
			    temp.setCodesetid("UM");
				list.add(temp);	
				
				temp=new Field("e01a1",ResourceFactory.getProperty("e01a1.label"));
				temp.setDatatype(DataType.STRING);
				temp.setKeyable(false);
			    temp.setVisible(true);
			    temp.setNullable(false);
			    temp.setSortable(true);
			    temp.setLength(50);
			    temp.setCodesetid("@K");
				list.add(temp);	
				
				item=DataDictionary.getFieldItem("a0101");
				temp=item.cloneField();
				temp.setVisible(true);
			    temp.setSortable(true);	
				temp.setKeyable(false);	
			    temp.setNullable(false);	
				list.add(temp);				    
		}
		else if("2".equals(strInfkind))
		{
			temp=new Field("b0110",ResourceFactory.getProperty("lable.statistic.companyname"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(false);
		    temp.setLength(50);
		    temp.setCodesetid("UN");
			list.add(temp);			
		}
		else
		{
			temp=new Field("e01a1",ResourceFactory.getProperty("column.sys.pos"));
			temp.setDatatype(DataType.STRING);
			temp.setKeyable(true);
//		    temp.setVisible(true);
		    temp.setNullable(false);
		    temp.setSortable(true);
		    temp.setLength(50);
		    temp.setCodesetid("@K");
			list.add(temp);				
		}
        ArrayList resultlist=privFieldList(querylist,strInfkind);
        /**同样根据是否填定查询值，取得查询指标*/
        String fields=query_Field(resultlist);
        
    	for(int i=0;i<querylist.size();i++)
    	{
    		FieldItem item=(FieldItem)querylist.get(i);
    		if(fields.indexOf(item.getItemid())==-1)
    			continue;
    		Field field = item.cloneField();
    		list.add(field);
    	}
    	return list;
    }
    
	public void execute() throws GeneralException {
        ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
        String like=(String)this.getFormHM().get("like");
        String result=(String)this.getFormHM().get("result");
        String history=(String)this.getFormHM().get("history");
        String[] dbpre=(String[])this.getFormHM().get("dbpre");
        if(list==null||list.size()==0)
        	throw new GeneralException(ResourceFactory.getProperty("errors.query.notexistfield"));
        /**=1人员,=2单位,=3职位*/
        String strInfkind=(String)this.getFormHM().get("type");
        if(strInfkind==null|| "".equals(strInfkind))
            strInfkind="1";
        if((dbpre==null||dbpre.length==0)&& "1".equals(strInfkind))
        	throw new GeneralException(ResourceFactory.getProperty("errors.static.notdbname"));
        if(like==null|| "".equals(like))
            like="0";
        String strsql=combine_SQL(list,like,result,history,dbpre,strInfkind);
        strsql=strsql.replaceAll("\"","\\\\\"");
        /**跨库标识*/
        if(dbpre.length>1)
        	this.bm_dbase=true;
        cat.debug("Module query's sql="+strsql);    
        this.getFormHM().put("sql",strsql);
        
        /**结果集名称*/
		this.getFormHM().put("setname","result");
		/**把查询结果指标-->显示的数据格式*/
		this.getFormHM().put("showlist",getFieldList(list,strInfkind));

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
	
}
