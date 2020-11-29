/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveSelfInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	
	private int fieldNum; 
	
	public void execute() throws GeneralException{
		try{
			DbNameBo db=new DbNameBo(this.getFrameconn());
			HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
			String A0100=(String)reqhm.get("a0100");
			String I9999=(String)reqhm.get("pi9999");
			String part_unit="";
			String part_setid="";
			ArrayList fieldlist=(ArrayList)this.getFormHM().get("infofieldlist");	         //获得fieldList
			String setname=(String)this.getFormHM().get("emp_e");
			String userbase=(String)reqhm.get("dbpre");
			String tablename=userbase + setname;	
			StringBuffer fields=new StringBuffer();
			StringBuffer fieldvalues=new StringBuffer();		
			String[] fieldsname=new String[fieldlist.size()];
			String[] fieldcode=new String[fieldlist.size()];
			String UN_code = "";
			String UM_code = "";
			String value="";

			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				fields.append(fieldItem.getItemid());
				fieldsname[i]=fieldItem.getItemid();
				//fieldcode[i]=fieldItem.getValue();
				fieldItem.setValue(PubFunc.getStr(fieldItem.getValue()));

				if("a0101".equalsIgnoreCase(fieldItem.getItemid()))
				{
					this.fieldNum = i;
				}			
				if("D".equals(fieldItem.getItemtype()))
				{
					fieldvalues.append(PubFunc.DateStringChange(fieldItem.getValue()));
					fieldcode[i]=PubFunc.DateStringChange(fieldItem.getValue());				 		  
				}else if("M".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						fieldcode[i]="'" + fieldItem.getValue() + "'";					
						fieldvalues.append("'" + fieldItem.getValue() + "'");
					}
				}else if("N".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						fieldcode[i]=fieldItem.getValue();
						fieldvalues.append(fieldItem.getValue());
					}
				}
				else
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						if(fieldItem.isCode())
						{
							if(part_unit!=null&&part_unit.equalsIgnoreCase(fieldItem.getItemid().toString())&&part_setid!=null&&part_setid.equalsIgnoreCase(setname))
							{
								value=AdminCode.getCodeName("UN", fieldItem.getValue());
								if(value==null||value.length()<=0)
									value=AdminCode.getCodeName("UM", fieldItem.getValue());
								if(value==null||value.length()<=0)
								{
									fieldItem.setValue("");
								}
							}else
							{
								value=AdminCode.getCodeName(fieldItem.getCodesetid(), fieldItem.getValue());
								if(value==null||value.length()<=0)
								{
									fieldItem.setValue("");
								}
							}

						}
						fieldcode[i]="'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'";
						if("b0110".equalsIgnoreCase(fieldItem.getItemid())|| "e0122".equalsIgnoreCase(fieldItem.getItemid())){
							
							if("b0110".equalsIgnoreCase(fieldItem.getItemid())){
								UN_code=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
							}else{
								UM_code=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
							}
						}
						if("e01a1".equalsIgnoreCase(fieldItem.getItemid())){
						}
						fieldvalues.append("'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'");
					}
				}			
				fields.append(",");
				fieldvalues.append(",");
			}



			boolean flag=false;
			StructureExecSqlString structureExecSqlString=new StructureExecSqlString();
			structureExecSqlString.setFieldcode(fieldcode);
			String checksave="01";

{


				RecordVo vo_old = null;
				{
					vo_old = db.getRecordVoA01(tablename,A0100,Integer.parseInt(I9999));
				}				
				
				flag=new StructureExecSqlString().InfoUpdate("1",tablename,fieldsname,fieldcode,A0100,I9999,userView.getUserName(),this.getFrameconn());
				db.updateMainSetA0100(tablename,UN_code,UM_code,vo_old,A0100);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);

		}
	}
	
	public synchronized String getUserId(String tableName) throws GeneralException{
		/*String strsql = "select max(A0100) as a0100 from " + tableName + " order by A0100";
		List rs=ExecuteSQL.executeMyQuery(strsql,this.getFrameconn());
		int userPlace;
		String userNumber;
		if (!rs.isEmpty()) {
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			if(rec.get("a0100")!=null && rec.get("a0100").toString().trim().length()>0)
			   userPlace =Integer.parseInt(rec.get("a0100").toString()) + 1;
			else
			   userPlace=1;
		} else
			userPlace = 1;
	//	System.out.println("userNumber" + userPlace);
		userNumber = Integer.toString(userPlace);
		for (int i = 0; i < 8 - (Integer.toString(userPlace)).length(); i++)
			userNumber = "0" + userNumber;
		cat.debug("userNumber ->" + userNumber);*/
		return DbNameBo.insertMainSetA0100(tableName,this.getFrameconn());
	}
	
	public void updatePinYinField(String tablename,String A0100,String pinyin_field,String[] fieldcode)
	{
		if(tablename==null||tablename.indexOf("A01")!=3)
			return;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		PubFunc pf = new PubFunc();	
		String value = fieldcode[this.fieldNum];
		String pinyin = pf.getPinym(value);
		StringBuffer sb = new StringBuffer();		
		sb.append(" update "+tablename);
		sb.append(" set "+pinyin_field+" = "+pinyin+"");
		sb.append(" where a0100 ='"+A0100+"'");
		try
		{
//			System.out.println(sb.toString());
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}
