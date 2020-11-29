package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 头像切割
 * @author tiany
 *
 */
public class PhotoCutTrans extends IBusiness {

	public void execute() throws GeneralException {
	try {
		String photoname =(String)this.getFormHM().get("photoname");
		String x=(String)this.getFormHM().get("x");
		String y=(String)this.getFormHM().get("y");
		String width = (String)(this.getFormHM().get("width"));
		String height = (String)(this.getFormHM().get("height"));
		String scale = (String) this.getFormHM().get("scale");
		//根据页面缩放比例得到真实数据
		if(!"".equals(scale)&&Double.parseDouble(scale)>1){
			x = (Integer.parseInt(x)*Double.parseDouble(scale))+"";
			y = (Integer.parseInt(y)*Double.parseDouble(scale))+"";
			width = (Integer.parseInt(width)*Double.parseDouble(scale))+"";
			height = (Integer.parseInt(height)*Double.parseDouble(scale))+"";
			//省略小数点
			x = x.substring(0, x.lastIndexOf("."));
			y = y.substring(0, y.lastIndexOf("."));
			width = width.substring(0, width.lastIndexOf("."));
			height = height.substring(0, height.lastIndexOf("."));
		}
		String photoType = (String)(this.getFormHM().get("photoType"));
   	 	if(x==null||x.trim().length()==0){
   	 	 throw new GeneralException("请设置头像区域!");
   	 	}
   	 	PhotoImgBo photoImgBo = new PhotoImgBo(this.frameconn);
   	 	PhotoImgBo bo = new PhotoImgBo(frameconn);
   	 	//删除低分辨率图片
   	 	bo.delFile(this.userView,2);
   	 	//图片切割
   	 	String lowimage = photoImgBo.cut(this.userView,Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(width),Integer.parseInt(height),photoType);
		//原来某个人的头像路径是固定的，使用VFS并且切割生成新的头像后，文件id是更改了的
		this.getFormHM().put("photoname", photoname);
   	 	this.getFormHM().put("lowimage", lowimage);
		this.getFormHM().put("photoType", photoType);
		}catch (Exception ex) {
			ex.printStackTrace();  
            String errorMsg=ex.toString();
            int index_i=errorMsg.indexOf("description:");
            throw new GeneralException(errorMsg.substring(index_i+12));
		}

	}

	





}