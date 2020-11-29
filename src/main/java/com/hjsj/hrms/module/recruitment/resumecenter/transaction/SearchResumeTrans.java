package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeEvaluationBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:SearchResumeTrans</p>
 * <p>Description:查询简历信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-02-04</p>
 * @author wangcq
 * @version 1.0
 * 
 */
public class SearchResumeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try{
			String resumeid = (String)this.getFormHM().get("resumeid");   //简历id
			resumeid = PubFunc.decrypt(resumeid);
			String pre = (String)this.getFormHM().get("nbase");     //人员库前缀
			pre = PubFunc.decrypt(pre);
			String zp_pos_id = (String)this.getFormHM().get("zp_pos_id");   //岗位id
			while(isIdEncrypt(zp_pos_id))
				zp_pos_id = PubFunc.decrypt(zp_pos_id);
			String headInfo  = (String)this.getFormHM().get("headInfo");  //标志位 true时只获取headMap信息
			ResumeBo resumeBo = new ResumeBo(this.frameconn,resumeid,pre);
			ArrayList operationList = new ArrayList();//人员简历操作
			HashMap headMap = resumeBo.getResumeHead(zp_pos_id,this.userView,false);   //根据简历id号、岗位号查询姓名、简历投递及状态信息
			this.getFormHM().put("resumeid", resumeid);
			this.getFormHM().put("nbase", pre);
			this.getFormHM().put("zp_pos_id", PubFunc.encrypt(zp_pos_id));
			this.getFormHM().put("username", headMap.get("username"));
			this.getFormHM().put("email", headMap.get("email"));
			this.getFormHM().put("recdate", headMap.get("create_time"));
			this.getFormHM().put("status", headMap.get("status"));
			this.getFormHM().put("lastPos", headMap.get("lastPos"));
			this.getFormHM().put("othPos", headMap.get("othPos"));
			//获取流程阶段信息
			ArrayList stageList = new ArrayList();
			String z0381 = PubFunc.decrypt((String)((HashMap) this.getFormHM().get("requestPamaHM")).get("z0381")); 
			RecruitProcessBo recruitBo = new RecruitProcessBo(this.frameconn,this.userView);
			//获取流程阶段信息
			stageList = recruitBo.getStageInfo(zp_pos_id,z0381,1);
			//获取查询方案列表
			ArrayList projectList =  new ArrayList();
			//获得招聘流程下一阶段的link_id
			String next_linkId= "";
			String next_nodeId = "";
			String link_id = "";
			if(!StringUtils.equals(headInfo, "true")){
				String from = (String)this.getFormHM().get("from");
				if(StringUtils.equalsIgnoreCase(from, "resumeCenter") || StringUtils.equalsIgnoreCase(from, "talents")){
					Integer current = (Integer)this.getFormHM().get("current");
					Integer pagesize = (Integer)this.getFormHM().get("pagesize");
					Integer rowindex = (Integer)this.getFormHM().get("rowindex");
					String hire_sql = (String)this.userView.getHm().get("hire_sql");//获取用户筛选简历的sql语句
					//获取表格控件中的排序sql
					TableDataConfigCache tdcc = null;
					if(StringUtils.equalsIgnoreCase(from, "resumeCenter"))
					{
						tdcc = (TableDataConfigCache) this.userView.getHm().get("zp_resume_191130_00001");						
					}else{
						tdcc = (TableDataConfigCache) this.userView.getHm().get("zp_talent_191130_00001");	
					}
					
					String querySql = tdcc.getQuerySql()==null?"":tdcc.getQuerySql();
					String filterSql = tdcc.getFilterSql()==null?"":tdcc.getFilterSql();
					
					ArrayList<HashMap> resume = resumeBo.getNextResume(hire_sql,querySql,filterSql,resumeid,current.intValue(),pagesize.intValue(),rowindex.intValue(),tdcc.getSortSql());     //获取下个人员简历的相关信息
					
					this.getFormHM().put("nextResumeid", resume.get(0).get("nextResumeid")==null?"":PubFunc.encrypt(resume.get(0).get("nextResumeid")+""));
					this.getFormHM().put("nextNbase", PubFunc.encrypt(resume.get(0).get("nextNbase")+""));
					this.getFormHM().put("nextZp_pos_id", PubFunc.encrypt(resume.get(0).get("nextZp_pos_id")+""));
					this.getFormHM().put("nextCurrent", resume.get(0).get("nextCurrent"));
					this.getFormHM().put("nextPagesize", resume.get(0).get("nextPagesize"));
					this.getFormHM().put("nextRowindex", resume.get(0).get("nextRowindex"));
					this.getFormHM().put("lastResumeid", resume.get(1).get("nextResumeid")==null?"":PubFunc.encrypt(resume.get(1).get("nextResumeid")+""));
					this.getFormHM().put("lastNbase", PubFunc.encrypt(resume.get(1).get("nextNbase")+""));
					this.getFormHM().put("lastZp_pos_id", PubFunc.encrypt(resume.get(1).get("nextZp_pos_id")+""));
					this.getFormHM().put("lastCurrent", resume.get(1).get("nextCurrent"));
					this.getFormHM().put("lastPagesize", resume.get(1).get("nextPagesize"));
					this.getFormHM().put("lastRowindex", resume.get(1).get("nextRowindex"));
				}else if(StringUtils.equalsIgnoreCase(from, "process"))
				{
					HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");    
					link_id = (String)hm.get("link_id");     //流程标志
					String c0102 = (String)hm.get("c0102");     //流程标志
					String page = (String)hm.get("page");     //页面信息
					String resume_flag = (String)hm.get("resume_flag");    
					String resume_name = (String)hm.get("resume_name");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("c0102", c0102);
					bean.set("z0381", PubFunc.encrypt(z0381));
					bean.set("page", page);
					bean.set("link_id", link_id);
					bean.set("resume_flag", resume_flag);
					bean.set("z0301", PubFunc.encrypt(zp_pos_id));
					bean.set("nbase", PubFunc.encrypt(pre));
					bean.set("a0100", PubFunc.encrypt(resumeid));
					bean.set("resume_id", resume_flag.split("`")[0]);
					bean.set("resume_name", resume_name);
					bean.set("link_name", (String)resumeBo.getCustom_name(link_id).get("custom_name"));
					this.getFormHM().put("infoBean", bean);
					operationList = resumeBo.getOperationList(zp_pos_id, link_id, z0381, this.userView);
					//获取查询方案列表
					projectList = recruitBo.getProjectList(link_id,zp_pos_id,1);
					//获得招聘流程下一阶段的link_id
					next_linkId=recruitBo.getNextLinkId(link_id,stageList);
					next_nodeId=recruitBo.getNextNodeId(link_id, stageList);
					String lastLinkId = recruitBo.getLastLinkId(link_id, stageList);
					ArrayList<String> skiplist = new ArrayList<String>();
					skiplist.add(lastLinkId);
//					skiplist.add(link_id);//当前环节
					skiplist.add(next_linkId);
					//是否必须按流程环节进行
					String skipFlag = recruitBo.getSkipFlag(z0381);
					this.getFormHM().put("skipFlag", skipFlag);
					this.getFormHM().put("skiplist", skiplist);
				}
				this.getFormHM().put("projectList", projectList);
				this.getFormHM().put("next_linkId",next_linkId);
				this.getFormHM().put("next_nodeId",next_nodeId);
				this.getFormHM().put("operationList", operationList);
				this.getFormHM().put("from", from);
				ArrayList subModuleInfo = resumeBo.getSubModuleInfo(zp_pos_id);     //获取简历中各个模块的信息集合 
				this.getFormHM().put("fieldSetList", subModuleInfo.get(0));
				this.getFormHM().put("resumeBrowseSetMap", subModuleInfo.get(1));
	            this.getFormHM().put("setShowFieldMap", subModuleInfo.get(2));
	            
	            if(StringUtils.equalsIgnoreCase(from, "talents"))
	                this.getFormHM().put("isMine", isMine(resumeid));
	            if(StringUtils.equalsIgnoreCase(from, "resumeCenter")){
	            	boolean isTalent = resumeBo.existTalent(resumeid, pre, this.userView.getUserName());
	            	if(isTalent)
	            	    this.getFormHM().put("isTalent", "true");
	            	else
	            		this.getFormHM().put("isTalent", "false");
	            }
			}
			this.getFormHM().put("stageList", stageList);
			
		    ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
	        HashMap map = parameterXMLBo.getAttributeValues();
	        String isAttach = "0";
            if (map != null && map.get("attach") != null) {
                isAttach = (String) map.get("attach");
            }
            
