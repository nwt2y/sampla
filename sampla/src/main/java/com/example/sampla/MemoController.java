package com.example.sampla;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Controller
@RequiredArgsConstructor
public class MemoController {

    private final MemoRepository repository;

    @Data
    public static class MemoForm {
        @NotBlank
        @Size(max = 100)
        private String text;
    }

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("memos", repository.findAll());
        model.addAttribute("form", new MemoForm());
        return "memo";
    }

    @PostMapping("/memo")
    public String add(@Valid @ModelAttribute("form") MemoForm form,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("memos", repository.findAll());
            return "memo";
        }
        Memo memo = new Memo();
        memo.setId(UUID.randomUUID().toString());
        memo.setText(form.getText());
        memo.setCreatedAt(Instant.now().toString());
        repository.save(memo);
        return "redirect:/";
    }

    @PostMapping("/memo/{id}/delete")
    public String delete(@PathVariable String id) {
        repository.delete(id);
        return "redirect:/";
    }
}