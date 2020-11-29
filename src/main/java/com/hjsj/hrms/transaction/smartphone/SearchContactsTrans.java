/**
 * 
 */
package com.hjsj.hrms.transaction.smartphone;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * @author cmq
 * Dec 24, 20103:15:31 PM
 */
public class SearchContactsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		try
		{
			StringBuffer buf=new StringBuffer();
			/**拼音简码*/
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);

			/**通讯录指标*/
			/*
			RecordVo vo=ConstantParamter.getRealConstantVo("SS_ADDRESSBOOK");
			String fields=vo.getString("str_value");
			*/
			/**人员库*/
			RecordVo vo=ConstantParamter.getRealConstantVo("SS_ADDRESS_BASE");
			String nbases="";
			if(vo==null)
				nbases="usr";
			else
			    nbases=vo.getString("str_value");
			if((nbases==null||nbases.length()==0))
			{
				nbases="usr";
			}
			String[] arr_base=StringUtils.split(nbases,',');
			//SS_EMAIL ,SS_MOBILE_PHONE
			vo=ConstantParamter.getRealConstantVo("SS_EMAIL");
			String email_field=vo.getString("str_value");	

				
			vo=ConstantParamter.getRealConstantVo("SS_MOBILE_PHONE");
			String phone_field=vo.getString("str_value");	
			if(!(phone_field==null|| "#".equalsIgnoreCase(phone_field)||phone_field.length()==0))
			{
				
			}		
			else
				throw new GeneralException(ResourceFactory.getProperty("error.sms.notdefine"));
		
			/**查询项*/
			String queryitem=(String)this.getFormHM().get("queryitem");
			StringBuffer wherebuf=new  StringBuffer();
			if(!(queryitem==null||queryitem.length()==0))
			{
				wherebuf.append(" where a0101 like '%");
				wherebuf.append(queryitem);
				wherebuf.append("%' ");
				if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) ))
				{
					wherebuf.append(" or ");
					wherebuf.append(pinyin_field );
					wherebuf.append(" like '");
					wherebuf.append(queryitem);
					wherebuf.append("%' ");
				}				
			}			
			ArrayList sqllist=new ArrayList();
			StringBuffer cols=new StringBuffer();
			cols.append("nbase,a0100,b0110,e0122,e01a1,a0101,phone,");
			cols.append(email_field);
			for(int i=0;i<arr_base.length;i++)
			{
				buf.setLength(0);
				buf.append("select '");
				buf.append(arr_base[i]);
				buf.append("' nbase,a0100,b0110,e0122,e01a1,a0101,");
				buf.append(phone_field);
				buf.append(" as phone ");
				if(!(email_field==null|| "#".equalsIgnoreCase(email_field)||email_field.length()==0))
				{
					buf.append(",");
					buf.append(email_field);
				}
				buf.append(" from ");
				buf.append(arr_base[i]);
				buf.append("a01 ");
				if(wherebuf.length()!=0)
				{
					buf.append(wherebuf.toString());
				}
				buf.append(" union all ");
			}
			buf.setLength(buf.length()-11);

			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select count(a0100) count from ("+buf.toString()+") ttt");
			int allcount = 0;
			if(this.frowset.next())
				allcount = this.frowset.getInt("count");
            
			this.getFormHM().put("sql",buf.toString());
            this.getFormHM().put("columns",cols.toString());
            this.getFormHM().put("strwhere","");  
            this.getFormHM().put("allcount",""+allcount); 
            
		}
		catch(Exception ex)
		{
        	ex.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(ex);       
		}

	}

}
