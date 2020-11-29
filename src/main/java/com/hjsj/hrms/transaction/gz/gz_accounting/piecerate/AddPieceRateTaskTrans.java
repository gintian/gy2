package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateBo;
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
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddPieceRateTaskTrans extends IBusiness {
	String model ="";
	String s0100 = "";
	public void execute() throws GeneralException {
		String canEdit="true";
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		model = (String) hm.get("model");
		s0100 = (String) hm.get("s0100");
		ArrayList FielditemList = new ArrayList();
        try {     
        	PieceRateBo TaskBo=new PieceRateBo(this.getFrameconn(),s0100,this.userView);
        	FielditemList= getViewList(this.getDispFieldList(DataDictionary.getFieldList("S01",Constant.USED_FIELD_SET)),
        			TaskBo);			
			if (("edit".equals(model))&&(!TaskBo.CanEdit())){
				canEdit="false";	
			}
			this.getFormHM().put("canEdit", canEdit);    
        	this.getFormHM().put("taskeditmodel", model);    
        	this.getFormHM().put("s0100", s0100);    
			this.getFormHM().put("fielditemlist", FielditemList);
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
    private ArrayList getDispFieldList(ArrayList list)
    {
        if(list == null)  return null;
        FieldItem item = null;
        String fieldname = null;
        ArrayList Displist = new ArrayList();
        String s=",S0100,USERFLAG,SP_FLAG,CURR_USER,APPUSER,APPPROCESS,";
        for(int i = 0; i < list.size(); i++)
        {
            item = (FieldItem)list.get(i);
            item = (FieldItem)item.cloneItem();
            fieldname = ","+item.getItemid().toUpperCase()+",";
            if (s.indexOf(fieldname)<0){
            	Displist.add(item);	
            }            
        }
        return Displist;
    }
    
	private ArrayList getViewList(List infoFieldList,PieceRateBo TaskBo) throws GeneralException
	{
	 List rs = null;
	 ContentDAO dao = new ContentDAO(this.getFrameconn());
	 ArrayList infoFieldViewList = new ArrayList(); 
	 try
	  {	
	    if (!infoFieldList.isEmpty())
	    {
		  boolean isExistData = false;
		  String strFill=",S0102,S0104,";
	  	  if ("edit".equals(model)) // 若是修改取其值
		  {	String strsql="select * from S01 where S0100="+s0100;
			rs = ExecuteSQL.executeMyQuery(strsql, this.getFrameconn());
			isExistData = !rs.isEmpty();
		   }
	 	   for (int i = 0; i < infoFieldList.size(); i++) // 字段的集合
		   {
				FieldItem fieldItem = (FieldItem) infoFieldList.get(i);
				FieldItemView fieldItemView = new FieldItemView();
				fieldItemView.setSequencename(fieldItem.getSequencename());
				fieldItemView.setSequenceable(fieldItem.isSequenceable());
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
				if (strFill.indexOf(","+fieldItem.getItemid().toUpperCase()+",")>-1){
					fieldItemView.setFillable(true);	
				}
				// 在struts用来表示换行的变量
				fieldItemView.setRowflag(String.valueOf(infoFieldList.size() - 1));
				if ("edit".equals(model) && isExistData)
				{
					LazyDynaBean rec = (LazyDynaBean) rs.get(0);
					if (isExistData)
					{
					  if ("A".equals(fieldItem.getItemtype())|| "M".equals(fieldItem.getItemtype())) {
						String codevalue = rec.get(fieldItem.getItemid()) != null 
			              ? rec.get(fieldItem.getItemid()).toString() : "";
						if (!"0".equals(fieldItem.getCodesetid())) 
						{
							if (codevalue != null && codevalue.trim().length() > 0
									&& fieldItem.getCodesetid() != null
									&& fieldItem.getCodesetid().trim().length() > 0)
							{
								fieldItemView.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),
												codevalue) != null ? AdminCode.getCode(fieldItem.getCodesetid(),
												codevalue).getCodename(): "");	
								
							}											
							else
								fieldItemView.setViewvalue(codevalue);	
							}	
					    	fieldItemView.setValue(codevalue);
						} 
					  else  if ("D".equals(fieldItem.getItemtype())) // 日期型有待格式化处理
					  {
						if (rec.get(fieldItem.getItemid()) != null
								&& rec.get(fieldItem.getItemid()).toString().length() > 10	&& fieldItem.getItemlength() == 18) {
							fieldItemView.setViewvalue(new FormatValue().format(fieldItem,
													rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,19)));
							fieldItemView.setValue(new FormatValue().format(
													fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,19)));
						}else if (rec.get(fieldItem.getItemid()) != null
								&& rec.get(fieldItem.getItemid()).toString().length() >= 10	&& fieldItem.getItemlength() == 10) {
							fieldItemView.setViewvalue(new FormatValue().format(fieldItem,
													rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
							fieldItemView.setValue(new FormatValue().format(
													fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
						} else if (rec.get(fieldItem.getItemid()) != null
								&& rec.get(fieldItem.getItemid()).toString().length() >= 10
								&& fieldItem.getItemlength() == 4) {
							fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString()
															.substring(0,4)));
							fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem
																			.getItemid().toLowerCase()).toString()
															.substring(0,4)));
						} else if (rec.get(fieldItem.getItemid()) != null&& rec.get(fieldItem.getItemid()).toString().length() >= 10
								&& fieldItem.getItemlength() == 7)
						{
							fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec
															.get(fieldItem.getItemid().toLowerCase())
															.toString().substring(0,7)));
							fieldItemView.setValue(new FormatValue()
											.format(fieldItem,rec.get(
																	fieldItem.getItemid().toLowerCase())
															.toString().substring(0,7)));
						} else {
							fieldItemView.setViewvalue("");
							fieldItemView.setValue("");
						}
				     } 
				     else 
					  {
						fieldItemView.setValue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()) != null ? rec
											.get(fieldItem.getItemid()).toString() : "", fieldItem.getDecimalwidth()));
					  }
				  }
	
				} else 	if ("add".equalsIgnoreCase(model)) {
					if ("S0105".equalsIgnoreCase(fieldItem.getItemid())){
				      	String s=TaskBo.getE0122Value();	
				      	if (!"".equals(s)){
				      		String s0105Id=s;
				      		String s0105desc=AdminCode.getCodeName("UM",s0105Id);
				      		if (!"".equals(s0105desc)){
				      			fieldItemView.setValue(s0105Id);	
				      			fieldItemView.setViewvalue(s0105desc);	
				      		}
				      	}
					}
					else{
						fieldItemView.setValue("");	
					}
					
				} else {
					fieldItemView.setValue("");
				}
				infoFieldViewList.add(fieldItemView);
			}	
	 	}
		return infoFieldViewList;
	   }catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
    }
		


}
