import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.liequ.rabbitmq.pool.ConnectionManager;
import com.rabbitmq.client.Channel;


public class ConnectionManagerTest {
	int threadPool = 6;
	CountDownLatch latch = new CountDownLatch(threadPool);
	@Test
	public void createChannel(){
		for( int i = 0 ; i < threadPool;i++){
			aa();
		}
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void aa(){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				latch.countDown();
				System.out.println("线程准备好了");
				for(int i = 0;i<2;i++){
					try {
						Channel	cn = ConnectionManager.getInstance().getChannel("one");
						System.out.println(cn);
						cn.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	@Test
	public void chanel() throws Exception{
		Channel	cn = ConnectionManager.getInstance().getChannel("one");
		cn.close();
		cn.close();
	}
}
