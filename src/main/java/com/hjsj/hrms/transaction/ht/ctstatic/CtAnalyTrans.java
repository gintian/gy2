package com.hjsj.hrms.transaction.ht.ctstatic;

import com.hjsj.hrms.businessobject.ht.ctstatic.CtstaticBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class CtAnalyTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a_code = (String)hm.get("a_code");
		a_code=a_code!=null?a_code:"";
		hm.remove("a_code");
		
		String reset = (String)hm.get("reset");
		reset=reset!=null?reset:"";
		hm.remove("reset");
		
		ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
		String dbstr=csxml.getTextValue("/Params/nbase");
		dbstr=dbstr!=null?dbstr:"";
		
		ArrayList dblist = dbList(dbstr);
		if(dblist.size()==0)
			throw new GeneralException("没有设置劳动合同中的人员库或是没有设置的劳动合同中的人员库的权限!");
		this.getFormHM().put("dblist",dblist);
		
		/**应用库前缀*/
		String dbname=(String)this.getFormHM().get("dbname");
		dbname=dbname!=null?dbname:"";
		if(dbname.trim().length()<1){
			CommonData temp=(CommonData)dblist.get(0);
			dbname = temp.getDataValue();
		}
		this.getFormHM().put("dbname",dbname);
		
		String htmain = csxml.getTextValue("/Params/htmain");
		htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
		if(htmain.trim().length()<1){
			htmain = csxml.getConstantValue("HETONGMAIN");
			htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
		}
		
		String htset = csxml.getTextValue("/Params/htset");
		htset=htset!=null&&htset.trim().length()>0?htset:"";
		if(htset.trim().length()<1){
			htset = csxml.getConstantValue("HETONGSET");
			htset=htset!=null&&htset.trim().length()>0?htset:"";
		}
		ArrayList setlist=itemList(htmain,htset);
		if(setlist.size()==0)
			throw new GeneralException("没有设置合同子集或是没有设置的合同子集的权限!");
		String setid=(String)this.getFormHM().get("setid");
		setid=setid!=null?setid:"";
//		if(setid.length()<1){
		String isFromLeft = (String) this.getFormHM().get("isFromLeft");
		if(setid.length()<1 || "1".equals(isFromLeft)){
			this.getFormHM().put("isFromLeft", "0");
			if(setlist!=null||setlist.size()>0){
				CommonData temp=(CommonData)setlist.get(0);
				setid = temp.getDataValue();
			}
		}
		this.getFormHM().put("setid", setid);
		this.getFormHM().put("setlist", setlist);

		ArrayList itemlist= fielditemList(setid);
		if(itemlist.size()==0){
			FieldSet fielset=DataDictionary.getFieldSetVo(setid);
			throw new GeneralException(fielset.getFieldsetdesc()+"中的指标没有权限!");
		}
		
		ArrayList valuelist= new ArrayList();
		String tabstr = "";
		String charname="";
		
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null?itemid:"";
		if("1".equals(reset)) {
			itemid="";
			this.getFormHM().remove("right_fields");
			this.getFormHM().put("fieldsSel", new ArrayList());
			
		}
		
		if((itemid.trim().length()<1&&itemlist.size()>0)|| "1".equals(isFromLeft)){
			CommonData temp=(CommonData)itemlist.get(0);
			itemid = temp.getDataValue();
		}    
		if(itemid!=null&&itemid.length()>0){
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			charname=fielditem.getItemdesc()+"结构";
			ContentDAO dao = new ContentDAO(this.frameconn);
			CtstaticBo ctbo = new CtstaticBo(dao,this.userView);
			String dbArr[] = dbname.split(",");
			int countall = 0;
			for(int i=0;i<dbArr.length;i++){
				if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
					int count = ctbo.countAll(dbArr[i],setid,a_code);
					ArrayList valuelist1 = ctbo.valueList(dbArr[i], setid, itemid, count,a_code);
					valuelist1 = updateOrgList(valuelist1,a_code);
					valuelist = ctbo.addValueList(valuelist,valuelist1);
					countall+=count;
				}
			}
			tabstr = ctbo.tabStr(valuelist,itemid, countall);
		}  
		this.getFormHM().put("itemid", itemid);
		this.getFormHM().put("orgcode", a_code);
		this.getFormHM().put("code", a_code);
		this.getFormHM().put("dbname", dbname);
		this.getFormHM().put("tablestr", tabstr);
		this.getFormHM().put("valuelist", valuelist);
		this.getFormHM().put("charname", charname);
		this.getFormHM().put("itemlist", itemlist);
	}  
	/**
	 * 代码类是组织机构时，过滤没有数据的组织机构 wangb 20180706
	 * @return 过滤后的机构树
	 */
	private ArrayList updateOrgList(ArrayList valuelist , String codeitem){
		ArrayList list = new ArrayList();
		if(!("UN".equalsIgnoreCase(codeitem) || "UM".equalsIgnoreCase(codeitem) || "@K".equalsIgnoreCase(codeitem))) //不是组织机构不处理
			return valuelist;
		if(valuelist.size() <= 50) // 机构总数不超过50个不过滤 没有数据机构 wangb 20180711
			return valuelist;
		for( int i = 0 ; i < valuelist.size() ; i++){
			CommonData temp = (CommonData) valuelist.get(i);
			if("0".equalsIgnoreCase(temp.getDataValue()))
				continue;
			list.add(temp);
		}
		return list;
	}
	
	private ArrayList itemList(String htmain,String htset){
		ArrayList setlist=new ArrayList();
		ArrayList list=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		FieldSet setmain = null;
		for(int i=0;i<list.size();i++){
			FieldSet fieldset=(FieldSet)list.get(i);
			/**未构库不加进来*/
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("A01".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			ArrayList checklist=this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
			if(checklist.size()<1)
				continue;
			if(htmain.length()>0){
				if(htmain.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())!=-1){
					setmain = fieldset;
					continue;
				}
			}
			if(htset.length()>0){
				if(htset.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())==-1)
					continue;
			}
			CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			setlist.add(temp);
		}//for i loop end.
		if(setmain!=null){
			CommonData temp=new CommonData(setmain.getFieldsetid(),setmain.getCustomdesc());
			setlist.add(0,temp);
		}
		return setlist;
	}
	private ArrayList dbList(String dbstr){
		/**库前缀列表*/
		ArrayList list=this.userView.getPrivDbList();
		ArrayList dblist=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			String pre=(String)list.get(i);
			if(dbstr.length()>0){
				if(dbstr.toLowerCase().indexOf(pre.toLowerCase())==-1) {
					continue;
				} else {
					CommonData data=new CommonData(pre,AdminCode.getCodeName("@@", pre));
					dblist.add(data);
				}
			} 
			
		}//for i loop end.
		return dblist;
	}
	private ArrayList fielditemList(String setid){
		ArrayList itemlist= new ArrayList();
		if(setid.trim().length()>0){
			ArrayList fieldlist = this.userView.getPrivFieldList(setid, Constant.USED_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
				if(fielditem!=null){
					if("A".equalsIgnoreCase(fielditem.getItemtype())){
						if(fielditem.getCodesetid()==null|| "0".equalsIgnoreCase(fielditem.getCodesetid())){
							continue;
						}
					}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
						continue;
					}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
						if(fielditem.getDecimalwidth()>0)
							continue;
					}
					CommonData temp=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					itemlist.add(temp);
				}
			}
		}
		return itemlist;
	}
}
