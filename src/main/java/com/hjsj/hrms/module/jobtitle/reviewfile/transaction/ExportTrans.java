package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ExportBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * <p>
 * Title: ExportTrans
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-9-15 下午3:10:22
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ExportTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		try {
			String w0301 = (String) this.getFormHM().get("w0301");
			w0301 = PubFunc.decrypt(w0301.substring(6));
			String type = (String) this.getFormHM().get("type");
			String usetype = (String) this.getFormHM().get("usetype");
			
			GenerateAcPwBo generateAcPwBo = new GenerateAcPwBo(this.frameconn,this.userView);
			ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();
			selList = generateAcPwBo.getSelectList("1", new ArrayList());// 实际选中的数据
			this.exportReviewAccounts(w0301, selList, usetype, type);
			
			// 以下为针对所选人员进行导出的逻辑，现删除，代码保留。chent start
			/*ArrayList idlist = (ArrayList) this.getFormHM().get("idlist");
			String isSelectAll = (String) this.getFormHM().get("isSelectAll");
			String usetype = (String) this.getFormHM().get("usetype");
			isSelectAll = "".equals(isSelectAll) ? "0" : isSelectAll;
			GenerateAcPwBo generateAcPwBo = new GenerateAcPwBo(this.frameconn,this.userView);
			ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();
			selList = generateAcPwBo.getSelectList(isSelectAll, idlist);// 实际选中的数据
			boolean flag = true;//选中数据是否是同一会议
			for (int i = 0; i < selList.size(); i++) {
				if (!selList.get(0).get("w0301").equals(selList.get(i).get("w0301"))) {
					flag = false;
					break;
				}
			}
			
			String w0301 = selList.get(0).get("w0301");// 会议id
			if (flag)
				this.exportReviewAccounts(w0301,selList,usetype);
			else 
				this.getFormHM().put("msg", "请选择相同会议的数据进行导出！");*/
			// 以上为针对所选人员进行导出的逻辑，现删除，代码保留。chent end

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 
	 * @param w0301
	 * @param reviewPersonIds
	 * 			当前申报人主键序号ID 
	 * @param usetype
	 * 			导出的帐号类型 1 = 查看帐号 2 = 投票帐号
	 * @throws Exception
	 */
	private void exportReviewAccounts(String w0301,ArrayList<HashMap<String, String>> selList,String usetype, String type)throws Exception{
		ExportBo exBo = new ExportBo(this.getFrameconn(), this.userView);
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());// 实例化导出Excel工具类
		excelUtil.setRowHeight((short)1800);
		excelUtil.setProtect(true);//是否启用锁定页面,先启用，才能设置只读
		String reviewPersonIds = "";// 选中的评审编号，全选时为空
		for(HashMap<String, String> map:selList){
			String reviewPersonId = map.get("w0501");
			reviewPersonIds += "'" + reviewPersonId + "',";
		}
		if (StringUtils.isNotEmpty(reviewPersonIds))
			reviewPersonIds = reviewPersonIds.substring(0,reviewPersonIds.length() - 1);
			ArrayList<LazyDynaBean> firstHeadList = exBo.getAllSheetHeadList(usetype,type);// 第一页列头
			//当导出上级会议的帐号密码时，只能导出学院聘任组的成员 haosl 20160809
			if ("2".equals(type)) {// 学科组情况考虑 导出分为 学科组成员页 科目等页
				// 导出学科页签时，按照学科分类，每一次导出都是同一个会议，会议id相同，科目不同
				ArrayList<LazyDynaBean> grouplist = exBo.getProGroup(w0301);
				// 根据不同的学科组查询不同的页签
				for (int j = 0; j < grouplist.size(); j++) {
					LazyDynaBean bean = grouplist.get(j);
					String group_name = (String) bean.get("group_name"); // 学科组名称
					String group_id = (String) bean.get("group_id");
					ArrayList firstDataList = exBo.getAllDataList(firstHeadList, reviewPersonIds, type,group_id, w0301,usetype);// 第一页数据集合
						excelUtil.exportExcel(group_name, null,firstHeadList, firstDataList, null,0);// 输出第一页
					
					if (firstDataList.size() > 0){
						LazyDynaBean ldbean=null;
						HSSFPatriarch patriarch=excelUtil.getSheet().createDrawingPatriarch();
						for (int k = 0; k < firstDataList.size(); k++) {
						    ldbean=(LazyDynaBean)firstDataList.get(k);
						    LazyDynaBean tembeanName=(LazyDynaBean)ldbean.get("username");
			    			LazyDynaBean tembeanpwd=(LazyDynaBean)ldbean.get("pwd");
			    			String name=(String)tembeanName.get("content");
			    			String pwd=(String)tembeanpwd.get("content");
			    			byte[] outstream=exBo.getdimensional(name,pwd);
			    			HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,0,0,(short)2,k+1,(short)3,k+2);
			    			patriarch.createPicture(anchor,excelUtil.getWb().addPicture(outstream, HSSFWorkbook.PICTURE_TYPE_PNG));  
			    			anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
						}
					}
				}
			} else {
				ArrayList firstDataList = exBo.getAllDataList(firstHeadList, reviewPersonIds, type, null,w0301,usetype);// 评审帐号第一页数据集合
				
				String excelName = "";
				if("1".equals(type)){
					excelName = JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT;
					
				}else if("3".equals(type)){
					excelName = JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT;
					
				}else if("4".equals(type)){
					excelName = JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
					
				}
				if ("1".equals(type)||("2".equals(usetype) && "3".equals(type))||"4".equals(type)){//评委会 、同行专家、二级单位
					excelUtil.exportExcel(excelName, null, firstHeadList,firstDataList, null, 0);// 输出第一页
					
					if (firstDataList.size() > 0){
						LazyDynaBean ldbean=null;
						HSSFPatriarch patriarch=excelUtil.getSheet().createDrawingPatriarch();
						for (int k = 0; k < firstDataList.size(); k++) {
						    ldbean=(LazyDynaBean)firstDataList.get(k);
						    LazyDynaBean tembeanName=(LazyDynaBean)ldbean.get("username");
			    			LazyDynaBean tembeanpwd=(LazyDynaBean)ldbean.get("pwd");
			    			String uername=(String)tembeanName.get("content");
			    			String pwd=(String)tembeanpwd.get("content");
			    			byte[] outstream=exBo.getdimensional(uername,pwd);
			    			HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,0,0,(short)2,k+1,(short)3,k+2);
			    			patriarch.createPicture(anchor,excelUtil.getWb().addPicture(outstream, HSSFWorkbook.PICTURE_TYPE_PNG));  
			    			anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
						}
					}
				}
			}
		String fileName = "";
		if("1".equals(usetype)){//评审帐号第二页数据集合
			ArrayList<ColumnsInfo> columnsInfo = exBo.getColumnsInfo(this.userView);//haosl 20160812 20160812			
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("reviewFile");
			HashMap columnMap = catche.getColumnMap();
	            for(ColumnsInfo c : columnsInfo) {
	            	String itmeid = c.getColumnId();
	            	if(columnMap.containsKey(itmeid)) {
	            		ColumnsInfo column = (ColumnsInfo)columnMap.get(itmeid);
	            		c.setTextAlign(column.getTextAlign());
	            	}
	            }
			ArrayList<LazyDynaBean> mergedCellList = exBo.getMergedCellList(columnsInfo);// 第二页复合列头
			ArrayList<LazyDynaBean> secondheadList = exBo.getSecondSheetHeadList(columnsInfo, false,w0301);// 第二页列头
			ArrayList secondDataList = exBo.getSecondDataList(columnsInfo,secondheadList, reviewPersonIds, false);// 第二页数据集合
			// 去掉仅用作查询的W0501,W0301
			secondheadList.remove(secondheadList.size() - 1);
			secondheadList.remove(secondheadList.size() - 1);
			excelUtil.exportExcel(ResourceFactory.getProperty("zc_new.zc_reviewfile.fistSheetName"),mergedCellList, secondheadList,
					secondDataList,null, 1);// 输出第二页
			fileName = "审核账号和密码_" + this.userView.getUserName() +".xls";// 材料评审
		}else{
			fileName = "投票帐号和密码_" + this.userView.getUserName() +".xls";// 投票
		}
		excelUtil.exportExcel(fileName);// 导出表格
		this.getFormHM().put("fileName",PubFunc.encrypt(fileName));// 表格名传进前台
	}
	private void exportReviewAccounts_old(String w0301,ArrayList<HashMap<String, String>> selList,String usetype)throws Exception{
		ExportBo exBo = new ExportBo(this.getFrameconn(), this.userView);
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());// 实例化导出Excel工具类
		excelUtil.setRowHeight((short)1800);
		ArrayList<LazyDynaBean> typelist = exBo.getAllTypeList();//专家类型
		
		List<String> selTypeList = new ArrayList<String>();//所选数据的包含的投票阶段
		String reviewPersonIds = "";// 选中的评审编号，全选时为空
		for(HashMap<String, String> map:selList){
			if(StringUtils.isNotBlank(usetype) && usetype.equals(map.get("w0573")))
				selTypeList.add(map.get("w0555"));
			String reviewPersonId = map.get("w0501");
			reviewPersonIds += "'" + reviewPersonId + "',";
		}
		if (StringUtils.isNotEmpty(reviewPersonIds))
			reviewPersonIds = reviewPersonIds.substring(0,reviewPersonIds.length() - 1);
		//是否需要输出Excel
		boolean isOutPut = false;
		for (int i = 0; i < typelist.size(); i++) {
			String type = (String) typelist.get(i).get("type");
			if(!selTypeList.contains(type))
				continue;
			isOutPut = true;
			ArrayList<LazyDynaBean> firstHeadList = exBo.getAllSheetHeadList(usetype,type);// 第一页列头
			//当导出上级会议的帐号密码时，只能导出学院聘任组的成员 haosl 20160809
			if ("2".equals(type)) {// 学科组情况考虑 导出分为 学科组成员页 科目等页
				// 导出学科页签时，按照学科分类，每一次导出都是同一个会议，会议id相同，科目不同
				ArrayList<LazyDynaBean> grouplist = exBo.getProGroup(w0301);
				// 根据不同的学科组查询不同的页签
				for (int j = 0; j < grouplist.size(); j++) {
					LazyDynaBean bean = grouplist.get(j);
					String group_name = (String) bean.get("group_name"); // 学科组名称
					String group_id = (String) bean.get("group_id");
					ArrayList firstDataList = exBo.getAllDataList(firstHeadList, reviewPersonIds, type,group_id, w0301,usetype);// 第一页数据集合
					excelUtil.exportExcel(group_name, null,firstHeadList, firstDataList, null,0);// 输出第一页
					
					if (firstDataList.size() > 0){
						LazyDynaBean ldbean=null;
						HSSFPatriarch patriarch=excelUtil.getSheet().createDrawingPatriarch();
						for (int k = 0; k < firstDataList.size(); k++) {
							ldbean=(LazyDynaBean)firstDataList.get(k);
							LazyDynaBean tembeanName=(LazyDynaBean)ldbean.get("username");
							LazyDynaBean tembeanpwd=(LazyDynaBean)ldbean.get("pwd");
							String name=(String)tembeanName.get("content");
							String pwd=(String)tembeanpwd.get("content");
							byte[] outstream=exBo.getdimensional(name,pwd);
							HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,0,0,(short)2,k+1,(short)3,k+2);
							patriarch.createPicture(anchor,excelUtil.getWb().addPicture(outstream, HSSFWorkbook.PICTURE_TYPE_PNG));  
							anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
						}
					}
				}
			} else {
				ArrayList firstDataList = exBo.getAllDataList(firstHeadList, reviewPersonIds, type, null,w0301,usetype);// 评审帐号第一页数据集合
				
				String excelName = "";
				if("1".equals(type)){
					excelName = JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT;
					
				}else if("3".equals(type)){
					excelName = JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT;
					
				}else if("4".equals(type)){
					excelName = JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
					
				}
				if ("1".equals(type)||("2".equals(usetype) && "3".equals(type))||"4".equals(type)){//评委会 、同行专家、二级单位
					excelUtil.exportExcel(excelName, null, firstHeadList,firstDataList, null, 0);// 输出第一页
					
					if (firstDataList.size() > 0){
						LazyDynaBean ldbean=null;
						HSSFPatriarch patriarch=excelUtil.getSheet().createDrawingPatriarch();
						for (int k = 0; k < firstDataList.size(); k++) {
							ldbean=(LazyDynaBean)firstDataList.get(k);
							LazyDynaBean tembeanName=(LazyDynaBean)ldbean.get("username");
							LazyDynaBean tembeanpwd=(LazyDynaBean)ldbean.get("pwd");
							String uername=(String)tembeanName.get("content");
							String pwd=(String)tembeanpwd.get("content");
							byte[] outstream=exBo.getdimensional(uername,pwd);
							HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,0,0,(short)2,k+1,(short)3,k+2);
							patriarch.createPicture(anchor,excelUtil.getWb().addPicture(outstream, HSSFWorkbook.PICTURE_TYPE_PNG));  
							anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
						}
					}
				}
			}
		}
		//未启动 材料审核 /投票 (阶段)给提示，不导出。 haosl 20170613
		if(!isOutPut){
			String msg = "";
			if("1".equals(usetype))
				msg = "选中申报人未启动材料审核阶段！";
			else if("2".equals(usetype))
				msg = "选中申报人未启动投票阶段！";
			this.getFormHM().put("msg", msg);
			return;
		}
		
		String fileName = "";
		if("1".equals(usetype)){//评审帐号第二页数据集合
			ArrayList columnsInfo = exBo.getColumnsInfo(this.userView);//haosl 20160812 20160812
			ArrayList<LazyDynaBean> mergedCellList = exBo.getMergedCellList(columnsInfo);// 第二页复合列头
			ArrayList<LazyDynaBean> secondheadList = exBo.getSecondSheetHeadList(columnsInfo, false,w0301);// 第二页列头
			ArrayList secondDataList = exBo.getSecondDataList(columnsInfo,secondheadList, reviewPersonIds, false);// 第二页数据集合
			// 去掉仅用作查询的W0501,W0301
			secondheadList.remove(secondheadList.size() - 1);
			secondheadList.remove(secondheadList.size() - 1);
			excelUtil.exportExcel(ResourceFactory.getProperty("zc_new.zc_reviewfile.fistSheetName"),mergedCellList, secondheadList,
					secondDataList,null, 1);// 输出第二页
			fileName = "审核账号和密码_" + this.userView.getUserName() +".xls";// 材料评审
		}else{
			fileName = "投票帐号和密码_" + this.userView.getUserName() +".xls";// 投票
		}
		excelUtil.exportExcel(fileName);// 导出表格
		this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));// 表格名传进前台
	}
}
