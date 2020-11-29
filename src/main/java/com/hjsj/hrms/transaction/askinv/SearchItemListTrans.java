/*
 * Created on 2005-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchItemListTrans extends IBusiness {

	//ResultSet frowset=null;
    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM();//.get("requestPamaHM");
        //取得调查表id及名称
        String id = (String) hm.get("id");
        if (id == null || "".equals(id)) {
            if (this.getFormHM().get("id") != null) {
                id = this.getFormHM().get("id").toString();
            } else {
                id = "0";
            }
        } else {
            this.getFormHM().put("id", id);
        }

        String content = (String) hm.get("content");
        if (!(content == null || "".equals(content))) {
//            try {
//                content = ChangeStr.ToGbCode(content);
//            } catch (IOException ex) {
//            	ex.printStackTrace();
//            }
            this.getFormHM().put("content", content);
        }

        StringBuffer strsql = new StringBuffer();
        strsql.append("select * from investigate_item where id='" + id+"' order by itemid");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        try 
        {
            this.frowset = dao.search(strsql.toString());
        	//this.frowset=stmt.executeQuery(strsql.toString());
            while (this.frowset.next()) {
                RecordVo vo = new RecordVo("investigate_item");
                vo.setString("itemid", PubFunc.nullToStr(this.frowset.getString("itemid")));

                String temp = frowset.getString("id");
                if (temp == null || "".equals(temp)) {
                    vo.setString("id", "0");
                } else {
                    vo.setString("id", frowset.getString("id"));
                }

                temp = frowset.getString("name");
                if (temp == null || "".equals(temp)) {
                    vo.setString("name", "...");
                } else {
                    vo.setString("name", frowset.getString("name"));
                }
                vo.setString("status",PubFunc.NullToZero(frowset.getString("status")));
                if(frowset.getString("fillflag")!=null&&!"".equals(frowset.getString("fillflag"))&& "1".equals(frowset.getString("fillflag")))
                      vo.setString("fillflag", ResourceFactory.getProperty("lable.investigate_item.must_fillflag"));
                else
                	vo.setString("fillflag","");
                String selects=(frowset.getString("selects")==null|| "".equals(frowset.getString("selects")))?"0":frowset.getString("selects");
                vo.setString("selects", "1".equals(selects)?"是":"否");
                vo.setString("maxvalue", (frowset.getString("maxvalue")==null|| "0".equals(frowset.getString("maxvalue")))?"":frowset.getString("maxvalue"));
                vo.setString("minvalue", (frowset.getString("minvalue")==null|| "0".equals(frowset.getString("minvalue")))?"":frowset.getString("minvalue"));
                list.add(vo);
            }
            this.getFormHM().put("itemlist", list);
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } 
    }
}