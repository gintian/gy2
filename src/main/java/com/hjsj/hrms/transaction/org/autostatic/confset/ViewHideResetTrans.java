package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.org.autostatic.confset.DataCondBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.DataSynchroBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class ViewHideResetTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		String fieldsetid = (String)hm.get("subset");
		fieldsetid=fieldsetid!=null?fieldsetid:"";
		
		String getyear = (String)hm.get("yearnum");
		String getmonth = (String)hm.get("monthnum");
		getmonth=getmonth!=null&&getmonth.length()>0?getmonth:"0";
		
		String grade = (String)hm.get("level");
		String areavalue = (String)hm.get("areavalue");
		areavalue=areavalue!=null&&areavalue.length()>0?areavalue:"";
		areavalue=!"0".equalsIgnoreCase(areavalue)?areavalue:"";
		
		String fieldstr = (String)reqhm.get("included");
		fieldstr = fieldstr!=null && fieldstr.length()>0?fieldstr:"";
		reqhm.remove("included");
		
		String viewhide = fileidStr(fieldsetid);
		
		hm.put("hideitemid",viewhide);
		//String view_scan = (String)hm.get("view_scan");
		PosparameXML pos = new PosparameXML(this.frameconn);
		String view_scan = pos.getNodeAttributeValue("/params/view_scan","datascan"); 
		view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";
		
		String changeflag = "1";
		if("0".equals(getmonth)){
			changeflag="2";
		}
		
		DataSynchroBo dsbo = new DataSynchroBo(this.userView, fieldsetid, dao, view_scan.substring(0,view_scan.length()-1), getyear, getmonth, changeflag); 
		hm.put("subset",fieldsetid);
		hm.put("monthnum",getmonth);
		hm.put("yearnum",getyear);	
		hm.put("fielitemlist",getItemlist(fieldsetid));
		if("K".equalsIgnoreCase(fieldsetid.substring(0, 1)))
			hm.put("levellist", dsbo.getPosLevel());
		else
			hm.put("levellist", dsbo.getLevel());
		hm.put("level",grade);
		
		String count =(String)reqhm.get("count");
		count=count!=null?count:"";
		reqhm.remove("count");
		
		int num=0;
		if(fieldstr.trim().length()>0){
			num = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
		}
		if(count.length()>1)
		{
			DataCondBo databo = new DataCondBo(this.userView,this.frameconn,fieldsetid,
					view_scan,getyear,getmonth,changeflag);
			databo.runCond("1","UN");
		}
		int num2=0;
		if(fieldstr.trim().length()>0){
			num2 = dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
		}
		if(num2!=num){
			if(count.length()>1)
			{
				DataCondBo databo = new DataCondBo(this.userView,this.frameconn,fieldsetid,
						view_scan,getyear,getmonth,changeflag);
				databo.runCond("1","UN");
			}
		}
		
		dsbo.setModule("1");
		StringBuffer selectsql = new StringBuffer();
		selectsql.append(dsbo.getdiselelctsql(vilStr(fieldsetid,viewhide,grade,areavalue)));
		selectsql.append(dsbo.getdiwheresql(areavalue,grade));
		DataCondBo dabo = new DataCondBo(this.userView,this.frameconn);
		hm.put("selectsql",selectsql.toString());
		hm.put("tablename",fieldsetid);
		ArrayList idlist = dabo.fieldList(fieldsetid);
		Field fielde=new Field("i9999","");
		fielde.setCodesetid("0");
		fielde.setDatatype(DataType.INT);
		fielde.setVisible(false);
		idlist.add(fielde);
		hm.put("fieldlist",idlist);
		hm.put("column","table"+fieldsetid);

		hm.put("areavalue",areavalue);

	}
	private String fileidStr(String filedsetid){
		String str = "";
		List fieldset = (ArrayList)getItemlist(filedsetid);
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			str+=fielditem.getItemid()+",";
		}
		if(str.length()>1){
			str = str.substring(0,str.length()-1);
		}
		return str; 
	}
	private String vilStr(String filedsetid,String viewhide,String grade,String areavalue){
		StringBuffer str = new StringBuffer();
		List fieldset = (ArrayList)getItemlist(filedsetid);

		if("K".equalsIgnoreCase(filedsetid.substring(0,1))){
			str.append("(select parentid from organization where codeitemid=a.E01A1 group by parentid) as B0110,");
		}
		str.append("i9999,");
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			if("K".equalsIgnoreCase(filedsetid.substring(0,1))){
				if(!"B0110".equalsIgnoreCase(fielditem.getItemid()))
					str.append(fielditem.getItemid()+",");
			}else{
				str.append(fielditem.getItemid()+",");
			}
		}
		String sqlstr="";
		if(str.length()>0){
			sqlstr = str.toString().substring(0,str.length()-1);
		}
		return sqlstr; 
	}

	public List getItemlist(String setname){
		List retlist=new ArrayList();
		if(!setname.startsWith("K")){
			FieldItem fi=DataDictionary.getFieldItem("B0110");
			retlist.add(0,fi);
		}else{
			FieldItem fi=DataDictionary.getFieldItem("B0110");
			retlist.add(0,fi);
			FieldItem efi=DataDictionary.getFieldItem("E01A1");			
			retlist.add(1,efi);
		}
		FieldItem fi=new FieldItem();
		fi.setItemid("id");
		fi.setFieldsetid("0");
		fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
		retlist.add(fi);
		
		FieldSet fs=DataDictionary.getFieldSetVo(setname);
		List fslist = fs.getFieldItemList(Constant.USED_FIELD_SET);
		for(int i=0;i<fslist.size();i++){
			FieldItem fielditem = (FieldItem)fslist.get(i);
			if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
				retlist.add(fielditem);
			}
		}			
		return retlist;
	}
}
