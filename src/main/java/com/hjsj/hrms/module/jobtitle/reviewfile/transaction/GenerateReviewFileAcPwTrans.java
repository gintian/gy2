package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成 上会材料的审查和投票账号交易类
 * @author haosl
 * @date 20170531
 */
public class GenerateReviewFileAcPwTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			/** method  =examine 生成审核账号 =vote 生成投票账号*/
			String method = (String)this.getFormHM().get("method");
			/** 1 评委会阶段 =2 学科组阶段  =3 同行专家阶段 =4 二级单位阶段*/
			int type = (Integer)this.getFormHM().get("type"); 
			String w0301 = (String)this.getFormHM().get("w0301");
			if(StringUtils.isNotBlank(w0301))
				w0301 = PubFunc.decrypt(w0301);
			ArrayList<MorphDynaBean> idlist = (ArrayList<MorphDynaBean>) this.getFormHM().get("idList");//选中的数据
			GenerateReviewFileAcPwBo acPwbo = new GenerateReviewFileAcPwBo(this.getFrameconn(),this.getUserView());
			 GenerateAcPwBo gbo = new GenerateAcPwBo(this.getFrameconn());
			ArrayList<HashMap<String, String>> selectList = new ArrayList<HashMap<String,String>>();
			selectList = acPwbo.getSelectList("0", idlist,userView);
			StartReviewBo srBo = new StartReviewBo(this.frameconn,userView);
			if("examine".equals(method)){
				if(type==1 || type==4){// 评委会、二级单位是否选择专家
					List<String> personList = acPwbo.getPersonList(type,w0301,null);
					//校验
					if(personList.size()==0){
						String msg = type==1?JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT:JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
	    				this.getFormHM().put("msg","请为申报人的"+msg+"添加专家!");
	    				return;
					}
					for(HashMap<String, String> map : selectList){
						String w0501 = map.get("w0501");
						String group_id = map.get("group_id");
						//向专家表中添加专家并生成帐号和密码
						acPwbo.createExamineAccounts(w0301,w0501,type,group_id,personList);	
					}
				}else{//学科组
					//校验
					for(int i=0;i<selectList.size();i++){
	    				String group_id = selectList.get(i).get("group_id");
	        			if(StringUtils.isEmpty(group_id)){
	        				String msg="请为所有选中的申报人选择学科组！";
		    				this.getFormHM().put("msg", msg);
		    				return;
	        			}
	        		}
					for(HashMap<String, String> map : selectList){
						String w0501 = map.get("w0501");
						String group_id = map.get("group_id");
						//向专家表中添加专家并生成帐号和密码
						List<String> personList = acPwbo.getPersonList(type,w0301,group_id);
						acPwbo.createExamineAccounts(w0301,w0501,type,group_id,personList);	
					}
				}
			}else if("vote".equals(method)){
				int number = 0;
				if(type==1 || type==4){//评委会、二级单位
					number = acPwbo.getExpertNum(w0301, type, null);
					if(number==0){
						String msg = type==1?JobtitleUtil.ZC_REVIEWFILE_STEP1SHOWTEXT:JobtitleUtil.ZC_REVIEWFILE_STEP4SHOWTEXT;
	    				this.getFormHM().put("msg","请为申报人的"+msg+"添加专家!");
	    				return;
					}
					ArrayList accountList  = GenerateAcPwBo.generate(number, dao);
					for(int i=0;i<selectList.size();i++){
						String w0501 = selectList.get(i).get("w0501");
						acPwbo.createVoteAccounts(type, w0501, w0301, null, accountList);
					}
				}else if(type==2){//学科组
	        		for(int i=0;i<selectList.size();i++){
	        			String group_id = selectList.get(i).get("group_id");
	        			if(StringUtils.isEmpty(group_id))
	        				throw new Exception("请为申报人选择学科组!");
	        		}
	        		//保证相同学科组只生成一份账号密码
	        		Map map = new HashMap(); 
	        		ArrayList accountList = new ArrayList();
	        		for(int i=0;i<selectList.size();i++){
	        			String group_id = selectList.get(i).get("group_id");
	        			String w0501 = selectList.get(i).get("w0501");
	        			if(map.containsKey(group_id))
	        				accountList = (ArrayList)map.get(group_id);
	        			else{
		        			number = acPwbo.getExpertNum(w0301,type,group_id);
		        			accountList  = GenerateAcPwBo.generate(number, dao);
		        			map.put(group_id, accountList);
	        			}
						acPwbo.createVoteAccounts(type, w0501, w0301, group_id, accountList);
	        		}
				}
			}
			//生成账号时，如果选中人已经启动当前环节则置空  haosl 2017--07-24
			ArrayList<HashMap<String, String>> tempselectList = new ArrayList<HashMap<String,String>>();
			for(HashMap<String,String> map : selectList){
				String w0555 = map.get("w0555");//评审环节
				String w0573 = map.get("w0573");//审查|投票
				if("vote".equals(method)){
					if("2".equals(w0573) && String.valueOf(type).equals(w0555))
						tempselectList.add(map);
				}else{
					if("1".equals(w0573) && String.valueOf(type).equals(w0555))
						tempselectList.add(map);
				}
			}
			srBo.updateW0555W0573(tempselectList,null,null);
			this.getFormHM().put("flag", "1");//	成功标记
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		}
	}
}
