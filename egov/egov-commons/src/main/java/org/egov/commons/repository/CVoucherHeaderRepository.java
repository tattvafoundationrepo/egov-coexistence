package org.egov.commons.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.egov.commons.CVoucherHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CVoucherHeaderRepository  extends JpaRepository<CVoucherHeader,Long>{
	
	List<CVoucherHeader> findAll();
	
	@Query(value = "SELECT * FROM voucherheader WHERE id = (SELECT MAX(id) FROM voucherheader)", nativeQuery = true)
    Optional<CVoucherHeader> getDetailsForMaxIdFromVoucherHeader();
	
	@Query(value = "SELECT * FROM voucherheader WHERE id = (SELECT MAX(id) FROM voucherheader)", nativeQuery = true)
    public  CVoucherHeader getDetailsForMaxIdFromVoucherHeaders();
	
	
	@Query(value = "SELECT VH.id AS MissingVoucherHeaders " +
            "FROM voucherheader VH " +
            "LEFT JOIN generalledger GL ON VH.Id = GL.voucherheaderid " +
            "WHERE GL.voucherheaderid IS NULL",
     nativeQuery = true)
List<BigInteger> findMissingVoucherHeaders();
}


