package com.hjsj.hrms.transaction.mobileapp.statis;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * <p> Title: StatisAnalysisTrans </p>
 * <p> Description:统计分析交易类 </p>
 * <p> Company: hjsj </p>
 * <p> create time: 2013-11-27 下午1:39:42 </p>
 * 
 * @author yangj
 * @version 1.0
 */
public class StatisAnalysisTrans extends IBusiness {

	private static final long serialVersionUID = 1L;	
	private UserView userView;
	private Connection conn;
	
	/** 设置范围业务 */
	private final String SET_SCOPE = "1";
	/** 统计条件分类,一级目录 */
	private final String ONE_DIRECTORY = "2";
	/** 统计条件分类,二级目录 */
	private final String TWO_DIRECTORY = "3";
	/**维度展示*/
	private final String DIMENSIONAL = "4";
	
	public void execute() throws GeneralException {
		String message = "";
		String succeed = "false";
		HashMap hm = this.getFormHM();
		try {
			userView = this.getUserView();
			conn = this.getFrameconn();
			String transType = (String) hm.get("transType");
			hm.remove("transType");
			hm.remove("message");
			hm.remove("succeed");
			// 不同业务流程分支点
			if (transType != null) {
				StatisAnalysisBo statisAnalysisBo = new StatisAnalysisBo(userView, conn);
				if (SET_SCOPE.equals(transType)) {//统计分析设置范围
					// 人员库
					List libraryList = statisAnalysisBo.getPersonnel();
					hm.put("libraryList", libraryList);
					// 常用查询,1代表常用查询
					List condStatList = statisAnalysisBo.getCondStatList(1);
					hm.put("condStatList", condStatList);
					hm.put("transType", SET_SCOPE);
					succeed = "true";
				} else if (ONE_DIRECTORY.equals(transType)) {// 统计分析一级目录
					List mGruops = statisAnalysisBo.getOneDirectory();
					if (mGruops != null) {
						hm.put("mGruops", mGruops);
						// 默认展示第一个的二级目录
						/*HashMap map = (HashMap) mGruops.get(0);
						List mChilds = statisAnalysisBo.getTwoDirectory((String) map.get("categories"),1);
						hm.put("mChilds", mChilds);*/
					}
					hm.put("transType", ONE_DIRECTORY);
					succeed = "true";
				} else if (TWO_DIRECTORY.equals(transType)) {// 统计分析二级目录
					String categories = (String) hm.get("categories");
					List mChilds = statisAnalysisBo.getTwoDirectory(categories);
					hm.put("mChilds", mChilds);
					hm.put("transType", TWO_DIRECTORY);
					succeed = "true";
				} else if (DIMENSIONAL.equals(transType)) {// 维度图展示
					//选择的统计项ID
					String condid = (String) hm.get("condid");
					String dim = (String) hm.get("dim");
					String infokind = (String) hm.get("infokind");
					if ("1".equals(infokind)) {
						 //选择的人员库 Usr,OTh, 
						String mBase = (String) hm.get("mBase");
						mBase = mBase==null||mBase.length()==0?"":mBase;
						statisAnalysisBo.setmBase(mBase);
						
						// 常用条件 
						String mCondID = (String) hm.get("mCondID");
						mCondID = mCondID==null||mCondID.length()==0?"":mCondID;
						statisAnalysisBo.setmCondID(mCondID);
										
						//所选的组织机构 0101,101,0101 
						String mOrg =(String) hm.get("mOrg");
						mOrg = mOrg==null||mOrg.length()==0?"":mOrg;
						statisAnalysisBo.setmOrg(mOrg);
						
						//是否需要上次查询结果
						String  lastResult = (String) hm.get("lastResult");
						lastResult = lastResult==null||lastResult.length()==0?"":lastResult;
						statisAnalysisBo.setLastResult(lastResult);	
					}
					if("1".equals(dim)){//一维
						List oneList = statisAnalysisBo.getOneDimStatisChart(condid,infokind);
						hm.put("oneList", oneList);
					}else if("2".equals(dim)){//二维
					    statisAnalysisBo. getTwoDimStatisTable(condid,infokind,hm);
					}				
					succeed = "true";
				} else {
					message = ResourceFactory.getProperty("mobileapp.statis.error.transTypeError");
					hm.put("message", message);
				}
			} else {
				message = ResourceFactory.getProperty("mobileapp.statis.error.transTypeError");
				hm.put("message", message);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			succeed = "false";
			String errorMsg = ex.toString();
			int index_i = errorMsg.indexOf("description:");
			message = errorMsg.substring(index_i + 12);
			hm.put("message", message);
		} finally {
			hm.put("succeed", succeed);
		}

	}

}
