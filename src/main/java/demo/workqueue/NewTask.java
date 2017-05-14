package demo.workqueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.liequ.rabbitmq.ConnectionManager;
import com.rabbitmq.client.Channel;

public class NewTask {

  private static final String TASK_QUEUE_NAME = "task_queue_length";

  public static void main(String[] argv) throws Exception {
	 Channel channel = ConnectionManager.getInstance().getChannel("brokerNameOne");
	 
	 Map<String, Object> args = new HashMap<String, Object>();
	 args.put("x-max-length", 5);
	 
    channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, args);

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	while(true){
		System.out.println("请输入内容：");
		String message = reader.readLine();
		if(message.equals("bye")){
			break;
		}
		channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
	}
   /* channel.basicPublish("", TASK_QUEUE_NAME,MessageProperties.MINIMAL_BASIC,
        message.getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + message + "'");*/
//    channel.confirmSelect();
//    channel.waitForConfirms();
    channel.close();
//    connection.close();
  }
}
