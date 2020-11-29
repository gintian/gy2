package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class FunctionWizardTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String tableid = (String)reqhm.get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		reqhm.remove("tableid");
		
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");
		
		fieldItemList(tableid);
		tempfieldsetlist(tableid);
		ProjectSet projectset = new ProjectSet();
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	    ArrayList standlist = projectset.standList(dao,this.userView);
		ArrayList standidlist = projectset.standidList(dao,this.userView);
		functionListunit();
		functionListpos();
		this.getFormHM().put("checktemp","salary");
		this.getFormHM().put("standlist",standlist);
		this.getFormHM().put("standidlist",standidlist);
		this.getFormHM().put("tabid",tableid);
		this.getFormHM().put("salaryid",salaryid);
		this.getFormHM().put("statlist",projectset.condList());
		this.getFormHM().put("rangelist",projectset.rangeList());
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
	private void fieldItemList(String tableid){
		 ArrayList fieldsetlist = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlist.add(obj1);
		HashSet   hs   =   new   HashSet();   
		if(tableid.length()>0){
			try {
				TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
				ArrayList list = changebo.getAllFieldItem();
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					hs.add(fielditem.getFieldsetid());
				}
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String[] arr   =   (String[])hs.toArray(new String[0]);   
		Arrays.sort(arr);
		for(int j=0;j<arr.length;j++){
			String fieldsetid = arr[j];
			FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
			if(fieldset!=null){
				if(fieldset==null)
					 continue;
				 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				CommonData obj=new CommonData(fieldset.getFieldsetid()
						 ,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				fieldsetlist.add(obj);
			}	 
		}
		
		CommonData obj=new CommonData("vartemp"
				 ,ResourceFactory.getProperty("menu.gz.variable"));
		fieldsetlist.add(obj);
		this.getFormHM().put("fieldsetlist",fieldsetlist);
	}
	
	/**
	 * 获取人事异动模版临时变量
	 * @Title: tempfieldsetlist   
	 * @Description:    
	 * @param @param tableid 
	 * @return void    
	 * @throws
	 */
	private void tempfieldsetlist(String tableid) {
		ArrayList fieldsetlist = new ArrayList();
		CommonData obj1=new CommonData("","");
		fieldsetlist.add(obj1);
		StringBuffer buf = new StringBuffer();
		if(tableid.length()>0){
			try {
				ContentDAO dao=new ContentDAO(this.frameconn);
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,cstate from  midvariable where 1=1 ");
				buf.append(" and nflag=0 and templetId <> 0 and (templetid ='");
				buf.append(tableid);
				buf.append("' or cstate = '1')");
				RowSet rset=dao.search(buf.toString());
				while(rset.next()) {
					FieldItem item=new FieldItem();
					String cname = rset.getString("cname");
					String chz = rset.getString("chz");
					CommonData obj=new CommonData(cname,cname+"-"+chz);
					fieldsetlist.add(obj);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		this.getFormHM().put("tempfieldsetlist",fieldsetlist);
	}
}
