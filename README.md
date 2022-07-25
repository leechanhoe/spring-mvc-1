![](https://velog.velcdn.com/images/dodo4723/post/cda1696e-c562-463d-b65b-63f7e806100b/image.png)

# 0. 목차

[1. 웹 애플리케이션의 이해](#1-웹-애플리케이션의-이해)

[2. 서블릿](#2-서블릿)

[3. 서블릿, JSP, MVP 패턴](#3-서블릿-jsp-mvc-패턴)

[4. MVC 프레임워크 만들기](#4-mvc-프레임워크-만들기)

[5. 스프링 MVC 구조 이해](#5-스프링-mvc-구조-이해)

[6. 스프링 MVC - 기본 기능](#6-스프링-mvc---기본-기능)

[7. 스프링 MVC - 웹 페이지 만들기](#7-스프링-mvc---웹-페이지-만들기)

[8. 마치며](#8-마치며)

# 1. 웹 애플리케이션의 이해

 <br/>
 <br/>

## 1.1. 웹 서버, 웹 애플리케이션 서버

웹에서 모든 형태의 데이터는 거의 HTTP 형식으로 전달된다. 서버끼리 데이터를 주고받을 때도 대부분 HTTP로 통신한다.

### 웹서버

- HTTP기반 동작이며 정적리소스(HTML,CSS,JS,이미지,영상), 기타 부가기능을 제공한다.
- nginx, apache
 
 <br/>

### 웹 애플리케이션 서버(WAS)

- WAS라고 한다. HTTP기반 동작이며 웹서버 기능을 포함한다.
- 프로그램 코드를 실행시켜 애플리케이션 로직 수행을 한다. 동적 HTML, HTTP API(JSON), 서블릿, JSP, 스프링 MVC 등은 모두 WAS에서 동작한다.
- 톰캣, jetty
 
 <br/>
 
### 웹 시스템 구성

- 웹서버, WAS, DB로 구성되나 WAS가 웹서버도 포함하는 경우도 많다
- WAS만 사용시 WAS에서 장애가 나면 오류화면조차 노출 불가능. - WAS가 너무 많은 역할을 담당하여 부하가 걸릴 수 있다.
- 정적리소스는 웹서버가 처리하고 동적로직은 WAS가 처리하는 식으로 분리하여 많이 사용한다. 
- 나눠 사용할 경우 리소스 관리가 효율적이다. 정적 리소스가 많이 사용되면 WEB서버를 증설하고 동적리소스가 많이 사용되면 WAS를 증설하면 된다.
 
 <br/>
 <br/>
 
## 1.2. 서블릿

### 서블릿이 탄생한 배경

회원 저장을 예로 들어 HTTP로 통신할 경우 다음과 같은 과정을 따른다.

> 1. TCP/IP 연결 대기, 소켓 연결
2. HTTP 요청 파싱해서 읽기
3. POST, GET방식 등 인지하고  요청URL(/save) 인지
Content-Type 확인
4. HTTP 메세지 바디 내용 파싱(username, age 등 데이터 사용할 수 있게)
5. **저장프로세스 실행(save)**
6. 비지니스 로직 실행 -> DB저장요청
7. HTTP RES 메세지 생성 시작 : HTTP 시작라인 생성, Header생성, 메시지 바디에 HTML 생성해서 입력
8. TCP/IP 응답전달, 소켓종료

위에 저것을 다 직접 구현하기엔 너무 힘들것이다.
**서블릿은 위의 저장프로세스 실행을 제외한 모든 것을 처리해준다**.

<br/>

### 서블릿 컨테이너
- WAS 안에 존재
- 서블릿 컨테이너가 서블릿 객체를 생성해주고 호출해줌
- 서블릿 생명주기도 관리
- 톰캣처럼 서블릿 지원하는 WAS
- JSP도 서블릿으로 변환되어 사용됨
- WAS는 동시요청을 위한 멀티쓰레드 처리를 지원

- 서블릿 객체는 싱글톤으로 관리
   - 요청마다 생성하면 비효율적 
   - 최초 로딩시점에 미리 만들어 재활용
   - 모든 고객요청은 동일한 서블릿 객체 인스턴스 접근
   - 공유변수 사용시 주의
   - 서블릿컨테이너 종료시 서블릿 객체들도 종료
   - request, response 객체는 항상 새로 생성
   
<br/>
<br/>

## 1.3. 동시요청 - 멀티쓰레드

**쓰레드**란 자바코드 한줄한줄 해석할 수 있는 단위이다. 단일 쓰레드와 멀티 쓰레드를 비교해보자.


### 단일쓰레드

request가 오면 WAS는 서블릿 수행을 위한 쓰레드 1개를 할당한다.
이때 다른 요청이오면 쓰레드가 1개라서 지연문제가 발생한다.
 

### 멀티쓰레드

추가 요청이 올 때 쓰레드를 또 생성해서 서블릿 수행한다

- 장점 : 동시요청 처리가 가능하며 리소스 허용시까지 처리가 가능

- 단점 : 쓰레드 생성비용이 비싸며 쓰레드 **컨텍스트 스위칭** 비용이 발생한다. 코어가 1개인 경우 쓰레드를 2개 왔다갔다 할 것이다. 또한 쓰레드 생성에 제한이 없으면 고객 요청이 1만개 올 때 서버가 죽을 것이다.
 

### 쓰레드 풀

쓰레드 생성에 대한 문제를 해결, 보완해준다.
- 쓰레드 풀에 쓰레드를 미리 만들어놔서 요청이 오면 만들어 둔 쓰레드를 준다.
- 200개를 셋팅하고 250개 요청이 들어오면 쓰레드풀 설정으로 대기, 거절 등 가능
- 톰캣은 최대 200개가 기본 설정
 
이전에 유니티로 게임을 만들때도 이와 비슷한 풀링기법으로 최적화 했던 기억이 난다.

### 실무팁

**WAS의 주요 튜닝포인트는 max thread 설정이다.**

너무 낮게 설정하면 클라이언트 응답 지연, 너무 높게 설정하면 동시 요청이 많아질 때 CPU와 메모리 리소스 임계점 초과로 서버 다운될 수 있다.

장애발생시 클라우드면 일단 서버부터 늘리고 튜닝 / 클라우드가 아니면 열심히 튜닝

쓰레드 풀 적정 숫자는 성능테스트를 해보는 것이 가장 좋다. 아파치나 jMeter, nGrinder 등이 있다.

**WAS는 멀티 쓰레드를 지원한다!**
개발자가 멀티 쓰레드 관련 코드를 신경쓸 필요가 없다.
싱글 쓰레드 프로그래밍 하듯이 소스코드를 개발해도 된다.
멀티쓰레드 환경이기 때문에 멤버변수가 공유되어 싱글톤 객체(서블릿, 스프링 빈)를 주의해서 사용해야 한다.

<br/>
<br/>

## 1.4. HTML, HTTP API, CSR, SSR

### 1) HTTP API

HTML이 아니라 데이터를 전달한다.

- 주로 JSON형식 데이터 통신을 하며 다양한 시스템에서 호출한다.
- UI 클라이언트의 접점이다. 앱클라이언트(아이폰, 안드로이드, PC APP)가 대표적이다.
- UI 클라이언트 사용시 js 통한 http API 호출하는 식으로 많이 사용되며 React, Vue가 대표적이다. 2~3달 전에 React공부를 할때 한번 호출해보았다.
- 서버 to 서버 통신시에도 많이 사용한다.
- 기업간 데이터 통신할때도 사용한다.
 
 <br/>

### 2) 서버사이드 랜더링(SSR)

- 서버에서 최종 HTML을 생성해서 클라이언트에 전달한다.
 
 <br/>

### 3) 클라이언트 사이드 랜더링(CSR)

- HTML 결과를 자바스크립트를 사용해 웹브라우저에서 동적으로 생성해서 적용한다.
- 동적인 화면에서 사용하며 웹 환경을 앱처럼 필요한 부분부분 변경 가능하다.
- 구글지도나 Gmail이 대표적이며 React, Vue에서 사용한다.
- SSR+CSR도 가능하다.

<br/>
<br/>

## 1.5. 자바 백엔드 웹 기술 역사

<br/>

### 과거기술

- 서블릿(1997) :서블릿으로는 HTML 동적 생성의 어려움이 있었음
- JSP(1999) : HTML 생성은 편하지만 비지니스 로직까지 너무 많은 역할이 묵여있음
- 서블릿, JSP 조합의 MVC 패턴 사용 : 모델, 뷰, 컨트롤러로 역할 나누어 개발
- MVC 프레임워크 춘추전국시대(2000년초~2010년초) : MVC 패턴 자동화, 복잡한 웹기술을 편리하게 사용할 수 있는 다양한 기능 지원(스트럿츠, 웹워크, 스프링MVC 과거ver)

<br/>

### 현재사용기술

- 애노테이션 기반 스프링 MVC 등장
   - @Controller 등으로 쉽게 처리되며 MVC 춘추전국시대의 마무리
- 스프링부트 등장
   - 서버를 내장
   - 기존 : 서버에 WAS설치 후 소스는 War로 만들어 서버에 배포
   - 스프링부트방식 : 빌드결과(Jar)에 WAS서버 포함되어 있어서 빌드 단순화

<br/>

### 최신기술 - 스프링 웹 기술 분화

- Web Servlet - Spring MVC
= Web Reactive - Spring WebFlux
 
<br/>

### 스프링 웹플럭스(WebFlux)

- 비동기 Non-Blocking 처리
- 최소 쓰레드로 최대 성능 - 컨텍스트 스위칭 비용 효율적
- 함수형 스타일로 개발 - 동시처리 코드 효율화
- 서블릿 기술 사용 안 함
- 어렵고 RDB지원이 부족하며 일반 MVC로 충분히 빠르다.
 
<br/>

### 자바 뷰템플릿 역사

- HTML을 편리하게 생성하는 뷰 기능
- JSP : 속노느림, 기능부족
- 프리마커, 벨로시티 : 속도문제 해결, 다양한 기능
- **타임리프(Thymeleaf) : 내추럴 템플릿(HTML모양 유지), 스프링 MVC와 강력한 기능 통합, 최선의 선택**

<br/>
<br/>

# 2. 서블릿

## 2.1. Hello 서블릿

메인에 `@ServletComponentScan`을 붙여주면 서블릿을 자동 등록 해준다.
![](https://velog.velcdn.com/images/dodo4723/post/9d4d045e-d1e7-49a2-8dfc-1eb7b8ab4f62/image.png)

`@WebServlet`에서 name은 서블릿 이름, urlPatterns를 통해 URL을 매핑한다.

![](https://velog.velcdn.com/images/dodo4723/post/0a9fe4bd-d29d-4d85-af05-565721bdc33a/image.png)

## 2.2 HTTPServletRequest
: 개발자 대신에 HTTP요청 메시지를 파싱하는 역할이다.
: start line, header에 대한 정보 조회 방법이다.

## 2.3 HTTP 요청 데이터

HTTP 요청 메시지를 통하여 클라이언트에서 서버로 데이터를 전달하는 방법에는 크게 세 가지가 있다.

### 2.3.1. GET - 쿼리 파라미터
- 메시지 바디 없이 url의 쿼리 파라미터를 통하여 전달
- ? 로 시작, 추가 파라미터는 &로 구분
![](https://velog.velcdn.com/images/dodo4723/post/b9aaf63a-1493-483f-a1cc-aabbc6703e41/image.png)

### 2.3.2. POST - HTML Form

- HTML form을 사용해서 클라이언트에서 서버로 데이터를 전송한다.
- 메시지 바디에 쿼리 파라미터 형식으로 전달
- 위의 코드를 중복해서 사용할 수 있다.(쿼리 파라미터 조회 메소드)

### 2.3.3. HTTP message body에 직접 담아 요청

HTTP API에 주로 사용 데이터를 주로 JSON 사용함.

1. API 메시지 바디 - 단순 텍스트
HTTP 메시지 바디의 데이터를 InputStream을 사용해서 바이트코드로 얻을 수 있고, Streamutils를 활용하여 인코딩 정보를 제공하면 string으로 바꿀 수 있다.
![](https://velog.velcdn.com/images/dodo4723/post/0e5cd912-214e-44de-a888-2f840dd71797/image.png)

2. API 메시지 바디 - Json
- lombok을 통해 자동으로 getter와 setter 생성
- Json을 파싱해서 사용하려면 jackson 라이브러리의 ObjectMapper를 사용하면 된다.

<br/>

#### 4. HTTP 응답 데이터 - 단순 텍스트,HTML
단순 텍스트 응답, HTML 응답, HTTP API - MessageBody JSON 응답등의 내용을 담아 전달한다.

- Content-Type,쿠키, Redirect등의 기능을 제공한다.

   - 간단한 텍스트 메시지를 담아 전송
	1. HTTP 응답으로 HTML을 반환할 때는
`response.setContentType("text/html");`
	2. Json 방식
 `response.setHeader("content-type", "application/json");`
 
<br/>
<br/>

# 3. 서블릿, JSP, MVC 패턴

## 3.1. 서블릿으로 회원 관리 웹 애플리케이션 만들기

### 요구사항

- 회원 정보
   - 이름 : `username`
   - 나이 : `age`


- 기능 요구사항
   - 회원 저장
   - 회원 목록 조회
   
<br/>

아래 코드는 HTML Form에서 데이터를 입력하고 전송을 누르면 실제 회원 데이터가 저장되도록 하는 `MemberSaveServlet`클래스이다
```java
@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MemberSaveServlet.service");
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        System.out.println("member = " + member);
        memberRepository.save(member);

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter w = response.getWriter();
        w.write("<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "성공\n" +
                "<ul>\n" +
                " <li>id="+member.getId()+"</li>\n" +
                " <li>username="+member.getUsername()+"</li>\n" +
                " <li>age="+member.getAge()+"</li>\n" +
                "</ul>\n" +
                "<a href=\"/index.html\">메인</a>\n" +
                "</body>\n" +
                "</html>");
    }
}
```

위 코드처럼 서블릿과 자바 코드만으로 HTML을 만들어보았다.

서블릿 덕분에 동적으로 원하는 HTML을 마음껏 만들 수 있다. 정적인 HTML 문서라면 화면이 계속 달라지는 회원의 저장 결과라던가, 회원 목록같은 동적인 HTML을 만드는 일은 불가능 할 것이다.

그런데, 코드에서 보듯이 이것은 **매우 복잡하고 비효율적**이다. 자바 코드로 HTML을 만들어 내는 것 보다 차라리 **HTML 문서에 동적으로 변경해야 하는 부분만 자바 코드를 넣을 수 있다면 **더 편리할 것이다.

**이것이 바로 템플릿 엔진이 나온 이유이다.** 템플릿 엔진을 사용하면 HTML 문서에서 필요한 곳만 코드를 적용해서 동적으로 변경할 수 있다.

템플릿 엔진에는 **JSP, Thymeleaf, Freemarker, Velocity**등이 있다.

<br/>

## 3.2. JSP로 회원 관리 웹 애플리케이션 만들기

아래 코드는 전에 서블릿으로만 만들었던 것과는 달리 JSP문서로 회원 저장로직을 만든것이다.
```java
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
 // request, response 사용 가능
    MemberRepository memberRepository = MemberRepository.getInstance();
    System.out.println("save.jsp");
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));

    Member member = new Member(username, age);
    System.out.println("member = " + member);
    memberRepository.save(member);
 %>
 <html>
 <head>
    <meta charset="UTF-8">
 </head>
 <body>
 성공
 <ul>
    <li>id=<%=member.getId()%></li>
    <li>username=<%=member.getUsername()%></li>
    <li>age=<%=member.getAge()%></li>
 </ul>
 <a href="/index.html">메인</a>
 </body>
 </html>
 ```
 
 JSP는 자바 코드를 그대로 다 사용할 수 있다.
`<%@ page import="hello.servlet.domain.member.MemberRepository" %>`
자바의 import 문과 같다.
`<% ~~ %>`
이 부분에는 자바 코드를 입력할 수 있다.
`<%= ~~ %>`
이 부분에는 자바 코드를 출력할 수 있다

회원 저장 JSP를 보면, 회원 저장 서블릿 코드와 같다. 다른 점이 있다면, HTML을 중심으로 하고, 자바코드를 부분부분 입력해주었다. `<% ~ %>` 를 사용해서 HTML 중간에 자바 코드를 출력하고 있다.

<br/>

## 3.3. 서블릿과 JSP의 한계

서블릿으로 개발할 때는 뷰(View)화면을 위한 **HTML을 만드는 작업이 자바 코드에 섞여서** 지저분하고 복잡했다.

JSP를 사용한 덕분에 뷰를 생성하는 HTML 작업을 깔끔하게 가져가고, 중간중간 동적으로 변경이 필요한 부분에만 자바 코드를 적용했다. 그런데 이렇게 해도 해결되지 않는 몇가지 고민이 남는다.

회원 저장 JSP를 보자. 코드의 상위 절반은 회원을 저장하기 위한 비즈니스 로직이고, 나머지 하위 절반만 결과를 HTML로 보여주기 위한 뷰 영역이다. 회원 목록의 경우에도 마찬가지다.

코드를 잘 보면, JAVA 코드, 데이터를 조회하는 리포지토리 등등 **다양한 코드가 모두 JSP에 노출**되어 있다. 

**JSP가 너무 많은 역할을 한다.** 이렇게 작은 프로젝트도 벌써 머리가 아파오는데, 수백 수천줄이 넘어가는JSP를 떠올려보면 정말 지옥과 같을 것이다. 

<br/>
<br/>

## 3.4. MVC 패턴


### Model View Controller
MVC 패턴은 지금까지 학습한 것 처럼 하나의 서블릿이나, JSP로 처리하던 것을 **컨트롤러(Controller)**와 **뷰(View)**라는 영역으로 서로 역할을 나눈 것을 말한다. 웹 애플리케이션은 보통 이 MVC 패턴을 사용한다.

#### 컨트롤러
HTTP 요청을 받아서 파라미터를 검증하고, 비즈니스 로직을 실행한다. 그리고 뷰에 전달할 결과 데이터를 조회해서 모델에 담는다.

#### 뷰
모델에 담겨있는 데이터를 사용해서 화면을 그리는 일에 집중한다. 여기서는 HTML을 생성하는 부분을 말한다

#### 모델
뷰에 출력할 데이터를 담아둔다. 뷰가 필요한 데이터를 모두 모델에 담아서 전달해주는 덕분에 뷰는 비즈니스 로직이나 데이터 접근을 몰라도 되고, 화면을 렌더링 하는 일에 집중할 수 있다.

![](https://velog.velcdn.com/images/dodo4723/post/d648081a-37f3-4524-9afb-196f1d6a735d/image.png)

위에서 했던 회원 저장을 컨트롤러와 뷰로 나눴다.

**회원 저장 - 컨트롤러**
```java
@WebServlet(name = "mvcMemberSaveSerrvlet", urlPatterns = "/servlet-mvc/members/save")
public class MvcMemberSaveSerrvlet extends HttpServlet {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        //model에 데이터를 보관한다.
        request.setAttribute("member", member);

        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
    }
}
```
**회원 저장 - 뷰**
```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
 <meta charset="UTF-8">
</head>
<body>
성공
<ul>
 <li>id=${member.id}</li>
 <li>username=${member.username}</li>
 <li>age=${member.age}</li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

MVC 덕분에 컨트롤러 로직과 뷰 로직을 확실하게 분리한 것을 확인할 수 있다. 향후 화면에 수정이 발생하면 뷰 로직만 변경하면 된다.

<br/>

### MVC 컨트롤러의 단점

**공통 처리가 어렵다.**
기능이 복잡해질 수 록 컨트롤러에서 공통으로 처리해야 하는 부분이 점점 더 많이 증가할 것이다. 단순히 공통 기능을 메서드로 뽑으면 될 것 같지만, 결과적으로 해당 메서드를 항상 호출해야 하고, 실수로 호출하지 않으면 문제가 될 것이다. 그리고 호출하는 것 자체도 중복이다

문제를 해결하려면 컨트롤러 호출 전에 먼저 공통 기능을 처리해야 한다. 소위 수문장 역할을 하는 기능이 필요하다. **프론트 컨트롤러(Front Controller)** 패턴을 도입하면 이런 문제를 깔끔하게 해결할 수 있다.

**스프링 MVC의 핵심도 바로 이 프론트 컨트롤러에 있다.**

<br/>
<br/>

# 4. MVC 프레임워크 만들기
MVC 프레임워크는 궁극적으로 스프링 MVC와 유사한 구조이기 때문에, 이해하기에 도움이 된다.

## 4.1. 프론트 컨트롤러 패턴(문지기)

- 예전에는 클라이언트가 공통로직이 필요할 경우 각각 다 만들어야 했다.
- 프런트 컨트롤러를 도입하면, 서블릿처럼 A,B,C를 각각 처리하도록 프론트 컨트롤러가 해결해준다.

<br/>

#### 프론트 컨트롤러의 특징
> - 서블릿 하나로 클라이언트의 요청을 받는다
- 요청에 맞는 컨트롤러를 찾아서 호출해준다.
- 공통처리가 가능해진다.
- 나머지 컨트롤러는 서블릿 사용 필요가 없어진다.

<br/>

## 4.2. 프론트 컨트롤러 도입
구조를 맞추는 단계

- 클라이언트가 HTTP요청을 하면, Front Controller가 매핑 정보에서 컨트롤러를 조회하고, 컨트롤러를 호출한다. 컨트롤러에서 JSP forward하고 HTML 응답을 해준다.

- 서블릿과 비슷한 모양의 컨트롤러 인터페이스를 도입하고, 각 컨트롤러들은 이 인터페이스를 구현하면 된다. 프론트 컨트롤러는 이 인터페이스를 호출해서 구현과 관계없이 로직의 일관성을 가져갈 수 있다.

- 내부 로직은 서블릿과 똑같이 만든다.

<br/>

## 4.3. View 분리

```java
request.setAttribute("member", member);
        String viewPath = "/WEB-INF/views/save-result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
```
위의 코드처럼, 모든 컨트롤러에서 뷰로 이동하는 부분에 중복이 있기 때문에, 뷰를 별도로 처리하는 객체를 만든다.

앞서 봤던 코드를 아래처럼 간단히 줄일 수 있다.
```java
return new MyView("/WEB-INF/views/new-form.jsp");
```
각각의 코드는 이렇게 `myView`객체를 생성 후 반환하고, 프론트 컨트롤러가 이를 일관적으로 처리한다.

<br/>

## 4.4. Model 추가
컨트롤러 입장에서, `HttpServletRequest`, `HttpServletResponce`를 활용한 파라미터는 필요하지만, 그자체는 필요하지 않다.

요청 파라미터 정보를 자바의 `Map`으로 대신 넘기게 하고 `request` 객체는 별도의 `Model` 객체를 만들어서 반환한다.

서블릿 기술을 전혀 사용하지 않도록 변경하는 것이 이 장의 내용이다.

<br/>

#### `frontControllerServlet` 인터페이스 V3
```java
@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {
    private Map<String, ControllerV3> controllerMap = new HashMap<>();
    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        ControllerV3 controller = controllerMap.get(requestURI);

        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request); 
        ModelView mv = controller.process(paramMap);
        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);
        view.render(mv.getModel(), request, response);
    }
    private Map<String, String> createParamMap(HttpServletRequest request) {
        //로직의 레벨을 맞추기위해 함수로 추출
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName,
                        request.getParameter(paramName)));
        return paramMap;
    }
    private MyView viewResolver(String viewName) {
        //service 에 구체적인 구현내용이 들어가기 보다는 레벨을 맞춰서 함수로 추출해줌
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
```

### 뷰 이름 중복 제거
컨트롤러에서 지정하는 뷰 이름에 중복이 있다.

`/WEB-INF/views/save.jsp`
`/WEB-INF/views/members.jsp`
중복되지 않는 `save`와 `members`를 논리 이름이라고 한다.
중복 제거를 위해 `viewReslover`를 통해 논리 이름만 반환할 수 있게 한다.

```java
private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
```

<br/>
<br/>

## 4.5. 단순하고 실용적인 컨트롤러
항상 `ModelView` 객체를 생성하고 반환해야 하는 부분이 번거롭다.

`controller`가 `ModelView`를 반환하지 않고, `ViewName`만 반환한다.

모델 객체 전달을 프론트 컨트롤러에서 생성한다.
```java
// ModelView mv = new ModelView("save-result"); // 모델 뷰 만들고
// mv.getModel().put("member",member); // put 함
// return mv;

model.put("member",member); // 그냥 put 만 하면 됨
return "save-result";
```

<br/>

## 4.6. 유연한 컨트롤러
완전히 다른 두 가지의 인터페이스를 호환가능하게 하는 **어댑터 패턴**을 추가하면 확장성이 용이해진다.

아래는 어댑터를 적용후 과정이다.
```java
 @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Object handler = getHandler(request);
        if (handler == null) { // 1. 핸들러를 찾는다
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adapter = getHandlerAdapter(handler); // 2. 어댑터를 찾는다

        ModelView mv = adapter.handle(request, response, handler);
        MyView view = viewResolver(mv.getViewName());
// 3. 이후는 전과 같다
        view.render(mv.getModel(), request, response);
    }
    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }
    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler=" + handler);
    }
```

아래는 V4를 추가하는 부분에서 어댑터의 역할이다
```java
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV4 controller = (ControllerV4) handler;

        Map<String, String> paramMap = createParamMap(request);
        HashMap<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model);
        //아래 두줄이 어댑터 역할
        ModelView mv = new ModelView(viewName);
        mv.setModel(model);
//  ControllerV4는 뷰의 이름을 반환하는데, 어댑터는 ModelView를 만들어서 반환해야 한다.
        return mv;
    }
```

<br/>
<br/>
<br/>


# 5. 스프링 MVC 구조 이해

## 5.1. 스프링 MVC 전체 구조

위에서 만들었던 스프링 MVC 프레임워크와 유사한 구조를 가지고 있다.
`DispatcherServlet`이 `Frontcontroller`를 구성했던 일을 그대로 한다.

> 동작 순서
1. 핸들러 조회
2. 핸들러 어댑터 조회
3. 핸들러 어댑터 실행
4. 핸들러 실행
5. ModelAndView 반환
6. viewResolver 호출
7. view 반환
8. 뷰 렌더링

<br>

## 5.2. 핸들러 매핑과 핸들러 어댑터
`implements Controller(web.servlet.mvc)`
`controller V2`와 `V3`의 중간 같은 역할을 한다.
이후로는 `@RequestMapping`을 사용하여 컨트롤러를 만든다.

## 5.3. 뷰 리졸버
`ModelAndView`를 사용한다.

## 5.4. 스프링 MVC - 시작하기
`@RequestMapping` : `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter` 를 다룬다.

## 5.5. 스프링 MVC - 컨트롤러 통합
`@RequestMapping`을 활용하면 메소드 레벨과의 조합도 가능하다.

> `@RequestMapping("/springmvc/v2/members")` // 클래스 레벨
`@RequestMapping("/new-form")` // 메소드 레벨
`@RequestMapping("/save")` // 메소드 레벨


## 5.6. 실용적인 방식

### `@RequestParam` 사용
스프링은 HTTP 요청 파라미터를 @RequestParam 으로 받을 수 있다.

`@RequestParam("username")` 은 `request.getParameter("username")` 와 거의 같은 코드라고 생각하면 된다.

GET 쿼리 파라미터, POST Form 방식을 모두 지원한다

### `@RequestMapping` -> `@GetMapping`, `@PostMapping`
`@RequestMapping` 은 URL만 매칭하는 것이 아니라, HTTP Method도 함께 구분할 수 있다.

예를 들어서 URL이 /new-form 이고, HTTP Method가 GET인 경우를 모두 만족하는 매핑을 하려면 다음과 같이 처리하면 된다.
```java
@RequestMapping(value = "/new-form", method = RequestMethod.GET)
```
이것을 `@GetMapping` , `@PostMapping` 으로 더 편리하게 사용할 수 있다.
참고로 Get, Post, Put, Delete, Patch 모두 애노테이션이 준비되어 있다.

<br>
<br>
<br>

# 6. 스프링 MVC - 기본 기능

## 6.1. 생성
- thymeleaf로 생성
- packaging을 jar를 선택하는데, 내장 톰캣에 최적화 할 때 사용한다. 반면, war는 톰캣을 별도로 설치하고 빌드된 파일을 넣을 때, jsp를 넣을때 사용

<br>

## 6.2. 로깅 간단히 알아보기

 스프링 부트 - 로깅 - Logback & SLF4J(인터페이스)
logger 참조는 다음과 같다.
```java
import org.slf4j.Logger;
```
- `@Controller`의 경우 반환 값이 `String`이면 뷰 이름으로 인식되어 뷰를 찾고 뷰가 렌더링 되지만, `@RestController`의 경우 `String`이 바로 HTTP 메시지 바디에 바로 입력돼서 반환이 된다.

- `system.out.println` 대신에 사용한다.

- 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.

- 콘솔, 파일, 네트워크 등 로그를 별도의 위치에 남길 수 있다.

- LEVEL은 TRACE>DEBUG>INFO>WARN>ERROR, 기본은 INFO이다.
조절은 application.properties에서 할 수 있다.

```java
@Slf4j // 로그 사용 방법 1번
@RestController
public class LogTestController {
    //private final Logger log = LoggerFactory.getLogger(getClass()); // 로그 사용 방법 2번

    @RequestMapping("/log-test")
    public String logTest(){
        String name = "Spring";

        log.info("info log = {}", name);
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info(" info log={}", name);
        log.warn(" warn log={}", name);
        log.error("error log={}", name);

        // log.debug("String concat log=" + name); 로그를 사용하지 않아도 계산이 되어 
      	// 과부하가 발생하기 때문에 이런식으로는 사용하지 않는다 
        return "ok";
    }
}
```

<br>

## 6.3. 요청 매핑
요청이 왔을때 어떤 컨트롤러가 매핑이 되는가

`@RequestMapping` 에 method 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 호출된다.

### 6.3.1. HTTP 메소드 매핑
아래의 경우에는 GET이 아니면 에러
```java
@RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
public String mappingGetV1() {
	log.info("mappingGetV1");
 	return "ok";
}
```

### 6.3.2.HTTP 메소드 매핑 축약
```java
@GetMapping(value = "/mapping-get-v2")
public String mappingGetV2() {
	log.info("mapping-get-v2");
	return "ok";
}
```
### 6.3.3. PathVariable(경로 변수) 사용
```java
@GetMapping("/mapping/{userId}")
public String mappingPath(@PathVariable("userId") String data) {
	log.info("mappingPath userId={}", data);
	return "ok";
}
```
### 6.3.4. PathVariable(경로 변수) 다중 사용
```java
@GetMapping("/mapping/users/{userId}/orders/{orderId}")
public String mappingPath(@PathVariable String userId, @PathVariable Long
orderId) {
 log.info("mappingPath userId={}, orderId={}", userId, orderId);
 return "ok";
}
```
### 6.3.5 미디어 타입의 경우
```java
@PostMapping(value = "/mapping-produce", produces = "text/html")
public String mappingProduces() {
 log.info("mappingProduces");
 return "ok";
}
```

## 6.4. HTTP 요청
다양한 파라미터들이 존재하는데, 다음과 같은 역할을 한다.

- `HttpMethod httpMethod` : HTTP 메소드 조회

- `Locale locale` : Locale 정보 조회

- `@RequestHeader MultiValueMap<String, String> headerMap` : 모든 HTTP 헤더를 `multivaluemap` 형식으로 조회
(`MultiValueMap` : 하나의 키에 여러 값을 받을 수 있는 것)

- `@RequestHeader("host") String host`
: 특정 HTTP 헤더를 조회

- `@CookieValue(value = "myCookie", required = false) String cookie` : 특정 쿠키를 조회

<br>


## 6.5. HTTP 요청 파라미터
HTTP 메시지를 통하여 클라이언트에서 서버로 메시지를 전달할 때는 3가지 방법이 있다.

> 1. GET - 쿼리 파라미터
2. POST - HTML 폼
3. HTTP message body에 데이터를 직접 담아서 요청

: GET 쿼리 파리미터 전송 방식이든, POST HTML Form 전송 방식이든 둘다 형식이 같으므로 구분없이조회할 수 있다.
이것을 간단히 **요청 파라미터(request parameter)** 조회라 한다.

### 6.5.1. `request.getParameter()`
: 단순히 `HttpServletRequest`가 제공하는 방식으로 요청 파라미터를 조회가 가능하다.
: 리소스는 `/resources/static` 아래에 두면 스프링 부트가 자동으로 인식한다

### 6.5.2. `@RequestParam`

`@RequestParam("username") String memberName`은  `request.getParameter("username")`와 같다

#### 6.5.3. `@ModelAttribute`

스프링MVC는 `@ModelAttribute` 가 있으면 다음을 실행한다.
- HelloData 객체를 생성한다.
- 요청 파라미터의 이름으로 `HelloData` 객체의 프로퍼티를 찾는다. 그리고 해당 프로퍼티의 `setter`를
호출해서 파라미터의 값을 입력(바인딩) 한다.
- 예) 파라미터 이름이 `username` 이면 `setUsername() `메서드를 찾아서 호출하면서 값을 입력한다.

아래 코드는 `@RequestParam`과  `@ModelAttribute`의 예시이다
```java
@Slf4j
@Controller
public class RequestParamController {

    @RequestMapping("/request-param-v1")
    public void requestParamV1(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        int age = Integer.parseInt(req.getParameter("age"));
        log.info("username ={}, age ={}", username, age);
        resp.getWriter().write("ok");
    }

    // @RequestParam 1번
    @ResponseBody //문자 반환을 위해 restcontroller로 바꾸든지 아니면 이 어노테이션을 쓰면 된다.
    @RequestMapping("/request-param-v2")
    public String requestParamV2(
            @RequestParam("username") String memberName,
            @RequestParam("age") int memberage){
        log.info("username = {}, age ={}", memberName, memberage);
        return "pitchu";
    }

    // @RequestParam 2번
    @ResponseBody
    @RequestMapping("/request-param-v3")
    public String requestParamV3(
            @RequestParam String username, //변수명과 똑같으면 생략이 가능하다.
            @RequestParam  int age){
        log.info("username = {}, age ={}", username, age);
        return "pitchu";
    }

    // @RequestParam 3번
    @ResponseBody
    @RequestMapping("/request-param-v4")
    public String requestParamV4(String username, int age){
        log.info("username = {}, age ={}", username, age); // string int Integer등의 단순 타입이면 @RequestParam도 생략이 가능하다
        return "pitchu";
    }

    // @RequestParam 4번
    @ResponseBody
    @RequestMapping("/request-param-required")
    public String requestParamRequired(
            @RequestParam(required = true) String username, // true면 꼭 들어와야됨
            @RequestParam(required = false) Integer age){ // int에 null 들어갈 수 없고 integer는 들어갈 수 있다.
        log.info("username = {}, age ={}", username, age);
        return "pitchu";
    }

    // @RequestParam 5번
    @ResponseBody
    @RequestMapping("/request-param-default")
    public String requestParamDefault(
            @RequestParam(required = true, defaultValue = "guest") String username, // 파라미터에 값이 없을 경우 defaultValue를 사용하면 기본 값을 적용한다.
            @RequestParam(required = false, defaultValue = "-1") int age) {
        log.info("username={}, age={}", username, age);
        return "ok";
    }
    // @RequestParam 6번
    @ResponseBody
    @RequestMapping("/request-param-map")
    public String requestParamMap(@RequestParam Map<String, Object> paramMap) {
        log.info("username={}, age={}", paramMap.get("username"), // 파라미터를 맵. 멀티 밸류 맵으로도 조회할 수도 있다.
                paramMap.get("age"));
        return "ok";
    }

    // @ModelAttribute 1번  hello data 객체를 생성 helloData 객체의 프로퍼티를 찾아서 setter를 호출 후 파라미터의 값을 바인딩
    @ResponseBody
    @RequestMapping("/model-attribute-v1")
    public String modelAttributeV1(@ModelAttribute HelloData helloData) {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }

    // @ModelAttribute 2번
    @ResponseBody
    @RequestMapping("/model-attribute-v2")
    public String modelAttributeV2(HelloData helloData) { //@ModelAttribute도 생략 가능 ,@RequestParam은 단순 타입,@ModelAttribute는 나머지   
        log.info("username={}, age={}", helloData.getUsername(),
                helloData.getAge());
        return "ok";
    }
}
```

<br>


## 6.6. HTTP 요청 메시지

### 6.6.1. 단순 텍스트
```java
public class RequestBodyStringController {
    // HTTP 요청 메시지 - 단순 텍스트 1번
    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        response.getWriter().write("ok");
    }


    // HTTP 요청 메시지 - 단순 텍스트 2번
    /**
     * InputStream(Reader): HTTP 요청 메시지 바디의 내용을 직접 조회
     * OutputStream(Writer): HTTP 응답 메시지의 바디에 직접 결과 출력
     */
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(InputStream inputStream, Writer responseWriter) throws IOException {
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        responseWriter.write("ok");
    }


    // HTTP 요청 메시지 - 단순 텍스트 3번 
    /**
     * HttpEntity: HTTP header, body 정보를 편라하게 조회
     * - 메시지 바디 정보를 직접 조회(@RequestParam X, @ModelAttribute X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     *
     * 응답에서도 HttpEntity 사용 가능
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(HttpEntity<String> httpEntity) {
        String messageBody = httpEntity.getBody();
        log.info("messageBody={}", messageBody);
        return new HttpEntity<>("ok");
    }
    
    // HTTP 요청 메시지 - 단순 텍스트 4번 : 제일 많이 쓰임
    @ResponseBody// 응답 결과를 바디에 담아 직접 전달 
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String messageBody) { // 편하게 HTTP 메시지 바디 정보 조회 가능 
        log.info("messageBody={}", messageBody);
        return "ok";
    }
}
```

<br>

### 6.6.2. JSON
JSON의 경우 HTTP 요청시에 content-type이 application/json인지 확인해야 한다.
```java
private ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/request-body-json-v1")
    public void requestBodyJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); // message body를 받아서
        log.info("messageBody={}", messageBody);
        HelloData data = objectMapper.readValue(messageBody, HelloData.class);// object Mapper를 사용하여 객체로 변환한다.
        log.info("username={}, age={}", data.getUsername(), data.getAge());
        response.getWriter().write("ok");

    }
    // @RequestBody를 이용해서 message body를 받고,
    // 객체 또한 objectMapper 를 쓰지 않고 변경이 가능하다.
    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodyJsonV3(@RequestBody HelloData helloData) {

        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());
        return "ok";
    }
```

<br>

## 6.7. 응답
응답데이터를 만드는 방법은 아래의 세 가지 이다.
> 1. 정적 리소스
2. 뷰 템플릿 : 동적인 HTML
3. HTTP 메시지 사용

### 1. 정적 리소스
`src/main/resources/static` 디렉토리에 넣는다.
### 2. 뷰 템플릿
`src/main/resources/templates` 디렉토리에 넣는다.

<br>

## 6.8. HTTP 메세지 컨버터

스프링 MVC는 다음의 경우에 HTTP 메시지 컨버터를 적용한다.
- HTTP 요청 : `@RequestBody` , `HttpEntity(RequestEntity)`
- HTTP 응답: `@ResponseBody` , `HttpEntity(ResponseEntity)`


#### 스프링 부트 기본 메시지 컨버터 (일부 생략)

```java
0 = ByteArrayHttpMessageConverter
1 = StringHttpMessageConverter 
2 = MappingJackson2HttpMessageConverter
```

스프링 부트는 다양한 메시지 컨버터를 제공하는데, 대상 클래스 타입과 미디어 타입 둘을 체크해서 사용여부를 결정한다. 만약 만족하지 않으면 다음 메시지 컨버터로 우선순위가 넘어간다.

이중 하나로 작동 예시를 들자면, Jackson2 타입의 경우에는 조건이 아래와 같다. 위에서 부터 탐색하면서

클래스 타입 : 객체 또는 HashMap
미디어타입: application/json 관련
요청 ex) @RequestBody HelloData Data
// canread를 통해 조건 충족하는가? read를 통하여 객체 생성하고 반환한다.

응답 ex) @ResponseBody return helloData
쓰기 미디어 타입 : application/json 관련
// canwrite를 통해 조건 충족하는가?
// 만족하면 write이용하여 메시지 바디에 데이터 생성

## 6.9. 요청 매핑 핸들러 어댑터 구조
![](https://velog.velcdn.com/images/dodo4723/post/e71f85a6-235a-46cb-861c-901025c6b086/image.png)
`ArgumentResolver` 때문에 다양한 파라미터 처리가능 `ReturnValueHandler`는 응답값을 반환하고 처리한다. 위의 두가지를 처리하는데에 앞서 배운 HTTP메시지 컨버터가 사용된다.

<br>
<br>

# 7. 스프링 MVC - 웹 페이지 만들기
## 7.1. 요구사항 분석

### 7.1.1. 상품 도메인 모델
- 상품 ID
- 상품명
- 가격
- 수량

### 7.1.2. 상품 관리 기능
- 상품 목록
- 상품 상세
- 상품 등록
- 상품 수정

![](https://velog.velcdn.com/images/dodo4723/post/22b040eb-8657-47b5-aeaa-98680461a702/image.png)
**디자이너**: 요구사항에 맞도록 디자인하고, 디자인 결과물을 웹 퍼블리셔에게 넘겨준다.

**웹 퍼블리셔**: 다자이너에서 받은 디자인을 기반으로 HTML, CSS를 만들어 개발자에게 제공한다.

**백엔드 개발자**: 디자이너, 웹 퍼블리셔를 통해서 HTML 화면이 나오기 전까지 시스템을 설계하고, 핵심 비즈니스 모델을 개발한다. 이후 HTML이 나오면 이 HTML을 뷰 템플릿으로 변환해서 동적으로 화면을 그리고, 또 웹 화면의 흐름을 제어한다.

<br>
<br>

## 7.2. 상품 도메인 개발
### 7.2.1 도메인
```java
@Data //되도록이면  @Getter @ Setter를 사용해라 @Data의 경우 도메인 모델에 사용하기에는 변수가 많아(포함된 어노테이션이 많아) 위험하다.
public class Item {
    private Long id;
    private String itemName;
    private Integer price; // NULL 값도 가정을 한다.
    private Integer quantity; // NULL 값도 가정을 한다.

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```
### 7.2.2 저장소
```java
@Repository
public class ItemRepository {
    private static final Map<Long, Item> store = new HashMap<>(); 
    // static 여러개가 동시에 접근하는 경우 Hashmap 쓰면 안된다.
    private static long sequence = 0L; 
    // 이것또한 automic long 등 사용하는게 낫다.
    // 다만 작은 프로젝트니 그냥 사용하였다. 
    
    public Item save(Item item){ // item 저장하기
        item.setId(+sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id){
        return store.get(id);
    }

    public List<Item> findAll(){
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item updateParam){
        // 아이템과 관련된 파라미터를 넣으면 업데이트가 된다.
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());// updateParam은 별도의 객체를 만드는게 맞음
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore(){
        store.clear();
    }
}
```

<br>
<br>

## 7.3. 상품 서비스 HTML
정적 리소스가 공개되는 `/resources/static` 폴더에 HTML을 넣어두면, 실제 서비스에서도 공개된다. 서비스를 운영한다면 공개할 필요없는 HTML을 두는 것은 주의해야한다.

<br>
<br>

## 7.4. 상품 목록 - 타임리프
### 7.4.1. 먼저 컨트롤러를 만든다.
```java
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;

    //@Autowired // 1. 생성자 하나 있으면 @Autowired는 생략 가능
    //public BasicItemController(ItemRepository itemRepository) {
    //    this.itemRepository = itemRepository; // 2. lombok 의 @RequiredArgsConstructor 사용하면 final 붙은거는 생략가능 
    //} 
}
```

<br>

### 7.4.2. thymeleaf 사용
#### 7.4.2.1. 선언
```html
<html xmlns:th="http://www.thymeleaf.org">
```

#### 7.4.2.2. 속성
그후 반환하는 view를 만드는데, 앞서 넣었던 HTML파일들을 타임리프를 사용해서 동적으로 바꿔야 한다.

thymeleaf는 그대로 볼때는 `href`, 뷰 템플릿을 거치면 `th:href`의 값이 `href`로 대치되면서 동적으로 변경한다.

#### 7.4.2.3. 핵심
핵심은 `th:xxx` 가 붙은 부분은 서버사이드에서 렌더링 되고, 기존 것을 대체한다. `th:xxx` 이 없으면 기존 html의 `xxx` 속성이 그대로 사용된다.
HTML을 파일 보기를 유지하면서 템플릿 기능도 할 수 있다.

#### 7.4.2.4. 리터럴 대체 '|...|'
타임리프에서 문자와 표현식 등은 분리되어 있기 때문에 더해서 사용해야 한다.
```html
<span th:text="'Welcome to our application, ' + ${user.name} + '!'">
```
다음과 같이 리터럴 대체 문법을 사용하면, 더하기 없이 편리하게 사용할 수 있다.
```html
<span th:text="|Welcome to our application, ${user.name}!|">
```

#### 7.4.2.5. 반복
반복은 `th:each` 를 사용한다. 이렇게 하면 모델에 포함된 `items` 컬렉션 데이터가 `item` 변수에 하나씩 포함되고, 반복문 안에서 `item` 변수를 사용할 수 있다
```
<tr th:each="item : ${items}">
```

#### 7.4.2.6. 변수 표현식
```
<td th:text="${item.price}">10000</td>
```
모델에 포함된 값이나 타임리프 변수로 선언한 값을 조회할 수 있다.
프로퍼티 접근법을 사용한다. `(item.getPrice())`

#### 7.4.2.7. 내용 변경
내용의 값을 `th:text` 값으로 변경

#### 7.4.2.8. URL 링크 표현식 2
`th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`

URL 링크 표현식을 사용하면 경로를 템플릿처럼 편리하게 사용할 수 있다.

경로 변수( {itemId} ) 뿐만 아니라 쿼리 파라미터도 생성한다.

예) `th:href="@{/basic/items/{itemId}(itemId=${item.id}, query='test')}"`
생성 링크: `http://localhost:8080/basic/items/1?query=test`

#### 7.4.2.9. URL 링크 간단히
`th:href="@{|/basic/items/${item.id}|}"`
리터럴 대체 문법을 활용해서 간단히 사용할 수도 있다

#### 7.4.3. 내츄럴 템플릿
타임리프는 순수 HTML 파일을 웹 브라우저에서 열어도 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있다. JSP를 생각해보면, JSP 파일은 웹 브라우저에서 그냥 열면 JSP 소스코드와 HTML이 뒤죽박죽 되어서 정상적인 확인이 불가능하다. 오직 서버를 통해서 JSP를 열어야 한다.

순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 **네츄럴 템플릿 (natural templates)**이라 한다.

<br>

## 7.5. 상품 등록 폼

1. 컨트롤러 내에서는 같은 URL이더라도  HTTP 메소드로 기능을 구분한다. (등록 폼과 등록 처리를 깔끔하게) 

```java
@GetMapping("/add")
public String addFrom() {
	return "basic/addForm";
}

@PostMapping("/add")
public String save() {
    return "basic/addForm";
}
```

<br>

## 7.6. 상품 등록 처리 - @ModelAttribute

메시지 바디에 쿼리 파라미터 형식으로 전달하였다 이를 처리하기 위해 `@RequestParam` 사용한다.

상품 등록 처리를 위해서는 두가지 방법이 있는데

### 7.6.1. @RequestParam을 사용하는 방법
```java
@PostMapping("/add")
    public String additemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);
        return "basic/item";
    }
```

### 7.6.2. @ModelAttribute를 사용하는 방법
```java
@PostMapping()//"/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {
        //ModelAttribute 가 자동으로 객체 만들고 set을 호출하기 떄문에 4문장 제거 가능
        itemRepository.save(item);
        // ModelAttribute의 내용으로 "item" 담김
        //model.addAttribute("item", item);
        return "basic/item";
    }
```
`@ModelAttribute` 의 이름을 생략하면 모델에 저장될 때 클래스명을 사용한다. 이때 클래스의 첫글자만 소문자로 변경해서 등록한다.

<br>

## 7.7. PRG Post/Redirect/Get 
사실 지금까지 진행한 상품 등록 처리 컨트롤러는 심각한 문제가 있다.

상품 등록을 완료하고 웹 브라우저의 새로고침 버튼을 클릭해보면 상품이 계속해서 중복 등록되는 것을 확인할 수 있다.

![](https://velog.velcdn.com/images/dodo4723/post/4e5afa12-03a4-4cfa-9897-c0151b200e69/image.png)

웹 브라우저의 새로 고침은 마지막에 서버에 전송한 데이터를 다시 전송한다.

새로 고침 문제를 해결하려면 상품 저장 후에 뷰 템플릿으로 이동하는 것이 아니라, 상품 상세 화면으로 리다이렉트를 호출해주면 된다.

웹 브라우저는 리다이렉트의 영향으로 상품 저장 후에 실제 상품 상세 화면으로 다시 이동한다. 따라서 마지막에 호출한 내용이 상품 상세 화면인 `GET /items/{id}` 가 되는 것이다.

이후 새로고침을 해도 상품 상세 화면으로 이동하게 되므로 새로 고침 문제를 해결할 수 있다.

<br>

## 7.8. RedirectAttributes
`RedirectAttributes` 를 사용하면 URL 인코딩도 해주고, `pathVarible` , 쿼리 파라미터까지 처리해준다.

다음과 같은 예문이 있다고 가정하자.
```java
@PostMapping("/add")
public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
 Item savedItem = itemRepository.save(item);
 redirectAttributes.addAttribute("itemId", savedItem.getId());
 redirectAttributes.addAttribute("status", true);
 return "redirect:/basic/items/{itemId}";
}
```

`redirectAttributes.addAttribute`로 넣었던 값중에서 return에 사용된 {}안의 값이 있을 경우, 해당 값으로 넣어주고 return에 사용되지 않은 나머지 값들은 리다이렉트 될 때 쿼리파라미터로 넘겨준다.

`"/basic/items/itemid?status=true"`가 반환된다.

<br>

### th:if

```
<h2 th:if="${param.status}" th:text="'저장 완료!'"></h2>
```
`th:if` : 해당 조건이 참이면 실행
`${param.status}` : 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능

원래는 컨트롤러에서 모델에 직접 담고 값을 꺼내야 한다. 그런데 쿼리 파라미터는 자주 사용해서 타임리프에서 직접 지원한다.

## 7.9. 결과물

![](https://velog.velcdn.com/images/dodo4723/post/1ed5e63d-e3e6-4fbf-bf45-9e0e46d24c46/image.png)

![](https://velog.velcdn.com/images/dodo4723/post/02946280-0dbf-419b-a2b0-5a18305bf68b/image.png)

![](https://velog.velcdn.com/images/dodo4723/post/6ad549ae-317c-46e7-ab2b-6765f07b242a/image.png)

![](https://velog.velcdn.com/images/dodo4723/post/eceae57d-c26a-4c8a-b89a-4880f1e95b64/image.png)

# 8. 마치며

처음엔 3주는 걸릴거라고 생각했는데 매일 꾸준히 강의를 들으니 2주만에 완강할 수 있었다. 

이 강의를 들으면서 HTTP에 대한 이해가 더 확고해진 것 같다. 이제 평소에 인터넷 웹페이지에 접속할 때에도 '아 이건 이렇게 요청과 응답을 주고받는구나'라고 눈길이 간다.

하지만, 아직 혼자 간단한 웹서버를 만들어보라고 하면 못할 것 같다. 다음 강의인 스프링 MVC 2편을 들어야겠다. 2편은 1편보다 강의내용이 1.5배정도 더 많아서 시간이 꽤 걸릴 것 같다. 복학까지 1달남은 지금 MVC 2편까지 듣고 복학하게 될 것 같다.