package com.hjsj.hrms.businessobject.sys.cmpp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SubmitSms {
	private List content;
	private String phone;
	private String qyqm;

	public SubmitSms(List content, String phone, String qyqm) {
		this.content = content;
		this.phone = phone;
		this.qyqm = qyqm;
	}

	public void run() {
		
		Socket msgSocket = null;
		InputStream input = null;
		OutputStream output = null;
		try {
			// 创建soket
			msgSocket = new Socket(MsgConfig.getIsmgIp(),
					MsgConfig.getIsmgPort());
			msgSocket.setKeepAlive(true);
			msgSocket.setSoTimeout(MsgConfig.getTimeOut());

			input = msgSocket.getInputStream();
			output = msgSocket.getOutputStream();

			// 连接
			int count = 0;
			boolean result = connectISMG(msgSocket, output, input);
			while (!result) {
				count++;
				result = connectISMG(msgSocket, output, input);
				if (count >= (MsgConfig.getConnectCount() - 1)) {// 如果再次连接次数超过两次则终止连接
					break;
				}
			}

			for (int i = 0; i < this.content.size(); i++) {
				if (this.content.size() > 1) {
					// System.out.println("(" + (i + 1) + "/" +
					// this.content.size() + ")" +this.content.get(i) +
					// this.qyqm);

					sendShortMsg("(" + (i + 1) + "/" + this.content.size()
							+ ")" + this.content.get(i),
							this.phone, msgSocket, output, input);
				} else {
					// System.out.println( this.content.get(i) + this.qyqm);
					sendShortMsg((String)this.content.get(i), this.phone,
							msgSocket, output, input);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}

				if (msgSocket != null) {
					msgSocket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// MsgContainer.cancelISMG();
	}

	private static boolean sendShortMsg(String msg, String cusMsisdn,
			Socket socket, OutputStream output, InputStream input) {
		try {
			int seq = MsgUtils.getSequence();
			byte[] msgByte = msg.getBytes("gb2312");
			MsgSubmit submit = new MsgSubmit();
			// 12+8+1+1+1+1+10+1+21+1+1+1+6+2+6+17+17+21+1+21+1+8
			submit.setTotalLength(159 + msgByte.length);
			submit.setCommandId(MsgCommand.CMPP_SUBMIT);
			submit.setSequenceId(seq);
			submit.setPkTotal((byte) 0x01);
			submit.setPkNumber((byte) 0x01);
			submit.setRegisteredDelivery((byte) 0x00);
			submit.setMsgLevel((byte) 0x01);
			submit.setFeeUserType((byte) 0x02);
			submit.setFeeTerminalId("");
			submit.setTpPId((byte) 0x00);
			submit.setTpUdhi((byte) 0x00);
			submit.setMsgFmt((byte) 0x0f);
			submit.setMsgSrc(MsgConfig.getSpId());
			submit.setSrcId(MsgConfig.getSpCode());
			submit.setDestTerminalId(cusMsisdn);
			submit.setMsgLength((byte) msgByte.length);
			submit.setMsgContent(msgByte);
			submit.setServiceId(MsgConfig.getServiceId());

			List dataList = new ArrayList();
			dataList.add(submit.toByteArray());
			CmppSender sender = new CmppSender(new DataOutputStream(output),
					new DataInputStream(input), dataList);
//			System.out.println("向手机号码：" + cusMsisdn + "下发短短信，序列号为:" + seq);
			boolean succ = sender.start();
			if (succ) {
//				System.out.println("向手机号码：" + cusMsisdn + "发送成功");
			} else {
//				System.out.println("向手机号码：" + cusMsisdn + "发送失败");
			}
			return succ;
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("发送短短信" + e.getMessage());
			return false;
		}
	}

	private boolean connectISMG(Socket socket, OutputStream output,
			InputStream input) {
//		System.out.println("请求连接到ISMG...");
		MsgConnect connect = new MsgConnect();
		connect.setTotalLength(12 + 6 + 16 + 1 + 4);// 消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
		connect.setCommandId(MsgCommand.CMPP_CONNECT);// 标识创建连接
		connect.setSequenceId(MsgUtils.getSequence());// 序列，由我们指定
		connect.setSourceAddr(MsgConfig.getSpId());// 我们的企业代码
		String timestamp = MsgUtils.getTimestamp();
		connect.setAuthenticatorSource(MsgUtils.getAuthenticatorSource(
				MsgConfig.getSpId(), MsgConfig.getSpSharedSecret(), timestamp));// md5(企业代码+密匙+时间戳)
		connect.setTimestamp(Integer.parseInt(timestamp));// 时间戳(MMDDHHMMSS)
		connect.setVersion((byte) 0x20);// 版本号 高4bit为2，低4位为0
		List dataList = new ArrayList();
		dataList.add(connect.toByteArray());

		try {
			CmppSender sender = new CmppSender(new DataOutputStream(output),
					new DataInputStream(input), dataList);
			boolean succ = sender.start();

			return succ;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
