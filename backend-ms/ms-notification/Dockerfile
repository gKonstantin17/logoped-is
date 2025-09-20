# FROM gradle:8.4.0-jdk21
FROM logoped-be-deps

# все скачанные ранее файлы будут браться из папки кеша родительского образа
ENV GRADLE_USER_HOME /home/gradle/cache
WORKDIR /app/ms-notification
COPY src ./src
COPY build.gradle settings.gradle ./

# формируем jar файл из исходного кода
# печать логов в консоль тоже занимает время, поэтому когда убедились что все ок - убираем параметр stacktrace
RUN gradle clean bootJar -i --stacktrace

# открытие порта для внешних вызовов и запуск проекта
EXPOSE 8765
ENTRYPOINT ["java", "-jar", "/app/ms-notification/build/libs/ms-notification-0.0.1-SNAPSHOT.jar"]