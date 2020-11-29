/*
 * Created on 2005-10-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchSetInfoShowTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	/**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        if(!",".equals(strfields.substring(strfields.length()))){
        	strfields=strfields+",";
        } 
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            list.add(fieldname);
        }
        return list;
    }
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String userbase = (String)hm.get("userbase");
    	String a0100 = (String)hm.get("a0100");
    	String setname = (String)hm.get("setname");
    	String setStr = "";
    	try{
    		String sql = "select str_value from constant where constant = 'ZP_SUBSET_LIST'";
    		this.frowset = dao.search(sql);
    		while(this.frowset.next()){
    			setStr = this.getFrowset().getString("str_value");
    		}
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    	String fieldStr = "";
    	try{
    		String sql = "select str_value from constant where constant = 'ZP_FIELD_LIST'";
    		this.frowset = dao.search(sql);
    		while(this.frowset.next()){
    			fieldStr = this.getFrowset().getString("str_value");
    		}
    	}catch(SQLException ex){
    		ex.printStackTrace();
    	}
		ArrayList zpsetlist=new ArrayList();
		ArrayList zpfieldlist=new ArrayList();
		 
		ArrayList zpsubfieldlist=new ArrayList();
		ArrayList infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		
		ArrayList subsetList = new ArrayList();
	    try{
	    	String subfieldstr = "";
	    	int count = 0;
	    	int fieldindex = fieldStr.indexOf(setname);
	    	if(fieldindex != -1){
	    		String substr = fieldStr.substring(fieldindex,fieldStr.length());
	    		int subindex = substr.indexOf("},");
	    		subfieldstr = fieldStr.substring(fieldindex,fieldindex+subindex);
	    		ArrayList list = splitField(subfieldstr);
	    		count = list.size();
	    	}
	        List rs = null;
	    	StringBuffer strsql=new StringBuffer();
		    strsql.append("select * from ");
		    strsql.append(userbase + setname);
		    strsql.append(" where a0100 = '");
			strsql.append(a0100);
			strsql.append("'");
		    rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
		    boolean isExistData=!rs.isEmpty();
	    	ArrayList infofieldlist=DataDictionary.getFieldList(setname,Constant.EMPLOY_FIELD_SET);
	    	if(!infofieldlist.isEmpty())
	    	{
			        ArrayList subfieldlist=new ArrayList();
				    for(int j=0;j<infofieldlist.size();j++)
		    	    {
				 	    FieldItem fieldItem=(FieldItem)infofieldlist.get(j);
				 	    if(subfieldstr.toLowerCase().indexOf(fieldItem.getItemid().toLowerCase())!=-1){
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
					        fieldItemView.setRowflag(String.valueOf(count-1));
	    	 		        //为了在选择代码时方便而压入权限码开始
					        //为了在选择代码时方便而压入权限码结束					  	   
					  	    if(isExistData){
					  	    	 LazyDynaBean recdata=(LazyDynaBean)rs.get(0);
								if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
								{
									//System.out.println("itemidA" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
									if(!"0".equals(fieldItem.getCodesetid()))
									{
										String codevalue=recdata.get(fieldItem.getItemid())!=null?recdata.get(fieldItem.getItemid()).toString():"";
										if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
										   fieldItemView.setFieldvalue(AdminCode.getCodeName(fieldItem.getCodesetid(),codevalue));
									    else
									       fieldItemView.setFieldvalue("");
									}
									else
									{
										//System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
										fieldItemView.setFieldvalue(recdata.get(fieldItem.getItemid())!=null?recdata.get(fieldItem.getItemid()).toString():"");
									}
									//fieldItemView.setFieldcode(this.getFrowset().getString(fieldItem.getItemid()));						
								}else if("D".equals(fieldItem.getItemtype()))                 //日期型有待格式化处理
								{
									//System.out.println("itemidD" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
										if(recdata.get(fieldItem.getItemid())!=null && recdata.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
										{
											fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,recdata.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
										    //fieldItemView.setFieldcode(this.getFrowset().getString(fieldItem.getItemid().toLowerCase()).substring(0,10));
										}else if(recdata.get(fieldItem.getItemid())!=null && recdata.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
										{
											fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,recdata.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
										}
										else if(recdata.get(fieldItem.getItemid())!=null && recdata.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
										{
											fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,recdata.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
										}
										else
			                            {
			                            	fieldItemView.setFieldvalue("");
										   // fieldItemView.setFieldcode("");
			                            }
								}
								else                                                          //数值类型的有待格式化处理
								{
									//System.out.println("itemidN" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
									fieldItemView.setFieldvalue(PubFunc.DoFormatDecimal(recdata.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()));
								
								}
							}else{
					  	    	fieldItemView.setValue("");
					  	    }
						    zpfieldlist.add(fieldItemView);
					      }
		    	     }
				  }    			
		}catch(Exception e){
		   e.printStackTrace();
		}finally{		
	       this.getFormHM().put("zpfieldlist",zpfieldlist);            //压回页面
	       this.getFormHM().put("zpsetlist",zpsetlist);
	       this.getFormHM().put("userbase",userbase);
       }
		
	}

}
