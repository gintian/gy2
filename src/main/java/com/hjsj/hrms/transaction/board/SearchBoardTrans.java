/*
 * Created on 2005-5-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchBoardTrans extends IBusiness {
	String announce=null;
	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		announce=(String)this.getFormHM().get("announce");
		String type=(String)this.getFormHM().get("type");
		//linbz  332 20170425 增加删除附件功能
		String state=(String)this.getFormHM().get("state");
		state = StringUtils.isEmpty(state) ? "" : state;
		if("deletFile".equalsIgnoreCase(state)){
			String id=(String)this.getFormHM().get("id");
			id = StringUtils.isEmpty(id) ? "" : id;
			String fileType=(String)this.getFormHM().get("ext");
			fileType = StringUtils.isEmpty(fileType) ? "" : fileType;
			if(StringUtils.isEmpty(fileType) && StringUtils.isEmpty(id)){
	        	return;
	        }else{
	        	//删除附件 同时删除库中对应的附件文件的后缀名
	        	StringBuffer strInsert = new StringBuffer();
	    		strInsert.append("update  announce set ext=null,thefile=null where id=?");
	    		ArrayList list = new ArrayList();
	    		list.add(id);
	    		ContentDAO dao = new ContentDAO(this.getFrameconn());
	    		try {
					dao.update(strInsert.toString(), list);
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        }
			try {
				VfsService.deleteFileGroup(this.userView.getUserName(),"announce_"+id, VfsModulesEnum.NOLOGIN);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		SQLExecute();
		
		String opt=(String)this.getFormHM().get("opt");
		this.getFormHM().put("opt", opt);
		this.getFormHM().put("annouce", announce);
		this.getFormHM().put("type", type);
		ArrayList msgList = this.getMessageTmpList();

		String chflag=(String) this.getFormHM().get("chflag");
		String tmpnbase="";
		if ("1".equalsIgnoreCase(chflag)) {
			ConstantXml constantbo = new ConstantXml(this.frameconn, "TR_PARAM");
			tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		}
		this.getFormHM().put("msgList", msgList);
		this.getFormHM().put("tmpnbase", tmpnbase);
	}

	/**
	 * SQL操作
	 * 
	 * @throws GeneralException
	 */

	public void SQLExecute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String approve = (String) hm.get("approve");

		String id = (String) hm.get("a_id");
		hm.remove("id");
		
		String chflag = (String) hm.get("chflag");
		chflag=chflag!=null?chflag:"";
		hm.remove("chflag");
		
		String trainid = (String) hm.get("trainid");
		trainid=trainid!=null?trainid:"";
		hm.remove("trainid");
		
		String opt = (String) hm.get("opt");
		if(opt == null)
		    opt = (String)this.getFormHM().get("opt");
		opt = opt != null ? opt : "";
        hm.remove("opt");
		
		String flag = (String) this.getFormHM().get("flag");
		/**
		 * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。
		 */
		this.getFormHM().put("spersonlist", selectPer());
		this.getFormHM().put("sperson","00");
		this.getFormHM().put("chflag",chflag);
		this.getFormHM().put("trainid",trainid);
		this.getFormHM().put("opt", opt);
		
		RecordVo vo = new RecordVo("announce");
		
		if ("1".equals(flag)){
			vo.removeValues();
			String titlename = (String)hm.get("titlename");
			titlename=titlename!=null&&titlename.trim().length()>0?titlename:"";
			hm.remove("titlename");
			titlename = SafeCode.decode(titlename);
			
			vo.setString("topic",titlename);
			this.getFormHM().put("boardTb", vo);
			this.getFormHM().put("noticeperson", "");
			this.getFormHM().put("selectPerson", "");
			return;
		}
		cat.debug("------>announce_id=====" + id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		try {
			vo.setString("id", id);
			vo = dao.findByPrimaryKey(vo);
			if ("3".equals(flag)) {
				String content = vo.getString("content");
				//content = PubFunc.toHtml(content);
				vo.setString("content", content);
			}
			BoardBo boardBo = new BoardBo(this.getFrameconn(),this.userView);
			String pArr[] = boardBo.getPriUser(id);
			if(pArr!=null&&pArr.length==2){
				this.getFormHM().put("noticeperson", pArr[1]);
				this.getFormHM().put("selectPerson", pArr[0]);
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("boardTb", vo);
		}
	}

	public void OrcaleExecute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String approve = (String) hm.get("approve");

		String id = (String) hm.get("a_id");
		String flag = (String) this.getFormHM().get("flag");
		/**
		 * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。
		 */
		if ("1".equals(flag))
			return;
		cat.debug("------>announce_id=====" + id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("announce");
		String sql = "select topic,content,createuser,createtime,period,approve,approveuser,approvetime,ext from announce where id="
				+ id;
		
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			vo.setString("id", id);
			// this.frowset=dao.search(sql);
			if (rs.next()) {
				vo.setString("topic", PubFunc.nullToStr(rs.getString("topic")));
				vo.setString("content", PubFunc.nullToStr(Sql_switcher
						.readMemo(rs, "content")));
				vo.setString("createuser", PubFunc.nullToStr(rs
						.getString("createuser")));
				vo.setString("createtime", PubFunc.DoFormatDate(rs
						.getString("createtime")));
				vo.setString("period", PubFunc.NullToZero(rs
						.getString("period")));
				vo.setString("approve", PubFunc.nullToStr(rs
						.getString("approve")));
				vo.setString("approveuser", PubFunc.nullToStr(rs
						.getString("approveuser")));
				String approvetime = PubFunc.DoFormatDate(rs
						.getString("approvetime"));
				vo.setString("ext", PubFunc.nullToStr(rs.getString("ext")));
			}
			// vo=dao.findByPrimaryKey(vo);

			if ("3".equals(flag)) {
				String content = vo.getString("content");
				content = PubFunc.toHtml(content);
				vo.setString("content", content);

			}
		} catch (OutOfMemoryError error) {
			System.out
					.println("------>SearchBoardTrans---->OutOfMemoryError-->");
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("boardTb", vo);
		    PubFunc.closeResource(rs);
		}
	}
	private ArrayList selectPer(){
		ArrayList list = new ArrayList();
		CommonData temp=new CommonData("00","请选择");
		list.add(temp);
		temp=new CommonData("01","人员");
		list.add(temp);
		temp=new CommonData("02","角色");
		list.add(temp);
		if("1".equals(announce)){
			temp=new CommonData("03","机构");
			list.add(temp);
		}
		return list;
	}
	
	public ArrayList getMessageTmpList()
	{
		ArrayList list = new ArrayList();
		
		String sql = "SELECT id,name FROM EMAIL_NAME WHERE nModule=1 AND nInfoclass=1 ORDER BY id";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs;
		try
		{			
			rs = dao.search(sql);
			while(rs.next())
			{
				String tmpId = rs.getString("id");
				String tmpName = rs.getString("name");
				CommonData data = new CommonData(tmpId, tmpName);
				
				list.add(data);
			}	
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public void db2Execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String approve = (String) hm.get("approve");

		String id = (String) hm.get("a_id");
		String flag = (String) this.getFormHM().get("flag");
		/**
		 * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。
		 */
		if ("1".equals(flag))
			return;
		cat.debug("------>announce_id=====" + id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("announce");
		String sql = "select topic,content,createuser,createtime,period,approve,approveuser,approvetime,ext from announce where id="
				+ id;
		
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			vo.setString("id", id);
			// this.frowset=dao.search(sql);
			if (rs.next()) {
				vo.setString("topic", PubFunc.nullToStr(rs.getString("topic")));
				vo.setString("content", PubFunc.nullToStr(Sql_switcher
						.readMemo(rs, "content")));
				vo.setString("createuser", PubFunc.nullToStr(rs
						.getString("createuser")));
				vo.setString("createtime", PubFunc.DoFormatDate(rs
						.getString("createtime")));
				vo.setString("period", PubFunc.NullToZero(rs
						.getString("period")));
				vo.setString("approve", PubFunc.nullToStr(rs
						.getString("approve")));
				vo.setString("approveuser", PubFunc.nullToStr(rs
						.getString("approveuser")));
				String approvetime = PubFunc.DoFormatDate(rs
						.getString("approvetime"));
				vo.setString("ext", PubFunc.nullToStr(rs.getString("ext")));
			}
			// vo=dao.findByPrimaryKey(vo);

			if ("3".equals(flag)) {
				String content = vo.getString("content");
				content = PubFunc.toHtml(content);
				vo.setString("content", content);

			}
		} catch (OutOfMemoryError error) {
			System.out
					.println("------>SearchBoardTrans---->OutOfMemoryError-->");
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("boardTb", vo);
			PubFunc.closeResource(rs);
		}

	}

}
