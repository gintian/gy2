package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-6-8:10:38:27
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SaveModifyLawBaseTrans extends IBusiness {

	/** 递归语句 */
	String rootId = "0";

	int num = 0;

	String sqlFile = "select * from law_base_struct where up_base_id= ? ";

	PreparedStatement statement = null;

	ResultSet resultset = null;

	ArrayList updatelist = new ArrayList();

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		//RecordVo law_base_vo = (RecordVo) this.getFormHM().get("law_base_vo");
		LazyDynaBean bean = (LazyDynaBean) this.getFormHM().get("law_base_bean");
		String base_id =(String) bean.get("base_id");
		if (base_id == null || "".equals(base_id)) {
			base_id = "root";
		}
		ContentDAO dao1 = new ContentDAO(this.getFrameconn());
		RecordVo law_base_vo = new RecordVo("law_base_struct");
		law_base_vo.setString("base_id", base_id);
		try {
			law_base_vo = dao1.findByPrimaryKey(law_base_vo);
		} catch (GeneralException e2) {	e2.printStackTrace();} catch (SQLException e2) {e2.printStackTrace();}
		//this.getFormHM().put("law_base_vo", law_base_vo);
		if (law_base_vo == null)
			return;
		String up_base_id = law_base_vo.getString("base_id");
		if (up_base_id == null)
			return;

		/**
		 * 得到递归子项id号ArrayList
		 */
		rootId = up_base_id; // 记住初始父类id

		Connection con = this.getFrameconn();
		
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			dbS.open(con, sqlFile);
			statement = con.prepareStatement(sqlFile); // 递归sql语句执行

			updatelist = selectString(up_base_id, new ArrayList(), 0, false); // 得到递归ArrayList

		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		} finally{
			try {
				// 关闭Wallet
				dbS.close(con);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		String name = SafeCode.decode((String)bean.get("name"));
		//String name = law_base_vo.getString("name");
		this.getFormHM().put("name",SafeCode.encode(name));
		name = PubFunc.doStringLength(name, 50);

		String description = SafeCode.decode((String)bean.get("description"));

		description = PubFunc.doStringLength(description, 250);

		String status = "0";

		String check = "";
		check = (String)bean.get("check");
		try {
			law_base_vo = dao1.findByPrimaryKey(law_base_vo);
		} catch (GeneralException e2) {	e2.printStackTrace();} catch (SQLException e2) {e2.printStackTrace();}
		/**
		 * checkbox的处理
		 */
		//if (this.getFormHM().get("check") == null
		//		|| this.getFormHM().get("check").toString().equals("")) {
		if (check== null
				|| "".equals(check)) {
			try {
/*				status = "0";
				StringBuffer sb = new StringBuffer();
				sb.append("update law_base_struct set name='");
				sb.append(name);
				sb.append("',description='");
				sb.append(description);
				sb.append("',status=");
				sb.append(status);
				sb.append(" where base_id='");
				sb.append(up_base_id);
				sb.append("'");

				st.executeUpdate(sb.toString());*/
				law_base_vo.setString("status","0");
				law_base_vo.setString("name",name);
				law_base_vo.setString("description",description);
				dao1.updateValueObject(law_base_vo);
				
				this.getFormHM().put("status", "0");

			} catch (Exception ex) {
				throw GeneralExceptionHandler.Handle(ex);
			}
			doSunGradeOper(updatelist, name, description, status, up_base_id);
			// law_base_vo.setString("status","0");
		} else {
			//check = this.getFormHM().get("check").toString();
			if ("off".equals(check)) {
/*				status = "0";
				StringBuffer sb = new StringBuffer();
				sb.append("update law_base_struct set name='");
				sb.append(name);
				sb.append("',description='");
				sb.append(description);
				sb.append("',status=");
				sb.append(status);
				sb.append(" where base_id='");
				sb.append(up_base_id);
				sb.append("'");
*/
				try {

					//st.executeUpdate(sb.toString());
					law_base_vo.setString("status","0");
					law_base_vo.setString("name",name);
					law_base_vo.setString("description",description);
					dao1.updateValueObject(law_base_vo);
					
					this.getFormHM().put("status", "0");
				} catch (Exception ex) {
					throw GeneralExceptionHandler.Handle(ex);
				}
				
				doSunGradeOper(updatelist, name, description, status,
						up_base_id);

			} else if ("on".equals(check)) {
				/**
				 * 检查上级目录操作是否无效
				 */
				if (judgeSuperAva(up_base_id)) {

					this.getFormHM().put("message2", "修改不成功,上级目录是无效的,本级目录不能改为有效");
					this.getFormHM().put("check", "");
					throw new GeneralException("", "修改不成功,上级目录是无效的,本级目录不能改为有效", "",
							"");

				} else {
					/*status = "1";
					StringBuffer sb = new StringBuffer();
					sb.append("update law_base_struct set name='");
					sb.append(name);
					sb.append("',description='");
					sb.append(description);
					sb.append("',status=");
					sb.append(status);
					sb.append(" where base_id='");
					sb.append(up_base_id);
					sb.append("'");*/
					try {
						//st.executeUpdate(sb.toString());
						law_base_vo.setString("status","1");
						law_base_vo.setString("name",name);
						law_base_vo.setString("description",description);
						dao1.updateValueObject(law_base_vo);
						
						this.getFormHM().put("status", "1");
						
					} catch (Exception ex) {
						//throw GeneralExceptionHandler.Handle(new GeneralException("","输入无效，请不要使用 ’ 号","",""));
						throw GeneralExceptionHandler.Handle(ex);
					}
				}

			} else {
				/*status = "0";
				StringBuffer sb = new StringBuffer();
				sb.append("update law_base_struct set name='");
				sb.append(name);
				sb.append("',description='");
				sb.append(description);
				sb.append("',status=");
				sb.append(status);
				sb.append(" where base_id='");
				sb.append(up_base_id);
				sb.append("'");*/
				try {
					//st.executeUpdate(sb.toString());
					law_base_vo.setString("status","0");
					law_base_vo.setString("name",name);
					law_base_vo.setString("description",description);
					dao1.updateValueObject(law_base_vo);
					
					this.getFormHM().put("status", "0");
				} catch (Exception ex) {
					throw GeneralExceptionHandler.Handle(ex);
				}
				doSunGradeOper(updatelist, name, description, status,
						up_base_id);

			}

		}
		if("on".equalsIgnoreCase(check))
		{
			try {
				dao1
						.update("update law_base_file set valid = '1' where base_id='"
								+ (String) law_base_vo.getString("base_id")
								+ "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if ("off".equalsIgnoreCase(check) || "".equals(check)) {
			try {
				dao1
						.update("update law_base_file set valid = '0' where base_id='"
								+ (String) law_base_vo.getString("base_id")
								+ "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		uodateValid(check,updatelist);
		try {
			if (statement != null)
				statement.close();
			if (resultset != null)
				resultset.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 执行子级设为无效操作
	 */
	public void doSunGradeOper(ArrayList updatelist, String name,
			String description, String status, String up_base_id) {
		Connection con = this.getFrameconn();
		ContentDAO dao = new ContentDAO(con);
		String sql = "update law_base_struct set status=?  where base_id=?";
		try {
			for (int i = 0; i < updatelist.size(); i++) {
				List values=new ArrayList();
				values.add(status);
				values.add(updatelist.get(i).toString());
	        	dao.update(sql, values);
			}
		} catch (Exception ex) {

			ex.printStackTrace();

		}

	}
    public void uodateValid(String check,ArrayList list)
    {
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        if(list==null||list.size()<=0)
        	return;
        String valid="";
        String sql = "update law_base_file set valid=?  where base_id=?";
        if("on".equalsIgnoreCase(check))
        {
        	valid = "1";
					
        }else if("off".equalsIgnoreCase(check))
        {
        	valid = "0";
        }else
        {
        	return;
        }
        try
        {
        	String id=null;
        	ArrayList s_list=new ArrayList();
        	for(int i=0;i<list.size();i++)
        	{
        		ArrayList one_list=new ArrayList();
        		id=list.get(i).toString();
        		one_list.add(valid);
        		one_list.add(id);
        		s_list.add(one_list);
        	}
        	dao.batchUpdate(sql,s_list);
        }catch(Exception e)
        {
        	 e.printStackTrace();
        }
    }
	/**
	 * 输出字符的递归
	 */
	public ArrayList selectString(String parentId, ArrayList list,
			int position, boolean init) {
/*		Vector childList = new Vector();

		try {

			statement.setString(1, parentId);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				childList.addElement(resultset.getString("base_id"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		if (num == 1 && parentId.equals(rootId)) {

		} else {
			++num;

			list.add(parentId);
            for(int i=0;i<list.size();i++)
            {
            	String p_Id=list.get(i).toString();
            	//System.out.println(p_Id);
            	ArrayList chlidlist=getChlidSelectString(p_Id);
            	for(int r=0;r<chlidlist.size();r++)
            	{
            		list.add(chlidlist.get(r).toString());
            	}
            	//break;
            }
			/*int msize = childList.size();

			if (msize > 0) {
				for (int i = 0; i < msize; i++) {
					selectString((String) childList.elementAt(i), list, i,
							false);

				}

			}*/
		}
         
		return list;

	}
     public ArrayList getChlidSelectString(String parentId)
     {
    	 ArrayList list=new ArrayList();
    	 
    	 try {
            /*for(int i=0;i<parenlist.size();i++)
            {*/
            	//String parentId=parenlist.get(i).toString();
            	statement.setString(1, parentId);
     			resultset = statement.executeQuery();
     			while (resultset.next()) {
     				String base_id=resultset.getString("base_id");     				
     				if(base_id!=null&&!base_id.equals(parentId))
     				{
     					list.add(base_id);
     				}
     				

     			}
            //}
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return list;
     }
	/**
	 * 查上级目录是否为无效
	 * 
	 * @param base_id
	 * @return
	 */
	public boolean judgeSuperAva(String up_base_id) {
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
                 ArrayList values=new ArrayList();
                 values.add(id);
				ResultSet rs = null;
				rs=dao.search(sql,values);
//				rs = pstatement.getResultSet();
				if (rs.next()) {
					nowId = rs.getString("base_id");
					id = rs.getString("up_base_id");
					status = rs.getString("status");

					if ("0".equals(status) && !nowId.equals(rootId)) {
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
