package com.hjsj.hrms.transaction.org.autostatic.confset;

import com.hjsj.hrms.businessobject.org.autostatic.confset.DataCondBo;
import com.hjsj.hrms.businessobject.org.autostatic.confset.DataSynchroBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.TimeScope;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DataScanTrans extends IBusiness{
	String year = Calendar.getInstance().get(Calendar.YEAR)+"";
	String month = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";
	String changeflag = "1";
	public void execute() throws GeneralException{
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			HashMap hm = this.getFormHM();
			HashMap reqhm = (HashMap) hm.get("requestPamaHM");
			String scan = (String)reqhm.get("scan");
			scan = scan!=null&&scan.length()>0?scan:"";
			reqhm.remove("scan");

			String fieldstr = (String)reqhm.get("included");
			fieldstr = fieldstr!=null && fieldstr.length()>0?fieldstr:"";
			reqhm.remove("included");

			String selectconfset = (String)hm.get("subset");
			String fieldsetid = selectconfset.substring(2,selectconfset.length());		
			changeflag =selectconfset.substring(0,1);	
			this.setId(fieldsetid,dao);//xuj 2010-6-3  
			//String view_scan = (String)hm.get("view_scan");
			PosparameXML pos = new PosparameXML(this.frameconn);
			String view_scan = pos.getNodeAttributeValue("/params/view_scan","datascan"); 
			view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";	
			
			
			String getyear = (String)hm.get("yearnum");
			hm.remove("yearnum");
			getyear=getyear!=null&&getyear.length()>0?getyear:year+"";
			String getmonth = (String)hm.get("monthnum");
			hm.remove("monthnum");
			getmonth=getmonth!=null&&getmonth.length()>0?getmonth:month+"";

			if("2".equals(changeflag)){
				getmonth="0";
			}
			DataSynchroBo dsbo = new DataSynchroBo(this.userView, fieldsetid,
					dao, view_scan.substring(0, view_scan.length() - 1),
					getyear, getmonth, changeflag);
			hm.put("subset", fieldsetid);
			hm.put("monthnum", getmonth);
			hm.put("yearnum", getyear);
			hm.put("retlist", this.getItemlist(fieldsetid));
			hm.put("fielitemlist", getItemlist(fieldsetid));
			hm.put("level", "0");
			if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
				hm.put("levellist", dsbo.getPosLevel());
				hm.put("inforflag","0");
			}else{
				hm.put("levellist", dsbo.getLevel());
				hm.put("inforflag","1");
			}
			hm.put("cfcount", changeflag);
			
			if ("insert".equals(scan)) {
				DataCondBo databo = new DataCondBo(this.userView,this.frameconn,fieldsetid,
						view_scan,getyear,getmonth,changeflag);
				databo.runCond("1","UN");
			}
			if (fieldstr.trim().length() > 0) {
				dsbo.loadPrevData(fieldstr, getyear, getmonth, changeflag);
			}


			String areavalue = "";
			dsbo.setModule("1");
			String wherestr = dsbo.getdiwheresql(areavalue,"0");
			StringBuffer selectsql = new StringBuffer();
			selectsql.append(dsbo.getdiselelctsql(vilStr(fieldsetid)));
			selectsql.append(wherestr);

			String viewhide = fileidStr(fieldsetid);


			hm.put("wheresql",wherestr);
			hm.put("column",viewhide);
			
			DataCondBo dabo = new DataCondBo(this.userView,this.frameconn);
			
			ArrayList idlist = dabo.fieldList(fieldsetid);
			
			Field fielde=new Field("i9999","");
			fielde.setCodesetid("0");
			fielde.setDatatype(DataType.INT);
			fielde.setVisible(false);
			idlist.add(fielde);
			
			hm.put("selectsql",selectsql.toString());
			hm.put("tablename",fieldsetid);
			hm.put("fieldlist",idlist);
			hm.put("column",fieldsetid+"_table");

			hm.put("hideitemid",viewhide);
			hm.put("areavalue",areavalue);		
			hm.put("view_scan",view_scan);
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public String fileidStr(String filedsetid){
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
	
	public String vilStr(String filedsetid){
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
		if(str.length()>1){
			sqlstr = str.toString().substring(0,str.length()-1);
		}
		return sqlstr; 
	}
	/**
	 * 获取当前子集所以指标（未权限过滤）
	 * @param setname
	 * @return
	 */
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
		if(fs!=null){
			for(int i=0;i<fslist.size();i++){
				FieldItem fielditem = (FieldItem)fslist.get(i);
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
					retlist.add(fielditem);
				}
			}		
		}
		return retlist;
	}
	
	private void setId(String fieldsetid,ContentDAO dao){
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(fieldsetid);
		buf.append(" set Id='");
		buf.append(getId());
		buf.append("' where ");
		buf.append(getequalz0time(fieldsetid+"Z0"));

		try {
			dao.update(buf.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得年月标识时间
	 */
	private String getId(){
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9)
				return year+"."+month;
			else
				return year+".0"+month;
		}
	}
	
	private String getequalz0time(String itemid){
		TimeScope ts = new TimeScope();
		String time = "";
		if("2".equals(changeflag)){
			time = year;
		}else{
			int inputmonth = Integer.parseInt(month);
			if(inputmonth>0 && inputmonth<10)
				time = year+"-0"+month;
			else
				time = year+"-"+month;
		}
		return ts.getTimeCond(itemid,"=",time);
	}
}
