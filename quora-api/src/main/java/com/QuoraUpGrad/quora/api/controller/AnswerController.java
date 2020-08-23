package com.QuoraUpGrad.quora.api.controller;

import com.QuoraUpGrad.quora.api.model.*;
import com.QuoraUpGrad.quora.service.business.AnswerService;
import com.QuoraUpGrad.quora.service.business.CommonUserService;
import com.QuoraUpGrad.quora.service.business.QuestionService;
import com.QuoraUpGrad.quora.service.entity.AnswerEntity;
import com.QuoraUpGrad.quora.service.entity.QuestionEntity;
import com.QuoraUpGrad.quora.service.exception.AnswerNotFoundException;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommonUserService commonUserService;


    /**
     * This method is for creating the answer. This method receives the object of AnswerRequest type with its attributes being set
     * @return AnswerResponse - UUID of the created answer.
     * @throws @AuthorizationFailedException - if the user is not signed in or if the token is expired .
     * @throws @InvalidQuestionException - if the question for which answer is to be created is not present in the database.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") String questionId , final AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity question = questionService.getQuestionById(questionId);

        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setQuestion(question);
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setAnswer(answerRequest.getAnswer());

        final AnswerEntity createdAnswerEntity = answerService.createAnswer(answerEntity, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid().toString()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * This method is for editing the answer. This method receives the object of AnswerEditRequest type with its attributes being set
     * @return AnswerEditResponse - UUID of the edited answer.
     * @throws @AuthorizationFailedException - if the user is not signed in or if the token is expired .
     * @throws @AnswerNotFoundException - if the answer which is to be edited is not present in the database.
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse>  editAnswerContent(@PathVariable("answerId") String answerId, final AnswerEditRequest answerEditRequest, @RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity editedAnswerEntity = answerService.editAnswer(answerId,authorization,answerEditRequest.getContent());

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedAnswerEntity.getUuid().toString()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.CREATED);

    }

    /**
     * This method is for deleting the answer. This method receives the answerId which is to be deleted
     * @return AnswerDeleteResponse -  UUID of the deleted answer.
     * @throws @AuthorizationFailedException - if the user is not signed in or if the token is expired .
     * @throws @AnswerNotFoundException - if the answer which is to be deleted is not present in the database.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerID,    @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity deletedAnswerEntity = answerService.deleteAnswer(answerID,authorization);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswerEntity.getUuid().toString()).status("ANSWER DELETED");

        return  new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * This method is for getting all answers for a question. This method receives the questionId for which answers are to be retrieved
     * @return AnswerDetailsResponse - UUID of the answers which are to be retrieved.
     * @throws @AuthorizationFailedException - if the user is not signed in or if the token is expired .
     * @throws @InvalidQuestionException - if the question for which answers are to be retrieved is not present in the database.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> allAnswers = answerService.getAnswersbyQuestionID(questionId,authorization);
        Iterator<AnswerEntity> allAnswersIterator = allAnswers.iterator();

        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<AnswerDetailsResponse>() ;

        while(allAnswersIterator.hasNext()){
            AnswerEntity answerEntity = allAnswersIterator.next();
            answerDetailsResponseList.add(new AnswerDetailsResponse().id(answerEntity.getUuid().toString()).answerContent(answerEntity.getAnswer()).questionContent(answerEntity.getQuestion().getContent()));
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);
    }

}
