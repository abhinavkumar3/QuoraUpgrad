package com.QuoraUpGrad.quora.service.business;


import com.QuoraUpGrad.quora.service.dao.UserAuthDao;
import com.QuoraUpGrad.quora.service.dao.UserDao;
import com.QuoraUpGrad.quora.service.entity.UserAuthEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionUserService {
    @Autowired
    UserAuthDao userAuthDao;

    @Autowired
    UserDao userDao;

    /**
     * This method checks if the access token exist in the DB and it is not logged out.
     *
     * @param accessToken token to be validated.
     * @throws AuthorizationFailedException ATHR-001 if the token doesn't exit in the DB , ATHR-002 if
     *                                      the user has already logged out using the token.
     * set message based on endpoint
     */
    public UserAuthEntity checkIfTokenIsValid(String accessToken, String endPoint) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
         //check for end point and set message accordingly
            String errorMsg = "";
            switch (endPoint) {
                case "create":
                    errorMsg = "User is signed out.Sign in first to post a question";
                    break;
                case ("all"):
                    errorMsg = "User is signed out.Sign in first to get all questions";
                    break;
                case "delete":
                    errorMsg = "User is signed out.Sign in first to delete a question";
                    break;
                case ("edit"):
                    errorMsg = "User is signed out.Sign in first to edit the question";
                    break;
                case ("allbyuser"):
                    errorMsg = "User is signed out.Sign in first to get all questions posted by a specific user";
                    break;
            }
            throw new AuthorizationFailedException(
                    "ATHR-002", errorMsg);
        }

        return userAuthEntity;

    }
}
