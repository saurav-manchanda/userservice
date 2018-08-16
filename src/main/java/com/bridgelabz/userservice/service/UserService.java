/********************************************************************************* *
 * Purpose: To do Login Registration with the help of MONGODB repository. 
 * Creating UserService class which is a service class.
 * 
 * @author Saurav Manchanda
 * @version 1.0
 * @since 11/07/2018
 *********************************************************************************/
package com.bridgelabz.userservice.service;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.userservice.model.User;
import com.bridgelabz.userservice.repository.Repository;
import com.bridgelabz.userservice.utilservice.MailService;
import com.bridgelabz.userservice.utilservice.ToDoException;
import com.bridgelabz.userservice.utilservice.TokenGenerator;
import com.bridgelabz.userservice.utilservice.Precondition.PreCondition;
import com.bridgelabz.userservice.utilservice.RedisRepository.IRedisRepository;
import com.bridgelabz.userservice.utilservice.messageaccessor.Messages;
import com.bridgelabz.userservice.utilservice.rabbitmq.IProducer;


/**
 * @author Saurav
 *         <p>
 *         Class UserService that implements the methods of IUserService
 *         interface
 *         </p>
 */
@Service
public class UserService implements IUserService {
	@Autowired
	Repository repository;
	@Autowired
	TokenGenerator token;
	@Autowired
	MailService mailService;
	@Autowired
	IProducer producer;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	IRedisRepository redisRepository;
	@Autowired
	Messages messages;
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	static String REQ_ID = "IN_USER_SERVICE";
	static String RESP_ID = "OUT_USER_SERVICE";

	/**
	 * @param user
	 * @return
	 * @throws ToDoException
	 *             <p>
	 *             This Method to validate the user.
	 *             </p>
	 */
	@Override
	public String validateUser(User user, HttpServletResponse resp) throws ToDoException {
		logger.info(REQ_ID + " Inside ValidateUser method");
		String email = user.getEmail();
		String password = user.getPassword();
		PreCondition.checkNotNull(email,messages.get("201"));
		PreCondition.checkNotNull(password, messages.get("203"));
		PreCondition.checkNotEmptyString(email, messages.get("202"));
		PreCondition.checkNotEmptyString(password, messages.get("204"));
		if (repository.getByEmail(email).isPresent()) {
			Optional<User> user1 = repository.getByEmail(email);
			String validToken = tokengenerator(user1.get());
			if (passwordEncoder.matches(password, user1.get().getPassword())
					&& user1.get().getStatus().equals("true")) {
				resp.addHeader("Authorization", validToken);
				return validToken;
			}
		}
		return null;
	}

	/**
	 * @param user
	 * @return
	 * @throws ToDoException
	 *             <p>
	 *             This Method to check the email if present or not in the database
	 *             </p>
	 */
	@Override
	public boolean checkEmail(User user) throws ToDoException {
		logger.info(REQ_ID + " checkEmail method started");
		String email = user.getEmail();
		PreCondition.checkNotEmptyString(email, messages.get("201"));
		PreCondition.checkNotNull(email, messages.get("202"));
		if (repository.getByEmail(email).isPresent()) {
			PreCondition.commonMethod(messages.get("205"));
		}
		logger.info(RESP_ID + " checkmail method ended");
		return false;
	}

	/**
	 * @param user
	 * @throws ToDoException
	 * @throws MessagingException
	 *             <p>
	 *             This Method to update the user in the database
	 *             </p>
	 */
	@Override
	public void updateUser(User user) throws ToDoException, MessagingException {
		logger.info(REQ_ID + " Update User method started");
		PreCondition.checkNotNull(user.getEmail(), messages.get("201"));
		PreCondition.checkNotNull(user.getPassword(), messages.get("203"));
		PreCondition.checkNotNull(user.getUserName(), messages.get("206"));
//		sendActivationLink(validToken, user);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.save(user);
//		redisRepository.setToken(validToken);
		logger.info(RESP_ID + " Update User method ended");
	}

