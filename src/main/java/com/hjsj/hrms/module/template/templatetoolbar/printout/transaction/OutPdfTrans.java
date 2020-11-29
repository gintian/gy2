/**
 * 
 */
package com.hjsj.hrms.module.template.templatetoolbar.printout.transaction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.sql.RowSet;

import org.apache.commons.lang.StringUtils;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 * <p>Title:OutPdfTrans.java</p>
 * <p>Description>:导出PDF文件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-2-19 下午03:38:40</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class OutPdfTrans extends IBusiness {
    
    
    @Override
    public void execute() throws GeneralException {
        HashMap formMap= this.getFormHM();
        TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
        String sysType = frontProperty.getSysType();
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
        String isDelete = frontProperty.getOtherParam("isDelete");
         //兼容导出wps与officer标识
        String officeOrWps = (String)formMap.get("officeOrWps");
        TemplateUtilBo utilBo= new TemplateUtilBo(this.frameconn,this.userView);    
        String tableName=utilBo.getTableName(frontProperty.getModuleId(),
                Integer.parseInt(frontProperty.getTabId()), frontProperty.getTaskId());
        String filterStr =  this.userView.getHm().get("filterStr")==null?"":(String)this.userView.getHm().get("filterStr");
        TemplateParam paramBo=new TemplateParam(this.frameconn,this.userView,Integer.parseInt(tabId));
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
        String out_pages=(String)this.getFormHM().get("out_pages");
        if(StringUtils.isNotEmpty(out_pages)) {
        	pageno=PubFunc.decrypt(out_pages);
        }
        yj="0";
        /**如果打印审批意见表时，但页号为空时，退出*/
        if("1".equals(yj)&&(pageno==null|| "".equalsIgnoreCase(pageno)))
                return;
        try
        {
            ArrayList objlist=new ArrayList();          
            /**打印全部人员*/
            if("2".equals(flag))
            {
                StringBuffer buf=new StringBuffer();
                if(!"0".equals(task_id))
                {
                    if("1".equals(infor_type)){
                        buf.append("select basepre,a0100,ins_id from ");
                    }else if("2".equals(infor_type)){
                        buf.append("select b0110,ins_id from ");
                    }else if("3".equals(infor_type)){
                        buf.append("select e01a1,ins_id from ");
                    }else{
                        buf.append("select basepre,a0100,ins_id from ");
                    }
                    buf.append(tableName);    
                    buf.append(" where 1=1 ");
                
                    buf.append(" and exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum  and "+tableName+".ins_id=t_wf_task_objlink.ins_id  ");
                    if("1".equals(returnFlag)||"2".equals(returnFlag) ||"11".equals(returnFlag)||"12".equals(returnFlag)||"13".equals(returnFlag)){//待办 ，我的已办\首页待办\首页待办列表\来自第三方系统或邮件
                        buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
                    }
                    
                    if(frontProperty.isBatchApprove())
                    {
                        buf.append(" and   task_id in (");
                        for(int i=0;i<tasklist.size();i++)
                        {
                            if(i!=0)
                                buf.append(",");
                            buf.append(tasklist.get(i));
                        }
                        buf.append(")");
                    }
                    else
                    {
                        //buf.append(" where submitflag=1 and ins_id=");
                        if(!"0".equals(task_id))
                        {
                            buf.append(" and  task_id=");
                            buf.append(task_id);
                        }
                    }
                    if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
                    	buf.append(" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ");
    				}else {
    					buf.append(" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");
    				}
                }
                else
                {
                    if("1".equals(infor_type)){
                        buf.append("select basepre,a0100  from ");  
                        }else if("2".equals(infor_type)){
                        buf.append("select b0110 from ");
                        }else if("3".equals(infor_type)){
                        buf.append("select e01a1 from ");
                        }else{
                            buf.append("select basepre,a0100  from ");  
                        }
                    
                    buf.append(tableName);
                    buf.append(" where 1=1 ");
                }
                
                if(filterStr.trim().length()>0){
                    buf.append(" and "+filterStr);
                }
                buf.append(this.getOrderBy(tabId));
                ContentDAO dao=new ContentDAO(this.getFrameconn());
                RowSet rset=dao.search(buf.toString());
                /**求每个对应的实例*/
                inslist=new ArrayList();
                while(rset.next())
                {
                    if(!"0".equals(task_id))
                        inslist.add(rset.getString("ins_id"));
                    else
                        inslist.add("0");
                    if("1".equals(infor_type)){
                        objlist.add(rset.getString("basepre")+rset.getString("a0100"));
                        }else if("2".equals(infor_type)){
                            objlist.add(rset.getString("b0110"));
                        }else if("3".equals(infor_type)){
                            objlist.add(rset.getString("e01a1"));
                        }else{
                            objlist.add(rset.getString("basepre")+rset.getString("a0100")); 
                        }
                    
                }
            }
            else if ("3".equals(flag))//选中人员
            {
                StringBuffer buf=new StringBuffer();
                if(!"0".equals(task_id))
                {
                    if("1".equals(infor_type)){
                        buf.append("select basepre,a0100,ins_id from ");
                        }else if("2".equals(infor_type)){
                        buf.append("select b0110,ins_id from ");
                        }else if("3".equals(infor_type)){
                        buf.append("select e01a1,ins_id from ");
                        }else{
                            buf.append("select basepre,a0100,ins_id from ");
                        }
                    buf.append(tableName);
                    
                    
                    buf.append(" where 1=1  ");
                    buf.append(" and exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id  ");
                    if("1".equals(moduleId)||"2".equals(moduleId)||"3".equals(moduleId)){//待办，我的申请，我的已办
                        buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
                    }
                    if(frontProperty.isBatchApprove())
                    {
                        //buf.append(" where submitflag=1 and  ins_id in (");                       
                        buf.append(" and submitflag=1 and task_id in (");                      
                        for(int i=0;i<tasklist.size();i++)
                        {
                            if(i!=0)
                                buf.append(",");
                            buf.append(tasklist.get(i));
                        }
                        buf.append(")");
                    }
                    else
                    {
                        //buf.append(" where submitflag=1 and ins_id=");
                        if(!"0".equals(task_id))
                        {
                            buf.append(" and submitflag=1 and task_id=");
                            buf.append(task_id);
                            //buf.append(")");
                        }
                    }
                    if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
                    	buf.append(" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ");
    				}else {
    					buf.append(" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");
    				}
                }

                else
                {
                    if("1".equals(infor_type)){
                        buf.append("select basepre,a0100  from ");  
                        }else if("2".equals(infor_type)){
                        buf.append("select b0110 from ");
                        }else if("3".equals(infor_type)){
                        buf.append("select e01a1 from ");
                        }else{
                            buf.append("select basepre,a0100  from ");  
                        }
                    buf.append(tableName);    
                    buf.append(" where submitflag=1");                  
                }
                buf.append(this.getOrderBy(tabId));
                ContentDAO dao=new ContentDAO(this.getFrameconn());
                RowSet rset=dao.search(buf.toString());
                /**求每个对应的实例*/
                inslist=new ArrayList();
                while(rset.next())
                {
                    if(!"0".equals(task_id))
                        inslist.add(rset.getString("ins_id"));
                    else
                        inslist.add("0");
                    if("1".equals(infor_type)){
                        objlist.add(rset.getString("basepre")+rset.getString("a0100"));
                        }else if("2".equals(infor_type)){
                            objlist.add(rset.getString("b0110"));
                        }else if("3".equals(infor_type)){
                            objlist.add(rset.getString("e01a1"));
                        }else{
                            objlist.add(rset.getString("basepre")+rset.getString("a0100")); 
                        }
                }               
            }           
            else//当前人员
            {
            	String cur_task_id= (String)this.getFormHM().get("cur_task_id");
            	cur_task_id = "0".equals(cur_task_id)?cur_task_id:PubFunc.decryption(cur_task_id);
                inslist=new ArrayList();
                String ins_id =utilBo.getInsId(cur_task_id);
                inslist.add(ins_id);
                String basepre="";
                String object_id =(String)this.getFormHM().get("object_id");
                object_id = PubFunc.decrypt(object_id);
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
            }
            
            if(objlist.size()==0){
            	String midString = "";
            	if("1".equals(outtype))//word
            		midString = "WORD";
            	else//pdf
            		midString = "PDF";
                if("1".equals(infor_type)){
                    throw new GeneralException("请选择需要生成"+midString+"的人员!");
                    }else if("2".equals(infor_type)){
                        throw new GeneralException("请选择需要生成"+midString+"的机构!");
                    }else if("3".equals(infor_type)){
                        throw new GeneralException("请选择需要生成"+midString+"的岗位!");
                    }
            }
                
            OutWordBo owbo = new OutWordBo(this.getFrameconn(),this.userView,Integer.parseInt(tabId),tasklist.get(0).toString());
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
            owbo.setOfficeOrWpsFlag(officeOrWps);
            owbo.setModule_id(moduleId);
            if(taskid_validate!=null&&!"".equals(taskid_validate)&&task_id.equals(taskid_validate.split("_")[0]))
            	owbo.getParamBo().setNeedJudgPre("0");
            
            filename=owbo.outword(objlist,1,inslist);
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

	private String getOrderBy(String tabId) {
		String orderBy = " order by a0000";
		String subModuleId = "templet_"+tabId;
		TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get(subModuleId);
        if(tableCacheList!=null)
        {
        	String sortSql = tableCacheList.getSortSql();
        	HashMap customParamHM = tableCacheList.getCustomParamHM()==null?new HashMap():tableCacheList.getCustomParamHM();
			String property = customParamHM.get("property")==null?"":(String)customParamHM.get("property");
			String direction = customParamHM.get("direction")==null?"":(String)customParamHM.get("direction");
			if(StringUtils.isNotBlank(property)&&StringUtils.isNotBlank(direction)) {
        		orderBy = " order by "+property+ " "+direction;
			}
			else if(sortSql!=null&&sortSql.trim().length()>0)
        	{
        		orderBy = sortSql;
        	}
        }
		return orderBy;
	}

	private ArrayList getInsList(String batch_task)throws GeneralException
	{
		ArrayList inslist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select ins_id from t_wf_task where task_id in (");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
				inslist.add(rset.getString("ins_id"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return inslist;
	}
	
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		String[] lists=StringUtils.split(batch_task,",");
		ArrayList list=new ArrayList();
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++){
			String temptaskid=lists[i];
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(templateMap!=null&&!templateMap.containsKey(temptaskid)){
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			list.add(lists[i]);
		}
		return list;
		
	}
	
	//兼容以前版本产生的数据
	boolean isSeqnum(String ins_id,String tabid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
	//		RowSet rowSet=dao.search("select seqnum from templet_"+tabid+" where ins_id="+ins_id);
			RowSet rowSet=dao.search("select seqnum from t_wf_task_objlink where ins_id="+ins_id);
			if(rowSet.next())
			{
				if(rowSet.getString(1)!=null&&rowSet.getString(1).trim().length()>0)
					flag=true;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
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
