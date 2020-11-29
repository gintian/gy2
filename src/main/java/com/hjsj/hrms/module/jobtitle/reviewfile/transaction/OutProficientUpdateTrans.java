package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 职称评审_上会材料_鉴定专家_保存
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 */
public class OutProficientUpdateTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	RowSet rs = null;
        String msg = null;
    	try {
            ArrayList datalist = (ArrayList) this.getFormHM().get("savedata");//更新的数据
            String w0301 = "";
            for(int i=0; i<datalist.size(); i++){
            	
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				String userid = "";
				if(map.get("user_id_e") != null){
					userid = PubFunc.decrypt(map.get("user_id_e").toString());
				}
				w0301 = PubFunc.decrypt(map.get("w0301_e").toString());
				String w0501 = PubFunc.decrypt(map.get("w0501_e").toString());
				String username = "";
				String password = "";
				String description = "";
				int state = 0;
					
				if(map.get("username") == null || "".equals(map.get("username").toString().trim())) {
					msg = "账号不允许为空！";
					return ;
				}
				if(map.get("password") != null){// 密码可以为空，不控制
					password = map.get("password").toString();
				}
				username = map.get("username").toString();
				if(map.get("state") != null){
					state = Integer.parseInt(map.get("state").toString());
				}
				if(map.get("description") != null){
					description = map.get("description").toString();
				}
				
				// 判断更新的是用户名时才去校验是否存在
				boolean isUpdateUserName = true;
				if(StringUtils.isNotEmpty(userid)){//更新时
					String sql = "select username from zc_expert_user where user_id='"+userid+"'";
					rs = dao.search(sql);
					if(rs.next()){
						String _username = rs.getString("username");
						if(username.equalsIgnoreCase(_username)){
							isUpdateUserName = false;
						}
					}
				}
				
				
				// 检查用户名是否已存在:更新的是用户名时才去校验是否存在
				if(isUpdateUserName){
					String sql = "select count(user_id) as count from zc_expert_user where username='"+username+"'";
					if(StringUtils.isNotEmpty(userid)){//更新的时候，要把当前更新的这条数据排除
						sql +=  " and user_id<>'"+userid+"'";
					}
					rs = dao.search(sql);
					if(rs.next() && rs.getInt("count")>0){
						msg = "该账号已存在！";
						return ;
					}
				}
				
				RecordVo vo = new RecordVo("zc_expert_user");
				//账号表序号、学科组编号、会议ID 、申报人主键序号ID 、帐号、密码、帐号状态、帐号类型、专家编号、描述信息、角色
				vo.setString("group_id", "");
				vo.setString("w0301", w0301);
				vo.setString("w0501", w0501);
				vo.setString("username", username);
				vo.setString("password", password);
				vo.setInt("state", state);
				vo.setInt("type", 3);
				vo.setString("description", description);
				vo.setString("role", null);
				vo.setInt("usetype", 2);
				
				if(map.get("isNew") != null) {// 新增记录
					IDFactoryBean idf = new IDFactoryBean();
					vo.setString("user_id", idf.getId("zc_expert_user.user_id", "", this.frameconn));
					vo.setString("w0101", null);
					dao.addValueObject(vo);
				} else {// 更新记录
					vo.setString("user_id", userid);
					dao.updateValueObject(vo);
				}
            }
            
            // 同步学科组人数
            ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
            reviewFileBo.asyncPersonNum(w0301,3);
        } catch (Exception ex) {
        	ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
        } finally {
			PubFunc.closeDbObj(rs);
			if(StringUtils.isNotEmpty(msg)){
				this.getFormHM().put("result", false);
				this.getFormHM().put("hinttext", msg);
			}
		}
    }
}
