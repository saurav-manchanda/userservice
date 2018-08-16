/********************************************************************************* *
 * Purpose: To create an implementation to GoogleKeep(ToDoApplication) and microservices.
 * @author Saurav Manchanda
 * @version 1.0
 * @since 11/08/2018
 *********************************************************************************/
package com.bridgelabz.userservice.utilservice.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Saurav
 *         <p>
 *         This class is the implementation of IProducer
 *         </p>
 */
@Component
public class ProducerImpl implements IProducer {

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Value("${saurav.rabbitmq.exchange}")
	private String exchange;

	@Value("${saurav.rabbitmq.routingkey}")
	private String routingKey;

	public void produceMsg(String to, String subject, String body) {
		Mail email = new Mail();
		email.setBody(body);
		email.setSubject(subject);
		email.setTo(to);
		amqpTemplate.convertAndSend(exchange, routingKey, email);
	}
}
