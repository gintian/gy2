/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:CoursewareTrans
 * </p>
 * <p>
 * Description:培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class CoursewareTrans extends IBusiness {

    /**
	 * 
	 */
    public CoursewareTrans() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */

    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String id = (String) hm.get("id");
        id = id != null && id.trim().length() > 0 ? id : "";
        id = PubFunc.decrypt(SafeCode.decode(id));
        // hm.remove("id");
        String a_code = (String) hm.get("a_code");
        a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
        a_code = PubFunc.decrypt(SafeCode.decode(a_code));
        // hm.remove("a_code");

        //20170418	linbz 6813 返回后校验已上传的文件与已保存的文件是否相同，不同说明未保存，则从服务器上删除
        String newPath = (String) hm.get("newPath");
        hm.remove("newPath");
        if(StringUtils.isNotEmpty(newPath)) {
		    if(newPath.indexOf("id:") > -1)
		        newPath = newPath.substring(newPath.indexOf("id:") + 3);
		    
		    if(StringUtils.isNotEmpty(newPath))
		        newPath = SafeCode.decode(PubFunc.decrypt(newPath));	
		    
		    String r5100 = (String) this.getFormHM().get("r5100");
		    r5100 = r5100 != null && r5100.trim().length() > 0 ? r5100 : "";
		    r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
		    //获取r5100为空时，表示数据库没有新增没有保存直接返回，则直接删掉刚刚上传的课件
		    if(StringUtils.isEmpty(r5100)){
		    	File file = new File(newPath);
				if (file.exists()) {
					file.delete();
				}
		    }else{
			    RecordVo rv = new RecordVo("R51");
			    rv.setString("r5100", r5100);
				try {
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					RecordVo rv1 = dao.findByPrimaryKey(rv);
					String urlold = rv1.getString("r5113");
					String nowPath = newPath.substring(newPath.lastIndexOf(File.separator + "coureware" + File.separator));
					//urlold为空时，表示数据库之前就没保存课件 || 上传过的文件不同于库里原有文件路径   则删除
					if(StringUtils.isEmpty(urlold) || !urlold.equalsIgnoreCase(nowPath)){
						File file = new File(newPath);
						if (file.exists()) {
							file.delete();
						}
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		}
        
        String isParent = "0";// 是否为上级分类下的课程 1=是
        TrainCourseBo tbo = new TrainCourseBo(this.userView, this.frameconn);
        if (!this.userView.isSuper_admin() && tbo.getCodeIsParent(id))
            isParent = "1";

        if (isPublish(id))// 如果该课程为发布状态 测课程下的课件不可编辑
            isParent = "1";

        ArrayList fieldList = new ArrayList();
        StringBuffer sqlstr = new StringBuffer();
        StringBuffer columns = new StringBuffer();
        StringBuffer strwhere = new StringBuffer();
        try {
            sqlstr.append("select ");
            ArrayList itemList = DataDictionary.getFieldList("r51", Constant.USED_FIELD_SET);
            String leibie = null;
            for (int i = 0; i < itemList.size(); i++) {
                FieldItem item = (FieldItem) itemList.get(i);
                if ("R5100".equalsIgnoreCase(item.getItemid())) {
                    item.setVisible(false);
                    FieldItem item3 = new FieldItem();
                    item3.setFieldsetid("r51");
                    item3.setItemid("edit");
                    item3.setItemdesc(ResourceFactory.getProperty("label.edit"));
                    item3.setItemtype("A");
                    item3.setCodesetid("0");
                    item3.setAlign("center");
                    item3.setReadonly(true);
                    fieldList.add(item3);
                    item3 = new FieldItem();
                    item3.setFieldsetid("r51");
                    item3.setItemid("down");
                    item3.setItemdesc("下载");
                    item3.setItemtype("A");
                    item3.setCodesetid("0");
                    item3.setAlign("center");
                    item3.setReadonly(true);
                    fieldList.add(item3);
                    item3 = new FieldItem();
                    item3.setFieldsetid("r51");
                    item3.setItemid("show");
                    item3.setItemdesc("浏览");
                    item3.setItemtype("A");
                    item3.setCodesetid("0");
                    item3.setAlign("center");
                    item3.setReadonly(true);
                    fieldList.add(item3);
                }
                if ("r5105".equalsIgnoreCase(item.getItemid())) {
                    leibie = item.getValue();
                }
                /*
                 * if (item.getItemid().equalsIgnoreCase("r5111") ||
                 * item.getItemid().equalsIgnoreCase("r5113") ||
                 * item.getItemid().equalsIgnoreCase("r5115")) {
                 * item.setVisible(false); }
                 */
                if ("r5113".equalsIgnoreCase(item.getItemid())) {
                    item.setVisible(false);
                }
                columns.append(item.getItemid() + ",");
                fieldList.add(item);
            }
            sqlstr.append("R51.*,'' as edit,'' as show from R51 where R5000='" + id + "'");
            sqlstr.append(" order by norder");
            columns.append("fileid,");
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("sqlstr", sqlstr.toString());
            this.getFormHM().put("itemlist", fieldList);
            this.getFormHM().put("tablename", "r51");
            this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
            this.getFormHM().put("id", SafeCode.encode(PubFunc.encrypt(id)));
            this.getFormHM().put("columns", "edit,show," + columns.toString());
            this.getFormHM().put("strsql", "select " + columns + "'' as edit,'' as show");
            this.getFormHM().put("strwhere", " from R51 where R5000='" + id + "'");
            this.getFormHM().put("isParent", isParent);
            this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(id)));
        }
    }

    private boolean isPublish(String id) {
        boolean flag = false;
        String sql = "select r5022 from r50 where r5000='" + id + "'";
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                String r5022 = this.frowset.getString("r5022");
                if ("04".equals(r5022))
                    flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

}
