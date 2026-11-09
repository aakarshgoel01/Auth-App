package com.authentication.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "User_Roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {
	@Id
	@Column(name = "role_id")
	private UUID id = UUID.randomUUID();
	@Column(unique = true, nullable = false)
	private String name;
}
