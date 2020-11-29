package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * <p>Title:SimpleQueryTrans</p>
 * <p>Description:简单查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:10:34:40 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SimpleQueryTrans extends IBusiness {

    /**
     * 
     */
    public SimpleQueryTrans() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * 分析表达式的合法式
     * @param expression
     * @param nmax　最大表达式因子号
     * @return
     */
    private boolean isHaveExpression(String expression,int nmax)
    {
        boolean bflag=true;
        String strlastno="";
        int ncurr=0;
        for(int i=0;i<expression.length();i++)
        {
          char v =expression.charAt(i);
          if(((i+1)!=expression.length())&&(v>='0'&&v<='9'))
          {
            strlastno=strlastno+v;
          }
          else
          {
            if(v>='0'&&v<='9')
            {
              strlastno=strlastno+v;
            }
            if(!"".equals(strlastno))
            {
              ncurr=Integer.parseInt(strlastno);
              if(ncurr>nmax)
              {
                  bflag=false;
                  break;
              }
            }
            strlastno="";
          }
        }        
        return bflag;
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
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
        String query_type=(String)this.getFormHM().get("query_type");
        String expression=(String)this.getFormHM().get("expression");
        expression=PubFunc.hireKeyWord_filter_reback(expression);
        if(expression.endsWith("*") || expression.endsWith("!") || expression.endsWith("+"))
            expression = expression.substring(0, expression.length() -1);
        
        String qobj=(String)this.getFormHM().get("qobj");
        /**查询类型，简单查询或通用查询*/
        if(query_type==null|| "".equals(query_type))
            query_type="1";
        if(factorlist==null)
            return;
        /**default 为人员库*/        
        String type=(String)this.getFormHM().get("type");
        if(type==null|| "".equals(type))
        	type="1";
        rebackKeyword(factorlist);
        //String type="1";
        String dbpre=(String)this.getFormHM().get("dbpre");
        String history=(String)this.getFormHM().get("history");
        String like=(String)this.getFormHM().get("like");
        String result=(String)this.getFormHM().get("result");
        if(history==null|| "".equals(history))
            history="0";
        if(like==null|| "".equals(like))
            like="0";
    	boolean blike=false;
    	//liuy 2015-4-15 8763：花名册应该只能看见19个人，可是简单查询，杨模糊查询，能查出100多条（模糊授权未按高级授权走） begin
    	//if(like.equals("1"))
    		//blike=true;
    	//liuy 2015-4-15 end
        boolean bresult=true;
    	if("1".equals(result))
    		bresult=false;           
        cat.debug("history="+history);
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
        /**合成通用的表达式*/
        for(int i=0;i<factorlist.size();i++)
        {
            Factor factor=(Factor)factorlist.get(i);
            if(i!=0)
            {
            	factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
                sexpr.append(factor.getLog());
            }
            sexpr.append(i+1);
            sfactor.append(factor.getFieldname().toUpperCase());            
            sfactor.append(PubFunc.keyWord_reback(factor.getOper()));
            String q_value=factor.getValue().trim();
            q_value = PubFunc.getStr(q_value);
            if(("0".equalsIgnoreCase(factor.getCodeid()))&& "1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
            {
            	if(!(q_value==null|| "".equals(q_value)))
            		sfactor.append("*");            
            }
            /**对字符型指标有模糊
             *针对字符型指标，前后都加上* 20100203 cmq changed 
             */
            if("1".equals(like)&&("A".equals(factor.getFieldtype())&&("0".equalsIgnoreCase(factor.getCodeid()))))
            {
            	if(!(q_value==null|| "".equals(q_value)))
            		sfactor.append("*");
            }            
            sfactor.append(PubFunc.getStr(factor.getValue())); 
            if("M".equals(factor.getFieldtype()))
            {
            	throw new GeneralException(factor.getHz()+" "+ResourceFactory.getProperty("error.query.factor"));
            }
            if("D".equals(factor.getFieldtype()))
            {
            	/*boolean isCorrect=false;
            	isCorrect=q_value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
            	if(!isCorrect)
            		isCorrect=q_value.matches("[0-9]{4}[#-.][0-9]{2}");
            	if(!isCorrect)
            		isCorrect=q_value.matches("[0-9]{4}");
            	if(!isCorrect)
            		throw new GeneralException(factor.getHz()+" "+ResourceFactory.getProperty("error.query.factor.date.format"));*/
            }
            /**对字符型指标有模糊*/
            if("1".equals(like)&&("A".equals(factor.getFieldtype())))
            {
            	if(!(q_value==null|| "".equals(q_value)))
            		sfactor.append("*");
            }
            sfactor.append("`");            
        }
        
        /**查询对象不是人员时，库前缀为空*/
        if(!"1".equals(type))
            dbpre="";
        /**通用查询时，表达式因子按用户填写进行分析处理*/
        if("2".equals(query_type))
        {
            sexpr.setLength(0);
            if(expression==null|| "".equals(expression))
               throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
            /**为了分析用*/
            if(!isHaveExpression(expression,factorlist.size()))
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
            expression=expression.replaceAll("!","-");
            TSyntax syntax=new TSyntax();
            if(!syntax.Lexical(expression))
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
            if(!syntax.DoWithProgram())
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
            sexpr.append(expression);
        }
        /**通用查询结束**/
        cat.debug("expr="+sexpr.toString());
        cat.debug("factor="+sfactor.toString());
        /**表过式分析*/
        /**非超级用户且对人员库进行查询*/
        String strwhere="";
        ArrayList fieldlist=new ArrayList();
        boolean bhis=false;
        if("1".equals(history))
        	bhis=true;
        fieldlist=privFieldList(fieldlist,type,history);
        String filterfield=getFilterFields(fieldlist,history);
        
        String columns="";
        StringBuffer strsql=new StringBuffer();
        StringBuffer distinct=new StringBuffer();
        /**1人员　2:单位 3:职位*/
        if("1".equals(type))
        {
        	/*
            strsql.append("select distinct a0000,");
            strsql.append(dbpre);
            strsql.append("a01.a0100, ");
            strsql.append(dbpre);            
            strsql.append("a01.b0110 b0110,");
            strsql.append(dbpre);
            strsql.append("a01.e0122 e0122,");            
            strsql.append(dbpre);                
            strsql.append("a01.e01a1 e01a1,a0101 ");
            strsql.append(" ");
            */
            FieldItem itemtemp = new FieldItem();
            itemtemp.setFieldsetid("");
            itemtemp.setItemdesc("人员库");
            itemtemp.setItemid("dbname");
            itemtemp.setPriv_status(1);
            itemtemp.setItemtype("A");
            itemtemp.setCodesetid("0");
            
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
            
        	if(dbpre!=null&& "All".equals(dbpre))
        	{
        		ArrayList dblist=this.userView.getPrivDbList();
        		if(dblist==null||dblist.size()<=0)
            	{
            		throw new GeneralException("没有人员库权限！");
            	}
        		StringBuffer bursql=new StringBuffer();        		
        		for(int i=0;i<dblist.size();i++)
            	{
            		 String nbase=dblist.get(i).toString();
            		 FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),nbase,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
            		 factorslist.setSuper_admin(userView.isSuper_admin());
            		 fieldlist=factorslist.getFieldList();
            		 strwhere=factorslist.getSqlExpression();
            		 
            		 String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,nbase,privCodeValue,true,kind,"org","","All");
            		 if(StringUtils.isNotEmpty(term_Sql))
            			 strwhere = strwhere.replaceFirst("FROM " + nbase + "A01", "FROM (" + term_Sql + ") " + nbase + "A01");
                     
            		 fieldlist=getMainFieldList(type);
             		 filterfield=getMainQueryFields(fieldlist);
             		 String dbNameSql="select distinct (select dbname from dbname WHERE pre ='"+nbase+"'  ) as dbname,'"+nbase+"' as nbase, "+nbase+"A01.a0100 "+" "+strwhere;
                     if(!bresult)
                        filterQueryResult(type,nbase,dbNameSql);
                     else
                        saveQueryResult(type,nbase,dbNameSql);    
                     
                     bursql.append("select "+i+" as i, (select dbname from dbname WHERE pre ='"+nbase+"'  ) as dbname,  '"+nbase+"' as nbase,"+nbase+"A01.a0100 as A0100,"+nbase+"A01.a0000 as a0000,"+nbase+"A01.b0110 as b0110,"+nbase+"A01.e0122 as e0122,"+nbase+"A01.e01a1 as e01a1 ");
            		 if(!(filterfield==null|| "".equals(filterfield)))
                     {
                     	bursql.append(",");
            			bursql.append(filterfield);
                     } 
            		 bursql.append(strwhere);
            		 bursql.append(" union all ");
            	}    
        		
        		/**存在不同字段*/
                
        		bursql.setLength(bursql.length()-11);
        		strsql.append(bursql);
        		fieldlist.add(0,itemtemp);
        		columns="nbase,a0000,a0100,b0110,e0122,e01a1,"+filterfield;
        		strwhere="";
                distinct.append("");
                this.getFormHM().put("order", "order by i,A0000");
        	}else
        	{
        		FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
        		fieldlist=factorslist.getFieldList();
        		factorslist.setSuper_admin(userView.isSuper_admin());
        		strwhere=factorslist.getSqlExpression();
        		
        		String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,dbpre,privCodeValue,true,kind,"org","","All");
        		if(StringUtils.isNotEmpty(term_Sql))
        			strwhere = strwhere.replaceFirst("FROM " + dbpre + "A01", "FROM (" + term_Sql + ") " + dbpre + "A01");
                
            	fieldlist=getMainFieldList(type);

            	strsql.append("select distinct a0000,(select dbname from dbname WHERE pre ='"+dbpre+"'  ) as dbname,'"+dbpre+"' as nbase,");
    	        strsql.append(dbpre);
    	        strsql.append("a01.a0100 ,");
    	        strsql.append(dbpre);        
    	        strsql.append("a01.b0110 b0110,");
                strsql.append(dbpre);
                strsql.append("a01.e01a1 e01a1,");	
                strsql.append(dbpre);
                strsql.append("a01.e0122 e0122");	            
                filterfield=getMainQueryFields(fieldlist);
                fieldlist.add(0,itemtemp);

                columns="nbase,a0100,b0110,e0122,e01a1,a0101,"+filterfield;
                distinct.append(" ");
                distinct.append(dbpre);
                distinct.append("a01.a0100");
        		if(!bresult)
        	        filterQueryResult(type,dbpre,strsql.toString()+" "+strwhere);
        	    else
        	        saveQueryResult(type,dbpre,strsql.toString()+" "+strwhere);    
        		 /**存在不同字段*/
        	    if(!(filterfield==null|| "".equals(filterfield)))
        	    {
        	        	columns=columns+filterfield+",";
        	        	strsql.append(",");
        	        	strsql.append(filterfield);
        	    } 
        	    this.getFormHM().put("order", "order by A0000");
        	}
        	
        }
        else if("2".equals(type))
        {
        	FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
        	factorslist.setSuper_admin(userView.isSuper_admin());
        	fieldlist=factorslist.getFieldList();
            fieldlist=privFieldList(fieldlist,type,history);
            strwhere=factorslist.getSqlExpression();
            strsql.append("select distinct b01.b0110 b0110 ");
            strsql.append(" ");
            filterfield=getMainQueryFields(fieldlist);
            columns="b0110,";   
            distinct.append("B01.B0110");  
            /**存在不同字段*/
            if(!(filterfield==null|| "".equals(filterfield)))
            {
            	columns=columns+filterfield+",";
            	strsql.append(",");
            	strsql.append(filterfield);
            } 
        }
        else if("3".equals(type))
        {
        	FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
            fieldlist=factorslist.getFieldList();
            factorslist.setSuper_admin(userView.isSuper_admin());
            fieldlist=privFieldList(fieldlist,type,history);
            strwhere=factorslist.getSqlExpression();
            strsql.append("select distinct k01.e01a1 e01a1");
            strsql.append(" ");
            filterfield=getMainQueryFields(fieldlist);
            columns="e01a1,";
            distinct.append("K01.E01A1");  
            /**存在不同字段*/
            if(!(filterfield==null|| "".equals(filterfield)))
            {
            	columns=columns+filterfield+",";
            	strsql.append(",");
            	strsql.append(filterfield);
            } 
        }               
        if("2".equals(type)&&(!"0".equals(qobj)))
        {
        	if(strwhere.length()>0)
        		strwhere=strwhere+" and "+getQueryObjWhere(qobj);
        }     

        /**对单位还得加上管理范围*/
        if("2".equals(type)|| "3".equals(type))
        {
        	String filtercond=getUnitPosFilterCond(Integer.parseInt(type));
        	if(filtercond.length()!=0)
        		strwhere=strwhere+" and "+filtercond;
        	 if(!bresult)
             	filterQueryResult(type,dbpre,strsql.toString()+" "+strwhere);
             else
             	saveQueryResult(type,dbpre,strsql.toString()+" "+strwhere);    
        }
        /**保存查询结果*/
        this.getFormHM().put("cond_sql",strsql.toString());
        this.getFormHM().put("columns","dbname,nbase,"+columns);
        this.getFormHM().put("strwhere",strwhere);            
        this.userView.getHm().put("staff_sql",SafeCode.encode(strwhere));//liuy 2014-10-31 员工管理查询sql            
        this.getFormHM().put("type",type);
        this.getFormHM().put("resultlist",fieldlist);
        this.getFormHM().put("keys", distinct.toString());
        this.getFormHM().put("distinct",distinct.toString());
        
        /**浏览信息用的卡片*/
        this.getFormHM().put("tabid", searchCard(type));        
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
            if("M".equalsIgnoreCase(item.getItemtype()))
                continue;
            
			if("b0110,e01a1,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
				continue;  
            if(j!=0)
                strfields.append(",");
            ++j;
          
            strfields.append(item.getItemid());
        }
        return strfields.toString();    	
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
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
				    mainset.remove(i);
				
				String fieldname=fielditem.getItemid();
				fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			}

		}else if("2".equals(flag)){
			
			mainset.add(DataDictionary.getFieldItem("b0110"));
		}else if("3".equals(flag)){
			mainset.add(DataDictionary.getFieldItem("e01a1"));
		}
		return mainset;
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
		    		inssql.append(" select DISTINCT ");
		    		inssql.append("B0110  from (");
		    		inssql.append(sql);
			    	inssql.append(") myset");
		  	    }
	    		else if("3".equals(type))
	    		{
	    			inssql.append("E01A1)");
	    			inssql.append(" select DISTINCT ");
	    			inssql.append("E01A1  from (");
		    		inssql.append(sql);
		    		inssql.append(") myset");				
	    		}
	    		else 
	    		{
	    			inssql.append("A0100)");
	    			inssql.append(" select DISTINCT ");
	    			inssql.append("A0100  from (");
		     		inssql.append(sql);
		    		inssql.append(") myset");				
	    		}
	    		dao.update("delete from "+tablename);
	    		dao.update(inssql.toString());
    		}
	     	catch(Exception ex)
	    	{
	     		ex.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(new GeneralException(/*"因子表达式错误"*/ex.getMessage()));
	    	}
		}
	}

    
    /**
     * 取得查询条件中的指标和固定指标不同的项目串
     * @param list
     * @return
     */
    private String getFilterFields(ArrayList list,String history)
    {
    	StringBuffer strfield=new StringBuffer();
    	for(int i=0;i<list.size();i++)
    	{
    		FieldItem item=(FieldItem)list.get(i);
    		if("1".equals(history)&&(!item.isMainSet()))
    			continue;	    		
    		strfield.append(item.getItemid().toLowerCase());
    		strfield.append(",");
    	}
    	if(strfield.length()>0)
    		strfield.setLength(strfield.length()-1);
    	return strfield.toString();
    }
    
    /**
     * @param fieldlist
     * @param flag 1:人员2：单位3：职位
     * @return
     */
	private ArrayList privFieldList(ArrayList fieldlist,String flag,String history) {
		ArrayList list=new ArrayList();
		/**权限分析*/
		for(int j=0;j<fieldlist.size();j++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(j);
    		if("1".equals(history)&&(!fielditem.isMainSet()))
    			continue;			
			String fieldname=fielditem.getItemid();
			if("e01a1".equals(fieldname)&& "3".equals(flag))
				continue;
			else if("b0110".equals(fieldname)&& "2".equals(flag))
				continue;
			else 
			{
				if("b0110,e0122,e01a1,a0101".indexOf(fieldname)!=-1)
					continue;
			}
			cat.debug("priv_field="+fieldname);
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname.toUpperCase())));
			list.add(fielditem);
		}
		return list;
	}    
	
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++){
			Factor factor = (Factor)list.get(i);
			String hz = factor.getHz();
			String oper = factor.getOper();
			String log = factor.getLog();
			String value = factor.getValue();
			String hzvalue = factor.getHzvalue();
			hz = PubFunc.hireKeyWord_filter_reback(hz);
			oper = PubFunc.hireKeyWord_filter_reback(oper);
			log = PubFunc.hireKeyWord_filter_reback(log);
			value = PubFunc.hireKeyWord_filter_reback(value);
			hzvalue = PubFunc.hireKeyWord_filter_reback(hzvalue);
			factor.setHz(hz);
			factor.setOper(oper);
			factor.setLog(log);
			factor.setValue(value);
			factor.setHzvalue(hzvalue);
		}
	}
}
