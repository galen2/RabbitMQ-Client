package com.liequ.rabbitmq;
// Copyright (c) 2007-Present Pivotal Software, Inc.  All rights reserved.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 1.1 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.



import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ToolUtils {

//    public static final boolean USE_NIO = System.getProperty("use.nio") == null ? false : true;

   /* private static ConnectionFactory connectionFactory = null;
    public synchronized static ConnectionFactory getConnectionFactory() {
    	if(connectionFactory==null){
    		 connectionFactory = new ConnectionFactory();
    	}
        return connectionFactory;
    }*/
    
    private static Connection connection = null;
    public synchronized static Connection getConnectionInstance() {
    	if(connection==null){
    		ConnectionFactory connectionFactory = new ConnectionFactory();
    	    try {
    	    	connectionFactory.setRequestedChannelMax(2);
				connection = connectionFactory.newConnection();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
    	}
    	return connection;
    }

    private static Channel channel = null;
    public synchronized static Channel getChannelInstance() {
//    	if(channel==null){
    		Connection connection = getConnectionInstance();
    		try {
				channel = connection.createChannel();
			} catch (Exception e) {
				e.printStackTrace();
			}
//    	}
        return channel;
    }

    public static void main(String[] args) {
    	
    	String apth =ToolUtils.class.getResource("/").getFile();
    	System.out.println(apth);
    	/*while(true){
        	Channel chanel = getChannelInstance();
        	if(chanel==null){
        		break;
        	}
        	System.out.println(chanel);
    	}*/
	}
    
    public static void closeConnection(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
