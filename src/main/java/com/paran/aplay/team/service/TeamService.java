package com.paran.aplay.team.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.common.error.exception.AlreadyExistsException;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.common.error.exception.PermissionDeniedException;
import com.paran.aplay.common.util.OciObjectStorageUtil;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.dto.request.TeamCreateRequest;
import com.paran.aplay.team.dto.request.TeamUpdateRequest;
import com.paran.aplay.team.dto.response.TeamDetailResponse;
import com.paran.aplay.team.dto.response.TeamResponse;
import com.paran.aplay.team.repository.TeamRepository;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.domain.UserTeam;
import com.paran.aplay.user.repository.UserTeamRepository;
import com.paran.aplay.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
  private final TeamRepository teamRepository;

  private final UserTeamRepository userTeamRepository;

  private final UserUtilService userUtilService;
  private final OciObjectStorageUtil objectStorageUtil;

  @Transactional(readOnly = true)
  public Team getTeamById(Long teamId) {
    return teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException(TEAM_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public Team validateTeamByUser(User user, Long teamId) {
    Team team = getTeamById(teamId);
    boolean isExist = userUtilService.checkUserExistsInTeam(user, team);
    if(!isExist){
      throw new PermissionDeniedException(USER_NOT_ALLOWED);
    }
    return team;
  }

  @Transactional(readOnly = true)
  public List<TeamResponse> getAllTeamByUser(User user) {
    return userUtilService.getUserTeamsByUser(user)
            .stream().map(userTeam -> userTeam.getTeam())
            .map(TeamResponse::from)
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TeamDetailResponse getTeamDetailById(User user, Long teamId) {
    Team team = getTeamById(teamId);
    boolean isExist = userUtilService.checkUserExistsInTeam(user, team);
    if(!isExist){
      throw new PermissionDeniedException(USER_NOT_ALLOWED);
    }

    List<User> members = userUtilService.getUserTeamsByTeam(team)
             .stream().map(userTeam -> userTeam.getUser())
             .collect(Collectors.toList());

    return TeamDetailResponse.from(team, members);
  }

  @Transactional
  public Boolean deleteTeam(User user, Long teamId) {
    Team team = getTeamById(teamId);
    boolean isExist = userUtilService.checkUserExistsInTeam(user, team);
    if(!isExist){
      throw new PermissionDeniedException(USER_NOT_ALLOWED);
    }

    userTeamRepository.deleteAllByTeamId(teamId);
    return !userUtilService.checkUserExistsInTeam(user, team);
  }

  @Transactional
  public Boolean deleteUserFromTeam(User user, Long teamId) {
    Team team = getTeamById(teamId);
    boolean isExist = userUtilService.checkUserExistsInTeam(user, team);
    if(!isExist){
      throw new PermissionDeniedException(USER_NOT_ALLOWED);
    }
    userUtilService.deleteUserFromTeam(user, team);
    isExist = userUtilService.checkUserExistsInTeam(user, team);

    return isExist;
  }

  @Transactional
  public Team createTeam(TeamCreateRequest request, MultipartFile image) {
    Team team = new Team(request);
    team = teamRepository.save(team);
    if (updateTeamProfileImage(team, image)) {
      team = teamRepository.save(team);
    }
    return team;
  }

  @Transactional
  public void inviteUserToTeam(User user, Team team) {
    if (userUtilService.checkUserExistsInTeam(user, team)) throw new AlreadyExistsException(USER_ALREADY_IN_TEAM);
    UserTeam userTeam = new UserTeam(user, team);
    userTeamRepository.save(userTeam);
  }

  @Transactional
  public void updateTeam(Team team, TeamUpdateRequest request, MultipartFile image) {
    boolean isUpdated = false;
    if(request.getName() != null) {
      team.updateName(request.getName());
      isUpdated = true;
    }
    if("".equals(request.getProfileImage())) {
      team.updateProfileImage(Team.DEFAULT_PROFILE_IMAGE_URL);
      isUpdated = true;
    }
    if (image != null) {
      isUpdated = updateTeamProfileImage(team, image) || isUpdated;
    }

    if(isUpdated)
      teamRepository.save(team);
  }

  private boolean updateTeamProfileImage(Team team, MultipartFile image){
    if(image == null)
      return false;

    var tmp = image.getOriginalFilename().split("\\.");
    if(tmp.length < 1) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }

    var fileType = tmp[tmp.length-1];
    if(!(fileType.equals("png") || fileType.equals("jpg") || fileType.equals("jpeg"))) {
      throw new InvalidRequestException(INVALID_FILE_EXTENSION);
    }

    if(updateTeamProfileImageFile(team.getId(), fileType, image)){
      String url = OciObjectStorageUtil.TEAM_PROFILE_IMAGE_PREFIX + team.getId() + "." + fileType;
      team.updateProfileImage(OciObjectStorageUtil.OBJECT_STORAGE_SERVER_URL + url);
      return true;
    }
    return false;
  }

  private boolean updateTeamProfileImageFile(Long teamId, String fileType, MultipartFile image) {
    boolean res = false;

    try {
      String url = OciObjectStorageUtil.TEAM_PROFILE_IMAGE_PREFIX + teamId.toString() + "." + fileType;
      objectStorageUtil.postObject(url, image.getInputStream());
      res = true;
    }
    catch (Exception e){
      System.err.println("팀 프로필 이미지 업데이트 오류.");
      System.err.println(e.getCause());
    }
    return res;
  }

}
