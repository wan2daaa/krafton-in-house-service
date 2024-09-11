# 기능 정리 

**기능 수정하기**
- 회원가입 (이메일, 이름, 비밀번호) 
- 로그인 (이메일, 비밀번호) (30분후 만료 세션 관리)
- 회의실 예약
- 게시판/자료실, 커뮤니티
- 메일,
- 결재 (?), 
- 설문투표
- 통합회계시스템, Web-Fax, 일정관리, Clean Desk 점검, 임직원몰 ID연동, 이름검색
- 인사관리, 조직관리, 역량관리, 성과관리, 급여관리, 근태관리, 복리후생, 신청결재 등
- 로컬 AD랑 Azure AD 두가지로 관리하자 


## 만들면 좋을 기능 
- 크래프톤 사내 카페 미리주문 기능
- 크래프톤 사내 식당 메뉴 조회 기능 및 현재 어느정도 붐비는지확인하는 기능(AI)?
- 크래프톤 사내 택배 조회 기능 (?)


## 결정해야하는 부분 
1. 인증을 JWT로 할까 Session으로할까
  - Session으로 올바르지 않은 요청을 차단하게
  - jwt도 되지만, 계속 인증을 하고 하면 안좋을 것 같다 
2. 내부 IP인지 아닌지 어떻게 구분할까
3. 역할마다 다양한 권한이 분리될 텐데 새로 추가되거나 그런 경우에는 어떤식으로 수정?
4. 푸쉬알림을 어떤식으로 구현할까? (SSE, Polling, Socket, Web Push)
5. 인사정보에 포함된 부서, 직급은 enum으로? 아님 DB로?

