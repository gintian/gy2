package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitGzSpCollectTrans 
 * 类描述： 初始化薪资汇总审批界面参数
 * 创建人：zhaoxg
 * 创建时间：Dec 16, 2015 6:18:31 PM
 * 修改人：zhaoxg
 * 修改时间：Dec 16, 2015 6:18:31 PM
 * 修改备注： 
 * @version
 */
public class InitGzSpCollectTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			HashMap hm=(HashMap)this.getFormHM();
			GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
			String salaryid = (String) hm.get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String imodule = (String) hm.get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
			imodule = PubFunc.decrypt(SafeCode.decode(imodule));
			String returnflag = (String) hm.get("returnflag");//menu:左侧菜单  collect：明细表  否则待办noreturn 则不显示返回按钮
		    String encryptParam = (String) this.getFormHM().get("encryptParam");
		    if(encryptParam!=null&&encryptParam.length()>0){
		    	
		    	if(StringUtils.isNumeric(this.getValByStr(encryptParam, "salaryid"))){//如果连接里面的salaryid不加密直接传数字也支持，但是判断下权限 zhaoxg add 2016-10-28
		    		salaryid = this.getValByStr(encryptParam, "salaryid");
		    		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		    		safeBo.isSalarySetResource(salaryid,imodule);
		    	}else
		    		salaryid = PubFunc.decrypt(SafeCode.decode(this.getValByStr(encryptParam, "salaryid")));  
		    	returnflag = this.getValByStr(encryptParam, "returnflag");
		    }else {
		    	CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);//从待办进来可能有问题，判断权限 sunjian 2017-9-13
	    		safeBo.isSalarySetResource(salaryid,imodule);
		    }
			String records = (String) hm.get("records");
			if(records!=null&&records.length()>0){
				records = records.substring(2);
			}
			String bosdate = (String) hm.get("bosdate");
			String count = (String) hm.get("count");
			String date_count = "";
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
			ArrayList itemList=gzbo.getSalaryItemList("",""+salaryid,1);
			gzbo.SalarySet(itemList); //判断哪些字段改变了需要同步
//			String comflag= gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD);
//		    String priv_mode="0";//是否出现数据比对菜单
//		    if(comflag!=null&&!comflag.equals(""))
//		    	priv_mode = "1";
		    HashMap buttonMap = new HashMap();//按钮一些个性参数的集合，为了不在方法中多传递参数
		    buttonMap.put("returnflag", returnflag);
