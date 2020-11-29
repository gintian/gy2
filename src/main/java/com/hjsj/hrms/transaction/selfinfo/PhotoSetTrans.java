package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 上传头像
 *
 * @author tiany
 */
public class PhotoSetTrans extends IBusiness {


    public void execute() throws GeneralException {
        InputStream streamIn = null;
        OutputStream streamOut = null;
        try {
            FormFile file = (FormFile) this.getFormHM().get("picturefile");
            String fname = file.getFileName();
            Sys_Infom_Parameter sys_Infom_Parameter = new Sys_Infom_Parameter(this.getFrameconn(), "INFOM");
            String para_maxsize = sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO, "MaxSize");
            String multimedia_maxsize = para_maxsize;

            if ((multimedia_maxsize == null) || ("".equals(multimedia_maxsize))) {
                multimedia_maxsize = "0";
            }
            float maxSize = Float.parseFloat(multimedia_maxsize);
            //最大尺寸KB先转为B，最去比对
            if ((maxSize > 0) && (maxSize * 1024 < file.getFileSize())) {
                throw new GeneralException("上传文件太大，请不要超过" + para_maxsize + "KB");
            }
            int indexInt = fname.lastIndexOf(".");
            String ext = fname.substring(indexInt , fname.length());
            PhotoImgBo photoImgBo = new PhotoImgBo(this.frameconn);
            //删除原有的头像以及低分辨率图片
            photoImgBo.delFile(this.userView,1);
            streamIn = file.getInputStream();
			//将新的头像保存到VFS中
			String fileid = photoImgBo.addFile(streamIn,this.userView,ext);
            this.getFormHM().put("photoname", fileid);
            //原来两个图片路径是固定的，一个过来，两个都能用,现在改用VFS之后，两个图片文件id没有任何关联，得加个字段来传
            this.getFormHM().put("lowimage", fileid);
			this.getFormHM().put("photoType", ext.substring(1,ext.length()));
			this.getFormHM().put("scale", "");
			String x = (String) this.getFormHM().get("x");
			if (x == null || x.length() == 0) {
				this.getFormHM().put("x", "50");
				this.getFormHM().put("y", "50");
				this.getFormHM().put("width", "100");
				this.getFormHM().put("height", "100");
			}
        } catch (Exception ex) {
            ex.printStackTrace();
            String errorMsg = ex.toString();
            int index_i = errorMsg.indexOf("description:");
            throw new GeneralException(errorMsg.substring(index_i + 12));
        } finally {
            try {
                if (streamIn != null)
                    streamIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
