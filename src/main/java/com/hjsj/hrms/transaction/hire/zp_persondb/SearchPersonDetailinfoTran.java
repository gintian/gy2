/*
 * Created on 2005-11-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPersonDetailinfoTran extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		RecordVo constantfield_vo=ConstantParamter.getRealConstantVo("ZP_FIELD_LIST");
		ArrayList zpfieldlist=new ArrayList();		
	    ArrayList list=new ArrayList();  
		if(constantfield_vo!=null){	
			String A0100=(String)this.getFormHM().get("a0100");
			if(A0100!=null && A0100.length()>=8){	 
				this.getFormHM().put("existusermessage","");
			}
			else
			{
				 this.getFormHM().put("existusermessage",ResourceFactory.getProperty("hire.zp_persondb.nosavemaininfo"));
			}
			String setname=(String)this.getFormHM().get("setname");     //主集、子集名
			String fieldsubStr="";
			String fieldStr=constantfield_vo.getString("str_value");
			cat.debug(fieldStr);
			if(fieldStr!=null && fieldStr.length()>0 && fieldStr.indexOf(setname + "{")!=-1){
			   fieldsubStr=fieldStr.substring(fieldStr.indexOf(setname + "{"));
			if(fieldsubStr!=null && fieldsubStr.length()>4)
			   fieldStr=fieldsubStr.substring(4,fieldsubStr.indexOf("}"));
			ArrayList infofieldlist=DataDictionary.getFieldList(setname,Constant.EMPLOY_FIELD_SET);
			List rs=null;
			String userbase=(String)this.getFormHM().get("userbase");   //人员库
			cat.debug("setname="+setname);	
			String tablename=userbase + setname;                        //操纵表的名称
			StringBuffer strsql=new StringBuffer();                     //保存sql的字符串
            //封装子集的数据
			strsql.append("select * from " + tablename);
			strsql.append(" where A0100='" + A0100 + "'");
			cat.debug("---zp strsql A0100------>" + strsql.toString());
		
		    try
			{
		    	  if(!infofieldlist.isEmpty())                         //字段s
				  {
				   	for(int i=0;i<infofieldlist.size();i++)
				   	{
				   		FieldItem fielditem=(FieldItem)infofieldlist.get(i);
				   		if(fieldStr.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
				   		{
				   			zpfieldlist.add(fielditem);
				   		}
				   	}
				  }
		    	rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());            //获取子集的纪录数据
	      	 for(int r=0;!rs.isEmpty() && r<rs.size();r++)
		     {
	   	 	     LazyDynaBean rec=(LazyDynaBean)rs.get(r);	
			     RecordVo vo=new RecordVo(tablename,1);
			     vo.setString("a0100",rec.get("a0100")!=null?rec.get("a0100").toString():"");
			     vo.setInt("i9999",Integer.parseInt(rec.get("i9999")!=null?rec.get("i9999").toString():""));
			     if(!infofieldlist.isEmpty())                         //字段s
			     {
			     	for(int i=0;i<infofieldlist.size();i++)
			     	{
			     		FieldItem fielditem=(FieldItem)infofieldlist.get(i);
			     		if(fieldStr.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
			     		{
				     		if(!"0".equals(fielditem.getCodesetid()))                 //是否是代码类型的
				     		{
				     			String codevalue=rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"";        //是,转换代码->数据描述	       //是,转换代码->数据描述	
				     			String codesetid=fielditem.getCodesetid();
				     			if(codevalue !=null && codevalue.trim().length()>0 && codesetid !=null && codesetid.trim().length()>0)
				     			{
				     				String value=AdminCode.getCode(codesetid,codevalue)!=null && AdminCode.getCode(codesetid,codevalue).getCodename()!=null?AdminCode.getCode(codesetid,codevalue).getCodename():"";
								    vo.setString(fielditem.getItemid(),value);
				     			}	
							    else
							    	vo.setString(fielditem.getItemid(),"");
				     		}else
				     		{
					     		if("D".equals(fielditem.getItemtype()))                               //日期类型的有待格式化处理
					     		{
					     			if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
					     				vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
		                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4)
		                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
		                            else if(rec.get(fielditem.getItemid())!=null && rec.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7)
		                            	vo.setString(fielditem.getItemid().toLowerCase(),new FormatValue().format(fielditem,rec.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
		                            else
		                            	vo.setString(fielditem.getItemid().toLowerCase(),"");
					     		}else if("N".equals(fielditem.getItemtype()))                        //数值类型的
					     		{
					     			vo.setString(fielditem.getItemid(),PubFunc.DoFormatDecimal(rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));
					     		}else                                                               //其他字符串类型
					     		{
					     			vo.setString(fielditem.getItemid(),rec.get(fielditem.getItemid())!=null?rec.get(fielditem.getItemid()).toString():"");
					     		}
				     		}
				     	
			     		}
	                }
			     }	
			     list.add(vo);
			  }
	      	
	      	 }catch(Exception sqle)
			 {
			   sqle.printStackTrace();
			   throw GeneralExceptionHandler.Handle(sqle);
			 }	
		 }		
	  }	
		  this.getFormHM().put("detailinfolist",list); //压回页面
		  this.getFormHM().put("zpfieldlist",zpfieldlist);
	}	
}
