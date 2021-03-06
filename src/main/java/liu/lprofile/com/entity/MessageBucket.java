package liu.lprofile.com.entity;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import liu.lprofile.com.util.ZIPUtil;

/**
 * 缓存桶采用单例模式
 * 
 * @author liu
 *
 */
public class MessageBucket {

	private static BlockingQueue<byte[]> blockingQueue = new ArrayBlockingQueue<byte[]>(64);

	private static MessageBucket messageBucket = new MessageBucket();

	private static final int HEAD = 4;// 消息头长度

	private MessageBucket() {
		super();
	}

	public static MessageBucket getInstance() {
		return messageBucket;
	}

	/**
	 * 为消息添加消息头
	 * 
	 * @param src
	 */
	private byte[] addHead(byte[] src, int start, int length) {
		byte[] result = new byte[HEAD + length];
		byte[] array = ZIPUtil.intToByteArray(length);
		for (int i = 0; i < src.length; i++) {
			if (i < array.length) {
				result[i] = array[i];
			} else {
				result[i] = src[i - array.length];
			}
		}
		return result;
	}

	private byte[] addHead(byte[] src, int length) {
		return addHead(src, 0, length);
	}

	/**
	 * 截取源数据中有效的部分
	 * 
	 * @param src
	 */
	private byte[] convertMessage(byte[] src, int length) {
		return Arrays.copyOf(src, length);
	}

	public void pushMessage(byte[] src, int length) {
		byte[] bs = addHead(src, length);
		try {
			blockingQueue.put(src);
		} catch (InterruptedException e) {
			// TODO 压入消息失败如何处理
			e.printStackTrace();
		}
	}

	public byte[] takeMessage() throws InterruptedException {
		return blockingQueue.take();
	}
}
