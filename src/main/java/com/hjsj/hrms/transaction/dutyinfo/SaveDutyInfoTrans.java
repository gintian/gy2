package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public SaveDutyInfoTrans() {
	}
	
	public SaveDutyInfoTrans(Connection conn){
		this.frameconn = conn;
	}
	
	

	public void execute() throws GeneralException {
		List fieldlist=(List)this.getFormHM().get("infofieldlist");	 
		String code=(String)this.getFormHM().get("code");
		if(code==null||code.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","请从左侧选择机构！","",""));
		String first=(String)this.getFormHM().get("first");
		String grade=(String)this.getFormHM().get("grade");
		String codeitemid="";
		String corcode="";
		String codeitemdesc="";
		RecordVo vo=new RecordVo("k01");		
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldlist.get(i);
			
			if(fieldItem.getItemid()!=null && fieldItem.getItemid().length()>0)
			{  
				if("codeitemid".equalsIgnoreCase(fieldItem.getItemid()))
				{
					codeitemid=fieldItem.getValue();
					continue;
				}else if("corcode".equalsIgnoreCase(fieldItem.getItemid()))
				{
					corcode=fieldItem.getValue();
					continue;
				}else if("codeitemdesc".equalsIgnoreCase(fieldItem.getItemid()))
				{
					codeitemdesc=fieldItem.getValue();
					continue;
				}
						    
			    //System.out.println(fieldItem.getItemid() + fieldItem.getItemtype()+"---"+fieldItem.getViewvalue());
			   // System.out.println(fieldItem.getItemid() + PubFunc.DateStringChange(fieldItem.getValue()));
				if("D".equals(fieldItem.getItemtype()) && (fieldItem.getValue() != null&&fieldItem.getValue().length()>0))
				{
					 vo.setDate(fieldItem.getItemid().toLowerCase(), PubFunc.DateStringChangeValue(fieldItem.getValue()));
				}else if("M".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						vo.setString(fieldItem.getItemid().toLowerCase(), null);
					}
					else
					{
						String content=fieldItem.getValue();						
						content=PubFunc.getStr(content);						
						vo.setString(fieldItem.getItemid().toLowerCase(), content);
					}
				}else if("N".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
					}
					else
					{
						vo.setString(fieldItem.getItemid().toLowerCase(), fieldItem.getValue());
					}
				}
				else
				{
				    if(fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
				    {
					    	
					     vo.setString(fieldItem.getItemid().toLowerCase(), null);
					}else
					{
						String content=PubFunc.getStr(fieldItem.getValue());
					    vo.setString(fieldItem.getItemid().toLowerCase(), PubFunc.splitString(content,fieldItem.getItemlength()));
					}
				}
				
			}
		}
		if(codeitemid==null||codeitemid.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","系统代码不能为空，操作失败！","",""));
		if(codeitemdesc==null||codeitemdesc.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","岗位名称不能为空，操作失败！","",""));
		String tmp = isCheckCodeItemid(code+codeitemid);
		if(tmp.length()>0)
		{
			tmp = tmp.substring(1);
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors1")+tmp,"",""));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new java.util.Date());
		corcode=corcode==null?"":corcode;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
		if("1".equals(corcode_unique)&&corcode.length()>0){
			try {
				this.frowset=dao.search("select codeitemid,codeitemdesc from (select codeitemid,codeitemdesc from organization where corcode='"+corcode+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date union all select codeitemid,codeitemdesc from vorganization where corcode='"+corcode+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date) tt");
				if(this.frowset.next()){
							throw GeneralExceptionHandler.Handle(new GeneralException("","岗位代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		if(addOrgData(code,codeitemid,codeitemdesc,corcode,grade,first))
		{
			RecordVo pos_vo= ConstantParamter.getRealConstantVo("POS_CODE_FIELD");//岗位代码
			if(pos_vo!=null)
			{
			    	String pos_code_field=pos_vo.getString("str_value");
			    	FieldItem f = DataDictionary.getFieldItem(pos_code_field);
			    	if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field) && f!=null && "1".equals(f.getUseflag()))
			    	{
			    		vo.setString(pos_code_field, corcode);
			    	}
			}
			new StructureExecSqlString().InfoInsertVo("4","k01",vo,code+codeitemid,userView.getUserName(),this.getFrameconn());
			this.getFormHM().put("edit_flag", "edit");
			this.getFormHM().put("return_codeid", code);
			this.getFormHM().put("code", code+codeitemid);
			this.getFormHM().put("kind", "0");
			this.getFormHM().put("parentid", code);
		}else
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","新增岗位操作失败！","",""));
		}
		
	}
	public boolean addOrgData(String code,String codeitemid,String codeitemdesc,String corcode,String grade,String first)throws GeneralException
	{
		 OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
		 ContentDAO dao=new ContentDAO(this.getFrameconn());	
		 boolean doInitLayer=false;
		 AddOrgInfo ao=new AddOrgInfo(this.frameconn);
		 String codesetid="@K";
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 Date start_date=null;
		 Date end_date=null;		
		 try {
				start_date = new Date(sdf.parse(sdf.format(new java.util.Date())).getTime());
				end_date = new Date(sdf.parse("9999-12-31").getTime());
		 } catch (ParseException e1) {
				e1.printStackTrace();
		 }
		 boolean isCorrect=true;
		 StringBuffer sqlstr=new StringBuffer();
		 RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
//		 sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,layer)");
		 sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,layer,levelA0000)");//添加同级排序字段  wangb 20181204 bug 42657
//		 sqlstr.append("values(?,?,?,?,?,?,?,?,?,?,?,?)");
		 sqlstr.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?)");//添加同级排序字段  wangb 20181204 bug 42657
		 
		 ArrayList sqlvalue=new ArrayList();
		 sqlvalue.add("@K");
		 sqlvalue.add((code +codeitemid).toUpperCase());
		 sqlvalue.add(PubFunc.splitString(codeitemdesc,codeitemdesclen));
		 String parentid="";
		 if(code!=null && code.trim().length()>0){
			 parentid=code.toUpperCase();
			 sqlvalue.add(code.toUpperCase());
		 }else{
			 parentid=(code + codeitemid).toUpperCase();
			 sqlvalue.add((code + codeitemid).toUpperCase());
		 }
		 sqlvalue.add((code + codeitemid).toUpperCase());
		 sqlvalue.add(null);
		 sqlvalue.add(grade);
		 sqlvalue.add(getMaxA0000(code));
		 sqlvalue.add(corcode);
		 sqlvalue.add(start_date);
		 sqlvalue.add(end_date);
		 int layer=0;
			try {
				layer = ao.getLayer(parentid, (code +codeitemid).toUpperCase(), codesetid);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				if(0==layer){
					doInitLayer=true;
				}
				sqlvalue.add(new Integer(layer));
		 sqlvalue.add(getMaxLevelA0000(code));//新增机构时，levelA0000 同级排序 指标也要添加数据   wangb 20181204 bug 42657
		 CodeItem item=new CodeItem();
		 item.setCodeid(codesetid);
		 item.setCodeitem((code +codeitemid).toUpperCase());
		 item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
		 if(code!=null && code.trim().length()>0)
				item.setPcodeitem(code.toUpperCase());
		 else
		    	item.setPcodeitem((code +codeitemid).toUpperCase());
		 item.setCcodeitem((code +codeitemid).toUpperCase());
		 item.setCodelevel(grade+"");
		 AdminCode.addCodeItem(item);
		 AdminCode.updateCodeItemDesc(codesetid,(code + codeitemid).toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
		 try
		 {
			 dao.insert(sqlstr.toString(),sqlvalue);
			 if("1".equals(first))
			 {
					StringBuffer update=new StringBuffer();
					update.append("update organization set childid='");
					update.append(code + codeitemid);
					update.append("' where codeitemid='");
					update.append(code);
					update.append("'");	
					dao.update(update.toString());
					first="0";
			 }
			 if(doInitLayer){//重置层级
					ao.executeInitLayer();
				}
		 }catch(Exception e)
		 {
			isCorrect=false; 
            e.printStackTrace();
		 }
		 return isCorrect;
	}
	private String getMaxA0000(String descode) throws GeneralException
	{
		String a0000="1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
		   this.frowset=dao.search("select max(a0000) as a0000 from organization where codeitemid like '" + descode + "%'");
		   if(this.frowset.next())
		   {
			   a0000=String.valueOf(this.frowset.getInt("a0000") + 1);
			   dao.update("update organization set a0000=a0000 + 1 where a0000>" + this.frowset.getInt("a0000"));
		   }
		   else
			   dao.update("update organization set a0000=a0000 + 1 where a0000>0"); 
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return a0000;
	}
	private String isCheckCodeItemid(String codeitemid)
	{
		boolean isCorrect=true;
		StringBuffer sql=new StringBuffer();
		sql.append("select codeitemdesc,codeitemid from organization where ");
		sql.append("codeitemid like '"+codeitemid+"%'");		
		sql.append(" union select codeitemdesc,codeitemid from vorganization where ");
		sql.append("codeitemid like '"+codeitemid+"%' order by codeitemid");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    StringBuffer sb = new StringBuffer();
	    try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				sb.append("、"+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * 同级机构排序获取levelA0000最大值   wangb    20181204   bug 42657
	 * @param descode
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxLevelA0000(String descode)throws GeneralException
	{
		String levelA0000 = "1";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
		try {
			list.add(descode);
			if(descode == null || "".equalsIgnoreCase(descode) || descode.trim().length()==0)//不存在 当前机构为顶级机构  bug 42657 wangb  20181204
				this.frowset=dao.search("select MAX(LEVELA0000) as levelA0000 from organization where codeitemid=parentid");
			else//上级机构存在
				this.frowset=dao.search("SELECT MAX(LEVELA0000) as levelA0000 FROM organization where PARENTID=?",list);
			if(this.frowset.next())
				levelA0000 = String.valueOf(this.frowset.getInt("levelA0000") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return levelA0000;
	}
}
