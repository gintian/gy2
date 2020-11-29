package com.hjsj.hrms.module.system.viewboard.transaction;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 公告维护
 * 
 * @createtime Mar 02, 2017 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class NoticeTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {

		try {
			// 取值
			String topic = (String) this.getFormHM().get("notice_name");// 主题
			
			//加密后的的特殊字符，使用前需要解密。解决 websphere环境报错的问题  郝树林 2017-06-24 update
			String content = (String) this.getFormHM().get("notice_content");// 内容
			if(StringUtils.isNotBlank(content)){
				content = PubFunc.keyWord_reback(SafeCode.decode(content));
			}
			String period = (String) this.getFormHM().get("notice_time");// 公告期
			String priority = (String) this.getFormHM().get("notice_seq");// 优先级
			List notice_object_list = (ArrayList) this.getFormHM().get("notice_object");// 通知对象
			boolean isApproved = (Boolean) this.getFormHM().get("isApproved");// 是否直批
			// 通知类型 1 ehr系统公告栏 2 招聘首页公告 3 社会招聘公告 4 校园招聘公告 11 培训新闻13外网公示信息
			int flag = (Integer) this.getFormHM().get("flag");

			// 《通知表》数据准备
			RecordVo vo = new RecordVo("announce");
			IDFactoryBean idf = new IDFactoryBean();
			int announce_id = Integer.parseInt(idf.getId("announce.id", "",this.getFrameconn()));
			vo.setInt("id", announce_id);
			vo.setString("topic", topic);
			vo.setString("content", content);
			vo.setInt("period", Integer.parseInt(period));
			vo.setInt("priority", Integer.parseInt(priority));
			if(isApproved){//直批
				vo.setInt("approve", 1);
				vo.setDate("approvetime", DateStyle.getSystemTime());
				vo.setString("approveuser", this.userView.getUserFullName());
			} else {
				vo.setInt("approve", 0);
			}
			vo.setInt("viewcount", 0);
			vo.setString("ext", "");
			vo.setInt("flag", flag);
			vo.setString("createuser", this.userView.getUserFullName());
			vo.setDate("createtime", DateStyle.getSystemTime());

			// 插入通知
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			int result = dao.addValueObject(vo);

			// 更新通知对象
			if (result == 1 && notice_object_list.size() > 0) {
				
				String privStr = "";
				String roleStr = "", perStr = "", orgStr = "";
				for (Object o : notice_object_list) {
					MorphDynaBean object = (MorphDynaBean) o;
					String id_e = (String) object.get("id");
					String id = PubFunc.decrypt(id_e);
					// String name = (String)object.get("name");
					String type = (String) object.get("type");
					String orgpre = "";
					if ("org".equalsIgnoreCase(type)) {
						orgpre = (String) object.get("orgpre");
					}
					// 组合成老公告模块保存接口需要的字符串形式。
					if ("role".equalsIgnoreCase(type)) {// 角色
						roleStr += ("1:" + id + ",");
					} else if ("person".equalsIgnoreCase(type)) {// 人员
						perStr += ("4:" + id + ",");
					} else if ("org".equalsIgnoreCase(type)) {// 机构
						orgStr += (orgpre + id + "`");
					}
				}
				if (StringUtils.isNotEmpty(orgStr)) {
					orgStr += ",";
				}
				privStr = roleStr + perStr + orgStr;

				BoardBo boardBo = new BoardBo(this.getFrameconn(), this.getUserView());
				boardBo.savePriv(privStr, String.valueOf(announce_id));
			}

			if (result == 1) {// 添加成功
				this.getFormHM().put("errorcode", "0");
			} else {// 失败
				this.getFormHM().put("errorcode", "1");
			}

		} catch (Exception e) {
			this.getFormHM().put("errorcode", "1");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
