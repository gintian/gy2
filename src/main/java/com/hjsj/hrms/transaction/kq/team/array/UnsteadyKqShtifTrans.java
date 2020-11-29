package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 不定班次排班
 * <p>Title:UnsteadyKqShtifTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 7, 2006 5:37:02 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class UnsteadyKqShtifTrans extends IBusiness implements KqClassArrayConstant{

	public void execute() throws GeneralException
	{
		//kq_org_dept_able_shift不定期排班表
		//org_dept_id 部门编号  ，class_id  班次编号，   codesetid部门编码  
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		if(code==null||code.length()<=0){
			code="";			
		}		
		if(kind==null||kind.length()<=0)
		{
			kind=RegisterInitInfoData.getKindValue(kind,this.userView);
			code="";
		}
		
		//codesetid,codeitemid
		String codesetid="";
		if("2".equalsIgnoreCase(kind))
		{
			codesetid="UN";
		}else if("1".equalsIgnoreCase(kind))
		{
			codesetid="UM";
		}else if("0".equalsIgnoreCase(kind))
		{
			codesetid="@K";
		}else
		{
			codesetid="UN";
		}
		StringBuffer sql=new StringBuffer();
		sql.append("select a.class_id as class_id,name,onduty_1,offduty_1,onduty_2,offduty_2");
		sql.append(",onduty_3,offduty_3,onduty_4,offduty_4 ");
		sql.append(" from kq_class a,kq_org_dept_able_shift b");
		sql.append(" where a.class_id=b.class_id ");
		sql.append(" and b.org_dept_id='"+code+"'");
		sql.append(" and UPPER(b.codesetid)='"+codesetid.toUpperCase()+"'");
		RecordVo vo=null;
		ArrayList vo_list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				vo= new RecordVo("kq_class");
				vo.setString("name",this.frowset.getString("name"));
				vo.setString("class_id",this.frowset.getString("class_id"));
				vo.setString("onduty_1",this.frowset.getString("onduty_1"));
				vo.setString("offduty_1",this.frowset.getString("offduty_1"));
				vo.setString("onduty_2",this.frowset.getString("onduty_2"));
				vo.setString("offduty_2",this.frowset.getString("offduty_2"));
				vo.setString("onduty_3",this.frowset.getString("onduty_3"));
				vo.setString("offduty_3",this.frowset.getString("offduty_3"));
				vo.setString("onduty_4",this.frowset.getString("onduty_4"));
				vo.setString("offduty_4",this.frowset.getString("offduty_4"));
				vo_list.add(vo);
			}
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}		
	    this.getFormHM().put("vo_list",vo_list);
	    this.getFormHM().put("code",code);
	    this.getFormHM().put("kind",kind);
	}	
    
}
