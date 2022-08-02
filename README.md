# Система Быстрых Платежей

## Сборка
```
mvn clean package
```

## Деплой
Скопировать в папку <TOMCAT_HOME>/webapps

## API
1. POST /users -- создать пользователя
2. GET /users -- список всех пользователей
3. POST /accounts -- создать счет пользователя
4. GET /accounts -- список всех счетов всех пользователей
5. GET /users/(USER-ID)/accounts -- список счетов пользователя с идентификатором USER-ID
6. POST /users/(USER-ID)/accounts -- создать новый счет пользователю с идентификатором USER-ID
7. GET /accounts?phone=(phone) -- найти все счета по телефону пользователя