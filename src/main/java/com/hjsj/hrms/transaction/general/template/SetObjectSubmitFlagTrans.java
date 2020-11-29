/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.actionform.general.template.TemplateListForm;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 17, 2008:1:02:34 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SetObjectSubmitFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		String ins_id=(String)this.getFormHM().get("ins_id");
	 	String a0100=(String)this.getFormHM().get("a0100");
		String setname=(String)this.getFormHM().get("setname");
		
		String seqnum=(String)this.getFormHM().get("seqnum");
		String task_id=(String)this.getFormHM().get("task_id");
	 	String basepre=(String)this.getFormHM().get("basepre");
		String submitflag=(String)this.getFormHM().get("submitflag");
		String infor_type=(String)this.getFormHM().get("infor_type");
		String sp_batch = (String)this.getFormHM().get("sp_batch");
		TemplateListForm templateListForm = (TemplateListForm)this.getFormHM().get("FrameForm_templateListForm");
	
		if(templateListForm!=null)
		{
			HttpSession session2 = (HttpSession)templateListForm.getSession2();
			ArrayList alist =templateListForm.getTemplatelistform().getAllList();
			ArrayList templist = new ArrayList();
			for(int i=0;i<alist.size();i++){
				LazyDynaBean tempbean =	(LazyDynaBean)alist.get(i);
				if("1".equals(infor_type))
				{
					if(tempbean.get("a0100")!=null&&tempbean.get("a0100").toString().equalsIgnoreCase(a0100)
							&&tempbean.get("basepre")!=null&&tempbean.get("basepre").toString().equalsIgnoreCase(basepre)){
						tempbean.set("submitflag", submitflag);
						templist.add(tempbean);
						continue;
					}
				}
				else if("2".equals(infor_type))
				{
					if(tempbean.get("B0110")!=null&&tempbean.get("B0110").toString().equalsIgnoreCase(a0100)){
						tempbean.set("submitflag", submitflag);
						templist.add(tempbean);
						continue;
					}
					
				}
				else if("3".equals(infor_type))
				{
					if(tempbean.get("E01A1")!=null&&tempbean.get("E01A1").toString().equalsIgnoreCase(a0100)){
						tempbean.set("submitflag", submitflag);
						templist.add(tempbean);
						continue;
					}
				}
				templist.add(tempbean);
			}
			templateListForm.getTemplatelistform().setList(templist);
			if(session2!=null){
				session2.removeAttribute("templateListForm");
				session2.setAttribute("templateListForm", templateListForm);
			}
			
		}
		
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("0".equals(task_id)&&!"1".equals(sp_batch))
			{
				RecordVo vo=new RecordVo(setname);
				if("1".equals(infor_type))
				{
					vo.setString("basepre", basepre);
					vo.setString("a0100", a0100);
				}
				else if("2".equals(infor_type))
					vo.setString("b0110",a0100);
				else if("3".equals(infor_type))
					vo.setString("e01a1",a0100);
				vo.setInt("submitflag", Integer.parseInt(submitflag));			
				if(!"0".equalsIgnoreCase(ins_id))
				{
					vo.setInt("ins_id", Integer.parseInt(ins_id));
				}		
				dao.updateValueObject(vo);
			}
			else
			{
				if(seqnum==null)
				{
					String sql="update t_wf_task_objlink set submitflag="+submitflag+" where seqnum=(select seqnum from "+setname+" where ins_id="+ins_id+" and ";
					if("1".equals(infor_type))
					{
						sql+="basepre='"+basepre+"' and a0100='"+a0100+"' ";
					}
					else if("2".equals(infor_type))
						sql+="b0110='"+a0100+"' "; 
					else if("3".equals(infor_type))
						sql+="e01a1='"+a0100+"' "; 
					
					sql+="   ) and  ins_id="+ins_id;
					if(!"0".equals(task_id)&&!"1".equals(sp_batch)){
						sql+="  and  task_id="+task_id;
					}
					int num=dao.update(sql);
					if(num==0&&!"0".equalsIgnoreCase(ins_id)) //兼容旧程序
					{
						sql="update "+setname+" set submitflag="+submitflag+"   where ins_id="+ins_id+" and ";
						if("1".equals(infor_type))
						{
							sql+="basepre='"+basepre+"' and a0100='"+a0100+"' ";
						}
						else if("2".equals(infor_type))
							sql+="b0110='"+a0100+"' "; 
						else if("3".equals(infor_type))
							sql+="e01a1='"+a0100+"' "; 
						dao.update(sql);
					}
				}
				else
					dao.update("update t_wf_task_objlink set submitflag="+submitflag+" where seqnum='"+seqnum+"' and  task_id="+task_id);
			}
			/**必须移走*/
			this.getFormHM().remove("FrameForm_templateListForm");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
