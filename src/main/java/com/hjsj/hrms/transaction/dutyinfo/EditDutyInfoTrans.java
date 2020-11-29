package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class EditDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String action=(String)this.getFormHM().get("edittype");
		String code=(String)this.getFormHM().get("code");
		String I9999=(String)this.getFormHM().get("i9999");
		List fieldlist=(List)this.getFormHM().get("infofieldlist");	         //获得fieldList
		String setname=(String)this.getFormHM().get("setname");
		String contentField = (String)this.getFormHM().get("contentField");
		String value=(String)this.getFormHM().get("contentFieldValue");
		String edit_flag=(String)this.getFormHM().get("edit_flag");//从信息维护中新建或是修改
		if(edit_flag!=null&& "new".equals(edit_flag))
			return;
		/*if(setname!=null && setname.length()>=3 && "01".equals(setname.substring(1,3)))
				action="new";*/
		String kind=(String)this.getFormHM().get("kind");
		if(code==null||code.length()<=0&&kind==null||kind.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","请从左侧选择机构！","",""));
		StringBuffer fields=new StringBuffer();
		StringBuffer fieldvalues=new StringBuffer();
		String[] fieldsname=new String[fieldlist.size()];
		String[] fieldcode=new String[fieldlist.size()];
		String  ps_superior="";
		RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.getFrameconn());
		if(ps_superior_vo!=null)
		{
		  ps_superior=ps_superior_vo.getString("str_value");
		  this.getFormHM().put("ps_superior",ps_superior);
		}
		RecordVo vo=new RecordVo(setname.toLowerCase());
		String corcode="";
		String codeitemdesc="";
		String codeitemid="";
		int corcodeindex=0;
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fieldItem=(FieldItem)fieldlist.get(i);
			if("codeitemid".equalsIgnoreCase(fieldItem.getItemid()))
			{
				codeitemid=fieldItem.getValue();
				continue;
			}
			if("corcode".equalsIgnoreCase(fieldItem.getItemid()))
			{
				corcode=fieldItem.getValue();
				corcodeindex=i;
				continue;
			}else if("codeitemdesc".equalsIgnoreCase(fieldItem.getItemid()))
			{
				codeitemdesc=fieldItem.getValue();
				continue;
			}
			if(fieldItem.getItemid()!=null && fieldItem.getItemid().length()>0 && !"e01a1".equalsIgnoreCase(fieldItem.getItemid()) && !"b0110".equalsIgnoreCase(fieldItem.getItemid()))
			{  
				if(ps_superior.equalsIgnoreCase(fieldItem.getItemid()))
				{
					//直接上级设置成自己时提示
					if(code.equalsIgnoreCase(fieldItem.getValue()))
					  throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("pos.posparameter.directnessnoself"),"",""));
					
					//直接上级不能设置成自己的下级，不能是循环
					if(!checkposup(code,this.frameconn,fieldItem.getValue(),ps_superior,1))
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("pos.posparameter.directnessnodown"),"",""));
				}
				
				
				
				fields.append(fieldItem.getItemid());
			    fieldsname[i]=fieldItem.getItemid();			    
			    
				if("D".equals(fieldItem.getItemtype()) && (fieldItem.getValue() != null&&fieldItem.getValue().length()>0))
				{
					// ;
					  fieldvalues.append(PubFunc.DateStringChange(fieldItem.getValue()));
					  vo.setDate(fieldItem.getItemid().toLowerCase(), PubFunc.DateStringChangeValue(fieldItem.getValue()));
				      fieldcode[i]=PubFunc.DateStringChange(fieldItem.getValue());				 		  
				}else if("M".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
						vo.setString(fieldItem.getItemid().toLowerCase(), null);
					}
					else
					{
						String content=fieldItem.getValue();
						/*content=content.replaceAll("&sup1;", "1");
						content=content.replaceAll("&sup2;", "2");
						content=content.replaceAll("&sup3;","3");
						content=content.replaceAll("&ordm;","o");
						content=content.replaceAll("&acirc;","a");
						content=content.replaceAll("&eth;","d");
						content=content.replaceAll("&yacute;","y");
						content=content.replaceAll("&thorn;","t");	
						content=content.replaceAll("&ETH;","D");
						content=content.replaceAll("&THORN;","T");
						content=content.replaceAll("&Yacute;","Y");*/
						content=PubFunc.getStr(content);
						fieldcode[i]="'" + content + "'";
						fieldvalues.append("'" + content + "'");
						vo.setString(fieldItem.getItemid().toLowerCase(), content);
					}
				}else if("N".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
						//vo.setString(fieldItem.getItemid(), null);
					}
					else
					{
						fieldcode[i]=fieldItem.getValue();
						fieldvalues.append(fieldItem.getValue());
						vo.setString(fieldItem.getItemid().toLowerCase(), fieldItem.getValue());
						
					}
				}
				else
				{
				    if(fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					   {
					    	fieldcode[i]="null";
					    	fieldvalues.append("null");
					    	vo.setString(fieldItem.getItemid().toLowerCase(), null);
					 }/*else if(fieldItem.getViewvalue() == null || fieldItem.getViewvalue().equals("null") || fieldItem.getViewvalue().equals(""))
					 {
						 fieldcode[i]="null";
					     fieldvalues.append("null");
					 }*/
					 else
					 {
						    String content=PubFunc.getStr(fieldItem.getValue());
					        fieldcode[i]="'" + PubFunc.splitString(content,fieldItem.getItemlength()) + "'";
							fieldvalues.append("'" + PubFunc.splitString(content,fieldItem.getItemlength()) + "'");
							vo.setString(fieldItem.getItemid().toLowerCase(), PubFunc.splitString(content,fieldItem.getItemlength()));
					 }
					}
				fields.append(",");
				fieldvalues.append(",");
			}
		}
		boolean flag=false;
		String id;
		String type="4";
		if(setname!=null&&"k01".equalsIgnoreCase(setname))  //添加岗位属性		 
		{
			corcode=corcode==null?"":corcode;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = (String)this.getFormHM().get("backdate");
			backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new java.util.Date());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
			if("1".equals(corcode_unique)&&corcode.length()>0){
				try {
					this.frowset=dao.search("select codeitemid,codeitemdesc from (select codeitemid,codeitemdesc from organization where corcode='"+corcode+"' and codeitemid<>'"+code+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date union all select codeitemid,codeitemdesc from vorganization where corcode='"+corcode+"' and codeitemid<>'"+code+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date) tt");
					if(this.frowset.next()){
						throw GeneralExceptionHandler.Handle(new GeneralException("","岗位代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}
			eidtOrgData(code,codeitemdesc,corcode);
			RecordVo pos_vo= ConstantParamter.getRealConstantVo("POS_CODE_FIELD");//岗位代码
			if(pos_vo!=null)
			{
			    	String pos_code_field=pos_vo.getString("str_value");
			    	if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field))
			    	{
			    		vo.setString(pos_code_field, corcode);
			    		FieldItem item = com.hrms.hjsj.sys.DataDictionary.getFieldItem(pos_code_field);
			    		fieldcode[corcodeindex]="'" + PubFunc.splitString(corcode,item.getItemlength()) + "'";
			    		fieldsname[corcodeindex]=pos_code_field;
			    	}
			}
		}
		if(!"null".equalsIgnoreCase(code)&&code!=null&& !"".equals(code))
		{
		    	if("new".equals(action))
		    	{
			       //id=new StructureExecSqlString().InfoInsert(type,setname,fields.toString(),fieldvalues.toString(),code,userView.getUserName(),this.getFrameconn());
		    		id=new StructureExecSqlString().InfoInsertVo(type,setname,vo,code,userView.getUserName(),this.getFrameconn());
		    		if(setname.length()==3 && "01".equals(setname.substring(1,3)) || setname.length()==6 && "01".equals(setname.substring(4,6)))
					{ 
					}
			        else
			        {
			    	   new StructureExecSqlString().insertZJRecord(setname, code, this.userView, this.getFrameconn());
			        }
		    	}
			    else
				   flag=new StructureExecSqlString().InfoUpdate(type,setname,fieldsname,fieldcode,code,I9999,userView.getUserName(),this.getFrameconn());
		}	
		this.getFormHM().put("infofieldlist",fieldlist);
	}
	
	
	/**
	 * 检查直接上级是否是自己的下级
	 * @param e01a1 本岗位编码
	 * @param conn
	 * @param checkCode 设置的上级岗位编码
	 * @param upitemid  直接上级关联指标id
	 * @param safeLock 安全锁，防止陷入循环，当层级达到10的时候强制跳出
	 * @return true 检查通过    |  false 检查不通过，说明要设置的上级岗位是自己的下级
	 */
	private boolean checkposup(String e01a1,Connection conn,String checkCode,String upitemid,int safeLock){
		boolean checkflag = true;
		String sql = "select e01a1 from k01 where "+upitemid+" = '"+e01a1+"'";
		
		List rl = ExecuteSQL.executeMyQuery(sql, conn);
		
		for(int i=0;i<rl.size();i++){
			LazyDynaBean ldb = (LazyDynaBean)rl.get(i);
			
			//如果有下级与设置的上级相同，跳出循环返回false
			if(checkCode.equalsIgnoreCase(ldb.get("e01a1").toString())){
				checkflag = false;
				break;
			}
			
			//一般岗位汇报关系不会超过10级，防止陷入无限循环，10级深度强制跳出
			if(safeLock==10)
				break;
			
			//递归查询下级，如果又返回false说明检查不通过，直接跳出并返回false
			if(!checkposup(ldb.get("e01a1").toString(),conn,checkCode,upitemid,++safeLock)){
				checkflag = false;
				break;
			}
				
		}
		
		return checkflag;
	}
	
	
	private boolean eidtOrgData(String codeitemid,String codeitemdesc,String corcode)throws GeneralException
	{
		 OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
		 ContentDAO dao=new ContentDAO(this.getFrameconn());		 
		 AddOrgInfo addOrgInfo=new AddOrgInfo();
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
		 RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
		 boolean isCorrect=true;
		 StringBuffer sqlstr=new StringBuffer();
		 sqlstr.append("update organization set ");
		 sqlstr.append("codeitemdesc='"+PubFunc.splitString(codeitemdesc,codeitemdesclen)+"',corcode='"+corcode+"'");
		 sqlstr.append(" where codeitemid='"+codeitemid+"'");
			
		
		 try
		 {
			 dao.update(sqlstr.toString());
			 sqlstr.delete(0, sqlstr.length());
			 sqlstr.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date from ");
			 sqlstr.append(" organization where codeitemid='"+codeitemid+"'");
			 this.frowset=dao.search(sqlstr.toString());
			 if(this.frowset.next())
			 {
				 CodeItem item=new CodeItem();
				 item.setCodeid(codesetid);
				 item.setCodeitem(codeitemid.toUpperCase());
				 item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
				 item.setPcodeitem(this.frowset.getString("parentid").toUpperCase());				 
				 item.setCcodeitem(this.frowset.getString("childid").toUpperCase());
				 item.setCodelevel(this.frowset.getString("grade"));
				 AdminCode.addCodeItem(item);
				 AdminCode.updateCodeItemDesc(codesetid, codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
			 }
		 }catch(Exception e)
		 {
			isCorrect=false; 
            e.printStackTrace();
		 }
		 return isCorrect;
	}
}
