package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowOrgYearSingleData extends  IBusiness{
    public void execute()throws GeneralException
    {
   	  	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	  	String b0110 = (String) this.getFormHM().get("b0110");
   	  	String sessiondate = (String) hm.get("sessiondate"); 	  
   	  	String cur_year=(String)this.getFormHM().get("cur_year");
   	  	ArrayList yearlist=RegisterDate.sessionYaer(this.frameconn,"1");
   	  	if(cur_year==null||cur_year.length()<=0)
   	  	{
   	  		if(yearlist!=null&&yearlist.size()>0){			
				if(sessiondate!=null&&sessiondate.length()>0){
					cur_year=sessiondate.substring(0,4);
				}else{
					CommonData vo = (CommonData) yearlist.get(0);
					cur_year=vo.getDataValue();
				}			
   	  		}else{
   	  			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
   	  		}  
	  	}
		
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fielditemlist= OrgRegister.newFieldItemList(fieldlist);//SingOpintion.newFieldOneList(fieldlist);
		/**
		 * 特殊处理日期字段Q03Z0 设置成显示状态
		 */
		for(int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);	
			if("Q03Z0".equalsIgnoreCase(fielditem.getItemid()))
				fielditem.setVisible(true);
		}
//		ArrayList sqllist=SingOpintion.getOneOrgYearSQLStr(fielditemlist,b0110,cur_year,"Q09");
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		ArrayList sqllist=null;
		if(dataInQ09(cur_year) || !dbWizard.isExistTable("Q09_arc", false)){
			sqllist=SingOpintion.getOneOrgYearSQLStr(fielditemlist,b0110,cur_year,"Q09");
		}else{
			sqllist=SingOpintion.getOneOrgYearSQLStr(fielditemlist,b0110,cur_year,"Q09_arc");
		}
		 
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition","9`"+sqllist.get(4).toString());
		this.getFormHM().put("relatTableid","9");
		this.getFormHM().put("returnURL","/kq/register/history/orgyearsingle.do?b_browse=link");
		 this.getFormHM().put("singfielditemlist", fielditemlist);	
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("cur_year",cur_year);
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("returnURL2","/kq/register/history/sumorgbrowse.do?b_search=link&action=sumorgbrowsedata.do&target=mil_body&a_inforkind=1&viewPost=kq");
		String org_name=AdminCode.getCodeName("UN", b0110);
		if(org_name==null||org_name.length()<=0)
			org_name=AdminCode.getCodeName("UM", b0110);
		this.getFormHM().put("org_name",org_name);

    }
    /**
     * 判断某日期数据是否在Q09（部门月汇总表）中
     * 
     * @return
     * @throws GeneralException
     */
    private boolean dataInQ09(String registerdate) throws GeneralException {
    	boolean bool = true;
    	RowSet rs = null;
    	if (registerdate == null) {
    		return false;
    	}
    	StringBuffer sql = new StringBuffer();
    	sql.append("select count(q03z0) num from Q09 ");
    	sql.append(" where q03z0 like '"+registerdate+"%'");
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	int num = 0;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
            	num = rs.getInt("num");
            }
            if(num == 0){
            	bool = false; 
            }            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return bool;
    }
}
