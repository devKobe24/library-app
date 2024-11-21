package com.group.libraryapp.domain.user.loanHistory;

import com.group.libraryapp.domain.user.User;

import javax.persistence.*;

@Entity
public class UserLoanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @Column(nullable = false, name = "book_name")
    private String bookName;

    @Column(nullable = false, name = "is_return")
    private boolean isReturn;

    protected UserLoanHistory() {}

    public UserLoanHistory(User user, String bookName) {
        this.user = user;
        this.bookName = bookName;
        this.isReturn = false;
    }

    // 대출 기록 반납처리를 위한 함수 생성.
    // 대출 기록에 대한 return 작업을 한다는 의미의 함수
    public void doReturn() {
        this.isReturn = true;
    }

    public String getBookName() {
        return this.bookName;
    }
}
