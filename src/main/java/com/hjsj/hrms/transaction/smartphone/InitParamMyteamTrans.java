package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Element;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InitParamMyteamTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		String basicinfo_template="";
		String canQuery="";
		String selectField="";
		HashMap setsMap = new HashMap();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String b_init = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("b_init");
		if("hroster".equals(b_init)){
			
			 String cardid="-1";
			 try
			 {
				 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
				 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
					 cardid="-1";
			 }
			 catch(Exception ex)
			 {
				 ex.printStackTrace();
			 }finally{
				 this.getFormHM().put("cardid",cardid);
			 }
		}else{
			String cardid="-1";
			 try
			 {
				 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
				 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
					 cardid="-1";
			 }
			 catch(Exception ex)
			 {
				 ex.printStackTrace();
			 }finally{
				 this.getFormHM().put("cardid",cardid);
			 }
			try{
				String sql = "select constant from constant where UPPER(Constant)='PDA'";
				this.frowset = dao.search(sql);
				if(!this.frowset.next())
					throw new GeneralException(ResourceFactory
							.getProperty("cs.person.html.set"));
				ConstantXml xml = new ConstantXml(this.frameconn,"PDA","pda");
				basicinfo_template = xml.getTextValue("/pda/basicinfo_template");
				List allchildren=xml.getAllChildren("/pda/sets");
				/**
				 * <?xml version="1.0" encoding="GB2312"?>
					<pda>
						<!-- 子集和指标 -->
						<sets>
							<subset id="A01" >
								<menu id="A0101" />
								<menu id="A0107" />
							</subset>
							<subset id="A04" >
								<menu id="A0407" />
							</subset>
					 	</sets>
					 	<!-- 人员基本情况模板 -->
					 	<basicinfo_template>
					 	  [性别]，[民族]，[年龄]岁([出生日期]出生)，[籍贯]，[入党时间]入党，[参加工作时间]参加工作。
					 	</basicinfo_template>
					 	<!-- 是否支持双击页面全屏 仅离开输出HTML用-->
					 	<DblClickFullScreen DblClickFullScreen="True|False" />
					 	<!-- 是否支持查询,查询指标格式:B0110,A0101,C0702,A0111 -->
					 	<serch_set CanQuery="True|False" SelectField="B0110,A0101,C0702,A0111" />
					</pda>
	
				 */
				for(int i=0;i<allchildren.size();i++){
					Element element = (Element)allchildren.get(i);
					ArrayList itemids = new ArrayList();
					String setid = element.getAttributeValue("id");
					List list =element.getChildren();
					for(int n=0;n<list.size();n++){
						Element fieldelement = (Element)list.get(n);
						itemids.add(fieldelement.getAttributeValue("id"));
					}
					setsMap.put(setid, itemids);
				}
				canQuery = xml.getNodeAttributeValue("/pda/serch_set", "CanQuery");
				selectField = xml.getNodeAttributeValue("/pda/serch_set", "SelectField");
				if(selectField.startsWith(","))
					selectField=selectField.substring(1);
				if(selectField.endsWith(","))
					selectField=selectField.substring(0,selectField.length()-1);
				if(selectField.length()<5)
					canQuery="False";
			}catch(Exception e){
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				try {
					this.getFormHM().put("basicinfo_template", this.analyseBasicinfo_template(basicinfo_template, dao));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				this.getFormHM().put("setsMap", setsMap);
				this.getFormHM().put("canQuery", canQuery);
				this.getFormHM().put("selectField", selectField);
			}
		
		}
	}
	
	private HashMap analyseBasicinfo_template(String basicinfo_template,ContentDAO dao) throws SQLException{
		HashMap map = new HashMap();
		StringBuffer itemnames=new StringBuffer();
		int si=-1,ei=-1;
		while(true){
			if(si!=-1){
				++si;
				++ei;
			}
			si=basicinfo_template.indexOf('[', si);
			ei=basicinfo_template.indexOf(']', ei);
			if(si==-1||ei==-1)
				break;
			itemnames.append(",'"+basicinfo_template.substring(si+1,ei)+"'");
		}
		String sql = "select itemid,itemdesc,fieldsetid from fielditem where useflag='1' and itemdesc in('###'"+itemnames.toString()+")";
		this.frowset=dao.search(sql);
		HashMap mapsets=new HashMap();
		HashMap mapsetstr = new HashMap();
		while(this.frowset.next()){
			String fieldsetid = this.frowset.getString("fieldsetid");
			String itemid=this.frowset.getString("itemid");
			String itemdesc=this.frowset.getString("itemdesc");
			basicinfo_template=basicinfo_template.replace("["+itemdesc+"]", "["+itemid+"]");
			if(mapsets.containsKey(fieldsetid)){
				ArrayList itemids = (ArrayList)mapsets.get(fieldsetid);
				StringBuffer itemidsb = (StringBuffer)mapsetstr.get(fieldsetid);
				itemids.add(itemid);
				itemidsb.append(","+itemid);
			}else{
				ArrayList itemids = new ArrayList();
				StringBuffer itemidsb = new StringBuffer();
				itemids.add(itemid);
				itemidsb.append(","+itemid);
				mapsets.put(fieldsetid, itemids);
				mapsetstr.put(fieldsetid, itemidsb);
			}
		}
		map.put("basicinfo_template", basicinfo_template);
		map.put("mapsets", mapsets);
		map.put("mapsetstr", mapsetstr);
		return map;
	}

}
