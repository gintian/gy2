package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.CodeItemBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SearchCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		TempvarBo tempvarbo = new TempvarBo();
		
		String itemid = (String)hm.get("itemid");
		itemid = itemid!=null&&itemid.length()>0?itemid:"";
		
		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid = fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		String tempid = (String)hm.get("tempid");
		if(itemid.trim().length()>0){
			if("tempvar".equalsIgnoreCase(fieldsetid)|| "vartemp".equalsIgnoreCase(fieldsetid)){
				hm.put("codelist",tempvarbo.codeTempList(this.frameconn,itemid));
			}else{
				if(tempid!=null && "T_item6_5".equalsIgnoreCase(tempid)){
					hm.put("codelist",tempvarbo.codeListForFormula(itemid));//获得“代码转名称2这个公式的列表”
					//hm.put("codelist",tempvarbo.codeList(this.frameconn,itemid));
				}else{
					ArrayList list=new ArrayList();
					list=tempvarbo.titleList(this.frameconn,itemid);
					hm.put("codelist",tempvarbo.codeList(this.frameconn,itemid));
					hm.put("str_value",list.get(list.size()-1));
				}
			}
			if("escope".equals(itemid)){
				hm.put("typeid","A");
			}else{
				FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			    if(fielditem!=null){
				  hm.put("typeid",fielditem.getItemtype());
			    }
			}
			
		}

		String standid = (String)hm.get("standid");
		standid = standid!=null&&standid.length()>0?standid:"";
		hm.remove("standid");
		if(standid.trim().length()>0){
			standid = standid.replaceAll("\"", "");
			String salaryid = (String)hm.get("salaryid");
			salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
			String tabid = (String)hm.get("tabid");
			tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
			
			String checktemp = (String)hm.get("checktemp");
			checktemp=checktemp!=null&&checktemp.trim().length()>0?checktemp:"";
			
			
			ArrayList hfactorlsit = new ArrayList();
			ArrayList vfactorlsit = new ArrayList();
			ArrayList s_hfactorlsit = new ArrayList();
			ArrayList s_vfactorlsit = new ArrayList();
			ArrayList itemidlist = new ArrayList();
			String[] arr = standid.split(":");
			if(arr.length==7){
				String hfactor = "";
				String hfcodesetid = "";
				String vfactor= "";
				String vfcodesetid = "";
				String s_hfactor= "";
				String s_hfcodesetid = "";
				String s_vfactor = "";
				String s_vfcodesetid = "";
				String itemtype = "";
				String itemname = "";
				
				if(arr[0].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[0]);
					if(item!=null){
						hfcodesetid=item.getCodesetid();
						hfactor=item.getItemdesc();
						CommonData dataobj = new CommonData("","");
						hfactorlsit.add(0,dataobj);
					}
				}
				if(arr[1].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[1]);
					if(item!=null){
						vfcodesetid=item.getCodesetid();
						vfactor=item.getItemdesc();
						CommonData dataobj = new CommonData("","");
						vfactorlsit.add(0,dataobj);
					}
				}
				if(arr[2].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[2]);
					if(item!=null){
						s_hfcodesetid=item.getCodesetid();
						s_hfactor=item.getItemdesc();
						CommonData dataobj = new CommonData("","");
						s_hfactorlsit.add(0,dataobj);
					}
				}
				if(arr[3].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[3]);
					if(item!=null){
						s_vfcodesetid=item.getCodesetid();
						s_vfactor=item.getItemdesc();
						CommonData dataobj = new CommonData("","");
						s_vfactorlsit.add(0,dataobj);
					}
				}
				if(arr[5].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[5]);
					if(item!=null){
						itemtype=item.getItemtype();
						itemname=item.getItemdesc();
						CommonData dataobj = new CommonData("","");
						itemidlist.add(dataobj);
					}
				}

				CodeItemBo codebo = new CodeItemBo(this.frameconn,this.userView);
				codebo.setHfcodesetid(hfcodesetid);
				codebo.setVfcodesetid(vfcodesetid);
				codebo.setItemtype(itemtype);
				codebo.setS_hfcodesetid(s_hfcodesetid);
				codebo.setS_vfcodesetid(s_vfcodesetid);
				if("salary".equalsIgnoreCase(checktemp)){
					if(tabid.trim().length()>0)
						codebo.getTableList(tabid);
					else{
						if(salaryid!=null&&salaryid.trim().length()>0){
							codebo.getMidVariableList(salaryid);
						}else{
							codebo.functionList();
						}
					}
				}else if("temp".equalsIgnoreCase(checktemp)){
					codebo.functionList();
				}else{
					if(salaryid!=null&&salaryid.trim().length()>0){
						codebo.getMidVariableList(salaryid);
					}else{
						codebo.functionList();
					}
				}
				hfactorlsit.addAll(codebo.getHfactorlist());
				vfactorlsit.addAll(codebo.getVfactorlist()) ;
				s_hfactorlsit.addAll(codebo.getS_hfactorlist());
				s_vfactorlsit.addAll(codebo.getS_vfactorlist());
				itemidlist.addAll(codebo.getItemidlist());

				StringBuffer str=new StringBuffer(); 
				StringBuffer hlstr=new StringBuffer();
				if(arr[6].trim().length()>0){
					str.append(arr[6]+".");
					hlstr.append(arr[6]+".");
				}
				if(arr[4].trim().length()>0){
					str.append(arr[4]);
					hlstr.append(arr[4]);
				}
				str.append("<br>(");
				hlstr.append("<br>(");
				if(hfactor.trim().length()>0){
					str.append(hfactor+",");
					hlstr.append(hfactor+",");
				}else{
					str.append("空,");
				}
				if(s_hfactor.trim().length()>0){
					str.append(s_hfactor+",");
				}else{
					str.append("空,");
				}
				if(vfactor.trim().length()>0){
					str.append(vfactor+",");
					hlstr.append(vfactor+",");
				}else{
					str.append("空,");
				}
				if(s_vfactor.trim().length()>0){
					str.append(s_vfactor);
					hlstr.append(vfactor+",");
				}else{
					str.append("空");
					if(vfactor.trim().length()<1){
						hlstr.append(",");
					}
				}
				if(itemname.trim().length()>0){
					hlstr.append(itemname);
				}
				str.append("): ");
				hlstr.append(") ");
				if(arr[5].trim().length()>0){
					FieldItem item = DataDictionary.getFieldItem(arr[5]);
					if(item!=null){
						str.append(item.getItemdesc());
						hlstr.append(item.getItemdesc());
					}
				}
				
				
				hm.put("hfactorlsit",hfactorlsit);
				hm.put("vfactorlsit",vfactorlsit);
				hm.put("s_hfactorlsit",s_hfactorlsit);
				hm.put("s_vfactorlsit",s_vfactorlsit);
				hm.put("itemidlist",itemidlist);
				hm.put("str",str.toString());
				hm.put("hlstr",hlstr.toString());
			}
			
		}
			
	}
}
