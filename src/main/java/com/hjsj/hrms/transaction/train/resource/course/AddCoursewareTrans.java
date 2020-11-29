/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:AddCoursewareTrans
 * </p>
 * <p>
 * Description:添加培训课程课件
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
public class AddCoursewareTrans extends IBusiness {

    /**
	 * 
	 */
    public AddCoursewareTrans() {
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
        hm.remove("id");
        String a_code = (String) hm.get("a_code");
        a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
        a_code = PubFunc.decrypt(SafeCode.decode(a_code));
        hm.remove("a_code");
        String r5100 = (String) hm.get("r5100");
        r5100 = r5100 != null && r5100.trim().length() > 0 ? r5100 : "";
        r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
        hm.remove("r5100");
        ArrayList fieldList = new ArrayList();
        Map r5105 = new HashMap();
        String url = "";// 外部URl连接
        String r5105codesetid = "57";
        try {
            ArrayList itemList = DataDictionary.getFieldList("r51", Constant.USED_FIELD_SET);
            RecordVo vo = null;
            ContentDAO dao = null;
            dao = new ContentDAO(this.getFrameconn());
            if (!"".equals(r5100)) {
                this.getFormHM().put("r5100",  SafeCode.encode(PubFunc.encrypt(r5100)));
                vo = new RecordVo("r51");
                vo.setString("r5100", r5100);

                vo = dao.findByPrimaryKey(vo);
            }
            for (int i = 0; i < itemList.size(); i++) {
                FieldItem item = (FieldItem) itemList.get(i);
                if ("R5100".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
                if ("0".equals(item.getState()))
                    continue;
                if ("r5105".equalsIgnoreCase(item.getItemid()))
                    r5105codesetid = item.getCodesetid();// 对应代码项

                if ("".equals(r5100)) {
                    this.getFormHM().put("r5100", null);
                    item.setValue("");
                    item.setViewvalue("");
                } else {
                    if ("r5105".equalsIgnoreCase(item.getItemid())) {
                        item.setValue(vo.getString(item.getItemid()));
                        RecordVo rv = new RecordVo("codeitem");
                        rv.setString("codesetid", item.getCodesetid());// "57");
                        rv.setString("codeitemid", vo.getString(item.getItemid()));
                        try {
                            rv = dao.findByPrimaryKey(rv);
                            item.setViewvalue(rv.getString("codeitemdesc"));
                        } catch (Exception e) {
                            item.setViewvalue("");
                        }
                    } else if ("r5113".equalsIgnoreCase(item.getItemid())) {
                        String r5113 = vo.getString("r5113");
                        if (r5113 != null && r5113.length() > 0) {
                            vo.setString("r5113", r5113.substring(r5113.lastIndexOf("\\") + 1));
                            if ("6".equals(vo.getString("r5105"))) {
                                url = r5113;
                                vo.setString("r5113", "");
                            }
                        }
                        item.setValue(vo.getString(item.getItemid()));
                    } else {
                        item.setValue(vo.getString(item.getItemid()));
                    }
                }
                fieldList.add(item);
            }

            // if(vo!=null&&"6".equals(vo.getString("r5105")))
            // url = vo.getString("r5113");

            // 查询所有课件类型
            String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='" + r5105codesetid + "' and invalid=1";
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                if("5".equalsIgnoreCase(this.frowset.getString("codeitemid")))//屏蔽掉程序中的aicc类型的课件 chengxg 2015-08-07
                    continue;
                
                r5105.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("itemlist", fieldList);
            this.getFormHM().put("tablename", "r51");
            this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
            this.getFormHM().put("id", SafeCode.encode(PubFunc.encrypt(id)));
            this.getFormHM().put("r5105", r5105);
            this.getFormHM().put("url", url);
        }
    }

}
