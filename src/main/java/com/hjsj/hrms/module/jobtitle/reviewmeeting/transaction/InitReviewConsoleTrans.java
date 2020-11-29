package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewConsoleBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class InitReviewConsoleTrans  extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			String w0301 = PubFunc.decrypt((String) this.getFormHM().get("w0301_e"));
			String review_links = (String) this.getFormHM().get("review_links");//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
			
			String enterType = (String) this.getFormHM().get("enterType");//是从什么入口进来的，1：创建修改评审会议条件，2：上会界面
			String evaluationType = (String) this.getFormHM().get("evaluationType");//1:投票  2：评分
			String userType = (String) this.getFormHM().get("userType");//1：随机账号，2：非随机，选人的
			boolean isFinished = (Boolean) this.getFormHM().get("isFinished");//当前环节是否结束，结束了只能查看
			ReviewConsoleBo bo = new ReviewConsoleBo(this.getFrameconn(), this.getUserView());
			bo.sortSeq(w0301, review_links, userType);//如果没有排序，自动用过索引排序,seq是后加字段，如果没有，则自动排序
			bo.setFinished(isFinished);
			int screenWidth = (Integer) this.getFormHM().get("screenWidth");
			String config = bo.getTableConfigForDiff(w0301, review_links,enterType,evaluationType,userType,screenWidth);
			this.getFormHM().put("tableConfig", config.toString());
			//为了显示有哪些账号已经评价过
			HashMap<String, String> categoriesmap = bo.getCategoriesMap(w0301, review_links,evaluationType);
			this.getFormHM().put("categoriesmap", categoriesmap);
			
			//获取申报人对应的数据信息包括分组名，w05表中的数据等
			HashMap<String, ArrayList<HashMap<String, String>>> personmap = bo.getCategories_relations(w0301, review_links);
			this.getFormHM().put("personmap", personmap);
			//对于学科组两种情况1：随机账号，需要每个分组对应的随机数是多少2.选人，需要知道有哪些组可以进行选择，选中的组对应的人数
			if("2".equals(review_links)) {
				if("1".equals(userType)) {
					//获取随机账号的map，<加密（categories_id）,数量>
					HashMap<String, Integer> randomCountMap = new HashMap<String, Integer>();
					randomCountMap = bo.getCountPerson(w0301,review_links);
					this.getFormHM().put("randomCountMap", randomCountMap);
				}else {
					//获取学科组的组名和组ID(审批人需要显示的选择框)(审批人需要显示的总人数)
					HashMap<String, String> groupMap = new HashMap<String, String>();
					groupMap = bo.getGroupMap(w0301,review_links);//获取组名和组人数的map集合
					bo.setExpertNum(w0301, review_links);//因为第一次进来的或者暂停修改了学科组人数之后，zc_personnel_categories表中对应的专家数不对了，这里同步一下，保证数据准确
					this.getFormHM().put("groupMap", groupMap);
					 
					//获取实际选择的<categories_id,group_id>
					HashMap<String, String> cateIdGroupIdMap = new HashMap<String, String>();
					cateIdGroupIdMap = bo.getCountMap(w0301,review_links);
					this.getFormHM().put("cateIdGroupIdMap", cateIdGroupIdMap);
				}
			}
			
			this.getFormHM().put("w0575codesetid", DataDictionary.getFieldItem("W0575").getCodesetid());
			
			String name = bo.meettingName(w0301);
			this.getFormHM().put("meettingName", name);
			
			ReviewMeetingBo mbo = new ReviewMeetingBo(this.getFrameconn(), this.getUserView());
			String value = mbo.getW03Ctrl_param(w0301).get(review_links);
			this.getFormHM().put("ctrl_param", ","+value+",");
			//是否有会议安排的权限，如果有的话，是能操作的，可以删除，新增等等，如果没有，只能进行看的操作
			this.getFormHM().put("readOnlyOperate", this.userView.hasTheFunction("38005051505"));
			
			ConstantXml constantXml = new ConstantXml(this.frameconn,"JOBTITLE_CONFIG");
	        String support_word= constantXml.getTextValue("/params/support_word");
	        this.getFormHM().put("support_word", support_word);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    
}
