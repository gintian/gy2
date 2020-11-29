package com.hjsj.hrms.interfaces.general;

import java.util.Map;

public interface SendMatters {
	//发送待办 map的内容：String taskId,String title,String url,String receiverId,String senderId,String startTime,String serviceURL
	public String insert(Map map);
	//审批、驳回、修改待办 map的内容：String taskId,String receiverId,String serviceURL
	public String update(Map map);
	//删除待办 map的内容：String taskId,String receiverId,String serviceURL
	public String delete(Map map);
}
