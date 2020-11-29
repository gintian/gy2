package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * 
 * <p>
 * Title:UpdateAccordTitleTrans.java
 * </p>
 * <p>
 * Description:读取对应方案更名
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-10-9
 * </p>
 * 
 * @author fanzhiguo
 * @version 4.0
 */
public class UpdateAccordTitleTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		String oper = (String) this.getFormHM().get("oper");
		String id = (String) this.getFormHM().get("id");
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			if (oper != null && "0".equals(oper))
			{
				String newTitle = (String) this.getFormHM().get("newTitle");
				newTitle = SafeCode.decode(newTitle);

				RecordVo vo = new RecordVo("gz_relation");
				vo.setInt("id", Integer.parseInt(id));

				vo = dao.findByPrimaryKey(vo);
				vo.setString("name", newTitle);
				dao.updateValueObject(vo);

			} else if (oper != null && "1".equals(oper))
			{
				int currentSeq = 0;
				int previousSeq = 0;
				int previousid = 0;
				int nextSeq = 0;
				int nextid = 0;

				this.frowset = dao.search("select seq from gz_relation where id=" + id);
				if (this.frowset.next())
					currentSeq = this.frowset.getInt("seq");

				this.frowset = dao.search("select * from gz_relation where seq>" + currentSeq + " order by seq");
				if (this.frowset.next())
				{
					nextSeq = this.frowset.getInt("seq");
					nextid = this.frowset.getInt("id");
				}

				this.frowset = dao.search("select * from gz_relation where seq<" + currentSeq + " order by seq desc");
				if (this.frowset.next())
				{
					previousSeq = this.frowset.getInt("seq");
					previousid = this.frowset.getInt("id");
				}

				String updateStr = "update gz_relation set seq=? where id=?";
				ArrayList list = new ArrayList();
				String act = (String) this.getFormHM().get("act");
				if ("up".equalsIgnoreCase(act))
				{
					ArrayList list1 = new ArrayList();
					list1.add(new Integer(previousSeq));
					list1.add(new Integer(id));
					list.add(list1);

					ArrayList list2 = new ArrayList();
					list2.add(new Integer(currentSeq));
					list2.add(new Integer(previousid));
					list.add(list2);

				} else if ("down".equalsIgnoreCase(act))
				{
					ArrayList list1 = new ArrayList();
					list1.add(new Integer(nextSeq));
					list1.add(new Integer(id));
					list.add(list1);

					ArrayList list2 = new ArrayList();
					list2.add(new Integer(currentSeq));
					list2.add(new Integer(nextid));
					list.add(list2);

				}
				dao.batchUpdate(updateStr, list);
			}

		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
