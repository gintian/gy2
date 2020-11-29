package com.hjsj.hrms.businessobject.general.operation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;



public class TwfnodeBo {
	public void insertnode(ContentDAO dao,Connection conn,String tabid) throws SQLException {
		String sql="select count(*) from t_wf_node where tabid='"+tabid+"'";
		try
		{
		int count=0;
		RowSet rs=dao.search(sql);
		if(rs.next()) {
            count=rs.getInt(1);
        }
		if(count==0||count==2)
		{
			if(count==2)
			{
				delnode(conn,tabid);
			}
			ArrayList recordlist=new ArrayList();
			RecordVo startnodeVo=new RecordVo("t_wf_node");
			RecordVo humannodeVo=new RecordVo("t_wf_node");
			RecordVo endnodeVo=new RecordVo("t_wf_node");
			IDGenerator idg=new IDGenerator(2,conn);
	
			String startnodeid=idg.getId("wf_node.node_id");
			String humannodeid=idg.getId("wf_node.node_id");
			String endnodeid=idg.getId("wf_node.node_id");
				
			startnodeVo.setString("node_id",startnodeid);
			startnodeVo.setString("tabid",tabid);
			startnodeVo.setString("nodename","begin");
			startnodeVo.setString("nodetype","1");
				
			humannodeVo.setString("node_id",humannodeid);
			humannodeVo.setString("tabid",tabid);
			humannodeVo.setString("nodename","human");
			humannodeVo.setString("nodetype","2");
				
			endnodeVo.setString("node_id",endnodeid);
			endnodeVo.setString("tabid",tabid);
			endnodeVo.setString("nodename","end");
			endnodeVo.setString("nodetype","9");
			recordlist.add(startnodeVo);
			recordlist.add(humannodeVo);
			recordlist.add(endnodeVo);
			dao.addValueObject(recordlist);	
			
			
			
			String tran_id1=idg.getId("wf_trans.tran_id");
			String tran_id2=idg.getId("wf_trans.tran_id");
			RecordVo aVo=new RecordVo("t_wf_transition");
			RecordVo bVo=new RecordVo("t_wf_transition");
			
			aVo.setInt("tran_id", Integer.parseInt(tran_id1));
			aVo.setString("tran_name","a");
			aVo.setInt("pre_nodeid",Integer.parseInt(startnodeid));
			aVo.setInt("next_nodeid",Integer.parseInt(humannodeid));
			aVo.setInt("tabid",Integer.parseInt(tabid));
			
			bVo.setInt("tran_id", Integer.parseInt(tran_id2));
			bVo.setString("tran_name","b");
			bVo.setInt("pre_nodeid",Integer.parseInt(humannodeid));
			bVo.setInt("next_nodeid",Integer.parseInt(endnodeid));
			bVo.setInt("tabid",Integer.parseInt(tabid));
			ArrayList list=new ArrayList();
			list.add(aVo);
			list.add(bVo);
			dao.addValueObject(list);	
		}
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public void delnode(Connection conn,String tabid) throws SQLException{
		String sql="delete from t_wf_node where tabid='"+tabid+"'";
		String sqltrans="delete from t_wf_transition where tabid='"+tabid+"'";
		String wf_chart="update Template_table set wf_chart=null where TabId='"+tabid+"'";
		ContentDAO dao = new ContentDAO(conn);
		dao.update(sql);
		dao.update(sqltrans);
		dao.update(wf_chart);
		dao.update(sql);
	}

}
