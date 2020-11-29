/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.actionform.general.template.TemplateForm;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Dec 6, 20064:04:51 PM
 * @author chenmengqing
 * @version 4.0
 */
public class BatchApproveTrans extends IBusiness {

	public void execute() throws GeneralException {
	    ArrayList tasklist=null;
	    HashMap requestHm = (HashMap)this.getFormHM().get("requestPamaHM");
        String unDealedTaskIds=(String)requestHm.get("unDealedTaskIds"); 
        boolean flag=false;//当前审批人是否是单一节点下多审批人中的一个
        String tabid="";
        String batch_task="0";
        String tasklist_str="";
        String taskid="0";   //用于存储审批中的第一个task_id
        if (unDealedTaskIds!=null && unDealedTaskIds.length()>0){
            unDealedTaskIds=SafeCode.decode(unDealedTaskIds);
            unDealedTaskIds=PubFunc.decrypt(unDealedTaskIds);
            tasklist_str=unDealedTaskIds;
            tasklist=getTaskList(unDealedTaskIds);
            for(int i=0;i<tasklist.size();i++)
            {//只需得到tabid
                LazyDynaBean dynabean=(LazyDynaBean)tasklist.get(i);
                tabid=(String)dynabean.get("tabid");
                break;
            }  
            requestHm.remove("unDealedTaskIds"); 
        }
        else {
            tasklist=(ArrayList)this.getFormHM().get("selectedlist");//这个参数最重要
            if(tasklist==null||tasklist.size()==0)
                throw new GeneralException(ResourceFactory.getProperty("error.notselect.task"));
            String pretab="";//用于控制所选的记录都属于一个模板
            String prenode="";//用于控制所选的记录都属于同一个节点
            /**审批模式=0自动流转，=1手工指派*/
            TemplateTableBo tablebo1=null;
            for(int i=0;i<tasklist.size();i++)
            {
                LazyDynaBean dynabean=(LazyDynaBean)tasklist.get(i);
                tabid=(String)dynabean.get("tabid");
                
                /**安全改造，将加密后的参数解密回来**/
                String task_id=PubFunc.decrypt((String)dynabean.get("task_id"));
                if("0".equals(taskid)){
                	taskid=task_id;
                }
                tasklist_str+=task_id+",";
                String ins_id=(String)dynabean.get("ins_id");
                String node_id=(String)dynabean.get("node_id");
                if(i!=0)
                {
                    //只能对同一个业务模板的任务进行批量处理！
                    if(!tabid.equalsIgnoreCase(pretab))
                    {
                        throw new GeneralException(ResourceFactory.getProperty("error.equal.template"));
                    }
                    //只能对同一个流程节点的任务进行批量处理！ wangrd 屏蔽 2015-01-09
                    /*
                if(!node_id.equalsIgnoreCase(prenode))
                {
                    throw new GeneralException(ResourceFactory.getProperty("error.equal.template.node"));
                }
                     */
                }
                if (tablebo1==null){
                    tablebo1=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
                }
                if(tablebo1.getSp_mode()==1){
                    if (tablebo1.isDef_flow_self(Integer.parseInt(task_id))){
                        throw new GeneralException("自定义审批流程的任务暂不支持批量审批！");  
                    }
                };
                
                pretab=tabid;
                prenode =node_id;
                
                if(isParallel(Integer.parseInt(task_id),Integer.parseInt(ins_id),tabid))
                {
                    flag=true;
                    // throw new GeneralException("不能对执行（考核关系角色）流程的任务进行批量审批！");
                } 
                
            }//for loop end  
        }
        String sp_flag=(String)this.getFormHM().get("sp_flag");
        HttpSession session=(HttpSession)this.getFormHM().get("session");
  
		
		TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
		TemplateUtilBo tb=new TemplateUtilBo(this.getFrameconn(),this.userView);
		String view = tb.getTemplateView(Integer.parseInt(tabid));
		/**审批模式=0自动流转，=1手工指派*/
		int sp_model=tablebo.getSp_mode();
		if(sp_model==1 && flag)
		{
			 throw new GeneralException("不能对执行（考核关系角色）手动指派的流程任务进行批量审批！");//什么意思？？？
		}
		batch_task=PubFunc.encrypt(tasklist_str);
		this.getFormHM().put("sp_batch","1");
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("view",view);
		this.getFormHM().put("taskid",taskid);
		this.getFormHM().put("batch_task",batch_task);
		this.getFormHM().put("tasklist_str",tasklist_str);
		TemplateForm tempform=(TemplateForm)session.getAttribute("templateForm");
		if(tempform==null)
		{
			tempform= new TemplateForm();
			session.setAttribute("templateForm",tempform);
		}
		tempform.setTasklist(tasklist);
		tempform.setTabid(tabid);
		tempform.setIns_id("1");//随便定义一个实例号
		tempform.setSp_flag(sp_flag);
	}

	
	/**
	 * 当前审批人是否是单一节点下多审批人中的一个
	 * @param taskid
	 * @param ins_id
	 * @return
	 */
	public boolean isParallel(int taskid,int ins_id,String tabid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
		/*	buf.append("select count(*) as nm from t_wf_task_datalink where ");
			buf.append("  ins_id="+ins_id+" and task_id="+taskid);*/
			buf.append("select  count(tr.role_property) from t_wf_node tn,t_wf_actor ta,t_sys_role tr where tn.node_id=ta.node_id  and ta.actorid=tr.role_id ");
			buf.append(" and tn.tabid="+tabid+" and actor_type=2 and role_property in (9,10,11,12,13,14)");
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				if(rset.getInt(1)>0)
					flag=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
    /**   
     * @Title: getTaskList   
     * @Description: 获取上次未处理完成的单据   
     * @param @param unDealedTaskIds
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    private ArrayList getTaskList(String unDealedTaskIds)throws GeneralException
    {
        ArrayList tasklist=new ArrayList();
        String[] lists=StringUtils.split(unDealedTaskIds,",");
        StringBuffer strsql=new StringBuffer();
        String format_str="yyyy-MM-dd HH:mm";
        if(Sql_switcher.searchDbServer()==Constant.ORACEL)
            format_str="yyyy-MM-dd hh24:mi";
        strsql.append("select U.ins_id,case when T.task_topic like '%共0%' then U.name  else T.task_topic end name,U.tabid,U.actorname fullname,a0101, task_state finished ,"
                +Sql_switcher.dateToChar("U.start_date",format_str)+" as ins_start_date,"
                +Sql_switcher.dateToChar("T.end_date",format_str)
                +" as ins_end_date,T.actor_type,T.actorname,T.task_id,t.node_id from t_wf_task T,t_wf_instance U");
        strsql.append(" where T.ins_id=U.ins_id ");
        strsql.append(" and  T.task_id in (");
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
            tasklist=dao.searchDynaList(strsql.toString());     
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return tasklist;
    }
	
	
}
