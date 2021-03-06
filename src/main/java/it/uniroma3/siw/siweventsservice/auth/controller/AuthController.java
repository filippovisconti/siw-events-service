package it.uniroma3.siw.siweventsservice.auth.controller;

import it.uniroma3.siw.siweventsservice.auth.models.Credentials;
import it.uniroma3.siw.siweventsservice.auth.models.User;
import it.uniroma3.siw.siweventsservice.auth.service.CredentialsService;
import it.uniroma3.siw.siweventsservice.auth.service.UserService;
import it.uniroma3.siw.siweventsservice.services.ActivityService;
import it.uniroma3.siw.siweventsservice.services.EventService;
import it.uniroma3.siw.siweventsservice.services.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private UserService userService;

	@Autowired
	private EventService eventService;

	@Autowired
	private OrganizerService organizerService;

	@Autowired
	private ActivityService activityService;

	@GetMapping("/login")
	public String showLoginForm (Model model) {
		return "login/loginForm";
	}

	@GetMapping("/logout")
	public String logout (Model model) {
		return "redirect:/";
	}

	@GetMapping("/register")
	public String getRegisterPage (Model model) {
		model.addAttribute("credentials", new Credentials());
		return "login/signUpForm";
	}

	@PostMapping("/register")
	public String register (@Valid @ModelAttribute("credentials") Credentials credentials, BindingResult bindingResult, Model model) {
		if (!bindingResult.hasErrors()) {
			//credentials.setRole("DEFAULT");
			this.credentialsService.saveCredentials(credentials);
			return "login/loginForm";
		} else {
			model.addAttribute("credentials", credentials);
			return "login/signUpForm";
		}
	}

	@GetMapping("/default")
	public String defaultAfterLogin (Model model) {

		UserDetails adminDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(adminDetails.getUsername());
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "redirect:/admin";
		} else if (credentials.getRole().equals(Credentials.DEFAULT_ROLE)) {
			return "redirect:/protected";
		}
		return "redirect:/login";
	}

	@GetMapping("/admin")
	public String getAdminHomePage (Model model) {
		User user = getCurrentUser();
		model.addAttribute("user", user);
		model.addAttribute("numEvents", this.eventService.eventNumber());
		model.addAttribute("numUsers", this.userService.userNumber());
		model.addAttribute("numOrganizers", this.organizerService.organizerNumber());
		model.addAttribute("numActivities", this.activityService.activityNumber());
		return "indexes/admin_index";
	}


	@GetMapping("/protected")
	public String getUserHomePage (Model model) {
		User user = getCurrentUser();
		model.addAttribute("user", user);
		return "indexes/user_index";
	}

	private User getCurrentUser () {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		User user = credentialsService.getUserDetails(username);
		return user;
	}


	@GetMapping("/")
	public String getPublicHomePage () {
		return "indexes/index";
	}
}
