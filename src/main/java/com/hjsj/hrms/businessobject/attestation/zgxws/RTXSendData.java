package com.hjsj.hrms.businessobject.attestation.zgxws;
/**
 * 腾讯通发送待办，实体包在hjsoft_jk
 * @author Administrator
 *
 */
public interface RTXSendData {
	
	/**
     * 发送消息提醒
     * @param receivers String 接收人(多个接收人以逗号分隔)
     * @param title String 消息标题
     * @param msg String 消息内容
     * @param type String 0:普通消息 1:紧急消息
     * @param delayTime String 显示停留时间(毫秒) 0:为永久停留(用户关闭时才关闭)
     * @return int 0:操作成功 非0:操作不成功
     */
	public boolean insertPending(String receivers,String title,String msg,String type,String delayTime);
	
}
