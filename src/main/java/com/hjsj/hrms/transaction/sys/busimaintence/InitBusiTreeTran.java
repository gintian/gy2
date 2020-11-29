package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InitBusiTreeTran extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String cid=(String) reqhm.get("cid");
		
		reqhm.remove("cid");
//		System.out.println(cid);
		String target = "mil_body";
		String xml = "/servlet/BusiTree?params=root&parentid=00" + "&target="
		+ target;
		TreeItemView treeItem = new TreeItemView();
		if(cid!=null&&cid.length()>0){
			xml= "/servlet/BusiTree?params=root&parentid=00" + "&target="
			+ target+"&cid="+cid;
			List dylist=(ArrayList)dao.searchDynaList("select * from t_hr_subsys where id='"+cid+"'");
			if(dylist!=null&&dylist.size()>0){
				DynaBean dyna=(DynaBean)dylist.get(0);
				treeItem.setName("root");
				treeItem.setIcon("/images/add_all.gif");
				treeItem.setTarget(target);
				treeItem.setRootdesc((String)dyna.get("id")+" "+(String) dyna.get("name"));
				treeItem.setText((String)dyna.get("id")+" "+(String) dyna.get("name"));
				treeItem.setLoadChieldAction(xml);
				treeItem.setAction("/system/busimaintence/ShowSubsys.do?b_query=link&id="+(String)dyna.get("id"));
			}else{
				
				throw GeneralExceptionHandler.Handle(new GeneralException("","<"+cid+">不存在！","",""));
			}
		}else{
			treeItem.setName("root");
			treeItem.setIcon("/images/add_all.gif");
			treeItem.setTarget(target);
			treeItem.setRootdesc("业务字典维护");
			treeItem.setText("业务字典维护");
			treeItem.setLoadChieldAction(xml);
			treeItem.setAction("/system/busimaintence/showbusiname.do?b_query=link");
		}
			
		hm.put("busitree",treeItem.toJS());
		try
		{
		   String dev_flag=SystemConfig.getPropertyValue("dev_flag");
		   if(dev_flag==null||dev_flag.length()<=0)
			  dev_flag="0";
		   this.getFormHM().put("userType", dev_flag);//=0/=null用户模式，=1开发商模式
		}catch(Exception e)
		{
			
		}
		//System.out.println(treeItem.toJS());
	}

}
