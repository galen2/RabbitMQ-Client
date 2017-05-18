import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.Test;

import com.liequ.rabbitmq.publish.PublishWorkQueue;


public class PublishMessageTest {

	@Test
	public void publiskMessage(){
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.print("请输入内容");
				String content = read.readLine();
				if (content.equals("byte")) {
					break;
				}
				PublishWorkQueue.basicPublish("brokerNameOne", "simple_queue", content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void publishBase(){
		int i = 0;
		while (true)  {
			if (i++ >=1000){
				break;
			}
			String content = UUID.randomUUID().toString();
			try {
				PublishWorkQueue.basicPublish("brokerNameOne", "simple_queue", content);
				System.out.println("发生产消息:"+content);
				Thread.currentThread().sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
