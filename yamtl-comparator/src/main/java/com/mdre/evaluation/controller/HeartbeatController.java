package com.mdre.evaluation.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HeartbeatController {
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello ssss%s!", name);
    }	
}