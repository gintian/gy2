package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * <p>Title:GeneralQueryTrans</p>
 * <p>Description:常用条件查询</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 18, 2005:2:58:36 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class GeneralQueryTrans extends IBusiness {

    /**
     * 
     */
    public GeneralQueryTrans() {
        super();
        // TODO Auto-generated constructor stub
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
        String curr_id[]=(String[])this.getFormHM().get("curr_id");
        String ret=null;
        if(curr_id==null||curr_id.length==0)
            return;
        
        //权限检查
        if (!this.userView.isHaveResource(IResourceConstant.LEXPR, curr_id[0]))
          return;
                
        String like=(String)this.getFormHM().get("like");//模糊查询
        String dbpre=(String)this.getFormHM().get("dbpre");
        String history=(String)this.getFormHM().get("history");
        String result=(String)this.getFormHM().get("result");
        String qobj=(String)this.getFormHM().get("qobj");        
        if(history==null|| "".equals(history))
            history="0";
    	boolean bresult=true;
    	if("1".equals(result))
    		bresult=false;         
        String fields=null;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("lexpr");
        vo.setString("id",curr_id[0]);
        
        String lexprName = "";
        ArrayList fieldlist=null;
        try
        {
        	String sqlwy="select history,name from lexpr where id= '"+curr_id[0]+"'";
        	this.frowset =dao.search(sqlwy);
        	while(this.frowset.next()){
        		ret = this.frowset.getString(1);
        		lexprName = this.frowset.getString(2);
        	}
        	
        	if(ret==null|| "".equals(ret))
        		ret="0";
        	
        	if(!ret.equalsIgnoreCase(history)){
        		if(!"0".equalsIgnoreCase(ret)&& "0".equalsIgnoreCase(history)){
        			history=ret;
        		}
        	}
            vo=dao.findByPrimaryKey(vo);
            String expr=vo.getString("lexpr");
            String factor=vo.getString("factor");
            expr=PubFunc.keyWord_reback(expr);
            factor=PubFunc.keyWord_reback(factor);
            String type=vo.getString("type");
            String fuzzy=vo.getString("fuzzyflag");
            if(fuzzy==null|| "".equals(fuzzy))
            	fuzzy="0";
            boolean blike=false;
            //liuy 2015-4-15 8763：花名册应该只能看见19个人，可是简单查询，杨模糊查询，能查出100多条（模糊授权未按高级授权走） begin
            if("1".equals(fuzzy)||(like!=null&& "1".equals(like)))
            	factor = addWildcardCharacter(factor);
            //liuy 2015-4-15 end
            /**查询对象不是人员时，库前缀为空*/
            if(!"1".equals(type))
                dbpre="";
            cat.debug("expr="+expr);
            cat.debug("factor="+factor);
            factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");  /*兼容报表管理、常用查询*/
            /**表过式分析*/
            String strwhere="";
            boolean bhis=false;
            if("1".equals(history))
            	bhis=true;            
            /**非超级用户且对人员库进行查询*/
            fieldlist=new ArrayList();            
                                  
           
            String filterfield="";
            StringBuffer strsql=new StringBuffer();
            /**1人员　2:单位 3:职位*/
            if("1".equals(type)) {
                FieldItem itemtemp = new FieldItem();
                itemtemp.setFieldsetid("");
                itemtemp.setItemdesc("人员库");
                itemtemp.setItemid("dbname");
                itemtemp.setPriv_status(1);
                itemtemp.setItemtype("A");
                itemtemp.setCodesetid("0");
            	 /**权限分析及过滤*/
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
                 
            	if(dbpre!=null&& "All".equals(dbpre)) {
            		ArrayList dblist=this.userView.getPrivDbList();
            		if(dblist==null||dblist.size()<=0)
                	{
                		throw new GeneralException("没有人员库权限！");
                	}
            		StringBuffer bursql=new StringBuffer();   
            		
            		for(int i=0;i<dblist.size();i++)
                	{
            			 String nbase=dblist.get(i).toString();
            			 FactorList factorlist=new FactorList(expr,factor,nbase,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
            			 fieldlist=factorlist.getFieldList();
            			 factorlist.setSuper_admin(userView.isSuper_admin());
            			 strwhere=factorlist.getSqlExpression();
            			 
            			 String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,nbase,privCodeValue,true,kind,"org","","All");
                         if(StringUtils.isNotEmpty(term_Sql))
                        	 strwhere = strwhere.replaceFirst("FROM " + nbase + "A01", "FROM (" + term_Sql + ") " + nbase + "A01");
                         
            			 fieldlist=privFieldList(fieldlist,type,history);
                         filterfield=getFilterFields(fieldlist,history);
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
            		bursql.setLength(bursql.length()-11);
            		strsql.append(bursql);
    		        fields="a0000,nbase,A0100,b0110,e0122,e01a1,a0101,"+filterfield;
                    fieldlist.add(0,itemtemp);
    		        /**在查询因子项目中存在不同字段*/
    	            if(!(filterfield==null|| "".equals(filterfield)))
    	            {
    	            	fields=fields+filterfield+",";    	            	
    	            }
    		        this.getFormHM().put("distinct","");
                    this.getFormHM().put("keys",""); 
                    this.getFormHM().put("order", "order by i,A0000");
                    strwhere="";
            	}else
            	{
            		FactorList factorlist=new FactorList(expr,factor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
            		fieldlist=factorlist.getFieldList();
            		strwhere=factorlist.getSqlExpression();
            		
            		 String term_Sql = infoUtils.getWhereSQLExists(this.frameconn,this.userView,dbpre,privCodeValue,true,kind,"org","","All");
                     if(StringUtils.isNotEmpty(term_Sql))
                    	 strwhere = strwhere.replaceFirst("FROM " + dbpre + "A01", "FROM (" + term_Sql + ") " + dbpre + "A01");
                     
            		fieldlist=privFieldList(fieldlist,type,history);
                    filterfield=getFilterFields(fieldlist,history);
                	fieldlist=getMainFieldList(type);
                
    		        strsql.append("select distinct a0000,(select dbname from dbname WHERE pre ='"+dbpre+"'  ) as dbname,'"+dbpre+"' as nbase,");
    		        strsql.append(dbpre);
    		        strsql.append("a01.a0100 ,");
    		        strsql.append(dbpre);        
    		        strsql.append("a01.b0110 b0110,");
    	            strsql.append(dbpre);
    	            strsql.append("a01.e01a1 e01a1,");		//chenmengqign e0122,->e01a1
    	            strsql.append(dbpre);
    	            strsql.append("a01.e0122 e0122");	            
    	            filterfield=getMainQueryFields(fieldlist);
    		        fields="a0100,b0110,e0122,e01a1,a0101,"+filterfield;
                    fieldlist.add(0,itemtemp);
                    
    		        if(bresult)
    	                /**保存查询结果*/
    	                saveQueryResult(type,dbpre,strsql.toString()+" "+strwhere);
    	            else
    	                filterQueryResult(type,dbpre,strsql.toString()+" "+strwhere);
                    this.getFormHM().put("distinct"," "+dbpre+"a01.a0100");
                    this.getFormHM().put("keys",dbpre+"a01.a0100");  
                    this.getFormHM().put("order", "order by A0000");
                    /**在查询因子项目中存在不同字段*/
                    if(!(filterfield==null|| "".equals(filterfield)))
                    {
                    	fields=fields+filterfield+",";
                    	strsql.append(",");
                    	strsql.append(filterfield);
                    }
            	}  
            	
            	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
                String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
            	if(uplevel==null||uplevel.length()==0)
            		uplevel="0";
            	this.getFormHM().put("uplevel", uplevel);
            }
            else if("2".equals(type))
            {
            	strsql.append("select distinct b01.b0110 ");
                strsql.append(" ");
                fields="b0110,";    
                this.getFormHM().put("distinct","B01.B0110");  
                this.getFormHM().put("keys","B01.B0110"); 
                FactorList factorlist=new FactorList(expr,factor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
                fieldlist=factorlist.getFieldList();
                strwhere=factorlist.getSqlExpression();
                /**权限分析及过滤*/
                fieldlist=privFieldList(fieldlist,type,history);
                filterfield=getFilterFields(fieldlist,history);
                /**在查询因子项目中存在不同字段*/
                if(!(filterfield==null|| "".equals(filterfield)))
                {
                	fields=fields+filterfield+",";
                	strsql.append(",");
                	strsql.append(filterfield);
                }
            }
            else if("3".equals(type))
            {
                strsql.append("select distinct k01.e01a1 ");
                strsql.append(" ");
                fields="e01a1,";    
                this.getFormHM().put("distinct","K01.E01A1");  
                this.getFormHM().put("keys","K01.E01A1"); 
                FactorList factorlist=new FactorList(expr,factor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
                fieldlist=factorlist.getFieldList();
                strwhere=factorlist.getSqlExpression();
                /**权限分析及过滤*/
                fieldlist=privFieldList(fieldlist,type,history);
                filterfield=getFilterFields(fieldlist,history);
                /**在查询因子项目中存在不同字段*/
                if(!(filterfield==null|| "".equals(filterfield)))
                {
                	fields=fields+filterfield+",";
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
            	{
            		strwhere=strwhere+" and "+filtercond;
            	}
            	if(bresult)
                    /**保存查询结果*/
                    saveQueryResult(type,dbpre,strsql.toString()+" "+strwhere);
                else
                    filterQueryResult(type,dbpre,strsql.toString()+" "+strwhere);
            } 
            cat.debug("Common query's sql="+strsql.toString());
            //System.out.println(strsql.toString());
          
            /**应用库过滤前缀符号*/
            ArrayList dbnewlist=userView.getPrivDbList();
            //人员库过滤sql语句
            StringBuffer cond = new StringBuffer();
            cond.append("select pre,dbname from dbname where pre in (");
            for (int i = 0; i < dbnewlist.size(); i++) {
                if (i != 0) cond.append(",");
                cond.append("'");
                cond.append((String) dbnewlist.get(i));
                cond.append("'");
            }
            if (dbnewlist.size() == 0) 
            	cond.append("''");
            cond.append(")");
            cond.append(" order by dbid");
            /**应用库前缀过滤条件*/
            this.getFormHM().put("dbcond",cond.toString());
            this.getFormHM().put("columns","dbname,nbase,"+fields);
            this.getFormHM().put("strwhere",strwhere);            
            this.getFormHM().put("type",type);
            this.getFormHM().put("resultlist",fieldlist);   
            this.getFormHM().put("lexprName",lexprName);   
            /**浏览信息用的卡片*/
            this.getFormHM().put("tabid", searchCard(type));
            this.getFormHM().put("cond_sql", strsql.toString());
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(ee);            	
        }
        
    }
    
    /**
     * 模糊查询时给因子表达式添加通配符
     * @param factor
     * @return
     */
    private String addWildcardCharacter(String factor){//模糊查询才使用此方法
    	StringBuffer strfactor =new StringBuffer();
    	String[] tmpStrs = {"<>","<=",">=","<",">","="};
    	String[] strs = factor.split("`");
    	String separator = "";
    	for(int i=0;i<strs.length;i++){
    	    String Itemid = strs[i].substring(0, 5);
    	    String tmpStr = strs[i].substring(5);
    	    for(int m = 0; m < 6; m++) {
    	        if(!tmpStr.startsWith(tmpStrs[m])) {
    	            continue;
    	        }
    	        
    	        separator = tmpStrs[m];
    	        break;
    	    }
    	    
    		FieldItem fielditem = DataDictionary.getFieldItem(Itemid);
    		String value=strs[i].substring(strs[i].indexOf(separator) + separator.length());
    		if(fielditem!=null&&("A".equals(fielditem.getItemtype())|| "M".equals(fielditem.getItemtype()))){
                if(!(value==null|| "".equals(value))){
	                strfactor.append(Itemid);
	                if(!fielditem.isCode())//判断是否代码型（不是代码型则是字符或备注型）
	                	strfactor.append(separator + "*");
	                else
	                	strfactor.append(separator);			                	
	                strfactor.append(value+"*`");
                }else
	                strfactor.append(Itemid+separator+value+"`");
            }else
            	strfactor.append(Itemid+separator+value+"`");
    	}
    	return strfactor.toString();
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
				String fieldname=fielditem.getItemid();
				fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			}

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
    		
    		AutoCreateQueryResultTable.execute(this.getFrameconn(), tablename, type);
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
	    		dao.update("delete from "+tablename);			
	    		dao.update(inssql.toString());
    		}
	    	catch(Exception ex)
	    	{
	    		ex.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(ex);
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
     * 
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
			/**对历史记录查询时，查询结果不显示主集指标*/
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
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			list.add(fielditem);
		}
		return list;
	}
}
