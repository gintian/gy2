/**
 * 
 */
package com.hjsj.hrms.transaction.general.deci.statics;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Owner
 *
 */
public class SearchAnalyseInfoDataTrans extends IBusiness {

    	/* (non-Javadoc)
		 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
		 */
		public void execute() throws GeneralException {
			List rs=null;
			// TODO Auto-generated method stub
	    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
	    	String flag=(String)hm.get("flag");	    	
			if(!("infoself".equalsIgnoreCase(flag) && userView.getStatus()!=4))
			{
				String dbpre=(String)this.getFormHM().get("dbpre");       //人员库
				String tablename=dbpre + "A01";                           //表的名称
				String A0100=(String)this.getFormHM().get("a0100");       //获得人员ID
				if("A0100".equals(A0100))
					A0100=userView.getUserId();
				List infoFieldList=null;
				List infoSetList=null;
				if("infoself".equalsIgnoreCase(flag))
				{
					infoFieldList=userView.getPrivFieldList("A01",0);   //获得当前子集的所有属性
					infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);   //获得所有权限的子集
				}
			    else
			    {	
				  infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
				  infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
			    }
				List infoFieldViewList=new ArrayList();               //保存处理后的属性
				try
				{
	        		if(!infoFieldList.isEmpty())
				    {
						boolean isExistData=false;
						StringBuffer strsql=new StringBuffer();
						strsql.append("select * from ");
						strsql.append(tablename);
						strsql.append(" where A0100='");
						strsql.append(A0100);
						strsql.append("'");
						rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
						isExistData=!rs.isEmpty();
						LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						for(int i=0;i<infoFieldList.size();i++)                //字段的集合
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
						        //在struts用来表示换行的变量
							    fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
								if(isExistData)
								{
									if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
									{
										if(!"0".equals(fieldItem.getCodesetid()))
										{
											String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
											if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
											   fieldItemView.setFieldvalue(AdminCode.getCodeName(fieldItem.getCodesetid(),codevalue));
										    else
										       fieldItemView.setFieldvalue("");
										}
										else
										{
											String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString().replaceAll("\n","<br>"):"";
											fieldItemView.setFieldvalue(fieldvalue);
										}
									}else if("D".equals(fieldItem.getItemtype()))        //日期型有待格式化处理
									{
											if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
											{
												fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
											}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
											{
												fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
											}
											else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
											{
												fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
											}
											else
				                            {
				                            	fieldItemView.setFieldvalue("");
				                            }
									}
									else                                              //数值类型的有待格式化处理
									{
		                             	fieldItemView.setFieldvalue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()));
									}
								}		
								fieldItemView.setRowindex(String.valueOf(i));
								infoFieldViewList.add(fieldItemView);
							}
						}
					}
				}catch(Exception e){
				   e.printStackTrace();
				}finally{
				   this.getFormHM().put("a0100",A0100);
			       this.getFormHM().put("infofieldlist",infoFieldViewList);          //压回页面
			       this.getFormHM().put("infosetlist",infoSetList);
		       }	
			}
			else
			{
				throw new GeneralException("","非自助平台用户!","","");
			}
	  }

}
