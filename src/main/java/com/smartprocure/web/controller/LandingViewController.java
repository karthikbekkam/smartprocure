package com.smartprocure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingViewController {

    @GetMapping("/")
    public String landingPage() {
        return "landing";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "landing";
    }

    @GetMapping("/features")
    public String featuresPage() {
        return "landing";
    }

    @GetMapping("/solutions")
    public String solutionsPage() {
        return "landing";
    }

    @GetMapping("/how-it-works")
    public String howItWorksPage() {
        return "landing";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "landing";
    }

    @GetMapping("/terms")
    public String termsPage() {
        return "legal/terms";
    }

    @GetMapping("/privacy")
    public String privacyPage() {
        return "legal/privacy";
    }
}
