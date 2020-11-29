package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.businessobject.board.SendBoard;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:欢迎页面查阅公告
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-6-3:11:45:04
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchWelcomeBoardViewTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String id = (String) hm.get("a_id");
        String annouceFlag = (String)hm.get("annouceflag");
        if (annouceFlag == null)
            annouceFlag = "";

    
//       if (!annouceFlag.equals("11") && !(this.userView.isHaveResource(IResourceConstant.ANNOUNCE, id))) // 2015-05-11 (首页公告面板)公告可以设置接收范围下的人，无需为每个人授予资源权限
//		      id = "-1";

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RecordVo vo = new RecordVo("announce");
        String sql = "select topic,content,createuser,createtime,period,approve,approveuser,approvetime,ext,viewcount,flag,fileid from announce where id=?";
        ArrayList paramList = new ArrayList();
        paramList.add(id);

        try
        {
            vo.setString("id", id);
            this.frowset = dao.search(sql, paramList);
            if (this.frowset.next())
            {
                vo.setString("topic", PubFunc.nullToStr(this.frowset.getString("topic")));
                vo.setString("content", Sql_switcher.readMemo(this.frowset, "content"));
                vo.setString("createuser", PubFunc.nullToStr(this.frowset.getString("createuser")));
                vo.setDate("createtime", PubFunc.FormatDate(this.frowset.getDate("createtime")));
                vo.setString("period", PubFunc.NullToZero(this.frowset.getString("period")));
                vo.setString("approve", PubFunc.NullToZero(this.frowset.getString("approve")));
                vo.setString("approveuser", PubFunc.nullToStr(this.frowset.getString("approveuser")));
                vo.setDate("approvetime", PubFunc.FormatDate(this.frowset.getDate("approvetime")));
                vo.setString("ext", PubFunc.nullToStr(this.frowset.getString("ext")));
                vo.setString("viewcount", PubFunc.nullToStr(this.frowset.getString("viewcount")));
                vo.setString("flag", PubFunc.NullToZero(this.frowset.getString("flag")));
                vo.setString("fileid", PubFunc.nullToStr(this.frowset.getString("fileid")));
            }

            String content = vo.getString("content");
            // content = PubFunc.toHtml(content);
            vo.setString("content", content);
            this.getFormHM().put("boardTb", vo);
            String task_id = (String) hm.get("task_id");
            if (task_id == null || task_id.length() < 1)
            {
                sql = "select task_id from per_task_pt WHERE plan_id='" + id + "' AND nbase='" + this.userView.getDbname() + "' AND mainbody_id='" + this.userView.getA0100() + "' AND flag=100";
                this.frowset = dao.search(sql);
                if (this.frowset.next())
                {
                    task_id = this.frowset.getString("task_id");
                    SendBoard sb = new SendBoard(this.frameconn, this.userView);
                    sb.delBoard(id, this.userView.getDbname(), this.userView.getA0100(), task_id);
                }
            }
            else
            {
                SendBoard sb = new SendBoard(this.frameconn, this.userView);
                sb.delBoard(id, this.userView.getDbname(), this.userView.getA0100(), task_id);
            }
            //dao.update("update announce set viewcount = viewcount + 1 where id=" + id);
            //viewcount为null时，修改不成功，   jingq  upd  2014.07.11
            String usql = "update announce set viewcount = "+Sql_switcher.isnull("viewcount", "0")+"+1 where id=" + id;
            dao.update(usql);
            this.getFormHM().put("id", id);
            this.getFormHM().put("annouceFlag", annouceFlag);
        }
        catch (Exception sqle)
        {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        finally
        {

        }
    }
}
