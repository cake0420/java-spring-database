<footer class="py-4 bg-dark text-white mt-5">
    <div class="container">
      <p class="m-0 text-center">© 2025 도서 서비스 - All Rights Reserved</p>
    </div>
  </footer>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    document.getElementById('logoutLink')?.addEventListener('click', function(e) {
      e.preventDefault();

      // 로그아웃 요청 처리
      const userId = '${sessionScope.USER_ID}';  // USER_ID를 세션에 저장해야 함

      fetch('${pageContext.request.contextPath}/api/sign-out', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId: userId })
      })
              .then(response => response.text())
              .then(data => {
                alert(data);
                window.location.href = '${pageContext.request.contextPath}/';
              })
              .catch(error => {
                console.error('로그아웃 실패:', error);
                alert('로그아웃 처리 중 오류가 발생했습니다.');
              });
    });
  </script>