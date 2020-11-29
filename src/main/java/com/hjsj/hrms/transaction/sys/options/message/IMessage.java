/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options.message;

/**
 * @author wangzhongjun
 *
 */
public interface IMessage {
	public String sendMessage(String constant, String days, String username);
	public String sendMessage(String constant, String days);
}
