# Публикация JavaTextDiff в Maven Central

## Настройки проекта

### GPG ключ
- **Key ID:** `56400C4C`
- **Полный отпечаток:** `CDA2321E84D09ECB28E6E624113CE91D56400C4C`
- **Passphrase:** `unidiffstatic2026`
- **Email:** `alemoroz@gmail.com`
- **Имя:** `Alec Moril`
- **Домашняя директория GPG:** `C:\Users\alec\AppData\Roaming\gnupg`

### GPG agent конфигурация
Файл: `C:\Users\alec\AppData\Roaming\gnupg\gpg-agent.conf`
```
allow-loopback-pinentry
allow-preset-passphrase
```

После изменения — перезапуск:
```bash
gpgconf --kill gpg-agent && gpgconf --launch gpg-agent
```

### Отправка публичного ключа на сервер
```bash
gpg --keyserver keyserver.ubuntu.com --send-keys 56400C4C
```

### Sonatype Central Portal
- **URL:** https://central.sonatype.com/
- **Namespace / Group ID:** `io.github.moriline` (Verified)
- **Username:** `5rje26`
- **Password / Token:** `BGF0JpzThXdYNxbE4LaFXeqTrUCbQfRXG`

### Maven координаты
| Поле | Значение |
|------|----------|
| **Group ID** | `io.github.moriline` |
| **Artifact ID** | `JavaTextDiff` |
| **Version** | `1.0.0` |

---

## Пошаговая инструкция

### Шаг 1: Подготовка `gradle.properties`

Файл: `gradle.properties` (НЕ коммитить в git — добавлен в `.gitignore`)

```properties
# GPG signing
signing.keyId=56400C4C
signing.password=unidiffstatic2026
signing.gnupg.executable=gpg
signing.gnupg.homeDir=C:\\Users\\alec\\AppData\\Roaming\\gnupg

# Sonatype credentials
sonatypeUsername=5rje26
sonatypePassword=BGF0JpzThXdYNxbE4LaFXeqTrUCbQfRXG

# JVM args
org.gradle.jvmargs=-Dfile.encoding=UTF-8
```

### Шаг 2: Генерация и подписание артефактов

```bash
# Установить GPG в PATH
set "PATH=C:\Program Files\GnuPG\bin;%PATH%"

# Запустить подписание и публикацию локально
gradlew.bat signMavenJavaPublication publishToMavenLocal
```

При запросе пароля GPG ввести: `unidiffstatic2026`

Артефакты появятся в:
```
C:\Users\alec\.m2\repository\io\github\moriline\JavaTextDiff\1.0.0\
```

### Шаг 3: Создание бандла с правильной структурой

```bash
# Создать директорию с правильной структурой Maven
mkdir build\publish-bundle\io\github\moriline\JavaTextDiff\1.0.0

# Скопировать артефакты
xcopy C:\Users\alec\.m2\repository\io\github\moriline\JavaTextDiff\1.0.0\*.* ^
      build\publish-bundle\io\github\moriline\JavaTextDiff\1.0.0\ /Y

# Сгенерировать MD5 и SHA1 контрольные суммы
powershell -Command "Get-ChildItem 'build\publish-bundle\io\github\moriline\JavaTextDiff\1.0.0' | ForEach-Object { $content = [System.IO.File]::ReadAllBytes($_.FullName); $md5 = [System.BitConverter]::ToString([System.Security.Cryptography.MD5]::Create().ComputeHash($content)).Replace('-','').ToLower(); $sha1 = [System.BitConverter]::ToString([System.Security.Cryptography.SHA1]::Create().ComputeHash($content)).Replace('-','').ToLower(); [System.IO.File]::WriteAllText($_.FullName + '.md5', $md5); [System.IO.File]::WriteAllText($_.FullName + '.sha1', $sha1) }"

# Создать ZIP архив
powershell -Command "Compress-Archive -Path 'build\publish-bundle\io' -DestinationPath 'build\JavaTextDiff-1.0.0-bundle.zip' -Force"
```

### Шаг 4: Загрузка на Central Portal

1. Открыть **https://central.sonatype.com/**
2. Войти через GitHub/SSO
3. Перейти на страницу **Publishing** → **Upload**
4. **Deployment Name:** `JavaTextDiff-1.0.0`
5. Загрузить файл: `build\JavaTextDiff-1.0.0-bundle.zip`

### Шаг 5: Валидация

Дождаться статуса **VALIDATED** (обычно 1–3 минуты).

Если валидация **не прошла** — проверить:
- Все файлы имеют `.md5` и `.sha1`
- Структура ZIP: `io/github/moriline/JavaTextDiff/1.0.0/<файлы>`
- Подписи `.asc` корректны

### Шаг 6: Публикация

1. На странице деплоя нажать **Publish** (или **Deploy**)
2. Дождаться статуса **PUBLISHED** (15–30 минут)
3. Проверить доступность:

```
https://repo1.maven.org/maven2/io/github/moriline/JavaTextDiff/1.0.0/
```

---

## Структура ZIP бандла

```
io/
└── github/
    └── moriline/
        └── JavaTextDiff/
            └── 1.0.0/
                ├── JavaTextDiff-1.0.0.jar
                ├── JavaTextDiff-1.0.0.jar.asc
                ├── JavaTextDiff-1.0.0.jar.md5
                ├── JavaTextDiff-1.0.0.jar.sha1
                ├── JavaTextDiff-1.0.0-sources.jar
                ├── JavaTextDiff-1.0.0-sources.jar.asc
                ├── JavaTextDiff-1.0.0-sources.jar.md5
                ├── JavaTextDiff-1.0.0-sources.jar.sha1
                ├── JavaTextDiff-1.0.0-javadoc.jar
                ├── JavaTextDiff-1.0.0-javadoc.jar.asc
                ├── JavaTextDiff-1.0.0-javadoc.jar.md5
                ├── JavaTextDiff-1.0.0-javadoc.jar.sha1
                ├── JavaTextDiff-1.0.0.pom
                ├── JavaTextDiff-1.0.0.pom.asc
                ├── JavaTextDiff-1.0.0.pom.md5
                ├── JavaTextDiff-1.0.0.pom.sha1
                ├── JavaTextDiff-1.0.0.module
                ├── JavaTextDiff-1.0.0.module.asc
                ├── JavaTextDiff-1.0.0.module.md5
                └── JavaTextDiff-1.0.0.module.sha1
```

---

## Использование опубликованной библиотеки

### Gradle (Kotlin DSL)
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.moriline:JavaTextDiff:1.0.0")
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.moriline</groupId>
    <artifactId>JavaTextDiff</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Публикация новой версии

1. Обновить `version = "1.0.1"` в `build.gradle.kts`
2. Выполнить **Шаг 2–6** с новым номером версии
3. Deployment Name: `JavaTextDiff-1.0.1`

---

## Полезные ссылки

| Описание | URL |
|----------|-----|
| Central Portal | https://central.sonatype.com/ |
| Maven Central repo | https://repo1.maven.org/maven2/io/github/moriline/JavaTextDiff/ |
| Sonatype docs | https://central.sonatype.org/publish/ |
| GPG keyserver | https://keyserver.ubuntu.com/ |
