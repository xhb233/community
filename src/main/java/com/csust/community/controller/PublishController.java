package com.csust.community.controller;

import com.csust.community.dto.QuestionDTO;
import com.csust.community.mapper.QuestionMapper;
import com.csust.community.model.Question;
import com.csust.community.model.User;
import com.csust.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author XieHaiBin
 * @Date 2020/6/15 19:19
 * @Version 1.0
 */
@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Integer id,
                       Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        //回显信息
        model.addAttribute("title",questionDTO.getTitle());
        model.addAttribute("description",questionDTO.getDescription());
        model.addAttribute("tag",questionDTO.getTag());
        model.addAttribute("id",questionDTO.getId()); //标识是已经存在的问题
        return "publish";
    }

    /**
     * Get请求选择渲染页面
     * @return
     */
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    /**
     * Post请求提交表单
     * @return
     */
    @PostMapping("/publish")
    public String postPublish(
            @RequestParam(value ="title",required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Integer id,
            HttpServletRequest request,
            Model model){
        //回显信息
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        if (title==null||title=="") {
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description==null||description=="") {
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if (tag ==null||tag=="") {
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        //验证用户是否登录
        User user = (User)request.getSession().getAttribute("user");
        if (user==null) {
            model.addAttribute("error","用户未登录");
            return "publish";
        }


        //插入数据库
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

}