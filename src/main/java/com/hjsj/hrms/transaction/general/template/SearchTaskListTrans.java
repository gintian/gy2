/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.general.template.workflow.TemplatePendingTaskBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <p>Title:SearchTaskListTran</p>
 * <p>Description:任务清单</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 27, 20061:10:25 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTaskListTrans extends IBusiness {

 
	public void execute() throws GeneralException {
	
		try
		{
			String templateId=(String)this.getFormHM().get("templateId");
			String sp_flag=(String)this.getFormHM().get("sp_flag");//需要审批
			String query_type=(String)this.getFormHM().get("query_type");
			String days=(String)this.getFormHM().get("days");
			//this.getFormHM().put("days", "");这段代码是谁添加的，影响到了默认的30天
			String start_date=(String)this.getFormHM().get("start_date");
			String end_date=(String)this.getFormHM().get("end_date");
			StringBuffer strsql=new StringBuffer();//这个sql语句并没有考虑接收范围等因素。接收范围在getTaskList（）方法中处理。郭峰
			StringBuffer strsql2=new StringBuffer();  //模板查询sql
			
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String fromflag=(String)map.get("fromflag"); 
			String bs_flag="1";  //1：审批任务 2：加签任务 3：报备任务  4：空任务
			if(map.get("bs_flag")!=null)
			{
				bs_flag=(String)map.get("bs_flag");
				map.remove("bs_flag");
			}
			
			String type=(String)map.get("type");  //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整  23：考勤业务办理  24：非考勤业务(业务申请不包含考勤信息)
			// * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审
			String module_id="1";
			if("10".equals(type))
				module_id="7";
			else if("11".equals(type))
				module_id="8";
			else if("23".equals(type))
				module_id="10";
			else if("6".equals(fromflag))
				module_id="9";
			
			this.getFormHM().put("type",type);
	
		
		
			this.getFormHM().put("bs_flag",bs_flag);
			ArrayList bs_flag_list=new ArrayList();
			bs_flag_list.add(new CommonData("1",ResourceFactory.getProperty("tab.label.bptask")));
		//	bs_flag_list.add(new CommonData("2",ResourceFactory.getProperty("tab.label.jqtask")));
			bs_flag_list.add(new CommonData("3",ResourceFactory.getProperty("tab.label.bbtask")));
			this.getFormHM().put("bs_flag_list",bs_flag_list);
			
			ArrayList columnList=getColumnList(sp_flag);
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("start_date", start_date!=null?start_date:"");
			paramBean.set("end_date", end_date!=null?end_date:"");
			paramBean.set("days", days!=null?days:"");
			paramBean.set("query_type", query_type!=null?query_type:"");
			paramBean.set("tabid", templateId!=null?templateId:"");
			paramBean.set("module_id", module_id!=null?module_id:"");
			paramBean.set("bs_flag", bs_flag!=null?bs_flag:"");
			TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(this.frameconn,this.userView);
			ArrayList dataList=templatePendingTaskBo.getDBList(paramBean,this.userView);
			
			LazyDynaBean abean=null;
			HashMap viewMap=new HashMap();
			for(Iterator t=dataList.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next();
				String tabid=(String)abean.get("tabid");
				String ismessage=(String)abean.get("ismessage");
				abean.set("isMessage", ismessage);
				String unitname=(String)abean.get("unitname");
				String states=(String)abean.get("states");
				if(states!=null&&states.trim().length()>0) 
						abean.set("states",AdminCode.getCodeName("23",states));
				if(states!=null&&states.trim().length()>0)  
				{
					String temp=AdminCode.getCodeName("UN",unitname);
					if(temp.length()==0)
						temp=AdminCode.getCodeName("UM",unitname);
					abean.set("unitname",temp);
				}
				
				
                String view = "";
                if(viewMap.get(tabid)==null){
                	TemplateUtilBo tb=new TemplateUtilBo(this.getFrameconn(),this.userView);
                    view = tb.getTemplateView(Integer.parseInt(tabid));
                    viewMap.put(tabid, view);
                }else{
                	view=(String)viewMap.get(tabid);
                }
                abean.set("view",view);
			}
			
			HashSet tabidSet=(HashSet)paramBean.get("tabidSet");
				this.getFormHM().put("taskList",dataList);
				
					
				 	ArrayList templateList=new ArrayList();
				 	if(tabidSet.size()>0)
				 		templateList=getTemplateList2(tabidSet);
				 	else
				 	{
				 		if(templateId!=null&&templateId.length()>0)
				 		{
				 			templateList=getTemplateList(templateId);
				 		}  
				 	}
				 	if("-1".equals(templateId))
				 	    this.getFormHM().put("templateList",templateList);
			
				
		
			if(type==null)
				type="";
			this.getFormHM().put("type",type);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	 
 
	
	public ArrayList getColumnList(String sp_flag)
	{
		ArrayList columnList=new ArrayList();
		columnList.add(getBean("select","选择"));
		if("1".equals(sp_flag))
			columnList.add(getBean("states","状态"));
		columnList.add(getBean("task_pri","任务优先级"));
		columnList.add(getBean("bread","是否阅读"));
		columnList.add(getBean("bfile","是否有附件"));
		columnList.add(getBean("a0101_1","发送人"));
		columnList.add(getBean("task_topic","主题"));
		columnList.add(getBean("start_date","接收时间"));
		columnList.add(getBean("unitname","发起单位"));
		return columnList;
	}
	
	public LazyDynaBean getBean(String itemid,String itemdesc)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("itemid",itemid);
		abean.set("itemdesc",itemdesc);
		return abean;
	}
	
	 
	 
	
	public ArrayList getTemplateList(String tabid)
	{
		ArrayList list=new ArrayList();
		HashMap map2 = new HashMap();
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			CommonData dt=new CommonData("-1","全部");
			list.add(dt);
			StringBuffer sql2=new StringBuffer(); 
			sql2.append("select   tabid,  name  from   Template_table   "+_withNoLock+"");
			sql2.append(" where tabid in ("+tabid+") ");
			RowSet rowSet=dao.search(sql2.toString());
			while(rowSet.next())
			{ 
				list.add(new CommonData(rowSet.getString("tabid"),rowSet.getString("name"))); 
			} 
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	public ArrayList getTemplateList2(HashSet tabidSet)
	{
		ArrayList list=new ArrayList();
		HashMap map2 = new HashMap();
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			CommonData dt=new CommonData("-1","全部");
			list.add(dt);
			StringBuffer sql2=new StringBuffer();
			StringBuffer tabidStr=new StringBuffer("");
			for(Iterator t=tabidSet.iterator();t.hasNext();)
			{
				tabidStr.append(","+(String)t.next());
			}
			
			sql2.append("select   tabid,  name  from   Template_table "+_withNoLock+"  ");
			sql2.append(" where tabid in ("+tabidStr.substring(1)+") ");
			RowSet rowSet=dao.search(sql2.toString());
			while(rowSet.next())
			{
				 
				list.add(new CommonData(rowSet.getString("tabid"),rowSet.getString("name")));
				 
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
}
