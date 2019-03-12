package com.bridgelabz.fundooNotes.user.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundooNotes.user.dto.LoginDto;
import com.bridgelabz.fundooNotes.user.dto.UserDto;
import com.bridgelabz.fundooNotes.user.exception.UserException;
import com.bridgelabz.fundooNotes.user.exception.UserException2;
import com.bridgelabz.fundooNotes.user.model.User;
import com.bridgelabz.fundooNotes.user.repository.IUserRepository;
import com.bridgelabz.fundooNotes.user.response.ResponseSender;
import com.bridgelabz.fundooNotes.user.response.UserResponse;
import com.bridgelabz.fundooNotes.utility.MailHelper;
import com.bridgelabz.fundooNotes.utility.TokenUtil;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@PropertySource("classpath:message.properties")
@Service
public class UserServicesImpl implements UserServices {
	// @Value("${sender.email}")
	static String fromEmail = null;
	
	String password = null;

	String toEmail = "phulsundarsushant26@gmail.com";

	// String receiver= null;
	String msgheader = "Welcome to Bridgeit ";
	String textmsg = "Your account has been added successfully";

	@Autowired
	IUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	Environment environment;


	/**
	 * to get current date and time
	 */
	String currDateTime = LocalDateTime.now().toString();


	/**
	 * Method to---> login
	 */
	public ResponseEntity<UserResponse> onLogin(LoginDto loginDto) //throws UnsupportedEncodingException
	{
		//// System.out.println(loginDto.getEmailid());

		// get the User (i.e User Object) from loginDTO
		User user = modelMapper.map(loginDto, User.class);
		//// System.out.println(user);

		System.out.println(user.getEmailid());

		  //get The User (i.e User Object) from Database --->having email passed here
		  // here we directly use java 8 lambada Throw Exception feature 
			User validUser =userRepository.findByEmailid(user.getEmailid()).orElseThrow(()-> new UserException(401,environment.getProperty("user.wrongemailid")));
		  
			System.out.println(validUser);
		  if(validUser.getIsVerified().equals("true"))
		  {
			  //match the loginDto password && validUser password
			  boolean passwordStatus =passwordEncoder.matches(loginDto.getPassword(), validUser.getPassword());
			  System.out.println(validUser.getPassword()+"<============>"+loginDto.
			  getPassword());
			  
			  if (passwordStatus == false ) 
			  { 
				  System.out.println("!!!!!!!!!!!!!!!!!");
			      throw new UserException(401,environment.getProperty("user.wrongemailid"));
			  }
			  else
			  { 	
				  //return "login successfully"; 
				  return ResponseSender.sendUserRespone("login successfully",200);
			  }
		  }
		  return ResponseSender.sendUserRespone("not a valid user",401);
	}

	
	/**
	 * Method for---> Register User
	 */
	public ResponseEntity<UserResponse> onRegister(UserDto userDto)  {
		System.out.println("*********************************************");

		User user = modelMapper.map(userDto, User.class);

		Optional<User> useralreadyPresent = userRepository.findByEmailid(user.getEmailid());

		if (useralreadyPresent.isPresent())
		{
			System.out.println("!!!!!!!!!!!!!!!!!");
			throw new UserException(401, environment.getProperty("user.register"));
		}

		// encode user password
		String password = passwordEncoder.encode(userDto.getPassword());

		user.setPassword(password);
		user.setUpdateDate(currDateTime);
		user.setRegisterDate(currDateTime);

		// the Repository==>will save the Object & also return the Saved Object
		user = userRepository.save(user);

		// call method-->to send mail
		sendmail("successfully register", user.getId(),"/validate");

		// return
		return ResponseSender.sendUserRespone("successfully Register",200);
	}

	
	/**
	 * Method for forgetPassword
	 */
	//public ResponseEntity<UserResponse> forgetPassword(String emailId)  //throws UserException, UnsupportedEncodingException {
	public ResponseEntity<UserResponse> forgetPassword(UserDto userDto)  //throws UserException, UnsupportedEncodingException {
	{	// userRepository.fin

		System.out.println(userDto+"*************");
		Optional<User> alreadyPresent = userRepository.findByEmailid(userDto.getEmailid());

		// check user present in database or not
		if (!alreadyPresent.isPresent()) {
			throw new UserException(401, environment.getProperty("user.forgetpassword.emailId"));
		}

		Long id = alreadyPresent.get().getId();

		// call method-->to send mail
		sendmail("email send to reset password", id,"");

		return ResponseSender.sendUserRespone("Mail send to resetpassword",200 );
	}

	
	/**
	 * method to resetpassword
	 */
	public ResponseEntity<UserResponse> resetPassword(String token, String password) //throws UserException, UnsupportedEncodingException {
	{	// encode user
		Long userid = TokenUtil.decodeToken(token);

		// convert Long to int
		Long id = userid;

		System.out.println("9999999999999999999" + userid);
		Optional<User> alreadyPresent = userRepository.findByid(userid);
		// System.out.println(alreadyPresent.get()+"&&&&&&&&&&&&&&&&&&&&&&&&&&");

		// check user present in database or not
		if (!alreadyPresent.isPresent()) {
			throw new UserException(401, environment.getProperty("user.resetpassword"));
		}

		// encode=====> user newpassword
		String newpassword = passwordEncoder.encode(password);

		// set newpassword===>for given emailid
		alreadyPresent.get().setPassword(newpassword);

		User user = alreadyPresent.get();
		System.out.println(user + "****************************");

		// the Repository==>will save the Object & also return the Saved Object
		user = userRepository.save(user);

		// return
		return ResponseSender.sendUserRespone("password reset successfully",200);
	}

	
	/**
	 * Method to verifyEmail
	 */
	public ResponseEntity<UserResponse> validEmail(String token) //throws UserException, UnsupportedEncodingException {
	{	// encode user
		Long userid = TokenUtil.decodeToken(token);

		// convert Long to int
		//int id = userid.intValue();
		

		System.out.println("+++++++++" + userid);
		//System.out.println("9999999999999999999" + id);

		Optional<User> alreadyPresent = userRepository.findByid(userid);
		// System.out.println(alreadyPresent.get()+"&&&&&&&&&&&&&&&&&&&&&&&&&&");

		// check user present in database or not
		if (!alreadyPresent.isPresent()) {
			throw new UserException(401, environment.getProperty("user.tokenexpire"));
		}

		alreadyPresent.get().setIsVerified("true");

		User user = alreadyPresent.get();
		System.out.println(user + "****************************");

		// the Repository==>will save the Object & also return the Saved Object
		user = userRepository.save(user);

		// return
		return ResponseSender.sendUserRespone("Email isverified successfully",200);
	}
//
//	/**
//	 * Send reponse to frontend
//	 */
//	public ResponseEntity<UserResponse> SendUserRespone(String msg,int code)
//	{
//		UserResponse response=new UserResponse();
//		
//		response.setMessage(msg); //get Message from parent class-->RuntimeException
//		response.setStatus(code); 
//		response.setTimeStamp(System.currentTimeMillis());
//
//		return new ResponseEntity<>(response,HttpStatus.OK);
//	}
	
