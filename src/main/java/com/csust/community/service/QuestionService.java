package com.csust.community.service;

import com.csust.community.dto.PageinationDTO;
import com.csust.community.dto.QuestionDTO;
import com.csust.community.mapper.QuestionMapper;
import com.csust.community.mapper.UserMapper;
import com.csust.community.model.Question;
import com.csust.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author XieHaiBin
 * @Date 2020/6/16 21:39
 * @Version 1.0
 */
@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * PageinationDTO 包含QuestionDTO属性，QuestionDTO带有user属性，user里有用户头像地址avatarUrl，用于获取用户头像地址
     *
     * @param page 页码
     * @param size 一个页面的问题数
     * @return 返回当前页码数应显示的问题列表和分页的状态设置
     */
    public PageinationDTO list(Integer page, Integer size) {

        Integer totalPage;
        Integer totalCount = questionMapper.count(); //数据库中问题总数
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) { //判断page是否合法
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.list(offset, size);//返回数据库中的limit offset,size规格的问题
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        PageinationDTO pageinationDTO = new PageinationDTO();  //存放当前页码的问题列表和分页的状态
        for (Question question : questions) {  //循环找出该问题列表所对应的user
            User user = userMapper.findById(question.getCreator());//通过问题存放的creator关联user的id查询
            QuestionDTO questionDTO = new QuestionDTO();  //存放具有user属性的Question
            BeanUtils.copyProperties(question, questionDTO);//快速拷贝到数据传送类DTO中
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        pageinationDTO.setQuestions(questionDTOList);
        pageinationDTO.setPageination(totalPage, page);

        return pageinationDTO;
    }

    /**
     * 返回包含当前用户所发布的问题列表的PageinationDTO
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public PageinationDTO list(Integer userId, Integer page, Integer size) {
        Integer totalPage;
        Integer totalCount = questionMapper.countByUserId(userId); //数据库中该用户的问题总数
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) { //判断page是否合法
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.listByUserId(userId, offset, size);//返回数据库中该用户的所有问题
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        PageinationDTO pageinationDTO = new PageinationDTO();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());//通过问题存放的creator关联user的id查询
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//快速拷贝到数据传送类DTO中
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        pageinationDTO.setQuestions(questionDTOList);
        pageinationDTO.setPageination(totalPage, page);

        return pageinationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question=questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        User user = userMapper.findById(question.getCreator());
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }
}
