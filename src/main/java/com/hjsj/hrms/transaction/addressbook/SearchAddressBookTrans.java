/*
 * Created on 2005-6-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchAddressBookTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stubx
		RecordVo constant_vo=ConstantParamter.getConstantVo("SS_QUERYTEMPLATE");
		List infoFieldList=userView.getPrivFieldList("A01");   //获得当前子集的所有属性
		//String A0100=(String)this.getFormHM().get("a0100");
		String A0100=userView.getUserId();
		A0100="00000001";
		List fieldlist=new ArrayList();
		String userbase =userView.getDbname();
		//System.out.println("userbase " + userbase);
		if(userbase ==null)
			userbase="Usr";
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from ");
		strsql.append("UsrA01");
		strsql.append(" where A0100='");
		strsql.append(A0100);
		strsql.append("'");
		try{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		this.frowset = dao.search(strsql.toString());
		boolean isExistData=this.frowset.next();
        if(!infoFieldList.isEmpty())
        {
        	for(int i=0;i<infoFieldList.size();i++)
        	{
        		FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
        	//	System.out.println("constant_vo.getString" + constant_vo.getString("str_value"));
        		if(constant_vo!=null && constant_vo.getString("str_value").toLowerCase().indexOf(fieldItem.getItemid())!=-1)
        		{
        			//System.out.println( fieldItem.getItemid() + "fff" + fieldItem.getItemid());
        			if(isExistData)
					{
						if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
						{
							if(!"0".equals(fieldItem.getCodesetid()))
							{
								String codevalue=this.getFrowset().getString(fieldItem.getItemid());
								//System.out.println("itemida" + fieldItem.getItemid() + "typea" + fieldItem.getItemtype());
								if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
									fieldItem.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),codevalue).getCodename());
							    else
							    	fieldItem.setViewvalue("");
							}
							else
							{
								//System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
								fieldItem.setViewvalue(this.getFrowset().getString(fieldItem.getItemid()));
							}
							fieldItem.setValue(this.getFrowset().getString(fieldItem.getItemid()));						
						}else if("D".equals(fieldItem.getItemtype()))                 //日期型有待格式化处理
						{
							if(this.getFrowset().getString(fieldItem.getItemid())!=null && this.getFrowset().getString(fieldItem.getItemid()).length()>10)
							{
								fieldItem.setViewvalue(this.getFrowset().getString(fieldItem.getItemid().toLowerCase()).substring(0,10));
								fieldItem.setValue(this.getFrowset().getString(fieldItem.getItemid().toLowerCase()).substring(0,10));
							}
							else
                            {
								fieldItem.setViewvalue("");
								fieldItem.setValue("");
                            }
						}
						else                                                          //数值类型的有待格式化处理
						{
							//fieldItemView.setFieldvalue(String.valueOf(this.getFrowset().getFloat(fieldItem.getItemid())));
							fieldItem.setValue(PubFunc.DoFormatDecimal(String.valueOf(this.getFrowset().getFloat(fieldItem.getItemid())),fieldItem.getDecimalwidth()));						
						}
					}
        			fieldlist.add(fieldItem);
        		}
        	}
        }
        this.getFormHM().put("fielditemlist",fieldlist);
        }catch(Exception e){
        	 e.printStackTrace();
   	      throw GeneralExceptionHandler.Handle(e);  	
        }
	}

}
