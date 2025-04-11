<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jsp" %>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-12">
            <h2 class="mb-4">마이페이지</h2>

            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">내 정보</h5>
                </div>
                <div class="card-body">
                    <p><strong>이름:</strong> <span id="userName">사용자 이름</span></p>
                    <p><strong>이메일:</strong> <span id="userEmail">사용자 이메일</span></p>
                </div>
            </div>

            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">현재 대출 중인 도서</h5>
                </div>
                <div class="card-body">
                    <div id="currentLoans">
                        <div class="alert alert-info">현재 대출 중인 도서가 없습니다.</div>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">대출 이력</h5>
                </div>
                <div class="card-body">
                    <div id="loanHistory">
                        <div class="alert alert-info">대출 이력이 없습니다.</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let isMypageData = false
    // 사용자 정보 가져오기
    async function loadUserData() {
        if (isMypageData) return; // 이미 로그아웃 중이면 무시
        isMypageData = true; // 플래그 설정

        try {
            const sessionId = '${sessionScope.SESSION_ID}';
            const response = await fetch('${pageContext.request.contextPath}/api/protected/mypage', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ sessionId })
            });

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text);
            }

            const data = await response.json();
            document.getElementById('userName').textContent = data.name;
            document.getElementById('userEmail').textContent = data.email;

        } catch (error) {
            alert('사용자 정보를 가져올 수 없습니다. 다시 로그인 해주세요: ' + error.message);
        } finally {
            isMypageData = false;
        }
    }

    // 대출 중인 도서 목록 가져오기
    async function loadCurrentLoans() {
        // 실제 구현 시 API 호출 필요
        const loansData = [
            { id: 1, title: '자바 프로그래밍', author: '홍길동', dueDate: '2025-04-20' },
            { id: 2, title: '스프링 부트', author: '김영희', dueDate: '2025-04-18' }
        ];

        const loansContainer = document.getElementById('currentLoans');

        if (loansData.length > 0) {
            let html = '<table class="table table-striped">';
            html += '<thead><tr><th>제목</th><th>저자</th><th>반납예정일</th><th>액션</th></tr></thead><tbody>';

            loansData.forEach(loan => {
                html += `<tr>
                <td>${loan.title}</td>
                <td>${loan.author}</td>
                <td>${loan.dueDate}</td>
                <td><button class="btn btn-sm btn-primary" onclick="returnBook(${loan.id})">반납하기</button></td>
            </tr>`;
            });

            html += '</tbody></table>';
            loansContainer.innerHTML = html;
        }
    }

    // 대출 이력 가져오기
    async function loadLoanHistory() {
        // 실제 구현 시 API 호출 필요
        const historyData = [
            { id: 3, title: 'SQL 기초', author: '이철수', loanDate: '2025-03-10', returnDate: '2025-03-25' },
            { id: 4, title: 'HTML & CSS', author: '박지민', loanDate: '2025-02-15', returnDate: '2025-03-01' }
        ];

        const historyContainer = document.getElementById('loanHistory');

        if (historyData.length > 0) {
            let html = '<table class="table table-striped">';
            html += '<thead><tr><th>제목</th><th>저자</th><th>대출일</th><th>반납일</th></tr></thead><tbody>';

            historyData.forEach(item => {
                html += `<tr>
                <td>${item.title}</td>
                <td>${item.author}</td>
                <td>${item.loanDate}</td>
                <td>${item.returnDate}</td>
            </tr>`;
            });

            html += '</tbody></table>';
            historyContainer.innerHTML = html;
        }
    }

    // 도서 반납
    async function returnBook(bookId) {
        if (confirm('이 도서를 반납하시겠습니까?')) {
            // 실제 구현 시 API 호출 필요
            alert('도서가 성공적으로 반납되었습니다.');
            await loadCurrentLoans();
            await loadLoanHistory();
        }
    }

    // 페이지 로드 시 실행
    window.addEventListener('DOMContentLoaded', async () => {
        await loadUserData();
        await loadCurrentLoans();
        await loadLoanHistory();
    });
</script>

<%@ include file="footer.jsp" %>