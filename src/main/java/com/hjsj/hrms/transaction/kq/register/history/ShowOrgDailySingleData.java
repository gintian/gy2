package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
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

public class ShowOrgDailySingleData extends  IBusiness{

	public void execute()throws GeneralException
    {
   	  	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	  	String b0110 = (String) this.getFormHM().get("b0110");
   	  	String sessiondate = (String) hm.get("sessiondate");		  
   	  	ArrayList yearlist=RegisterDate.sessionDate(this.frameconn,"1");
   	  	String cur_date=(String)this.getFormHM().get("registerdate");
   	  	if(cur_date==null||cur_date.length()<=0){
		  if(yearlist!=null&&yearlist.size()>0){			
				if(sessiondate!=null&&sessiondate.length()>0){
					cur_date=sessiondate;
				}else{
					CommonData vo = (CommonData) yearlist.get(0);
					cur_date=vo.getDataValue();
				}			
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
			}  	
	  	}
	   	ArrayList datelist=RegisterDate.getKqDate(this.getFrameconn(),cur_date,1);
	    String start_date = datelist.get(0).toString(); 		 
		String end_date=datelist.get(1).toString();
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
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		ArrayList sqllist=null;
		if(dataInQ07(start_date) || !dbWizard.isExistTable("Q07_arc", false)){
			sqllist=SingOpintion.getOneOrgMothSQLStr(fielditemlist,b0110,start_date,end_date,"Q07");
		}else{
			sqllist=SingOpintion.getOneOrgMothSQLStr(fielditemlist,b0110,start_date,end_date,"Q07_arc");	
		}
//		ArrayList sqllist=SingOpintion.getOneOrgMothSQLStr(fielditemlist,b0110,start_date,end_date,"Q07");		 
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition",SafeCode.encode("7`"+sqllist.get(4).toString()));
		this.getFormHM().put("relatTableid","7");
		this.getFormHM().put("returnURL","/kq/register/history/orgdailysingle.do?b_browse=link");
		this.getFormHM().put("returnURL2","/kq/register/history/dailyorgbrowse.do?b_search=link&action=dailyorgbrowsedata.do&target=mil_body&a_inforkind=1&viewPost=kq");
		 this.getFormHM().put("singfielditemlist", fielditemlist);	
		this.getFormHM().put("yearlist",yearlist);		
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("registerdate",cur_date);
		String org_name=AdminCode.getCodeName("UN", b0110);
		if(org_name==null||org_name.length()<=0)
			org_name=AdminCode.getCodeName("UM", b0110);
		this.getFormHM().put("org_name",org_name);
    }
	/**
     * 判断某日期数据是否在Q07（部门日明细表）中
     * 
     * @return
     * @throws GeneralException
     */
    private boolean dataInQ07(String registerdate) throws GeneralException {
    	boolean bool = true;
    	RowSet rs = null;
    	if (registerdate == null) {
    		return false;
    	}
    	registerdate = registerdate.substring(0, registerdate.length()-3);
    	StringBuffer sql = new StringBuffer();
    	sql.append("select count(q03z0) num from Q07 ");
    	sql.append(" where q03z0 like '"+registerdate+"%'");
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	int num = 0;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
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
