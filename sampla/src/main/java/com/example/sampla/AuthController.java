package com.example.sampla;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Data
    public static class SignupForm {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
                 message = "半角英数字とアンダースコアで3〜20文字にしてください")
        private String username;

        @NotBlank
        @Size(min = 8, max = 64, message = "8〜64文字にしてください")
        private String password;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("form", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("form") SignupForm form,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }
        AppUser user = new AppUser();
        user.setUsername(form.getUsername());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));

        if (!userRepository.create(user)) {
            result.rejectValue("username", "duplicate", "このユーザー名は使われています");
            return "signup";
        }
        return "redirect:/login?registered";
    }
}