package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.Map;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:判断招聘流程是否有招聘过程数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-8 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class SortLinksFuncTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
        	//标识是否是招聘状态排序
        	String hjzt = (String)this.getFormHM().get("hjzt");
    		MorphDynaBean seqsBean = (MorphDynaBean)this.getFormHM().get("seqs");
    		//拿到整个招聘环节-操作设置的id和seq 更新排序 wangjl
    		Map<String,String> seqs = PubFunc.DynaBean2Map(seqsBean);
        
            RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
        	recruitflowBo.sortFuncSeq(hjzt,seqs);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
