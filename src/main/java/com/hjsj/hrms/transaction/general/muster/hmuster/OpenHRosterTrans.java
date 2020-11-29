package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
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
		hm.put("changeDbpre", "1");
		 /**各模块调用花名册的太多，此参数判断是显示返回按钮还是显示关闭按钮=1的时候，是弹出窗口，不显示返回按钮，显示关闭按钮=0显示返回按钮*/
	    String closeWindow=(String)hm.get("closeWindow");
	    if(closeWindow==null)
	    {
	    	closeWindow="0";
	    }
	    else
	    {
	    	hm.remove("closeWindow");
	    }
	    String returnflag="";
	    if(hm.get("returnflag")!=null)
	    {
	    	returnflag=(String)hm.get("returnflag");
	    }
	    else
	    {
	    	returnflag=(String)this.getFormHM().get("returnflag");
	    }
	    this.getFormHM().put("returnflag", returnflag==null?"":returnflag);
		 String nFlag=(String)hm.get("nFlag");//=3人员花名册, 21单位花名册, 41职位花名册, 51基准岗位花名册
		 nFlag=nFlag!=null&&nFlag.trim().length()>0?nFlag:"0";
		 hm.remove("nFlag");
		 this.getFormHM().put("modelFlag",nFlag);
		 this.getFormHM().put("historyRecord", "1");//刚点开，不取数
		 String temp=this.userView.getResourceString(5);
		 if(temp.trim().length()==0) 
				temp="-1";
		 String isPrint="1";//从人事异动进入高级花名册时，判断是否出现打印预演，输出excel和pdf，=1出现=0不出现
		 StringBuffer strsql=new StringBuffer();
		 if("3".equals(nFlag)|| "21".equals(nFlag)|| "41".equals(nFlag)|| "51".equals(nFlag)||
			"1".equals(nFlag)|| "4".equals(nFlag)|| "2".equals(nFlag)){
			 String a_inforkind=(String)hm.get("a_inforkind");
			 a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"1";//=1人员,2机构,3职位,5基准岗位
			 hm.remove("a_inforkind");
			 this.getFormHM().put("inforkind",a_inforkind);
			 
			 String rootdesc=ResourceFactory.getProperty("infor.menu.outhmuster");
			 treeItem.setRootdesc(rootdesc);
			 treeItem.setLoadChieldAction("/general/muster/hmuster/searchHrostertree?flag=1&moduleflag="+nFlag+"&flaga="+a_inforkind);	   
			 treeItem.setAction("javascript:void(0)");
			 
			 String dbpre = (String)hm.get("dbpre");
			 dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"";
			 this.getFormHM().put("dbpre",dbpre);
			 
			 String result=(String)hm.get("result");
			 result=result!=null&&result.trim().length()>0?result:"0";
			 hm.remove("result");
			 this.getFormHM().put("result",result);
//			 if(nFlag.equals("3")){
//				 this.getFormHM().put("historyRecord","0");
//			 }else
//				 this.getFormHM().put("historyRecord","1");
			 strsql.append("SELECT tabid FROM muster_name where ");
			 strsql.append("nmodule='");
			 strsql.append(nFlag);
			 if(!this.userView.isSuper_admin()){
				
				strsql.append("' and tabid in (");   
				strsql.append(temp); 
				strsql.append(") ");
			}else{
				strsql.append("'"); 
			}
	        strsql.append(" and tabid<>1000 and tabid<>1010 and tabid<>1020");// 不显示档案花名册
		 }else if("15".equals(nFlag)){  // 个人所得税
			 String conSQL=PubFunc.decrypt(SafeCode.decode((String)hm.get("conSQL")));
			 String fromTable=(String)hm.get("fromTable");
			 String a_code=(String)hm.get("a_code");
			 a_code=a_code!=null&&a_code.trim().length()>0?a_code:"UN";
			 hm.remove("a_code");
			 
			 String salarydate=(String)hm.get("salarydate");
			 salarydate=salarydate!=null&&salarydate.trim().length()>0?salarydate:"";
			 hm.remove("salarydate");
			 
			 String sortid=(String)hm.get("sortid");
			 sortid=sortid!=null&&sortid.trim().length()>0?sortid:"";
			 hm.remove("sortid");
			 String filterByMdule=(String)hm.get("filterByMdule");
			 StringBuffer url = new StringBuffer();
			 url.append("/general/muster/hmuster/searchHrostertree?flag=1&moduleflag=");
			 url.append(nFlag);
			 url.append("&a_code=");
			 url.append(a_code);
			 url.append("&salarydate=");
			 url.append(salarydate);
			 url.append("&sortid=");
			 url.append(sortid);
			 url.append("&filterByMdule="+filterByMdule);
			 
			 String rootdesc=ResourceFactory.getProperty("infor.menu.outhmuster");
			 treeItem.setRootdesc(rootdesc);
			 treeItem.setLoadChieldAction(url.toString());
			 treeItem.setAction("javascript:void(0)");	   
			 
			 strsql.append("SELECT tabid FROM muster_name where ");
			 strsql.append("nmodule='");
			 strsql.append(nFlag+"'");
			 this.getFormHM().put("conSQL", conSQL);
			 this.getFormHM().put("fromtable", fromTable);
		 }else{
			 	String relatTableid=(String)hm.get("relatTableid");
			 	//hm.remove("relatTableid");该值用来判断是否进行取数，保留lizhenwei 2010-07-08
				String condition=(String)this.getFormHM().get("condition");
				condition=SafeCode.decode(condition);
				hm.remove("condition");
				condition=condition.replaceAll("%20"," ");
				String returnURL=(String)hm.get("returnURL");
				returnURL=SafeCode.decode(returnURL);
				hm.remove("returnURL");
				
				if("5".equals(nFlag)){
					if(hm.get("print")!=null)
						isPrint=(String)hm.get("print");
					String spflag =(String)hm.get("spflag");
					spflag=spflag!=null&&spflag.trim().length()>0?spflag:"1";
					hm.remove("spflag");
					
					this.getFormHM().put("spflag",spflag);
				}else{
					this.getFormHM().put("spflag","");
				}
				String kqtable="";//考勤历史数据
				if("81".equals(nFlag))
				{
					String tmp=(String)hm.get("kqtable");
					if(tmp!=null&&tmp.length()>0)
					{
						kqtable=tmp;
						hm.remove("kqtable");
					}
				}
				this.getFormHM().put("kqtable",kqtable);
				String rootdesc=ResourceFactory.getProperty("infor.menu.outhmuster");

				treeItem.setRootdesc(rootdesc);
				treeItem.setLoadChieldAction("/general/muster/hmuster/searchHrostertree?flag=1&moduleflag="+nFlag+"&relatTableid="+relatTableid);
				treeItem.setAction("javascript:void(0)");	   
				
				this.getFormHM().put("relatTableid",relatTableid);
				this.getFormHM().put("condition",condition);
				this.getFormHM().put("returnURL",returnURL);
				this.getFormHM().put("inforkind",nFlag);

				 strsql.append("SELECT tabid FROM muster_name where ");
				 strsql.append("nmodule='");
				 strsql.append(nFlag);
				 strsql.append("'");
				 if(!"1".equals(nFlag)&&!"4".equals(nFlag)){
					 strsql.append(" and nPrint="+relatTableid);
				 }
				 if(!"5".equals(nFlag)){
					 if(!this.userView.isAdmin()&&!"1".equals(this.userView.getGroupId())){
						 strsql.append(" and tabid in (");   
						 strsql.append(temp); 
						 strsql.append(") ");
					 }
				 }
		 }
		 strsql.append(" order by sortid,norder");
		 this.getFormHM().put("isPrint", isPrint);
		 this.getFormHM().put("treeCode",treeItem.toJS());
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 updateHmusterSort();
		 try {
			 this.frowset=dao.search(strsql.toString());
			 boolean istabid=false;
			 String tabid ="-1";
			 while(this.frowset.next()){
					tabid = this.frowset.getString("tabid");
					if("5".equals(nFlag)){
						istabid=true;
						break;
					}else{
						if(!this.userView.isSuper_admin()){
							if(temp.indexOf(tabid)!=-1){
								istabid=true;
								break;
							}
						}else{
							istabid=true;
							break;
						}
					}
			 }
			 if(!istabid){
				 this.getFormHM().put("tabID","-1");
			 }else{
				 this.getFormHM().put("tabID",tabid);
			 }
			 this.getFormHM().put("isCloseButton", "0");
			 this.getFormHM().put("closeWindow", closeWindow);
		 } catch (SQLException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }

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
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=81 and nPrint="+relatTabid+" order by sortid,norder");
			else if("5".equals(nFlag))
				this.frowset=dao.search("select tabid,cname from muster_name where nModule=5 and nPrint="+relatTabid+" order by sortid,norder");
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
	/**
	 * 默认把排序字段，更新成表号，排序字段值为null的，才做此操作
	 */
	public void updateHmusterSort()
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer();
			sql.append(" update muster_name set norder=tabid ");
			sql.append(" where "+Sql_switcher.isnull("norder", "0"));
			sql.append("=0");
			dao.update(sql.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
