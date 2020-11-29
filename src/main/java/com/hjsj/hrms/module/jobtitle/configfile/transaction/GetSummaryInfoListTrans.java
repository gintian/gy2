package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * xus 18/4/8 
 * 投票方式窗口获取数据
 * @version
 */
public class GetSummaryInfoListTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		// 业务字典指标
		ArrayList fieldList = DataDictionary.getFieldList("W05", 1);
		ArrayList showList = new ArrayList();
		for (int i = 0;i<fieldList.size(); i++) {
			FieldItem fi = (FieldItem) fieldList.get(i);
			// 去除没有启用的指标
			if (!"1".equals(fi.getUseflag())){
				continue;
			}
			// 去除隐藏的指标
			if (!"1".equals(fi.getState())){
				continue;
			}
			// 去掉学科组id
			if("group_id".equals(fi.getItemid().toLowerCase())
                    || "w0536".equalsIgnoreCase(fi.getItemid())){
				continue;
			}
			//修改显示的指标名称
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", fi.getItemid());
			String itemDesc=fi.getItemdesc();
			//同行专家
			if("W0527".equals(fi.getItemid().toUpperCase())||"W0529".equals(fi.getItemid().toUpperCase())||"W0531".equals(fi.getItemid().toUpperCase())||"W0533".equals(fi.getItemid().toUpperCase())){
				itemDesc=itemDesc+"("+ResourceFactory.getProperty("zc.reviewfile.outsideexpert")+")";
			}
			//专业组
			if("W0543".equals(fi.getItemid().toUpperCase())||"W0545".equals(fi.getItemid().toUpperCase())||"W0547".equals(fi.getItemid().toUpperCase())||"W0557".equals(fi.getItemid().toUpperCase())){
				itemDesc=itemDesc+"("+ResourceFactory.getProperty("zc.menu.subjectsshowtext")+")";
			}
			//评委会
			if("W0549".equals(fi.getItemid().toUpperCase())||"W0551".equals(fi.getItemid().toUpperCase())||"W0553".equals(fi.getItemid().toUpperCase())||"W0559".equals(fi.getItemid().toUpperCase())){
				itemDesc=itemDesc+"("+ResourceFactory.getProperty("zc.menu.committeeshowtext")+")";
			}
			//二级单位
			if("W0563".equals(fi.getItemid().toUpperCase())||"W0565".equals(fi.getItemid().toUpperCase())||"W0567".equals(fi.getItemid().toUpperCase())||"W0569".equals(fi.getItemid().toUpperCase())){
				itemDesc=itemDesc+"("+ResourceFactory.getProperty("zc_new.label.inOther")+")";
			}
			bean.set("itemdesc", itemDesc);
			showList.add(bean);
		}
		this.formHM.put("fieldList", showList);
		
		JobtitleConfigBo jcb=new JobtitleConfigBo(this.frameconn, this.userView);
		HashMap voteMap=jcb.getVoteConfig();
		this.formHM.put("voteColumns", voteMap.get("voteColumns"));;
	}
}
