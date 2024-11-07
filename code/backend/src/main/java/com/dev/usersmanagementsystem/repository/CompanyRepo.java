package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Integer> {

}