//		    buttonMap.put("priv_mode", priv_mode);
		    
			String selectcollectPoint = getNumitem(salaryid);//ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD);//汇总指标中已选指标
			String collectPoint = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field");//汇总指标
			if(collectPoint==null||collectPoint.length()==0){
				collectPoint = "UNUM";
			}
			String title = gzbo.getTemplatevo().getString("cname");
			if(bosdate==null||bosdate.length()==0){
				HashMap dateMap = spbo.getBosdateAndCount(Integer.parseInt(salaryid));
				bosdate = (String) dateMap.get("bosdate");
				bosdate = bosdate==null?"":bosdate;
				count = (String) dateMap.get("count");
				count = count==null?"":count;
				if(bosdate.length()>0&&count.length()>0)
					date_count = SafeCode.encode(PubFunc.encrypt(bosdate))+"#"+SafeCode.encode(PubFunc.encrypt(count));
			}else{
				date_count = bosdate+"#"+count;//传过去的bosdate和count已经SafeCode.encode(PubFunc.encrypt())
				bosdate = PubFunc.decrypt(SafeCode.decode(bosdate));
				count = PubFunc.decrypt(SafeCode.decode(count));
			}
			String showDate = null;
			if(StringUtils.isNotBlank(bosdate) && bosdate.contains(".")) {
				String[] temp = bosdate.split("\\.");
				if(temp.length>1){
					showDate = temp[0]+"年"+temp[1]+"月"+"第"+count+"次";
				}
			}
				
			String codeset = spbo.getCodeSet(salaryid, collectPoint);
			String[] value = selectcollectPoint.split(",");
			StringBuffer str = new StringBuffer();
			str.append("['id','text','sp_flag','desc','num'");
			for(int i=0;i<value.length;i++){
				str.append(",'");
				str.append(value[i].toUpperCase()+"'");
			}
			str.append("]");
			String tar=this.userView.getBosflag();
			String  verify_ctrl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
			if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
				verify_ctrl="0";
			if("1".equals(verify_ctrl))
			{
				String verify_ctrl_sp=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_sp");//审核控制，审批不是发放
				if(verify_ctrl_sp!=null&&verify_ctrl_sp.length()>0)
					verify_ctrl=verify_ctrl_sp;
			}
			String isControl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				String amount_ctrl_sp=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_sp");
				if(amount_ctrl_sp!=null&&amount_ctrl_sp.trim().length()>0)
					isControl=amount_ctrl_sp;				
			}
			String ctrlType = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type");//1强行控制 0仅提示
			if(ctrlType==null||ctrlType.trim().length()==0)
				ctrlType="1";
			
			String relation_id=gzbo.getSpRelationId();
			String  sp_actor_str="";
			if(relation_id.length()>0)
				sp_actor_str=gzbo.getSpActorStr(relation_id,1);
			
			String isSendMessage="0";
			if(ctrl_par.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrl_par.getValue(SalaryCtrlParamBo.NOTE,"mail")))
				isSendMessage="1";
			if(ctrl_par.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrl_par.getValue(SalaryCtrlParamBo.NOTE,"sms")))
				isSendMessage="1";
			
			String subNoShowUpdateFashion=gzbo.getLprogramAttri("no_show",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0";
			
		    boolean isApprove=true;
		    boolean isAppeal=true;
		    if(relation_id!=null&&relation_id.length()>0&&sp_actor_str.length()>0)
		    	isApprove=false;
		    if(relation_id!=null&&relation_id.length()>0&&sp_actor_str.length()==0)
		    	isAppeal=false;
			
		    if(imodule==null||imodule.length()==0){
		    	ContentDAO dao=new ContentDAO(this.frameconn);
		    	String sql = "select cstate from salarytemplate where salaryid="+salaryid+"";
		    	RowSet rs = dao.search(sql);
		    	if(rs.next()){
		    		imodule = "1".equals(rs.getString("cstate"))?rs.getString("cstate"):"0";
		    	}
		    }
		    //薪资审批页面业务日期显示
		    ArrayList spOperationDateList = spbo.getOperationDateListSP(Integer.parseInt(salaryid));
		    ArrayList spOperationCoundList = spbo.getOperationCoundList(date_count,salaryid);
			this.getFormHM().put("spOperationDateList", spOperationDateList);
			this.getFormHM().put("spOperationCoundList", spOperationCoundList);
		    this.getFormHM().put("setId", "salarysp_"+salaryid);//栏目设置 唯一标识 subModuleId
		    this.getFormHM().put("mainPulicPlan",(!"1".equals(imodule)&&this.userView.hasTheFunction("3240329"))
		    		||("1".equals(imodule)&&this.userView.hasTheFunction("3250329"))?"1":"0");//汇总页面栏目设置公有方案权限
		    
		    this.getFormHM().put("detailPulicPlan", (!"1".equals(imodule)&&this.userView.hasTheFunction("3240330"))
		    		||("1".equals(imodule)&&this.userView.hasTheFunction("3250330"))?"1":"0");//明细页面栏目设置公有方案权限
		    
			this.getFormHM().put("isSendMessage", isSendMessage);			
			this.getFormHM().put("buttons", spbo.getButtons(salaryid,collectPoint,tar,isAppeal,isApprove,buttonMap,sp_actor_str,imodule));
			this.getFormHM().put("bosflag",tar);//版本
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("salaryid_encrypt", SafeCode.encode(PubFunc.encrypt(salaryid)));
			this.getFormHM().put("collectvalue", str.toString());
			this.getFormHM().put("collectPoint", collectPoint);
			this.getFormHM().put("selectcollectPoint", selectcollectPoint);
			this.getFormHM().put("columns", spbo.getColumns(salaryid,selectcollectPoint,collectPoint));
			this.getFormHM().put("codeset", codeset);
			this.getFormHM().put("isTotalControl", isControl);
			this.getFormHM().put("verify_ctrl",verify_ctrl);
			this.getFormHM().put("ctrlType", ctrlType);
			this.getFormHM().put("sp_actor_str",sp_actor_str);
			this.getFormHM().put("gz_module",imodule);
			this.getFormHM().put("returnflag", returnflag);
			this.getFormHM().put("subNoShowUpdateFashion",subNoShowUpdateFashion);
			this.getFormHM().put("records", records);
			this.getFormHM().put("bosdate", SafeCode.encode(PubFunc.encrypt(bosdate)));
			this.getFormHM().put("count", SafeCode.encode(PubFunc.encrypt(count)));
			this.getFormHM().put("imodule", SafeCode.encode(PubFunc.encrypt(imodule)));
			this.getFormHM().put("viewtype", SafeCode.encode(PubFunc.encrypt("1")));
			this.getFormHM().put("date_count", date_count);
			this.getFormHM().put("title", title);
			this.getFormHM().put("showDate", StringUtils.isBlank(showDate)?"":showDate);


			/**
			 * 获取常用报表
			 */
			if((!"1".equals(imodule)&&this.userView.hasTheFunction("324031002"))||("1".equals(imodule)&&this.userView.hasTheFunction("325031002"))){
				SalaryReportBo salaryReportBo=new SalaryReportBo(this.getFrameconn(),salaryid,this.getUserView());
				ArrayList list=salaryReportBo.listCommonReport(imodule,"1");
				this.getFormHM().put("commonreportlist",list);
			}


		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获取薪资类别下所有数值型指标
	 * @param salaryid
	 * @param rightvalue
	 * @return
	 * @throws GeneralException 
	 */
	private String getNumitem(String salaryid) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer str = new StringBuffer();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
		sql.append(" and itemtype in ('N'");
		sql.append(")");
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		ArrayList list = new ArrayList();
		try {
			dylist = dao.searchDynaList(sql.toString());
			LinkedHashMap columnMap=new LinkedHashMap();
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if((this.userView.isSuper_admin() || !"0".equals(this.userView.analyseFieldPriv(dynabean.get("itemid").toString().trim()))) && !"a00z1".equalsIgnoreCase(dynabean.get("itemid").toString().trim())&&!"a0000".equalsIgnoreCase(dynabean.get("itemid").toString().trim())&&!"a00z3".equalsIgnoreCase(dynabean.get("itemid").toString().trim())){
					str.append(",");
					str.append(dynabean.get("itemid").toString());
					FieldItem fi = DataDictionary.getFieldItem(dynabean.get("itemid").toString());
					ColumnsInfo info = new ColumnsInfo(fi);
					list.add(info);
					columnMap.put(dynabean.get("itemid").toString().trim().toLowerCase(), info);
				}
			}
			if(str.length()==0){
				str.append(",");
			}
			TableDataConfigCache config = new TableDataConfigCache();
			config.setTableColumns(list);
			
			config.setColumnMap(columnMap);
			
			Integer pagesize = new Integer(20);
			config.setPageSize(pagesize);
			userView.getHm().put("salarysp_"+salaryid, config);
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str.toString().substring(1);
	}
	/**
	 * 
	 * @Title: getValByStr   
	 * @Description:获取加密参数中的链接
	 * @param @param url
	 * @param @param str
	 * @param @return 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	private String getValByStr(String url, String str) {
		String val = "";
		try {
			url = PubFunc.decrypt(url);
			String _url = url.substring(1);
			String[] strs = _url.split("&");
		    for(int i = 0; i < strs.length; i++) {
		    	String param = strs[i];
		    	String[] params=param.split("=");
		    	if (params.length>1&&params[0].equalsIgnoreCase(str))
		    		val=params[1];
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
}
