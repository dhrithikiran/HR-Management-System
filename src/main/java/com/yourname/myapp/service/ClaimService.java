package com.yourname.myapp.service;

import com.yourname.myapp.entity.Claim;
import com.yourname.myapp.repository.ClaimRepository;
import java.math.BigDecimal;
import java.util.List;

public class ClaimService {

    private final ClaimRepository repo = new ClaimRepository();

    public List<Claim> getAllClaims() {
        return repo.findAll();
    }

    public List<Claim> getByEmployeeId(String employeeId) {
        return repo.findByEmployeeId(employeeId);
    }

    public void addClaim(String employeeId, String claimType, BigDecimal amount) {
        Claim claim = new Claim();
        claim.setEmployeeId(employeeId);
        claim.setClaimType(claimType);
        claim.setAmount(amount);
        claim.setClaimStatus(Claim.ClaimStatus.PENDING);
        repo.save(claim);
    }

    public void updateClaimStatus(Long id, String status) {
        Claim claim = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
        claim.setClaimStatus(Claim.ClaimStatus.valueOf(status));
        repo.save(claim);
    }
}
