package nidanpro_backend.security;

import lombok.RequiredArgsConstructor;
import nidanpro_backend.repository.StaffUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final StaffUserRepository staffUserRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = staffUserRepository.findByEmailIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .disabled(!user.isActive())
        .roles(user.getRole().getRoleName())
        .build();
  }
}
