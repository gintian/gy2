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
import com.hjsj.hrms.valueobject.common.FieldItemView;
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
public class UpdatePersonenrollinfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo constantuser_vo=null;
		RecordVo constantfield_vo=null;
		try{
			constantuser_vo=ConstantParamter.getRealConstantVo("SS_LOGIN_USER_PWD");
			constantfield_vo=ConstantParamter.getRealConstantVo("ZP_FIELD_LIST");
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		String fieldsubStr="";
		if(constantuser_vo !=null && constantfield_vo !=null)
		{
			String fieldStr=constantfield_vo.getString("str_value");
			String usernamefield=constantuser_vo.getString("str_value");
			
			if(usernamefield !=null && usernamefield.indexOf(",")>0)
				usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
		    else
			    usernamefield="username";
			if(fieldStr!=null && fieldStr.length()>0){
				String userbase=(String)this.getFormHM().get("userbase");
				String A0100=(String)this.getFormHM().get("a0100");		
				String I9999=(String)this.getFormHM().get("i9999");
				String setname=(String)this.getFormHM().get("setname");
				ArrayList zpfieldlist=new ArrayList();
				if(fieldStr!=null && fieldStr.indexOf(setname + "{")!=-1)
				   fieldsubStr=fieldStr.substring(fieldStr.indexOf(setname + "{"));
				if(fieldsubStr!=null && fieldsubStr.length()>4)
				   fieldStr=fieldsubStr.substring(4,fieldsubStr.indexOf("}"));
				ArrayList infofieldlist=DataDictionary.getFieldList(setname,Constant.EMPLOY_FIELD_SET);
				String actiontype=(String)this.getFormHM().get("actiontype");	
				if(A0100!=null && A0100.length()>0 && "A01".equalsIgnoreCase(setname))
					actiontype="update";
			    List rs=null;
				try
				{
					if(!infofieldlist.isEmpty())
			    	{int n=0;
					 for(int i=0;i<infofieldlist.size();i++)
			    	 {
					 	FieldItem fielditem=(FieldItem)infofieldlist.get(i);
					 	 if(fieldStr.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
				    	 {
					 	 	n++;
					 	 }
					 }
					boolean isExistData=false;
					if("update".equals(actiontype))                      //若是修改取其值
					{
						StringBuffer strsql=new StringBuffer();
						if(!"A01".substring(1,3).equals(setname.substring(1,3)))   //如果子集的修改则条件有I9999
						{
							strsql.append("select * from ");
							strsql.append(userbase + setname);
							strsql.append(" where ");
							strsql.append("a0100");
							strsql.append("='");
							strsql.append(A0100);
							strsql.append("'");
						    strsql.append(" and I9999=");
							strsql.append(I9999);
						}
						else
						{
							strsql.append("select * from ");
							strsql.append(userbase + setname);
							strsql.append(" where A0100='");
							strsql.append(A0100);
							strsql.append("'");
						}
						rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
						isExistData=!rs.isEmpty();					
				    }
					if("A01".substring(1,3).equals(setname.substring(1,3)) && "new".equalsIgnoreCase(actiontype)){
					FieldItem item=DataDictionary.getFieldItem(usernamefield);
					if(item!=null)
					{
						if(fieldStr.indexOf(usernamefield)!=-1)
							n=n+2;
						else
							n=n+3;
						FieldItemView fieldItemView=new FieldItemView();
						fieldItemView.setAuditingFormula(item.getAuditingFormula());
						fieldItemView.setAuditingInformation(item.getAuditingInformation());
						fieldItemView.setCodesetid(item.getCodesetid());
						fieldItemView.setDecimalwidth(item.getDecimalwidth());
						fieldItemView.setDisplayid(item.getDisplayid());
						fieldItemView.setDisplaywidth(item.getDisplaywidth());
						fieldItemView.setExplain(item.getExplain());
						fieldItemView.setFieldsetid(item.getFieldsetid());
						fieldItemView.setItemdesc(item.getItemdesc());
						fieldItemView.setItemid(item.getItemid());
						fieldItemView.setItemlength(item.getItemlength());
						fieldItemView.setItemtype(item.getItemtype());
						fieldItemView.setModuleflag(item.getModuleflag());
						fieldItemView.setState(item.getState());
						fieldItemView.setUseflag(item.getUseflag());
						fieldItemView.setPriv_status(item.getPriv_status());
						fieldItemView.setRowflag(String.valueOf(n-1));
					 	if("update".equals(actiontype))
						{
						  if(isExistData)
						  {
						  	 LazyDynaBean recdata=(LazyDynaBean)rs.get(0);
							 if("A".equals(item.getItemtype()) || "M".equals(item.getItemtype()))
							 {
								if(!"0".equals(item.getCodesetid()))
								{
									String codevalue=recdata.get(item.getItemid())!=null?recdata.get(item.getItemid()).toString():"";
									if(codevalue !=null && codevalue.trim().length()>0 && item.getCodesetid()!=null && item.getCodesetid().trim().length()>0)
										fieldItemView.setViewvalue(AdminCode.getCode(item.getCodesetid(),codevalue)!=null?AdminCode.getCode(item.getCodesetid(),codevalue).getCodename():"");
									else
										fieldItemView.setViewvalue("");
								}
								else
								{
									fieldItemView.setViewvalue(recdata.get(item.getItemid())!=null?recdata.get(item.getItemid()).toString():"");
								}
								fieldItemView.setValue(recdata.get(item.getItemid())!=null?recdata.get(item.getItemid()).toString():"");						
							}else if("D".equals(item.getItemtype()))                 //日期型有待格式化处理
							{
								if(recdata.get(item.getItemid())!=null && recdata.get(item.getItemid()).toString().length()>=10 && item.getItemlength()==10)
								{
									fieldItemView.setViewvalue(new FormatValue().format(item,recdata.get(item.getItemid().toLowerCase()).toString().substring(0,10)));
									fieldItemView.setValue(new FormatValue().format(item,recdata.get(item.getItemid().toLowerCase()).toString().substring(0,10)));
								}else if(recdata.get(item.getItemid())!=null && recdata.get(item.getItemid()).toString().length()>=10 && item.getItemlength()==4)
								{
									fieldItemView.setViewvalue(new FormatValue().format(item,recdata.get(item.getItemid().toLowerCase()).toString().substring(0,4)));
									fieldItemView.setValue(new FormatValue().format(item,recdata.get(item.getItemid().toLowerCase()).toString().substring(0,4)));
								}else if(recdata.get(item.getItemid())!=null && recdata.get(item.getItemid()).toString().length()>=10 && item.getItemlength()==7)
								{
									fieldItemView.setViewvalue(new FormatValue().format(item,recdata.get(item.getItemid().toLowerCase()).toString().substring(0,7)));
									fieldItemView.setValue(new FormatValue().format(item,recdata.get(item.getItemid().toLowerCase()).toString().substring(0,7)));
								}
								else
			                    {
									fieldItemView.setViewvalue("");
									fieldItemView.setValue("");
			                    }
							}
							else                                                          //数值类型的有待格式化处理
							{
								fieldItemView.setValue(PubFunc.DoFormatDecimal(recdata.get(item.getItemid())!=null?recdata.get(item.getItemid()).toString():"",item.getDecimalwidth()));						
							}
						  }
					 }
					 	zpfieldlist.add(fieldItemView);
					 	FieldItemView fieldItemViewp=new FieldItemView();
						fieldItemViewp.setCodesetid("0");
						fieldItemViewp.setFieldsetid("A01");
						fieldItemViewp.setItemdesc(ResourceFactory.getProperty("hire.zp_persondb.password"));
						fieldItemViewp.setItemid("userpassword");
						fieldItemViewp.setItemlength(8);
						fieldItemViewp.setItemtype("A");	
						fieldItemViewp.setRowflag(String.valueOf(n-1));
						fieldItemViewp.setValue("");
						zpfieldlist.add(fieldItemViewp);
						FieldItemView fieldItemViewpp=new FieldItemView();
						fieldItemViewpp.setCodesetid("0");
						fieldItemViewpp.setFieldsetid("A01");
						fieldItemViewpp.setItemdesc(ResourceFactory.getProperty("hire.zp_persondb.okpassword"));
						fieldItemViewpp.setItemid("okuserpassword");
						fieldItemViewpp.setItemlength(8);
						fieldItemViewpp.setItemtype("A");	
						fieldItemViewpp.setRowflag(String.valueOf(n-1));
						fieldItemViewpp.setValue("");
						zpfieldlist.add(fieldItemViewpp);
					}
					else
					{
						if(fieldStr.indexOf(usernamefield)!=-1)
							n=n+2;
						else
							n=n+3;
						FieldItemView fieldItemView=new FieldItemView();
						fieldItemView.setCodesetid("0");
						fieldItemView.setFieldsetid("A01");
						fieldItemView.setItemdesc(ResourceFactory.getProperty("hire.zp_persondb.username"));
						fieldItemView.setItemid(usernamefield);
						fieldItemView.setItemlength(50);
						fieldItemView.setItemtype("A");	
						fieldItemView.setRowflag(String.valueOf(n-1));
						fieldItemView.setValue("");
						zpfieldlist.add(fieldItemView);
						FieldItemView fieldItemViewp=new FieldItemView();
						fieldItemViewp.setCodesetid("0");
						fieldItemViewp.setFieldsetid("A01");
						fieldItemViewp.setItemdesc(ResourceFactory.getProperty("hire.zp_persondb.password"));
						fieldItemViewp.setItemid("userpassword");
						fieldItemViewp.setItemlength(8);
						fieldItemViewp.setItemtype("A");	
						fieldItemViewp.setRowflag(String.valueOf(n-1));
						fieldItemViewp.setValue("");
						zpfieldlist.add(fieldItemViewp);
						FieldItemView fieldItemViewpp=new FieldItemView();
						fieldItemViewpp.setCodesetid("0");
						fieldItemViewpp.setFieldsetid("A01");
						fieldItemViewpp.setItemdesc(ResourceFactory.getProperty("hire.zp_persondb.okpassword"));
						fieldItemViewpp.setItemid("okuserpassword");
						fieldItemViewpp.setItemlength(8);
						fieldItemViewpp.setItemtype("A");	
						fieldItemViewpp.setRowflag(String.valueOf(n-1));
						fieldItemViewpp.setValue("");
						zpfieldlist.add(fieldItemViewpp);
						
					}
					}
					
			
			    	   for(int i=0;i<infofieldlist.size();i++)
			    	   {
			    	   	 FieldItem fielditem=(FieldItem)infofieldlist.get(i);
			    	    if(fieldStr.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
			    	    {
			    	    	FieldItemView fieldItemView=new FieldItemView();
							fieldItemView.setAuditingFormula(fielditem.getAuditingFormula());
							fieldItemView.setAuditingInformation(fielditem.getAuditingInformation());
							fieldItemView.setCodesetid(fielditem.getCodesetid());
							fieldItemView.setDecimalwidth(fielditem.getDecimalwidth());
							fieldItemView.setDisplayid(fielditem.getDisplayid());
							fieldItemView.setDisplaywidth(fielditem.getDisplaywidth());
							fieldItemView.setExplain(fielditem.getExplain());
							fieldItemView.setFieldsetid(fielditem.getFieldsetid());
							fieldItemView.setItemdesc(fielditem.getItemdesc());
							fieldItemView.setItemid(fielditem.getItemid());
							fieldItemView.setItemlength(fielditem.getItemlength());
							fieldItemView.setItemtype(fielditem.getItemtype());
							fieldItemView.setModuleflag(fielditem.getModuleflag());
							fieldItemView.setState(fielditem.getState());
							fieldItemView.setUseflag(fielditem.getUseflag());
							fieldItemView.setPriv_status(fielditem.getPriv_status());
				            //在struts用来表示换行的变量
							fieldItemView.setRowflag(String.valueOf(n-1));
			    	 		//为了在选择代码时方便而压入权限码开始
							//为了在选择代码时方便而压入权限码结束
						   	if("update".equals(actiontype))
							{
							  if(isExistData)
							  {
							  	 LazyDynaBean recdata=(LazyDynaBean)rs.get(0);
								 if("A".equals(fielditem.getItemtype()) || "M".equals(fielditem.getItemtype()))
								 {
									if(!"0".equals(fielditem.getCodesetid()))
									{
										String codevalue=recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"";
										//System.out.println("itemida" + fieldItem.getItemid() + "typea" + fieldItem.getItemtype());
										if(codevalue !=null && codevalue.trim().length()>0 && fielditem.getCodesetid()!=null && fielditem.getCodesetid().trim().length()>0)
											fieldItemView.setViewvalue(AdminCode.getCode(fielditem.getCodesetid(),codevalue)!=null?AdminCode.getCode(fielditem.getCodesetid(),codevalue).getCodename():"");
										else
											fieldItemView.setViewvalue("");
									}
									else
									{
										//System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
										fieldItemView.setViewvalue(recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"");
									}
									fieldItemView.setValue(recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"");						
								}else if("D".equals(fielditem.getItemtype()))                 //日期型有待格式化处理
								{
									if(recdata.get(fielditem.getItemid())!=null && recdata.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
										fieldItemView.setValue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
									}else if(recdata.get(fielditem.getItemid())!=null && recdata.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
										fieldItemView.setValue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
									}else if(recdata.get(fielditem.getItemid())!=null && recdata.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
										fieldItemView.setValue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
									}
									else
				                    {
										fieldItemView.setViewvalue("");
										fieldItemView.setValue("");
				                    }
								}
								else                                                          //数值类型的有待格式化处理
								{
									fieldItemView.setValue(PubFunc.DoFormatDecimal(recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));						
								}
							  }
						 }
						 else
					     {
						 	fieldItemView.setValue("");
						 }
						   	zpfieldlist.add(fieldItemView);
				        }
			    	   }
				    }
				}catch(Exception e){
				   e.printStackTrace();
				   throw GeneralExceptionHandler.Handle(e);
				}finally{		
				   this.getFormHM().put("a0100",A0100);
				   this.getFormHM().put("i9999",I9999);
				   this.getFormHM().put("existusermessage","");
			       this.getFormHM().put("zpfieldlist",zpfieldlist);            //压回页面
			   }
			}
		}
	}

}
