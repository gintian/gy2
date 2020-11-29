/**
 * 
 */
package com.hjsj.hrms.module.template.templatelist.transaction;

import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 人事异动列表选中按钮
 * @author liuzy
 *
 */
public class TemplateSubmitFlagTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
        TemplateFrontProperty frontProperty =new TemplateFrontProperty(this.getFormHM());            
        String moduleId = frontProperty.getModuleId();
        String tabId = frontProperty.getTabId();
        String taskId = frontProperty.getTaskId();         
        String infor_type=frontProperty.getInforType();
        String search_sql=frontProperty.getOtherParam("search_sql");//查询控件的查询条件
        //29802 获取条件语句时出现T. 需去掉
        if(search_sql.indexOf("T.") != -1){
			search_sql = search_sql.replace("T.", "");
		}
        String subModuleId = frontProperty.getOtherParam("sub_moduleId");
		String submitflag=(String)this.getFormHM().get("submitflag");
		String doSelectAll=(String) this.getFormHM().get("doSelectAll");//是否是全选  0=全选，1=单个选  28838
		String objectid = (String)this.getFormHM().get("objectid");
		objectid = PubFunc.decrypt(objectid);//解密
		String a0100="";
		String basepre="";
		if (StringUtils.isNotBlank(objectid)){
			if ("1".equals(infor_type)){
				basepre=objectid.split("`")[0];
				a0100 =objectid.split("`")[1];
			}
		}
		TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn,this.userView);
		String tableName=utilBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);
		String setname=tableName; 
		
		String filterSql = "";
		//由于卡片下不过滤，故subModuleId参数只在列表下有
		if(null != this.userView.getHm().get("templateList_filterSql"+subModuleId) && StringUtils.isNotEmpty(subModuleId)){
			//表格过滤的过滤条件
			filterSql = this.userView.getHm().get("templateList_filterSql"+subModuleId).toString();
		}
		
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("0".equals(taskId))
			{
				//全选 28838 linbz 优化为全选就是所有记录不仅限于当前页
				if("0".equals(doSelectAll)){
					
					StringBuffer updateSql = new StringBuffer("update " + setname + " set submitflag=? where 1=1 ");
					//29606 增加查询控件的查询条件
					if(StringUtils.isNotEmpty(search_sql)){
						updateSql.append(search_sql);
					}
					//过滤条件
					if(StringUtils.isNotEmpty(filterSql)){
						updateSql.append(filterSql);
					}
					list.add(submitflag);
					dao.update(updateSql.toString(),list);
				}else{
					RecordVo vo=new RecordVo(setname);
					if("1".equals(infor_type))
					{
						vo.setString("basepre", basepre);
						vo.setString("a0100", a0100);
					}
					else if("2".equals(infor_type))
						vo.setString("b0110",objectid);
					else if("3".equals(infor_type))
						vo.setString("e01a1",objectid);
					vo.setInt("submitflag", Integer.parseInt(submitflag));			
					dao.updateValueObject(vo);
				}
			}
			else
			{
				if(StringUtils.isNotBlank(taskId)){
					//全选  28838 linbz 优化为全选就是所有记录不仅限于当前页
					if("0".equals(doSelectAll)){
						//批量的情况 task_id 有逗号
						ArrayList listAll = utilBo.getTaskIdtoInsId(taskId);
						for(int i=0;i<listAll.size();i++){
							HashMap map = (HashMap) listAll.get(i);
							String insId = (String)map.get("ins_id");
							String taskIdo = (String)map.get("task_id");
							
							ArrayList listTemp = new ArrayList();
							listTemp.add(submitflag);
							listTemp.add(insId);
							listTemp.add(taskIdo);
							list.add(listTemp);
						}
						
						StringBuffer updateSql = new StringBuffer("update t_wf_task_objlink set submitflag=? ");
						updateSql.append(" where seqnum in (select seqnum from "+setname+" where 1=1 ");
						updateSql.append(" and ins_id=? "); 
						//29606 增加查询控件的查询条件
						if(StringUtils.isNotEmpty(search_sql)){
							updateSql.append(search_sql);
						}
						//过滤条件
						if(StringUtils.isNotEmpty(filterSql)){
							updateSql.append(filterSql);
						}
						updateSql.append("   ) ");
						updateSql.append("  and  task_id=?");
						
						dao.batchUpdate(updateSql.toString(),list);
					}else{
						StringBuffer updateSql = new StringBuffer("update t_wf_task_objlink set submitflag=?");
						list.add(submitflag);
						updateSql.append(" where seqnum=(select seqnum from "+setname+" where 1=1 and ");
						if("1".equals(infor_type)){
							updateSql.append("basepre=? and a0100=? ");
							list.add(basepre);
							list.add(a0100);
						}
						else if("2".equals(infor_type)){
							updateSql.append(" b0110=?"); 
							list.add(objectid);
						}
						else if("3".equals(infor_type)){
							updateSql.append(" e01a1=?"); 
							list.add(objectid);
						}
						updateSql.append(" and ins_id=?"); 
						String ins_id = utilBo.getInsId(taskId);
						list.add(ins_id);
						
						updateSql.append("   ) ");
						updateSql.append("  and  task_id=?");
						list.add(taskId);
						dao.update(updateSql.toString(),list);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
