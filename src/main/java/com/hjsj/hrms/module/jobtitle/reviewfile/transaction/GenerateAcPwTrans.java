package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * 职称评审_上会材料_生成账号密码获得学科组
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
public class GenerateAcPwTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
    public void execute() throws GeneralException {
        
        String isSelectAll = (String) this.getFormHM().get("isSelectAll");//表格控件是否全选
    	ArrayList<MorphDynaBean> idlist = (ArrayList<MorphDynaBean>) this.getFormHM().get("idlist");//选中或反选的数据
    	int inputValue = this.getFormHM().get("inputValue")==null?0:Integer.parseInt((String)this.getFormHM().get("inputValue"));//输入的数据
    	StartReviewBo srBo = new StartReviewBo(this.frameconn,userView); 
        try {
        	if(inputValue==0)
        		throw new Exception("请输入正确的账号数！");
        	GenerateAcPwBo generateAcPwBo = new GenerateAcPwBo(this.frameconn, this.userView);
        	ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();
    		selList = generateAcPwBo.getSelectList(isSelectAll, idlist);//实际选中的数据
			String w0301 = "";
			ArrayList<HashMap<String, String>> userPwdList = new ArrayList<HashMap<String, String>>();//先生成账号密码
			userPwdList = (ArrayList<HashMap<String, String>>)GenerateAcPwBo.generate(inputValue, new ContentDAO(this.frameconn));
			for(HashMap<String, String> sList : selList){
				String select_w0501 = sList.get("w0501");
				String select_w0301 = sList.get("w0301");
				w0301 = select_w0301;
				generateAcPwBo.createExpertUser(select_w0501, select_w0301, userPwdList,2);//写入投票账号密码
			}
			// 同步外部专家人数
			if(StringUtils.isNotBlank(w0301)){
				ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
				reviewFileBo.asyncPersonNum(w0301,3);
			}
			//生成账号时，如果选中人已经启动当前环节则置空  haosl 2017--07-24
			ArrayList<HashMap<String, String>> tempselectList = new ArrayList<HashMap<String,String>>();
			for(HashMap<String,String> map : selList){
				String w0555 = map.get("w0555");//评审环节
				String w0573 = map.get("w0573");//审查|投票
				//同行专家只有投票阶段  
				if("2".equals(w0573) && "3".equals(w0555))
					tempselectList.add(map);
			}
			srBo.updateW0555W0573(tempselectList,null,null);
			this.getFormHM().put("msg", "账号密码生成成功！");
        } catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    }

   

}
