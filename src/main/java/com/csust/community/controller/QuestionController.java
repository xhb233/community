package com.csust.community.controller;

import com.csust.community.dto.CommentCreateDTO;
import com.csust.community.dto.CommentDTO;
import com.csust.community.dto.QuestionDTO;
import com.csust.community.service.CommentService;
import com.csust.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author XieHaiBin
 * @Date 2020/6/18 13:17
 * @Version 1.0
 */
@Controller
public class QuestionController {  //管理查看问题页面

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    //问题在数据库中的编号id
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model) {
        QuestionDTO questionDTO = questionService.getById(id);

        List<CommentDTO> comments = commentService.listByQuestionId(id);

        //累加阅读数
        questionService.incView(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        return "question";
    }
}
