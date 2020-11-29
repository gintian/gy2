package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionWizardTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String checktemp = (String)reqhm.get("checktemp");
		checktemp=checktemp!=null&&checktemp.trim().length()>0?checktemp:"";
		
        this.getFormHM().put("strexpression","");
		
		this.getFormHM().put("numexpression1","");
		this.getFormHM().put("numexpression2","");
		
		this.getFormHM().put("dateexpression1","");
		this.getFormHM().put("dateexpression2","");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		functionList(dao);
		this.getFormHM().put("datestr","");
		this.getFormHM().put("checktemp",checktemp);
		this.getFormHM().put("standid","");
		ProjectSet projectset = new ProjectSet();
		ArrayList standlist = projectset.standList(dao,this.userView);
		ArrayList standidlist = projectset.standidList(dao,this.userView);
		this.getFormHM().put("standlist",standlist);
		this.getFormHM().put("standidlist",standidlist);
		this.getFormHM().put("tabid","");
		this.getFormHM().put("statlist",projectset.condList());
		this.getFormHM().put("rangelist",projectset.rangeList());
		functionListunit();
		functionListpos();
		
		CommonData obj1 = new CommonData("", "");
		ArrayList fieldsetlist = new ArrayList();
		fieldsetlist.add(0,obj1);
		obj1 = new CommonData("Q03" ,"Q03"+"-"+"日明细");
		fieldsetlist.add(obj1);
		this.getFormHM().put("fieldsetlist", fieldsetlist);

	}
	private void functionListunit(){
		ArrayList listset = new ArrayList();
		ArrayList fieldsetlistunit = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlistunit.add(obj1);
			listset = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 CommonData obj=new CommonData(fieldset.getFieldsetid()
							,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistunit.add(obj);
			}
			this.getFormHM().put("fieldsetlistunit",fieldsetlistunit);
	}
	private void functionListpos(){
		ArrayList listset = new ArrayList();
		ArrayList fieldsetlistpos = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlistpos.add(obj1);
		listset = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 CommonData obj=new CommonData(fieldset.getFieldsetid()
							,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistpos.add(obj);
			}
			this.getFormHM().put("fieldsetlistpos",fieldsetlistpos);
	}
	 /**
     * 查询子集
     * @param dao
     * @return retlist
     * @throws GeneralException
     */
	private void functionList(ContentDAO dao){
		CommonData obj1=new CommonData("","");
		ArrayList alist=new ArrayList();
		alist.add(0,obj1);
		ArrayList dlist = new ArrayList();
		dlist.add(0,obj1);
		ArrayList nlist = new ArrayList();
		nlist.add(0,obj1);
		ArrayList itemlist = new ArrayList();
		itemlist.add(0,obj1);
		ArrayList vlist = new ArrayList();
		vlist.add(0,obj1);
		StringBuffer codestr = new StringBuffer();
		StringBuffer strarr = new StringBuffer();
	    ArrayList listitem=  DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);	
	    if(listitem!=null){
				 for(int j=0;j<listitem.size();j++){
					 FieldItem item = (FieldItem)listitem.get(j);
					 if("A".equalsIgnoreCase(item.getItemtype())){
						 CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),item.getItemdesc());
						 if(item.isCode()){
							itemlist.add(obj);
							codestr.append(item.getItemid()+":");
							codestr.append(item.getItemdesc()+":");
							codestr.append(item.getItemtype()+",");
						 }else{
							 strarr.append(item.getItemid()+":");
							 strarr.append(item.getItemdesc()+":");
							 strarr.append(item.getItemtype()+",");
						 }
						alist.add(obj);
						vlist.add(obj);
					 }else if("N".equalsIgnoreCase(item.getItemtype())){
						 CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),item.getItemdesc());
						nlist.add(obj);
						vlist.add(obj);
						strarr.append(item.getItemid()+":");
						strarr.append(item.getItemdesc()+":");
						strarr.append(item.getItemtype()+",");
					 }else if("D".equalsIgnoreCase(item.getItemtype())){
						 CommonData obj=new CommonData(item.getItemid()+":"+item.getItemdesc(),item.getItemdesc());
						dlist.add(obj);
						vlist.add(obj);
						strarr.append(item.getItemid()+":");
						strarr.append(item.getItemdesc()+":");
						strarr.append(item.getItemtype()+",");
					}
				 }
		}
		this.getFormHM().put("alist",alist);
		this.getFormHM().put("dlist",dlist);
		this.getFormHM().put("nlist",nlist);
		this.getFormHM().put("vlist",vlist);
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("codearr",codestr.toString());
		this.getFormHM().put("strarr",strarr.toString());
	}
}
