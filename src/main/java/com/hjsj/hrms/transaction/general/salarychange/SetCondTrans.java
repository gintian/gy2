package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		if (hm.get("type")!=null){
		    if("setConditionsInfo".equals(hm.get("type"))){//把计算条件放进userview，解决使用url参数过长的问题
		        String conditions = (String)hm.get("conditions");  
		        this.userView.getHm().put("template_calcConditon", conditions);
		    }
		    hm.remove("type");
		    return;
		}
		
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String tableid = (String)reqhm.get("tableid");
		tableid=tableid!=null&&tableid.length()>0?tableid:"";
		reqhm.remove("tableid");
		/*
		String conditions = (String)reqhm.get("conditions");
		conditions=conditions!=null&&conditions.length()>0?conditions:"";
		reqhm.remove("conditions");
		*/
		
		String conditions = (String)this.userView.getHm().get("template_calcConditon");
		conditions=conditions!=null&&conditions.length()>0?conditions:"";
		hm.put("conditions",SafeCode.decode(conditions));
		this.userView.getHm().remove("template_calcConditon");
		
		
		ArrayList itemlist = new ArrayList();
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
			ArrayList list = changebo.getAllFieldItem();
			int infor_type =changebo.getInfor_type();
			HashMap map = changebo.getSub_domain_map();
			HashMap field_name_map = changebo.getField_name_map();
//			CommonData dataobj1 = new CommonData("b0110:单位名称","单位名称");
//			itemlist.add(dataobj1);
			
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)(((FieldItem)list.get(i)).cloneItem());
				if(infor_type!=1&&("codesetid".equalsIgnoreCase(fielditem.getItemid())|| "codeitemdesc".equalsIgnoreCase(fielditem.getItemid())|| "corcode".equalsIgnoreCase(fielditem.getItemid())|| "parentid".equalsIgnoreCase(fielditem.getItemid())|| "start_date".equalsIgnoreCase(fielditem.getItemid())))
				{
					continue;
				}
				if(fielditem.getVarible()==2){//去掉子集
					continue;
				}
				if("photo".equalsIgnoreCase(fielditem.getItemdesc()))
					continue;
				if("attachment".equalsIgnoreCase(fielditem.getItemdesc()))
					continue;
				if("ext".equalsIgnoreCase(fielditem.getItemdesc()))
					continue;
				String itemdesc = "";
				if(fielditem.isChangeAfter()){
					if(stritem.indexOf(fielditem.getItemid()+"_2,")!=-1)
						continue;
					stritem+=fielditem.getItemid()+"_2,";
					itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
				}else if(fielditem.isChangeBefore()){
					//多个变化前加上_id
					String sub_domain_id="";
					if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
					
					sub_domain_id ="_"+(String)map.get(""+i);
					}
					if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1,")!=-1)
						continue;
					if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
						continue;
					stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
//					if(sub_domain_id!=null&&sub_domain_id.length()>0){
//					fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
//					fielditem.setItemdesc(""+map.get(""+i+"hz"));
//					}
					if("A01".equalsIgnoreCase(fielditem.getFieldsetid())){
						itemdesc=fielditem.getItemdesc();
					}else{
						itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
					}
				}else{
					if(stritem.indexOf(fielditem.getItemid())!=-1)
						continue;
					stritem+=fielditem.getItemid()+",";
					itemdesc=fielditem.getItemdesc();
				}
				CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
				itemlist.add(dataobj);
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.frameconn,tableid);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(fielditem.getItemid())!=-1)
				continue;
			CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),fielditem.getItemdesc());
			itemlist.add(dataobj);
		}
		CommonData dataobj = new CommonData("","");
		itemlist.add(0,dataobj);
		
		
		

		hm.put("itemid","");
		hm.put("itemlist",itemlist);
		hm.put("tableid",tableid);
	}
}
