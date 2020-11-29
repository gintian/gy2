package com.hjsj.hrms.transaction.org.orgdata;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class OrgDataTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String infor = (String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"2";
		reqhm.remove("infor");
		
		String codeitem = (String)reqhm.get("codeitem");
		codeitem=codeitem!=null&&codeitem.trim().length()>0?codeitem:"UN";
		reqhm.remove("codeitem");
		
		String checkorg = "";
		if(!"UN".equalsIgnoreCase(codeitem)){
			FieldItem item = DataDictionary.getFieldItem(codeitem);
			checkorg = codeitem;
			if(item!=null&&item.isCode()){
				if("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid()))
					checkorg="UN";
			}
		}

		String setname = "B01";
		String loadtype="1";
		if("3".equals(infor)){
			loadtype="0";
			setname = "K01";
		}
		
		this.getFormHM().put("infor", infor);
		this.getFormHM().put("loadtype", loadtype);
		this.getFormHM().put("setname", setname);
		this.getFormHM().put("codeitem", codeitem);
		this.getFormHM().put("codeitemlist", codeItemList(infor));
		this.getFormHM().put("checkorg", checkorg);
	}
	private ArrayList codeItemList(String infor){
		ArrayList list = new ArrayList();
		ArrayList fieldlist = new ArrayList();
		if("2".equals(infor))
			fieldlist = this.userView.getPrivFieldList("B01",Constant.USED_FIELD_SET);
		else
			fieldlist = this.userView.getPrivFieldList("K01",Constant.USED_FIELD_SET);
		CommonData temp=new CommonData("UN",ResourceFactory.getProperty("general.inform.search.org"));
		list.add(temp);
		for(int i=0;i<fieldlist.size();i++){
			FieldItem item = ( FieldItem)fieldlist.get(i);
			if(item.isCode()){
				if("B0110".equalsIgnoreCase(item.getItemid())|| "E0122".equalsIgnoreCase(item.getItemid())
						|| "E01A1".equalsIgnoreCase(item.getItemid())){
					continue;
				}else{
					CommonData temp1=new CommonData(item.getItemid(),item.getItemdesc());
					list.add(temp1);
				}
			}
		}
		return list;
	}
}
