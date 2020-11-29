package com.hjsj.hrms.module.jobtitle.cardview.transaction;

import com.hjsj.hrms.module.jobtitle.cardview.businessobject.ListViewBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class ListViewTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		/**
		 * 1：初始化数据获得申报人列表信息
		 * 2：校验投票结果
         * 3：提交投票结果
         * 4.需要展示的列数
		 */
		try {
			String type = (String) this.getFormHM().get("type");
			ListViewBo lvb = new ListViewBo(this.getFrameconn(),this.userView);
			
			if("1".equals(type)) {
				HashMap<String, String> map = lvb.getTableShowItem();//1.展示那几列
				//从配置表中拿取需要展示的列
				String showItem = map.get("voteColumns");
				String sortItemId = (String) this.getFormHM().get("sortItemId");
				ArrayList<HashMap<String, String>> personDataList = lvb.getPersonDataList(showItem,sortItemId);
				
				this.getFormHM().put("personDataList", personDataList);
			}else if("4".equals(type)) {
				String state = lvb.getW0301State();//会议是否是进行中的
				if("05".equals(state)) {
					int counts = lvb.getCountState();//分组是否是进行中的
					HashMap<String,Integer> map = lvb.getUserType();//如果是审核账号，则启动了就不能登陆
					int usetype = (Integer) map.get("usetype");
					if(counts > 0 && usetype != 1) {//启动了并且不是审核账号
						int count = lvb.getApprovelState();//是否全部提交了
						if(count == 0 && usetype != 1) {
							this.getFormHM().put("erroror", "3");//全部提交了
						}else {
							this.getContent();
						}
					}else if(counts > 0 && usetype == 1){
						boolean isAllStrt = lvb.getSumCategories(counts);
						if(isAllStrt)
							this.getFormHM().put("erroror", "5");//当前会议未启动
						else
							this.getContent();
					}else if(counts == 0 && usetype == 1){//会议没有启动并且是审核账号才能进入
						this.getContent();
					}else {
						this.getFormHM().put("erroror", "4");//当前会议未启动
					}
				}else {
					this.getFormHM().put("erroror", "2");//当前会议未启动
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getContent() {
		ListViewBo lvb = new ListViewBo(this.getFrameconn(),this.userView);
		ArrayList<String> aList = new ArrayList<String>();
		HashMap<String, String> map = lvb.getTableShowItem();//1.展示那几列
		//1|null：材料评审  2：投票,3打分
		String useType = lvb.getTypeOfVoteOrScore();
		
		HashMap<String,Integer> mapType = lvb.getUserType();//如果是审核账号，则启动了就不能登陆
		int type = (Integer) mapType.get("type");//1：评委会2：学科组成员3：同行专家4：二级单位
		//从配置表中拿取需要展示的列
		String showItem = map.get("voteColumns");
		String voteType = map.get("voteType");
		//voteType: 1->卡片  2-》列表
		//打分的不用判断
		if(!"3".equals(useType) && "2".equals(voteType)) {
			if(StringUtils.isBlank(showItem.trim())) {
				this.getFormHM().put("erroror", "1");
			}else {
				String[] itemArray = showItem.split(",");
				FieldItem _tempItem = null;
				String itemDesc = "";
				
				for(String itemid : itemArray) {
					_tempItem = DataDictionary.getFieldItem(itemid, "W05");//应该从w05业务字典中取
					if(_tempItem == null)
						_tempItem=DataDictionary.getFieldItem(itemid);
					itemDesc = _tempItem.getItemdesc();
					aList.add(itemDesc);
				}
			}
			this.getFormHM().put("showItem", showItem.toLowerCase());
			this.getFormHM().put("itemDescList", aList);
		}
		String queue = lvb.getShowQueue(useType);
		
		this.getFormHM().put("type", type);
		this.getFormHM().put("useType", useType);
		this.getFormHM().put("queue", queue);
		this.getFormHM().put("voteType", voteType);
	}
}
