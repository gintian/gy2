package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class SaveHrSyncPostCodeFiled extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");
		String savefieldstr = hsb.getSaveFieldsStr(code_fields); // 要保存的指标字段
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		hsb.syncFieldTrans("t_post_view", code_fields);//数据视图更新翻译指标 wangb 20170619
        this.saveFields(code_fields,dao);
        String mess ="";
        mess=getCardMesslist(code_fields);
		this.getFormHM().put("types","ok");
		this.getFormHM().put("mess",mess);
		//新增字段时更新触发器 wangb 20170619
		boolean fieldAndCode = false;
		String fieldAndCodeSeq = ":";
		if ("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
			fieldAndCode = true;
			fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
		}
		CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
				this.userView, fieldAndCode, fieldAndCodeSeq);
		//先删除触发器 wangb 20170619
		csf.delFrigger(CreateSyncFrigger.POST_FLAG);
		//后添加触发器 wangb 20170619
		csf.createFrigger(CreateSyncFrigger.POST_FLAG);
	}
	 public String getCardMesslist(ArrayList code_fields)
	 {
	    	StringBuffer mess=new StringBuffer();
	    	if(code_fields==null||code_fields.size()<=0)
	    		return "";	
	    	String sql="";
	    	try
	    	{
	    		ContentDAO dao=new ContentDAO (this.getFrameconn());
	    		mess.append("<br>");
	    		int r=1;
	    		for(int i=0;i<code_fields.size();i++)
		    	{
		    		if(code_fields.get(i).toString()!=null&& "b0110".equals(code_fields.get(i).toString()))
		    			mess.append("单位名称("+code_fields.get(i).toString()+")");		    		
		    		if(code_fields.get(i).toString()!=null&& "e01a1".equals(code_fields.get(i).toString()))
		    			mess.append("岗位名称("+code_fields.get(i).toString()+")");
		    		sql="select itemid,itemdesc from fielditem where Upper(itemid)='"+code_fields.get(i).toString().toUpperCase()+"'";
		    		RowSet rs=dao.search(sql);
		    		if(rs.next())
			    	{
			    		   mess.append(rs.getString("itemdesc")+"("+code_fields.get(i).toString()+")");
			    	}
		    		if(r%5==0)
		    			   mess.append("<br>");
		    		   else
		    			 mess.append(",");  
		    	    r++;
		    	}
	    		 mess.append("<br>");
	    	}catch(Exception e)
	    	{
	    	  e.printStackTrace();	
	    	}
	    	return mess.toString();
	  }
	private boolean saveFields(ArrayList code_fields,ContentDAO dao)throws GeneralException 
	{
		boolean isCorrect=false;
		StringBuffer buf=new StringBuffer();
		if(code_fields==null||code_fields.size()<=0)
			buf.append("");
		else
		{
			for(int i=0;i<code_fields.size();i++)
			{
				buf.append(""+code_fields.get(i).toString()+",");
			}
			buf.setLength(buf.length()-1);
		}
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		hsb.setTextValue(hsb.POST_CODE_FIELDS,buf.toString());
		hsb.saveParameter(dao);
		return isCorrect;
	}

}
