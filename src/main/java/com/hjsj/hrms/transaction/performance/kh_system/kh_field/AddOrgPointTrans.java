package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class AddOrgPointTrans extends IBusiness{
	public void execute()throws GeneralException{
		ArrayList pointlist=new ArrayList();
		ArrayList templist=new ArrayList();
		ArrayList templist1=new ArrayList();
		ArrayList alllist=new ArrayList();
		LazyDynaBean abean=new LazyDynaBean();
		String pointset_menu="";
		String pointcode_menu="";
		String pointname_menu="";
		String	showmenus="";
		CommonData cdq=new CommonData("-1","请选择");
		templist.add(cdq);
		templist1.add(cdq);
		StringBuffer innerhtml=new StringBuffer("");
		CommonData cd;
		try {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String aflag=(String)hm.get("aflag");
			
			if("0".equals(aflag)){
				String sql="select * from fieldset where fieldsetid like 'B%' and useflag=1 and fieldsetid <>'B01' order by fieldsetid";
				ContentDAO dao=new ContentDAO(this.frameconn);
				this.frowset=dao.search(sql);
				while(this.frowset.next()){
					abean=new LazyDynaBean();
					cd=new CommonData();
					String fieldsetid=this.frowset.getString("fieldSetId")==null?"":this.frowset.getString("fieldSetId");
					String itemdesc=this.frowset.getString("customdesc")==null?"":this.frowset.getString("fieldSetDesc");
					if("2".equalsIgnoreCase(this.userView.analyseTablePriv(fieldsetid))){//控制 只有在用户管理中 授权了子集及相应自己指标的字段才能应用dml 2011-03-25
						cd.setDataValue(fieldsetid);
						cd.setDataName(itemdesc);
						pointlist.add(cd);
					}else{
						
					}
				}
				ArrayList khpidlist=(ArrayList)this.getFormHM().get("khpidlist");
				if(khpidlist==null||khpidlist.size()==0){
				
					this.getFormHM().put("khpidlist", templist);
				}else{
					this.getFormHM().put("khpidlist", khpidlist);
				}
				ArrayList khpnamelist=(ArrayList)this.getFormHM().get("khpnamelist");
				if(khpnamelist==null||khpnamelist.size()==0){
					this.getFormHM().put("khpnamelist", templist1);
				}else{
					this.getFormHM().put("khpnamelist", khpnamelist);
				}
				abean.set("itemid", "");
				abean.set("itemdesc", "第一条");
				alllist.add(abean);
				AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
			    appb.init();
			    appb.setReturnHt(null);
			    Hashtable ht=appb.analyseParameterXml();
			    pointset_menu=(String)ht.get("pointset_menu");
			    pointcode_menu=(String)ht.get("pointcode_menu");
			    pointname_menu=(String)ht.get("pointname_menu");
			    DbWizard dbwizard=new DbWizard(this.getFrameconn());
			    if(dbwizard.isExistTable(pointset_menu,false)){
			    	  this.getFormHM().put("orgpoint", pointset_menu);
			    }else{
			    	 this.getFormHM().put("orgpoint", "");
			    }
			    this.getFormHM().put("khpid", pointcode_menu);
			    this.getFormHM().put("khpname", pointname_menu);
			}else{
				
				AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
				appb.init();
				appb.setReturnHt(null);
				Hashtable ht=appb.analyseParameterXml();
				pointset_menu=(String)ht.get("pointset_menu");
			    pointcode_menu=(String)ht.get("pointcode_menu");
			    pointname_menu=(String)ht.get("pointname_menu");
			    showmenus=(String)ht.get("showmenus");
			    String sql="select * from fieldset where fieldsetid like 'B%' and useflag=1 and fieldsetid <>'B01' order by fieldsetid";
			    String[] temp=showmenus.split(",");
				ContentDAO dao=new ContentDAO(this.frameconn);
				this.frowset=dao.search(sql);
				while(this.frowset.next()){
					abean=new LazyDynaBean();
					cd=new CommonData();
					String fieldsetid=this.frowset.getString("fieldSetId");
					String itemdesc=this.frowset.getString("customdesc");
					if("2".equalsIgnoreCase(this.userView.analyseTablePriv(fieldsetid))){//控制 只有在用户管理中 授权了子集及相应自己指标的字段才能应用dml 2011-03-25
						cd.setDataValue(fieldsetid);
						cd.setDataName(itemdesc);
						pointlist.add(cd);
					}else{
						
					}
					
				}
				if("2".equalsIgnoreCase(this.userView.analyseTablePriv(pointset_menu))){
					sql="select * from fielditem where fieldsetid='"+pointset_menu+"' and USEFLAG='1'";
					this.frowset=dao.search(sql);
				
				
					innerhtml.append("<table width=\"100%\" border='0' cellspacing=\"0\" align=\"center\" valign=\"top\" cellpadding=\"0\" class=\"ListTable\" id='targetCollectTable'>");
					while(this.frowset.next()){
						boolean flag=false;
						cd=new CommonData();
						String itemdesc=this.frowset.getString("itemdesc")==null?"":this.frowset.getString("itemdesc");
						String fieldsetid=this.frowset.getString("itemid")==null?"":this.frowset.getString("itemid");
						if("A".equalsIgnoreCase(this.frowset.getString("itemtype").trim())&& "0".equals(this.frowset.getString("codesetid").trim())){
							cd.setDataName(itemdesc);
							cd.setDataValue(fieldsetid);
							if("2".equalsIgnoreCase(this.userView.analyseFieldPriv(fieldsetid))){//控制 只有在用户管理中 授权了子集及相应自己指标的字段才能应用dml 2011-03-25
								templist.add(cd);
								templist1.add(cd);
							}else{
								
							}
						}
						if(this.frowset.getString("itemid").equals(pointcode_menu)||this.frowset.getString("itemid").equals(pointname_menu)){
							
						}else{
							if("2".equalsIgnoreCase(this.userView.analyseFieldPriv(this.frowset.getString("itemid")))){//控制 只有在用户管理中 授权了子集及相应自己指标的字段才能应用dml 2011-03-25
								innerhtml.append("<tr>");
								innerhtml.append("<td align='center' class=\"RecordRow\" nowrap width=\"15%\">");
								for(int i=0;i<temp.length;i++){
									if(this.frowset.getString("itemid").equalsIgnoreCase(temp[i])){
										flag=true;
										break;
									}
								}
								if(flag){
									innerhtml.append("<input type=\"checkbox\" name=\"allitems\" value=\""+this.getFrowset().getString("itemid")+"\" checked/>");
								}else
									innerhtml.append("<input type=\"checkbox\" name=\"allitems\" value=\""+this.getFrowset().getString("itemid")+"\"/>");
								innerhtml.append("</td>");
								innerhtml.append("<td align='left' class=\"RecordRow\" nowrap >");
								innerhtml.append(this.frowset.getString("itemdesc"));
								innerhtml.append("</td></tr>");
							}else{
								
							}
						}
					}
					innerhtml.append("</table>");
				
				}
			}
			this.getFormHM().put("khpidlist", templist);
			this.getFormHM().put("khpnamelist", templist1);
			this.getFormHM().put("orgpoint", pointset_menu);
			this.getFormHM().put("khpid", pointcode_menu);
			this.getFormHM().put("khpname", pointname_menu);
			this.getFormHM().put("innerhtml", innerhtml.toString());
			this.getFormHM().put("aflag", aflag);
			this.getFormHM().put("alllist", alllist);
			this.getFormHM().put("pointList", pointlist);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
