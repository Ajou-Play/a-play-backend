package com.paran.aplay.user.repository;

import com.paran.aplay.user.domain.AuthProvider;
import com.paran.aplay.user.domain.OAuthUser;
import com.paran.aplay.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByNameAndIsQuit(@Param("name") String name, @Param("isQuit") Boolean isQuit);

  Optional<User> findByIdAndIsQuit(@Param("userId") Long userId, @Param("isQuit") Boolean isQuit);

  Optional<User> findByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

  boolean existsByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

  @Query("select ou from OAuthUser ou where ou.provider = :provider and ou.providerId = :providerId")
  Optional<OAuthUser> findByProviderAndProviderId(@Param("provider") AuthProvider provider, @Param("providerId") String providerId);
}
