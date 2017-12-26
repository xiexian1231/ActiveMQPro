package cn.xsq.demo1;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Producter {

	// ActiveMq ��Ĭ���û���
	private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
	// ActiveMq ��Ĭ�ϵ�¼����
	private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
	// ActiveMQ �����ӵ�ַ
	private static final String BROKEN_URL = ActiveMQConnection.DEFAULT_BROKER_URL;

	AtomicInteger count = new AtomicInteger(0);
	// ���ӹ���
	ConnectionFactory connectionFactory;
	// ���Ӷ���
	Connection connection;
	// �������
	Session session;
	ThreadLocal<MessageProducer> threadLocal = new ThreadLocal<>();

	public void init() {
		try {
			// ����һ�����ӹ���
			connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEN_URL);
			// �ӹ����д���һ������
			connection = connectionFactory.createConnection();
			// ��������
			connection.start();
			// ����һ����������ͨ������������������ļ���
			session = connection.createSession(true, Session.SESSION_TRANSACTED);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String disname) {
		try {
			// ����һ����Ϣ����
			Queue queue = session.createQueue(disname);
			// ��Ϣ������
			MessageProducer messageProducer = null;
			if (threadLocal.get() != null) {
				messageProducer = threadLocal.get();
			} else {
				messageProducer = session.createProducer(queue);
				threadLocal.set(messageProducer);
			}
			while (true) {
				Thread.sleep(1000);
				int num = count.getAndIncrement();
				// ����һ����Ϣ
				TextMessage msg = session.createTextMessage(
						Thread.currentThread().getName() + "productor:���Ǵ�˧�磬��������������������,count:" + num);
				System.out.println(Thread.currentThread().getName() + "productor:���Ǵ�˧�磬��������������������,count:" + num);
				// ������Ϣ
				messageProducer.send(msg);
				// �ύ����
				session.commit();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}