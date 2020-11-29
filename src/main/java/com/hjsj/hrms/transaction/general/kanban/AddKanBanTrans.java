package com.hjsj.hrms.transaction.general.kanban;

import com.hjsj.hrms.businessobject.general.kanban.KanBanBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddKanBanTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String checkflag = (String)hm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
		hm.remove("checkflag");
		ArrayList fieldlist = new ArrayList();
		ArrayList list = DataDictionary.getFieldList("p05",Constant.USED_FIELD_SET);
		String billperson="";
		String kbtitle="";
		try{
			KanBanBo kb = new KanBanBo(this.userView,this.frameconn);
			if("update".equalsIgnoreCase(checkflag)){
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");
				fieldlist = updateValue(list,p0500,checkflag);
				this.getFormHM().put("p0500", p0500);
				billperson = billPerson(checkflag,p0500);
				kbtitle="任务修改";
			}else if("audit".equalsIgnoreCase(checkflag)){
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");
				fieldlist = updateValue(list,p0500,checkflag);
				billperson = billPerson(checkflag,p0500);
				this.getFormHM().put("p0500", p0500);
				kbtitle="任务审核";
			}else if("fill".equalsIgnoreCase(checkflag)){
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");
				fieldlist = updateValue(list,p0500,checkflag);
				this.getFormHM().put("p0500", p0500);
				billperson = billPerson(checkflag,p0500);
				kbtitle="任务填写";
			}else if("reply".equalsIgnoreCase(checkflag)){
				String p0500 = (String)hm.get("p0500");
				p0500=p0500!=null&&p0500.trim().length()>0?p0500:"";
				hm.remove("p0500");
				fieldlist = updateValue(list,p0500,checkflag);
				this.getFormHM().put("p0500", p0500);
				billperson = billPerson(checkflag,p0500);
				kbtitle="任务回复";
			}else{
				fieldlist = kb.addValue(list);
				this.getFormHM().put("person", "");
				this.getFormHM().put("checkperson","");
				billperson = billPerson(checkflag,"");
				kbtitle="任务新增";
			}
			String filltable = "";
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
				if(fielditem!=null&&fielditem.getPriv_status()==1&&fielditem.isFillable()){
					filltable+=fielditem.getItemid()+"::"+fielditem.getItemdesc()+"`";
				}
			}
			
			this.getFormHM().put("filltable",filltable);
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("checkflag", checkflag);
			this.getFormHM().put("billperson",billperson);
			this.getFormHM().put("kbtitle",kbtitle);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	 private static double round(double v,int scale){ 
	        if(scale<0){ 
	            throw new IllegalArgumentException( 
	                "The scale must be a positive integer or zero"); 
	        } 
	        BigDecimal b = new BigDecimal(Double.toString(v)); 
	        BigDecimal one = new BigDecimal("1"); 
	        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue(); 
	 } 
	 private String billPerson(String checkflag,String p0500){ 
		 StringBuffer billperson = new StringBuffer();
		 String dbname = this.userView.getDbname();
		 String b0100 = this.userView.getUserOrgId();
		 String e0122 = this.userView.getUserDeptId();
		 String e01a1 = this.userView.getUserPosId();
		 String a0101 = this.userView.getUserFullName();
		 if(!"update".equalsIgnoreCase(checkflag)&&!"add".equalsIgnoreCase(checkflag)){
			 ContentDAO dao = new ContentDAO(this.frameconn);
			 RecordVo vo = new RecordVo("p05");
			 vo.setString("p0500", p0500);
			 try {
				 vo = dao.findByPrimaryKey(vo);
				 dbname = vo.getString("nbase_0");
				 b0100 = vo.getString("b0110_0");
				 e0122 = vo.getString("e0122_0");
				 e01a1 = vo.getString("e01a1_0");
				 a0101 = vo.getString("a0101_0");
			 } catch (GeneralException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 } catch (SQLException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
		 }
		 
		 dbname=dbname!=null?dbname:"";
		 b0100=b0100!=null?b0100:"";
		 e0122=e0122!=null?e0122:"";
		 e01a1=e01a1!=null?e01a1:"";
		 a0101=a0101!=null?a0101:"";
		 if(dbname.trim().length()>0){
			 billperson.append(AdminCode.getCodeName("@@", dbname));
			 billperson.append(" ");
		 }
		 if(b0100.trim().length()>0){
			 billperson.append(AdminCode.getCodeName("UN", b0100));
			 billperson.append(" ");
		 }
		 if(e0122.trim().length()>0){
			 billperson.append(AdminCode.getCodeName("UM", e0122));
			 billperson.append(" ");
		 }
		 if(e01a1.trim().length()>0){
			 billperson.append(AdminCode.getCodeName("@K",e01a1));
			 billperson.append(" ");
		 }
		 if(a0101.trim().length()>0){
			 billperson.append(a0101);
		 }
		 return billperson.toString();
	 } 
	 
	 private ArrayList updateValue(ArrayList list,String p0500,String checkflag){
		 ArrayList fieldlist = new ArrayList();
		 try {
			 ContentDAO dao = new ContentDAO(this.frameconn);
			 RecordVo vo = new RecordVo("p05");
			 vo.setString("p0500", p0500);
			 vo = dao.findByPrimaryKey(vo);
			 FieldItem items = new FieldItem("A01","");

			 if("update".equalsIgnoreCase(checkflag)){
				 items = new FieldItem("A01","");
				 items.setItemid("a0101_0");
				 items.setItemdesc("接单人");
				 items.setItemtype("A");
				 items.setCodesetid("0");
				 items.setValue(vo.getString("a0101_0"));

				 items.setPriv_status(1);
				 fieldlist.add(items);

				 items = new FieldItem("A01","");
				 items.setItemid("a0101_1");
				 items.setItemdesc("任务审核人");
				 items.setItemtype("A");
				 items.setCodesetid("0");
				 items.setValue(vo.getString("a0101_1"));
				 items.setPriv_status(1);
				 fieldlist.add(items);
			 }
			String lead = SystemConfig.getPropertyValue("workform");
			lead=lead!=null?lead:"";
			KanBanBo kb = new KanBanBo(this.userView,this.frameconn);
			 for(int i=0;i<list.size();i++){
				 FieldItem fielditem = (FieldItem)list.get(i);
				 String value = "";
				 if(!fielditem.isVisible()){
					 continue;
				 }
				 if(fielditem==null)
					 continue;
				 if("a0101".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("b0110".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("e0122".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("e01a1".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("a0100".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("NBASE".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("p0500".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 } if("p0502".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("p0507".equalsIgnoreCase(fielditem.getItemid())){
					 if("update".equalsIgnoreCase(checkflag))
						 continue;
				 }else if("p0513".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("p0509".equalsIgnoreCase(fielditem.getItemid())){
					 if("update".equalsIgnoreCase(checkflag)){
						 continue;
					 }
				 }else if("p0504".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }else if("p0521".equalsIgnoreCase(fielditem.getItemid())){
					 continue;
				 }
				 
				 if("D".equalsIgnoreCase(fielditem.getItemtype())){
					 Date dvalue = vo.getDate(fielditem.getItemid());
					 WeekUtils weekutils = new WeekUtils();
					 if(dvalue!=null)
						 value = weekutils.dateTostr(dvalue);
					 if("p0501".equalsIgnoreCase(fielditem.getItemid())|| "p0502".equalsIgnoreCase(fielditem.getItemid())){
						 fielditem.setViewvalue(weekutils.dateTohms(dvalue));
					 }
				 }else{
					 value = vo.getString(fielditem.getItemid());
				 }
				 if("N".equalsIgnoreCase(fielditem.getItemtype())&&fielditem.getDecimalwidth()==2){
					 fielditem.setDecimalwidth(2);
					 value=value!=null&&value.trim().length()>0?value:"0";
					 value = Double.toString(round(Double.parseDouble(value),2));
				 }
				 if(fielditem.isCode()){
					 fielditem.setViewvalue(AdminCode.getCodeName(fielditem.getCodesetid(), value));
				 }
				 if("M".equalsIgnoreCase(fielditem.getItemtype())){
					 if("fill".equalsIgnoreCase(checkflag)){
						 if("p0509".equalsIgnoreCase(fielditem.getItemid()))
							 value = value.replaceAll("\n", "<br>&nbsp;&nbsp;");
						 if("p0503".equalsIgnoreCase(fielditem.getItemid()))
							 value = value.replaceAll("\n", "<br>");
					 }else{
						 if("p0509".equalsIgnoreCase(fielditem.getItemid()))
							 value = value.replaceAll("\n", "<br>&nbsp;&nbsp;");
						 else{
							 if(!"update".equalsIgnoreCase(checkflag)){
								 value = value.replaceAll("\n", "<br>");
							 }
						 }
					 }
				 }
				 fielditem.setValue(value);
				 if("update".equalsIgnoreCase(checkflag)){
					 fielditem.setPriv_status(1);
				 }else{
					 if("p0509".equalsIgnoreCase(fielditem.getItemid())){
						 if("fill".equalsIgnoreCase(checkflag)){
							 fielditem.setPriv_status(1);
						 }else if("reply".equalsIgnoreCase(checkflag)){
							 fielditem.setPriv_status(1);
						 }
					 }else{
						 if("fill".equalsIgnoreCase(checkflag)){
							 if("P0501".equalsIgnoreCase(fielditem.getItemid()))
								 fielditem.setPriv_status(0);
							 else if("P0503".equalsIgnoreCase(fielditem.getItemid()))
								 fielditem.setPriv_status(0);
							 else if("P0505".equalsIgnoreCase(fielditem.getItemid()))
								 fielditem.setPriv_status(0);
							 else
								 fielditem.setPriv_status(1);
						 }else{
							 fielditem.setPriv_status(0); 
						 }
					 }
					 if("p0507".equalsIgnoreCase(fielditem.getItemid())){
						 if("fill".equalsIgnoreCase(checkflag)){
							 fielditem.setPriv_status(1);
						 }
					 }
				 }
				
				if(lead.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1){
					if(this.userView.isDeptLeader()||this.userView.isOrgLeader()||kb.isHaveRole("00000005")){
						fielditem.setPriv_status(1);
					}else{
						continue;
					}
				}
				fieldlist.add(fielditem);
			 }
			 String person = vo.getString("nbase_0")+"::"+vo.getString("a0100_0")+"::"+vo.getString("a0101_0");
			 String checkperson = vo.getString("nbase_1")+"::"+vo.getString("a0100_1")+"::"+vo.getString("a0101_1");
			 this.getFormHM().put("person", person);
			 this.getFormHM().put("checkperson", checkperson);
		 } catch (SQLException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return fieldlist;
	 }
}
