package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class SearchIniPartyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String fieldstr="";
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String param = (String)hm.get("param");
		param = param!=null&&param.length()>0?param:"Y";//党务
		String codesetid ="64";
		if("Y".equalsIgnoreCase(param)){
			codesetid = "64";
		}
		if("V".equalsIgnoreCase(param)){//团务
			codesetid = "65";
		}
		if("W".equalsIgnoreCase(param)){//工会
			codesetid = "66";
		}
		if("H".equalsIgnoreCase(param)){//工会
			RecordVo constantuser_vo = ConstantParamter
			.getRealConstantVo("PS_C_CODE");
			if (constantuser_vo == null) {
				String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
				throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
			}
			codesetid = constantuser_vo.getString("str_value");
			if("".equals(codesetid)|| "#".equals(codesetid)){
				String temp=ResourceFactory.getProperty("pos.posbusiness.nosetposccode");
				throw GeneralExceptionHandler.Handle(new GeneralException("",temp,"", ""));
			}
			FieldItem fi = DataDictionary.getFieldItem("h0100");
			fi.setCodesetid(codesetid);
		}
		DbWizard dbwizard=new DbWizard(this.getFrameconn());	
	    if(!dbwizard.isExistTable(param+"01",false)){
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("dtgh.party."+param),"",""));
	    }
	    this.getFormHM().put("code", codesetid);
	    String sql = "select codesetdesc from codeset where codesetid='"
			+ codesetid + "'";
	    try{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    this.frowset = dao.search(sql);
		    if (this.frowset.next())
		    	this.getFormHM().put("codesetdesc",
					this.frowset.getString("codesetdesc"));
			this.getFormHM().put("isShowCondition", "none");
			this.getFormHM().put("partylike", "1");
			fieldstr="codeitemid,codeitemdesc";
			String constant = "PARTY_MAINSET_FIELD";//在参数设置中要显示的列
	    	 if("Y".equals(param))
	    		 constant = "PARTY_MAINSET_FIELD";	
			 else if("V".equals(param))
				 constant = "CORPS_MAINSET_FIELD";	
			 else if("W".equals(param))
				 constant = "";
			 else if("H".equals(param)){
				 constant = "SPOST_MAINSET_FIELD";
				 fieldstr="codeitemid";
			 }
			RecordVo vo =ConstantParamter.getRealConstantVo(constant);
			if(vo!=null)
			{   if(vo.getString("str_value").indexOf("h0100") == -1 && "H".equals(param))
				   fieldstr+=",h0100";
				fieldstr+=vo.getString("str_value");
			}
			
			
			this.getFormHM().put("fieldstr", fieldstr);
			this.getFormHM().put("orglike", "0");
			this.getFormHM().put("querylike", "0");
			this.getFormHM().put("query", "");
			this.getFormHM().put("isShowCondition", "none");
			ArrayList fieldList=splitField(fieldstr,true);
			if(fieldList!=null)
			  this.getFormHM().put("fieldList", fieldList);
			else
			  this.getFormHM().put("fieldList", new ArrayList());
		    selectField(param);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	}
    public void selectField(String param)
    {
    	 String constant = "SS_YQUERYTEMPLATE";
    	 if("Y".equals(param))
    		 constant = "SS_YQUERYTEMPLATE";	
		 else if("V".equals(param))
			 constant = "SS_VQUERYTEMPLATE";	
		 else if("W".equals(param))
			 constant = "SS_WQUERYTEMPLATE";
		 else if("H".equals(param))
			 constant = "SS_HQUERYTEMPLATE";
    	 RecordVo vo= ConstantParamter.getRealConstantVo(constant);
    	 if(vo!=null)
         {
             String strfields=vo.getString("str_value");
             ArrayList fieldlist=splitField(strfields,false);
             this.getFormHM().put("selectfieldlist",fieldlist);            
         }
         else
         {
             this.getFormHM().put("selectfieldlist",new ArrayList());            	
         }
    }
    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields,boolean b)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();                      //zhaogd 2013-11-21 修改了判断顺序，使其符合党团管理模块
            if("codeitemdesc".equals(fieldname)&&b){
            	FieldItem item_0 = new FieldItem("","codeitemdesc");
           	    item_0.setCodesetid("0");
           	    item_0.setItemlength(30);
           	    item_0.setItemtype("A");
           	    item_0.setItemdesc("名称");
           	    item_0.setDisplaywidth(30);
           	    list.add(item_0);
            }else if("codeitemid".equals(fieldname)&&b){
            	FieldItem item_0 = new FieldItem("","codeitemid");
	           	item_0.setCodesetid("0");
	            item_0.setItemlength(30);
	        	item_0.setItemtype("A");
	        	item_0.setItemdesc("系统代码");
           	    item_0.setDisplaywidth(30);
	        	list.add(item_0);
            }else{
            	FieldItem item=DataDictionary.getFieldItem(fieldname);
                if(item!=null)
                {
                	 FieldItem item_0=(FieldItem)item.clone(); 
                	 if("h0100".equals(fieldname) && b)
                     	item_0.setCodesetid("0");
                     list.add(item_0);
                }
            }
        }
        return list;
    }
}
