/********************************************************************************* *
 * Purpose: To make a TokenGenerator Class. Creating a Token Generator Class that generates the token.
 * 
 * @author Saurav Manchanda
 * @version 1.0
 * @since 11/07/2018
 *********************************************************************************/
package com.bridgelabz.userservice.utilservice;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

import com.bridgelabz.userservice.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
/**
 * @author Saurav
 * <p>
 * Class TokenGenerator that has a token generator method.
 * </p>
 */
@Service
public class TokenGenerator {

	final static String KEY = "Saurav";
/**
 * Method to generate a token
 * @param user
 * @return
 */
	public String generator(User user) {
		String email = user.getEmail();
		String passkey = user.getId();
		System.out.println(email +" "+ passkey);
		long nowMillis = System.currentTimeMillis() + (20 * 60 * 60 * 1000);
		Date now = new Date(nowMillis);
		JwtBuilder builder = Jwts.builder().setId(passkey).setIssuedAt(now).setSubject(email)
				.signWith(SignatureAlgorithm.HS256, KEY);
		
		return builder.compact();
	}
/**
 * Method to parse the JWT token necessary for assigning 
 * @param jwt
 */
	public String parseJWT(String jwt) {

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(KEY)).parseClaimsJws(jwt)
				.getBody();
		return claims.getSubject();
	}
}
