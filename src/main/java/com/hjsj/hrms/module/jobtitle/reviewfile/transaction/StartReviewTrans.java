package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title: StartReviewTrans </p>
 * <p>Description: 更新会议中评审状态</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-9-15 下午12:55:35</p>
 * @author liuyang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class StartReviewTrans extends IBusiness{

    @SuppressWarnings("unchecked")
	@Override
    public void execute() throws GeneralException {
	    StartReviewBo srbo = new StartReviewBo(this.getFrameconn(),this.getUserView());
	    String msg="";//消息
	    try {
	        ArrayList<MorphDynaBean> idlist = (ArrayList) this.getFormHM().get("idlist");
	        String method = (String) this.getFormHM().get("method");//method 材料审查和启动评审操作
	        boolean inReview =  (Boolean) this.getFormHM().get("inReview");
	        boolean inExpert = (Boolean) this.getFormHM().get("inExpert");
	        boolean exExpert = (Boolean) this.getFormHM().get("exExpert");
	        boolean inCollege =(Boolean) this.getFormHM().get("inCollege");
	        Integer type = null;
	        if(inReview)
	        	type = 1;
	        else if(inExpert)
	        	type = 2;
	        else if(exExpert)
	        	type = 3;
	        else if(inCollege)
	        	type = 4;
	        ArrayList<HashMap<String, String>> allList = srbo.getSelectList(idlist, this.userView);//实际选中的数据
	        String w0301 = "";
	    	if(allList.size()>0){
	    		w0301 = allList.get(0).get("w0301");
	    	} 
	    	if("examine".equalsIgnoreCase(method)){//材料审查
		    	if(!StringUtils.isEmpty(w0301)){
	    			for(int i=0;i<allList.size();i++){
	    				String group_id = allList.get(i).get("group_id");
	    				String w0501 = allList.get(i).get("w0501");
		    			int experNum = srbo.getExperAccountNum(w0301,type,1,group_id,w0501);
		    			if(experNum==0){//提示未生成帐号不能审查材料
		    				if(type == 1)//评委会
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT+"】生成审查账号！";
		    				else if(type==2)//学科组 校验
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT+"】生成审查账号！";
		    				else if(type==3)//同行专家
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT+"】生成审查账号！";
		    				else //二级单位 
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT+"】生成审查账号！";
		    				this.getFormHM().put("msg", msg);
	        				return;
		    			}
	    			}
		    	}
	    		//更新w0555和w0573=2,清除评审结果（zc_data_evaluation） haosl 20161010
	    		srbo.updateW0555W0573 (allList, type,1);
	    		//业务范围的上级会议材料审查时修改账号状态为启用，帐号区分为”查看帐号“ 同时清除该阶段的投票帐号haosl 20160927
	    		srbo.startUsingAccount(allList, type);
	        }else if("vote".equals(method)){
	        	if(!StringUtils.isEmpty(w0301)){
		    		//评委会、同行专家、二级单位 校验
	    			for(int i=0;i<allList.size();i++){
	    				String w0501 = allList.get(i).get("w0501");
	    				String group_id = allList.get(i).get("group_id");
		    			int experNum = srbo.getExperAccountNum(w0301,type,2,group_id,w0501);
		    			if(experNum==0){//提示未生成帐号不能审查材料
		    				if(type == 1)//评委会
		    					msg="请为选中申报人的 【"+JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT+"】生成投票帐号！";
		    				else if(type==2)//学科组 校验
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP2SHOWTEXT+"】生成投票帐号！";
		    				else if(type==3)//同行专家
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP3SHOWTEXT+"】生成投票帐号！";
		    				else //二级单位 
		    					msg="请为选中申报人的【"+JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT+"】生成投票帐号！";
		    				this.getFormHM().put("msg", msg);
	        				return;
		    			}
	    			}
	        	}
	        	//清除投票结果
	        	srbo.clearData(allList,type+"",2);
	        	//更新w0555和w0573=2,清除评审结果（zc_data_evaluation）
	        	srbo.updateW0555W0573(allList, type,2);///更新w0555和w0573=2 haosl 20161010
	        	
	        	String categories_id_e = (String) this.getFormHM().get("categories_id");
	        	if(StringUtils.isNotEmpty(categories_id_e)) {//差额投票时，更新申报人员分类表（zc_personnel_categories）中投票状态（approval_state）为已启动（1）
	        		String categories_id = PubFunc.decrypt(categories_id_e);
	        		ReviewFileBo bo = new ReviewFileBo(this.getFrameconn(), this.getUserView());
					bo.updateApproval_state(categories_id, "1");
	        		
	        	}
	        			
	        	
	        }
	    	msg="启动成功！";
	    	this.getFormHM().put("msg", msg);
	    } catch (Exception e) {
	        e.printStackTrace();	
	        throw GeneralExceptionHandler.Handle(e);
	    }
    }
}