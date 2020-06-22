package com.csust.community.service;

import com.csust.community.dto.CommentCreateDTO;
import com.csust.community.dto.CommentDTO;
import com.csust.community.enums.CommentTypeEnum;
import com.csust.community.exception.CustomizeErrorCode;
import com.csust.community.exception.CustomizeException;
import com.csust.community.mapper.CommentMapper;
import com.csust.community.mapper.QuestionExtMapper;
import com.csust.community.mapper.QuestionMapper;
import com.csust.community.mapper.UserMapper;
import com.csust.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @Author XieHaiBin
 * @Date 2020/6/20 16:54
 * @Version 1.0
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) { //判断评论是否存在
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType()==null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType()== CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment==null) {
                //评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        }else{
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question==null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    public List<CommentDTO> listByQuestionId(Long id) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id).andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        List<Comment> comments = commentMapper.selectByExample(commentExample);//拿到该问题下的所有评论
        if (comments.size()==0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators=comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());//获得所有评论人的id
        List<Long> userIds=new ArrayList<>();
        userIds.addAll(commentators);
        //获取评论人并转化为map
        UserExample userExample=new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long,User> userMap=users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //转换comment为commentDTO
        List<CommentDTO> commentDTOS=comments.stream().map(comment -> {
           CommentDTO commentDTO=new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
           return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
