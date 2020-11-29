package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:DeletePositionApplyTrans
 * </p>
 * <p>
 * Description:上传简历附件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-07-31
 * </p>
 * 
 * @author zx
 * @version 1.0
 */
public class UploadResumeFilesTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			String linkid = (String) this.getFormHM().get("linkid");// 流程环节id
			String a0100 = PubFunc.decrypt((String) this.getFormHM().get("a0100"));
			String flag = (String) this.getFormHM().get("flag");// 标志位，判断是在候选人列表进来还是简历详情页面上传的文件
			String nbase = PubFunc.decrypt((String) this.getFormHM().get("nbase"));
			ArrayList file_list = (ArrayList) (this.getFormHM().get("file_list")==null?new ArrayList():this.getFormHM().get("file_list"));// 窗口关闭所剩余的文件 服务器名称
			ResumeFileBo rfb = new ResumeFileBo(this.frameconn, this.userView);
			/**
			 * 全部删除文件
			 */
			if (file_list.isEmpty()&& StringUtils.isEmpty(flag)) {
				rfb.deleteAllFiles(a0100, nbase, linkid);
				return;
			}

			if ("1".equals(flag)) {// 从个人简历上传的文件
				MorphDynaBean bean = (MorphDynaBean) file_list.get(0);
				String fileid = (String)bean.get("fileid");
				String fileName = (String)bean.get("filename");
				String isOK = rfb.addFile(fileid, PubFunc.decrypt(fileName), linkid, nbase, a0100);
				this.getFormHM().put("isOK", isOK);
				// 针对简历详情页面上传局部刷新页面问题
				ArrayList files = rfb.getCurrentFile(nbase, a0100);
				this.getFormHM().put("uploadFileList", files);
			} else {
				/**
				 * 获取数据库中当前操作员给指定人员上传的文件
				 */
				HashMap<String, String> map = rfb.getAllBaseFile(a0100, nbase, linkid); 
				/**
				 * 将前台传过来的上传文件转为map，便于下面对其判断使用
				 */
				HashMap preMap = new HashMap();
				MorphDynaBean bean = null;
				String fileid = "";
				String fileName = "";
				for (int i = 0; i < file_list.size(); i++) {
					bean = (MorphDynaBean) file_list.get(i);
					fileid = (String)bean.get("fileid");
					fileName = PubFunc.decrypt((String)bean.get("filename"));
					preMap.put("" + i + "", fileid);
					if (!map.containsValue(fileid)) {// 数据库中不存在当前上传的文件，执行新增操作(若存在则跳过) String isOK =
						rfb.addFile(fileid, fileName, linkid, nbase, a0100);
					}
				}
				  
				this.getFormHM().put("isOK", "savesuccess"); 
			    ArrayList values = new ArrayList(map.values()); 
				for (int i = 0; i < values.size(); i++) {
					fileid = (String) values.get(i);
					String id = "";
					if (!preMap.containsValue(fileid)) {// preMap中不含有数据库中map的差异数据即为需要删除的文件
						for (Map.Entry<String, String> obj : map.entrySet()) { // 获取简历附件id
							if (StringUtils.isNotEmpty(fileid) && fileid.equalsIgnoreCase(obj.getValue())) {
								id = obj.getKey();
								break;
							}
						} // 删除文件记录
						rfb.deleteFile(id,fileid);
					}
				}
				 
				this.getFormHM().put("isOK", "uploadSuccess");

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 上传保存
	 * 
	 * @param rfb
	 * @param serFileName
	 * @param filename
	 * @param localname
	 * @param path
	 * @param linkid
	 * @param nbase
	 * @param a0100
	 * @return
	 */
	@Deprecated
	private String addFile(ResumeFileBo rfb, String serFileName, String filename, String localname, String path,
			String linkid, String nbase, String a0100) {
		HashMap hm = new HashMap();
		hm.put("serFileName", serFileName);
		hm.put("filename", filename);
		hm.put("localname", localname);
		hm.put("path", path);
		hm.put("linkid", linkid);
		hm.put("nbase", nbase);
		hm.put("a0100", a0100);
		String isOK = rfb.addFile(hm);// 保存附件
		return isOK;
	}
}
