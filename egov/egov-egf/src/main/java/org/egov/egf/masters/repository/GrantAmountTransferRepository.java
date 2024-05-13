package org.egov.egf.masters.repository;

import java.util.List;

import org.egov.egf.masters.model.GrantAmountTransfer;
import org.egov.model.masters.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrantAmountTransferRepository extends JpaRepository<GrantAmountTransfer, Long>{
	
	
	//public List<GrantAmountTransfer> findByNameLikeIgnoreCaseOrCodeLikeIgnoreCase(String grantType, String grant);
	
	List<GrantAmountTransfer> findAll();
	
	List<String> findBankAccountTypeByBankAccountNumber(String bankAccountNumber);

}