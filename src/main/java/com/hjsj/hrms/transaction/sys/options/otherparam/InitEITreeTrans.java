package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InitEITreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String froms=(String) reqhm.get("froms");
		String defaultInput="1";
		String input=(String)reqhm.get("input");
		if(input!=null&&input.length()>0)
			defaultInput=input;
		reqhm.remove("input");
		String xml="/servlet/eitemtree?param=root";
		if("item".equals(froms)){
			String codese=(String) reqhm.get("codesetid"); 
			//系統管理，參數設置，人员类别指标范围报错  jingq upd 2014.10.24
			codese = PubFunc.keyWord_reback(codese);
			String[] c=codese.split("/");
			xml="/servlet/eitemtree?param=root&codesetid="+c[1];
		}
		TreeItemView treeItem = new TreeItemView();
		treeItem.setName("root");
		treeItem.setIcon("/images/group.gif");
		treeItem.setTarget("");
		treeItem.setRootdesc("人员指标");
		treeItem.setText("人员指标");
		treeItem.setLoadChieldAction(xml);
		treeItem.setAction("");
//		UserView uv=this.getUserView();
		String checkvalue=",";
		try{
			OtherParam op=new OtherParam(this.getFrameconn());
			if("item".equals(froms)){
				String name=(String) reqhm.get("name");
				String codefield=(String) reqhm.get("codesetid");
				//系統管理，參數設置，人员类别指标范围报错  jingq upd 2014.10.24
				codefield = PubFunc.keyWord_reback(codefield);
				String[] codefields=codefield.split("/");
				String codesetid=codefields[0];
				String feild=codefields[1];
				Map myMap=op.getEmployeeType(feild,codesetid,name);
				checkvalue=(String)myMap.get("table")+(String)myMap.get("field");
				reqhm.remove("codesetid");
				reqhm.remove("name");
			}else{
				String name=(String) reqhm.get("name");
				reqhm.remove("name");
				Map myMap=op.getBaseFieldMap(name);
				checkvalue=(String)myMap.get("table")+(String)myMap.get("field");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		hm.put("treecode","Global.checkvalue=',"+checkvalue+",';"+"Global.defaultInput="+defaultInput+";Global.showroot=false;"+treeItem.toJS());
//		System.out.println(hm.get("treecode").toString());
//		hm.put("treecode",this.getfistlay(uv));

	}
	private List getFieldlist(UserView uv){
		List myList=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		return myList;
	}
	private String  getfistlay(UserView uv){
		StringBuffer sbxml=new StringBuffer();
		List fieldlist=this.getFieldlist(uv);
		for(Iterator it=fieldlist.iterator();it.hasNext();){
			FieldSet fs=(FieldSet)it.next();
			TreeItemView treeitem = new TreeItemView();
//			String fieldsetdesc = fs.getFieldsetdesc();
			String fieldsetdesc = fs.getCustomdesc();
			
			treeitem.setName("root");
			treeitem.setRootdesc(fieldsetdesc);
			treeitem.setTitle(fieldsetdesc);
			treeitem.setLoadChieldAction("/servlet/eitemtree?param=child&fid="+fs.getFieldsetid());
			treeitem.setIcon("/images/groups.gif");
			treeitem.setAction("");
			sbxml.append("Global.defaultInput=1;"+treeitem.toJS());
			
		}
		
		
		return sbxml.toString();
	}

}
