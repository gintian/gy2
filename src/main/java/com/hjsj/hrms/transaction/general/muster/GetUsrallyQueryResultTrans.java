package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

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
public class GetUsrallyQueryResultTrans extends IBusiness {

    /**
     * 
     */
    public GetUsrallyQueryResultTrans() {
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

    	/*
    	String codeid=userView.getManagePrivCode();
    	String codevalue=userView.getManagePrivCodeValue();
    	if(codeid==null||codeid.equals(""))
    		return "";
    	if(flag==2)
    		strcond.append(" B01.B0110 like ");
    	else
    		strcond.append(" K01.E01A1 like ");
    	strcond.append("'");
    	strcond.append(codevalue);
    	strcond.append("%");
    	strcond.append("'");
    	*/
    	/**
    	 * cmq changed at 20121002 单位和岗位权限控制规则如下：
    	 * 业务范围->操作单位->人员范围
    	 */
    	strcond.append(this.userView.getUnitPosWhereByPriv(flag));
    	
    	return strcond.toString();
    }    
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        
    	String id=(String)this.getFormHM().get("id");   	
        String dbpre=(String)this.getFormHM().get("setname"); 
        String infor=(String)this.getFormHM().get("infor");
        String history="0";
        String result="0";
        String qobj="0";        
       
    	boolean bresult=true;
    	if("1".equals(result))
    		bresult=false;         
        String fields=null;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("lexpr");
        vo.setString("id",id);
        ArrayList fieldlist=null;
        try
        {
            vo=dao.findByPrimaryKey(vo);
            String expr=vo.getString("lexpr");
            String factor=vo.getString("factor");
            String type=vo.getString("type");
            String fuzzy=vo.getString("fuzzyflag");
            if(fuzzy==null|| "".equals(fuzzy))
            	fuzzy="0";
            boolean blike=false;
            if("1".equals(fuzzy))
            	blike=true;
            /**查询对象不是人员时，库前缀为空*/
            if(!"1".equals(type))
                dbpre="";
            cat.debug("expr="+expr);
            cat.debug("factor="+factor);
            factor=factor.replaceAll("\\$THISMONTH\\[\\]",ResourceFactory.getProperty("general.inform.search.this.month"));  /*兼容报表管理、常用查询*/
            /**表过式分析*/
            String strwhere="";
            boolean bhis=false;
            if("1".equals(history))
            	bhis=true;            
            /**非超级用户且对人员库进行查询*/
            fieldlist=new ArrayList();            
            if((!userView.isSuper_admin())&& "1".equals(type))
            {
                strwhere=userView.getPrivSQLExpression(expr+"|"+factor,dbpre,bhis,blike,bresult,fieldlist);
                cat.debug("---->Common query's priv="+strwhere);
            }
            else
            {
                FactorList factorlist=new FactorList(expr,factor,dbpre,bhis ,blike,bresult,Integer.parseInt(type),userView.getUserId());
                fieldlist=factorlist.getFieldList();
                strwhere=factorlist.getSqlExpression();
            }
            cat.debug("Common query's where="+strwhere);
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
            }            
            /**权限分析及过滤*/
            fieldlist=privFieldList(fieldlist,type,history);
            String filterfield=getFilterFields(fieldlist,history);
            
            StringBuffer strsql=new StringBuffer();
            /**1人员　2:单位 3:职位*/
            if("1".equals(type))
            {
                strsql.append("select distinct a0000,");
                strsql.append(dbpre);
                strsql.append("a01.a0100 ,");
                strsql.append(dbpre);
                strsql.append("a01.b0110 b0110,");
                strsql.append(dbpre);
                strsql.append("a01.e0122 e0122,");
                strsql.append(dbpre);
                strsql.append("a01.e01a1 e01a1,a0101 ");
                strsql.append(" ");
                fields="a0100,b0110,e0122,e01a1,a0101,";
                this.getFormHM().put("distinct",dbpre+"a01.a0100");
            }
            else if("2".equals(type))
            {
                strsql.append("select distinct b01.b0110 ");
                strsql.append(" ");
                fields="b0110,";    
                this.getFormHM().put("distinct","B01.B0110");                
            }
            else if("3".equals(type))
            {
                strsql.append("select distinct k01.e01a1 ");
                strsql.append(" ");
                fields="e01a1,";    
                this.getFormHM().put("distinct","K01.E01A1");                
            }
            /**在查询因子项目中存在不同字段*/
            if(!(filterfield==null|| "".equals(filterfield)))
            {
            	fields=fields+filterfield+",";
            	strsql.append(",");
            	strsql.append(filterfield);
            }
            
            StringBuffer objIDs=new StringBuffer("");
            this.frowset=dao.search(strsql.toString()+" "+strwhere);
            while(this.frowset.next())
            {
            	String columnName="";
            	if("1".equals(infor))
            	{
            		objIDs.append("/"+dbpre+this.frowset.getString("a0100"));
            	}
            	else if("2".equals(infor))
            	{
            		objIDs.append("/"+this.frowset.getString("b0110"));
            	}
            	else if("3".equals(infor))
            	{
            		objIDs.append("/"+this.frowset.getString("e01a1"));
            	}
            }
            
            this.getFormHM().put("infor",infor);        
            if(objIDs.length()>0)
            	this.getFormHM().put("objIDs",objIDs.substring(1));
            else
            	this.getFormHM().put("objIDs","");
                      
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(ee);            	
        }
        
    }

    /**
     * 过滤查询结果
     * @param type
     * @param dbpre
     * @param sql
     */
    private void filterQueryResult(String type, String dbpre,String sql)throws GeneralException
    {
		if(this.userView.getStatus()==4)
			return;
		if("2".equals(type))
			dbpre="B";
		if("3".equals(type))
			dbpre="K";
		String tablename=this.userView.getUserName()+dbpre+"result";
		StringBuffer delsql=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
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
    
	/**
	 * 保存查询结果
	 * @param type
	 * @param dbpre
	 */
	private void saveQueryResult(String type, String dbpre,String sql)throws GeneralException
	{
		if(this.userView.getStatus()==4)
			return;
		if("2".equals(type))
			dbpre="B";
		if("3".equals(type))
			dbpre="K";
		String tablename=this.userView.getUserName()+dbpre+"result";
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
