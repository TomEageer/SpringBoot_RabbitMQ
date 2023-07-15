package com.xdclass.xdclasssp;

import com.xdclass.xdclasssp.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XdclassSpApplicationTests {

    @Autowired
    private RabbitTemplate template;

    @Test
    void testSend() {
        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "order.new", "新订单3333333");
    }

    @Test
    void testConfirmCallback() throws InterruptedException {

        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            /**
             * @param correlationData 配置
             * @param ack 交换机是否收到消息，true成功，false失败
             * @param cause 失败的原因
             */

            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("\nConfirmCallback========\n");
                System.out.println("correlationData:" + correlationData);
                System.out.println("ack:" + ack);
                System.out.println("cause:" + cause);

                if (ack) {
                    System.out.println("发送成功");
                    //更新数据库的状态成功
                } else {
                    System.out.println("发送失败，记录到日志或者数据库");
                    //更新书库消息的状态失败
                }

            }
        });


        //TODO 向数据库新增一条记录，状态是发送

        //发送消息
        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "order.new", "新订单");
        Thread.sleep(1000);//由于是test方法，导致test结束后，rabbit相应的资源也关闭掉了，虽然消息发送出去，但异步的ConfirmCallback却由于资源关闭而出现了上面的问题
    }

    @Test
    void testReturnCallback() throws InterruptedException {

        template.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                int code = returnedMessage.getReplyCode();
                System.out.println("code=" + code);
                System.out.println("returnedMessage=" + returnedMessage);


            }
        });

        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "order.new", "新订单testReturnCallback");
//        template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "exception.order.new", "新订单testReturnCallback");//模拟异常
        Thread.sleep(1000);

    }

}
