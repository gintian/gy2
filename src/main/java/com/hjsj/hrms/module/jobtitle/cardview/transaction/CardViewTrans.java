package com.hjsj.hrms.module.jobtitle.cardview.transaction;

import com.hjsj.hrms.module.jobtitle.cardview.businessobject.CardViewBo;
import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 上会材料 _展示
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class CardViewTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			CardViewBo cardViewBo = new CardViewBo(this.getFrameconn(), this.userView);// 工具类
			
			String username = this.userView.getUserName();
			String queue = this.getFormHM().get("queue")==null?"1":(String)this.getFormHM().get("queue");//每个申报人分组的批次,如果没有，显示第一批次的人
			String categories_id = this.getFormHM().get("categories_id")==null?"":(String)this.getFormHM().get("categories_id");
			
			String type = (String)this.getFormHM().get("type");//1：获取标识 2:是否有新的没有评价的记录 3：获取“公示、投票环节显示申报材料表单上传的word模板内容” 4:分类设置的投票人数 5:获取分组
			if(StringUtils.isNotEmpty(type)){
				if("0".equals(type)) {
					//1|null：材料评审  2：投票,3打分
					String useType = (String)this.getFormHM().get("useType");
					ArrayList personinfo = cardViewBo.getPersoninfo(queue,categories_id,useType);// 获取人员信息 
					this.getFormHM().put("personinfo", personinfo);
				}else if("1".equals(type)){
					/*该参数（isshowrefresh）改到system.property中，代码先不删除以防以后启用。chent 20170720
					JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
					this.getFormHM().put("isshowrefresh", jobtitleConfigBo.getParamConfig("isshowrefresh"));*/
					
					boolean isshowrefresh = false;
					String value = SystemConfig.getPropertyValue("jobtitle_isshowrefresh"); 
					if(value != null && value.trim().length()>0 && "true".equalsIgnoreCase(value)){
						isshowrefresh = true;
					}
					this.getFormHM().put("isshowrefresh", isshowrefresh);
				} else if("2".equals(type)) {
					boolean isexist = true;
					String useType = (String)this.getFormHM().get("useType");
					ArrayList personinfo = cardViewBo.getPersoninfo(queue,categories_id,useType);// 获取人员信息 
					if(personinfo.size() == 0){
						isexist = false;
					}
					this.getFormHM().put("isexist", isexist);
				} else if("3".equals(type)) {
					JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(),this.getUserView());
					boolean support_word = jobtitleConfigBo.getParamConfig("support_word");	// 公示、投票环节显示申报材料表单上传的word模板内容
					this.getFormHM().put("support_word", support_word);
					
				} else if("4".equals(type)) {
					String useType = (String)this.getFormHM().get("useType");
					LinkedHashMap<String, String> categoriesnummap = cardViewBo.getCategoriesNumMap(queue,useType);
					this.getFormHM().put("categoriesnummap", categoriesnummap);
				} else if("5".equals(type)) {
					LinkedHashMap<String, ArrayList<String>> categoriesmap = cardViewBo.getCategoriesMap();
					this.getFormHM().put("categoriesmap", categoriesmap);
				} else if("6".equals(type)) {
					//1|null：材料评审  2：投票,3打分
					String useType = (String)this.getFormHM().get("useType");
					ArrayList<String> categoriesList = cardViewBo.getCategoriesList(useType);
					this.getFormHM().put("categorieslist", categoriesList);
				} else if("7".equals(type)) {
				
					ArrayList<String> levelList = cardViewBo.getLevelList();
					this.getFormHM().put("levellist", levelList);
				} else if("8".equals(type)) {//获取分组申报人
					HashMap<String,Integer> queueMap = cardViewBo.getQueue();//<申报人分组id，批次>
					int approvalCount = cardViewBo.getApprovalCount();
					this.getFormHM().put("approvalCount", approvalCount);
					this.getFormHM().put("queueMap", queueMap);
				} else if("9".equals(type)) {//打分的时候需要给打分界面传的参数//全部都是加密的model，，object_List，，relation_Id
					MorphDynaBean queueMap = (MorphDynaBean)this.getFormHM().get("queueMap");
					String w0301 = (String)this.getFormHM().get("w0301");
					String reviewlink = (String)this.getFormHM().get("reviewlink");
					//获取w0505的集合和评分需要展示的申报材料的参数
					HashMap<String,Object> object_Map = cardViewBo.getObjectId(queueMap,reviewlink,w0301);
					
					this.getFormHM().put("need_parameter", object_Map.get("NEEDPASS_PARAMETER"));
					this.getFormHM().put("object_List", object_Map.get("W0505_ENCODE"));
					this.getFormHM().put("model", PubFunc.encrypt("1"));
					this.getFormHM().put("relation_Id", PubFunc.encrypt("1_"+w0301+"_"+reviewlink));
				}else if("10".equals(type)) {//打分回掉修改进度
					String w0301 = (String)this.getFormHM().get("w0301");
					String reviewlink = (String)this.getFormHM().get("reviewlink");
					cardViewBo.saveSubmitCount(PubFunc.decrypt(categories_id),w0301,reviewlink);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
