FROM gradle:8.13.0-jdk17

ENV GRADLE_USER_HOME /home/gradle/cache

# entity классы
WORKDIR /app/lib-entities
COPY lib-entities/build.gradle ./
COPY lib-entities/settings.gradle ./
COPY lib-entities/src ./src
RUN gradle publishToMavenLocal -i --stacktrace


# утилиты
WORKDIR /app/lib-utils
COPY lib-utils/build.gradle ./
COPY lib-utils/settings.gradle ./
COPY lib-utils/src ./src
RUN gradle publishToMavenLocal -i --stacktrace

# зависимости для ВСЕХ проектов: микросервисы, gateway, eureka и пр. (ВСЕ jar файлы), чтобы дочерние образы не качали их заново, а брали отсюда (из кеша)
WORKDIR /app/logoped-be-deps
# в этот файл build.gradle нужно добавить dependencies из всех проектов (т.е. собираем все зависимости "в одну кучу")
COPY docker/mount/logoped-be-deps/build.gradle ./
COPY docker/mount/logoped-be-deps/settings.gradle ./
COPY docker/mount/logoped-be-deps/src ./src
RUN gradle bootJar -i --stacktrace

# Можно запускать контейнер образа, чтобы проверять наличие нужных файлов (для удобства - вкладка files в docker desktop)
# Но для работы дочерних образов runtime контейнер не нужен, он только для проверки
CMD ["tail", "-f"]