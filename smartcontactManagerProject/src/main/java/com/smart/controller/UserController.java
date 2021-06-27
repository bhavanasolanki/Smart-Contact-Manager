
package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@ModelAttribute
	public void addcommondata(Model m,Principal p) {
		String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	m.addAttribute("user",user);
	}
	
	
	@RequestMapping("/index") 
	public String userDashboard(Model m,Principal p) {
    	System.out.println("inside dashboard controller..");
    	m.addAttribute("title","User Dashboard Page");
    	String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	m.addAttribute("user",user);
		return "normal/userDashboard";
	}
	
	@RequestMapping("/add-contact")
	public String addContactHandler(Model m) {
		System.out.println("inside dashboard-add-contact-form controller..");
    	m.addAttribute("title","Add Contact Form");
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processaddContact(@ModelAttribute("contact") Contact contact,@RequestParam("ContactImg") MultipartFile file,Principal p,HttpSession session) {
		System.out.println(contact);
		try {
		String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	
    	if(file.isEmpty()) {
    		System.out.println("file is empty...");
    		contact.setImageurl("contact.png");
    	}
    	else
    	{
    		contact.setImageurl(file.getOriginalFilename());
    		
				File savef=new ClassPathResource("static/images").getFile();
				Path path=Paths.get(savef.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);    		
    	}
    	contact.setUser(user);
    	user.getContacts().add(contact);
    	this.userRepository.save(user);
    	
    	// save success message..
    	session.setAttribute("message", new Message("Contact added sucessfully!","success"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// error msg..
			session.setAttribute("message", new Message("Something went wrong!","danger"));
		}
		return "normal/add_contact_form";
	}
	
	@GetMapping("/show-contacts")
	public String showcontacts(Model m,Principal p) {
		System.out.println("inside show contacts controller..");
		m.addAttribute("title","All contacts list");
		String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	
    	List<Contact> contacts=user.getContacts();
    	
    	m.addAttribute("contacts",contacts);
		return "normal/show_contacts";
	}
	
	@GetMapping("/contact/{Cid}")
	public String contactDetail(@PathVariable("Cid") Integer Cid,Model m,Principal p) {
		Contact contact=this.contactRepository.getById(Cid);
		String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	
		if(user.getId()==contact.getUser().getId())
		     m.addAttribute("contact",contact);
		return "normal/contact_detail";
	}
	@GetMapping("/delete/{Cid}")
	public String DeletecontactDetail(@PathVariable("Cid") Integer Cid,Model m,Principal p,HttpSession session) {
		System.out.println("inside delete contact controller..");
		Contact contact=this.contactRepository.getById(Cid);
		String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	user.getContacts().remove(contact);
		this.userRepository.save(user);
		   
		session.setAttribute("message", new Message("Contact deleted sucessfully!","success"));
		return "redirect:/user/show-contacts";
	}
	@PostMapping("/update/{Cid}")
	public String UpdatecontactDetail(@PathVariable("Cid") Integer Cid,Model m,Principal p,HttpSession session) {
		System.out.println("inside update contact controller..");
		m.addAttribute("title","Update Contact");
    	Contact contact=this.contactRepository.getById(Cid);
    	m.addAttribute("contact",contact);
		return "normal/update-contacts";
	}
	
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute("contact") Contact contact,@RequestParam("ContactImg") MultipartFile file,Principal p,HttpSession session){
		System.out.println(contact);
		try {
			Contact oldcontact=this.contactRepository.findById(contact.getCid()).get();
			System.out.println(oldcontact);
			if(file.isEmpty()) {
	    		
	    		contact.setImageurl(oldcontact.getImageurl());
	    	}
	    	else
	    	{
	    		contact.setImageurl(file.getOriginalFilename());
	    		
					File savef=new ClassPathResource("static/images").getFile();
					Path path=Paths.get(savef.getAbsolutePath()+File.separator+file.getOriginalFilename());
	                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);    		
	    	}
	    	
			String username=p.getName();
	    	User user=this.userRepository.getUserByUserName(username);
			contact.setUser(user);
			this.contactRepository.save(contact);
	    	this.userRepository.save(user);
	    	
	    	// save success message..
	    	session.setAttribute("message", new Message("Contact updated sucessfully!","success"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// error msg..
				session.setAttribute("message", new Message("Something went wrong!","danger"));
			}
		
		return "redirect:/user/contact/"+contact.getCid();
	}
	@GetMapping("/your-profile")
	public String profilrfunc(Model m) {
		m.addAttribute("title","User Profile");
		return "normal/profile";
	}
	@GetMapping("/settings")
	public String settinghdler(Model m) {
		m.addAttribute("title","Settings");
		return "normal/setting";
	}
	
	@PostMapping("/change-password")
	public String change(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal p,HttpSession session) {
		String username=p.getName();
    	User user=userRepository.getUserByUserName(username);
    	if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
    		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
    		this.userRepository.save(user);
    		session.setAttribute("message", new Message("Password updated!","success"));
   		 
    	}
    	else {
    		session.setAttribute("message", new Message("Wrong old password!","danger"));
		    return "normal/setting";
    	}
		return "normal/userDashboard";
	}
}
