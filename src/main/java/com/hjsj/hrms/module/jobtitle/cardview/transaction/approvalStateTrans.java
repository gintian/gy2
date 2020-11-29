package com.hjsj.hrms.module.jobtitle.cardview.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

/**
 * 资格评审_更新审批状态
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class approvalStateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		String type = (String)this.getFormHM().get("type");//1:审批状态  2:专家状态标识
		String state = null;
		try {
			state = (String)this.getFormHM().get("state");
		} catch(Exception e) {
			
		}
		String w0501 = (String)this.getFormHM().get("w0501");
		w0501 = PubFunc.decrypt(w0501);
		String w0301 = (String)this.getFormHM().get("w0301");
		w0301 = PubFunc.decrypt(w0301);
		String categories_id = (String)this.getFormHM().get("categories_id");
		if(StringUtils.isNotEmpty(categories_id)) {
			categories_id = PubFunc.decrypt(categories_id);
		}
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		RowSet rs1 = null;
		try {
			if("2".equals(type) && "3".equals(state)) {
				// 提交的时候，改为批量提交
				ArrayList<MorphDynaBean> objArr = (ArrayList<MorphDynaBean>)this.getFormHM().get("objArr");
				ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
				for(MorphDynaBean mbean : objArr) {
					ArrayList<String> tmplist = new ArrayList<String>();
					tmplist.add(PubFunc.decrypt((String)mbean.get("w0501")));
					tmplist.add(PubFunc.decrypt((String)mbean.get("w0301")));
					tmplist.add(this.userView.getUserName());
					
					list.add(tmplist);
				}
				StringBuilder sql = new StringBuilder();
				sql.append("update zc_data_evaluation  ");
				sql.append(" set expert_state=3,opinion='' ");
				sql.append(" where W0501=? ");
				sql.append(" and W0301=? ");
				sql.append(" and username=? ");
				
				dao.batchUpdate(sql.toString(), list);
				String old_categories_id = "";
				String username = "";
				// 同步投票状态和打分进度
				for(MorphDynaBean mbean : objArr) {
					HashMap<String,Integer> map = new HashMap<String,Integer>();
					int submitnum = 0;
					String _categories_id = (String)mbean.get("categories_id");
					if(StringUtils.isNotEmpty(_categories_id) && !old_categories_id.equalsIgnoreCase(_categories_id)) {
						old_categories_id = _categories_id;
						_categories_id =PubFunc.decrypt(_categories_id);
						//int submitnum = 0;
						int allnum = 0;
						/*rs = dao.search("select COUNT(distinct username) as count from zc_data_evaluation where categories_id=? and expert_state=3 ", Arrays.asList(new String[] {_categories_id}));
						if(rs.next()){
							submitnum = rs.getInt("count");
						}*/
						//找到这个分组中有多少个已经投票了的，直接将这个个数和库中的expertnum比较，这样就不会出现已经提交了，暂停，再添加一个人再提交出错问题了
						rs = dao.search("select username,COUNT(username) as count from zc_data_evaluation where categories_id=? and expert_state=3 group by username", Arrays.asList(new String[] {_categories_id}));
						while(rs.next()){
							allnum = rs.getInt("count");
							username = rs.getString("username");
							map.put(username, allnum);
						}
						
						int personCount = 0;
						//一个组中一共有的人数
						rs1 = dao.search("select COUNT(1) as count from zc_personnel_categories zpc left join zc_categories_relations zcr on zpc.categories_id=zcr.categories_id where zpc.categories_id=?", Arrays.asList(new String[] {_categories_id}));
						if(rs1.next()){
							personCount = rs1.getInt("count");
						}
						
						Iterator iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							Integer count = (Integer)entry.getValue();
							if(count == personCount) {//如果对应的人员已经提交的人数和总人数相同则认为是
								submitnum++;
							}
						}
						RecordVo vo = new RecordVo("zc_personnel_categories");
							vo.setString("categories_id", _categories_id);
						vo = dao.findByPrimaryKey(vo);
						int expertnum = vo.getInt("expertnum");
						/*if(expertnum>0 && submitnum>0 && submitnum == expertnum) {// 应投==已投：已结束
							vo.setString("approval_state", "2");
						}*/
						vo.setInt("submitnum", submitnum);
						dao.updateValueObject(vo);
					}
				}
			}else {
				boolean isExist = false;
				/** 查询是否评价过 */
				StringBuilder sql = new StringBuilder(); 
				ArrayList<String> list = new ArrayList<String>();
				sql.append("select count(w0501) as count");
				sql.append(" From zc_data_evaluation ");
				sql.append(" where W0501=? ");
				sql.append(" and W0301=? ");
				sql.append(" and username=? ");
				list.add(w0501);
				list.add(w0301);
				list.add(this.userView.getUserName());
				rs = dao.search(sql.toString(), list);
				while(rs.next()){
					if(rs.getInt("count") > 0){
						isExist = true;
					}
				}
				
				sql.setLength(0);
				list.removeAll(list);
				/** 评价过则更新 */
				if(isExist){
					sql.append("update zc_data_evaluation ");
					if("1".equals(type)){// 审批状态
						sql.append(" set approval_state=? ");
					} else if("2".equals(type)){// 专家状态标识  
						sql.append(" set expert_state=? ");
					}
					sql.append(",opinion='' where W0501=? ");
					sql.append(" and W0301=? ");
					sql.append(" and username=? ");
					
					list.add(state);
					list.add(w0501);
					list.add(w0301);
					list.add(this.userView.getUserName());
					dao.update(sql.toString(), list);
				} 
				/** 没有评价过则新增 */
				else {
					sql.append("insert into  zc_data_evaluation ");
					sql.append(" (W0501, W0301, username, W0101, expert_state, approval_state, opinion, categories_id) ");
					sql.append(" values (?, ?, ?, ?, ?, ?, ?, ?) ");
					list.add(w0501);//W0501
					list.add(w0301);//W0301
					list.add(this.userView.getUserName());//username
					list.add(this.userView.getA0100());//W0101专家编号
					if("1".equals(type)){// 1:审批状态
						list.add("");//expert_state
						list.add(state);//approval_state
					} else if("2".equals(type)){// 2:专家状态标识  
						list.add(state);//approval_state
						list.add("");//expert_state
					}
					list.add("");//opinion
					if(StringUtils.isNotEmpty(categories_id)) {
						list.add(categories_id);
					}else {
						list.add(null);
					}
					dao.insert(sql.toString(), list);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs1);
		}
	}

}
