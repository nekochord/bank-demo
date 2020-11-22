package com.demo.account.repository;

import com.demo.account.entity.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionRepository extends CrudRepository<AccountTransaction, Long> {

    public Page<AccountTransaction> findByAccountIdOrderByCreatedDateDesc(Long accountId, Pageable pageable);
}
