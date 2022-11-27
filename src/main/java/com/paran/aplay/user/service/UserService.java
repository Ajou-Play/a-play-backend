package com.paran.aplay.user.service;

import static com.paran.aplay.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.common.error.exception.AlreadyExistsException;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.common.error.exception.PermissionDeniedException;
import com.paran.aplay.common.util.OciObjectStorageUtil;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.dto.request.TeamUpdateRequest;
import com.paran.aplay.user.domain.Authority;
import com.paran.aplay.user.domain.LocalUser;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.domain.UserChannel;
import com.paran.aplay.user.domain.UserTeam;
import com.paran.aplay.user.dto.request.UserSignUpRequest;
import com.paran.aplay.user.dto.request.UserUpdatePasswordRequest;
import com.paran.aplay.user.dto.request.UserUpdateRequest;
import com.paran.aplay.user.repository.UserChannelRepository;
import com.paran.aplay.user.repository.UserRepository;
import com.paran.aplay.user.repository.UserTeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final OciObjectStorageUtil objectStorageUtil;


  @Transactional(readOnly = true)
  public User signIn(String principal, String credentials) {
    if (!hasText(principal) || !hasText(credentials)) {
      throw new InvalidRequestException(LOGIN_PARAM_REQUIRED);
    }
    LocalUser user = (LocalUser) userRepository.findByEmailAndIsQuit(principal, false)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    user.checkPassword(passwordEncoder, credentials);
    return user;
  }

  @Transactional
  public User signUp(UserSignUpRequest request) {
    if (userRepository.existsByEmailAndIsQuit(request.getEmail(), false)) {
      throw new AlreadyExistsException(USER_ALREADY_EXISTS);
    }

    //TODO: 이메일 인증 로직

    LocalUser.validatePassword(request.getPassword());

    User newUser = LocalUser.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .authority(Authority.EDITOR)
        .build();

    return userRepository.save(newUser);
  }

  @Transactional
  public void changeUserPassword(User user, UserUpdatePasswordRequest request) {
    LocalUser localUser = (LocalUser) (LocalUser) userRepository.findById(user.getId())
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    localUser.checkPassword(passwordEncoder, request.getOldPassword());
    localUser.changePassword(passwordEncoder, request.getNewPassword());
    User newUser = localUser;

    userRepository.save(newUser);
  }

  @Transactional
  public User updateUser(User user, UserUpdateRequest request, MultipartFile image) {
    boolean isUpdated = false;
    if("".equals(request.getProfileImage())) {
      user.updateProfileImage(User.DEFAULT_PROFILE_IMAGE_URL);
      isUpdated = true;
    }
    if (image != null) {
      var tmp = image.getOriginalFilename().split("\\.");
      if(tmp.length < 1) {
        throw new InvalidRequestException(INVALID_LENGTH);
      }

      var fileType = tmp[tmp.length-1];
      if(!(fileType.equals("png") || fileType.equals("jpg") || fileType.equals("jpeg"))) {
        throw new InvalidRequestException(INVALID_FILE_EXTENSION);
      }

      try {
        String url = OciObjectStorageUtil.USER_PROFILE_IMAGE_PREFIX + user.getId().toString() + "." + fileType;
        objectStorageUtil.postObject(url, image.getInputStream());
        user.updateProfileImage(OciObjectStorageUtil.OBJECT_STORAGE_SERVER_URL + url);
        isUpdated = true;
      }
      catch (Exception e){
        System.err.println("유저 프로필 이미지 업데이트 오류.");
        System.err.println(e.getCause());
      }
    }

    if(isUpdated)
      user = userRepository.save(user);
    return user;
  }
}
