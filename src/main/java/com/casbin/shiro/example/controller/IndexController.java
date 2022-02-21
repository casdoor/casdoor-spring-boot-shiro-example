// Copyright 2021 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.casbin.shiro.example.controller;

import com.casbin.shiro.example.dao.FooModel;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.BearerToken;
import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.service.CasdoorAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yixiang Zhao (@seriouszyx)
 * @date 2022-01-01 22:05
 **/
@Controller
public class IndexController {

    @Resource
    private CasdoorAuthService casdoorAuthService;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/foos")
    public String getFoos(Model model) {
        List<FooModel> foos = new ArrayList<>();
        foos.add(new FooModel(1L, "a"));
        foos.add(new FooModel(2L, "b"));
        foos.add(new FooModel(3L, "c"));
        model.addAttribute("foos", foos);
        return "foos";
    }

    @RequestMapping("/login")
    public String login() {
        return "redirect:" + casdoorAuthService.getSigninUrl("http://localhost:8080/login/oauth2");
    }

    @RequestMapping("/login/oauth2")
    public String doLogin(String code, String state) {
        String token = casdoorAuthService.getOAuthToken(code, state);
        BearerToken bearerToken = new BearerToken(token);
        SecurityUtils.getSubject().login(bearerToken);
        CasdoorUser casdoorUser = (CasdoorUser) SecurityUtils.getSubject().getPrincipal();
        System.out.println(casdoorUser);
        SecurityUtils.getSubject().getSession().setAttribute("name", casdoorUser.getName());
        return "redirect:http://localhost:8080/foos";
    }
}