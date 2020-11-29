package com.hjsj.hrms.module.org.virtualorg.trans;

import com.hjsj.hrms.module.org.virtualorg.bo.VirturalRoleTransBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;

/***
 * 
 * <p>Title: GetVirturalRoleTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  Nov 18, 2016 6:20:37 PM</p>
 * @author changxy 
 * @version 1.0
 * 虚拟角色（增删,修改） codesetid 83
 */
public class EditRoleTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		String parentid=(String)this.getFormHM().get("parentid");
		String type=(String)this.getFormHM().get("types");//判断是修改 添加 保存 操作
		VirturalRoleTransBo bo=new VirturalRoleTransBo(this.frameconn,this.userView);
		boolean flag=false;
		/*if("add".equalsIgnoreCase(type))
		   flag=bo.addRoleTrans(parentid,codeitemid);
		else*/ if("del".equalsIgnoreCase(type)){
			flag=bo.delVirRole(codeitemid);
		}
		else if("update".equalsIgnoreCase(type)){
			ArrayList<MorphDynaBean> codeitemdescList=(ArrayList<MorphDynaBean>)this.getFormHM().get("updatemapList");
			ArrayList<MorphDynaBean> insertMapList=(ArrayList<MorphDynaBean>)this.getFormHM().get("insertmapList");			
			if(codeitemdescList.size()>0)
				flag=bo.updateVirRole(codeitemdescList);
			if(insertMapList.size()>0)
				flag=bo.addRoleTrans(insertMapList);
		}	
			this.getFormHM().put("flag", flag);
		
	}

	
}
