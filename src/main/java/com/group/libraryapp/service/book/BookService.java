package com.group.libraryapp.service.book;


import com.group.libraryapp.domain.book.Book;
import com.group.libraryapp.domain.book.BookRepository;
import com.group.libraryapp.domain.user.User;
import com.group.libraryapp.domain.user.UserRepository;
import com.group.libraryapp.domain.user.loanHistory.UserLoanHistory;
import com.group.libraryapp.domain.user.loanHistory.UserLoanHistoryRepository;
import com.group.libraryapp.dto.book.request.BookCreateRequest;
import com.group.libraryapp.dto.book.request.BookLoanRequest;
import com.group.libraryapp.dto.book.request.BookReturnRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // 대출 기록 정보 확인을 위해 의존성 추가.
    private final UserLoanHistoryRepository userLoanHistoryRepository;

    // 유저 정보를 가져오기 위해 의존성 추가.
    private final UserRepository userRepository;

    public BookService(
            BookRepository bookRepository,
            UserLoanHistoryRepository userLoanHistoryRepository,
            UserRepository userRepository
    ) {
        this.bookRepository = bookRepository;
        this.userLoanHistoryRepository = userLoanHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveBook(BookCreateRequest request) {
        Book book = bookRepository.save(new Book(request.getName()));
    }

    @Transactional
    public void loanBook(BookLoanRequest request) {
        // 1. 책 정보를 가져온다.
        Book book = bookRepository.findByName(request.getBookName())
                .orElseThrow(IllegalArgumentException::new);

        // 2. 대출 기록 정보를 확인해서 대출중인지 확인한다.
        // 3. 만약에 확인했는데 대출 중이라면 예외를 발생시킨다.
        if (userLoanHistoryRepository.existsByBookNameAndIsReturn(book.getName(), false)) {
            throw new IllegalArgumentException("진작 대출되어 있는 책입니다.");
        }

        // 4. 유저 정보를 가져온다.
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(IllegalArgumentException::new);

        user.loanBook(book.getName());
    }

    @Transactional
    public void returnBook(BookReturnRequest request) {
        // 1. 유저의 이름을 가지고 유저를 찾아서 유저의 아이디를 가져온다.
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(IllegalArgumentException::new);

        user.returnBook(request.getBookName());

        // ==================== 추가 =================
        // userLoanHistoryRepository.save(history);
        // 위 코드는 사용하지 않아도 됩니다.
        // 원래대로라면 위 코드로 변경된 객체의 값을 다시 업데이트 해줘야겠지만
        // `@Transactional` 어노테이션을 사용하고 있기 때문에 영속성 컨텍스트가
        // `returnBook(BookReturnRequest)` 함수 블럭 안에 존재하고
        // 영속성 컨텍스트는 "변경 감지" 기능이 있기 때문에 영속성 컨텍스트 내부에서 가져온
        // Entity 객체인 `UserLoanHistory`에 대해서 변경이 일어나면 그것을 감지했다가
        // 자동으로 업데이트 해주기 때문입니다.
    }
}
