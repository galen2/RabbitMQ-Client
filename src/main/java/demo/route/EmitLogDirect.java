package demo.route;
import com.liequ.rabbitmq.ConnectionManager;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class EmitLogDirect {

  private static final String EXCHANGE_NAME = "direct_logs";
  static Address[] addrArr = new Address[]{new Address("192.168.33.14", 5672)};

  public static void main(String[] argv) throws Exception {
	  argv=new String[]{"info33"};
  
	  Channel channel = ConnectionManager.getInstance().getChannel("brokerNameOne");
	  Connection connection =  ConnectionManager.getInstance().getConnection("brokerNameOne", channel);

    channel.exchangeDeclare(EXCHANGE_NAME, "direct");

    String severity = getSeverity(argv);
    String message = getMessage(argv);

    channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + severity + "':'" + message + "'");

    channel.close();
    
    connection.close();
  }

  private static String getSeverity(String[] strings){
    if (strings.length < 1)
    	    return "info";
    return strings[0];
  }

  private static String getMessage(String[] strings){
    if (strings.length < 2)
    	    return "Hello World!";
    return joinStrings(strings, " ", 1);
  }

  private static String joinStrings(String[] strings, String delimiter, int startIndex) {
    int length = strings.length;
    if (length == 0 ) return "";
    if (length < startIndex ) return "";
    StringBuilder words = new StringBuilder(strings[startIndex]);
    for (int i = startIndex + 1; i < length; i++) {
        words.append(delimiter).append(strings[i]);
    }
    return words.toString();
  }
}