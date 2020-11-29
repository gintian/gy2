package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.org.autostatic.confset.ViewHideSortBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String subset = (String)reqhm.get("subset");
		reqhm.remove("subset");
		subset=subset!=null?subset:"";
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		this.getFormHM().put("sortfieldlist", sortDieldList(dao,subset));
		this.getFormHM().put("subset",subset);
	}
	private ArrayList sortDieldList(ContentDAO dao,String setid){
		ArrayList sortlist = new ArrayList();
		ViewHideSortBo vsbo = new ViewHideSortBo(dao,this.userView,setid);
		String sortfield=vsbo.getSortitem();
		sortfield=sortfield!=null?sortfield:"";
		try {
			if(sortfield.trim().length()>0){
				CommonData obj=null;
				String fieldArr[] = sortfield.split(",");
				if(fieldArr!=null&&fieldArr.length>0){
					for(int i=0;i<fieldArr.length;i++){
						if(fieldArr[i]!=null&&fieldArr[i].trim().length()>0){
							FieldItem fi= null;
							if(!setid.startsWith("K")){
								if("id".equalsIgnoreCase(fieldArr[i])){
									fi=new FieldItem();
									fi.setItemid("id");
									fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
								}else{
									if("0".equals(this.userView.analyseFieldPriv(fieldArr[i])))
										continue;
									fi=DataDictionary.getFieldItem(fieldArr[i]);
								}
							}else{
								if("B0110".equalsIgnoreCase(fieldArr[i])){
									fi=new FieldItem();
									fi.setItemid("B0110");
									fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
								}else if("id".equalsIgnoreCase(fieldArr[i])){
									fi=new FieldItem();
									fi.setItemid("id");
									fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
								}else{
									if("0".equals(this.userView.analyseFieldPriv(fieldArr[i])))
										continue;
									fi=DataDictionary.getFieldItem(fieldArr[i]);
								}
							}
							obj=new CommonData(fi.getItemid(),fi.getItemdesc());
							sortlist.add(obj);
						}
					}
				}
				//当有新指标添加了，排序中未有此指标时：xuj 2010-4-7
				ArrayList fieldset = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
				for(int i=0;i<fieldset.size();i++){
					FieldItem fielditem = (FieldItem)fieldset.get(i);
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
						if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
							continue;
						if(sortfield.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())==-1){
							obj=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
							sortlist.add(obj);
						}
					}
				}
			}else{
				ArrayList fieldset = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
				if(!setid.startsWith("K")){
					FieldItem fi=DataDictionary.getFieldItem("B0110");
					CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
					sortlist.add(obj);
				}else{
					FieldItem fi=new FieldItem();
					fi.setItemid("B0110");
					fi.setFieldsetid("UM");
					fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
					CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
					sortlist.add(obj);
					FieldItem efi=DataDictionary.getFieldItem("E01A1");			
					obj=new CommonData(efi.getItemid(),efi.getItemdesc());
					sortlist.add(obj);
				}
				FieldItem fi=new FieldItem();
				fi.setItemid("id");
				fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
				CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
				sortlist.add(obj);
				for(int i=0;i<fieldset.size();i++){
					FieldItem fielditem = (FieldItem)fieldset.get(i);
					if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
						if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
							continue;
						obj=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
						sortlist.add(obj);
					}
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sortlist;
	}
}
