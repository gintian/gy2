/**
 * 
 */
package com.hjsj.hrms.module.template.historydata.formcorrelation.templatetoolbar.printout.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.module.template.historydata.formcorrelation.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 导出word或者pdf
* @Title: OutPdfTrans
* @Description:
* @author: hej
* @date 2019年11月19日 下午4:42:53
* @version
 */
public class OutPdfTrans extends IBusiness {
    
	@Override
    public void execute() throws GeneralException {
        HashMap formMap= this.getFormHM();
        TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
        String moduleId = frontProperty.getModuleId();
        String returnFlag = frontProperty.getReturnFlag();
        String tabId = frontProperty.getTabId();
        String task_id = frontProperty.getTaskId();
        String infor_type=frontProperty.getInforType();
        String flag=(String)this.getFormHM().get("flag");
        String outtype=(String)this.getFormHM().get("outtype");//导出的类型 1 word 2 pdf
        String downtype=(String)this.getFormHM().get("downtype");
        String noShowPageNo = frontProperty.getOtherParam("noshow_pageno");//设定的不显示的页签
        String taskid_validate = PubFunc.decrypt(frontProperty.getOtherParam("taskid_validate"));
        String record_id = frontProperty.getOtherParam("record_id");
        String archive_id = frontProperty.getOtherParam("archive_id");
		String archive_year = frontProperty.getOtherParam("archive_year"); 
        TemplateParam paramBo=new TemplateParam(this.frameconn,this.userView,Integer.parseInt(tabId),archive_id);
		int signatureType = paramBo.getTemplateModuleParam().getSignatureType();
        if(flag==null|| "".equals(flag))
            flag="0";
        ArrayList inslist=null; 
        ArrayList tasklist=null;
        if(frontProperty.isBatchApprove())
        {
           tasklist=getTaskList(task_id);//getInsList(batch_task);
        }
        else
        {
            tasklist=new ArrayList();
            tasklist.add(task_id);
        }       
        String yj=(String)this.getFormHM().get("yj");
        String pageno=(String)this.getFormHM().get("pageno");
        yj="0";
        /**如果打印审批意见表时，但页号为空时，退出*/
        if("1".equals(yj)&&(pageno==null|| "".equalsIgnoreCase(pageno)))
                return;
        try
        {
        	ContentDAO dao=new ContentDAO(this.getFrameconn());
            ArrayList objlist=new ArrayList();          
        	String object_id = "";
        	String basepre="";
    		TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,this.userView, Integer.parseInt(tabId),archive_id);
			String sql = dataBo.getArchiveSql(record_id,archive_year);
			this.frowset = dao.search(sql);
			if(this.frowset.next()) {
				object_id = this.frowset.getString("objectid");
				String ins_id = this.frowset.getString("ins_id");
				inslist=new ArrayList();
				inslist.add(ins_id);
			}
            String a0100="";
            if("2".equals(infor_type)){
                    a0100=object_id;
                    objlist.add(a0100);
            }else if("3".equals(infor_type)){
                a0100=object_id;
                objlist.add(a0100);
            }else{
                int i = object_id.indexOf("`");
                if (i>0){
                    basepre=object_id.substring(0,i);
                    a0100=object_id.substring(i+1);
                }
 
                objlist.add(basepre+a0100);
            }
            if(objlist.size()==1&&(objlist.get(0)==null||((String)objlist.get(0)).trim().length()==0))
                    objlist=new ArrayList();
                
            OutWordBo owbo = new OutWordBo(this.getFrameconn(),this.userView,Integer.parseInt(tabId),tasklist.get(0).toString(),archive_id);
            if (frontProperty.isSelfApply()){
                owbo.setSelfApply(true);
            }
            String filename=null;
            owbo.setSigntype(signatureType);
            owbo.setNoshow_pageno(noShowPageNo);
            owbo.getParamBo().setReturnFlag(returnFlag);
            owbo.setDowntype(downtype);
            owbo.setOuttype(outtype);
            owbo.setShow_pageno(pageno);
            owbo.setModule_id(moduleId);
            owbo.setArchive_year(archive_year);
            owbo.setRecord_id(record_id);
            if(taskid_validate!=null&&!"".equals(taskid_validate)&&task_id.equals(taskid_validate.split("_")[0]))
            	owbo.getParamBo().setNeedJudgPre("0");
            
            filename=owbo.outword(1,inslist);
            filename=PubFunc.encrypt(filename);
            this.getFormHM().clear();//清空参数，减少网络数据传输量
            this.getFormHM().put("filename",filename);
            TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabId),this.userView);
            
            if(tablebo.getOperationtype()!=0){
                if("1".equals(infor_type))
                judgeIsLlexpr(objlist,tablebo.getLlexpr());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
	
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		String[] lists=StringUtils.split(batch_task,",");
		ArrayList list=new ArrayList();
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++){
			String temptaskid=lists[i];
			list.add(lists[i]);
		}
		return list;
		
	}
	
	private void judgeIsLlexpr(ArrayList objlist,String llexpr)
	{
		HashMap hm=new HashMap();
		ArrayList a0100lists=null;
		String first_base=null;
		String a0100=null;
		try{
			for(int i=0;i<objlist.size();i++)
			{
				String obj_id=(String)objlist.get(i);
				if(obj_id==null|| "".equals(obj_id))
					continue;
				String pre=obj_id.substring(0,2).toLowerCase();
				/**对人员信息群时，过滤单位、部门及职位*/
				if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
					continue;
				pre=obj_id.substring(0,3).toLowerCase();
				/**按人员库进行分类*/
				if(!hm.containsKey(pre))
				{
					a0100lists=new ArrayList();
				}
				else
				{
					a0100lists=(ArrayList)hm.get(pre);
				}
				a0100lists.add(obj_id.substring(3));
				if(i==0)
				{
					first_base=pre;
					a0100=obj_id.substring(3);
				}
				hm.put(pre,a0100lists);
			}//for i loop end.
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	    /**加规则过滤*/
		ArrayList alUsedFields=null;
		String temptable=null;
		if(llexpr!=null && llexpr.trim().length()>0)
	    { 
		  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		  temptable=createSearchTempTable(this.getFrameconn());
	    }
		
	    Iterator iterator=hm.entrySet().iterator();
	    StringBuffer judgeSql=new StringBuffer();

		while(iterator.hasNext())
		{

			Entry entry=(Entry)iterator.next();
			String pre=entry.getKey().toString();
			ArrayList a0100list =(ArrayList)entry.getValue();
			
			
		    judgeSql.append("select A0101 from ");
			judgeSql.append(pre);
			judgeSql.append("A01 where a0100 in(''");
			for(int i=0;i<a0100list.size();i++)
			{
				judgeSql.append(",'");
				judgeSql.append(a0100list.get(i));
				judgeSql.append("'");
			}
			judgeSql.append(")");
			judgeSql.append(" and ");
			judgeSql.append(getFilterSQL(temptable,pre,alUsedFields,llexpr));
			judgeSql.append(" UNION ");
			//System.out.println(a0100list + pre);		
		}
		if(judgeSql.length()>7)
		{
		  judgeSql.setLength(judgeSql.length()-7);
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
	      RowSet rset=null;
	      String judgedesc="";
	      boolean bl=false;
	      
	      try
	      {
	    	  //System.out.println(judgeSql.toString() + llexpr);
	    	  rset=dao.search(judgeSql.toString());
	   		
	    	  while(rset.next())
	    	  {
	    		  if(bl==false)
	    		  {
	    			  judgedesc=rset.getString("a0101");
	    			  bl=true;
	    		  }else
	    		  {
	    			  judgedesc+="," + rset.getString("a0101");  
	    		  }	    		 
	          }
	    	  if(bl)
	    	  {
	    		  this.getFormHM().put("sp_flag","3");
	    		  this.getFormHM().put("judgeisllexpr",judgedesc + ResourceFactory.getProperty("general.template.ishavenotjudge"));
	    	  }else
	    	  {
	    		  this.getFormHM().put("judgeisllexpr","1");
	    	  }
	      }
	      catch(Exception ex)
	      {
	    	  ex.printStackTrace();
	      }
		}else
		{
			this.getFormHM().put("judgeisllexpr","1");
		}
	
	
	}
	private String getFilterSQL(String temptable,String BasePre,ArrayList alUsedFields,String llexpr)
	{
		String sql=" (1=2)";
		try{
		if(llexpr!=null && llexpr.length()>0)
		{
			StringBuffer inserSql=new StringBuffer();
			inserSql.append("insert into ");
			inserSql.append(temptable);
			inserSql.append("(a0100) select '");
			inserSql.append(BasePre);
			inserSql.append("' "+Sql_switcher.concat()+"a0100 from ");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( this.userView ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;							
			yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.getFrameconn(),"A", null);
			String tempTableName = yp.getTempTableName();
			sql="('" + BasePre + "'" +Sql_switcher.concat()+"" +  BasePre +"A01.a0100 in  (select distinct a0100 from " + temptable + "))";
			inserSql.append(tempTableName);
			inserSql.append(" where " + yp.getSQL());
			dao.insert(inserSql.toString(),new ArrayList());	
		}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	private String createSearchTempTable(Connection conn)
	{
		String temptable="temp_search_xry_01";
		try{
			StringBuffer sql=new StringBuffer();
			sql.delete(0,sql.length());
			sql.append("drop table ");
			sql.append(temptable);
			try{
			  ExecuteSQL.createTable(sql.toString(),conn);
			}catch(Exception e)
			{
				//e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("CREATE TABLE ");
			sql.append(temptable);
			//sql.append("(a0100  varchar (100) PRIMARY KEY (a0100))");
			sql.append("(a0100  varchar (100))");
			try{
				  ExecuteSQL.createTable(sql.toString(),conn);				  			  
		    }catch(Exception e)
			{
				e.printStackTrace();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return temptable;
	}
}
