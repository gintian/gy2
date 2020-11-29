package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * 推荐职位
 * <p>Title: RecommendPositionTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-3-21 上午11:46:29</p>
 * @author xiongyy
 * @version 1.0
 */
public class RecommendPositionTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        try {
            String z0301s = (String) this.getFormHM().get("z0301s");
            String a0100s = (String) this.getFormHM().get("a0100s");
            String from = (String) this.getFormHM().get("from");
            String msg = "";
            PositionBo pobo = new PositionBo(this.frameconn,new ContentDAO(this.frameconn),this.getUserView());
            String a0100ForUnRecommend = pobo.recommendPosition(z0301s,a0100s);
            ArrayList z0301List = this.toDecryptSplit(z0301s);
            ResumeFilterBo rbo = new ResumeFilterBo(frameconn, userView);
            //推荐职位时调用简历过滤
            rbo.updateSuitable(z0301List,null);
            if("talents".equalsIgnoreCase(from))
            	//2016/3/1 wangjl 不从人才库移出
//                pobo.deleteOnRecommend(a0100s,a0100ForUnRecommend);
            if(a0100ForUnRecommend!=null&&a0100ForUnRecommend.length()>0){
                msg = pobo.getMsgByUnRecommend(a0100ForUnRecommend);
            }
            //职位候选人处获取职位信息
            if("resumeInfo".equals(from)){
	            ResumeBo bo = new ResumeBo(this.frameconn,this.userView);
	            LazyDynaBean resumeInfo = bo.getResumeInfo(PubFunc.decrypt(a0100s.split("`")[1]), PubFunc.decrypt(a0100s.split("`")[0]), PubFunc.decrypt(z0301s.split(",")[0]), new HashMap());
	            this.getFormHM().put("resumeInfo", resumeInfo);
            }
            this.getFormHM().put("msg", msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        
    }
    //给加密的以逗号分隔的字符串 解析成 集合
    public ArrayList toDecryptSplit(String str) {
    	ArrayList list = new ArrayList();
        String[] split = str.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(PubFunc.decrypt(split[i]));
        }
        return list;
    }
}
