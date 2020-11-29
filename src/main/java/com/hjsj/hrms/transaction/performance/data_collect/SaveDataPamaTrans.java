package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveDataPamaTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList paramList = new ArrayList();
			String sdbid=(String) this.getFormHM().get("sdbid");//从界面获取被选中的人员库
			if(!("".equals(sdbid)||sdbid==null)){
				sdbid=sdbid.substring(0, sdbid.length()-1);
			}
			String [] dbid=null ;
			if(!("".equals(sdbid)||sdbid==null)){
				 dbid= sdbid.split(",");
			}
			sdbid="";
			if(dbid!=null){
				for(int i=0;i<dbid.length;i++){
					String sql="select pre from dbname where dbid="+dbid[i];
					this.frowset = dao.search(sql);
					while(frowset.next()){
						sdbid=sdbid+frowset.getString(1)+",";
					}
				}
			}
			if(!("".equals(sdbid)||sdbid==null)){
				sdbid=sdbid.substring(0, sdbid.length()-1);
			}
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			String audit=(String) this.getFormHM().get("audit");//获取审批状态的相关指标
			String fieldsetid =(String) this.getFormHM().get("fieldsetid");//获取子集ID
			String personScope = (String) this.getFormHM().get("personScope");//涉及是简单、复杂条件
			String cexpr = (String) this.getFormHM().get("cexpr");//涉及条件内容
			String params ="<scope cbase='"+sdbid+"' state_id='"+audit+"' set_id='"+fieldsetid+"' flag='"+personScope+"'>"+cexpr+"</scope>";
			StringBuffer xmlValue=new StringBuffer("<?xml version=\"1.0\" encoding=\"GB2312\"?><Params>");
			xmlValue.append(params);
			xmlValue.append("</Params>");
			String sql = "select * from Constant where constant='DATA_COLLECT_SCOPE'";
			try(
				ResultSet rs = dao.search(sql);
			) {
				if (rs.next()) {
					sql = "update Constant set str_value=? where constant='DATA_COLLECT_SCOPE'";
					bo.setAttributeValue("set_id", fieldsetid, fieldsetid);//此处一定要先存set_id，因为要用这个判断是否已经存在这个子集的信息
					bo.setAttributeValue("cbase", sdbid, fieldsetid);
					bo.setAttributeValue("state_id", audit, fieldsetid);
					bo.setAttributeValue("flag", personScope, fieldsetid);
					bo.setTextValue(cexpr, fieldsetid);
					String value = bo.saveStrValue();
					paramList.add(value);
					dao.update(sql, paramList);
				} else {
					sql = "insert into Constant (constant,str_value) values('DATA_COLLECT_SCOPE',?) ";
					paramList.add(xmlValue.toString());
					dao.insert(sql, paramList);
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