            this.getFormHM().put("isAttach", isAttach);
            /*
             * 获取上传的文件
             */
            ResumeFileBo resumeFileBo = new ResumeFileBo(this.getFrameconn(), this.userView);
            ArrayList files = resumeFileBo.getFiles(pre, resumeid, "0");
            ArrayList resume = resumeFileBo.getFiles(pre, resumeid, "1");//简历
            /*给简历附件排序 start*/
            EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
            String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			String hireChannel = "";
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				hireChannel = bo.getCandidateStatus(candidate_status_itemId, resumeid);
			}
			//如果启用了上传文件分类，则对已上传文件进行排序
			String attach_codeset = (String) map.get("attachCodeset");
			if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset)&&StringUtils.isNotEmpty(hireChannel)) {
				ArrayList attach_code_list = bo.getAttachCodeset(map, hireChannel);
				resume = bo.sortFileList(attach_code_list,resume);
			}
			/*给简历附件排序 end*/
            ArrayList allFiles = new ArrayList();//所有上传的文件
            allFiles.addAll(resume);
            allFiles.addAll(files);
            this.getFormHM().put("uploadFileList", allFiles);
			
			/***
			 * 简历评价
			 */
			ResumeEvaluationBo evaBo = new ResumeEvaluationBo(this.frameconn, this.userView);
			ArrayList evaluationList = evaBo.getEvaluationList(pre, resumeid, this.userView.getDbname(), this.userView.getA0100(),1);//所有评价
			ArrayList list = evaBo.getEvaluationList(pre, resumeid, this.userView.getDbname(), this.userView.getA0100(),0);//我的评价
			LazyDynaBean evaluationBean = new LazyDynaBean();
			if(list.size()>0)
			{
				evaluationBean = (LazyDynaBean)list.get(0);
			}else{
				evaluationBean.set("score", "-1");
				evaluationBean.set("content", "");
			}
			this.getFormHM().put("evaluationBean", evaluationBean);
			this.getFormHM().put("evaluation", evaluationList);
			
			/***简历状态**/
			String node_flag = resumeBo.getNode_flag(resumeid, zp_pos_id, pre);
			this.getFormHM().put("node_flag", node_flag);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 判断该人才库中的人员是否属于我的人才库中的人员（公共人才库中的人员包含我的人才库中的人员）
	 * @param a0100 人员编号
	 * @return flag 0：不属于| 1：属于    超级用户查看时默认为属于
	 */
    private String isMine(String a0100) {
        String flag = "0";
        try {
            if(this.userView.isSuper_admin())
                return "1";
                
            String sql = "select 1 from zp_talents where a0100=? and create_user=?";
            ArrayList params = new ArrayList();
            params.add(a0100);
            params.add(this.userView.getUserName());
            
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql, params);
            if (this.frowset.next())
                flag = "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;

    }
    /**
     * 判断id是否加密过
     * @param id
     * @return true 加密过
     * @throws GeneralException 
     */
    private static boolean isIdEncrypt(String id) throws GeneralException{
        return id != null && id.length()>10;
        /*zxj 20160308 招聘职位id长度新招聘升级不知什么原因改成了8位，老数据仍是10位，所以长度小于10位的认为是已解密的数据
         * 此处用新生成id的方法判断是错误的，会影响序号生成器的计数，并且从数据库取数影响效率
    	IDGenerator idg = new IDGenerator(2, conn);
        int length = idg.getId(tableName).length();
    	Pattern pattern = Pattern.compile("\\d{"+length+"}");
    	Matcher matcher = pattern.matcher(id);
    	return !matcher.matches();
    	*/
    }
}
