package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class KhTemplateTreeTrans extends IBusiness{

	
	public void execute() throws GeneralException {
		try
		{
		
			String b0110 ="HJSJ";
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			if(this.userView.getStatus()==0)
			{
				if(this.userView.getManagePrivCodeValue()!=null&&!"".equals(this.userView.getManagePrivCode()))
				{
					b0110 = this.userView.getManagePrivCodeValue();
				}
			}
			else if(this.userView.getStatus()==4)
			{
				String a0100=this.userView.getA0100();
				String pre = this.userView.getDbname();
				String unit=bo.getB0110(pre, a0100);
				if(unit!=null&&!"".equals(unit))
				{
					b0110 = unit;
				}
			}
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag="";
			if(map.get("returnflag")!=null)
			{
				returnflag=(String)map.get("returnflag");
			}else
			{
				returnflag=(String)map.get("returnflag");
			}
			this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
			String subsys_id = (String)map.get("subsys_id");
			/**method=0,直接进入考核模板模块，否则从考核计划中进入模板时，如果360度考核的话，不出现有个性指标的模板参数 method=1是360=2是目标管理*/
			String method=(String)map.get("method");
			String isVisible=(String)map.get("isVisible");
			String planStatus="0";
			String templateId=(String)map.get("templateid");
			String persionControl=(String)map.get("persionControl"); //是否要权限控制模板树的展现 yes 是 no 否  JinChunhai 2011.03.02
			if(persionControl==null || persionControl.length()<=0)
			{
				persionControl="yes";
			}
			map.remove("persionControl");
			if(templateId==null|| "".equals(templateId))
				templateId="-1";
			if(isVisible!=null&& "2".equals(isVisible))
			{
				/**标志模板是否可修改=0查看，=1修改*/
				 planStatus=(String)map.get("isEdit");
				 map.remove("isEdit");
				
			}
			this.getFormHM().put("planStatus", planStatus);
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("il_body");
			String rootdesc=ResourceFactory.getProperty("lable.kh.template");
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/servlet/performance/KhTemplateTree?templatesetid=-1&subsys_id="+subsys_id+"&b0110="+b0110+"&isVisible="+isVisible+"&method="+method+"&persionControl="+persionControl);
		    treeItem.setAction("javascript:void(0)");
		    this.getFormHM().put("tree",treeItem.toJS());
		    this.getFormHM().put("subsys_id",subsys_id);
		    this.getFormHM().put("isVisible", isVisible);
		    this.getFormHM().put("method", method);
		    this.getFormHM().put("t_tid",templateId);
		    this.getFormHM().put("persionControl", persionControl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
