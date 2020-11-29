package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 组织机构菜单级联关联类
 * @author Administrator
 *
 */
public class CodeItemBo {

	private Connection conn = null;
	private UserView userview;

	public CodeItemBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
	
	/***
	 * 获取当前codeitem的codesetId
	 * UN：单位
	 * UM：部门
	 * @K：岗位 
	 * @param codeItemId 
	 * @return
	 */
	public ArrayList getCodeInfo(String codeItemId)
	{
		ArrayList codeSetInfo = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(" select codesetid,parentid from organization where codeitemid = '"+codeItemId+"'");
			if(rs.next())
			{
				codeSetInfo.add(rs.getString("codesetid"));
				codeSetInfo.add(rs.getString("parentid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeSetInfo;
	}
	/***
	 * 根据codeitemId查询当前codeitem父节点信息
	 * @param codeItemId
	 * @return
	 */
	public ArrayList getCodeParenteInfo(String codeItemId,String codeType)
	{
		ArrayList codeParenteInfo = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(" select codesetid,parentid,codeitemdesc from organization where codeitemid = '"+codeItemId+"'");
			boolean flg = false;
			String codeId = "";
			if(rs.next())
			{
				if("UM".equalsIgnoreCase(rs.getString("codesetid"))&&!"@K".equalsIgnoreCase(codeType))
				{
					flg = true;
					codeId = rs.getString("parentid");
				}else{					
					codeParenteInfo.add(rs.getString("codesetid"));
//					codeParenteInfo.add(rs.getString("parentid"));
					//当前节点信息
					codeParenteInfo.add(codeItemId);
					codeParenteInfo.add(rs.getString("codeitemdesc"));
				}
			}
			if(flg)
			{
				return this.getCodeParenteInfo(codeId,codeType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeParenteInfo;
	}
}