	/**
	 * @param validToken
	 * @param user
	 * @throws ToDoException
	 * @throws MessagingException
	 *             <p>
	 *             This Method to send the validation link
	 *             </p>
	 */
	@Override
	public void sendActivationLink(String validToken, User user) throws ToDoException, MessagingException {
		logger.info(REQ_ID + " sendActivationLink method started");
		String email = user.getEmail();
		PreCondition.checkNotNull(email, messages.get("201"));
		PreCondition.checkNotEmptyString(email, messages.get("202"));
		String to = email;
		String subject = "Activation Link";
		String body = "Click on the link below to activate your acount\n" + "http://192.168.0.67:1995/activate/?"
				+ validToken;
		producer.produceMsg(to, subject, body);
		redisRepository.setToken(validToken);
		logger.info(RESP_ID + " mail sent successfully to activate the account");

	}

	/**
	 * @param user
	 * @throws MessagingException
	 *             <p>
	 *             This Method to send the mail to the user with there password
	 *             </p>
	 */
	@Override
	public void sendMail(User user, String validToken) throws MessagingException {
		logger.info(REQ_ID + " sendMail method started");
		String email = user.getEmail();
		if (repository.getByEmail(email).isPresent()) {
			String to = email;
			String subject = "To change your password";
			String body = "Click on the link below to change your password\n" +"http://192.168.0.67:1995/newpassword/?"
					+ validToken;
			producer.produceMsg(to, subject, body);
			logger.info(RESP_ID + " Mail sent successfully to change the password");
		}
	}

	/**
	 * @param token2
	 *            <p>
	 *            This method is to activate the user by setting its status as true
	 *            </p>
	 */
	@Override
	public boolean activate(String token2) {
		logger.info(REQ_ID + " inside activate method of service");
		Optional<User> user = repository.getByEmail(token.parseJWT(token2));
		user.get().setStatus("true");
		repository.save(user.get());
		logger.info(RESP_ID + " out of activate method of service");
		return true;
	}

	/**
	 * @param user
	 *            <p>
	 *            This method is generating the token.
	 *            </p>
	 */
	@Override
	public String tokengenerator(User user) {
		logger.info(REQ_ID + " inside the tokengenerator method of service");
		String validToken = token.generator(user);
		token.parseJWT(validToken);
		logger.info("The token generated is:" + validToken);
		return validToken;
	}

	/**
	 * @param token,password
	 *            <p>
	 *            This method is resetting the password of the corresponding user
	 *            email id entered
	 *            </p>
	 * @throws ToDoException
	 */
	@Override
	public void resetPassword(String token1, String password, String newPassword) throws ToDoException {
		logger.info(REQ_ID + " inside resetPassword method of service");
		if (password.equals(newPassword)) {
			String email = token.parseJWT(token1);
			Optional<User> user1 = repository.getByEmail(email);
			User user = new User();
			user.setEmail(user1.get().getEmail());
			user.setId(user1.get().getId());
			user.setPassword(passwordEncoder.encode(password));
			user.setStatus(user1.get().getStatus());
			user.setUserName(user1.get().getUserName());
			repository.save(user);
			logger.info(RESP_ID + " resetPassword done");
		} else {
			logger.error("password and comfirm password donot match");
			PreCondition.commonMethod(messages.get("208"));
		}
	}

	/**
	 * The method is for checking whether the email of the user is present or not in
	 * the database
	 */
	@Override
	public boolean isEmailPresent(User user) throws ToDoException {
		logger.info(REQ_ID + " isEmailPresent method of service");
		String email = user.getEmail();
		PreCondition.checkNotEmptyString(email, messages.get("202"));
		PreCondition.checkNotNull(email, messages.get("201"));
		if (repository.getByEmail(email).isPresent()) {
			return true;
		}
		return false;
	}

	@Override
	public List<?> getAllUsers() {
		List<User> users=repository.findAll();
		return users;
	}
}
