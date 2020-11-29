package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class SaveHrSyncPostFiled  extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String type = (String)this.getFormHM().get("type");
		ArrayList code_fields=(ArrayList)this.getFormHM().get("code_fields");
		//String appfield = (String)this.getFormHM().get("appfield");
		//String savefieldstr = hsb.getSaveFieldsStr(code_fields); // 要保存的指标字段
		ContentDAO dao = new ContentDAO(this.getFrameconn());
        this.saveFields(type,code_fields,dao);
        String mess ="";
        mess=getCardMesslist(code_fields);
		this.getFormHM().put("types","ok");
		this.getFormHM().put("mess",mess);
		String postcodefieldstr=hsb.getTextValue(hsb.POST_CODE_FIELDS);
		ArrayList debarlist = this.debarFields(code_fields,postcodefieldstr);
		this.savePostCodeFields(debarlist,dao);
		String mess2 = "";
		mess2=getCardMesslist(debarlist);
		this.getFormHM().put("mess2",mess2);
		hsb = new HrSyncBo(this.frameconn);
		hsb.creatPostTable("t_post_view");
		//新增字段时更新触发器 wangb 20170615
		boolean fieldAndCode = false;
		String fieldAndCodeSeq = ":";
		if ("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
			fieldAndCode = true;
			fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
		}
		CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
				this.userView, fieldAndCode, fieldAndCodeSeq);
		//先删除触发器 wangb 20170615
		csf.delFrigger(CreateSyncFrigger.POST_FLAG);
		//后添加触发器 wangb 20170615
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
		    		if(code_fields.get(i).toString()!=null&& "e0122".equals(code_fields.get(i).toString()))
		    			mess.append("部门名称("+code_fields.get(i).toString()+")");
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
	private boolean saveFields(String type,ArrayList code_fields,ContentDAO dao)throws GeneralException 
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
		if("K".equalsIgnoreCase(type)){
			  String fields = hsb.getTextValue(hsb.POST_FIELDS);			  
			  for(int i=0;i<code_fields.size();i++){
				  if(fields.indexOf(code_fields.get(i).toString())==-1)
				  hsb.setAppAttributeValue(hsb.K,code_fields.get(i).toString(),code_fields.get(i).toString());
			  }
			  String[] oldfields = fields.split(",");
			  for(int i=0;i<oldfields.length;i++){
				  if(buf.toString().indexOf(oldfields[i])==-1){
					  hsb.delAppAttributeValue(hsb.K,oldfields[i]);
					  hsb.deleteColumn("t_post_view",oldfields[i]);
				  }
			  }
			  hsb.setTextValue(hsb.POST_FIELDS,buf.toString());
		  }
		hsb.saveParameter(dao);
		return isCorrect;
	}
	private boolean savePostCodeFields(ArrayList code_fields,ContentDAO dao)throws GeneralException 
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
	private ArrayList debarFields(ArrayList fileds,String codefileds){
		  ArrayList codefiledlist = new ArrayList();
		  if(fileds==null||fileds.size()<=0)
			  return codefiledlist;
		  String[] codefiled = codefileds.split(",");
		  for(int i=0;i<codefiled.length;i++){
			  if(fileds.indexOf(codefiled[i])!=-1){
				  codefiledlist.add(codefiled[i]);
			  }
		  }
		  return codefiledlist;
		  
	  }


}
