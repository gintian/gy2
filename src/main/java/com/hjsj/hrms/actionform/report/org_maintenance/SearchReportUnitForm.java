
package com.hjsj.hrms.actionform.report.org_maintenance;

import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchReportUnitForm extends FrameForm {
	
	//显示填报单位信息
	private PaginationForm reportUnitListForm = new PaginationForm(); 	
    private int current=1;

    
    //修改填报单位
    private String unitCode;//填报单位编码
    private String unitName;//填报单位名称
    
    //增加填报单位
    private String parentCode ;//父
    private String len;//单位代码约束
    private String codeFlag;//codeFlag标识了当前页面显示信息的父接点单位编码
    private String addUnitCode;//填报单位编码
    private String addUnitName;//填报单位名称
    private String mlen; //实际的单位代码约束
    private String lenInfo; //单位编码长度约束信息
    private String addFlag; //添加填报单位后刷新单位树
    
    private String start_date="";  //有效日期起
    private String end_date="";   //有效日期止
    private String goal_unit="";
    private String goal_unitDesc="";
    
    //  批量表类授权中的填报单位编码集合
	private String rtUnitCodes;
	private PaginationForm reportTypeList = new PaginationForm();  //报表类别
	//	在保存报表类别时使用，填报单位编码
	private String rtUnitCode;
	
	private ArrayList reportList=new ArrayList();
	
	private ArrayList reportUId=new ArrayList();
	
	private HashMap allSelectMap = new HashMap();
	
	private String sql="";
	
	private String content;//授权人员
	
	private String  isActuarialData="0";  //是否有精算数据,有则撤销单位时须填写人员划转单位
	
	private String backdate ="" ; //选择时间点
	
	private String historyu01 = "";
	private String analysereportflag ="";
	

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
    public void outPutFormHM() {
		this.setIsActuarialData((String)this.getFormHM().get("isActuarialData"));
		this.setHistoryu01((String)this.getFormHM().get("historyu01"));
		this.setStart_date((String)this.getFormHM().get("start_date"));
		this.setEnd_date((String)this.getFormHM().get("end_date"));
		
		this.setReportList((ArrayList)this.getFormHM().get("reportList"));
		//显示填报单位信息
		this.getReportUnitListForm().setList((ArrayList)this.getFormHM().get("reportUnitList"));
		this.getReportUnitListForm().getPagination().gotoPage(current);
		
		//修改填报单位信息显示
		this.setUnitCode((String)this.getFormHM().get("unitcode"));
		this.setUnitName((String)this.getFormHM().get("unitname"));
		
		
		
		this.setLen((String)this.getFormHM().get("maxlen"));
		String p= (String)this.getFormHM().get("parentCode");
		this.setParentCode(p);
		
		//System.out.println("p=" + p);
		
		this.setAddUnitCode((String)this.getFormHM().get("addUnitCode"));
		this.setAddUnitName((String)this.getFormHM().get("addUnitName"));
		

		this.setCodeFlag((String)this.getFormHM().get("codeFlag"));
		
		this.setMlen((String)this.getFormHM().get("mlen"));
		this.setLenInfo((String)this.getFormHM().get("lenInfo"));
		
		this.setAddFlag((String)this.getFormHM().get("addFlag"));
		
		this.setRtUnitCodes((String)this.getFormHM().get("rtunitcodes"));

		this.getReportTypeList().setList((ArrayList)this.getFormHM().get("reporttypelist"));
		this.getReportTypeList().getPagination().gotoPage(current);	
		this.setRtUnitCode((String)this.getFormHM().get("rtunitcode"));
		
		//kangkai
		this.setReportUId((ArrayList)this.getFormHM().get("reportUId"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setBackdate((String)this.getFormHM().get("backdate"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setAnalysereportflag((String)this.getFormHM().get("analysereportflag"));
		this.setAllSelectMap((HashMap)this.getFormHM().get("allSelectMap"));
		
	}

	@Override
    public void inPutTransHM() {
		//保存修改填报单位信息数据 
		this.getFormHM().put("unitCode",this.getUnitCode());
		this.getFormHM().put("unitName",this.getUnitName());
		
		//选中的填报单位对象
		this.getFormHM().put("selectedlist",this.getReportUnitListForm().getSelectedList());
		this.getFormHM().put("codeflag" , this.getCodeFlag());
		
		
		this.getFormHM().put("selectedrtlist",this.getReportTypeList().getSelectedList());
		this.getFormHM().put("selectedrtlistall",this.getReportTypeList().getList());
		//System.out.println("codeflag=" + this.getCodeFlag());
		
		//增加填报单位
		this.getFormHM().put("addUnitCode" ,this.getAddUnitCode());
		this.getFormHM().put("addUnitName" , this.getAddUnitName());
		this.getFormHM().put("parentCode",this.getParentCode());
		this.getFormHM().put("len",this.getLen());
		this.getFormHM().put("addFlag","no");
		this.getFormHM().put("content",this.getContent());
		
		this.getFormHM().put("start_date",this.getStart_date());
		this.getFormHM().put("end_date",this.getEnd_date());
		this.getFormHM().put("backdate",this.getFormHM().get("backdate"));
		this.getFormHM().put("allselectedlist", this.getAllSelectMap());
	
	}

	/**
	 * @return Returns the reportUnitListForm.
	 */
	public PaginationForm getReportUnitListForm() {
		return reportUnitListForm;
	}
	/**
	 * @param reportUnitListForm The reportUnitListForm to set.
	 */
	public void setReportUnitListForm(PaginationForm reportUnitListForm) {
		this.reportUnitListForm = reportUnitListForm;
	}

	
	/**
	 * @return Returns the unitCode.
	 */
	public String getUnitCode() {
		return unitCode;
	}
	/**
	 * @param unitCode The unitCode to set.
	 */
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public String getAnalysereportflag() {
		return analysereportflag;
	}

	public void setAnalysereportflag(String analysereportflag) {
		this.analysereportflag = analysereportflag;
	}

	/**
	 * @return Returns the unitName.
	 */
	public String getUnitName() {
		return unitName;
	}
	/**
	 * @param unitName The unitName to set.
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	
	/**
	 * @return Returns the parentCode.
	 */
	public String getParentCode() {
		return parentCode;
	}
	/**
	 * @param parentCode The parentCode to set.
	 */
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	
	
	
	/**
	 * @return Returns the len.
	 */
	public String getLen() {
		return len;
	}
	/**
	 * @param len The len to set.
	 */
	public void setLen(String len) {
		this.len = len;
	}
	
	
	/**
	 * @return Returns the codeFlag.
	 */
	public String getCodeFlag() {
		return codeFlag;
	}
	/**
	 * @param codeFlag The codeFlag to set.
	 */
	public void setCodeFlag(String codeFlag) {
		this.codeFlag = codeFlag;
	}
	
	
	/**
	 * @return Returns the addUnitName.
	 */
	public String getAddUnitName() {
		return addUnitName;
	}
	/**
	 * @param addUnitName The addUnitName to set.
	 */
	public void setAddUnitName(String addUnitName) {
		this.addUnitName = addUnitName;
	}
	/**
	 * @return Returns the current.
	 */
	
	/**
	 * @return Returns the addUnitCode.
	 */
	public String getAddUnitCode() {
		return addUnitCode;
	}
	/**
	 * @param addUnitCode The addUnitCode to set.
	 */
	public void setAddUnitCode(String addUnitCode) {
		this.addUnitCode = addUnitCode;
	}

	public String getMlen() {
		return mlen;
	}

	public void setMlen(String mlen) {
		this.mlen = mlen;
	}

	public String getLenInfo() {
		return lenInfo;
	}

	public void setLenInfo(String lenInfo) {
		this.lenInfo = lenInfo;
	}

	public String getAddFlag() {
		return addFlag;
	}

	public void setAddFlag(String addFlag) {
		this.addFlag = addFlag;
	}

	public String getRtUnitCodes() {
		return rtUnitCodes;
	}

	public void setRtUnitCodes(String rtUnitCodes) {
		this.rtUnitCodes = rtUnitCodes;
	}

	public PaginationForm getReportTypeList() {
		return reportTypeList;
	}

	public void setReportTypeList(PaginationForm reportTypeList) {
		this.reportTypeList = reportTypeList;
	}

	public String getRtUnitCode() {
		return rtUnitCode;
	}

	public void setRtUnitCode(String rtUnitCode) {
		this.rtUnitCode = rtUnitCode;
	}

	public ArrayList getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList reportList) {
		this.reportList = reportList;
	}

    	
	public HashMap getAllSelectMap() {
		return allSelectMap;
	}

	public void setAllSelectMap(HashMap allSelectMap) {
		this.allSelectMap = allSelectMap;
	}

	public ArrayList getReportUId() {
		return reportUId;
	}

	public void setReportUId(ArrayList reportUId) {
		this.reportUId = reportUId;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//System.out.print(arg0.getPath()+"&"+arg1.getParameter("b_add"));
		if("/report/org_maintenance/addreportunit".equals(arg0.getPath())&&arg1.getParameter("b_add")!=null&& "link2".equals(arg1.getParameter("b_add"))){
            /**定位到首页,*/
			Connection con =null;
			ResultSet rs =null;
			try {
				con = (Connection)AdminDb.getConnection();
				ContentDAO dao=new ContentDAO(con);
				this.sql=PubFunc.keyWord_reback(this.sql);
			rs =	dao.search(this.sql);
			LazyDynaBean abean=null;
			ArrayList list2 = new ArrayList();
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
			while(rs.next()){
				abean=new LazyDynaBean();
				
				RecordVo vo = new RecordVo("tt_organization");
				String uid = String.valueOf(rs.getInt("unitid"));
				vo.setString("unitid",uid);
				abean.set("unitid", uid);
				StringBuffer temp = new StringBuffer(rs.getString("unitname"));
				/*if(temp.length()>=25){
					temp.insert(25,"<br>");
				}*/
				vo.setString("unitname" ,temp.toString());
				abean.set("unitname", temp.toString());
				String unitcode = rs.getString("unitcode");
				vo.setString("unitcode" , unitcode );
				abean.set("unitcode",unitcode);
				String reporttypes = rs.getString("reporttypes");
				TTorganization ttorganization=new TTorganization(con);
				HashMap reportmap =  ttorganization.getReportTsort();
				//System.out.println("reporttypes=" + reporttypes);
				if(analysereportflag!=null&& "1".equals(analysereportflag)){
					String analysereports =rs.getString("analysereports");
					ArrayList templist = new ArrayList();
					if(analysereports!=null&&analysereports.length()>0){
					String reports [] =	analysereports.split(",");
					String temptypes =",";
					for(int i=0;i<reports.length;i++){
						if(reports[i].trim().length()>0&&temptypes.indexOf(","+reportmap.get(reports[i].trim())+",")==-1&&reportmap.get(reports[i].trim())!=null){
							temptypes+=reportmap.get(reports[i].trim())+",";
							templist.add(reportmap.get(reports[i].trim()));
						}
					}
					if(temptypes.length()>1){
						reporttypes= "";
						Collections.sort(templist);
						for(int i=0;i<templist.size();i++){
							reporttypes+= templist.get(i)+",";
						}
					}
					}else{//一个表都没选  赵旭光 2013-4-10
						reporttypes = null;
					}
					
				}
				if(reporttypes == null || "".equals(reporttypes)){
					vo.setString("reporttypes" , "");
					abean.set("reporttypes","");
				}else{
					vo.setString("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
					abean.set("reporttypes","("+reporttypes.substring(0,reporttypes.length()-1)+")");
				}
				String un = this.getUserName(unitcode,con);
				
				//System.out.println("un=" + un);
				
				if(un==null|| "".equals(un)){
					vo.setString("b0110","");
					abean.set("b0110","");
				}else{
					vo.setString("b0110","("+un.substring(0,un.length()-1)+")");
					abean.set("b0110","("+un.substring(0,un.length()-1)+")");
				}
				String a0000 = rs.getString("a0000");
				
				//System.out.println("reporttypes=" + reporttypes);
				
				if(a0000 == null || "".equals(a0000)){
					vo.setString("a0000" , "");
					abean.set("a0000","");
				}else{
					vo.setString("a0000",a0000);
					abean.set("a0000",a0000);
				}
				abean.set("start_date", df.format(rs.getDate("start_date")));
				abean.set("end_date", df.format(rs.getDate("end_date")));
				 
				list2.add(abean);
			
		}
			this.getFormHM().put("reportUnitList",list2);
			this.getReportUnitListForm().setList(list2);
	//		this.outPutFormHM();
//			if(con!=null)
//				con.close();
			} 
			catch(SQLException ex){
				
			}
			catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				if(con!=null)
					try {
						con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			//      if(this.getSetlistform().getPagination()!=null)
      //      	this.getSetlistform().getPagination().firstPage();
			//	return super.validate(arg0, arg1);
		}else if("/report/org_maintenance/reporttypelist".equals(arg0.getPath()) && (arg1.getParameter("b_addtype")!=null || arg1.getParameterMap().keySet().toString().indexOf("b_")==-1)){
			ArrayList currlistselected1 = this.getReportTypeList().getSelectedList();
			ArrayList currlistall =this.reportTypeList.getList();
			ArrayList alllist = this.reportTypeList.getAllList();
			for(int j =0;j<currlistall.size();j++){
				if(allSelectMap.containsKey(currlistall.get(j)))
					allSelectMap.remove(currlistall.get(j));
			}
			for(int i=0;i<currlistall.size();i++){
				((RecordVo)currlistall.get(i)).setString("sid","0");
				for(int k = 0;k<currlistselected1.size();k++){
					if(currlistall.get(i)==currlistselected1.get(k)){
						((RecordVo)currlistall.get(i)).setString("sid","1");
					}
				}
			}
			for(int k = 0;k<currlistselected1.size();k++){
				allSelectMap.put(currlistselected1.get(k), null);
			}
		}
		return super.validate(arg0, arg1);
	}
	public String getUserName(String uid,Connection con){
		StringBuffer userName = new StringBuffer();;
		String sql="select username from operuser   where unitcode='"+uid+"'";
		ContentDAO dao = new ContentDAO(con);		
		RowSet rs=null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				String un = rs.getString("username");
				userName.append(un);
				userName.append(",");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return userName.toString();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getIsActuarialData() {
		return isActuarialData;
	}

	public void setIsActuarialData(String isActuarialData) {
		this.isActuarialData = isActuarialData;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public String getHistoryu01() {
		return historyu01;
	}

	public void setHistoryu01(String historyu01) {
		this.historyu01 = historyu01;
	}
	
}
