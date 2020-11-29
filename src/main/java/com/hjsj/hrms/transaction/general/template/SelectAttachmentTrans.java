package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectAttachmentTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			ArrayList mediasortList = new ArrayList();
			ArrayList mediasortList2 = new ArrayList();//多媒体目录
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String attachmenttype=(String) hm.get("attachmenttype");//附件的类型 0:公共附件 1:个人附件
			String infor_type = (String)hm.get("infor_type");//不可能为空
			hm.remove("infor_type");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sb = new StringBuffer("");
			sb.append("select * from mediasort where dbflag="+infor_type);
			this.frowset = dao.search(sb.toString());
			while(this.frowset.next()){
			    String flag = this.frowset.getString("flag");
			    //liuyz bug29869 没有授权多媒体分类提示不对
			    String id = String.valueOf(this.frowset.getInt("id"));
				String sortname = this.frowset.getString("sortname");
				CommonData data=new CommonData(id,sortname);
			    mediasortList2.add(data);
			    if (!this.userView.isSuper_admin()){//判断多媒体权限
			        if (!this.userView.hasTheMediaSet(flag)) continue;
			    }
				mediasortList.add(data);
			}
			if(attachmenttype!=null&& "0".equals(attachmenttype)){//如果是公共附件不用判断权限
			}else{//如果是个人附件
				//liuyz bug29869 没有授权多媒体分类提示不对
				if (mediasortList.size()<1)
					throw GeneralExceptionHandler.Handle(new GeneralException("您没有授权多媒体分类的权限！"));
		         if (mediasortList2.size()<1){
		              if("1".equals(infor_type)){
		                  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("general.template.cannotUploadAttach")));  
		              }else if("2".equals(infor_type)){
		                  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("general.template.havenotCreateEmpMediaType")));
		              }else{
		                  throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("general.template.havenotCreatePositionMediaType")));
		              }
		                
		         }
			}
			this.getFormHM().put("mediasortList", mediasortList);
			if(mediasortList.size()==0){
				this.getFormHM().put("mediasortid", "-9999");
			}else{
				CommonData obj = (CommonData)mediasortList.get(0);
				this.getFormHM().put("mediasortid", obj.getDataValue());//默认显示第一个
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
