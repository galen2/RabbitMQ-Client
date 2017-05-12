import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.liequ.rabbitmq.util.Envm;


public class JSONTest {

	@Test
	public void parest(){
   	JsonParser parse =new JsonParser();  //创建json解析器
    try {
    	String path = Envm.ROOT.concat("rabbitmq.json");
    	 File configFile = new File(path);
//    	 FileInputStream in = new FileInputStream(configFile);
    	 
        JsonObject json=(JsonObject) parse.parse(new FileReader(configFile));  //创建jsonObject对象
        Set<Entry<String, JsonElement>> entrySet =  json.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
        	 String key = entry.getKey().toString().trim();
        	 JsonObject result=json.get(key).getAsJsonObject();
        	
        	 System.out.println( result.has("userName22"));
//        	 String userName = result.get("userName").getAsString();
//        	 System.out.println(userName);
        }
       /* System.out.println("resultcode:"+json.get("resultcode").getAsInt());  //将json数据转为为int型的数据
        System.out.println("reason:"+json.get("reason").getAsString());     //将json数据转为为String型的数据
*/         
//        System.out.println(json.getAsNumber().intValue());
       /* System.out.println(json.entrySet().size());
        JsonObject result=json.get("brokerOne").getAsJsonObject();
        System.out.println(result.get("firstName").getAsString());*/
       /* JsonObject today=result.get("brokerTwo").getAsJsonObject();
        System.out.println("temperature:"+today.get("temperature").getAsString());
        System.out.println("weather:"+today.get("weather").getAsString());*/
         
    } catch (JsonIOException e) {
        e.printStackTrace();
    } catch (JsonSyntaxException e) {
        e.printStackTrace();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
	}
}
