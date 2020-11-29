package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class OpenHRosterTrans extends IBusiness {

	public void execute() throws GeneralException {
//		 TODO Auto-generated method stub
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");		
		treeItem.setIcon("/images/add_all.gif");	
		treeItem.setTarget("nil_body");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		 String nFlag=(String)hm.get("nFlag");
		 nFlag=nFlag!=null&&nFlag.trim().length()>0?nFlag:"0";
		 hm.remove("nFlag");
		 this.getFormHM().put("modelFlag",nFlag);
		 String temp=this.userView.getResourceString(5);
		 if(temp.trim().length()==0) 
				temp="-1";
		 StringBuffer strsql=new StringBuffer();
//		 if(nFlag.equals("3")||nFlag.equals("21")||nFlag.equals("41")
//				 ||nFlag.equals("1")||nFlag.equals("4")||nFlag.equals("2")){
			 String a_inforkind=(String)hm.get("a_inforkind");
			 a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"1";
			 hm.remove("a_inforkind");
			 this.getFormHM().put("inforkind",a_inforkind);
			 
			 String rootdesc=ResourceFactory.getProperty("infor.menu.outhmuster");
			 treeItem.setRootdesc(rootdesc);
//			 treeItem.setLoadChieldAction("/general/muster/hmuster/searchHrostertree?flag=1&moduleflag="+nFlag+"&flaga="+a_inforkind);	
			 treeItem.setLoadChieldAction("/general/muster/hmuster/searchAllHrostertree?flag=1");
			 treeItem.setAction("javascript:void(0)");
			 
//			 String dbpre = (String)hm.get("dbpre");
//			 dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"";
//			 this.getFormHM().put("dbpre",dbpre);
//			 
//			 String result=(String)hm.get("result");
//			 result=result!=null&&result.trim().length()>0?result:"0";
//			 hm.remove("result");
//			 this.getFormHM().put("result",result);
//			 if(nFlag.equals("3")){
//				 this.getFormHM().put("historyRecord","0");
//			 }else
//				 this.getFormHM().put("historyRecord","1");
//			 strsql.append("SELECT tabid FROM muster_name ");
//			 strsql.append("nmodule='");
//			 strsql.append(nFlag);
//			 if(!this.userView.isSuper_admin()){
				
//				strsql.append("' and tabid in ("); 
//				strsql.append("where tabid in (");
//				strsql.append(temp); 
//				strsql.append(") ");
//			}
//			 else{
//				strsql.append("'"); 
//			}
//		 }
		 
//		 else if(nFlag.equals("15")){
//			 String a_code=(String)hm.get("a_code");
//			 a_code=a_code!=null&&a_code.trim().length()>0?a_code:"UN";
//			 hm.remove("a_code");
//			 
//			 String salarydate=(String)hm.get("salarydate");
//			 salarydate=salarydate!=null&&salarydate.trim().length()>0?salarydate:"";
//			 hm.remove("salarydate");
//			 
//			 String sortid=(String)hm.get("sortid");
//			 sortid=sortid!=null&&sortid.trim().length()>0?sortid:"";
//			 hm.remove("sortid");
//			 
//			 StringBuffer url = new StringBuffer();
//			 url.append("/general/muster/hmuster/searchHrostertree?flag=1&moduleflag=");
//			 url.append(nFlag);
//			 url.append("&a_code=");
//			 url.append(a_code);
//			 url.append("&salarydate=");
//			 url.append(salarydate);
//			 url.append("&sortid=");
//			 url.append(sortid);
//			 
//			 String rootdesc=ResourceFactory.getProperty("infor.menu.outhmuster");
//			 treeItem.setRootdesc(rootdesc);
//			 treeItem.setLoadChieldAction(url.toString());
//			 treeItem.setAction("javascript:void(0)");	   
//			 
//			 strsql.append("SELECT tabid FROM muster_name where ");
//			 strsql.append("nmodule='");
//			 strsql.append(nFlag+"'");
//		 }else{
//			 	String relatTableid=(String)hm.get("relatTableid");
//			 	hm.remove("relatTableid");
//				String condition=(String)hm.get("condition");
//				condition=SafeCode.decode(condition);
//				hm.remove("condition");
//				condition=condition.replaceAll("%20"," ");
//				String returnURL=(String)hm.get("returnURL");
//				returnURL=SafeCode.decode(returnURL);
//				hm.remove("returnURL");
//				
//				if(nFlag.equals("5")){
//					String spflag =(String)hm.get("spflag");
//					spflag=spflag!=null&&spflag.trim().length()>0?spflag:"1";
//					hm.remove("spflag");
//					
//					this.getFormHM().put("spflag",spflag);
//				}else{
//					this.getFormHM().put("spflag","");
//				}
//				
//				String rootdesc=ResourceFactory.getProperty("infor.menu.outhmuster");
//
//				treeItem.setRootdesc(rootdesc);
//				treeItem.setLoadChieldAction("/general/muster/hmuster/searchHrostertree?flag=1&moduleflag="+nFlag+"&relatTableid="+relatTableid);
//				treeItem.setAction("javascript:void(0)");	   
//				
//				this.getFormHM().put("relatTableid",relatTableid);
//				this.getFormHM().put("condition",condition);
//				this.getFormHM().put("returnURL",returnURL);
//				this.getFormHM().put("inforkind",nFlag);
//
//				 strsql.append("SELECT tabid FROM muster_name where ");
//				 strsql.append("nmodule='");
//				 strsql.append(nFlag);
//				 strsql.append("'");
//				 if(!nFlag.equals("1")&&!nFlag.equals("4")){
//					 strsql.append(" and nPrint="+relatTableid);
//				 }
//				 if(!nFlag.equals("5")){
//					 if(!this.userView.isAdmin()&&!this.userView.getGroupId().equals("1")){
//						 strsql.append(" and tabid in (");   
//						 strsql.append(temp); 
//						 strsql.append(") ");
//					 }
//				 }
//		 }
//		 strsql.append(" order by tabid");
		 this.getFormHM().put("treeCode",treeItem.toJS());
//		 ContentDAO dao=new ContentDAO(this.getFrameconn());
//		 try {
//			 this.frowset=dao.search(strsql.toString());
//			 boolean istabid=false;
//			 String tabid ="-1";
//			 while(this.frowset.next()){
//					tabid = this.frowset.getString("tabid");
//					if(nFlag.equals("5")){
//						istabid=true;
//						break;
//					}else{
//						if(!this.userView.isSuper_admin()){
//							if(temp.indexOf(tabid)!=-1){
//								istabid=true;
//								break;
//							}
//						}else{
//							istabid=true;
//							break;
//						}
//					}
//			 }
//			 if(!istabid){
//				 this.getFormHM().put("tabID","-1");
//			 }else{
//				 this.getFormHM().put("tabID",tabid);
//			 }
//		 } catch (SQLException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//		 }

	}
	/**
	 * 取得考勤高级花名册信息列表
	 * @param relatTabid
	 * @return
	 */
	public ArrayList getKQ_GZMusterList(String relatTabid,String nFlag)
	{
		ArrayList kq_musterList=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			if("81".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=81 and nPrint="+relatTabid);
			else if("5".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=5 and nPrint="+relatTabid);
			while (this.frowset.next()) {
//				if(!this.getUserView().isHaveResource(IResourceConstant.HIGHMUSTER,this.frowset.getString("tabid")))
//					continue;
				CommonData vo=new CommonData();
				vo.setDataName(this.frowset.getString("cname"));				
				vo.setDataValue(this.frowset.getString("tabid"));
				kq_musterList.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return kq_musterList;
	}

}
