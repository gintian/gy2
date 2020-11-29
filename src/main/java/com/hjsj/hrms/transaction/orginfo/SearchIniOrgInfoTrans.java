package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SearchIniOrgInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String fieldstr="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		//InfoUtils infoUtils=new InfoUtils();
		//code=infoUtils.getCodeInifValue(this.getFrameconn(),this.userView);
	    this.getFormHM().put("code", code);
	    String privcode=this.userView.getManagePrivCode();
	    if(privcode==null||privcode.length()<=0)
	    	privcode="UN";
	    String codemess=AdminCode.getCodeName(privcode, code);
		this.getFormHM().put("codemess", codemess);
		this.getFormHM().put("isShowCondition", "none");
		
		String unit_code_field="";	
		
		RecordVo vo= ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD");
	    if(vo!=null)
	    {
	    	unit_code_field=vo.getString("str_value");
	    	if(unit_code_field!=null&&unit_code_field.length()>0&&!"#".equals(unit_code_field))
	    	{
	    		if(fieldstr.toLowerCase().indexOf(unit_code_field.toLowerCase())==-1)
	    		{
	    			fieldstr=fieldstr+","+unit_code_field;
	    		}
	    	}
	    }
	    
		vo=ConstantParamter.getRealConstantVo("UNIT_MAINSET_FIELD");
		if(vo!=null)
		{
			fieldstr+=vo.getString("str_value");
		}
		fieldstr=splitFieldStr(fieldstr);
		if(fieldstr==null||fieldstr.length()<=0)
			fieldstr=",b0110";
		if(fieldstr.indexOf("b0110")==-1)
			fieldstr+=",b0110";
		this.getFormHM().put("fieldstr", fieldstr+",codesetid,orgtype");
		this.getFormHM().put("orglike", "0");
		this.getFormHM().put("querylike", "0");
		this.getFormHM().put("query", "");
		ArrayList fieldList=splitField(fieldstr);
		if(fieldList!=null)
		  this.getFormHM().put("fieldList", fieldList);
		else
		  this.getFormHM().put("fieldList", new ArrayList());
	    selectField();	   
	    Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
	    
	}
    public void selectField()
    {
    	 RecordVo vo= ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE");
    	 if(vo!=null)
         {
             String strfields=vo.getString("str_value");
             ArrayList fieldlist=splitField(strfields);
             this.getFormHM().put("selectfieldlist",fieldlist);            
         }
         else
         {
             this.getFormHM().put("selectfieldlist",new ArrayList());            	
         }
    }
    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if("b0110".equalsIgnoreCase(fieldname)) {
                item=DataDictionary.getFieldItem(fieldname,"b01");
            } else if("e01a1".equalsIgnoreCase(fieldname)) {
                item=DataDictionary.getFieldItem(fieldname,"k01");
            }
            
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone();             	 
                 list.add(item_0);
            }
           
        }
        return list;
    }
    private String splitFieldStr(String strfields)
    {
    	if(strfields==null||strfields.length()<=0)
    		return"";
    	String fieldarr[]=strfields.split(",");
    	String fieldstr="";
		if(fieldarr!=null)
		{
			for(int i=0;i<fieldarr.length;i++)
			{
				String field=fieldarr[i];
				if(fieldstr.indexOf(field)==-1){
					FieldItem item=DataDictionary.getFieldItem(field);
					if("b0110".equalsIgnoreCase(field)) {
                        item=DataDictionary.getFieldItem(field,"b01");
                    } else if("e01a1".equalsIgnoreCase(field)) {
                        item=DataDictionary.getFieldItem(field,"k01");
                    }
					
					if(item!=null&&(!"0".equals(item.getUseflag())&&item.getUseflag().length()>0))
						fieldstr=fieldstr+","+field;
				}
			}
		}
		return fieldstr;
    }
}
