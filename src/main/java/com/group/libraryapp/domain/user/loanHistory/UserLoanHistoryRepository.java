package com.group.libraryapp.domain.user.loanHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoanHistoryRepository extends JpaRepository<UserLoanHistory, Long> {

    // SELECT * FROM user_loan_history WHERE book_name = ? AND is_return = ?
    boolean existsByBookNameAndIsReturn(String name, boolean isReturn);
}
