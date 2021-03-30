# testRS485Communication

실행전 준비사항.

1. https://www.oracle.com/kr/java/technologies/javase-jdk11-downloads.html 에서 jdk11을 설치.
2. https://fazecast.github.io/jSerialComm/ 에서 jSerialComm 라이브러리 설치.
3. https://gluonhq.com/products/javafx/ 에서 javaFX 라이브러리 설치.
4. jackson 라이브러리 설치.

실행방법.

actest1.jar파일을 다운로드 받고 windows의 명령 프롬프트 창에서 jar파일이 설치된 경로로 이동한다.

그 다음
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar actest1.jar
(%PATH_TO_FX%는 javaFX의 lib 경로임.)
코드를 실행시킨다.

kbm 내용수정

추가 20210330
추가 20210330

