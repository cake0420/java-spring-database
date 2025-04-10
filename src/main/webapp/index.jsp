<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="header.jsp" %>

<div class="container mt-5">
  <div class="row">
    <div class="col-md-12 text-center">
      <h1 class="display-4 mb-4">도서 서비스에 오신 것을 환영합니다</h1>
      <p class="lead">다양한 도서를 검색하고 대출할 수 있는 서비스입니다.</p>
    </div>
  </div>

  <div class="row mt-4">
    <div class="col-md-8 offset-md-2">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">도서 검색</h5>
          <div class="input-group mb-3">
            <input type="text" class="form-control" placeholder="도서명 또는 저자 검색">
            <button class="btn btn-primary" type="button">검색</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="row mt-5">
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-body">
          <h5 class="card-title">신간 도서</h5>
          <p class="card-text">최신 도서 정보를 확인하고 대출하세요.</p>
          <a href="#" class="btn btn-outline-primary">더보기</a>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-body">
          <h5 class="card-title">인기 도서</h5>
          <p class="card-text">가장 많이 대출된 인기 도서를 확인하세요.</p>
          <a href="#" class="btn btn-outline-primary">더보기</a>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-body">
          <h5 class="card-title">내 도서 관리</h5>
          <p class="card-text">대출 중인 도서와 연체 정보를 확인하세요.</p>
          <c:choose>
            <c:when test="${sessionScope.SESSION_ID != null}">
              <a href="${pageContext.request.contextPath}/mypage" class="btn btn-outline-primary">마이페이지</a>
            </c:when>
            <c:otherwise>
              <a href="${pageContext.request.contextPath}/signin" class="btn btn-outline-primary">로그인하기</a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="footer.jsp" %>