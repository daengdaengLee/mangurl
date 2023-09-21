# mangurl

## TL;DR

- URL 단축 서비스입니다.
- [여기](https://mangurl.net)에서 사용할 수 있습니다.

## API

[Swagger 문서](https://mangurl.net/api/swagger-ui/index.html)로 API 사용법을 제공하고 있습니다.

### 단축 URL 사용

- 브라우저 주소창에 생성한 단축 URL 주소를 입력해주세요. 또는 가장 편한 방법으로 단축 URL로 HTTP 요청을 보내주세요.
- 단축 URL에 대한 응답 코드는 `302 Found` 입니다. 원본 URL은 `Location` 헤더에서 확인할 수 있습니다.
- 등록되지 않은 단축 URL에 대해서는 `404 Not Found` 코드를 응답합니다.

## 개발 환경 구축 및 실행

- JDK 17 이 필요합니다. [이곳](https://adoptium.net/temurin/releases/?version=17)에서 다운받아 설치하거나 편한 방법으로 설치해 주세요.
- DynamoDB에 연결하거나 도커 이미지를 빌드하기 위해 도커가 필요합니다. [가이드](https://docs.docker.com/engine/install/)에서 자기 환경에 맞는 방법에 따라 설치해 주세요.
- Gradle Wrapper를 사용하기 때문에 Gradle 을 직접 설치할 필요가 없습니다. 프로젝트 루트 경로에서 아래 명령어를 사용할 수 있습니다.
    - 실행: `./gradlew bootRun`
    - 테스트: `./gradlew test`
    - 빌드: `./gradlew bootJar` (이후 `java -jar ./build/libs/mangurl-<version>.jar` 명령어로 실행할 수 있습니다.)
    - 도커 이미지 빌드: `./gradlew bootBuildImage`
- Spring Profile 설정
    - 지원하는 Profile
        - `local`: 로컬에서 개발할 때 사용합니다. `http://localhost:8000` 에 로컬 DynamoDB가 실행 중이어야 합니다.
          `docker/local/compose.yaml` 로 실행할 수 있습니다.
        - `local-container`: 빌드한 도커 이미지를 로컬에서 실행할 때 사용합니다. `docker/local-container/compose.yaml` 로 실행할 수 있습니다.
        - `test`: 테스트할 때 사용합니다.
        - `dev`: 개발용 DynamoDB에 연결할 때 사용합니다. AWS IAM Credentials 가 필요합니다. 담당자에게 문의하세요.
        - `prod`: 프로덕션 환경에서 사용합니다. 해당 Profile 파일은 접근이 제한된 S3에 보관합니다.
    - `src/main/resources/application.yml` 의 `spring.profiles.active` 항목을 원하는 profile로 변경합니다.
    - 실행할 때 `SPRING_PROFILES_ACTIVE` 환경변수에 원하는 profile을 설정합니다.
- 로컬 DynamoDB
    - DynamoDB는 도커를 사용해 로컬에서 실행할 수 있습니다.
    - 실행: `cd docker/<profile> && docker compose up`
    - 실행: `cd docker/<profile> && docker compose down`
