<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jsp" %>

<div class="container mt-5">
  <div class="row">
    <div class="col-md-6 offset-md-3">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4 class="mb-0">로그인</h4>
        </div>
        <div class="card-body">
          <form id="signinForm">
            <div class="mb-3">
              <label for="email" class="form-label">이메일</label>
              <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">비밀번호</label>
              <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <div class="d-grid">
              <button type="submit" class="btn btn-primary">로그인</button>
            </div>
          </form>
          <div class="mt-3 text-center">
            <p>계정이 없으신가요? <a href="${pageContext.request.contextPath}/signup">회원가입</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  let isSigningIn = false;

  document.getElementById('signinForm').addEventListener('submit', function(e) {
    e.preventDefault();

    if (isSigningIn) return; // 중복 로그인 방지
    isSigningIn = true; // 로그인 진행 중으로 설정

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('${pageContext.request.contextPath}/api/sign-in', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        password: password
      })
    })
            .then(response => {
              if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
              }
              return response.text();
            })
            .then(data => {
              alert('로그인이 완료되었습니다.');
              window.location.href = '../..';
            })
            .catch(error => {
              alert('로그인 실패: ' + error.message);
            })
            .finally(() => {
              isSigningIn = false; // 로그인 완료되거나 실패 시 다시 클릭 가능
            });
  });


</script>

<%@ include file="footer.jsp" %>