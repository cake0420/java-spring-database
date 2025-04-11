<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 캐시 방지 헤더 --%>
<%
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>도서 서비스</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body {
      font-family: 'Noto Sans KR', sans-serif;
      background-color: #f8f9fa;
    }
    .navbar {
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
  </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white">
  <div class="container">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/">도서 서비스</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <c:choose>
          <c:when test="${sessionScope.SESSION_ID != null}">
            <li class="nav-item">
              <a class="nav-link" href="${pageContext.request.contextPath}/mypage">마이페이지</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#" id="logoutLink">로그아웃</a>
            </li>
          </c:when>
          <c:otherwise>
            <li class="nav-item">
              <a class="nav-link" href="${pageContext.request.contextPath}/signin">로그인</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="${pageContext.request.contextPath}/signup">회원가입</a>
            </li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
  let isLoggingOut = false;
  document.getElementById('logoutLink')?.addEventListener('click', async function(e) {
    e.preventDefault();
    if (isLoggingOut) return; // 이미 로그아웃 중이면 무시
    isLoggingOut = true; // 플래그 설정

    const sessionId = '${sessionScope.SESSION_ID}';
    try {
      const response = await fetch('${pageContext.request.contextPath}/api/sign-out', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ sessionId })
      });

      const data = await response.text();

      alert(data);
      // 캐시된 내용 제거 후 홈으로 이동
      window.location.href = '${pageContext.request.contextPath}/';
    } catch (error) {
      console.error('로그아웃 실패:', error);
      alert('로그아웃 처리 중 오류가 발생했습니다.');
    } finally {
      isLoggingOut = false; // 실패했을 경우 다시 클릭 가능하도록 초기화
    }
  });
</script>
