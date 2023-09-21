# mangurl

## TL;DR

- URL 단축 서비스입니다.
- [여기](https://mangurl.net)에서 사용할 수 있습니다.

## API

[Swagger 문서](https://mangurl.net/api/swagger-ui/index.html)로 API 사용법을 제공하고 있습니다.

### 단축 URL 사용

- 생성한 단축 URL을 사용하려는 경우, 브라우저 주소창에 생성한 단축 URL 주소를 입력해주세요.
  또는 가장 편한 방법으로 단축 URL로 HTTP 요청을 보내주세요.
- 단축 URL에 대한 응답 코드는 `302 Found` 입니다. 원본 URL은 `Location` 헤더에서 확일할 수 있습니다.
- 등록되지 않은 단축 URL에 대해서는 `404 Not Found` 코드를 응답합니다.
