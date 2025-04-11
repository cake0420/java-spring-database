<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jsp" %>

<div class="container mt-5">
  <div class="row">
    <div class="col-md-6 offset-md-3">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4 class="mb-0">회원가입</h4>
        </div>
        <div class="card-body">
          <form id="signupForm">
            <div class="mb-3">
              <label for="name" class="form-label">이름</label>
              <input type="text" class="form-control" id="name" name="name" required>
            </div>
            <div class="mb-3">
              <label for="email" class="form-label">이메일</label>
              <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">비밀번호</label>
              <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <div class="mb-3">
              <label for="confirmPassword" class="form-label">비밀번호 확인</label>
              <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
            </div>
            <div class="d-grid">
              <button type="submit" class="btn btn-primary">회원가입</button>
            </div>
          </form>
          <div class="mt-3 text-center">
            <p>이미 계정이 있으신가요? <a href="${pageContext.request.contextPath}/signin">로그인</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  let isSignUp = false;
  document.getElementById('signupForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (isSignUp) return;
    isSignUp = true

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // 비밀번호 확인
    if (password !== confirmPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    // 회원가입 요청 전송
    fetch('${pageContext.request.contextPath}/api/sign-up', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: name,
        email: email,
        password: password
      })
    })
      .then(response => {
        if (!response.ok) {
          return response.text().then(text => { throw new Error(text) });
        }
        return response.text();
      })
      .then(data => {
        alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '${pageContext.request.contextPath}/signin';
      })
      .catch(error => {
        alert('회원가입 실패: ' + error.message);
      }).finally( () => {
        isSignUp = false;
      }
    );
  });
</script>

<%@ include file="footer.jsp" %>