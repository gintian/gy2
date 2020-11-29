package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class OperTypeTrans extends IBusiness { 

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");

		String flag = "";
		if(hm!=null){
			flag =(String)hm.get("flag");
			flag=flag!=null&&flag.trim().length()>0?flag:"";
			hm.remove("flag");
		}
		
		String chkflag = (String)this.getFormHM().get("chkflag");
		chkflag=chkflag!=null&&chkflag.trim().length()>0?chkflag:"";
		
		String a_inforkind = "";
		if(hm!=null){
			a_inforkind =(String)hm.get("a_inforkind");
			a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"";
			hm.remove("a_inforkind");
		}else{
			a_inforkind =(String)this.getFormHM().get("a_inforkind");
			a_inforkind=a_inforkind!=null&&a_inforkind.trim().length()>0?a_inforkind:"";
		}
		
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		ArrayList dblist= new ArrayList();
		if("addtype".equals(chkflag)){
			String typename = (String)this.getFormHM().get("typename");
			typename=typename!=null&&typename.trim().length()>0?typename:"";
			if(typename.trim().length()>0){
				String styleid = musterbo.styleId();
				ContentDAO dao  = new ContentDAO(this.getFrameconn());
				try {
					dao.update("insert into lstyle(styleid,styledesc) values('"+styleid+"','"+typename+"')");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dblist = musterbo.getMusterTypeList(a_inforkind,"1");
		}else if("delete".equals(chkflag)){
			String styleid = (String)this.getFormHM().get("styleid");
			styleid=styleid!=null&&styleid.trim().length()>0?styleid:"";
			String[] arr = styleid.split(",");
			StringBuffer styleidStr = new StringBuffer();
			styleidStr.append("('");
			
			for(int i=0;i<arr.length;i++){
				if(arr[i]!=null&&arr[i].trim().length()>0){
					styleidStr.append(arr[i]+"','");
				}
			}
			styleidStr.append("')");
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			try {
				dao.update("delete from lstyle where styleid in "+styleidStr);
				StringBuffer updateStr = new StringBuffer();
				updateStr.append("update lname set ModuleFlag=");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2){
					updateStr.append("substr(moduleflag,1,1)||'00'||substr(moduleflag,4,17)");
					updateStr.append(" where substr(moduleflag,2,2) in ");
					updateStr.append(styleidStr);
				}else if(Sql_switcher.searchDbServer()==Constant.MSSQL){
					updateStr.append("STUFF(moduleflag,2,2,'00') where substring(moduleflag,2,2) in ");
					updateStr.append(styleidStr);
				}else {
					updateStr.append("substr(moduleflag,1,1)+'00'+ substring(moduleflag,4,17)");
					updateStr.append(" where substr(moduleflag,2,2) in ");
					updateStr.append(styleidStr);
				}
				dao.update(updateStr.toString());
				if(this.userView.isSuper_admin())
					dblist = musterbo.getMusterTypeList(a_inforkind,"1");
				else
					dblist = musterbo.getMusterTypeList(a_inforkind,"1");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if("add".equals(flag)){
			this.getFormHM().put("chkflag","add");
			dblist = musterbo.getMusterTypeList(a_inforkind,"1");
		}
		if("del".equals(flag)){
			this.getFormHM().put("chkflag","del");
			if(this.userView.isSuper_admin())
				dblist = musterbo.getMusterTypeList(a_inforkind,"1");
			else
				/**分类下没有名册的分类，也可以删除*/
				dblist = musterbo.getMusterTypeList(a_inforkind,"1");
		}
		ArrayList typelist=new ArrayList();
		for(int i=0;i<dblist.size();i++){
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dblist.get(i);
			String styleid = dbname.getString("styleid");
			vo.setDataName(dbname.getString("styledesc"));
			vo.setDataValue(styleid+":"+dbname.getString("styledesc"));
			typelist.add(vo);
		}	
		if(typelist.size()<1){
			CommonData vo=new CommonData();
			vo.setDataName("");
			vo.setDataValue("");
			typelist.add(vo);
		}
		this.getFormHM().put("typelist",typelist);
		this.getFormHM().put("a_inforkind","a_inforkind");
	}
	

}