## 조건 
- 크래프톤 임직원 약 1700명
- 전자 결재는 어떤식으로 활용될까?
- 사용자의 정보는 모두 Azure Active Directory에서 저장하고 관리 , (인증, 인가포함)
  [spring Azure AD 연동](https://velog.io/@ddclub12/Azure-Active-Directory-OAuth2.0-%EC%9D%B8%EC%A6%9D-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0with-Spring)
  - 그럼 인증 정보는 JWT? 세션? 어떤걸로 할까?
- SSO는? 


## 기술

[azure-graph](https://learn.microsoft.com/ko-kr/azure/developer/java/identity/enable-spring-boot-webapp-authorization-entra-id?tabs=asa)
로 인사정보연계시스템에 저장후, Azure Active Directory에 저장하기


AD와 애저 AD 연결하기

이와 같은 이유를 포함한 여러 이유로, 조직은 하이브리드 IT 환경을 두고 온프레미스 AD의 데이터를 애저 AD에 동기화하는 방법을 택한다. 

마이크로소프트는 이를 위한 두 가지 툴로, Azure AD Connect와 Azure AD Connect cloud sync를 제공

- 하지만, AD를 맥북에 설치 할 수 없어서 AD를 저장하는 식으로 일단 구현해보자

[install utm for install windows server](https://tcsfiles.blob.core.windows.net/documents/AIST3720Notes/InstallUTMonanM1Mac.html)

## AD (Active Directory)란? 
[AD 참고글](https://blog.naver.com/quest_kor/221487945625)

- 기본적으로 AD는 **사용자**가 **마이크로소프트 IT 환경에서 업무를 수행하는 데 도움을 주는 데이터베이스이자 서비스 집합**입니다.


-  데이터베이스(또는 디렉토리)는 환경에 대한 중요한 정보를 담고 있습니다. 
  - 사용자와 컴퓨터 목록, 누가 무엇을 할 수 있는지에 대한 정보 등이 포함. 
    - 예를 들어, 데이터베이스에는 100명의 사용자 계정을 각 사용자의 직책, 전화번호, 비밀번호와 같은 세부정보와 함께 리스팅할 수 있습니다. 
  - 또한, 각 사용자의 권한도 기록합니다. 
    - 예를 들어, 모든 사용자가 회사 복지 정보를 읽도록 허용하고, 금융 문서는 소수의 사람들만 보거나 수정하도록 허용할 수 있습니다.


- 서비스는 IT 환경에서 일어나는 **대부분의 활동을 제어**합니다. 
  - 특히 서비스는 일반적으로 사용자가 입력하는 사용자 ID와 비밀번호를 확인하는 방법으로, 
  - 사용자가 주장하는 본인이 맞는지 검증하고(인증), 각기 허용된 데이터에만 액세스할 수 있도록 합니다(인가).

### AD는 어떤 구조로 구성?
AD에는 크게 
- 도메인(Domain), 
- 트리(Trees), 
- 포레스트(Forests)

의 세 가지 계층이 있습니다. 

- **도메인**은 관련된 사용자, 컴퓨터 및 기타 AD 객체(예를 들어, 회사의 시카고 지사를 위한 모든 AD 객체)로 구성되는 그룹입니다. 
  - **여러 개의 도메인을 트리로 결합**할 수 있으며, **여러 개의 트리를 포레스트로 그룹화**할 수 있습니다. 
여기서 중요한 점은 다음과 같습니다.
- **도메인은 관리 경계**입니다. -> **특정 도메인을 위한 객체는 하나의 데이터베이스에 저장되며 함께 관리**가 가능합니다.
-  **포레스트는 보안 경계**입니다. -> 서로 다른 포레스트의 객체는 각 포레스트의 관리자가 해당 객체간 신뢰를 형성하지 않는 한 상호 작용할 수 없습니다. 
  - 예를 들어, 상호 독립된 여러 개의 사업부가 있는 경우 여러 개의 포레스트를 만드는 것이 좋습니다.


### AD 데이터베이스 안에는 무엇이 있습니까?

- AD 데이터베이스(디렉토리)에는 **도메인의 AD 객체(AD objects)에 대한 정보가 포함**됩니다. 
- 보편적인 AD 객체 유형으로는 
  - 사용자, 
  - 컴퓨터, 
  - 애플리케이션, 
  - 프린터, 
  - 공유 폴더 등이 있습니다. 
- 일부 객체는 다른 객체를 포함할 수 있습니다(AD를 ‘계층적’이라고 말하는 이유). 
- 특히, 앞으로 이어질 글에서는 조직이 AD 객체를 조직 단위(organizational units, OU)로 구성해서 관리를 간소화하고 사용자를 그룹으로 묶어 보안 능률을 높이는 방법을 살펴볼 것입니다. 
- 이러한 OU와 그룹은 그 자체로 디렉토리에 저장되는 객체입니다.

- 객체에는 특성(attributes)이 있습니다. 
- 명확하게 드러나는 특성도 있고, 잘 드러나지 않는 특성도 있습니다. 
  - 예를 들어, 사용자 객체는 일반적으로 그 사람의 **이름, 비밀번호, 부서, 이메일 주소**와 같은 특성 외에 
  - 고유한 전역 고유 식별자(Gobally Unique Identifier, GUID)와 보안 식별자(Security Identifier, SID), 마지막 로그온 시간, 그룹 멤버십과 같은 특성도 포함합니다.


----
## 자료 
Azure Active Directory는 이제 Microsoft Entra ID라고 함
[크래프톤 사내 커뮤니케이션](https://www.1conomynews.co.kr/news/articleView.html?idxno=24258)

[AD azure 와 Spring 연동](https://learn.microsoft.com/ko-kr/azure/developer/java/spring-framework/configure-spring-boot-starter-java-app-with-azure-active-directory-b2c-oidc)


[슬랙도 활용중](https://www.1conomynews.co.kr/news/articleView.html?idxno=24258)

[신작 제안 시스템](https://www.1conomynews.co.kr/news/articleView.html?idxno=24258)

[크래프톤 기존 부서](https://krafton.com/%EB%B6%84%EB%A5%98%EB%90%98%EC%A7%80-%EC%95%8A%EC%9D%8C/1048-2/)
[SPRING SSO](https://kimseungjae.tistory.com/15)


[AD와 Azure AD를 분리한 이유](https://toad.co.kr/it/?idx=6765744&bmode=view)
[https://octatco.com/blog=66](https://octatco.com/blog=66)
- 로컬 AD 와 Azure AD 동기화 작업 방법 :Azure AD Connect와 Azure AD Connect cloud sync

[LDAP , AD 관계](https://sunrise-min.tistory.com/entry/%ED%95%9C-%EB%B2%88%EC%AF%A4%EC%9D%80-%EB%93%A4%EC%96%B4%EB%B4%A4%EC%9D%84-AD-LDAP%EC%97%90-%EB%8C%80%ED%95%9C-%EC%A0%95%EB%A6%AC)

