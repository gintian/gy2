package com.hjsj.hrms.transaction.mobileapp.emp;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.mobileapp.utils.SearchInformationClassBo;
import com.hjsj.hrms.transaction.mobileapp.utils.Tools;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p> Title: EmpSearchTrans </p>
 * <p> Description: 员工查询</p>
 * <p> Company: hjsj </p>
 * <p> create time 2013-11-11 下午05:35:10 </p>
 * 
 * @author xuj
 * @version 1.0
 */
public class EmpSearchTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	/**
	 *根据人员权限查询人员
	 */
	private final String PERSON_SEARCH    = "1";
	/**
	 *获得人员子集
	 */
	private final String SUB_SET          = "2"; 
	/**
	 *获得人员子集详细信息
	 */
	private final String SUB_SET_DETAILED = "3"; 
	/**
	 *设置查询
	 */
	private final String SET_PRE_QUERY    = "4"; 
	/**
	 *员工明显——员工联系方式
	 */
	private final String PERSON_CONTAC    = "5";
	/**
	 *获得人员业务列表
	 */
	private final String PERSON_BUSINESS  = "6";
	/**
	 *本人资料
	 */
	private final String PERSON_SELF      = "7";
	/**
	 *获取便捷查询hint
	 */
	private final String HINTINFO         = "8";
	/**
	 *查询常用条件
	 */
	private final String SET_COMMON_QUERY = "9";
	/**
	 *通过扫描出的工号、身份证、考勤卡号查询员工
	 */
	private final String QUERYA0100BYSCAN = "10";

	
	public void execute() throws GeneralException {
		
		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			
			hm.remove("transType");
			hm.remove("message");
			hm.remove("succeed");
			
			if (transType == null) {
				message = ResourceFactory.getProperty("mobileapp.emp.error.transTypeError");
				hm.put("message", message);
				return;
			}
			
			String[] trans = transType.split(",");
			boolean result = false;
			for(int i=0;i<trans.length;i++){
				/* 不同业务流程分支点*/
				if(PERSON_SEARCH.equals(trans[i]))
					result = doPERSON_SEARCH(hm);
				else if(SUB_SET.equals(trans[i]))
					result = doSUB_SET(hm);
				else if(SUB_SET_DETAILED.equals(trans[i]))
					result = doSUB_SET_DETAILED(hm);
				else if(SET_PRE_QUERY.equals(trans[i]))
					result = doSET_PRE_QUERY(hm);
				else if(PERSON_CONTAC.equals(trans[i]))
					result = doPERSON_CONTAC(hm);
				else if(PERSON_BUSINESS.equals(trans[i]))
					result = doPERSON_BUSINESS(hm);
				else if(PERSON_SELF.equals(trans[i]))
					result = doPERSON_SELF(hm);
				else if(HINTINFO.equals(trans[i]))
					result = doHINTINFO(hm);
				else if(SET_COMMON_QUERY.equals(trans[i]))
					result = doSET_COMMON_QUERY(hm);
				else if(QUERYA0100BYSCAN.equals(trans[i]))
					result = doQUERYA0100BYSCAN(hm);
			}
			
			if(result){
				succeed = "true";
			}else{
				message = ResourceFactory.getProperty("mobileapp.emp.error.transTypeError");
				hm.put("message", message);
			}
			hm.put("transType", transType);
			
		} catch (Exception e) {
			succeed = "false";
			if(message.trim().length()==0){
			    message = ResourceFactory.getProperty("mobileapp.error");
			}
            hm.put("message", message);
			e.printStackTrace();
		}finally{
			hm.put("succeed", succeed);
		}

	}

	private boolean doQUERYA0100BYSCAN(HashMap hm) throws GeneralException, SQLException {
		String scanResult = (String)hm.get("scanResult");
		Pattern pattern = Pattern.compile("[0-9]*"); 
		Matcher isNum = pattern.matcher(scanResult);
		String code128c = "";
		if(isNum.matches()&&scanResult.startsWith("0")){
			code128c = scanResult.substring(1);
		}
		String mobileUniqueField = SystemConfig.getPropertyValue("mobile_unique_field");
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		String dbpre = "Usr";
		List dbpres = this.getNbaseList();
		for(int i=0,s=dbpres.size();i<s;i++){
			dbpre = (String)dbpres.get(i);
			if(i!=0){
				sql.append(" union all ");
			}
			if(mobileUniqueField.length()==0){
				sql.append("select distinct " + dbpre + "a01.a0100,'" + dbpre + "' dbpre from "+dbpre+"A01 where 1=2 ");
				FieldItem item = DataDictionary.getFieldItem("e0127");
				if(item!=null&& "1".equals(item.getUseflag())){//员工工号
					sql.append(" or e0127='"+scanResult+"'");
					if(code128c.length()>0){
						sql.append(" or e0127='"+code128c+"'");
					}
				}
				item = DataDictionary.getFieldItem("a0177");
				if(item!=null&& "1".equals(item.getUseflag())){//身份证
					sql.append(" or a0177='"+scanResult+"'");
					if(code128c.length()>0){
						sql.append(" or a0177='"+code128c+"'");
					}
				}
				item = DataDictionary.getFieldItem("c01tc");
				if(item!=null&& "1".equals(item.getUseflag())){//考勤卡号
					sql.append(" or c01tc='"+scanResult+"'");
					if(code128c.length()>0){
						sql.append(" or c01tc='"+code128c+"'");
					}
				}
			}else{
				sql.append("select distinct " + dbpre + "a01.a0100 a0100,'" + dbpre + "' dbpre from "+dbpre+"A01 where  ");
				sql.append(mobileUniqueField+"='"+scanResult+"'");
				if(code128c.length()>0){
					sql.append(" or "+mobileUniqueField+"='"+code128c+"'");
				}
			}
		}
		
		this.frowset = dao.search(sql.toString());
		String dbprea0100="";
		int i=0;
		while(this.frowset.next()){
			i++;
			dbprea0100=this.frowset.getString("dbpre")+this.frowset.getString("a0100");
		}
		if(i>1){//能查出多条也是异常情况
			dbprea0100="";
		}
		hm.put("scanResult", scanResult);
		hm.put("dbprea0100", dbprea0100);
		return true;
	}

	private boolean doSET_COMMON_QUERY(HashMap hm) {
		// 获取常用查询
		SearchInformationClassBo infobo = new SearchInformationClassBo(userView, this.frameconn);
		infobo.getLexpr(hm);
		return true;
	}

	private boolean doHINTINFO(HashMap hm) {
		String generalmessage =ResourceFactory.getProperty("label.title.name");
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
		FieldItem item = DataDictionary.getFieldItem(onlyname);
		if (item != null&&!"a0101".equalsIgnoreCase(onlyname)&&!"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
			generalmessage+="\\"+item.getItemdesc();
		}
		String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		if(pinyin_field!=null&&!pinyin_field.equals(onlyname)){//去除重复
			item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
			if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag()))&&!"a0101".equalsIgnoreCase(pinyin_field)&&!"0".equals(userView.analyseFieldPriv(item.getItemid())))
				generalmessage+="\\"+item.getItemdesc();
		}
		
		hm.put("hintinfo", generalmessage);
		return true;
	}

	private boolean doSUB_SET_DETAILED(HashMap hm) {
		SearchInformationClassBo searchInformationClassBo = new SearchInformationClassBo(userView, this.frameconn);
		// 获得人员库前缀
		String dbpre = (String) hm.get("mNbase");
		if(dbpre==null)
			dbpre = (String)hm.get("dbpre");
		// 获得人员编号
		String a0100 = (String) hm.get("mA0100");
		if(a0100==null)
			a0100 = (String)hm.get("a0100");
		// 获得人员库后缀
		String setid = (String) hm.get("setid");
		//查询人员子集详细信息
		List subSetDetailedList = searchInformationClassBo.getSubSetDetailed(dbpre, a0100, setid);
		hm.put("subSetDetailedList", subSetDetailedList);
		return true;
	}

	private boolean doPERSON_SELF(HashMap hm) {
		String dbpre = (String)hm.get("dbpre");
		String a0100 = (String)hm.get("a0100");
		String url = (String) hm.get("url");
		SearchInformationClassBo searchInformationClassBo = new SearchInformationClassBo(userView, this.frameconn);
		searchInformationClassBo.setUrl(url);
		List personInfoList = searchInformationClassBo.getSelfInfo(dbpre, a0100);
		hm.put("personInfoList", personInfoList);
		return true;
	}

	private boolean doPERSON_BUSINESS(HashMap hm) {
		String dbpre = (String)hm.get("dbpre");
		String a0100 = (String)hm.get("a0100");
		SearchInformationClassBo searchInformationClassBo = new SearchInformationClassBo(userView, this.frameconn);
		Map mBusesslist = new HashMap();
		//业务信息增加权限判断
		Tools t = new Tools();
		if(t.hasFuncNode("0K041")){
			if(this.userView.hasTheFunction("0K041")){
				mBusesslist.put("leader", searchInformationClassBo.getLeaderInfo(dbpre, a0100));
			}
			if(this.userView.hasTheFunction("0K042")){
				mBusesslist.put("staff", searchInformationClassBo.getStaffInfo(dbpre, a0100));
			}
			if(this.userView.hasTheFunction("0K043")){
				mBusesslist.put("staff_add", searchInformationClassBo.getStaffAddInfo(dbpre, a0100));
			}
			if(this.userView.hasTheFunction("0K044")){
				mBusesslist.put("staff_minus", searchInformationClassBo.getStaffMinusInfo(dbpre, a0100));
			}
		}else{
			mBusesslist.put("leader", searchInformationClassBo.getLeaderInfo(dbpre, a0100));
			//获取最近入职人员人数
			mBusesslist.put("staff_add", searchInformationClassBo.getStaffAddInfo(dbpre, a0100));
			//获取最近离职员工人数
			mBusesslist.put("staff_minus", searchInformationClassBo.getStaffMinusInfo(dbpre, a0100));
			mBusesslist.put("staff", searchInformationClassBo.getStaffInfo(dbpre, a0100));
		}
		hm.put("mBusesslist", mBusesslist);
		return true;
	}

	private boolean doPERSON_CONTAC(HashMap hm) {
		String a0100 = (String)hm.get("a0100");
		String dbpre = (String)hm.get("dbpre");
		SearchInformationClassBo infobo = new SearchInformationClassBo(userView, this.frameconn);
		//Map contact = infobo.getContact(dbpre, a0100);
		List contacts = infobo.getContacts(dbpre, a0100);
		hm.put("contact", contacts);
		return true;
	}

	private boolean doSET_PRE_QUERY(HashMap hm) throws GeneralException {
		List libraryList = this.searchPreLiist();					
		hm.put("libraryList", libraryList);
		return true;
	}

	private boolean doSUB_SET(HashMap hm) {
		SearchInformationClassBo searchInformationClassBo = new SearchInformationClassBo(userView, this.frameconn);
		// 获得人员库前缀
		String mNbase = (String) hm.get("mNbase");
		if(mNbase==null)
			mNbase = (String)hm.get("dbpre");
		// 获得人员编号
		String mA0100 = (String) hm.get("mA0100");
		if(mA0100==null)
			mA0100 = (String)hm.get("a0100");
		//查询人员子集列表
		List subSetNameList = searchInformationClassBo.getSubSetInfo(mNbase,mA0100);
//		List subSetNameList = new ArrayList();
		hm.put("subSetNameList", subSetNameList);
		//返回前台业务标示
		/**加载第一个子集的详细信息*/
		if(subSetNameList.size()>0){
			Map map = (Map) subSetNameList.get(0);
			String setid = (String) map.get("id");
			//查询人员子集详细信息
			List subSetDetailedList = searchInformationClassBo.getSubSetDetailed(mNbase, mA0100, setid);
			hm.put("subSetDetailedList", subSetDetailedList);
		}
		return true;
	}

	private boolean doPERSON_SEARCH(HashMap hm) throws GeneralException {
		String pageIndex = (String) hm.get("pageIndex");
		String pageSize = (String) hm.get("pageSize");
		String url = (String) hm.get("url");
		String infotype = (String) hm.get("infotype");
		String keywords = (String) hm.get("keywords");
		if(keywords==null){
		    keywords="";
		}
		if(infotype==null||infotype.trim().length()==0){
		    infotype = "1";
		}
		String querytype = (String)hm.get("querytype");
		querytype = querytype==null||querytype.length()==0?"0":querytype;
		// 第几页
		pageIndex = pageIndex == null ? "1" : pageIndex;
		// 每页条数
		pageSize = pageSize == null ? "10" : pageSize;
		// 人员库
		String mpre = (String) hm.get("mpre");
		List prelist = userView.getPrivDbList();
		if (prelist.size() == 0) {
			String message = ResourceFactory
					.getProperty("current.user.dbpre.no");
			throw GeneralExceptionHandler.Handle(new Exception(
					message));
		}
		
		mpre = mpre == null || mpre.length() == 0 ? prelist.get(0)
				.toString() : mpre;
		// 是否模糊
		String mlike = (String) hm.get("mlike");
		mlike = mlike == null || mlike.length() == 0 ? "false"
				: mlike;
		// 是否历史
		String mhistory = (String) hm.get("mhistory");
		mhistory = mhistory == null || mhistory.length() == 0 ? "false"
				: mhistory;
		// 是否二次
		String msecond = (String) hm.get("msecond");
		msecond = msecond == null || msecond.length() == 0 ? "true"
				: msecond;

		// 快速查询
		String queryValue = (String) hm.get("queryValue");
		String selectField = (String) hm.get("selectField");

		// 常用查询
		String lexprid = (String) hm.get("lexprid");

		
		//被点击人员的人员编号
		String prea0100 = (String) hm.get("prea0100");

		SearchInformationClassBo searchInformationClassBo = new SearchInformationClassBo(userView, this.frameconn);
		searchInformationClassBo.setMhistory(mhistory);
		searchInformationClassBo.setMlike(mlike);
		searchInformationClassBo.setMpre(mpre);
		searchInformationClassBo.setMsecond(msecond);
		searchInformationClassBo.setQueryValue(queryValue);
		searchInformationClassBo.setSelectField(selectField);
		searchInformationClassBo.setLexprid(lexprid);
		searchInformationClassBo.setPageIndex(pageIndex);
		searchInformationClassBo.setPageSize(pageSize);
		searchInformationClassBo.setUrl(url);
		searchInformationClassBo.setInfotype(infotype);
		searchInformationClassBo.setQuerytype(querytype);
		searchInformationClassBo.setPrea0100(prea0100);
		searchInformationClassBo.setKeywords(keywords);
		
		/**---------统计分析---------*/
		//维度
		String statisDim = (String) hm.get("statisDim");
		statisDim = statisDim == null || statisDim.length() == 0 ? "": statisDim;
		if(statisDim.length()==1){
			//点击的ID
			String statisSLegendID = (String) hm.get("statisSLegendID");
			statisSLegendID = statisSLegendID == null || statisSLegendID.length() == 0 ? "": statisSLegendID;
			//组织机构编号
			String statisOgr = (String) hm.get("statisOgr");
			statisOgr = statisOgr == null || statisOgr.length() == 0 ? "": statisOgr;
			//人员1，单位2，岗位3
			String statisInfokind = (String) hm.get("statisInfokind");
			statisInfokind = statisInfokind == null || statisInfokind.length() == 0 ? "": statisInfokind;
			
			searchInformationClassBo.setStatisDim(statisDim);
			searchInformationClassBo.setStatisOgr(statisOgr);
			searchInformationClassBo.setStatisSLegendID(statisSLegendID);
			searchInformationClassBo.setStatisInfokind(statisInfokind);
		}
		
		if("2".equals(querytype)){//快速查询
			List fieldlist = (List)hm.get("fieldlist");
			FieldItem fielditem=null;
			ArrayList list = new ArrayList();
			for(int i=0,n=fieldlist.size();i<n;i++){
				MorphDynaBean bean = (MorphDynaBean)fieldlist.get(i);
				fielditem = new FieldItem();
                fielditem.setItemid((String)bean.get("itemid"));
                //fielditem.setSequenceable(true);
                //fielditem.setC_rule(Integer.parseInt(bean.get("c_rule").toString()));
                //fielditem.setSeqprefix_field((String)bean.get("seqprefix_field"));
                //fielditem.setByprefix(Boolean.parseBoolean(bean.get("byprefix").toString()));
                //fielditem.setPrefix_field_len(Integer.parseInt(bean.get("prefix_field_len").toString()));
                //fielditem.setSequencename((String)bean.get("sequencename"));
                fielditem.setFieldsetid((String)bean.get("fieldsetid"));
                fielditem.setItemdesc((String)bean.get("itemdesc"));
                fielditem.setCodesetid((String)bean.get("codesetid"));
                fielditem.setExplain((String)bean.get("explain"));
                //fielditem.setDecimalwidth(Integer.parseInt(bean.get("decimalwidth").toString()));
                //fielditem.setDisplaywidth(Integer.parseInt(bean.get("displaywidth").toString()));
                //fielditem.setItemlength(Integer.parseInt(bean.get("itemlength").toString()));
                fielditem.setItemtype((String)bean.get("itemtype"));
                fielditem.setUseflag((String)bean.get("useflag"));
                //fielditem.setInputtype(Integer.parseInt(bean.get("inputtype").toString()));
                //fielditem.setFillable(Boolean.parseBoolean(bean.get("fillable").toString()));
                fielditem.setState((String)bean.get("state"));
                fielditem.setVisible(Boolean.parseBoolean(bean.get("visible").toString()));
                fielditem.setValue((String)bean.get("value"));
                fielditem.setViewvalue((String)bean.get("viewvalue"));
                list.add(fielditem);
			}
			searchInformationClassBo.setFieldlist(list);
		}
		List personInfoList = searchInformationClassBo.searchInfoList();
		
		hm.put("personInfoList", personInfoList);
		return true;
	}

	/**
	 * 
	 * @Title: searchPreLiist   
	 * @Description:获取权限内人员库
	 * @throws GeneralException 
	 * @return List
	 */
	private List searchPreLiist() throws GeneralException {
		List libraryList = new ArrayList();
		try {
			// 获得库前缀
			List prelist = this.getUserView().getPrivDbList();
			if (prelist != null) {
				String libraryName = "";
				StringBuffer sBuffer = new StringBuffer();
				Map libraryMap = new HashMap();
				for (int i = 0, n = prelist.size(); i < n; i++) {
					// 根据库前缀查询库名
					libraryName = AdminCode.getCodeName("@@", (String) prelist.get(i));
					libraryMap.put("pre", (String) prelist.get(i));
					libraryMap.put("name", libraryName);
					libraryList.add(libraryMap);
					libraryMap = new HashMap();
					// 追加一个全部人员库
					sBuffer.append(prelist.get(i));
					sBuffer.append("`");
				}
				libraryMap.put("pre", sBuffer.toString());
				libraryMap.put("name", ResourceFactory.getProperty("mobileapp.emp.allPersonnel"));
				libraryList.add(libraryMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return libraryList;
	}
	
	/** 
	 * 
	 * @Title: getNbaseList
	 * @Description:获得认证人员库
	 * @param @return
	 * @return ArrayList
	 * @throws GeneralException
	 */
	private List getNbaseList() throws GeneralException {
		List list = new ArrayList();
		RowSet rs = null;
		try {
			/**登录参数表*/
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			if (login_vo != null) {
				String[] loginList = login_vo.getString("str_value").split(",");
				for (int i = 0; i < loginList.length; i++) {
					// 排除保存的空库
					if (loginList[i].length() >= 1)
						list.add(loginList[i]);
				}
			}
/**
 * 		StringBuffer sBuffer = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
			// 使应聘人才库中设置的人员库在通讯录中不显示
			RecordVo strvalue = ConstantParamter.getConstantVo("ZP_DBNAME");
			sBuffer.append("select pre from dbname");
			if (strvalue != null)
				sBuffer.append(" where pre <> '" + strvalue.getString("str_value") + "'");
			sBuffer.append(" order by DbId");
			rs = dao.search(sBuffer.toString());
			sBuffer.setLength(0);
			while (rs.next()) {
				list.add(rs.getString("pre"));
			}
*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}

}
