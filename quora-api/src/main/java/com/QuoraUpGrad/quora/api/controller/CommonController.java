package com.QuoraUpGrad.quora.api.controller;

import com.QuoraUpGrad.quora.api.model.UserDetailsResponse;
import com.QuoraUpGrad.quora.service.business.CommonUserService;
import com.QuoraUpGrad.quora.service.entity.UserEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonUserService commonUserService;

    /**
     * This endpoint is used to get the details of any user in the Application.
     * This endpoint can be accessed by any user in the application.
     * @PathVariable userId userId for which the details need to be fetched.
     * @param accessToken authorization used for authenticating the user.
     * @return details of User as ResponseEntity of UserDetailsResponse.
     * @throws @AuthorizationFailedException If the access token provided by the user does not exist in the database or If the user has signed out, @UserNotFoundException If the user with uuid whose profile is to be retrieved does not exist in the database.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/userprofile/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> fetchUserDetails(@PathVariable(value = "userId") final String userId, @RequestHeader("authorization") final String accessToken)
            throws UserNotFoundException, AuthorizationFailedException {
        commonUserService.checkIfTokenIsValid(accessToken);
        UserEntity userEntity = commonUserService.getUserById(userId);
        UserDetailsResponse userResponse = new UserDetailsResponse()
                .firstName(userEntity.getFirstName()).lastName(userEntity.getLastName()).userName(userEntity.getUserName())
                .emailAddress(userEntity.getEmail()).country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe())
                .dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

}
