package com.example.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ledger.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

  public Role findByName(String name);

}
