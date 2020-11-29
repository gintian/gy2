package com.hjsj.hrms.transaction.general.chkformula;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String tableid = (String)reqhm.get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		reqhm.remove("tableid");
		
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"0";
		reqhm.remove("flag");
		
		String chkid = (String)reqhm.get("chkid");
		chkid=chkid!=null&&chkid.trim().length()>0?chkid:"";
		reqhm.remove("chkid");
		
		ArrayList itemlist = new ArrayList();
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
			ArrayList list = changebo.getAllFieldItem();
			HashMap sub_domain_map = changebo.getSub_domain_map();
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)list.get(i);
				String itemdesc = "";
				if(fielditem.isChangeAfter()&&!fielditem.isMemo()){
					if(stritem.indexOf(fielditem.getItemid()+"_2")!=-1)
						continue;
					stritem+=fielditem.getItemid()+"_2,";
					itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
				}else if(fielditem.isChangeBefore()){
					if(sub_domain_map!=null&&sub_domain_map.get(""+i)!=null&&sub_domain_map.get(""+i).toString().trim().length()>0){
						if(stritem.indexOf(fielditem.getItemid()+"_"+sub_domain_map.get(""+i)+"_1")!=-1)
							continue;
						}else{
							if(stritem.indexOf(fielditem.getItemid()+"_1")!=-1)
								continue;
						}
					if(sub_domain_map!=null&&sub_domain_map.get(""+i)!=null&&sub_domain_map.get(""+i).toString().trim().length()>0){
						stritem+=fielditem.getItemid()+"_"+sub_domain_map.get(""+i)+"_1,";
						fielditem.setItemdesc(""+sub_domain_map.get(""+i+"hz"));
						fielditem.setItemid(fielditem.getItemid()+"_"+sub_domain_map.get(""+i)+"_1 ");
					}else{
						stritem+=fielditem.getItemid()+"_1,";
					}
					
					if("A01".equalsIgnoreCase(fielditem.getFieldsetid())){
						itemdesc=fielditem.getItemdesc();
					}else{
						itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
					}
				} else {
					itemdesc=fielditem.getItemdesc();
				}
				if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())
						&&!fielditem.isMemo()&&!"attachment".equalsIgnoreCase(fielditem.getItemid())){
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
					itemlist.add(dataobj);
				}
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.frameconn,tableid);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(fielditem.getItemid())!=-1)
				continue;
			stritem+=fielditem.getItemid()+",";
			CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),fielditem.getItemdesc());
			itemlist.add(dataobj);
		}
		
		CommonData dataobj = new CommonData("","");
		itemlist.add(0,dataobj);
		
		this.getFormHM().put("sql","select chkId,Name,Information,tabid");
		this.getFormHM().put("where","from hrpChkformula where tabid='"+tableid+"' and flag='"+flag+"'");
		this.getFormHM().put("column","chkId,Name,Information,tabid");
		this.getFormHM().put("orderby"," order by seq");
		this.getFormHM().put("tabid",tableid);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("chkid",chkid);
	}

}
