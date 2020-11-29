package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryFieldInfoTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String querytype=(String)hm.get("querytype");
		if(querytype==null||querytype.length()<=0)
			querytype="1";
		InfoUtils infoUtils=new InfoUtils();
		ArrayList queryfieldlist=(ArrayList)this.getFormHM().get("queryfieldlist");
		if(queryfieldlist==null||queryfieldlist.size()<=0)
		{
			queryfieldlist=infoUtils.selectField(querytype,"a0101",this.getFrameconn());
			//过滤非人员子集的指标
			if(queryfieldlist != null && queryfieldlist.size() > 0){
			    ArrayList list = new ArrayList();
			    for(int i = 0; i < queryfieldlist.size(); i++) {
			        FieldItem fi = (FieldItem) queryfieldlist.get(i);
			        if(fi.getFieldsetid().startsWith("A")) 
			            list.add(fi);
			    }
			    
			    queryfieldlist = list;
			}
		}		
		
		this.getFormHM().put("queryfieldlist", queryfieldlist);
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String browse_search_state=sysoth.getValue(Sys_Oth_Parameter.BROWSE_SEARCH_STATE);//人员信息浏览查询项状态 0，隐藏 1，显示
	    browse_search_state=browse_search_state!=null&&browse_search_state.length()>0?browse_search_state:"0";
	    if("0".equals(browse_search_state))
	    	this.getFormHM().put("isShowCondition", "none");
	    else
	    	this.getFormHM().put("isShowCondition", "block");
	    String photo_other_view=sysoth.getValue(Sys_Oth_Parameter.PHOTO_OTHER_VIEW);
		if(photo_other_view==null||photo_other_view.length()<=0)
			photo_other_view="";
		this.getFormHM().put("photo_other_view", photo_other_view);
		String orglike=(String)this.getFormHM().get("orglike");
		if(orglike==null||orglike.length()<=0)
			orglike="1";
		String query=(String)this.getFormHM().get("query");
		if(query==null||query.length()<=0)
			query="";
		String check=(String)this.getFormHM().get("check");
		if(check==null||check.length()<=0)
			check="no";
		this.getFormHM().put("orglike", orglike);
		this.getFormHM().put("query", query);
		this.getFormHM().put("check", check);
		
		this.getFormHM().put("querySecond", "0");
		
		/*String code =this.userView.getManagePrivCodeValue();		
		if(code==null||code.length()<=0)
		{
			if(this.userView.isSuper_admin()||(this.userView.getManagePrivCode()!=null&&this.userView.getManagePrivCode().equals("UN")))
			{
				String sql="select codeitemid from organization where codeitemid=parentid order by a0000";
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				try {
					this.frowset=dao.search(sql);
					if(this.frowset.next())
						code=this.frowset.getString("codeitemid");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}	
		}
		this.getFormHM().put("code", code);*/
		//this.getFormHM().put("queryfieldhtml", queryfieldhtml);
	}


}
