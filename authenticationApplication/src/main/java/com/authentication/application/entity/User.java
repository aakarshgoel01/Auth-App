package com.authentication.application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "Users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="user_id")
	private UUID id ;

	@Column(name="user_name", nullable = false, unique = true)
	private String name;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, unique = true, length = 500)
	private String email;

	private String image;

	private boolean enable = true;

	private Instant createdAt = Instant.now();

	private Instant updatedAt = Instant.now();

	@Enumerated(EnumType.STRING)
	private Provider provider = Provider.LOCAL;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "User_Roles",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<Role>();

	@PrePersist
	public void prePersist(){
		if(createdAt == null)
		createdAt = Instant.now();

		updatedAt = Instant.now();

	}

	@PreUpdate
	public void preUpdate(){
		updatedAt = Instant.now();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
	}

	@Override
	public String getUsername() {
		return getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isEnable();
	}
}
