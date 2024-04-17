# 1. 사용할 base 이미지 지정
FROM openjdk:11-jre-slim

# 2. 애플리케이션 파일을 Docker 이미지로 복사
COPY .mvn/wrapper/maven-wrapper.jar app.jar

# 3. 컨테이너가 시작될 때 실행할 명령을 지정
CMD ["java", "-jar", "app.jar"]
