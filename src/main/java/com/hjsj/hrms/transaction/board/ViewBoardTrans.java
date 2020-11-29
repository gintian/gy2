/*
 * Created on 2005-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.board;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewBoardTrans extends IBusiness {

    public void execute() throws GeneralException {
        SQLExecute();
    }

    /**
     * SQL操作
     * @throws GeneralException
     */
    private void SQLExecute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String id = (String) hm.get("a_id");
        String flag = (String) this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if (!"2".equals(flag))
            return;
        cat.debug("------>announce_id=====" + id);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RecordVo vo = new RecordVo("announce");
        try {
            vo.setString("id", id);
            vo = dao.findByPrimaryKey(vo);
            String content = vo.getString("content") == null ? "" : vo.getString("content");
            //content=PubFunc.toHtml(content);
            vo.setString("content", content);
            this.getFormHM().put("boardTb", vo);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
        }
    }
}
