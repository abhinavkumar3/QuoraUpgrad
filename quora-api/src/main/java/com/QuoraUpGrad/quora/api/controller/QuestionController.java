package com.QuoraUpGrad.quora.api.controller;

import com.QuoraUpGrad.quora.api.model.*;
import com.QuoraUpGrad.quora.service.business.QuestionService;
import com.QuoraUpGrad.quora.service.business.QuestionUserService;
import com.QuoraUpGrad.quora.service.entity.QuestionEntity;
import com.QuoraUpGrad.quora.service.entity.UserAuthEntity;
import com.QuoraUpGrad.quora.service.entity.UserEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.InvalidQuestionException;
import com.QuoraUpGrad.quora.service.exception.UserNotFoundException;
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
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionUserService questionUserService;

    /**
     * Create a question
     *
     * @param questionRequest This object has the content i.e the question.
     * @param authorization access token to authenticate user.
     * @return UUID of the question created in DB.
     * @throws AuthorizationFailedException In case the access token is invalid.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException {
        //check if user is valid
        UserAuthEntity userAuthEntity = questionUserService.checkIfTokenIsValid(authorization, "create");
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        questionEntity.setUserEntity(userAuthEntity.getUserEntity());
        //call create method on service class
        final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    /**
     * Get all questions posted by any user.
     *
     * @param authorization access token to authenticate user.
     * @return List of QuestionDetailsResponse
     * @throws AuthorizationFailedException In case the access token is invalid.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = questionUserService.checkIfTokenIsValid(authorization, "all");
        Iterator<QuestionEntity> itrQuestions = questionService.getAllQuestions().iterator();
        List<QuestionDetailsResponse> questionResponseList = new ArrayList<QuestionDetailsResponse>();
        while (itrQuestions.hasNext()) {
            QuestionEntity questionEntity = itrQuestions.next();
            ((ArrayList) questionResponseList).add(new QuestionDetailsResponse().id(questionEntity.getUuid()).
                    content(questionEntity.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponseList, HttpStatus.OK);
    }

    /**
     * Delete a question
     *
     * @param authorization access token to authenticate user.
     * @param questionId id of the question to be edited.
     * @return Id and status of the question deleted.
     * @throws AuthorizationFailedException In case the access token is invalid.
     * @throws InvalidQuestionException if question with questionId doesn't exist.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, InvalidQuestionException {
        //check if the user is signed in
        UserEntity signedInUser = questionUserService.checkIfTokenIsValid(authorization, "delete").getUserEntity();
        QuestionEntity questionEntity = questionService.getQuestionById(questionId);

        //checks if the user is authorized to delete a question before allowing him to do so
        questionService.deleteQuestion(signedInUser, questionEntity);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     * Edit a question
     *
     * @param authorization access token to authenticate user.
     * @param questionId id of the question to be edited.
     * @param questionEditRequest new content for the question.
     * @return Id and status of the question edited.
     * @throws AuthorizationFailedException In case the access token is invalid.
     * @throws InvalidQuestionException if question with questionId doesn't exist.
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId,
            QuestionEditRequest questionEditRequest
    )
            throws AuthorizationFailedException, InvalidQuestionException {
        UserEntity signedInUser = questionUserService.checkIfTokenIsValid(authorization, "edit").getUserEntity();
        QuestionEntity questionEntity;
        questionEntity = questionService.getQuestionById(questionId);
        questionService.editQuestionContent(signedInUser, questionEntity, questionEditRequest.getContent());
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * Get all questions posted by a user with given userId.
     *
     * @param userId of the user for whom we want to see the questions asked by him
     * @param authorization access token to authenticate user.
     * @return List of QuestionDetailsResponse
     * @throws AuthorizationFailedException In case the access token is invalid.
     */
    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String[] bearerToken = authorization.split("Bearer ");
        UserEntity userEntity = questionUserService.checkIfTokenIsValid(authorization, "allbyuser").getUserEntity();
        Iterator<QuestionEntity> itrQuestions = questionService.getQuestionsByUser(userId).iterator();
        List<QuestionDetailsResponse> questionResponseList = new ArrayList<QuestionDetailsResponse>();
        while (itrQuestions.hasNext()) {
            QuestionEntity questionEntity = itrQuestions.next();
            questionResponseList.add(new QuestionDetailsResponse().id(questionEntity.getUuid()).
                    content(questionEntity.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponseList, HttpStatus.OK);
    }

}
