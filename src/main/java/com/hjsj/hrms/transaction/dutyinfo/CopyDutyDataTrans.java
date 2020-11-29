package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CopyDutyDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private String type="";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public void execute() throws GeneralException 
	{
		String content=(String)this.getFormHM().get("content");		
		ArrayList orgcodeitemid=(ArrayList)this.getFormHM().get("orgcodeitemid");
		type = (String)this.getFormHM().get("type");//标示复制的是单位（org）还是岗位（""）
		type = type!=null?type:"";
		if(content==null||content.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("请选择复制组织单元！"));
		if(orgcodeitemid==null||orgcodeitemid.size()<=0)
			if("org".equals(type)){
				throw GeneralExceptionHandler.Handle(new GeneralException("请选择要复制单位！"));
			}else
				throw GeneralExceptionHandler.Handle(new GeneralException("请选择要复制岗位！"));
		
		
		Date start_date=null;
		Date end_date=null;		
		try {
			start_date = new Date(sdf.parse(sdf.format(new java.util.Date())).getTime());
			end_date = new Date(sdf.parse("9999-12-31").getTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String[] codes=content.split(",");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    AddOrgInfo addOrgInfo=new AddOrgInfo();
	    String flag="";
		try
		{
			for(int i=0;i<codes.length;i++)
			{
				String a_code=codes[i];
				if(a_code!=null&&a_code.length()>2)
				{
					String codesetid=a_code.substring(0,2);
					String code=a_code.substring(2);
					StringBuffer strsql=new StringBuffer();
					strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
					strsql.append(code);
					strsql.append("' and codeitemid<>parentid ");
					strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from vorganization where parentid='");
					strsql.append(code);
					strsql.append("' and codeitemid<>parentid ");
					strsql.append(" order by codeitemid desc");
					this.frowset=dao.search(strsql.toString());
					boolean b = false;
					String chilecode="";
					String codeitemid="";					
					int grade=0;
					int len=30;
					String first="1";
					while(this.frowset.next())
					{		
						b = true;	
						first="0";
						chilecode=this.frowset.getString("codesetid");
					    codeitemid=this.frowset.getString("codeitemid");					  
					    grade=this.frowset.getInt("grade");					  
					    if(chilecode!=null)
					    {
					    	
					    	
						    if(code!=null)
						    {
						    		len=codeitemid.trim().length()-code.trim().length();
						    }
						    else
						    {
						    		len=codeitemid.trim().length();
						    }
						
							codeitemid=addOrgInfo.GetNext(codeitemid,code);
							
							
							break;				    	
					    }
					}				
					if(!b&&"org".equals(type))//要复制到的节点下没有子节点
					{
						first="1";
						strsql.delete(0,strsql.length());
						strsql.append("select grade from organization where codeitemid='");
				    	strsql.append(code);
				    	strsql.append("' and codesetid='");
				        strsql.append(codesetid);
				        strsql.append("'");
				    	this.frowset=dao.search(strsql.toString());
				    	grade=1;
				    	if(this.frowset.next())
				    	{
				    	    grade=this.frowset.getInt("grade");
				    	    grade=grade + 1;
				    	}		    	
				    	if(code!=null && code.trim().length()>0)
				        {
				           len=30-code.trim().length();
				    	}else
				        {
				    	  
				    	  strsql.delete(0,strsql.length());
						  strsql.append("select ");
						  strsql.append(Sql_switcher.length("codeitemid"));
						  strsql.append(" as codeitemidlen from organization where parentid=codeitemid and codesetid='");
				          strsql.append(codesetid);
				          strsql.append("'");	
					      this.frowset=dao.search(strsql.toString()); 
					      if(this.frowset.next())
					      {
					    	  first="0";
					    	  len=this.frowset.getInt("codeitemidlen");
					    	  String sql="select * from organization where parentid=codeitemid ";
					    	  sql=sql+" order by codeitemid desc";
					    	  this.frowset=dao.search(sql);
					    	  if(this.frowset.next())
					    	  {
					    		  codeitemid=this.frowset.getString("codeitemid");
					    		  
					    	  }
					      }
					      else
					      {
					    	  first="1";
					      }
				      }
				      codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
				   }
					
				   copyDutyData(code,codeitemid,grade,orgcodeitemid,start_date,end_date,first);
				}
			}
			flag="ok";
			/*
			 * bug 48721 复制岗位不需要充值层级。导致触发器卡死 wangb 20190624
			//重置grade
			StringBuffer sql = new StringBuffer();
			sql.append("update organization set grade=1 where parentid=codeitemid");
			dao.update(sql.toString());
			sql.delete(0,sql.length());
			sql.append("update organization set grade=(select grade+1 from organization o where organization.parentid=o.codeitemid) where parentid<>codeitemid");
			dao.update(sql.toString());
			*/
		}catch(Exception e)
		{
           e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}
	private boolean copyDutyData(String code/*要复制到的节点*/,String codeitemid,int grade,ArrayList orgcodeitemidlist/*要复制的节点*/,Date start_date,Date end_date,String first)throws GeneralException
	{
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 StringBuffer sql2=new StringBuffer(); 
		 for(int i=0;i<orgcodeitemidlist.size();i++)
	     {
	    	 String orgcodeitemid=(String)orgcodeitemidlist.get(i);
	    	 if(orgcodeitemid==null||orgcodeitemid.length()<=0)
	    		 continue;
			 if("org".equalsIgnoreCase(type)){
				 sql2.setLength(0);
				 sql2.append("select max("+Sql_switcher.length("codeitemid")+") codeitemidlen from organization");
		 		 sql2.append(" where codeitemid like '"+orgcodeitemid+"%' and codeitemid<>'"+orgcodeitemid+"' and codeitemid<>'"+(code +codeitemid).toUpperCase()+"'");
		 		 sql2.append(" and "+Sql_switcher.dateValue(sdf.format(new java.util.Date()))+" between start_date and end_date");
		 		 try {
					this.frowset = dao.search(sql2.toString());
					int len = 0;
					if(this.frowset.next()){
						len=this.frowset.getInt("codeitemidlen");
					}
					if(len==0){
						len = code.length()+codeitemid.length();
					}else{
						len = len-orgcodeitemid.length()+code.length()+codeitemid.length();
					}
					if(len>=30){
						this.getFormHM().put("errordes", "当前组织机构代码 已达最大长度，无法新增下级机构！");
						 throw new GeneralException("", "当前组织机构代码 已达最大长度，无法新增下级机构！", "", "");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			 }
	     }
		 
		 AddOrgInfo addOrgInfo=new AddOrgInfo();
		 String codesetid="@K";
		 boolean isCorrect=true;
		 StringBuffer sqlstr=new StringBuffer();
		 sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,levelA0000)");
		 sqlstr.append("values(?,?,?,?,?,?,?,?,?,?,?,?)");
		 try
		 {
			 String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
			 for(int i=0;i<orgcodeitemidlist.size();i++)
		     {
		    	 String orgcodeitemid=(String)orgcodeitemidlist.get(i);
		    	 if(orgcodeitemid==null||orgcodeitemid.length()<=0)
		    		 continue;
		    	 
		    	 boolean need=false;
		    	 if(!"org".equalsIgnoreCase(type)){
					 if("1".equals(first)){
						 String sql = "select parentid from organization where codeitemid='"+orgcodeitemid+"'";
						 this.frowset = dao.search(sql);
						 if(this.frowset.next()){
							 String parentid = this.frowset.getString("parentid");
							 codeitemid = orgcodeitemid.substring(parentid.length());
						 }
					 }else{
						 String sql = "select parentid from organization where codeitemid='"+orgcodeitemid+"'";
						 this.frowset = dao.search(sql);
						 String tmpcodeitemid ="";
						 if(this.frowset.next()){
							 String parentid = this.frowset.getString("parentid");
							 tmpcodeitemid = orgcodeitemid.substring(parentid.length());
						 }
						 
						 sql = "select codeitemid from organization where parentid='"+code+"' and parentid<>codeitemid";
						 this.frowset = dao.search(sql);
						 int len =0;
						 int m=0;
						 boolean flag = true;
						 while(this.frowset.next()){
							 String ttcodeitemid = this.frowset.getString("codeitemid");
							 if(m==0){
								 len = ttcodeitemid.length()-code.length();
								 m=1;
							 }
							 if(len==tmpcodeitemid.length()){
								 if((code+tmpcodeitemid).equalsIgnoreCase(ttcodeitemid)){
									 flag = false;
									 break;
								 }
							 }else{
								 flag = false;
								 break;
							 }
						 }
						 if(flag){
							 codeitemid = tmpcodeitemid;
							 need = true;
						 }
					 }
				 }
		    	 StringBuffer sql=new StringBuffer();  
		    	 sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade,corcode from organization");
		 		 sql.append(" where codeitemid = '"+orgcodeitemid+"'");
		 		 String corcode="";
			     String codeitemdesc="";
		 		 this.frowset=dao.search(sql.toString());
		 		 if(this.frowset.next())
		 		 {
		 			corcode=this.frowset.getString("corcode");
		 			codeitemdesc=this.frowset.getString("codeitemdesc");
		 			codesetid=this.frowset.getString("codesetid");
		 		 }
		    	 ArrayList sqlvalue=new ArrayList();
				 sqlvalue.add(codesetid);
				 sqlvalue.add((code +codeitemid).toUpperCase());
				 sqlvalue.add(PubFunc.splitString(codeitemdesc,50));
				 if(code!=null && code.trim().length()>0)
					 sqlvalue.add(code.toUpperCase());
				 else
					 sqlvalue.add((code + codeitemid).toUpperCase());
				 sqlvalue.add((code + codeitemid).toUpperCase());
				 sqlvalue.add(null);
				 sqlvalue.add( new Integer(grade));
				 sqlvalue.add(getMaxA0000(code,dao));
				 if("1".equals(corcode_unique)){
					 sqlvalue.add("");
				 }else
					 sqlvalue.add(corcode);
				 sqlvalue.add(start_date);
				 sqlvalue.add(end_date);	
				 sqlvalue.add(this.getMaxLevelA0000(code));
				 dao.insert(sqlstr.toString(),sqlvalue);
				 
				 CodeItem item=new CodeItem();
				 item.setCodeid(codesetid);
				 item.setCodeitem((code +codeitemid).toUpperCase());
				 item.setCodename(PubFunc.splitString(codeitemdesc,50));
				 if(code!=null && code.trim().length()>0)
						item.setPcodeitem(code.toUpperCase());
				 else
				    	item.setPcodeitem((code +codeitemid).toUpperCase());
				 item.setCcodeitem((code +codeitemid).toUpperCase());
				 item.setCodelevel(grade+"");
				 AdminCode.addCodeItem(item);
				 AdminCode.updateCodeItemDesc(codesetid,(code + codeitemid).toUpperCase(),PubFunc.splitString(codeitemdesc,50));
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
				 if("org".equalsIgnoreCase(type)){
					 copySetUint(dao,orgcodeitemid,(code +codeitemid).toUpperCase(),this.userView.getUserName());
				 }else{
					 copySetPos(dao,orgcodeitemid,(code +codeitemid).toUpperCase(),code,this.userView.getUserName());
				 }
				//扩展复制部门时连同子节点复制(包括其下的岗位)
				 if("org".equalsIgnoreCase(type)){
					 sql.setLength(0);
					 sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade,corcode from organization");
			 		 sql.append(" where codeitemid like '"+orgcodeitemid+"%' and codeitemid<>'"+orgcodeitemid+"' and codeitemid<>'"+(code +codeitemid).toUpperCase()+"'");
			 		 sql.append(" and "+Sql_switcher.dateValue(sdf.format(new java.util.Date()))+" between start_date and end_date order by codeitemid");
				     String olditemid="";
				     String newitemid="";
				     int oldtopgrade=0;
				     String oldtopparentid="";
				     String oldparentid="";
				     String newparentid="";
				     String oldchildid="";
				     String newchildid="";
				     int newgrade=0;
			 		 this.frowset=dao.search(sql.toString());
			 		 boolean changechild=true;
			 		 while(this.frowset.next()) {
			 			corcode=this.frowset.getString("corcode");
			 			codeitemdesc=this.frowset.getString("codeitemdesc");
			 			codesetid=this.frowset.getString("codesetid");
			 			olditemid=this.frowset.getString("codeitemid");
			 			oldparentid=this.frowset.getString("parentid");
			 			oldchildid=this.frowset.getString("childid");
			 			if(oldparentid.equalsIgnoreCase(orgcodeitemid)){
			 				oldtopgrade=this.frowset.getInt("grade");
			 				oldtopparentid=this.frowset.getString("parentid");
			 				if(changechild){
				 				StringBuffer update=new StringBuffer();
								update.append("update organization set childid='");
								update.append(code +codeitemid +(olditemid.substring(oldtopparentid.length())));
								update.append("' where codeitemid='");
								update.append(code +codeitemid);
								update.append("'");	
								dao.update(update.toString());
								changechild=false;
			 				}
			 			}
			 			
				    	 sqlvalue=new ArrayList();
						 sqlvalue.add(codesetid);
						 newitemid=(code +codeitemid +(olditemid.substring(oldtopparentid.length()))).toUpperCase();
						 sqlvalue.add(newitemid);
						 sqlvalue.add(PubFunc.splitString(codeitemdesc,50));
						 newparentid=(code +codeitemid+(oldparentid.substring(oldtopparentid.length()))).toUpperCase();
						 sqlvalue.add(newparentid);
						 newchildid=(code +codeitemid+((olditemid.substring(oldtopparentid.length())))).toUpperCase();
						 sqlvalue.add(newchildid);
						 sqlvalue.add(null);
						 newgrade=this.frowset.getInt("grade")-oldtopgrade+grade;
						 sqlvalue.add( new Integer(newgrade));
						 sqlvalue.add(getMaxA0000(code,dao));
						 if("1".equals(corcode_unique)){
							 sqlvalue.add("");
						 }else
							 sqlvalue.add(corcode);
						 
						 sqlvalue.add(start_date);
						 sqlvalue.add(end_date);	
						 sqlvalue.add(getMaxLevelA0000(newparentid));
						 dao.insert(sqlstr.toString(),sqlvalue);
						 
						 item=new CodeItem();
						 item.setCodeid(codesetid);
						 item.setCodeitem(newitemid);
						 item.setCodename(PubFunc.splitString(codeitemdesc,50));
						 item.setPcodeitem(newparentid);
						 item.setCcodeitem(corcode);
						 item.setCodelevel(newgrade+"");
						 AdminCode.addCodeItem(item);
						 if(!"@K".equalsIgnoreCase(codesetid)){
							 copySetUint(dao,olditemid,newitemid,this.userView.getUserName());
						 }else{
							 copySetPos(dao,olditemid,newitemid,newparentid,this.userView.getUserName());
						 }
			 		 }
				 }
				 if(need){
					 String tsql = "select codeitemid from organization where parentid='"+code+"' and parentid<>codeitemid order by codeitemid desc";
					 this.frowset = dao.search(tsql);
					 if(this.frowset.next()){
						 codeitemid = this.frowset.getString("codeitemid");
						 codeitemid = codeitemid.substring(code.length());
					 }
				 }
				 if(i<orgcodeitemidlist.size()-1)
					 codeitemid=addOrgInfo.GetNext((code +codeitemid).toUpperCase(),code);	
				
		     }
		 }catch(Exception e)
		 {
			 isCorrect=false;
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);			
		 }
	     return isCorrect;
	}
	private void copySetPos(ContentDAO dao,String orgcodeitemid,String codeitemid,String e0122,String CreateUserName)throws GeneralException
	{
		String itemid="";
		String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
		RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
		if(pos_code_field_constant_vo!=null)
		{
		  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
		  if(pos_code_field!=null&&pos_code_field.length()>1){
			  FieldItem item = DataDictionary.getFieldItem(pos_code_field);
			  if(item!=null){
				  itemid=item.getItemid();
			  }
		  }
		}
		List infoSetListPos=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
		StringBuffer sql=new StringBuffer();		
		try{
			String parentId = "";
			for(int k=0;k<infoSetListPos.size();k++)
			{
				FieldSet fieldset=(FieldSet)infoSetListPos.get(k);
				if("k01".equalsIgnoreCase(fieldset.getFieldsetid()))
				{
					String tempsql = "select parentid from organization where codeitemid='"+orgcodeitemid+"'";
					this.frecset=dao.search(tempsql);
					if(this.frecset.next()){
						parentId = this.frecset.getString("parentid");
					}
					RecordVo vo=new RecordVo(fieldset.getFieldsetid());
					vo.setString("e01a1", orgcodeitemid);
					vo.setString("e0122", parentId);
					try{
						vo=dao.findByPrimaryKey(vo);
					}catch(Exception e){
						vo.setDate("CreateTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
						vo.setDate("ModTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
						vo.setString("CreateUserName".toLowerCase(), this.userView.getUserName());
						vo.setString("ModUserName".toLowerCase(), null);
						dao.addValueObject(vo);
					}
					if(vo!=null)
					{
						vo.setString("e01a1", codeitemid);
						vo.setString("e0122", e0122);
						if("1".equals(corcode_unique)&&itemid.length()>0){
							vo.setString(itemid, "");
						}
						vo.setDate("CreateTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
						vo.setDate("ModTime".toLowerCase(), PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss"));
						vo.setString("CreateUserName".toLowerCase(), this.userView.getUserName());
						vo.setString("ModUserName".toLowerCase(), null);
						dao.addValueObject(vo);
					}
				}else
				{
					List infoFieldlist=DataDictionary.getFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
                    if(infoFieldlist==null)
                    	continue;
					StringBuffer fields=new StringBuffer();                   
					for(int i=0;i<infoFieldlist.size();i++)
                    {
                    	FieldItem fieldItem=(FieldItem)infoFieldlist.get(i);
                    	if(!"e01a1".equalsIgnoreCase(fieldItem.getItemid())&&!"i9999".equalsIgnoreCase(fieldItem.getItemid()))
                    	{
                    		fields.append(fieldItem.getItemid()+",");
                    		
                    	}
	                }
					fields.append("i9999");					
					sql.delete(0,sql.length());
					sql.append("insert into "+fieldset.getFieldsetid()+"("+fields.toString()+",e01a1)");
					sql.append("select  "+fields.toString()+",'"+codeitemid+"' from "+fieldset.getFieldsetid());
					sql.append(" where e01a1='"+orgcodeitemid+"'");
					//System.out.println(sql.toString());
					dao.insert(sql.toString(), new ArrayList());
					
					ArrayList list=new ArrayList();
					sql.delete(0,sql.length());
					sql.append("update "+fieldset.getFieldsetid()+" set ");
					sql.append("CreateTime="+PubFunc.DoFormatSystemDate(true));
					sql.append(",ModTime="+PubFunc.DoFormatSystemDate(true));
					sql.append(",CreateUserName='"+CreateUserName+"'");					
					sql.append(" where e01a1='"+codeitemid+"'");
					//System.out.println(sql.toString());
					dao.update(sql.toString());
				}
				
				
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		
	}
	private void copySetUint(ContentDAO dao,String orgcodeitemid,String codeitemid,String CreateUserName)throws GeneralException
	{
		String itemid="";
		String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
		RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
		if(unit_code_field_constant_vo!=null)
		{
		  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
		  if(unit_code_field!=null&&unit_code_field.length()>1){
			  FieldItem item = DataDictionary.getFieldItem(unit_code_field);
			  if(item!=null){
				  itemid=item.getItemid();
			  }
		  }
		}
		List infoSetListPos=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		StringBuffer sql=new StringBuffer();		
		try{
			for(int k=0;k<infoSetListPos.size();k++)
			{
				FieldSet fieldset=(FieldSet)infoSetListPos.get(k);
					List infoFieldlist=DataDictionary.getFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
                    if(infoFieldlist==null)
                    	continue;
					StringBuffer fields=new StringBuffer();                   
					for(int i=0;i<infoFieldlist.size();i++)
                    {
                    	FieldItem fieldItem=(FieldItem)infoFieldlist.get(i);
                    	if(!"b0110".equalsIgnoreCase(fieldItem.getItemid())&&!"i9999".equalsIgnoreCase(fieldItem.getItemid()))
                    	{
                    		if("b01".equalsIgnoreCase(fieldset.getFieldsetid())&&"1".equals(corcode_unique)&&itemid.equalsIgnoreCase(fieldItem.getItemid())){
                    			
                    		}else
                    			fields.append(fieldItem.getItemid()+",");
                    	}
	                }
					//fields.append("i9999,");
					sql.delete(0,sql.length());
					if(!"b01".equalsIgnoreCase(fieldset.getFieldsetid())){
						sql.append("insert into "+fieldset.getFieldsetid()+"("+fields.toString()+"i9999,b0110)");
						if(Sql_switcher.searchDbServer()==2)
							sql.append("select  "+fields.toString()+"nvl(i9999,1),'"+codeitemid+"' as b0110 from "+fieldset.getFieldsetid());
						else
							sql.append("select  "+fields.toString()+"isnull(i9999,1),'"+codeitemid+"' as b0110 from "+fieldset.getFieldsetid());
						sql.append(" where b0110='"+orgcodeitemid+"'");
					}else{
						sql.append("insert into "+fieldset.getFieldsetid()+"("+fields.toString()+"b0110)");
						sql.append("select  "+fields.toString()+"'"+codeitemid+"' as b0110 from "+fieldset.getFieldsetid());
						sql.append(" where b0110='"+orgcodeitemid+"'");
					}
					try{	
						//System.out.println(sql.toString());
						dao.insert(sql.toString(), new ArrayList());
					}catch(Exception e){}
					ArrayList list=new ArrayList();
					sql.delete(0,sql.length());
					sql.append("update "+fieldset.getFieldsetid()+" set ");
					sql.append("CreateTime="+PubFunc.DoFormatSystemDate(true));
					sql.append(",ModTime="+PubFunc.DoFormatSystemDate(true));
					sql.append(",CreateUserName='"+CreateUserName+"'");					
					sql.append(" where b0110='"+codeitemid+"'");
					//System.out.println(sql.toString());
					dao.update(sql.toString());
			}
		}catch(Exception e)
		{
			System.out.println(sql.toString());
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		
	}
	private String getMaxA0000(String descode,ContentDAO dao) throws GeneralException
	{
		RowSet rs = null;
		String a0000="1";
		try{
			   rs=dao.search("select max(a0000) as a0000 from organization where codeitemid like '" + descode + "%'");
			   if(rs.next())
			   {
				   a0000=String.valueOf(rs.getInt("a0000") + 1);
				   dao.update("update organization set a0000=a0000 + 1 where a0000>" + rs.getInt("a0000"));
			   }
			   else
				   dao.update("update organization set a0000=a0000 + 1 where a0000>0"); 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return a0000;
	}
	
	/**
	 * 同级机构排序获取levelA0000最大值   wangb    2019619
	 * @param descode
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxLevelA0000(String descode)throws GeneralException
	{
		String levelA0000 = "1";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    RowSet rs = null;
		try {
			rs=dao.search("SELECT MAX(LEVELA0000) as levelA0000 FROM organization where PARENTID='"+descode+"'");//多了from 30649 wangb 20170816
			if(rs.next())
				levelA0000 = String.valueOf(rs.getInt("levelA0000") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		return levelA0000;
	}
	
}
