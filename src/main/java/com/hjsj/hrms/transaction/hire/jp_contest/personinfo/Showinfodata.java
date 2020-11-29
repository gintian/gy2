package com.hjsj.hrms.transaction.hire.jp_contest.personinfo;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParam;
import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:Showinfodata.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 22, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class Showinfodata extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap hashm=(HashMap)this.getFormHM().get("requestPamaHM");
		String flag=(String)hashm.get("flag");
		String appstate = (String)hm.get("appstate");//申请状态
		if(appstate==null|| "".equalsIgnoreCase(appstate))
			appstate="0";
		String state = (String)hm.get("state");//状态
		if(state==null|| "".equalsIgnoreCase(state))
			state="0";
		hm.put("statestr",this.getStateStr(state));
		hm.put("appstatestr",this.getAppStateSelStr(appstate));
		String jp_station = (String)hm.get("jp_station");//拟竞聘岗位
		if(jp_station==null|| "".equalsIgnoreCase(jp_station)|| "00".equalsIgnoreCase(jp_station)|| "11".equalsIgnoreCase(jp_station))
			jp_station = "0";
		ArrayList stationlist = this.getSelperlist(jp_station,state,appstate);
		stationlist = this.getStationList();
		hm.put("stationlist",stationlist);
		
		ArrayList sel_perlist = this.getSelperlist(jp_station,state,appstate);
		ArrayList nbaselist  = this.getSelnbaselist(jp_station,state,appstate);
		
		EngageParamXML epXML = new EngageParamXML(this.getFrameconn());
		String app_view = epXML.getTextValue(EngageParamXML.ATTENT_VIEW);
		EngageParam ep = new EngageParam(this.getFrameconn());
		ArrayList display_list =ep.getFields(app_view);
		
		
		
		
		boolean b0110_is=false;
		boolean e0122_is=false;
		boolean e01a1_is=false;
		ArrayList fieldlist=new ArrayList();
		StringBuffer select_str = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		ArrayList Infolist = new ArrayList();
		ArrayList columnlist = new ArrayList();
		ArrayList column = new ArrayList();
		ArrayList typelist = new ArrayList();
		column.add("选择");
		
		if(display_list!=null&&display_list.size()>0){
			int n=0;
			for(int i=0;i<display_list.size();i++)
			{
				CommonData data=(CommonData)display_list.get(i);
				String itemid=data.getDataValue();			
				if("b0110".equalsIgnoreCase(itemid))
					b0110_is=true;
				if("e0122".equalsIgnoreCase(itemid))
					e0122_is=true;
				if("e01a1".equalsIgnoreCase(itemid))
					e01a1_is=true;
				FieldItem fielditem=DataDictionary.getFieldItem(itemid);
				fielditem.setVisible(true);
				fieldlist.add(fielditem);
				columns.append(itemid+",");
				columnlist.add(itemid);
				typelist.add(fielditem.getItemtype().toUpperCase());
				column.add(data.getDataName());
			}
			columns.append(" zp_apply_jobs.z0700 ");
			columns.append(" ,zp_apply_jobs.state ");
			FieldItem fielditem=DataDictionary.getFieldItem("z0700");
			fielditem.setVisible(true);
			fieldlist.add(fielditem);
			columnlist.add("z0700");
			typelist.add("A");
			columnlist.add("state");
			typelist.add("A");
			column.add("竞聘岗位");
			column.add("申请状态");
			column.add("申请表");
			column.add("材料");
			FieldItem fielditem00=null;
			if(!b0110_is)
			{
				fielditem00=DataDictionary.getFieldItem("b0110");
				fielditem00.setVisible(false);
				fieldlist.add(fielditem00);
				columns.append(",b0110");
			}
			if(!e0122_is)
			{
				fielditem00=DataDictionary.getFieldItem("e0122");
				fielditem00.setVisible(false);
				fieldlist.add(fielditem00);
				columns.append(",e0122");
			}
			if(!e01a1_is)
			{
				fielditem00=DataDictionary.getFieldItem("e01a1");
				fielditem00.setVisible(false);
				fieldlist.add(fielditem00);
				columns.append(",e01a1");
			}
			String nbase="";
			if(nbaselist.size()!=0&&nbaselist!=null)
			for(int r=0;r<nbaselist.size();r++)
			{
				StringBuffer joinstr = new StringBuffer();
				nbase=nbaselist.get(r).toString();
				select_str.append("select '"+nbase+"' as nbase,"+nbase+"A01.a0100,");
				select_str.append(columns.toString());
				select_str.append(" from "+nbase+"A01 ");
				for(int i=0;i<fieldlist.size();i++){
					FieldItem fi = (FieldItem)fieldlist.get(i);
					if(!"A01".equalsIgnoreCase(fi.getFieldsetid())&&!"Z07".equalsIgnoreCase(fi.getFieldsetid())){
						joinstr.append("left join (select A.a0100,"+fi.getItemid()+" from "+nbase+fi.getFieldsetid()+" A,");
						joinstr.append("(select a0100,max(i9999) i9999 from "+nbase+fi.getFieldsetid()+"  group by a0100) B where A.a0100 = B.a0100 and A.i9999 = B.i9999) ");
						joinstr.append(nbase+fi.getFieldsetid()+i+" on "+nbase+"A01.a0100 = "+nbase+fi.getFieldsetid()+i+".a0100 ");
					}
				}
				joinstr.append(" left join zp_apply_jobs on "+nbase+"A01.a0100 = zp_apply_jobs.a0100 and nbase='"+nbase+"'");
				if(!"0".equalsIgnoreCase(state))
					joinstr.append(" left join z07 z on zp_apply_jobs.z0700 = z.z0700 ");
				select_str.append(joinstr.toString());
				select_str.append(" where "+nbase+"A01.a0100 in (");
				for(int i=0;i<sel_perlist.size();i++){
					LazyDynaBean bean = (LazyDynaBean)sel_perlist.get(i);
					if(bean.get("nbase").toString().equalsIgnoreCase(nbase))
						select_str.append("'"+bean.get("a0100")+"',");
				}
				select_str.setLength(select_str.length()-1);
				select_str.append(")");
				select_str.append(" and zp_apply_jobs.state<>'01' ");
				if(!"0".equalsIgnoreCase(jp_station))
					select_str.append(" and zp_apply_jobs.z0700 in (select z0700 from z07 where z0701 = '"+jp_station+"')");
				if(!"0".equalsIgnoreCase(appstate))
					select_str.append(" and zp_apply_jobs.state = '"+appstate+"'");
				if(!"0".equalsIgnoreCase(state))
					select_str.append(" and z0713 = '"+state+"'");
				select_str.append(" union ");
				
			}
			if(select_str.length()>0){
				select_str.setLength(select_str.length()-7);
				Infolist = this.getInfoList(fieldlist,select_str.toString());
			}
		}
		String userpriv="";
		if("infoself".equalsIgnoreCase(flag))
    	{
    		userpriv="selfinfo";
    	}	
		hm.put("columnlist",columnlist);
		hm.put("columns",column);
		hm.put("rolelist",Infolist);
		hm.put("userpriv", userpriv);
		hm.put("typelist",typelist);
		if(display_list==null||display_list.size()<=0)
			hm.put("ye","2");
		else if(Infolist.size()==0||Infolist==null)
			hm.put("ye","0");
		else
			hm.put("ye","1");
		String template = epXML.getTextValue(EngageParamXML.TEMPLATE);
		if(template==null||template.length()<=0)
			template="";
		getTenplateList(template);
	}

	public ArrayList getInfoList(ArrayList fieldlist,String sql){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				String value = "";
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("a0100",rs.getString("a0100"));
				bean.set("nbase",rs.getString("nbase"));
				for(int i=0;i<fieldlist.size();i++){
					FieldItem fielditem = (FieldItem)fieldlist.get(i);
					String itemType = fielditem.getItemtype().toUpperCase();
					String codesetid =  fielditem.getCodesetid();
					String itemid = fielditem.getItemid().toLowerCase();
					String itemvalue = "";
					if("D".equalsIgnoreCase(itemType))
						itemvalue = rs.getDate(itemid)==null?null:String.valueOf(rs.getDate(itemid));
					else
						itemvalue = rs.getString(itemid);
					if(itemvalue==null|| "".equals(itemvalue))
						value = "";
					else{
						if("z0700".equalsIgnoreCase(itemid)){
							value = this.getJpname(itemvalue);
							bean.set("zp_z0700",itemvalue);
						}
						else if("A".equalsIgnoreCase(itemType)&&!"0".equalsIgnoreCase(codesetid))
							value = AdminCode.getCodeName(codesetid,itemvalue);
						else if("D".equalsIgnoreCase(itemType)){
							int length = fielditem.getItemlength();
							value  = itemvalue.substring(0,length);
						}
						else if("N".equalsIgnoreCase(itemType)){
							int length = fielditem.getDecimalwidth();
							value = this.formatValue(itemvalue,length);
						}
						else if("M".equalsIgnoreCase(itemType))
								value = itemvalue;
						else
							value = itemvalue;
					}
					bean.set(itemid,value);
				}
				String sqlstate = "select codeitemdesc from codeitem where Upper(codesetid) = '23' and Upper(codeitemid) = '"+rs.getString("state")+"'";
				RowSet rs2 = dao.search(sqlstate);
				if(rs2.next())
				bean.set("state",rs2.getString("codeitemdesc"));
				bean.set("stateid",rs.getString("state"));
				list.add(bean);
			}
		} catch (SQLException e) {e.printStackTrace();}
		return list;
	}
	public String getJpname(String id){
		String value = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select z0701,z0706 from z07 where z0700 = '"+id+"'";
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				//value = rs.getString("z0706");
				value = AdminCode.getCodeName("@K",rs.getString("z0701"));
			}
		} catch (SQLException e) {e.printStackTrace();}
		return value;
	}
	/**
	 * 获取规范的表达式的值,自动四舍五入
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return  规范后的值
	 */
	public String formatValue(String exprValue , int flag){
		
		StringBuffer sb = new StringBuffer();	
		if(flag == 0){
			sb.append("####");
		}else{
			sb.append("####.");
			for(int i = 0 ; i < flag ; i++){
				sb.append("0");
			}
		}	
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		double dv=Double.parseDouble(dstr);
		if(dv==0)
		{
			dstr="0"+dstr;
		}
		return dstr;
	}
	/**
	 * 
	 * @param jp_station
	 * @param state
	 * @param appstate
	 * @return
	 */
	public ArrayList getSelperlist(String jp_station,String state,String appstate){
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select nbase,A0100 from zp_apply_jobs where ");
		if("0".equalsIgnoreCase(jp_station)&&!"0".equalsIgnoreCase(state))
			sql.append(" Upper(Z0700) in (select z0700 from z07 where Upper(Z0713) ='"+state.toUpperCase()+"')");
		else if(!"0".equalsIgnoreCase(jp_station)&& "0".equalsIgnoreCase(state))
			sql.append(" Upper(Z0700) in (select z0700 from z07 where Upper(Z0701) = '"+jp_station.toUpperCase()+"')");
		else if(!"0".equalsIgnoreCase(jp_station)&&!"0".equalsIgnoreCase(state)){
			sql.append(" Upper(Z0700) in (select z0700 from z07 where Upper(Z0701) = '"+jp_station.toUpperCase()+"' and Upper(Z0713) = '"+state.toUpperCase()+"')");
		}
		else
			sql.append(" 1=1 ");
		if(!"0".equalsIgnoreCase(appstate))
			sql.append(" and Upper(state) ='"+appstate.toUpperCase()+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("nbase",this.frowset.getString("nbase"));
				bean.set("a0100",this.frowset.getString("A0100"));
				list.add(bean);
			}
		} catch (SQLException e) {e.printStackTrace();}
		return list;
	}
	
	/**
	 * 
	 * @param jp_station
	 * @param state
	 * @param appstate
	 * @return
	 */
	public ArrayList getSelnbaselist(String jp_station,String state,String appstate){
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct(nbase) from zp_apply_jobs where ");
		if("0".equalsIgnoreCase(jp_station)&&!"0".equalsIgnoreCase(state))
			sql.append("z0700 in (select z0700 from z07 where z0713 ='"+state+"')");
		else if(!"0".equalsIgnoreCase(jp_station)&& "0".equalsIgnoreCase(state))
			sql.append("z0700 in (select z0700 from z07 where z0701 = '"+jp_station+"')");
		else if(!"0".equalsIgnoreCase(jp_station)&&!"0".equalsIgnoreCase(state)){
			sql.append(" z0700 in (select z0700 from z07 where z0701 = '"+jp_station+"' and z0713 = '"+state+"')");
		}
		else
			sql.append(" 1=1 ");
		if(!"0".equalsIgnoreCase(appstate))
			sql.append(" and state ='"+appstate+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				list.add(this.frowset.getString("nbase"));
			}
		} catch (SQLException e) {e.printStackTrace();}
		return list;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList getStationList(){
		ArrayList stationlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn()); 
		StringBuffer sql = new StringBuffer();
		ArrayList codelist = new ArrayList();
		sql.append("select distinct(z0701) from z07");
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				String s = this.frowset.getString("z0701");
				if(s!=null)
				codelist.add(s);
			}
		} catch (SQLException e) {e.printStackTrace();}
		sql.delete(0,sql.length());
		if(codelist.size()<=0){
			CommonData data = new CommonData("00","无岗位");
			stationlist.add(data);
			return stationlist;
		}
		sql.append("select codeitemid,codeitemdesc from organization where Upper(codesetid)='@K' and Upper(codeitemid) in (");
		for(int i=0;i<codelist.size();i++){
			sql.append("'"+codelist.get(i).toString().toUpperCase()+"',");
		}
		sql.setLength(sql.length()-1);
		sql.append(")");
		CommonData data1 = new CommonData("11","全部");
		stationlist.add(data1);
		try {
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				CommonData data = new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
				stationlist.add(data);
			}
		} catch (SQLException e) {e.printStackTrace();}
		return stationlist;
		
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	public String getStateStr(String state){
		StringBuffer statestr = new StringBuffer();
		statestr.append("<select name=\"state\" onchange=\"selchange();\">");
		if("0".equals(state)){
			statestr.append("<option value=\"0\" \"  selected= \"selected\">");
		}else{
			statestr.append("<option value=\"0\">");
		}
		statestr.append(ResourceFactory.getProperty("label.all"));
		statestr.append("</option>");
		if("05".equals(state)){
			statestr.append("<option value=\"05\" \"  selected= \"selected\">");
		}else{
			statestr.append("<option value=\"05\">");
		}
		statestr.append(ResourceFactory.getProperty("label.hiremanage.status5"));
		statestr.append("</option>");
		if("06".equals(state)){
			statestr.append("<option value=\"06\" \"  selected= \"selected\">");
		}else{
			statestr.append("<option value=\"06\">");
		}
		statestr.append(ResourceFactory.getProperty("label.hiremanage.status6"));
		statestr.append("</option>");
		statestr.append("</select>");
		return statestr.toString();
	}
	
	/**
	 * 
	 * @param appstate
	 * @return
	 */
	public String getAppStateSelStr(String appstate){
		StringBuffer sbselstr=new StringBuffer();
		sbselstr.append("<select name=\"appstate\" onchange=\"selchange();\">");
		if("0".equals(appstate)){
			sbselstr.append("<option value=\"0\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"0\">");
		}
		
		sbselstr.append("全部");
		sbselstr.append("</option>");
		if("02".equals(appstate)){
			sbselstr.append("<option value=\"02\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"02\">");
		}
		sbselstr.append(ResourceFactory.getProperty("button.appeal"));
		sbselstr.append("</option>");
		if("03".equals(appstate)){
			sbselstr.append("<option value=\"03\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"03\">");
		}
		sbselstr.append(ResourceFactory.getProperty("label.hiremanage.status3"));
		sbselstr.append("</option>");
		if("07".equals(appstate)){
			sbselstr.append("<option value=\"07\" \"  selected= \"selected\">");
		}else{
			sbselstr.append("<option value=\"07\">");
		}
		sbselstr.append(ResourceFactory.getProperty("button.reject"));
		sbselstr.append("</option>");
		
		
		//sbselstr.append("</option>");
		sbselstr.append("</select>");
		return sbselstr.toString();
	}
	
	private void getTenplateList(String selectedId)
    {
    	ArrayList list=new ArrayList();
    	StringBuffer strsql=new StringBuffer();
    	if(selectedId==null||selectedId.length()<=0)
    	{
    		
            this.formHM.put("templatelist", null);
    		return ;
    	}	
    	strsql.append("select tabid,name from template_table where ");
    	strsql.append("tabid in (");
    	String[] tIds=selectedId.split(",");
    	for(int i=0;i<tIds.length;i++)
    	{
    		strsql.append("'"+tIds[i]+"',");
    	}
    	strsql.setLength(strsql.length()-1);
    	strsql.append(")");
    	String all="";
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
			rset=dao.search(strsql.toString());			
			while(rset.next())
			{			
				if (!userView.isHaveResource(IResourceConstant.RSBD, rset.getString("tabid")))
					continue;	
				CommonData vo=new CommonData();   		
				vo.setDataValue(rset.getString("tabid"));
	            vo.setDataName(rset.getString("tabid")+":"+rset.getString("name"));
	            list.add(vo);
	            all+=rset.getString("tabid")+",";
			}
		}
		catch(Exception ex)
		{
			
		}
		if(all.length()>0)
			all = all.substring(0,all.length()-1);
		CommonData vo=new CommonData(all,"全部");
		list.add(0,vo);
		this.formHM.put("templatelist", list);
    }
}
