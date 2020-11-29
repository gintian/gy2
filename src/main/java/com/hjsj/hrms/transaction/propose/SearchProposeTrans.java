package com.hjsj.hrms.transaction.propose;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * @author chenmengqing
 */
public class SearchProposeTrans extends IBusiness {

    /*
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String id = (String) hm.get("a_id");
        String flag = (String) this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。
         */
        if ("1".equals(flag))
        {
            this.getFormHM().put("check", "");
            this.getFormHM().put("replayCheck", "on");
            return;
        }
        cat.debug("------>suggest_id=====" + id);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RecordVo vo = new RecordVo("suggest");
        try 
        {
            String bread=(String)hm.get("bread");
            hm.remove("bread");
 
            String sql = this.getSearchSQL(id, flag);
            this.frowset=dao.search(sql);
            if(!this.frowset.next())
            {
                if("0".equals(flag))
                    throw GeneralExceptionHandler.Handle(new Exception("不能修改此条意见！"));
                else 
                    throw GeneralExceptionHandler.Handle(new Exception("不能回复此条意见！"));
            }else {
            	if(StringUtils.isNotEmpty(this.frowset.getString("replyuser"))) {
            		 throw GeneralExceptionHandler.Handle(new Exception("此意见已回复,不能修改此条意见！"));
            	}
            }

            vo.setString("id", id);
            vo = dao.findByPrimaryKey(vo);
            String annymous = vo.getString("annymous");
            if ("1".equals(annymous))
            {
                this.getFormHM().put("check", "on");
            } 
            else
            {
                this.getFormHM().put("check", "");
            }
            /**公开标识*/
            String flagcheck = vo.getString("flag");
            if ("1".equals(flagcheck))
            {
                this.getFormHM().put("replayCheck", "on");
            } 
            else 
            {
                this.getFormHM().put("replayCheck", "");
            }
            
            if(bread!=null&& "1".equals(bread))
              vo.setString("bread","1");
            dao.updateValueObject(vo);
        } 
        catch (Exception sqle)
        {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } 
        finally 
        {
            this.getFormHM().put("proposevo", vo);
        }
    }

    /**
     * 求查询内容的SQL语句
     * @return
     */
    private String getSearchSQL(String id, String flag)throws GeneralException  {
        
        String equalChar = "=";
        if(flag != null && "2".equals(flag))
            equalChar = "<>";           
        
        StringBuffer sql = new StringBuffer();
        sql.append("select replyuser from suggest");
        
//        if(this.userView.hasTheFunction("110501"))          
//        {
            String a0100whe = "or (e0122='"+this.userView.getUserDeptId()
                            + "'  and b0110='"+this.userView.getUserOrgId()
                            + "' and e01a1='"+this.userView.getUserPosId()
                            + "')) and createuser" + equalChar + "'"+this.userView.getUserFullName()+"' and id="+id;
            
            if("UN".equals(this.userView.getManagePrivCode().toString().trim()) && "".equals(this.userView.getManagePrivCodeValue().toString().trim()))
            {
                sql.append(" WHERE (1=1 or flag=1 " + a0100whe);
            }
            else if("UN".equals(this.userView.getManagePrivCode().toString().trim()))
            {
                sql.append(" where ((B0110 like  '"+this.userView.getUserOrgId()+"%') or ( flag=1)"+ " "+a0100whe);
            }
            else if("UM".equals(this.userView.getManagePrivCode().toString().trim()))
            {
                sql.append(" where ((E0122 like  '"+this.userView.getUserDeptId()+"%') or (flag=1)"+ " "+a0100whe);
            }
            else if("UN".equals(this.userView.getManagePrivCode().toString().trim())&&!"".equals(this.userView.getManagePrivCodeValue().toString().trim()))
            {
                sql.append(" where ((B0110 like  '"+this.userView.getManagePrivCodeValue()+"%') or ( flag=1)"+ " "+a0100whe);
            }
            else if("UM".equals(this.userView.getManagePrivCode().toString().trim())&&!"".equals(this.userView.getManagePrivCodeValue().toString().trim()))
            {
                sql.append(" where ((E0122 like  '"+this.userView.getManagePrivCodeValue()+"%') or ( flag=1)"+ " "+a0100whe);
            }        
            else
            {
                sql.append(" where ((createuser " + equalChar + " '"+this.userView.getUserFullName()+"' ) or (flag=1)"+ " "+a0100whe);
            }
    
        //只能答复非本人的意见
        if("<>".equals(equalChar))
        {
            if(this.userView.hasTheFunction("110501"))
            {
                sql.append(" AND createuser<>'" + this.userView.getUserFullName() + "'");
                //sql.append(" AND flag<>1");
            }
            else
                sql.append(" AND 1=2");
        }
        else
        {
            //只能修改本人的未答复的意见
            sql.append(" AND createuser='" + this.userView.getUserFullName() + "'");
        }
            
        return sql.toString();
    }
}