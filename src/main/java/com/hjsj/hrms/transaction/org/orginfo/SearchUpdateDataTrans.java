/*
 * Created on 2006-1-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchUpdateDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String codeitemdesc=(String)hm.get("codeitemdesc");
		codeitemdesc = com.hrms.frame.codec.SafeCode.decode(codeitemdesc);
		this.getFormHM().put("codeitemdesc", codeitemdesc);
		String isorg = (String)hm.get("isorg");
		this.getFormHM().put("isorg",isorg);
		StringBuffer strsql=new StringBuffer();
		strsql.append("select (");
		strsql.append(Sql_switcher.length("codeitemid"));
		strsql.append("-");
		strsql.append(Sql_switcher.length("parentid"));
		strsql.append(") as len,corcode,start_date,end_date from "+isorg+"anization where codeitemid='");
		strsql.append(codeitemid);
		strsql.append("'");
		String corcode="";
		Date start_date =null;
		Date end_date = null;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(strsql.toString());
		    if(this.frowset.next())
			{
				int len=this.frowset.getInt("len");
				corcode=this.frowset.getString("corcode");
				corcode=corcode!=null?corcode:"";
				corcode=!"null".equals(corcode)?corcode:"";
				start_date = this.frowset.getDate("start_date");
				end_date = this.frowset.getDate("end_date");
				if(len==0)
					this.getFormHM().put("len","30");
				else
					this.getFormHM().put("len",String.valueOf(len));
			}
		    
		    String posfillable="0",unitfillable="0";
			RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
			if(pos_code_field_constant_vo!=null)
			{
			  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
			  if(pos_code_field!=null&&pos_code_field.length()>1){
				  FieldItem item = DataDictionary.getFieldItem(pos_code_field);
				  if(item!=null){
					  if(item.isFillable()){
						  posfillable="1";
					  }
				  }
			  }
			}
			RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
			if(unit_code_field_constant_vo!=null)
			{
			  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
			  if(unit_code_field!=null&&unit_code_field.length()>1){
				  FieldItem item = DataDictionary.getFieldItem(unit_code_field);
				  if(item!=null){
					  if(item.isFillable()){
						  unitfillable="1";
					  }
				  }
			  }
			}
			this.getFormHM().put("posfillable", posfillable);
			this.getFormHM().put("unitfillable", unitfillable);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("corcode", corcode);
			this.getFormHM().put("start_date", start_date!=null?com.hjsj.hrms.utils.PubFunc.FormatDate(start_date,"yyyy-MM-dd"):"");
			this.getFormHM().put("end_date", end_date!=null?com.hjsj.hrms.utils.PubFunc.FormatDate(end_date,"yyyy-MM-dd"):"");
		}
	}

}
