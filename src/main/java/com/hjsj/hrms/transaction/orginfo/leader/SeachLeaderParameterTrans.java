package com.hjsj.hrms.transaction.orginfo.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SeachLeaderParameterTrans extends IBusiness {

	/**
	 * <?xml version="1.0" encoding="GB2312"?> <param ># <org_m>B0x</ org _m
	 * >#班子基本情况信息集 <org_c>Bxx,Bxy,</ org _c>#班子其它信息集 <emp_e
	 * link_field="Axxxx">Axx</ emp _e>#班子成员信息集 </param>
	 * 
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String org_m = "";
		ArrayList org_mlist = new ArrayList();
		String org_c = "";
		StringBuffer org_c_view = new StringBuffer();
		String emp_e = "";
		ArrayList emp_elist = new ArrayList();
		String link_field = "";
		ArrayList link_fieldlist = new ArrayList();
		ArrayList b0110list = new ArrayList();
		String b0110="";
		String orderby="";
		//班子类型指标
		String leaderType = "";
		//领导班子届次指标
		String sessionitem = "";
		
		//领导班子主集关联72代码类的指标
		ArrayList leaderTypeList = new ArrayList();
		leaderTypeList.add(new CommonData("", ""));
		//领导班子主集字符或数字指标
		ArrayList sessionitemList = new ArrayList();
		sessionitemList.add(new CommonData("", ""));
		try {
			ArrayList blist = this.userView.getPrivFieldSetList(2);
			CommonData data = new CommonData("", "");
			org_mlist.add(data);
			for (int i = 0; i < blist.size(); i++) {
				FieldSet fieldset = (FieldSet) blist.get(i);
				String setid = fieldset.getFieldsetid();
				if (!"B01".equalsIgnoreCase(setid)
						&& !"B00".equalsIgnoreCase(setid)) {
					data = new CommonData(setid, fieldset.getCustomdesc());
					org_mlist.add(data);
				}
			}

			ConstantXml xml = new ConstantXml(this.frameconn,
					"ORG_LEADER_STRUCT");
			org_m = xml.getValue("org_m");
			org_m = org_m == null ? "" : org_m;
			org_c = xml.getValue("org_c");
			org_c = org_c == null ? "" : org_c;
			if (org_c.length() > 0) {
				String tmp[] = org_c.split(",");
				for (int i = 0; i < tmp.length; i++) {
					String setid = tmp[i];
					if (setid.length() == 3) {
						FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
						if (fieldset != null) {
							org_c_view.append("、" + fieldset.getCustomdesc());
						}
					}
				}
			}
			emp_e = xml.getValue("emp_e");
			emp_e = emp_e == null ? "" : emp_e;
			ArrayList alist = this.userView.getPrivFieldSetList(1);
			data = new CommonData("", "");
			emp_elist.add(data);
			for (int i = 0; i < alist.size(); i++) {
				FieldSet fieldset = (FieldSet) alist.get(i);
				String setid = fieldset.getFieldsetid();
				if (!"A01".equalsIgnoreCase(setid)
						&& !"A00".equalsIgnoreCase(setid)) {
					data = new CommonData(setid, fieldset.getCustomdesc());
					emp_elist.add(data);
				}
			}
			link_field = xml
					.getNodeAttributeValue("/param/emp_e", "i9999");
			link_field = link_field == null ? "" : link_field;
			link_fieldlist.add(new CommonData("", ""));
			b0110list.add(new CommonData("", ""));
			if (emp_e.length() == 3) {
				ArrayList flist = this.userView.getPrivFieldList(emp_e);
				if (flist != null) {
					for (int i = 0; i < flist.size(); i++) {
						FieldItem item = (FieldItem) flist.get(i);
						if ("N".equals(item.getItemtype())&&item.getDecimalwidth()<1) {
							data = new CommonData(item.getItemid(), item
									.getItemdesc());
							link_fieldlist.add(data);
						} else if ("UN".equals(item.getCodesetid())
								|| "UM".equals(item.getCodesetid())) {
							data = new CommonData(item.getItemid(), item
									.getItemdesc());
							b0110list.add(data);
						}

					}
				}
			}

			b0110 = xml
			.getNodeAttributeValue("/param/emp_e", "b0110");
			orderby = xml
			.getNodeAttributeValue("/param/emp_e", "orderby");
			
			
			leaderType = xml.getNodeAttributeValue("/param/org_m", "team_type");
			sessionitem = xml.getNodeAttributeValue("/param/org_m", "term"); 
			
			ArrayList fieldlist= DataDictionary.getFieldList(org_m, Constant.USED_FIELD_SET);
			for(int i=0;fieldlist!=null && i<fieldlist.size();i++){
				FieldItem fi = (FieldItem)fieldlist.get(i);
				if("72".equals(fi.getCodesetid())){
					CommonData cm = new CommonData(fi.getItemid(),fi.getItemdesc());
					leaderTypeList.add(cm);
				}else if("N".equals(fi.getItemtype()) || ("0".equals(fi.getCodesetid())&& "A".equals(fi.getItemtype()))){
					CommonData cm = new CommonData(fi.getItemid(),fi.getItemdesc());
					sessionitemList.add(cm);
				}
			}
			
			LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
			LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);	
			String display_field=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);			
			ArrayList list=leaderParam.getFields(display_field);
			String display_mess=getMess(list);
			this.getFormHM().put("display_mess",display_mess);
			
			//常用统计
			String gcond=leadarParamXML.getTextValue(LeadarParamXML.GCOND);	
			if(gcond==null||gcond.length()<=0)
				gcond="";
			list=leaderParam.getSelectSname(gcond);
			String gcond_mess=getMess(list);
			this.getFormHM().put("gcond_mess",gcond_mess);
			//wangcq 2014-12-09 begin 组织机构-领导班子参数配置中增加人员库设置
			String bz_pre=leadarParamXML.getTextValue(LeadarParamXML.BZDBPRE);			
			String bz_mess=getDBMess(bz_pre);		
			this.getFormHM().put("bz_mess",bz_mess);
			//wangcq 2014-12-09 end
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("org_m", org_m);
			this.getFormHM().put("org_mlist", org_mlist);
			this.getFormHM().put("org_c", org_c);
			this.getFormHM().put("org_c_view",
					org_c_view.length() > 1 ? org_c_view.substring(1) : "");
			this.getFormHM().put("emp_e", emp_e);
			this.getFormHM().put("emp_elist", emp_elist);
			this.getFormHM().put("link_field", link_field);
			this.getFormHM().put("link_fieldlist", link_fieldlist);
			this.getFormHM().put("b0110list", b0110list);
			this.getFormHM().put("b0110", b0110);
			this.getFormHM().put("order_by", orderby);
			this.getFormHM().put("leaderType", leaderType);
			this.getFormHM().put("sessionitem", sessionitem);
			this.getFormHM().put("leaderTypeList", leaderTypeList);
			this.getFormHM().put("sessionitemList", sessionitemList);
		}
	}
	
	private String getMess(ArrayList list){
		StringBuffer mess=new StringBuffer();
		if(list==null||list.size()<=0)
			return "";
		for(int i=0;i<list.size();i++)
		{
			 CommonData dataobj =(CommonData)list.get(i);
			 mess.append(dataobj.getDataName()+",");
			 
		}
		return mess.toString();
	}
	private String getDBMess(String dbpre){
		String[] pres = dbpre.trim().split(",");
		StringBuffer dbpres = new StringBuffer();
		ArrayList dblist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select dbname,pre from dbname";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				RecordVo vo = new RecordVo("dbname");
				vo.setString("pre",this.frowset.getString("pre"));
				vo.setString("dbname",this.frowset.getString("dbname"));
				dblist.add(vo);
			}
		} catch (SQLException e) {e.printStackTrace();}
		for(int i=0;i<pres.length;i++){
			for(int j=0;j<dblist.size();j++){
				RecordVo db = (RecordVo)dblist.get(j);
				if(pres[i].equalsIgnoreCase(db.getString("pre").toString()))
					dbpres.append(db.getString("dbname").toString());
			}
			if((i+1)%2==0)
				dbpres.append("<br>");
			else
				dbpres.append(",");
		}
		dbpres.setLength(dbpres.length()-1);
		return dbpres.toString();
	}
}
