/********************************************************************************* *
 * Purpose: To create an implementation to GoogleKeep(ToDoApplication) and microservices.
 * @author Saurav Manchanda
 * @version 1.0
 * @since 11/08/2018
 *********************************************************************************/
package com.bridgelabz.userservice.utilservice.rabbitmq;

import javax.mail.MessagingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bridgelabz.userservice.utilservice.MailService;


/**
 * @author Saurav
 * <p>
 * This class is implementing the IConsumer interface
 * </p>
 */
@Component
public class ConsumerImpl implements IConsumer {

	@Autowired
	MailService mailService;
	
	@Override
	@RabbitListener(queues = "${saurav.rabbitmq.queue}")
	public void recievedMessage(Mail email) throws MessagingException {
		String to=email.getTo();
		String subject=email.getSubject();
		String body=email.getBody();
		mailService.sendMail(to,subject,body);
	}

}
