/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
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
public class EditDutyDetailInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
	    String setname=(String)this.getFormHM().get("setname");
	    String edittype=(String)this.getFormHM().get("edittype");
	   // System.out.println("edittype " + edittype);
		String code=(String)this.getFormHM().get("code");
		//String kind=(String)this.getFormHM().get("kind");
		String I9999=(String)this.getFormHM().get("i9999");
		List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
	//	List infoSetList=userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);   //获得所有权限的子集
		List infoFieldViewList=new ArrayList();                  //保存处理后的属性
		String filename="";
		try
		{
			if(!infoFieldList.isEmpty())
		    {
				boolean isExistData=false;
				StringBuffer strsql=new StringBuffer();
				if("update".equals(edittype))
				{
					strsql.append("select * from ");
					strsql.append(setname);
					strsql.append(" where E01A1='");
					strsql.append(code);
					strsql.append("'");
					strsql.append(" and I9999=");
					strsql.append(I9999);						
					rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
					isExistData=!rs.isEmpty();
				}
				for(int i=0;i<infoFieldList.size();i++)                            //字段的集合
				{
				    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);				    
					if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
					{
						FieldItemView fieldItemView=new FieldItemView();
						fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
						fieldItemView.setCodesetid(fieldItem.getCodesetid());
						fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setFillable(fieldItem.isFillable());
	                    //在struts用来表示换行的变量
					    fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
						if("update".equals(edittype))
						{
							if(isExistData)
							{
								LazyDynaBean rec=(LazyDynaBean)rs.get(0);
								if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
								{
									if(!"0".equals(fieldItem.getCodesetid()))
									{
										String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
										if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
										   fieldItemView.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),codevalue)!=null?AdminCode.getCode(fieldItem.getCodesetid(),codevalue).getCodename():"");
									    else
									       fieldItemView.setViewvalue("");
									}
									else
									{
										//System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
										fieldItemView.setViewvalue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");
									}
									fieldItemView.setValue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");						
								}else if("D".equals(fieldItem.getItemtype()))                 //日期型有待格式化处理
								{
									if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
									}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
									}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10&& fieldItem.getItemlength()==7)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
									}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10&& fieldItem.getItemlength()==18)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,18)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,18)));
									}
									else
		                            {
		                            	fieldItemView.setViewvalue("");
									    fieldItemView.setValue("");
		                            }
								}
								else                                                          //数值类型的有待格式化处理
								{
									//fieldItemView.setFieldvalue(String.valueOf(this.getFrowset().getFloat(fieldItem.getItemid())));
									fieldItemView.setValue(PubFunc.DoFormatDecimal(String.valueOf(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():""),fieldItem.getDecimalwidth()));						
								}
							}
						}
						else
						{
							fieldItemView.setValue("");
						}
						infoFieldViewList.add(fieldItemView);
					}
				}
			}
		}catch(Exception e){
		   e.printStackTrace();
		}finally{
		   this.getFormHM().put("code",code);
		   this.getFormHM().put("i9999",I9999);
		   this.getFormHM().put("setname",setname);
	       this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
	      // this.getFormHM().put("infosetlist",infoSetList); 
	      // this.getFormHM().put("kind",kind);
       }
}

}
