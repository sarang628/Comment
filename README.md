
## submodule 확인
comment 프로젝트는 2가지 submodule을 사용
- comment_di
  - UILayer와 DataLayer을 연결해주는 DomainLayer역할  
  - cd ./src/main/java/com/sarang/torang/di 
  - git submodule add https://github.com/sarang628/comment_di.git
- repository
  - TorangRepository 모듈 사용시 필요
  - TorangRepository는 interface만 정의되어있고 실제 구현은 분리되어있음
  - cd ./src/main/java/com/sarang/torang/di
  - git submodule add https://github.com/sarang628/repository