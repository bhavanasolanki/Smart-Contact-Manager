package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
    @Autowired
	private UserRepository userRepository;
    
	@RequestMapping("/")
	public String helper(Model m) {
		System.out.println("inside home controller....");
		m.addAttribute("title","Home page");
		return "home";
	}
	@RequestMapping("/signup")
	public String helperSignup(Model m) {
		System.out.println("inside signup controller....");
		m.addAttribute("title","Sign-up page");
		m.addAttribute("user",new User());
		return "signup";
	}
	@PostMapping("/registered")
	public String helperRegistered(@Valid @ModelAttribute("user") User user,BindingResult res,Model m) {
		System.out.println("inside registered controller....");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		System.out.println(user);
		if(res.hasErrors()) {
			System.out.println(res);
			return "signup";
		}
		this.userRepository.save(user);
		return "login";
	}

	@RequestMapping("/loginPage")
    public String CustomLogin(Model m) {
		m.addAttribute("title","Login Page");
    	return "login";
    }
	
	@RequestMapping("/about")
    public String aboutpage(Model m) {
		m.addAttribute("title","About admin");
    	return "about";
    }
	
	@RequestMapping("/forgot")
	public String forgotemail() {
		return "forgot_email_form";
	}
	@Autowired
	private EmailService emailService;
	@PostMapping("/sendOTP")
	public String otpvreify(@RequestParam("email") String email,HttpSession session) {
		
		Random r=new Random(1000);
		int otp=r.nextInt(9999);
		
		System.out.println("otp: "+otp);
		
		String subject="Verification OTP from Smart Contact Mananger";
		String message="<h1> OTP="+otp+"</h1>";
		String to=email;
		boolean f=this.emailService.sendEmail(subject, message, to);
		if(f) {
			session.setAttribute("message", new Message("OTP sent sucessfully!","success"));
			// storing otp and email on session..
			
			session.setAttribute("actualOTP", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			session.setAttribute("message", new Message("error sending email","danger"));
		}
		return "forgot_email_form";
	}
	
	@PostMapping("/verify-otp")
	public String verification(@RequestParam("userOTP") int userOTP,HttpSession session) {
		int actualOTP=(int)session.getAttribute("actualOTP");
	    String email=(String)session.getAttribute("email");
	    System.out.println("act"+actualOTP+"jhghjgj");
	    System.out.println("user"+userOTP+"jhggjjj");
	    if(actualOTP==userOTP) {
	    	session.setAttribute("message", new Message("verified sucessfully!","success"));
	    	User user=userRepository.getUserByUserName(email);
	    	if(user==null) {
	    		session.setAttribute("message", new Message("user does not exist","danger"));
			    return "forgot_email_form";
	    	}
	    	return "change_password";
	    }
	    else
	    {
	    	session.setAttribute("message", new Message("error in verification!","danger"));
	    	return "verify_otp";
	    }
	    
	}
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/new-password")
	public String nyanya(@RequestParam("newPassword") String newPassword,HttpSession session) {
		String email=(String)session.getAttribute("email");
		User user=userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		return "login";
	}
}