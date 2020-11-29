package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:SaveLawBaseTrans
 * </p>
 * <p>
 * Description:保存规章制度添加目录操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 2, 2005:11:19:48 AM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveLawBaseTrans extends IBusiness {

	public SaveLawBaseTrans() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String basetype = (String) getFormHM().get("basetype");
		String dir = "";
		/**
		 * 判断是否是管理员如果是机构号默认为-1不是填入机构号
		 */
		if (userView.isSuper_admin()) {
			dir = "-1";
		} else {
			dir = userView.getUserOrgId();
		}
		if(dir==null||dir.length()<=0)
		{
			dir="-1";
		}
		RecordVo law_base_vo = (RecordVo) this.getFormHM().get("law_base_vo");
		int row = 0;
		if (law_base_vo == null)
			return;
		law_base_vo.setInt("basetype", Integer.parseInt(basetype));
		String up_base_id = law_base_vo.getString("up_base_id");
		String check_base_id = law_base_vo.getString("base_id");
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		String base_id = idg.getId("law_base_struct.id");
		/**
		 * 根级栏目下的子级添加标识
		 */
		String rootFlag = "0";
		StringBuffer strsql = new StringBuffer();
		ContentDAO orderdao = new ContentDAO(this.getFrameconn());
		strsql
				.append("select max(DisplayOrder) as displayorder from law_base_struct ");
		if ("root".equals(up_base_id))
			strsql.append("where base_id=up_base_id ");
		else {
			strsql.append("where up_base_id='");
			strsql.append(up_base_id);
			strsql.append("' and base_id<>up_base_id");
		}
		try {
			this.frowset = orderdao.search(strsql.toString());
			if (this.frowset.next()) {
				String displayorder = this.frowset.getString("displayorder");
				if (displayorder == null)
					law_base_vo.setInt("displayorder", Integer.parseInt("1"));
				else
					law_base_vo.setInt("displayorder", Integer
							.parseInt(displayorder) + 1);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}

		if ("root".equals(up_base_id)) {
			law_base_vo.setString("base_id", base_id);
			law_base_vo.setString("up_base_id", base_id);
			rootFlag = "1";
		} else {
			rootFlag = "0";
			law_base_vo.setString("base_id", base_id);
		}
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/**
		 * 目录有效标识
		 */

		String check = "";
		if (this.getFormHM().get("check") == null
				|| "".equals(this.getFormHM().get("check").toString())) {
			law_base_vo.setString("status", "0");
		} else {
			check = this.getFormHM().get("check").toString();
			if ("off".equals(check)) {
				law_base_vo.setString("status", "0");
			} else if ("on".equals(check)) {
				law_base_vo.setString("status", "1");
			} else {
				law_base_vo.setString("status", "0");
			}
		}

		// System.out.println(law_base_vo);
		/**
		 * 根目录下面的添加子级操作
		 */
		if ("1".equals(rootFlag)) {
			String name = law_base_vo.getString("name");
			name = PubFunc.doStringLength(name, 50);

			law_base_vo.setString("name", name);
			String description = law_base_vo.getString("description");
			description = PubFunc.doStringLength(description, 250);
			law_base_vo.setString("description", description);
			law_base_vo.setString("dir", dir);
			row = dao.addValueObject(law_base_vo);
		}
		/**
		 * 非根目录下的添加子级操作
		 */
		else {
			/**
			 * 检查上级目录操作是否无效
			 */
			if (judgeSuperAva(up_base_id, check_base_id)) {
				if ("0".equals(law_base_vo.getString("status").toString())) {
					String name = law_base_vo.getString("name");
					name = PubFunc.doStringLength(name, 50);
					law_base_vo.setString("name", name);
					String description = law_base_vo.getString("description");
					description = PubFunc.doStringLength(description, 250);
					law_base_vo.setString("description", description);
					law_base_vo.setString("dir", dir);
					row = dao.addValueObject(law_base_vo);
				} else {
					/**
					 * 添加不能通过 提示上级目录是无效的,请选无效
					 */
					this.getFormHM().put("message2", "添加不成功,上级目录是无效的,请选无效");
					this.getFormHM().put("check", "");
					throw new GeneralException("", "添加不成功,上级或本级目录是无效的,请选无效","", "");
				}
			} else {
				String name = law_base_vo.getString("name");
				name = PubFunc.doStringLength(name, 50);

				law_base_vo.setString("name", name);
				String description = law_base_vo.getString("description");
				description = PubFunc.doStringLength(description, 250);
				law_base_vo.setString("description", description);
				law_base_vo.setString("dir", dir);
				row = dao.addValueObject(law_base_vo);
			}

		}
		if (row > 0 && base_id != null && !"".equals(base_id)) {
			UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
			user_bo.saveResource(base_id, this.userView,
					IResourceConstant.LAWRULE);
		}

	}

	/**
	 * 查上级目录
	 * 
	 * @param base_id
	 * @return
	 */
	public boolean judgeSuperAva(String up_base_id, String base_id) {
		boolean flage = false;
		Connection con = this.getFrameconn();
		String sql = "select base_id,up_base_id,status from law_base_struct where base_id=?";
		PreparedStatement pstatement = null;
		String id = up_base_id;
		String status = "";
		String nowId = "";
		ContentDAO dao = new ContentDAO(con);
		try {
			int whileflag = 0;
			do {
				ResultSet rs = null;
				List values=new ArrayList();
	        	values.add(id);
	        	rs=dao.search(sql, values);
				if (rs.next()) {
					nowId = rs.getString("base_id");
					id = rs.getString("up_base_id");
					status = rs.getString("status");
					if ("0".equals(status)) {
						flage = true;
						whileflag = 1;

					}
					if (nowId.equals(id)) {
						whileflag = 1;
					}
				} else {
					whileflag = 1;
				}
				if (rs != null) {
					rs.close();
				}
			} while (whileflag == 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flage;
	}

}