	/**
	 * Method to send email
	 */
	public void sendmail(String mailSubject, Long userId, String string) {
		/*
		 * Outgoing Mail (SMTP) Server requires TLS or SSL: smtp.gmail.com (use
		 * authentication) Use Authentication: Yes Port for SSL: 465
		 */

		fromEmail = environment.getProperty("sender.email");
		password = environment.getProperty("sender.password");

		System.out.println("SSLEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); // SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
		props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); // SMTP Port

		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};

		Session session = Session.getDefaultInstance(props, auth);
		System.out.println("Session created");

		System.out.println(fromEmail + " " + "*******" + " " + toEmail + " " + mailSubject);

		MailHelper.sendEmail(session, toEmail, mailSubject, MailHelper.getUrl(userId)+string);
	}

}



















/*
 * //successfull
 *//**
	 * Method for---> Register User
	 *//*
		 * public String authenticate(Optional<User> user, String password) throws
		 * UserException,UnsupportedEncodingException {
		 * System.out.println("*********************************************");
		 * 
		 * Optional<User> useralreadyPresent =
		 * userRepository.findByEmailid(user.get().getEmailid());
		 * 
		 * if (user.get().getIsVerified().equals(true)) {
		 * System.out.println("!!!!!!!!!!!!!!!!!"); throw new
		 * UserException(401,environment.getProperty("user.register")); }
		 * 
		 * // encode user password String password =
		 * passwordEncoder.encode(userDto.getPassword());
		 * 
		 * user.setPassword(password);
		 * 
		 * //the Repository==>will save the Object & also return the Saved Object user=
		 * userRepository.save(user);
		 * 
		 * //call method-->to send mail sendmail("successfully register",user.getId());
		 * 
		 * //return return "successfully Register"; }
		 */




